package trio_binning;

import kmer_counting.KmerCounter;
import util.DataStructure;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Represents a single task performed during trio binning.
 * A task comprises computing the number of distinct canonical k-mers a given read shares with each of
 * a given set of k-mer databases by first extracting the distinct canonical k-mers in the read and then
 * searching for them in the indices for the specified k-mer databases.
 */
public class TrioBinningTask implements Runnable {

    private final DataStructure[] databases;
    private final KmerCounter kmerCounter;
    private final BufferedWriter bufferedWriter;
    private final int readID;
    private final String read;

    /**
     * Create a trio binning task for a specified set of k-mer databases, a specified k-mer counter and buffered writer,
     * read id and read.
     *
     * @param databases array of data structures representing the indices for the set of k-mer databases
     * @param kmerCounter k-mer counter to use in the task
     * @param bufferedWriter buffered writer to write the output of the task
     * @param readID id of the read
     * @param read read to use in the task
     */
    public TrioBinningTask(DataStructure[] databases,
                           KmerCounter kmerCounter,
                           BufferedWriter bufferedWriter,
                           int readID, String read) {
        this.databases = databases;
        this.kmerCounter = kmerCounter;
        this.bufferedWriter = bufferedWriter;

        this.readID = readID;
        this.read = read;
    }

    /**
     * Perform a single trio binning task which comprises extracting the distinct canonical k-mers of a read and
     * computing the number of such k-mers the read shares with each of a given set of k-mer databases.
     */
    @Override
    public void run() {
        Iterable<Long> readKmers = kmerCounter.getAllKmers(read);

        StringBuilder builder = new StringBuilder(databases.length << 4);
        builder.append(readID);
        builder.append(',');

        for (int i = 0; i < databases.length; i++) {
            DataStructure currentDatabase = databases[i];

            int count = 0;
            int numDistinctKmers = 0;
            for (long kmer : readKmers) {
                numDistinctKmers++;
                if (currentDatabase.search(kmer)) {
                    count++;
                }
            }
            // prevent division by 0
            if (numDistinctKmers == 0) {
                numDistinctKmers++;
            }

            builder.append((double) count / numDistinctKmers);
            builder.append(',');
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append('\n');

        try {
            bufferedWriter.write(builder.toString());
        } catch (IOException ioException) {
            System.out.println("Could not write results for read " + readID);
        }
    }

    public DataStructure[] getDatabases() {
        return databases;
    }

    public KmerCounter getKmerCounter() {
        return kmerCounter;
    }

    public int getReadID() {
        return readID;
    }

    public String getRead() {
        return read;
    }
}
