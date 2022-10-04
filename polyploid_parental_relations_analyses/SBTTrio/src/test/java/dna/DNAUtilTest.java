package dna;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DNAUtilTest {

    @Test
    void createDNAUtil() {
        DNAUtil dnaUtil = new DNAUtil(21);
        assertThat(dnaUtil.getKmerSize()).isEqualTo(21);
    }

    @Test
    void longToString() {
        DNAUtil dnaUtil = new DNAUtil(18);
        // represents ACGTTAGCTAACCCTGAT
        long kmer = 0b000110111100100111000001010111100011L;
        assertThat(dnaUtil.longToString(kmer)).isEqualTo("ACGTTAGCTAACCCTGAT");

        // represents CCTACAGGACGTACGTAT
        kmer = 0b010111000100101000011011000110110011L;
        assertThat(dnaUtil.longToString(kmer)).isEqualTo("CCTACAGGACGTACGTAT");
    }

    @Test
    void stringToLong() {
        DNAUtil dnaUtil = new DNAUtil(18);

        String kmer01 = "ACGTTAGCTAACCCTGAT";
        assertThat(dnaUtil.stringToLong(kmer01)).isEqualTo(0b000110111100100111000001010111100011L);

        String kmer02 = "CCTACAGGACGTACGTAT";
        assertThat(dnaUtil.stringToLong(kmer02)).isEqualTo(0b010111000100101000011011000110110011L);
    }

    @Test
    void reverseComplement() {
        DNAUtil dnaUtil = new DNAUtil(18);

        String kmer = "ACGTTAGCTAACCCTGAT";
        String reverseComplement = "ATCAGGGTTAGCTAACGT";
        assertThat(dnaUtil.longToString(dnaUtil.reverseComplement(dnaUtil.stringToLong(kmer)))).isEqualTo(reverseComplement);

        kmer = "CCTACAGGACGTACGTAT";
        reverseComplement = "ATACGTACGTCCTGTAGG";
        assertThat(dnaUtil.longToString(dnaUtil.reverseComplement(dnaUtil.stringToLong(kmer)))).isEqualTo(reverseComplement);
    }

    @Test
    void canonicalForm() {
        DNAUtil dnaUtil = new DNAUtil(4);

        // represent GATA
        long kmer = 0b10001100L;
        assertThat(dnaUtil.canonicalForm(kmer)).isEqualTo(kmer);

        // represent ACTT
        kmer = 0b11111L;
        long reverseComplement = 0b1011L;
        assertThat(dnaUtil.canonicalForm(kmer)).isEqualTo(reverseComplement);
    }

}
