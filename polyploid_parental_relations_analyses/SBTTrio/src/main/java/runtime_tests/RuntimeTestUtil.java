package runtime_tests;

import containers.Container;

/**
 * Utility class for testing the average search runtime of different container data structures.
 */
public class RuntimeTestUtil {

    /**
     * Test the average search runtime of a specified container data structure on a given set of k-mers to index with
     * the specified container data structure, a given set of k-mers to search for and a specified number of iterations.
     *
     * @param indexSet set of k-mers to index with the specified container data structure
     * @param searchSet set of k-mers to search for
     * @param container container data structure to test the average search runtime of
     * @param iterations number of iterations of the test
     * @return average search runtime in ns
     */
    public static long testAverageSearchRuntime(long[] indexSet, long[] searchSet, Container container, int iterations) {
        container.build(indexSet);

        long totalRuntime = 0;
        for (int i = 0; i < iterations; i++) {
            for (long kmer : searchSet) {
                long startTime = System.nanoTime();
                container.search(kmer);
                long stopTime = System.nanoTime();
                totalRuntime += stopTime - startTime;
            }
        }
        return totalRuntime / (searchSet.length * iterations);
    }

}
