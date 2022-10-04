package containers.hash_set;

import containers.Container;
import containers.ContainerFactory;

public class HashSetFactory implements ContainerFactory {

    /**
     * Create a hash set for a specified k-mer size.
     *
     * @param kmerSize k-mer size to create the hash set for
     * @return new hash set for the specified k-mer size
     */
    @Override
    public Container createContainer(int kmerSize) {
        return new HashSet(kmerSize);
    }

    @Override
    public String getContainerName() {
        return "Hash table";
    }
}
