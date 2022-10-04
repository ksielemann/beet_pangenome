package trio_binning;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;

import static org.assertj.core.api.Assertions.assertThat;

public class RoundRobinBuffferedWriterTest {

    @Test
    void createRoundRobinBufferedWriter() throws Exception {
        RoundRobinBufferedWriter writer =
                new RoundRobinBufferedWriter(4, "src/test/resources/testOutput", "csv");

        assertThat(writer.getCurrentIndex()).isEqualTo(-1);
        assertThat(writer.getWriters().length).isEqualTo(4);
        for (BufferedWriter bufferedWriter : writer.getWriters()) {
            assertThat(bufferedWriter).isNotNull();
        }
    }

    @Test
    void getNextWriter() throws Exception {
        RoundRobinBufferedWriter writer =
                new RoundRobinBufferedWriter(4, "src/test/resources/testOutput", "csv");

        for (int i = 0; i < 20; i++) {
            assertThat(writer.getNextWriter()).isEqualTo(writer.getWriters()[i % 4]);
        }
    }

}
