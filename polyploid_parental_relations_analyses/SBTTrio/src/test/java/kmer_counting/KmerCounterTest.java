package kmer_counting;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class KmerCounterTest {

    @Test
    void createKmerCounter() {
        KmerCounter kmerCounter = new KmerCounter(8);

        assertThat(kmerCounter.getKmerSize()).isEqualTo(8);
    }

    @Test
    void getAllKmers() {
        KmerCounter kmerCounter = new KmerCounter(5);
        String read = "ACGTACGTA";

        // corresponds to ["ACGTA", "CGTAC"]
        List<Long> expected = Arrays.asList(108L, 433L);

        Iterable<Long> canonicalKmers = kmerCounter.getAllKmers(read);

        assertThat(canonicalKmers).containsOnlyOnceElementsOf(expected);
        assertThat(canonicalKmers).containsAll(expected);
    }

}
