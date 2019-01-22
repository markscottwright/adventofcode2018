package adventofcode2018;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class Day18Test {

    @Test
    public void testSample() {
        //@formatter:off
        String lumberAreaScan = 
                ".#.#...|#.\n" + 
                ".....#|##|\n" + 
                ".|..|...#.\n" + 
                "..|#.....#\n" + 
                "#.#|||#|#|\n" + 
                "...#.||...\n" + 
                ".|....|...\n" + 
                "||...#|.#|\n" + 
                "|.||||..|.\n" + 
                "...#.|..|.\n";
        String expectedNextState =
                ".......##.\n" + 
                "......|###\n" + 
                ".|..|...#.\n" + 
                "..|#||...#\n" + 
                "..##||.|#|\n" + 
                "...#||||..\n" + 
                "||...|||..\n" + 
                "|||||.||.|\n" + 
                "||||||||||\n" + 
                "....||..|.\n";
        //@formatter:on

        Character[][] lumberArea = Day18
                .parse(Arrays.asList(lumberAreaScan.split("\\n")));
        Character[][] nextState = Day18.evolveLumberArea(lumberArea);
        assertEquals(expectedNextState.trim(),
                Day18.printLumberArea(nextState).trim());
    }

}
