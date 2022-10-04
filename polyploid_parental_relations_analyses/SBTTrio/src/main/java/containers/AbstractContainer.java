package containers;

import exceptions.AddStructureToContainerException;
import util.DataStructure;

/**
 * Abstract parent class for all containers.
 */
public abstract class AbstractContainer implements Container {

    /**
     * Add a container data structure corresponding to the specified prefix to this container.
     * Because containers do not support the above-mentioned operation, this method only throws an exception and
     * does nothing else.
     *
     * @param prefix numeric representation of the k-mer prefix that corresponds to the sub data structure
     * @param dataStructure sub data structure to add
     */
    @Override
    public void add(long prefix, DataStructure dataStructure) {
        throw new AddStructureToContainerException("Cannot add data structure to container");
    }

}
