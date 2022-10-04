package dna;

/**
 * Implements basic properties of the DNA alphabet.
 */
public class DNA {

    /**
     * Maximum k-mer size allowed for the DNA alphabet.
     */
    public static final int MAX_KMER_SIZE = 31;
    /**
     * Characters of the DNA alphabet.
     */
    public static final char[] BASES = new char[] {'A', 'C', 'G', 'T'};

    /**
     * Convert a specified character to an integer.
     *
     * @param character character to convert
     * @return <ul>
     *     <li>0 if the character is 'A'</li>
     *     <li>1 if the character is 'C'</li>
     *     <li>2 if the character is 'G'</li>
     *     <li>3 otherwise</li>
     * </ul>
     */
    public static int charToInt(char character) {
        if (character == 'A') {
            return 0;
        } else if (character == 'C') {
            return 1;
        } else if (character == 'G') {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Convert a specified integer in the range [0, 3] to a character of the DNA alphabet.
     *
     * @param integer integer to convert
     * @return <ul>
     *     <li>'A' if the integer is 0</li>
     *     <li>'C' if the integer is 1</li>
     *     <li>'G' if the integer is 2</li>
     *     <li>'T' if the integer is 3</li>
     * </ul>
     */
    public static char intToChar(int integer) {
        return BASES[integer];
    }

}
