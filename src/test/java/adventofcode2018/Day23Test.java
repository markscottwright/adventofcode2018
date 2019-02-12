package adventofcode2018;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import adventofcode2018.Day23.BestPositionFinder;
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
        Volume vol1 = new Day23.Volume(new Position(0, 0, 0), 1);
        Volume vol2 = new Day23.Volume(new Position(0, 0, 0), 1);
        assertEquals(3, vol1.intersection(vol2).get().xSide);
        assertEquals(3, vol1.intersection(vol2).get().ySide);
        assertEquals(3, vol1.intersection(vol2).get().zSide);
        assertEquals(27,
                vol1.intersection(vol2).get().cubicVolume().intValue());

        Volume vol3 = new Day23.Volume(new Position(1, 0, 0), 1);
        assertEquals(2, vol1.intersection(vol3).get().xSide);
        assertEquals(3, vol1.intersection(vol3).get().ySide);
        assertEquals(3, vol1.intersection(vol3).get().zSide);
        assertEquals(18,
                vol1.intersection(vol3).get().cubicVolume().intValue());
    }

    @Test
    public void testBotRanges() {
        Volume v1 = new Volume(new Position(0, 0, 0), 1, 1, 1);
        assertTrue(v1.isPoint());
        Nanobot outOfRange = new Nanobot(1, 1, 1, 1);
        assertFalse(outOfRange.signalRangeVolume().isPoint());
        assertTrue(v1.intersection(outOfRange.signalRangeVolume()).isPresent());
        assertFalse(outOfRange.inRange(v1.minCorner));

        Nanobot inRange = new Nanobot(1, 0, 0, 1);
        assertTrue(v1.intersection(inRange.signalRangeVolume()).isPresent());
        assertTrue(inRange.inRange(v1.minCorner));
    }

    @Test
    public void testQuadrants() {
        Volume vol = new Day23.Volume(new Position(50, 50, 50), 50);
        Volume[] quadrants = vol.quadrants();
        for (Volume q1 : quadrants) {
            for (Volume q2 : quadrants) {
                if (!q1.equals(q2)) {
                    assertFalse(q1.intersection(q2).isPresent());
                }
            }
        }
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
        Volume containingVolume = Nanobot.containingVolume(bots);

        Volume v1 = new Volume(new Position(10, 10, 10), 21, 21, 21);
        List<Nanobot> inRange = Day23.botsInRange(bots, v1);
        Optional<Volume> intersection = bots.get(1).signalRangeVolume()
                .intersection(v1);
        assertTrue(intersection.isPresent());
        assertTrue(inRange.contains(bots.get(1)));

        Position p = new Position(12, 12, 12);
        assertEquals(5l, bots.stream().filter(b -> b.inRange(p)).count());

        BestPositionFinder finder = new BestPositionFinder();
        finder.find(containingVolume, bots);
        assertEquals(5, finder.getMostBotsInRange());
        assertEquals(1, finder.getMostConnectedPositions().size());
        assertEquals(new Position(12, 12, 12),
                finder.getMostConnectedPositions().get(0));
    
        BestPositionFinder finder2 = new BestPositionFinder();
        finder2.find2(containingVolume, bots);
        assertEquals(5, finder2.getMostBotsInRange());
        assertEquals(1, finder2.getMostConnectedPositions().size());
        assertEquals(new Position(12, 12, 12),
                finder2.getMostConnectedPositions().get(0));}

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
