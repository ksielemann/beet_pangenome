package containers;

import containers.bit_packed_sorted_array.BitPackedSortedArray;
import containers.bit_packed_sorted_array.BitPackedSortedArrayFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BitPackedSortedArrayTest {

    @Test
    void createBitPackedSortedArray() {
        BitPackedSortedArray sortedArray = new BitPackedSortedArray(4);
        assertThat(sortedArray.getData()).isNull();
    }

    @Test
    void build() {
        BitPackedSortedArray sortedArray = new BitPackedSortedArray(4);

        sortedArray.build(TestUtil.PATTERNS);
        assertThat(sortedArray.getData().size()).isEqualTo(TestUtil.PATTERNS.length);

        sortedArray.build(TestUtil.OTHER_PATTERNS);
        assertThat(sortedArray.getData().size()).isEqualTo(TestUtil.OTHER_PATTERNS.length);
    }

    @Test
    void search() {
        TestUtil.search(new BitPackedSortedArrayFactory());
    }

}
