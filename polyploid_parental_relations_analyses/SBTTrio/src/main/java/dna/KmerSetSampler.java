package dna;

import containers.hash_set.HashSet;

import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements random sampling with and without replacement of k-mers and sampling without replacement of canonical k-mers.
 */
public class KmerSetSampler {

    /**
     * The k-mer size this set sampler is for.
     */
    private final int kmerSize;

    private DNAUtil dnaUtil;

    /**
     * Create a set sampler for a specified k-mer size.
     *
     * @param kmerSize k-mer size to create the set sampler for
     */
    public KmerSetSampler(int kmerSize) {
        this.kmerSize = kmerSize;
    }

    public KmerSetSampler(int kmerSize, DNAUtil dnaUtil) {
        this.kmerSize = kmerSize;
        this.dnaUtil = dnaUtil;
    }

    /**
     * Randomly sample a set of specified size of k-mers without replacement.
     * Implements a sparse Fisher-Yates shuffle
     * (<a href="https://doi.org/10.48550/arXiv.2104.05091">Simple, Optimal Algorithms for Random Sampling Without Replacement</a>)
     * to achieve optimal O(s) time and space complexity where s is the set size.
     *
     * @param setSize size of the set to sample
     * @return array representation of the sampled set
     */
    public long[] randomlySampleWithoutReplacement(int setSize) {
        long maxKmer = 1L << (kmerSize << 1);

        long[] result = new long[setSize];

        if (setSize > maxKmer) {
            throw new IllegalArgumentException("The specified set size is greater than the number of distinct k-mers");
        } else if (setSize == maxKmer) {
            for (long i = 0; i < maxKmer; i++) {
                result[(int) i ] = i;
            }
        } else {
            Hashtable<Long, Long> pool = new Hashtable<>(setSize * 3);
            Random random = new Random();
            int position = 0;

            for (long i = 0; i < setSize; i++) {
                long next = ThreadLocalRandom.current().nextLong(i, maxKmer);
                long nextKmer = pool.getOrDefault(next, next);
                pool.put(next, pool.getOrDefault(i, i));
                result[position] = nextKmer;
                position++;
            }
        }

        return result;
    }

    /**
     * Randomly sample a set of specified size of k-mers with replacement.
     *
     * @param setSize size of the set to sample
     * @return array representation of the sampled set
     */
    public long[] randomlySample(int setSize) {
        long maxKmer = 1L << (kmerSize << 1);
        long[] result = new long[setSize];

        for (int i = 0; i < setSize; i++) {
            result[i] = (ThreadLocalRandom.current().nextLong(0, maxKmer));
        }
        return result;
    }

    /**
     * Randomly sample a set of specified size of canonical k-mers without replacement.
     *
     * @param setSize size of the set to sample
     * @return array representation of the sampled set
     */
    public long[] randomlySampleCanonicalSetWithoutReplacement(int setSize) {
        if (dnaUtil == null) {
            throw new NullPointerException("DNAUtil must be set before canonical set sampling");
        }

        long maxKmer = 1L << (kmerSize << 1);
        HashSet hashTable = new HashSet(kmerSize, setSize);

        int numInsertedElements = 0;
        while (numInsertedElements < setSize) {
            if (hashTable.add(dnaUtil.canonicalForm(ThreadLocalRandom.current().nextLong(0, maxKmer)))) {
                numInsertedElements++;
            }
        }

        long[] result = new long[setSize];
        int position = 0;
        for (long kmer : hashTable) {
            result[position] = kmer;
            position++;
        }
        return result;
    }

    public int getKmerSize() {
        return kmerSize;
    }

}
