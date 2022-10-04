package dna;

import java.util.Iterator;

/**
 * Implements an iterator over all k-mers of a specified size in lexicographically ascending order.
 */
public class KmerIterator implements Iterator<String> {

    /**
     * The final k-mer to return by this iterator.
     */
    private final long lastKmer;
    /**
     * The k-mer size this iterator is for.
     */
    private final int kmerSize;
    private final DNAUtil dnaUtil;
    /**
     * The numeric representation of the k-mer that this iterator returns next.
     */
    private long currentKmer;

    /**
     * Create an iterator over all k-mers of a specified size.
     *
     * @param kmerSize k-mer size for which to create the iterator
     */
    public KmerIterator(int kmerSize) {
        this.kmerSize = kmerSize;
        lastKmer = 1L << (kmerSize << 1);
        dnaUtil = new DNAUtil(kmerSize);
        currentKmer = 0;
    }

    @Override
    public boolean hasNext() {
        return !(currentKmer >= lastKmer);
    }

    /**
     * Retrieve the string representation of the next k-mer in lexicographically ascending order.
     * Time complexity: Theta(k) where k is the size of the k-mers
     *
     * @return the next k-mer in lexicographically ascending order
     */
    @Override
    public String next() {
        String kmer = dnaUtil.longToString(currentKmer);
        currentKmer++;
        return kmer;
    }

    public long getLastKmer() {
        return lastKmer;
    }

    public long getCurrentKmer() {
        return currentKmer;
    }

    public int getKmerSize() {
        return kmerSize;
    }

}
