package trio_binning;

import containers.bit_packed_sorted_array.BitPackedSortedArrayFactory;
import htsjdk.samtools.fastq.FastqReader;
import index_building.IndexBuilder;
import kmer_counting.KmerCounter;
import org.apache.commons.cli.*;
import util.ArgumentValidation;
import util.DataStructure;

import java.io.*;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

public class TrioBinningRunner {

    public static void main(String[] args) throws IOException {
        // Define options
        Options options = new Options();

        Option databaseFiles = Option.builder("d").longOpt("databases")
                .hasArgs().argName("file")
                .valueSeparator(';')
                .required()
                .desc("paths to the kmer databases separated by ;")
                .build();
        Option readFile = Option.builder("r").longOpt("readfile")
                .hasArg().argName("file")
                .required()
                .desc("path to the read file in fastq format")
                .build();
        Option accessTrieHeight = Option.builder("h").longOpt("height")
                .hasArg().argName("H")
                .required()
                .desc("use an access trie of height H")
                .build();
        Option kmerSize = Option.builder("k").longOpt("kmer-size")
                .hasArg().argName("K")
                .required()
                .desc("length of the kmers")
                .build();
        Option numThreads = Option.builder("t").longOpt("threads")
                .hasArg().argName("T")
                .desc("use T threads. Default: 8")
                .build();
        Option outputPrefix = Option.builder("o").longOpt("output")
                .hasArg().argName("output_prefix")
                .desc("prefix of the output files. Default: results")
                .build();

        options.addOption(databaseFiles);
        options.addOption(readFile);
        options.addOption(accessTrieHeight);
        options.addOption(kmerSize);
        options.addOption(numThreads);
        options.addOption(outputPrefix);

        // Parse options
        CommandLineParser parser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException parseException) {
            System.out.println(parseException.getMessage());
            helpFormatter.printHelp("burst_trie", options);
            System.exit(1);
        }

        // Validate options
        String[] files = cmd.getOptionValues(databaseFiles);
        for (String file : files) {
            ArgumentValidation.validateFileArgument(file);
        }
        ArgumentValidation.validateFileArgument(cmd.getOptionValue(readFile));
        int height = ArgumentValidation.validateIntArgument(cmd.getOptionValue(accessTrieHeight));
        int size = ArgumentValidation.validateIntArgument(cmd.getOptionValue(kmerSize));
        int threads = 8;
        if (cmd.hasOption(numThreads)) {
            threads = ArgumentValidation.validateIntArgument(cmd.getOptionValue(numThreads));
        }
        String output = "./results";
        if (cmd.hasOption(outputPrefix)) {
            output = cmd.getOptionValue(outputPrefix);
        }

        // Perform trio binning
        performTrioBinning(files, cmd.getOptionValue(readFile),
                height, size, threads, output);
    }

    /**
     * Perform the entire trio binning process.
     *
     * @param databaseFiles array of strings representing the paths to the k-mer databases for which to perform trio binning
     * @param readFilePath path to the read file
     * @param accessTrieHeight access trie height to use for the indices
     * @param kmerSize k-mer size to use during trio binning
     * @param numThreads number of threads
     * @param outputPrefix prefix of the output files
     * @throws IOException
     */
    public static void performTrioBinning(String[] databaseFiles,
                                          String readFilePath,
                                          int accessTrieHeight, int kmerSize,
                                          int numThreads, String outputPrefix) throws IOException {
        // Build index
        System.out.println("Building indices");
        IndexBuilder indexBuilder = new IndexBuilder(accessTrieHeight, kmerSize);
        DataStructure[] databases = new DataStructure[databaseFiles.length];
        for (int i = 0; i < databaseFiles.length; i++) {
            databases[i] = indexBuilder.buildIndex(databaseFiles[i], new BitPackedSortedArrayFactory());
            System.out.println(Arrays.toString(databases));
        }

        // Process reads
        BufferedReader bufferedReader;
        if (readFilePath.endsWith(".gz")) {
            bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(readFilePath))));
        } else {
            bufferedReader = new BufferedReader(new FileReader(readFilePath));
        }

        FastqReader fastqReader = new FastqReader(new File(readFilePath), bufferedReader, true);
        KmerCounter kmerCounter = new KmerCounter(kmerSize);

        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        RoundRobinBufferedWriter writer =
                new RoundRobinBufferedWriter(numThreads, outputPrefix, "csv");

        System.out.println("Calculating counts");
        int readID = 0;
        while (fastqReader.hasNext()) {
            String read = fastqReader.next().getReadString();
            Runnable task = new TrioBinningTask(databases,
                    kmerCounter, writer.getNextWriter(), readID, read);
            pool.execute(task);
            readID++;
        }

        // complete all existing tasks and then shutdown the pool
        pool.shutdown();

        while (true) {
            try {
                if (pool.awaitTermination(1, TimeUnit.SECONDS)) {
                    break;
                }
            } catch (InterruptedException exception) {}
        }

        // write all remaining output to disk and close writers
        writer.closeAll();

        System.out.println("Done");
    }

}
