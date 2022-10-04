package hash_functions;

public class MurmurHash64Finalizer implements HashFunction {

    /**
     * Implements the finalizer of the 128-bit MurmurHash3 function for 64-bit inputs and returns the 64-bit
     * hash of the input.
     *
     * @param pattern 64-bit input
     * @return 64-bit hash of the input
     */
    @Override
    public long hash(long pattern) {
        pattern ^= pattern >>> 33;
        pattern *= 0xff51afd7ed558ccdL;
        pattern ^= pattern >>> 33;
        pattern *= 0xc4ceb9fe1a85ec53L;
        pattern ^= pattern >>> 33;
        return pattern;
    }
}
