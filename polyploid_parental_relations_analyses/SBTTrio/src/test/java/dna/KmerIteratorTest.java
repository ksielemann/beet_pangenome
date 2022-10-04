package dna;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KmerIteratorTest {

    @Test
    void createKmerIterator() {
        KmerIterator kmerIterator = new KmerIterator(8);

        assertThat(kmerIterator.getKmerSize()).isEqualTo(8);
        assertThat(kmerIterator.getCurrentKmer()).isEqualTo(0);
        assertThat(kmerIterator.getLastKmer()).isEqualTo(65536);
    }

    @Test
    void iterate() {
        KmerIterator kmerIterator = new KmerIterator(2);

        String[] result = new String[16];

        int position = 0;
        while (kmerIterator.hasNext()) {
            result[position] = kmerIterator.next();
            position++;
        }

        String[] expected = new String[] {"AA", "AC", "AG", "AT", "CA", "CC", "CG", "CT",
                "GA", "GC", "GG", "GT", "TA", "TC", "TG", "TT"};

        assertThat(result).isEqualTo(expected);
    }

}
