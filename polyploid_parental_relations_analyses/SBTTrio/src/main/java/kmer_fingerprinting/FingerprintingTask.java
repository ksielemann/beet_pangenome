package kmer_fingerprinting;

import containers.hash_set.HashSet;
import dna.KmerSetSampler;
import util.DataStructure;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Represents a single task performed during fingerprinting.
 * A task includes sampling a random set of canonical k-mers, computing the fingerprint of each input k-mer database
 * and computing the fingerprint intersections with the first k-mer database.
 */
public class FingerprintingTask implements Runnable {

    private final DataStructure[] databases;
    private final KmerSetSampler kmerSetSampler;
    private final BufferedWriter bufferedWriter;
    private final int setID;
    private final int setSize;

    /**
     * Create a fingerprinting task for a specified set of k-mer databases, a specified k-mer set sampler and buffered writer,
     * set id and set size.
     *
     * @param databases array of data structures representing the indices for the set of k-mer databases
     * @param kmerSetSampler k-mer set sampler to use in the task
     * @param bufferedWriter buffered writer to write the output of the task
     * @param setID id of the random set
     * @param setSize size of the random set to sample and use during the task
     */
    public FingerprintingTask(DataStructure[] databases,
                              KmerSetSampler kmerSetSampler,
                              BufferedWriter bufferedWriter,
                              int setID, int setSize) {
        this.databases = databases;
        this.kmerSetSampler = kmerSetSampler;
        this.bufferedWriter = bufferedWriter;
        this.setID = setID;
        this.setSize = setSize;
    }

    /**
     * Perform a single fingerprinting task which comprises random sampling of a set of canonical k-mers, computing
     * fingerprints and fingerprint intersections.
     */
    @Override
    public void run() {
        // sample a random set of canonical kmers without replacement
        long[] randomSet = kmerSetSampler.randomlySampleCanonicalSetWithoutReplacement(setSize);
        int kmerSize = kmerSetSampler.getKmerSize();
        // estimate 8 characters per database
        StringBuilder builder = new StringBuilder(databases.length << 3);
        builder.append(setID);
        builder.append(',');

        // compute the fingerprint for each database
        HashSet[] fingerprints = new HashSet[databases.length];
        for (int i = 0; i < databases.length; i++) {
            DataStructure currentDatabase = databases[i];
            // using randomSet.length as the number of distinct elements is probably a little exaggerated
            HashSet fingerprint = new HashSet(kmerSize, randomSet.length);
            int numElements = 0;
            for (long kmer : randomSet) {
                if (currentDatabase.search(kmer)) {
                    fingerprint.add(kmer);
                    numElements++;
                }
            }
            fingerprints[i] = fingerprint;
            builder.append(numElements);
            builder.append(',');
        }

        // compute the intersection of each fingerprint with the first one
        if (fingerprints.length > 1) {
            for (int i = 1; i < fingerprints.length; i++) {
                int numIntersectElements = 0;
                for (long kmer : fingerprints[i]) {
                    if (fingerprints[0].search(kmer)) {
                        numIntersectElements++;
                    }
                }
                builder.append(numIntersectElements);
                builder.append(',');
            }
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append('\n');

        try {
            bufferedWriter.write(builder.toString());
        } catch (IOException ioException) {
            System.out.println("Could not write results for set " + setID);
        }
    }

    public DataStructure[] getDatabases() {
        return databases;
    }

    public KmerSetSampler getKmerSetSampler() {
        return kmerSetSampler;
    }

    public BufferedWriter getBufferedWriter() {
        return bufferedWriter;
    }

    public int getSetID() {
        return setID;
    }

    public int getSetSize() {
        return setSize;
    }
}
