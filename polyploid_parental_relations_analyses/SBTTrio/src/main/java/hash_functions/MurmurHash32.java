package hash_functions;

import java.util.concurrent.ThreadLocalRandom;

public class MurmurHash32 implements HashFunction {

    private static final int C1 = 0xcc9e2d51;
    private static final int C2 = 0x1b873593;
    private static final int R1 = 15;
    private static final int R2 = 13;
    private static final int M = 5;
    private static final int N = 0xe6546b64;
    private final int seed;

    public MurmurHash32() {
        seed = ThreadLocalRandom.current().nextInt();
    }

    /**
     * Implements the 32-bit MurmurHash3 function for 32-bit inputs (which are given as longs)
     * and returns a 64-bit value of which the first 32-bit are occupied by the hash.
     *
     * @param pattern 32-bit input given as a long
     * @return 32-bit hash of the input given as a long
     */
    @Override
    public long hash(long pattern) {
        int hash = seed;

        int k = (int) (pattern & 0xffff);
        hash = mix(k, hash);

        hash ^= 4;
        return finalize(hash);
    }

    private static int mix(int k, int hash) {
        k *= C1;
        k = Integer.rotateLeft(k, R1);
        k *= C2;
        hash ^= k;
        return Integer.rotateLeft(hash, R2) * M + N;
    }

    private static int finalize(int hash) {
        hash ^= hash >>> 16;
        hash *= 0x85ebca6b;
        hash ^= hash >>> 16;
        hash *= 0xc2b2ae35;
        hash ^= hash >>> 16;
        return hash;
    }

}
