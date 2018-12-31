package adventofcode2018;

import static org.junit.Assert.*;

import org.junit.Test;

public class Day2Part2Test {

    @Test
    public void testDifferByOne() {
        assertTrue(Day2Part2.differByOne("a", "b"));
        assertFalse(Day2Part2.differByOne("abcde", "axcye"));
        assertTrue(Day2Part2.differByOne("fghij", "fguij"));
    }

}
