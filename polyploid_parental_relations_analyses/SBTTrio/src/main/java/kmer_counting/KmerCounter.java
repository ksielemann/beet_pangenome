package kmer_counting;

import containers.hash_set.HashSet;
import dna.DNAUtil;

/**
 * Implements a simple k-mer counter based on a hash set.
 */
public class KmerCounter {

    /**
     * The k-mer size this k-mer counter is for.
     */
    private final int kmerSize;
    private final DNAUtil dnaUtil;

    /**
     * Create a k-mer counter for a specified k-mer size.
     *
     * @param kmerSize k-mer size for which to create the k-mer counter
     */
    public KmerCounter(int kmerSize) {
        this.kmerSize = kmerSize;
        dnaUtil = new DNAUtil(kmerSize);
    }

    /**
     * Retrieve all distinct canonical k-mers from a specified input sequence.
     *
     * @param input sequence of which to extract the distinct canonical k-mers
     * @return iterable of the numeric representations of the distinct canonical k-mers in the specified input
     */
    public Iterable<Long> getAllKmers(String input) {
        // at most numKmers elements will be saved in the hash table
        int numKmers = input.length() - kmerSize + 1;
        HashSet hashSet = new HashSet(kmerSize, numKmers);

        // This takes Theta(n * k) time, but I do not know how to make it more efficient due to the immutability of strings
        // However it would take that much time anyway as you still need to hash the string to add it to the hash table
        for (int i = 0; i < numKmers; i++) {
            hashSet.add(dnaUtil.canonicalForm(dnaUtil.stringToLong(input.substring(i, i + kmerSize))));
        }

        return hashSet;
    }

    public int getKmerSize() {
        return kmerSize;
    }
}
