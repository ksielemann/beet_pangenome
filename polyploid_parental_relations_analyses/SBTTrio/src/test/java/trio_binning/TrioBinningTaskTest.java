package trio_binning;

import containers.bit_packed_sorted_array.BitPackedSortedArrayFactory;
import index_building.IndexBuilder;
import kmer_counting.KmerCounter;
import org.junit.jupiter.api.Test;
import util.DataStructure;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TrioBinningTaskTest {

    private static final String READ = "ACGTACGTA";

    @Test
    void createTrioBinningTask() throws IOException {
        IndexBuilder indexBuilder = new IndexBuilder(1, 5);
        DataStructure hapmerDatabase = indexBuilder.buildIndex("src/test/resources/testHapmerDatabase01.txt", new BitPackedSortedArrayFactory());

        KmerCounter kmerCounter = new KmerCounter(5);
        BufferedWriter writer = new BufferedWriter(new FileWriter("src/test/resources/testOutput_0.csv"));

        TrioBinningTask task = new TrioBinningTask(new DataStructure[]{hapmerDatabase}, kmerCounter, writer, 0, READ);

        assertThat(task.getDatabases()).isEqualTo(new DataStructure[]{hapmerDatabase});
        assertThat(task.getKmerCounter()).isEqualTo(kmerCounter);
        assertThat(task.getReadID()).isEqualTo(0);
        assertThat(task.getRead()).isEqualTo(READ);

        writer.close();
    }

    @Test
    void runDatabase01() throws IOException {
        IndexBuilder indexBuilder = new IndexBuilder(1, 5);
        DataStructure hapmerDatabaseA = indexBuilder.buildIndex("src/test/resources/testHapmerDatabase01.txt", new BitPackedSortedArrayFactory());
        String outputPath = "src/test/resources/testOutput_0.csv";

        KmerCounter kmerCounter = new KmerCounter(5);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));

        TrioBinningTask task = new TrioBinningTask(new DataStructure[]{hapmerDatabaseA}, kmerCounter, writer, 0, READ);
        task.run();
        // expected output is: 0,1 because all canonical k-mers of the read appear in the database

        writer.close();

        List<String> lines = Files.readAllLines(Paths.get(outputPath));
        assertThat(lines).hasSize(1);
        assertThat(lines).containsOnly("0,1.0");
    }

    @Test
    void runDatabase02() throws IOException {
        IndexBuilder indexBuilder = new IndexBuilder(1, 5);
        DataStructure hapmerDatabaseA = indexBuilder.buildIndex("src/test/resources/testHapmerDatabase02.txt", new BitPackedSortedArrayFactory());
        String outputPath = "src/test/resources/testOutput_0.csv";

        KmerCounter kmerCounter = new KmerCounter(5);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));

        TrioBinningTask task = new TrioBinningTask(new DataStructure[]{hapmerDatabaseA}, kmerCounter, writer, 0, READ);
        task.run();
        // expected output is: 0,0.5 because 1 of 2 canonical k-mers of the read appears in the database

        writer.close();

        List<String> lines = Files.readAllLines(Paths.get(outputPath));
        assertThat(lines).hasSize(1);
        assertThat(lines).containsOnly("0,0.5");
    }

    @Test
    void runDatabases() throws IOException {
        IndexBuilder indexBuilder = new IndexBuilder(1, 5);
        DataStructure hapmerDatabaseA = indexBuilder.buildIndex("src/test/resources/testHapmerDatabase01.txt", new BitPackedSortedArrayFactory());
        DataStructure hapmerDatabaseB = indexBuilder.buildIndex("src/test/resources/testHapmerDatabase02.txt", new BitPackedSortedArrayFactory());
        DataStructure[] databases = new DataStructure[]{hapmerDatabaseA, hapmerDatabaseB};

        String outputPath = "src/test/resources/testOutput_0.csv";
        KmerCounter kmerCounter = new KmerCounter(5);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));

        TrioBinningTask task = new TrioBinningTask(databases, kmerCounter, writer, 0, READ);
        task.run();

        writer.close();

        List<String> lines = Files.readAllLines(Paths.get(outputPath));
        assertThat(lines).hasSize(1);
        assertThat(lines).containsOnly("0,1.0,0.5");
    }
}
