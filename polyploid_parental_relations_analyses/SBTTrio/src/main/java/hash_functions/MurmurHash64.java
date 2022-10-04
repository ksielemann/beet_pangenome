package hash_functions;

import java.util.concurrent.ThreadLocalRandom;

public class MurmurHash64 implements HashFunction {

    private static final long C1 = 0x87c37b91114253d5L;
    private static final long C2 = 0x4cf5ad432745937fL;
    private static final int R1 = 31;
    private final long seed;

    public MurmurHash64() {
        seed = ThreadLocalRandom.current().nextLong();
    }

    /**
     * Implements the 128-bit MurmurHash3 function for 64-bit inputs and returns only the first 64-bits of the hash.
     *
     * @param pattern 64-bit input
     * @return 64-bit hash of the input
     */
    public long hash(long pattern) {
        long h1 = seed;
        long h2 = seed;

        long k1 = 0;
        k1 ^= pattern;
        k1 *= C1;
        k1 = Long.rotateLeft(k1, R1);
        k1 *= C2;
        h1 ^= k1;

        h1 ^= 8;
        h2 ^= 8;

        h1 += h2;
        h2 += h1;

        h1 = finalize(h1);
        h2 = finalize(h2);

        h1 += h2;

        return h1;
    }

    private static long finalize(long hash) {
        hash ^= hash >>> 33;
        hash *= 0xff51afd7ed558ccdL;
        hash ^= hash >>> 33;
        hash *= 0xc4ceb9fe1a85ec53L;
        hash ^= hash >>> 33;
        return hash;
    }

}
