package containers.sorted_array;

import containers.AbstractContainer;

import java.util.Arrays;

/**
 * Implements a sorted array of k-mers for k < 32.
 */
public class SortedArray extends AbstractContainer {

    /**
     * The long array that backs this sorted array.
     */
    private long[] data;

    /**
     * Build this sorted array to accommodate a specified list of k-mers.
     * Time complexity: O(n log n) where n is the number of elements to build this sorted array for
     *
     * @param patterns list of numeric representations of the k-mers to build this sorted array for
     */
    @Override
    public void build(long[] patterns) {
        Arrays.sort(patterns);
        data = patterns;
    }

    /**
     * Search this sorted array for a specified k-mer using a binary search.
     * Time complexity: O(log n) where n is the number of elements in the array
     *
     * @param pattern numeric representation of the k-mer to search for
     * @return <ul>
     *     <li>true if this sorted array contains the specified k-mer</li>
     *     <li>false if this sorted array does not contain the specified k-mer</li>
     * </ul>
     */
    @Override
    public boolean search(long pattern) {
        int left = 0;
        int right = data.length - 1;
        int mid;
        long midElement;

        while(left <= right) {
            // standard division already performs floor operation
            mid = (left + right) / 2;
            midElement = data[mid];

            if (pattern < midElement) {
                right = mid - 1;
            } else if (pattern > midElement) {
                left = mid + 1;
            } else {
                return true;
            }
        }

        return false;
    }

    public long[] getData() {
        return data;
    }
}
