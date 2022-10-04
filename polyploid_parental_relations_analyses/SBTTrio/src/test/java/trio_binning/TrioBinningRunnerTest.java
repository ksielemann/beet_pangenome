package trio_binning;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TrioBinningRunnerTest {

    private final String hapmerDatabasePath = "src/test/resources/testHapmerDatabase01.txt";
    private final String otherHapmerDatabasePath = "src/test/resources/testHapmerDatabase02.txt";
    private final int height = 1;
    private final int size = 5;
    private final int threads = 4;
    private final String outputPrefix = "src/test/resources/testOutput";

    @Test
    void performTrioBinningUncompressed() throws Exception {
        String readFilePath = "src/test/resources/testReads.fastq";

        TrioBinningRunner.performTrioBinning(new String[]{hapmerDatabasePath},
                readFilePath, height, size, threads, outputPrefix);

        List<String> expected = Arrays.asList("0," + (double) 2/81, "1," + (double) 1/105,
                "2,0.0", "3," + (double) 2/99, "4," + (double) 2/81, "5," + (double) 1/105,
                "6,0.0", "7," + (double) 2/99);

        List<String> lines = new LinkedList<>();
        for (int i = 0; i < threads; i++) {
            String outputPath = outputPrefix + "_" + i + ".csv";
            lines.addAll(Files.readAllLines(Paths.get(outputPath)));
        }

        assertThat(lines.size()).isEqualTo(8);
        assertThat(lines).containsAll(expected);
    }

    @Test
    void performTrioBinningCompressed() throws Exception {
        String readFilePath = "src/test/resources/testReads.fastq.gz";

        TrioBinningRunner.performTrioBinning(new String[]{hapmerDatabasePath},
                readFilePath, height, size, threads, outputPrefix);

        List<String> expected = Arrays.asList("0," + (double) 2/81, "1," + (double) 1/105,
                "2,0.0", "3," + (double) 2/99, "4," + (double) 2/81, "5," + (double) 1/105,
                "6,0.0", "7," + (double) 2/99);

        List<String> lines = new LinkedList<>();
        for (int i = 0; i < threads; i++) {
            String outputPath = outputPrefix + "_" + i + ".csv";
            lines.addAll(Files.readAllLines(Paths.get(outputPath)));
        }

        assertThat(lines.size()).isEqualTo(8);
        assertThat(lines).containsAll(expected);
    }

    @Test
    void performTrioBinningCompressedMultipleDatabases() throws Exception {
        String readFilePath = "src/test/resources/testReads.fastq.gz";

        TrioBinningRunner.performTrioBinning(new String[]{hapmerDatabasePath, otherHapmerDatabasePath},
                readFilePath, height, size, threads, outputPrefix);

        List<String> expected = Arrays.asList("0," + (double) 2/81 + ",0.0", "1," + (double) 1/105 + ",0.0",
                "2,0.0,0.01", "3," + (double) 2/99 + "," + (double) 1/99, "4," + (double) 2/81 + ",0.0", "5," + (double) 1/105 + ",0.0",
                "6,0.0,0.01", "7," + (double) 2/99 + "," + (double) 1/99);

        List<String> lines = new LinkedList<>();
        for (int i = 0; i < threads; i++) {
            String outputPath = outputPrefix + "_" + i + ".csv";
            lines.addAll(Files.readAllLines(Paths.get(outputPath)));
        }

        assertThat(lines.size()).isEqualTo(8);
        assertThat(lines).containsAll(expected);
    }

    @Test
    void main() throws Exception {
        String[] args = new String[]{"-d", hapmerDatabasePath,
                "-r", "src/test/resources/testReads.fastq.gz", "-h", "1", "-k", "5", "-t", "4", "-o", outputPrefix};

        TrioBinningRunner.main(args);

        // corresponds to ["0,2/81", "1,1/105", "2,0/", "3,2/99", "4,2/81", "5,1/105", "6,0/", "7,2/99"]
        List<String> expected = Arrays.asList("0," + (double) 2/81, "1," + (double) 1/105,
                "2,0.0", "3," + (double) 2/99, "4," + (double) 2/81, "5," + (double) 1/105,
                "6,0.0", "7," + (double) 2/99);

        List<String> lines = new LinkedList<>();
        for (int i = 0; i < threads; i++) {
            String outputPath = outputPrefix + "_" + i + ".csv";
            lines.addAll(Files.readAllLines(Paths.get(outputPath)));
        }

        assertThat(lines.size()).isEqualTo(8);
        assertThat(lines).containsAll(expected);
    }

    @Test
    void mainMultipleDatabases() throws Exception {
        String[] args = new String[]{"-d", hapmerDatabasePath + ";" + otherHapmerDatabasePath,
                "-r", "src/test/resources/testReads.fastq.gz", "-h", "1", "-k", "5", "-t", "4", "-o", outputPrefix};

        TrioBinningRunner.main(args);

        List<String> expected = Arrays.asList("0," + (double) 2/81 + ",0.0", "1," + (double) 1/105 + ",0.0",
                "2,0.0,0.01", "3," + (double) 2/99 + "," + (double) 1/99, "4," + (double) 2/81 + ",0.0", "5," + (double) 1/105 + ",0.0",
                "6,0.0,0.01", "7," + (double) 2/99 + "," + (double) 1/99);

        List<String> lines = new LinkedList<>();
        for (int i = 0; i < threads; i++) {
            String outputPath = outputPrefix + "_" + i + ".csv";
            lines.addAll(Files.readAllLines(Paths.get(outputPath)));
        }

        assertThat(lines.size()).isEqualTo(8);
        assertThat(lines).containsAll(expected);
    }

}
