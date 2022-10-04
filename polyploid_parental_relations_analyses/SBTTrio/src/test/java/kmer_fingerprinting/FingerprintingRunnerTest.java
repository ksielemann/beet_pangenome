package kmer_fingerprinting;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class FingerprintingRunnerTest {

    private final String[] databaseFiles = new String[]{"src/test/resources/testHapmerDatabase01.txt",
            "src/test/resources/testHapmerDatabase02.txt"};
    private final int height = 1;
    private final int size = 5;
    private final int threads = 4;
    private final String outputPrefix = "src/test/resources/fingerprintingTestOutput";

    /*
    As the set sampling is performed randomly, asserting anything here is difficult or impossible.
    Therefore, these tests are simply to see if there are any exception thrown during execution.
     */

    @Test
    void performFingerprinting() throws IOException {
        FingerprintingRunner.performFingerprinting(databaseFiles, height, size, 100,
                100, threads, outputPrefix);
    }

    @Test
    void main() throws IOException {
        String[] args = new String[]{"-d", String.join(";", databaseFiles),
            "-h", "1", "-k", "5", "-s", "100", "-c", "100", "-t", "4", "-o", outputPrefix};

        FingerprintingRunner.main(args);
    }

}
