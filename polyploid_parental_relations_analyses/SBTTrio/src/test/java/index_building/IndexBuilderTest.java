package index_building;

import containers.bit_packed_sorted_array.BitPackedSortedArrayFactory;
import org.junit.jupiter.api.Test;
import util.DataStructure;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexBuilderTest {

    // represents ["AAACACCT", "AAACTTAG", "AGTTAGTT", "CAGGCATA", "CCCGACTA", "CTCCCCAA", "GAGTTGCT",
    // "GGCCCCTA", "GTCAGCAT", "TAATCTGG", "TCTTTCGG", "TGTCAATT", "TTTTCAAG", "CATCATAC", "GAGAATGA"]
    private static final long[] KMERS_INCLUDED = new long[]{
            279L, 498L, 12079L, 19020L, 22044L, 30032L, 35815L,
            42332L, 46227L, 50042L, 57306L, 60687L, 65346L, 19761L, 34872L
    };

    // represents ["ATTTTTTT", "AGAGAGAG", "CGCGCGCG", "CGATCGAT", "CTACTACT",
    // "AAAAAAAA", "TTTTTTTT", "GATAAAAA", "GTTGGTTG", "TATCTGTT"]
    private static final long[] KMERS_EXCLUDED = new long[]{
            16383L, 8738L, 26214L, 25443L, 29127L,
            0L, 65535L, 35840L, 48830L, 52719L
    };

    @Test
    void createIndexBuilder() {
        IndexBuilder indexBuilder = new IndexBuilder(5, 15);

        assertThat(indexBuilder.getAccessTrieHeight()).isEqualTo(5);
        assertThat(indexBuilder.getKmerSize()).isEqualTo(15);
    }

    @Test
    void buildIndexSorted() {
        String kmerDatabasePath = "src/test/resources/testKmerDatabase.txt";

        IndexBuilder indexBuilder = new IndexBuilder(2, 8);
        DataStructure index = indexBuilder.buildIndexSorted(kmerDatabasePath, new BitPackedSortedArrayFactory());

        for (long kmer : KMERS_INCLUDED) {
            assertThat(index.search(kmer)).isTrue();
        }
        for (long kmer : KMERS_EXCLUDED) {
            assertThat(index.search(kmer)).isFalse();
        }
    }

    @Test
    void buildIndex() {
        String kmerDatabasePath = "src/test/resources/testKmerDatabase.txt";

        IndexBuilder indexBuilder = new IndexBuilder(2, 8);
        DataStructure index = indexBuilder.buildIndex(kmerDatabasePath, new BitPackedSortedArrayFactory());

        for (long kmer : KMERS_INCLUDED) {
            assertThat(index.search(kmer)).isTrue();
        }
        for (long kmer : KMERS_EXCLUDED) {
            assertThat(index.search(kmer)).isFalse();
        }
    }

}
