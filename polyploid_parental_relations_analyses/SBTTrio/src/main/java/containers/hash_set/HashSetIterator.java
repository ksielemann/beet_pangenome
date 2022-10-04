package containers.hash_set;

import java.util.Iterator;

/**
 * Implements an iterator over the elements of a bit-packed hash set.
 * Iterating over all elements of a given hash set has time complexity O(m) where m is
 * the size of the array that backs the specified hash set. Because of the choice of the array size this is O(n).
 */
public class HashSetIterator implements Iterator<Long> {

    /**
     * The hash set this iterator is for.
     */
    private final HashSet hashSet;
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
     * Create an iterator over the elements of a specified bit-packed hash set.
     *
     * @param hashSet hash set to create an iterator for
     */
    public HashSetIterator(HashSet hashSet) {
        this.hashSet = hashSet;
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
        long element = -1;
        int position = currentPosition;

        while (element == -1 && position < hashSet.size()) {
            element = hashSet.getTable()[position];
            position++;
        }

        currentPosition = position;
        currentElement = element;
    }


}
