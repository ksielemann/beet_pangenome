package containers.bit_packed_sorted_array;

import containers.Container;
import containers.ContainerFactory;

public class BitPackedSortedArrayFactory implements ContainerFactory {

    /**
     * Create a bit-packed sorted array for a specified k-mer size.
     *
     * @param kmerSize k-mer size to create the sorted array for
     * @return new bit-packed sorted array for the specified k-mer size
     */
    @Override
    public Container createContainer(int kmerSize) {
        return new BitPackedSortedArray(kmerSize);
    }

    @Override
    public String getContainerName() {
        return "Bit-packed sorted array";
    }
}
