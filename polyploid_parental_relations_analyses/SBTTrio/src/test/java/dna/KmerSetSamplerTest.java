package dna;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KmerSetSamplerTest {

    @Test
    void createKmerSetSampler() {
        KmerSetSampler kmerSetSampler = new KmerSetSampler(8);

        assertThat(kmerSetSampler.getKmerSize()).isEqualTo(8);
    }

    @Test
    void randomlySampleWithoutReplacement() {
        int kmerSize = 8;
        long maxKmer = 1L << (kmerSize << 1);
        KmerSetSampler kmerSetSampler = new KmerSetSampler(kmerSize);

        for (int i = 0; i < 20; i++) {
            long[] randomSet = kmerSetSampler.randomlySampleWithoutReplacement(200);

            assertThat(randomSet.length).isEqualTo(200);
            assertThat(randomSet).doesNotHaveDuplicates();

            for (long kmer : randomSet) {
                assertThat(kmer).isLessThan(maxKmer);
            }
        }
    }

    @Test
    void randomlySample() {
        int kmerSize = 8;
        long maxKmer = 1L << (kmerSize << 1);
        KmerSetSampler kmerSetSampler = new KmerSetSampler(kmerSize);

        for (int i = 0; i < 20; i++) {
            long[] randomSet = kmerSetSampler.randomlySample(200);

            assertThat(randomSet.length).isEqualTo(200);

            for (long kmer : randomSet) {
                assertThat(kmer).isLessThan(maxKmer);
            }
        }
    }

    @Test
    void randomlySampleCanonicalSetWithoutReplacement() {
        int kmerSize = 8;
        long maxKmer = 1L << (kmerSize << 1);
        KmerSetSampler kmerSetSampler = new KmerSetSampler(kmerSize, new DNAUtil(kmerSize));

        DNAUtil dnaUtil = new DNAUtil(kmerSize);

        for (int i = 0; i < 20; i++) {
            long[] randomSet = kmerSetSampler.randomlySampleCanonicalSetWithoutReplacement(200);

            assertThat(randomSet.length).isEqualTo(200);
            assertThat(randomSet).doesNotHaveDuplicates();

            for (long kmer : randomSet) {
                assertThat(kmer).isLessThan(maxKmer);
                assertThat(dnaUtil.canonicalForm(kmer)).isEqualTo(kmer);
            }
        }
    }

}
