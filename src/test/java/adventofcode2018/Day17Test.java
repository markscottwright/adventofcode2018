package adventofcode2018;

import java.util.Arrays;

import org.junit.Test;

import adventofcode2018.Day17.Ground;

public class Day17Test {

    @Test
    public void testParsingSample() {
        //@formatter:off
        String scanText = "x=495, y=2..7\n" + 
                "y=7, x=495..501\n" + 
                "x=501, y=3..7\n" + 
                "x=498, y=2..4\n" + 
                "x=506, y=1..2\n" + 
                "x=498, y=10..13\n" + 
                "x=504, y=10..13\n" + 
                "y=13, x=498..504";
        //@formatter:on

        Ground ground = Ground.parseScan(Arrays.asList(scanText.split("\n+")));
        ground.print(System.out);
        ground.seep();
        ground.print(System.out);
    }

}
