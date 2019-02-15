package adventofcode2018;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import adventofcode2018.Day25.Constellation;
import adventofcode2018.Day25.Position;

public class Day25Test {

    @Test
    public void testSampelData1() {
        String data =
        //@formatter:off
                "0,0,0,0\r\n" + 
                " 3,0,0,0\r\n" + 
                " 0,3,0,0\r\n" + 
                " 0,0,3,0\r\n" + 
                " 0,0,0,3\r\n" + 
                " 0,0,0,6\r\n" + 
                " 9,0,0,0\r\n" + 
                "12,0,0,0";
                //@formatter:on

        Set<Position> points = Arrays.stream(data.split("\r\n"))
                .map(Day25.Position::parse).collect(Collectors.toSet());
        HashSet<Constellation> constellations = Constellation.build(points);
        assertEquals(2, constellations.size());
    }

    @Test
    public void testSampelData2() {
        String data =
        //@formatter:off
                "-1,2,2,0\r\n" + 
                "0,0,2,-2\r\n" + 
                "0,0,0,-2\r\n" + 
                "-1,2,0,0\r\n" + 
                "-2,-2,-2,2\r\n" + 
                "3,0,2,-1\r\n" + 
                "-1,3,2,2\r\n" + 
                "-1,0,-1,0\r\n" + 
                "0,2,1,-2\r\n" + 
                "3,0,0,0";
        //@formatter:off

        Set<Position> points = Arrays.stream(data.split("\r\n"))
                .map(Day25.Position::parse).collect(Collectors.toSet());
        HashSet<Constellation> constellations = Constellation.build(points);
        assertEquals(4, constellations.size());
    }
    
    @Test
    public void testSampleData3() {
        String data =
        //@formatter:off
                "1,-1,0,1\r\n" + 
                "2,0,-1,0\r\n" + 
                "3,2,-1,0\r\n" + 
                "0,0,3,1\r\n" + 
                "0,0,-1,-1\r\n" + 
                "2,3,-2,0\r\n" + 
                "-2,2,0,0\r\n" + 
                "2,-2,0,-1\r\n" + 
                "1,-1,0,-1\r\n" + 
                "3,2,0,2";
        //@formatter:off

        Set<Position> points = Arrays.stream(data.split("\r\n"))
                .map(Day25.Position::parse).collect(Collectors.toSet());
        HashSet<Constellation> constellations = Constellation.build(points);
        assertEquals(3, constellations.size());
    }
    @Test
    public void testSampleData4() {
        String data =
        //@formatter:off
                "1,-1,-1,-2\r\n" + 
                "-2,-2,0,1\r\n" + 
                "0,2,1,3\r\n" + 
                "-2,3,-2,1\r\n" + 
                "0,2,3,-2\r\n" + 
                "-1,-1,1,-2\r\n" + 
                "0,-2,-1,0\r\n" + 
                "-2,2,3,-1\r\n" + 
                "1,2,2,0\r\n" + 
                "-1,-2,0,-2";
        //@formatter:off

        Set<Position> points = Arrays.stream(data.split("\r\n"))
                .map(Day25.Position::parse).collect(Collectors.toSet());
        HashSet<Constellation> constellations = Constellation.build(points);
        assertEquals(8, constellations.size());
    }}
