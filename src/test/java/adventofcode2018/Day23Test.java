package adventofcode2018;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import adventofcode2018.Day23.Nanobot;
import adventofcode2018.Day23.Position;
import adventofcode2018.Day23.Volume;

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
    public void testVolumeIntersection() {
        Volume vol1 = new Day23.Volume(new Position(0,0,0), 1);
        Volume vol2 = new Day23.Volume(new Position(0,0,0), 1);
        assertEquals(3, vol1.intersection(vol2).get().xSide);
        assertEquals(3, vol1.intersection(vol2).get().ySide);
        assertEquals(3, vol1.intersection(vol2).get().zSide);
        assertEquals(27, vol1.intersection(vol2).get().cubicVolume().intValue());
        
        Volume vol3 = new Day23.Volume(new Position(1,0,0), 1);
        assertEquals(2, vol1.intersection(vol3).get().xSide);
        assertEquals(3, vol1.intersection(vol3).get().ySide);
        assertEquals(3, vol1.intersection(vol3).get().zSide);
        assertEquals(18, vol1.intersection(vol3).get().cubicVolume().intValue());
    }
}
