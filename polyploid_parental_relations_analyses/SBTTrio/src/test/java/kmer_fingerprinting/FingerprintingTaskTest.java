package kmer_fingerprinting;

import containers.bit_packed_sorted_array.BitPackedSortedArrayFactory;
import dna.DNAUtil;
import dna.KmerSetSampler;
import index_building.IndexBuilder;
import org.junit.jupiter.api.Test;
import util.DataStructure;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class FingerprintingTaskTest {

    @Test
    void createFingerprintingTask() throws IOException {
        int kmerSize = 5;
        IndexBuilder indexBuilder = new IndexBuilder(1, kmerSize);
        DataStructure[] databases = new DataStructure[]{
                indexBuilder.buildIndex("src/test/resources/testHapmerDatabase01.txt", new BitPackedSortedArrayFactory()),
                indexBuilder.buildIndex("src/test/resources/testHapmerDatabase02.txt", new BitPackedSortedArrayFactory())
        };
        KmerSetSampler kmerSetSampler = new KmerSetSampler(kmerSize, new DNAUtil(kmerSize));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/test/resources/fingerprintingTestOutput.csv"));
        int setID = 5;
        int setSize = 100;

        FingerprintingTask task = new FingerprintingTask(databases, kmerSetSampler, bufferedWriter, setID, setSize);

        assertThat(task.getDatabases()).isEqualTo(databases);
        assertThat(task.getKmerSetSampler()).isEqualTo(kmerSetSampler);
        assertThat(task.getBufferedWriter()).isEqualTo(bufferedWriter);
        assertThat(task.getSetID()).isEqualTo(setID);
        assertThat(task.getSetSize()).isEqualTo(setSize);

        bufferedWriter.close();
    }

    @Test
    void run() throws IOException {
        int kmerSize = 5;
        IndexBuilder indexBuilder = new IndexBuilder(1, kmerSize);
        DataStructure[] databases = new DataStructure[]{
                indexBuilder.buildIndex("src/test/resources/testHapmerDatabase01.txt", new BitPackedSortedArrayFactory()),
                indexBuilder.buildIndex("src/test/resources/testHapmerDatabase02.txt", new BitPackedSortedArrayFactory())
        };
        KmerSetSampler kmerSetSampler = new KmerSetSampler(kmerSize, new DNAUtil(kmerSize));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("src/test/resources/fingerprintingTestOutput.csv"));
        int setID = 5;
        int setSize = 100;

        FingerprintingTask task = new FingerprintingTask(databases, kmerSetSampler, bufferedWriter, setID, setSize);
        task.run();

        bufferedWriter.close();
    }

}
