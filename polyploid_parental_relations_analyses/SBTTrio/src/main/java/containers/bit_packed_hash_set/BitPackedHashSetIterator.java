package containers.bit_packed_hash_set;

import java.util.Iterator;

/**
 * Implements an iterator over the elements of a bit-packed hash set.
 * Iterating over all elements of a given hash set has time complexity O(m) where m is
 * the size of the array that backs the specified hash set. Because of the choice of the array size this is O(n).
 */
public class BitPackedHashSetIterator implements Iterator<Long> {

    /**
     * The bit-packed hash set this iterator is for.
     */
    private final BitPackedHashSet hashSet;
    /**
     * The current position in the bit-packed k-mer array that backs the hash set.
     */
    private int currentPosition;
    /**
     * The element of the hash set that this iterator returns next.
     */
    private long currentElement;
    /**
     * The number of elements already returned by this iterator.
     */
    private int numReturnedElements = 0;
    /**
     * The element that indicates an empty slot in the hash set.
     */
    private final long nullElement;

    /**
     * Create an iterator over the elements of a specified bit-packed hash set.
     *
     * @param hashSet bit-packed hash set to create an iterator for
     */
    public BitPackedHashSetIterator(BitPackedHashSet hashSet) {
        this.hashSet = hashSet;
        nullElement = hashSet.getNullElement();
        getNextElement();
    }

    @Override
    public boolean hasNext() {
        return hashSet.getNumElements() > numReturnedElements;
    }

    @Override
    public Long next() {
        long result = currentElement;
        numReturnedElements++;
        getNextElement();
        return result;
    }

    /**
     * Get the next element contained in the hash set this iterator is for.
     */
    private void getNextElement() {
        long element = nullElement;
        int position = currentPosition;

        while (element == nullElement && position < hashSet.size()) {
            element = hashSet.getTable().get(position);
            position++;
        }

        currentPosition = position;
        currentElement = element;
    }

}
