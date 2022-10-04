package hash_functions;

public class MurmurHash32Finalizer implements HashFunction {

    /**
     * Implements the finalizer of the 32-bit MurmurHash3 function for 32-bit inputs (which are given as longs)
     * and returns a 64-bit value of which the first 32-bit are occupied by the hash.
     *
     * @param pattern 32-bit input given as a long
     * @return 32-bit hash of the input given as a long
     */
    @Override
    public long hash(long pattern) {
        pattern ^= pattern >>> 16;
        pattern *= 0x85ebca6b;
        pattern ^= pattern >>> 16;
        pattern *= 0xc2b2ae35;
        pattern ^= pattern >>> 16;
        return pattern;
    }
}
