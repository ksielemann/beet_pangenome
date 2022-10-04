package containers;

public interface ContainerFactory {

    /**
     * Create the container for a specified k-mer size.
     *
     * @param kmerSize k-mer size to create the container for
     * @return new instance of the container class for the specified k-mer size
     */
    Container createContainer(int kmerSize);

    /**
     * Retrieve the name of the container class.
     *
     * @return name of the container class
     */
    String getContainerName();

}
