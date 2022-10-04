package util;

public interface DataStructure {

    /**
     * Search for a k-mer in the data structure.
     *
     * @param pattern numeric representation of the k-mer to search for
     * @return <ul>
     *     <li>true if the data structure contains the specified pattern</li>
     *     <li>false if the data structure does not contain the specified pattern</li>
     * </ul>
     */
    boolean search(long pattern);

    /**
     * Add a sub data structure to this data structure.
     *
     * @param prefix numeric representation of the k-mer prefix that corresponds to the sub data structure
     * @param dataStructure sub data structure to add
     */
    void add(long prefix, DataStructure dataStructure);

}
