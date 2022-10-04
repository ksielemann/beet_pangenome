package base_data_structures;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BitPackedKmerArrayTest {

    private static final long[] KMERS = new long[] {
            0b110000010111001000101L,
            0b111100011101001011000L,
            0b011000000101001100010L,
            0b11111100000000110001011000L
    };

    @Test
    void createBitPackedKmerArray() {
        BitPackedKmerArray array = new BitPackedKmerArray(249, 21);

        assertThat(array.size()).isEqualTo(249);
        assertThat(array.getKmerSize()).isEqualTo(21);
        assertThat(array.getBitVector()).isNotNull();
        // 249 * 21 / 64 = 81.703125
        assertThat(array.getBitVector().getNumWords()).isEqualTo(82);
    }

    @Test
    void set() {
        BitPackedKmerArray array = new BitPackedKmerArray(4, 21);

        for (int i = 0; i < 4; i++) {
            array.set(i, KMERS[i]);
        }

        assertThat(array.getBitVector().getWords()).isEqualTo(new long[] {
                3470458675940306501L,
                525868L
        });
    }

    @Test
    void get() {
        BitPackedKmerArray array = new BitPackedKmerArray(4, 21);

        for (int i = 0; i < 4; i++) {
            array.set(i, KMERS[i]);
        }

        for (int i = 0; i < 3; i++) {
            assertThat(array.get(i)).isEqualTo(KMERS[i]);
        }

        assertThat(array.get(3)).isEqualTo(0b100000000110001011000L);
    }

}
