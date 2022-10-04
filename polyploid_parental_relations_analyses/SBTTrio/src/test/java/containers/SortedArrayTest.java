package containers;

import containers.sorted_array.SortedArray;
import containers.sorted_array.SortedArrayFactory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SortedArrayTest {

    @Test
    void createSortedArray() {
        SortedArray sortedArray = new SortedArray();
        assertThat(sortedArray.getData()).isNull();
    }

    @Test
    void build() {
        SortedArray sortedArray = new SortedArray();

        sortedArray.build(TestUtil.PATTERNS);
        assertThat(sortedArray.getData().length).isEqualTo(TestUtil.PATTERNS.length);
        assertThat(sortedArray.getData()).containsExactly(TestUtil.PATTERNS);

        sortedArray.build(TestUtil.OTHER_PATTERNS);
        assertThat(sortedArray.getData().length).isEqualTo(TestUtil.OTHER_PATTERNS.length);
        assertThat(sortedArray.getData()).containsExactly(TestUtil.OTHER_PATTERNS);
    }

    @Test
    void search() {
        TestUtil.search(new SortedArrayFactory());
    }

}
