package containers;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtil {

    // corresponds to ["ACCC", "ACGT", "AGTT", "ATGC", "GCAA", "GTAT", "TATA", "TATC", "TTGT"]
    public static final long[] PATTERNS = new long[]{21L, 27L, 47L, 57L, 144L, 179L, 204L, 205L, 251L};

    // corresponds to ["ATTT", "AGAT", "CAGT", "CGGG", "CACA", "GGTC", "TCTG", "TCTA"]
    public static final long[] OTHER_PATTERNS = new long[]{63L, 35L, 75L, 106L, 68L, 173L, 222L, 220L};

    public static void search(ContainerFactory containerFactory) {
        Container container = containerFactory.createContainer(4);
        container.build(PATTERNS);

        for (long pattern : TestUtil.PATTERNS) {
            assertThat(container.search(pattern)).isTrue();
        }
        for (long pattern : TestUtil.OTHER_PATTERNS) {
            assertThat(container.search(pattern)).isFalse();
        }
    }

}
