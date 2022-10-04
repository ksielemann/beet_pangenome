package containers.sorted_array;

import containers.Container;
import containers.ContainerFactory;

public class SortedArrayFactory implements ContainerFactory {

    /**
     * Create a sorted array for a specified k-mer size.
     *
     * @param kmerSize k-mer size to create the sorted array for
     * @return new sorted array for the specified k-mer size
     */
    @Override
    public Container createContainer(int kmerSize) {
        return new SortedArray();
    }

    @Override
    public String getContainerName() {
        return "Sorted array";
    }
}
