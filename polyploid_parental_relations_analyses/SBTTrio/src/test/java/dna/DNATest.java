package dna;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DNATest {

    @Test
    void charToInt() {
        assertThat(DNA.charToInt('A')).isEqualTo(0);
        assertThat(DNA.charToInt('C')).isEqualTo(1);
        assertThat(DNA.charToInt('G')).isEqualTo(2);
        assertThat(DNA.charToInt('T')).isEqualTo(3);
    }

    @Test
    void intToChar() {
        assertThat(DNA.intToChar(0)).isEqualTo('A');
        assertThat(DNA.intToChar(1)).isEqualTo('C');
        assertThat(DNA.intToChar(2)).isEqualTo('G');
        assertThat(DNA.intToChar(3)).isEqualTo('T');
    }

}
