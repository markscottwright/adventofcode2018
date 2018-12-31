package adventofcode2018;

import static org.junit.Assert.*;

import org.junit.Test;

public class Day6Test {

    @Test
    public void manhattanDistanceIsCorrect() {
        assertEquals(2, new Day6.Coordinate(2, 2).manhattanDistance(1, 1));
        assertEquals(3, new Day6.Coordinate(1, 1).manhattanDistance(2, 3));
    }

}
