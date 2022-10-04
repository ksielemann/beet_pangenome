package trio_binning;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Implements a collection of buffered writers which are accessed/used in round-robin fashion to prevent the
 * potential bottleneck of output writing when using multithreading.
 */
public class RoundRobinBufferedWriter {

    /**
     * The array of buffered writers which are accessed in round-robin fashion.
     */
    private final BufferedWriter[] writers;
    /**
     * The index of the buffered writer accessed in the previous use of this round-robin buffered writer.
     */
    private int currentIndex = -1;

    /**
     * Create a round-robin buffered writer with a specified number of writers, a specified output prefix and output
     * format name.
     *
     * @param numWriters number of writers to use
     * @param outputPrefix prefix of the output files
     * @param format output format name
     * @throws IOException
     */
    public RoundRobinBufferedWriter(int numWriters, String outputPrefix, String format) throws IOException {
        if (numWriters < 1) {
            throw new IllegalArgumentException("Need at least one writer to initialize round-robin buffered writer");
        }
        this.writers = new BufferedWriter[numWriters];
        for (int i = 0; i < numWriters; i++) {
            String fileName = outputPrefix + "_" + i + "." + format;
            writers[i] = new BufferedWriter(new FileWriter(fileName));
        }
    }

    /**
     * Get the next writer in round-robin fashion.
     *
     * @return next writer in round-robin order
     */
    public BufferedWriter getNextWriter() {
        currentIndex++;
        currentIndex %= writers.length;
        return writers[currentIndex];
    }

    public BufferedWriter[] getWriters() {
        return writers;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * Close all writers.
     *
     * @throws IOException
     */
    public void closeAll() throws IOException {
        for (BufferedWriter writer : writers) {
            writer.close();
        }
    }
}
