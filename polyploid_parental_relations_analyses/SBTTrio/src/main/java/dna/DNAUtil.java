package dna;

/**
 * Implements numerous utility functions for k-mers over the DNA alphabet.
 */
public class DNAUtil {

    /**
     * The bit mask used during the conversion of the numeric representation of a k-mer to the string representation.
     */
    private static final long bitMask = 0b11L;
    /**
     * The k-mer size this utility class is for.
     */
    private final int kmerSize;

    /**
     * Create a utility class for a specified k-mer size.
     *
     * @param kmerSize k-mer size to create the utility class for
     */
    public DNAUtil(int kmerSize) {
        this.kmerSize = kmerSize;
    }

    /**
     * Convert the numeric representation of a k-mer to the corresponding string representation.
     *
     * @param kmer numeric representation of the k-mer to convert
     * @return string representation of the k-mer
     */
    public String longToString(long kmer) {
        long intermediate = kmer;
        long bitMask = 0b11L;
        StringBuilder builder = new StringBuilder(kmerSize);
        builder.setLength(kmerSize);

        for (int i = 0; i < kmerSize; i++) {
            builder.setCharAt(kmerSize - i - 1, DNA.intToChar((int) (intermediate & bitMask)));
            intermediate = intermediate >>> 2;
        }

        return builder.toString();
    }

    /**
     * Convert the string representation of a k-mer to the corresponding numeric representation.
     *
     * @param kmer string representation of the k-mer to convert
     * @return numeric representation of the k-mer
     */
    public long stringToLong(String kmer) {
        long result = 0;
        for (int i = 0; i < kmer.length(); i++) {
            result = result << 2;
            result += DNA.charToInt(kmer.charAt(i));
        }
        return result;
    }

    /**
     * Compute the reverse complement of a specified k-mer.
     *
     * @param kmer numeric representation of the k-mer of which to compute the reverse complement
     * @return numeric representation of the reverse complement of the specified k-mer
     */
    public long reverseComplement(long kmer) {
        long kmerCopy = kmer;
        long result = 0;

        for (int i = 0; i < kmerSize; i++) {
            long intermediate = (~(kmerCopy & bitMask)) & bitMask;
            result = result << 2;
            result = result | intermediate;
            kmerCopy = kmerCopy >>> 2;
        }

        return result;
    }

    /**
     * Compute the canonical form of a specified k-mer.
     *
     * @param kmer numeric representation of the k-mer of which to compute the canonical form
     * @return numeric representation of the canonical form of the specified k-mer
     */
    public long canonicalForm(long kmer) {
        long reverseComplement = reverseComplement(kmer);
        if (reverseComplement <= kmer) {
            return reverseComplement;
        } else {
            return kmer;
        }
    }

    public int getKmerSize() {
        return kmerSize;
    }
}
