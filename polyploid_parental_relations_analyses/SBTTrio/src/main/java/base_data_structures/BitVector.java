package base_data_structures;

/**
 * Implements a fixed-size bit vector.
 */
public class BitVector {

    private final static int MODULO_64_BITMASK = 63;
    private final static long FIRST_BITMASK = 1L;

    private final long[] words;

    /**
     * Determine the word the specified index is located in.
     *
     * @param bitIndex index of which to determine the word it is located in
     * @return index of the word the specified index is located in
     */
    private static int wordIndex(int bitIndex) {
        return bitIndex >> 6;
    }

    /**
     * Construct a new bit vector with a specified capacity.
     * Assumes that the specified capacity is >= 0.
     *
     * @param numBits capacity of the new bit vector
     */
    public BitVector(int numBits) {
        int size = wordIndex(numBits - 1) + 1;
        words = new long[size];
    }

    /**
     * Get the number of words this bit vector comprises.
     * Time complexity: O(1)
     *
     * @return number of words this bit vector comprises
     */
    public int getNumWords() {
        return words.length;
    }

    /**
     * Set the bit at the specified index of this bit vector to 1.
     * Time complexity: O(1)
     *
     * @param bitIndex index of the bit to set to 1
     */
    public void set(int bitIndex) {
        int wordIndex = wordIndex(bitIndex);

        words[wordIndex] |= (1L << (bitIndex & MODULO_64_BITMASK));
    }

    /**
     * Set the bit at the specified index of this bit vector to 0.
     * Time complexity: O(1)
     *
     * @param bitIndex index of the bit to set to 0
     */
    public void clear(int bitIndex) {
        int wordIndex = wordIndex(bitIndex);

        words[wordIndex] &= ~(1L << (bitIndex & MODULO_64_BITMASK));
    }

    /**
     * Retrieve the value of the bit at the specified index of this bit vector.
     * Time complexity: O(1)
     *
     * @param bitIndex index of the bit of which to return the value
     * @return value of the bit at the specified index
     */
    public boolean get(int bitIndex) {
        int wordIndex = wordIndex(bitIndex);

        return (words[wordIndex] & (1L << (bitIndex & MODULO_64_BITMASK))) != 0;
    }

    /**
     * Retrieve the word at the specified word index of this bit vector.
     * Time complexity: O(1)
     *
     * @param wordIndex index of the word to retrieve
     * @return word at the specified word index
     */
    public long getWord(int wordIndex) {
        return words[wordIndex];
    }

    public int hashCode() {
        long h = 1234;
        for (int i = words.length; --i >= 0; )
            h ^= words[i] * (i + 1);

        return (int)((h >> 32) ^ h);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof BitVector set))
            return false;
        if (this == obj)
            return true;

        if (words.length != set.getNumWords())
            return false;

        // Check words in use by both BitSets
        for (int i = 0; i < words.length; i++)
            if (words[i] != set.getWord(i))
                return false;

        return true;
    }

    /**
     * Extract the bits in the range [fromIndex, toIndex) of this
     * bit vector as a long in the first toIndex - fromIndex bits.
     * Assumes toIndex > fromIndex and toIndex - fromIndex <= 64.
     * Time complexity: O(1)
     *
     * @param fromIndex index to start extraction from (inclusive)
     * @param toIndex index to stop extraction at (exclusive)
     * @return bits in the range [fromIndex, toIndex) as a long
     */
    public long get(int fromIndex, int toIndex) {
        int from = fromIndex & MODULO_64_BITMASK;
        int to = toIndex & MODULO_64_BITMASK;

        int fromWordIndex = wordIndex(fromIndex);
        int toWordIndex = wordIndex(toIndex);

        // need to treat toIndex being a multiple of 64 specially
        if (to == 0) {
            toWordIndex -= 1;
        }

        // both indices lie in the same word
        if (fromWordIndex == toWordIndex) {
            long word = words[wordIndex(fromIndex)];
            int leftShift = 64 - to;
            word <<= leftShift;
            word >>>= from + leftShift;
            return word;
        } else {
            long firstWord = words[fromWordIndex];
            long secondWord = words[toWordIndex];

            // extract bits [0, to) from second word
            secondWord <<= 64 - to;
            secondWord >>>= from - to;

            // extract bits [from, 64) from first word
            firstWord >>>= from;

            return firstWord | secondWord;
        }
    }

    /**
     * Set the bits in the range [fromIndex, toIndex) of this
     * bit vector to the first toIndex - fromIndex
     * bits of the specified long element.
     * Assumes toIndex > fromIndex and toIndex - fromIndex <= 64.
     * Time complexity: O(k) where k = toIndex - fromIndex
     *
     * @param fromIndex index to start setting from (inclusive)
     * @param toIndex index to stop setting at (exclusive)
     * @param element long number of which to use the first toIndex - fromIndex bits
     *                for setting
     */
    public void set(int fromIndex, int toIndex, long element) {
        int currentIndex = fromIndex & MODULO_64_BITMASK;
        int currentWordIndex = wordIndex(fromIndex);
        long currentWord = words[currentWordIndex];

        for (int i = 0; i < toIndex - fromIndex; i++) {
            if (currentIndex >= 64) {
                // save changes
                words[currentWordIndex] = currentWord;
                // move to next word
                currentIndex = 0;
                currentWordIndex++;
                currentWord = words[currentWordIndex];
            }

            if ((element & FIRST_BITMASK) > 0) {
                // bit at position i of element is 1
                currentWord |= (1L << currentIndex);
            } else {
                // bit at position i of element is 0
                currentWord &= ~(1L << currentIndex);
            }
            element >>>= 1;
            currentIndex++;
        }
        // save changes
        words[currentWordIndex] = currentWord;
    }

    /**
     * Compares the bits in the range [fromIndex, toIndex) of this
     * bit vector to the first toIndex - fromIndex
     * bits of the specified element.
     * Assumes toIndex > fromIndex and toIndex - fromIndex <= 64.
     * Assumes that the remaining bits of the specified element are 0.
     * Time complexity: O(1)
     *
     * @param fromIndex index to start comparison from (inclusive)
     * @param toIndex index to stop comparison at (exclusive)
     * @param element long number to compare to
     * @return
     */
    public boolean compare(int fromIndex, int toIndex, long element) {
        return get(fromIndex, toIndex) == element;
    }

    public long[] getWords() {
        return words;
    }

}
