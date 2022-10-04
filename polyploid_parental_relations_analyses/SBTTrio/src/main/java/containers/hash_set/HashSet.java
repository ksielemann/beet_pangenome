package containers.hash_set;

import containers.AbstractContainer;
import hash_functions.HashFunction;
import hash_functions.MurmurHash32Finalizer;
import hash_functions.MurmurHash64Finalizer;

import java.util.Iterator;

/**
 * Implements an open addressing hash set of k-mers for k < 32.
 *
 * To resolve collisions, quadratic probing with the function i(i+1)/2 is used where i is the number of probes
 * already performed.
 *
 * The class of hash functions is chosen depending on the size of the k-mers:
 * <ul>
 *     <li>0 < k < 17: finalizer of the 32-bit version of MurmurHash3</li>
 *     <li>16 < k < 32: finalizer of the 128-bit version of MurmurHash3</li>
 * </ul>
 *
 * See <a href="https://github.com/aappleby/smhasher">SMHasher GitHub</a> for
 * the original MurmurHash3 implementation.
 *
 * Using only finalizers prohibits the use of random seeds for the hash functions.
 * However, in our empirical tests for k-mers (k < 32) the distribution was only slightly worse
 * compared to the distribution obtained when using the full MurmurHash3 hash functions.
 * We also observed a substantial speed improvement when using only finalizers and
 * therefore decided to substitute the full hash functions for their finalizers.
 *
 * The size of a hash set is always a power of 2. Therefore, together with the probing function
 * specified above, the period of the search is the size of the hash table as proved in
 * <a href="http://dx.doi.org/10.1093/comjnl/15.4.314">The quadratic hash method when the table size is a power of 2</a>.
 *
 * When building the hash set for a specified list of elements, the load factor will be smaller than 0.55.
 * When adding elements to the hash set, the load factor will always be smaller than 0.75.
 */
public class HashSet extends AbstractContainer implements Iterable<Long> {

    /**
     * The size of the k-mers saved in this hash set.
     */
    private final int kmerSize;
    /**
     * The hash function used by this hash set.
     */
    private HashFunction hashFunction;
    /**
     * The long array that backs this hash set.
     */
    private long[] table;
    /**
     * The number of elements saved in this hash set.
     */
    private int numElements = 0;
    /**
     * 2^{powerOfTwo} is the size of this hash set.
     */
    private int powerOfTwo;

    /**
     * Create a hash set for a specified k-mer size.
     *
     * @param kmerSize k-mer size to create the hash set for
     */
    public HashSet(int kmerSize) {
        this.kmerSize = kmerSize;

        hashFunction = chooseHashFunction(kmerSize);
    }

    /**
     * Create a hash set for a specified k-mer size and an estimated number of elements to be saved in the hash set.
     *
     * @param kmerSize k-mer size to create the hash set for
     * @param numElements estimate of the number of elements to be saved in the hash set
     */
    public HashSet(int kmerSize, int numElements) {
        this.kmerSize = kmerSize;
        hashFunction = chooseHashFunction(kmerSize);

        int size = calculateSize(numElements);
        initialize(size);
        powerOfTwo = 32 - Integer.numberOfLeadingZeros(size);
    }

    /**
     * Build this hash set to efficiently accommodate a specified list of k-mers.
     * The optimal size is calculated with the assumption that the k-mers contained in the specified list are
     * pairwise different.
     *
     * @param patterns list of numeric representations of the k-mers to build this hash set for
     */
    @Override
    public void build(long[] patterns) {
        int size = calculateSize(patterns.length);
        initialize(size);
        powerOfTwo = 32 - Integer.numberOfLeadingZeros(size);

        for (long pattern : patterns) {
            table[index(pattern)] = pattern;
        }
        numElements = patterns.length;
    }

    /**
     * Search this hash set for a specified k-mer.
     * Time complexity: O(1) expected
     *
     * @param pattern numeric representation of the k-mer to search for
     * @return <ul>
     *     <li>true if this hash set contains the specified k-mer</li>
     *     <li>false if this hash set does not contain the specified k-mer</li>
     * </ul>
     */
    @Override
    public boolean search(long pattern) {
        int index = index(pattern);
        return index >= 0 && table[index] >= 0;
    }

