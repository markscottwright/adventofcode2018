package adventofcode2018;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import adventofcode2018.Day23.Nanobot;
import adventofcode2018.Day23.Position;
import adventofcode2018.Day23.Volume;
import adventofcode2018.Day23.VolumeAndConnections;

public class Day23Test {

    @Test
    public void testNanobotParsing() {
        assertEquals(1, Nanobot.parse("pos=<1,2,3>, r=4").x);
        assertEquals(2, Nanobot.parse("pos=<1,2,3>, r=4").y);
        assertEquals(3, Nanobot.parse("pos=<1,2,3>, r=4").z);
        assertEquals(4, Nanobot.parse("pos=<1,2,3>, r=4").signalRadius);
    }

    @Test
    public void testDistances() {
        assertEquals(1,
                new Nanobot(0, 0, 0, 4).distance(new Nanobot(1, 0, 0, 1)));
    }

    @Test
    public void testSampleData() {
        //@formatter:off
        String data = 
            "pos=<0,0,0>, r=4\n" + 
            "pos=<1,0,0>, r=1\n" + 
            "pos=<4,0,0>, r=3\n" + 
            "pos=<0,2,0>, r=1\n" + 
            "pos=<0,5,0>, r=3\n" + 
            "pos=<0,0,3>, r=1\n" + 
            "pos=<1,1,1>, r=1\n" + 
            "pos=<1,1,2>, r=1\n" + 
            "pos=<1,3,1>, r=1\n";
        //@formatter:on
        List<Nanobot> nanobots = Arrays.stream(data.split("\n+"))
                .map(Nanobot::parse).collect(Collectors.toList());

        Nanobot strongestSignal = nanobots.stream().max(
                (b1, b2) -> Integer.compare(b1.signalRadius, b2.signalRadius))
                .get();
        long numInRange = nanobots.stream().filter(strongestSignal::inRange)
                .count();
        assertEquals(9, nanobots.size());
        assertEquals(7, numInRange);
    }

    @Test
    public void testSamplePositioning() throws Exception {
        String input =
        //@formatter:off
                "pos=<10,12,12>, r=2\r\n" + 
                "pos=<12,14,12>, r=2\r\n" + 
                "pos=<16,12,12>, r=4\r\n" + 
                "pos=<14,14,14>, r=6\r\n" + 
                "pos=<50,50,50>, r=200\r\n" + 
                "pos=<10,10,10>, r=5";
                //@formatter:on
        String[] lines = input.split("\r\n");
        List<Nanobot> bots = Arrays.stream(lines).map(Nanobot::parse)
                .collect(toList());

        VolumeAndConnections solution = Day23
                .findMostConnectedSpaceNearestOrigin(bots);
        assertEquals(new Position(12, 12, 12), solution.getVolume().minCorner);
    }

    @Test
    public void testDistanceFromVolume() {
        Volume vol = new Day23.Volume(new Position(50, 50, 50), 50);
        assertEquals(0, vol.distanceFrom(vol.minCorner));

        Position p1 = new Position(vol.minCorner.x, vol.minCorner.y,
                vol.minCorner.z - 1);
        assertEquals(1, vol.distanceFrom(p1));
        Position p2 = new Position(vol.minCorner.x - 1, vol.minCorner.y - 1,
                vol.minCorner.z - 1);
        assertEquals(3, vol.distanceFrom(p2));
    }
}
