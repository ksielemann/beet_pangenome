package base_data_structures;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class BitVectorTest {

    @Test
    void createBitVector() {
        BitVector vector = new BitVector(64);
        assertThat(vector.getNumWords()).isEqualTo(1);

        vector = new BitVector(257);
        assertThat(vector.getNumWords()).isEqualTo(5);

        vector = new BitVector(528);
        assertThat(vector.getNumWords()).isEqualTo(9);
    }

    @Test
    void setSingleBit() {
        BitVector vector = new BitVector(100);
        long[] expected = new long[] {0, 0};

        assertThat(vector.getWords()).isEqualTo(expected);

        vector.set(10);
        expected[0] = 1024;

        assertThat(vector.getWords()).isEqualTo(expected);

        vector.set(67);
        expected[1] = 8;

        assertThat(vector.getWords()).isEqualTo(expected);
    }

    @Test
    void clearSingleBit() {
        BitVector vector = new BitVector(100);
        vector.set(2);
        vector.set(10);
        vector.set(66);
        vector.set(67);
        long[] expected = new long[] {1028, 12};

        assertThat(vector.getWords()).isEqualTo(expected);

        vector.clear(50);

        assertThat(vector.getWords()).isEqualTo(expected);

        vector.clear(10);
        expected[0] = 4;

        assertThat(vector.getWords()).isEqualTo(expected);

        vector.clear(66);
        expected[1] = 8;

        assertThat(vector.getWords()).isEqualTo(expected);
    }

    @Test
    void getSingleBit() {
        int[] onePositions = new int[] {2, 10, 66, 67};

        BitVector vector = new BitVector(100);
        for (int pos: onePositions) {
            vector.set(pos);
        }

        HashSet<Integer> ones = new HashSet<>();
        for (int pos: onePositions) {
            ones.add(pos);
        }

        for (int i = 0; i < 100; i++) {
                if (ones.contains(i)) {
                    assertThat(vector.get(i)).isTrue();
                } else {
                    assertThat(vector.get(i)).isFalse();
                }
        }
    }

    @Test
    void set() {
        BitVector vector = new BitVector(128);
        long[] expected = new long[] {0, 0};

        assertThat(vector.getWords()).isEqualTo(expected);

        vector.set(0, 64, 3470458675940306501L);
        expected[0] = 3470458675940306501L;

        assertThat(vector.getWords()).isEqualTo(expected);

        vector.set(64, 128, 1670894530193786412L);
        expected[1] = 1670894530193786412L;

        assertThat(vector.getWords()).isEqualTo(expected);
    }

    @Test
    void setOverlapping() {
        BitVector vector = new BitVector(128);
        vector.set(0, 64, Long.MAX_VALUE);
        vector.set(64, 128, Long.MAX_VALUE);

        vector.set(60, 68, 0L);

        assertThat(vector.getWords()).isEqualTo(new long[] {
                0xfffffffffffffffL,
                0x7ffffffffffffff0L
        });

        vector.set(60, 68, 0xf7L);

        assertThat(vector.getWords()).isEqualTo(new long[] {
                Long.MAX_VALUE,
                Long.MAX_VALUE
        });
    }

    @Test
    void get() {
        BitVector vector = new BitVector(128);
        vector.set(0, 64, 3470458675940306501L);
        vector.set(64, 128, 1670894530193786412L);

        long expected = 0b1000001011L;
        assertThat(vector.get(10, 20)).isEqualTo(expected);

        expected = 0b100010L;
        assertThat(vector.get(68, 74)).isEqualTo(expected);

        expected = 0b110000110L;
        assertThat(vector.get(59, 68)).isEqualTo(expected);
    }

}
