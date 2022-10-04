package containers;

import util.DataStructure;

public interface Container extends DataStructure {

    /**
     * Build the container for a specified list of k-mers.
     *
     * @param patterns list of numeric representations of the k-mers to build the container for
     */
    void build(long[] patterns);

}
