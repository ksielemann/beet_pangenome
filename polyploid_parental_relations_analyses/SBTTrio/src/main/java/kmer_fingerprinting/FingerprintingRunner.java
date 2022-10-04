package kmer_fingerprinting;

import containers.sorted_array.SortedArrayFactory;
import dna.DNAUtil;
import dna.KmerSetSampler;
import index_building.IndexBuilder;
import org.apache.commons.cli.*;
import util.ArgumentValidation;
import util.DataStructure;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FingerprintingRunner {

    public static void main(String[] args) throws IOException {
        // define options
        Options options = new Options();

        Option databaseFiles = Option.builder("d").longOpt("databases")
                .hasArgs().argName("file")
                .valueSeparator(';')
                .required()
                .desc("paths to the kmer databases separated by ;")
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
        Option numSets = Option.builder("s").longOpt("sets")
                .hasArg().argName("S")
                .required()
                .desc("perform fingerprinting for S sets")
                .build();
        Option setSize = Option.builder("c").longOpt("cardinality")
                .hasArg().argName("C")
                .required()
                .desc("use sets with C elements")
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
        options.addOption(accessTrieHeight);
        options.addOption(kmerSize);
        options.addOption(numSets);
        options.addOption(setSize);
        options.addOption(numThreads);
        options.addOption(outputPrefix);

        // parse options
        CommandLineParser parser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException parseException) {
            System.out.println(parseException.getMessage());
            helpFormatter.printHelp("StaticBurstTrieTrio", options);
            System.exit(1);
        }

        // validate options
        String[] files = cmd.getOptionValues(databaseFiles);
        for (String file : files) {
            ArgumentValidation.validateFileArgument(file);
        }
        int height = ArgumentValidation.validateIntArgument(cmd.getOptionValue(accessTrieHeight));
        int size = ArgumentValidation.validateIntArgument(cmd.getOptionValue(kmerSize));
        int sets = ArgumentValidation.validateIntArgument(cmd.getOptionValue(numSets));
        int cardinality = ArgumentValidation.validateIntArgument(cmd.getOptionValue(setSize));
        int threads = 8;
        if (cmd.hasOption(numThreads)) {
            threads = ArgumentValidation.validateIntArgument(cmd.getOptionValue(numThreads));
        }
        String output = "./results";
        if (cmd.hasOption(outputPrefix)) {
            output = cmd.getOptionValue(outputPrefix);
        }

        // perform fingerprinting
        performFingerprinting(files, height, size, sets, cardinality, threads, output);
    }

    /**
     * Perform the entire fingerprinting process.
     *
     * @param databaseFiles array of strings representing the paths to the k-mer databases for which to perform fingerprinting
     * @param accessTrieHeight access trie height to use for the indices
     * @param kmerSize k-mer size to use during fingerprinting
     * @param numSets number of random sets to generate
     * @param setSize size of the random sets
     * @param numThreads number of threads
     * @param outputPrefix prefix of the output files
     * @throws IOException
     */
    public static void performFingerprinting(String[] databaseFiles, int accessTrieHeight, int kmerSize, int numSets,
                                             int setSize, int numThreads, String outputPrefix) throws IOException {
        // Build indices
        System.out.println("Building indices");
        IndexBuilder indexBuilder = new IndexBuilder(accessTrieHeight, kmerSize);
        DataStructure[] databases = new DataStructure[databaseFiles.length];
        for (int i = 0; i < databaseFiles.length; i++) {
            databases[i] = indexBuilder.buildIndex(databaseFiles[i], new SortedArrayFactory());
            System.out.println(Arrays.toString(databases));
        }

        KmerSetSampler kmerSetSampler = new KmerSetSampler(kmerSize, new DNAUtil(kmerSize));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPrefix + ".csv"));

        StringBuilder header = new StringBuilder(6 + 28 * databases.length);
        header.append("setID,");
        for (int i = 0; i < databases.length; i++) {
            header.append("fingerprint_").append(i).append(",");
        }
        for (int i = 0; i < databases.length - 1; i++) {
            header.append("intersect_1+").append(i).append(",");
        }
        header.deleteCharAt(header.length() - 1);
        bufferedWriter.write(header.toString());
        bufferedWriter.newLine();

        System.out.println("Generating fingerprints");
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        for (int i = 0; i < numSets; i++) {
            Runnable task = new FingerprintingTask(databases, kmerSetSampler, bufferedWriter, i, setSize);
            pool.execute(task);
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

        // write all remaining output to disk and close writer
        bufferedWriter.close();

        System.out.println("Done");
    }

}