    /**
     * Add a specified k-mer to this hash set.
     * Time complexity: O(1) expected
     *
     * @param pattern numeric representation of the k-mer to add
     * @return <ul>
     *     <li>true if the specified k-mer was added to this hash set</li>
     *     <li>false if this hash set already contains the specified k-mer</li>
     * <ul/>
     */
    public boolean add(long pattern) {
        int index = index(pattern);
        if (index == -1) {
            rehash();
        }

        if (table[index] == -1) {
            // this hash set does not contain pattern yet
            table[index] = pattern;
            numElements++;

            // rehash when load factor >= 0.75
            // which is equivalent to 4 * load factor >= 3
            if ((numElements << 4) >>> powerOfTwo >= 3) {
                rehash();
            }

            return true;
        } else {
            // this hash set already contains pattern
            return false;
        }
    }

    /**
     * Calculate the smallest size for the array that backs the hash set for a
     * specified number of elements such that the size is a power of 2 and the
     * load factor is smaller than 0.55.
     *
     * @param elements number of elements for which to calculate a suitable size
     * @return suitable size for the specified number of elements
     */
    public static int calculateSize(int elements) {
        // Integer.highestOneBit is the largest 2^k such that 2^k <= patterns.size()
        // definitely need double that number to achieve a load factor of at most 0.55
        int size = Integer.highestOneBit(elements) << 1;
        // only one expensive division to check whether another doubling in size is necessary
        if (size == 0) {
            size = 1;
        } else if (((double) elements) / size >= 0.55) {
            size = size << 1;
        }
        return size;
    }

    /**
     * Compute the table slot for a specified k-mer.
     * Time complexity: O(1) expected
     *
     * @param pattern numeric representation of the k-mer for which to find the table slot
     * @return <ul>
     *     <li>index of the slot occupied by the specified k-mer if this hash set already contains the specified k-mer</li>
     *     <li>index of the first free slot in the probe sequence if this hash set does not contain the specified k-mer</li>
     * </ul>
     */
    private int index(long pattern) {
        long currentState = hashFunction.hash(pattern);

        for (int i = 0; i < table.length; i++) {
            // modulo 2^k is equivalent to bitwise AND with 2^k - 1
            int index = (int) currentState & (table.length - 1);
            if (table[index] == pattern || table[index] == -1) {
                return index;
            }
            // use quadratic probing to find next position
            currentState += i + 1;
        }

        // if we run through the entire probe sequence and do not find the pattern or a free slot,
        // the hash table will be full
        return -1;
    }

    /**
     * Adjust the size of this hash set to efficiently accommodate the current number of elements and rehash
     * all elements currently in this hash set.
     * Time complexity: O(n) expected where n is the number of elements in the hash set
     */
    private void rehash() {
        long[] patterns = new long[numElements];
        int i = 0;
        for (long pattern: this) {
            patterns[i] = pattern;
            i++;
        }

        hashFunction = chooseHashFunction(kmerSize);
        build(patterns);
    }

    public int getNumElements() {
        return numElements;
    }

    public int size() {
        return table.length;
    }

    public long[] getTable() {
        return table;
    }

    public int getKmerSize() {
        return kmerSize;
    }

    public HashFunction getHashFunction() {
        return hashFunction;
    }

    @Override
    public Iterator<Long> iterator() {
        return new HashSetIterator(this);
    }

    /**
     * Choose a suitable hash function for a specified k-mer size.
     *
     * @param kmerSize k-mer size for which to choose a hash function
     * @return <ul>
     *     <li>instance of the MurmurHash32Finalizer class if k < 17</li>
     *     <li>instance of the MurmurHash64Finalizer class otherwise</li>
     * </ul>
     */
    private static HashFunction chooseHashFunction(int kmerSize) {
        if (kmerSize < 17) {
            return new MurmurHash32Finalizer();
        } else {
            return new MurmurHash64Finalizer();
        }
    }

    /**
     * Initialize the long array that backs this hash set for a specified array size.
     * Time complexity: O(m) where m is the size of the array
     *
     * @param size size of the long array that backs this hash set
     */
    private void initialize(int size) {
        table = new long[size];
        for (int i = 0; i < size; i++) {
            table[i] = -1;
        }
    }

}
