package containers.bit_packed_sorted_array;

import base_data_structures.BitPackedKmerArray;
import containers.AbstractContainer;

import java.util.Arrays;

/**
 * Implements a bit-packed sorted array of k-mers for k < 32.
 */
public class BitPackedSortedArray extends AbstractContainer {

    /**
     * The number of bits used by this sorted array to represent a single k-mer.
     */
    private final int bitKmerSize;
    /**
     * The bit-packed k-mer array that backs this sorted array.
     */
    private BitPackedKmerArray data;

    /**
     * Create a bit-packed sorted array for a specified k-mer size.
     *
     * @param kmerSize k-mer size to create the sorted array for
     */
    public BitPackedSortedArray(int kmerSize) {
        this.bitKmerSize = kmerSize << 1;
    }

    /**
     * Build this sorted array to accommodate a specified list of k-mers.
     * Time complexity: O(n log n) where n is the number of elements to build this sorted array for
     *
     * @param patterns list of numeric representations of the k-mers to build this sorted array for
     */
    @Override
    public void build(long[] patterns) {
        Arrays.sort(patterns);
        data = new BitPackedKmerArray(patterns.length, bitKmerSize);
        for (int i = 0; i < patterns.length; i++) {
            data.set(i, patterns[i]);
        }
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
        int right = data.size() - 1;
        int mid;
        long midElement;

        while(left <= right) {
            // standard division already performs floor operation
            mid = (left + right) / 2;
            midElement = data.get(mid);

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

    public BitPackedKmerArray getData() {
        return data;
    }

}
