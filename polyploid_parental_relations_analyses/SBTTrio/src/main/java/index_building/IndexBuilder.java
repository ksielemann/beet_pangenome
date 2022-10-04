package index_building;

import access_trie.FixedPrefixLengthAccessTrie;
import containers.Container;
import containers.ContainerFactory;
import dna.DNAUtil;
import dna.KmerIterator;
import util.DataStructure;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

/**
 * Orchestrates the index construction.
 */
public class IndexBuilder {

    /**
     * The access trie height used by this index builder.
     */
    private final int accessTrieHeight;
    /**
     * The k-mer size this index builder is for.
     */
    private final int kmerSize;
    private final DNAUtil dnaUtil;

    /**
     * Create an index builder for a specified access trie height and a specified k-mer size.
     *
     * @param accessTrieHeight access trie height for which to create the index builder
     * @param kmerSize k-mer size for which to create the index builder
     */
    public IndexBuilder(int accessTrieHeight, int kmerSize) {
        this.accessTrieHeight = accessTrieHeight;
        this.kmerSize = kmerSize;

        dnaUtil = new DNAUtil(kmerSize - accessTrieHeight);
    }

    /**
     * Construct an index from a sorted file of distinct k-mers using a specified container data structure in the index.
     *
     * @param kmerDatabasePath path to the input file containing the distinct k-mers in lexicographically sorted order
     * @param containerFactory factory for the container data structure to use in the index
     * @return index constructed from the specified file using the specified container data structure
     */
    public DataStructure buildIndexSorted(String kmerDatabasePath, ContainerFactory containerFactory) {
        printInfo(kmerDatabasePath, containerFactory);

        DataStructure accessTrie = new FixedPrefixLengthAccessTrie(accessTrieHeight, kmerSize);

        int[] prefixCounts = getPrefixCounts(kmerDatabasePath);

        try (BufferedReader reader = new BufferedReader(new FileReader(kmerDatabasePath))) {
            for (int i = 0; i < prefixCounts.length; i++) {
                long[] currentKmers = new long[prefixCounts[i]];

                for (int j = 0; j < prefixCounts[i]; j++) {
                    // split at any white space character and extract only the suffix
                    long suffix = dnaUtil.stringToLong(reader.readLine().split("\\s+")[0].substring(accessTrieHeight));
                    currentKmers[j] = suffix;
                }
                // Now all kmers starting with currentPrefix are in the currentKmers list
                // and we can construct the container for them
                if (currentKmers.length > 0) {
                    Container container = containerFactory.createContainer(kmerSize - accessTrieHeight);
                    container.build(currentKmers);
                    accessTrie.add(i, container);
                }
            }
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
            System.exit(1);
        }

        return accessTrie;
    }

    /**
     * Construct an index from a file of distinct k-mers using a specified container data structure in the index.
     *
     * @param kmerDatabasePath path to the input file containing the distinct k-mers for which to construct the index
     * @param containerFactory factory for the container data structure to use in the index
     * @return index constructed from the specified file using the specified container data structure
     */
    public DataStructure buildIndex(String kmerDatabasePath, ContainerFactory containerFactory) {
        printInfo(kmerDatabasePath, containerFactory);

        DataStructure accessTrie = new FixedPrefixLengthAccessTrie(accessTrieHeight, kmerSize);

        int[] prefixCounts = getPrefixCounts(kmerDatabasePath);
        int[] currentPositions = new int[prefixCounts.length];
        long[][] buckets = new long[prefixCounts.length][];

        long bitMask = (1L << ((kmerSize - accessTrieHeight) << 1)) - 1;

        try (BufferedReader reader = new BufferedReader(new FileReader(kmerDatabasePath))) {
            String currentLine = reader.readLine();

            while (currentLine != null) {
                // extract prefix
                long kmer = dnaUtil.stringToLong(currentLine.split("\\s+")[0]);

                // this needs checking
                int prefix = (int) (kmer >> ((kmerSize - accessTrieHeight) << 1));

                if (buckets[prefix] == null) {
                    buckets[prefix] = new long[prefixCounts[prefix]];
                }

                // save suffix
                buckets[prefix][currentPositions[prefix]] = kmer & bitMask;

                currentPositions[prefix]++;

                // all kmers starting with the specific prefix have been found
                if (currentPositions[prefix] == prefixCounts[prefix]) {
                    Container container = containerFactory.createContainer(kmerSize - accessTrieHeight);
                    container.build(buckets[prefix]);
                    accessTrie.add(prefix, container);

                    buckets[prefix] = null;
                }

                currentLine = reader.readLine();
            }
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
            System.exit(1);
        }

        return accessTrie;
    }

    /**
     * Print the number of lines in a specified file and the name of a specified container data structure.
     *
     * @param kmerDatabasePath path to the file containing the k-mers row-wise
     * @param containerFactory factory for the container data structure
     */
    private void printInfo(String kmerDatabasePath, ContainerFactory containerFactory) {
        int numKmers = countLinesOfFile(kmerDatabasePath);
        System.out.println("Building index for " + numKmers + " distinct k-mers");
        System.out.println("Container data structure for " + kmerDatabasePath + ": " + containerFactory.getContainerName());
    }

    /**
     * Compute the number of lines in a specified file.
     *
     * @param path path to the file for which to count the number of lines
     * @return number of lines of the specified file
     */
    private int countLinesOfFile(String path) {
        int numberOfLines = -1;
        try (Stream<String> fileStream = Files.lines(Paths.get(path))) {
            numberOfLines = (int) fileStream.count();
        } catch(IOException ioException) {
            System.out.println(ioException.getMessage());
            System.exit(1);
        }
        return numberOfLines;
    }

    /**
     * Count the number of occurrences of each k-mer prefix of length accessTrieHeight in a
     * specified file for more memory efficient index construction.
     *
     * @param kmerDatabasePath path to the file containing the k-mers
     * @return array of length 4^{accessTrieHeight} of which the entries specify the counts of each prefix
     */
    private int[] getPrefixCounts(String kmerDatabasePath) {
        int[] counts = new int[(1 << (accessTrieHeight << 1))];
        Iterator<String> prefixes = new KmerIterator(accessTrieHeight);

        try (BufferedReader reader = new BufferedReader(new FileReader(kmerDatabasePath))) {
            String currentLine = reader.readLine();

            int currentPosition = 0;
            int currentCount = 0;
            String currentPrefix = null;

            while (prefixes.hasNext()) {
                currentPrefix = prefixes.next();
                currentCount = 0;

                while (currentLine != null && currentLine.startsWith(currentPrefix)) {
                    currentCount++;
                    currentLine = reader.readLine();
                }

                counts[currentPosition] = currentCount;
                currentPosition++;
            }
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
            System.exit(1);
        }
        return counts;
    }

    public int getAccessTrieHeight() {
        return accessTrieHeight;
    }

    public int getKmerSize() {
        return kmerSize;
    }

}
