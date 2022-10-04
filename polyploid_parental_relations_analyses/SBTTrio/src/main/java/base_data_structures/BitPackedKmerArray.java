package base_data_structures;

/**
 * Implements a bit-packed array of k-mers which backs the bit packed container data structures.
 */
public class BitPackedKmerArray {

    private final int kmerSize;
    private BitVector bitVector;
    private int size;

    /**
     * Creates a bit-packed array of k-mers with specified size, i.e. number of k-mers to be
     * saved in the array. Assumes that size <= Integer.MAX_VALUE / kmerSize.
     *
     * @param size number of k-mers to save in the array
     * @param kmerSize size of the binary k-mers, i.e. 2k for k-mers over the DNA alphabet
     */
    public BitPackedKmerArray(int size, int kmerSize) {
        this.kmerSize = kmerSize;
        this.size = size;
        bitVector = new BitVector(size * this.kmerSize);
    }

    /**
     * Set the k-mer at the specified index of this array.
     *
     * @param index index of this array at which to set the k-mer
     * @param element numeric representation of the k-mer to set
     */
    public void set(int index, long element) {
        int fromIndex = index * kmerSize;
        bitVector.set(fromIndex, fromIndex + kmerSize, element);
    }

    /**
     * Get the k-mer at the specified index of this array.
     *
     * @param index index of this array at which to get the k-mer
     * @return numeric representation of the k-mer at the specified index of this array
     */
    public long get(int index) {
        int fromIndex = index * kmerSize;
        return bitVector.get(fromIndex, fromIndex + kmerSize);
    }

    public int size() {
        return size;
    }

    public int getKmerSize() {
        return kmerSize;
    }

    public BitVector getBitVector() {
        return bitVector;
    }

}
