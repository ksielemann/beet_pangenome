package runtime_tests;

import containers.Container;
import containers.ContainerFactory;
import containers.bit_packed_hash_set.BitPackedHashSetFactory;
import containers.bit_packed_sorted_array.BitPackedSortedArrayFactory;
import containers.hash_set.HashSetFactory;
import containers.sorted_array.SortedArrayFactory;
import dna.KmerSetSampler;

/**
 * Orchestrates runtime tests.
 */
public class RuntimeTestRunner {

    public static final int KMER_SIZE = 15;
    public static final int ITERATIONS = 10;
    public static final int INDEX_SET_SIZE = 2000000;
    public static final int SEARCH_SET_SIZE = 1000000;

    public static void main(String[] args) {
        KmerSetSampler setSampler = new KmerSetSampler(KMER_SIZE);

        long[] indexSet = setSampler.randomlySampleWithoutReplacement(INDEX_SET_SIZE);
        long[] searchSet = setSampler.randomlySample(SEARCH_SET_SIZE);

        ContainerFactory[] factories = new ContainerFactory[]{
                new SortedArrayFactory(),
                new BitPackedSortedArrayFactory(),
                new HashSetFactory(),
                new BitPackedHashSetFactory()};

        for (ContainerFactory factory : factories) {
            System.out.println(factory.getContainerName());
            Container container = factory.createContainer(KMER_SIZE);
            System.out.println(RuntimeTestUtil.testAverageSearchRuntime(indexSet, searchSet, container, ITERATIONS));
        }
    }

}
