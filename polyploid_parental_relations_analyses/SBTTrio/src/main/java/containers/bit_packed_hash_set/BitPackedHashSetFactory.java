package containers.bit_packed_hash_set;

import containers.Container;
import containers.ContainerFactory;

public class BitPackedHashSetFactory implements ContainerFactory {

    /**
     * Create a bit-packed hash set for a specified k-mer size.
     *
     * @param kmerSize k-mer size to create the hash set for
     * @return new bit-packed hash set for the specified k-mer size
     */
    @Override
    public Container createContainer(int kmerSize) {
        return new BitPackedHashSet(kmerSize);
    }

    @Override
    public String getContainerName() {
        return "Bit packed hash set";
    }
}
