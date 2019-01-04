package adventofcode2018;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;

import org.junit.Test;

public class Day8Test {

    @Test
    public void testSampleData() throws IOException {
        int metadataTotal = Day8.getMetadataValue(new StreamTokenizer(
                new StringReader("2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2")));
        assertEquals(138, metadataTotal);
    }

}
