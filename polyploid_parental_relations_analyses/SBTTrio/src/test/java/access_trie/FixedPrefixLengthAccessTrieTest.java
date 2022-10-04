package access_trie;

import containers.TestUtil;
import containers.sorted_array.SortedArray;
import org.junit.jupiter.api.Test;
import util.DataStructure;

import static org.assertj.core.api.Assertions.assertThat;

public class FixedPrefixLengthAccessTrieTest {

    @Test
    void createOptimizedAccessTrie() {
        int height = 3;
        int kmerSize = 21;

        FixedPrefixLengthAccessTrie accessTrie = new FixedPrefixLengthAccessTrie(height, kmerSize);

        assertThat(accessTrie.getHeight()).isEqualTo(height);
        assertThat(accessTrie.getKmerSize()).isEqualTo(kmerSize);
        assertThat(accessTrie.getBitMask()).isEqualTo(68719476735L);
        assertThat(accessTrie.getLeaves()).hasSize(64);
        assertThat(accessTrie.getLeaves()).containsOnlyNulls();
    }

    @Test
    void add() {
        int height = 3;
        int kmerSize = 7;

        FixedPrefixLengthAccessTrie accessTrie = new FixedPrefixLengthAccessTrie(height, kmerSize);

        SortedArray sortedArray = new SortedArray();
        sortedArray.build(TestUtil.PATTERNS);

        // the index corresponding to ACT is 0 * 4^2 + 1 * 4^1 + 3 * 4^0 = 7
        accessTrie.add(7L, sortedArray);

        DataStructure leaf = accessTrie.getLeaves()[7];

        assertThat(leaf).isNotNull();
        assertThat(leaf).isEqualTo(sortedArray);
    }

    @Test
    void search() {
        int height = 3;
        int kmerSize = 7;

        FixedPrefixLengthAccessTrie accessTrie = new FixedPrefixLengthAccessTrie(height, kmerSize);

        SortedArray sortedArray = new SortedArray();
        sortedArray.build(TestUtil.PATTERNS);

        // the index corresponding to ACT is 0 * 4^2 + 1 * 4^1 + 3 * 4^0 = 7
        accessTrie.add(7L, sortedArray);

        for (long pattern : TestUtil.PATTERNS) {
            assertThat(accessTrie.search((7 << 8) + pattern)).isTrue();
        }
        for (long pattern : TestUtil.OTHER_PATTERNS) {
            assertThat(accessTrie.search((7 << 8) + pattern)).isFalse();
        }

        // corresponds to TGTA
        assertThat(accessTrie.search(236L)).isFalse();
        assertThat(accessTrie.search(42L)).isFalse();
    }

}
