package access_trie;

import util.DataStructure;

/**
 * Implements an access trie with specified height as an array.
 *
 * The k-mer prefixes are encoded by the access trie.
 * The array saves pointers to buckets that contain the k-mer suffixes where each bucket corresponds to a unique prefix.
 *
 * Using an access trie, we reduce the redundancy of saving the same prefix many times while not incurring the
 * space overhead typically observed for trie data structures at deeper (often sparse) levels.
 */
public class FixedPrefixLengthAccessTrie implements DataStructure {

    /**
     * Height of this access trie.
     */
    private final int height;
    /**
     * Size of the k-mers saved by this data structure, not only the prefix length.
     */
    private final int kmerSize;
    /**
     * Bit mask used for extracting suffixes of k-mers.
     */
    private final long bitMask;
    /**
     * Array of buckets/containers of this access trie.
     */
    private final DataStructure[] leaves;

    /**
     * Create a fixed prefix length access trie with a specified height and for a specified k-mer size.
     *
     * @param height height of the access trie to create
     * @param kmerSize k-mer size to create the access trie for
     */
    public FixedPrefixLengthAccessTrie(int height, int kmerSize) {
        this.height = height;
        this.kmerSize = kmerSize;

        // bit mask for extracting suffixes: 4^(kmerSize - height) - 1
        bitMask = (1L << ((kmerSize - height) << 1)) - 1;
        // initialize array of size 4^size
        leaves = new DataStructure[(1 << (height << 1))];
    }

    /**
     * Search for a specified k-mer in this access trie and the corresponding container if necessary.
     * Locating the container in which to search for the suffix of the specified k-mer has time complexity O(1).
     *
     * @param pattern numeric representation of the k-mer to search for
     * @return <ul>
     *     <il>true if this access trie (and the corresponding container) contains the specified k-mer</il>
     *     <il>false if this access trie does not contain the specified k-mer</il>
     * </ul>
     */
    public boolean search(long pattern) {
        // only want the prefix of length height; therefore, shift by 2 * (kmerSize - height) to the right
        // parentheses are extremely important here
        int index = (int) (pattern >> ((kmerSize - height) << 1));
        if (leaves[index] == null) {
            return false;
        } else {
            // throw away the prefix and only search for the suffix
            return leaves[index].search(pattern & bitMask);
        }
    }

    /**
     * Add a container data structure corresponding to the specified prefix to this access trie.
     * Time complexity: O(1)
     *
     * @param prefix numeric representation of the k-mer prefix that corresponds to the sub data structure
     * @param dataStructure sub data structure to add
     */
    public void add(long prefix, DataStructure dataStructure) {
        leaves[(int) prefix] = dataStructure;
    }

    public int getHeight() {
        return height;
    }

    public int getKmerSize() {
        return kmerSize;
    }

    public long getBitMask() {
        return bitMask;
    }

    public DataStructure[] getLeaves() {
        return leaves;
    }
}
