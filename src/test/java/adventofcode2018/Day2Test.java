package adventofcode2018;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class Day2Test {

    @Test
    public void checksumReturnCorrectValues() {
        assertTrue(Day2.boxIdContains("aa", 2));
        assertTrue(Day2.boxIdContains("aabb", 2));
        assertFalse(Day2.boxIdContains("abcdef", 2)
                || Day2.boxIdContains("abcdef", 3));
        assertTrue(Day2.boxIdContains("bababc", 2)
                && Day2.boxIdContains("bababc", 3));
        assertTrue(Day2.boxIdContains("abbcde", 2));
        assertFalse(Day2.boxIdContains("abbcde", 3));
        assertTrue(Day2.boxIdContains("ababab", 3));
    }

    @Test
    public void checksum_ofSampleData_returnsExpectedValue()
            throws IOException {
        String data = "abcdef\n" + "bababc\n" + "abbcde\n" + "abcccd\n"
                + "aabcdd\n" + "abcdee\n" + "ababab\n";
        assertEquals(12, Day2.checksum(new StringReader(data)));
    }

}
