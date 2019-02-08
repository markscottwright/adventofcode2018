package adventofcode2018;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import adventofcode2018.Day23.Volume;

/**
 * Definitely going about part two the wrong way. If I had looked at the input
 * data, it would be obvious that enumerating all points in range was not
 * feasible.
 * 
 * Check out the Separating Axis Theory
 * <p>
 * http://www.dyn4j.org/2010/01/sat/
 * <p>
 * https://en.wikipedia.org/wiki/Hyperplane_separation_theorem
 * 
 * @author mwright
 *
 */
public class Day23 {

    public static class Volume {
        final Position minCorner;
        final int xSide; // note that this is inclusive
        final int ySide; // note that this is inclusive
        final int zSide; // note that this is inclusive

        public Volume(Position center, int radius) {
            this.minCorner = new Position(center.x - radius, center.y - radius,
                    center.z - radius);
            this.xSide = this.ySide = this.zSide = radius * 2;
        }

        private Volume(Position minCorner, int xSide, int ySide, int zSide) {
            assert xSide >= 0;
            assert ySide >= 0;
            assert zSide >= 0;
            this.minCorner = minCorner;
            this.xSide = xSide;
            this.ySide = ySide;
            this.zSide = zSide;
        }

        public static int[] intersection(int[] a, int[] b) {
            assert a[0] <= a[1];
            assert b[0] <= b[1];
            if ((b[0] <= a[0] && a[0] <= b[1])
                    || (b[0] <= a[1] && a[1] <= b[1])) {
                int[] intersection = new int[] { Integer.max(a[0], b[0]),
                        Integer.min(a[1], b[1]) };
                assert intersection[0] <= intersection[1];
                return intersection;
            } else
                return null;
        }

        Optional<Volume> intersection(Volume c) {
            int[] xIntersection = intersection(
                    new int[] { minCorner.x, minCorner.x + xSide },
                    new int[] { c.minCorner.x, c.minCorner.x + c.xSide });
            int[] yIntersection = intersection(
                    new int[] { minCorner.y, minCorner.y + ySide },
                    new int[] { c.minCorner.y, c.minCorner.y + c.ySide });
            int[] zIntersection = intersection(
                    new int[] { minCorner.z, minCorner.z + zSide },
                    new int[] { c.minCorner.z, c.minCorner.z + c.zSide });
            if (xIntersection == null || yIntersection == null
                    || zIntersection == null) {
                return Optional.empty();
            } else {
                return Optional.of(new Volume(
                        new Position(xIntersection[0], yIntersection[0],
                                zIntersection[0]),
                        xIntersection[1] - xIntersection[0],
                        yIntersection[1] - yIntersection[0],
                        zIntersection[1] - zIntersection[0]));
            }

        }

        BigInteger cubicVolume() {
            return BigInteger.valueOf(xSide + 1)
                    .multiply(BigInteger.valueOf(ySide + 1)
                            .multiply(BigInteger.valueOf(zSide + 1)));
        }

        @Override
        public String toString() {
            return "Volume [minCorner=" + minCorner + ", xSide=" + xSide
                    + ", ySide=" + ySide + ", zSide=" + zSide + "]";
        }
    }

    public static class Position {
        @Override
        public String toString() {
            return "Position [x=" + x + ", y=" + y + ", z=" + z + "]";
        }

        final int x;
        final int y;
        final int z;

        public Position(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        int distance(Position o) {
            return Math.abs(x - o.x) + Math.abs(y - o.y) + Math.abs(z - o.z);
        }
    }

    public static class Nanobot extends Position {
        @Override
        public String toString() {
            return "Nanobot [x=" + x + ", y=" + y + ", z=" + z
                    + ", signalRadius=" + signalRadius + "]";
        }

        final int signalRadius;

        public Nanobot(int x, int y, int z, int signalRadius) {
            super(x, y, z);
            this.signalRadius = signalRadius;
        }

        boolean inRange(Position o) {
            return distance(o) <= signalRadius;
        }

        Volume signalRangeVolume() {
            return new Volume(this, signalRadius);
        }

        PointsInRange pointsInRange() {
            return new PointsInRange(this);
        }

        public static Nanobot parse(String line) {
            Pattern pattern = Pattern
                    .compile("pos=<([0-9-]+),([0-9-]+),([0-9-]+)>, r=(\\d+)");
            Matcher matcher = pattern.matcher(line);
            matcher.matches();
            return new Nanobot(Integer.valueOf(matcher.group(1)),
                    Integer.valueOf(matcher.group(2)),
                    Integer.valueOf(matcher.group(3)),
                    Integer.valueOf(matcher.group(4)));
        }

        static class PointsInRange
                implements Iterable<Position>, Iterator<Position> {

            private int x, y, z;
            private Nanobot nanobot;

            PointsInRange(Nanobot b) {
                this.nanobot = b;
                this.x = b.x - b.signalRadius;
                this.y = b.y - b.signalRadius;
                this.z = b.z - b.signalRadius;

                while (!inRange())
                    if (!advance())
                        break;
            }

            private boolean inRange() {
                return nanobot.inRange(new Position(this.x, this.y, this.z));
            }

            /**
             * Advance to next allowed position, not necessarily in range.
             * Returns false at end of iteration.
             * 
             * TODO: this is inefficient.
             * 
             * @return
             */
            private boolean advance() {
                if (x == nanobot.signalRadius + nanobot.x
                        && y == nanobot.signalRadius + nanobot.y
                        && z == nanobot.signalRadius + nanobot.z)
                    return false;

                x += 1;
                if (x > nanobot.x + nanobot.signalRadius) {
                    x = nanobot.x - nanobot.signalRadius;
                    y += 1;
                    if (y > nanobot.y + nanobot.signalRadius) {
                        y = nanobot.y - nanobot.signalRadius;
                        z += 1;
                    }
                }
                return true;
            }

            @Override
            public Iterator<Position> iterator() {
                return this;
            }

            @Override
            public boolean hasNext() {
                return inRange();
            }

            @Override
            public Position next() {
                if (!inRange())
                    return null;
                Position position = new Position(x, y, z);

                // advance to next valid state
                do {
                    if (!advance())
                        break;
                } while (!inRange());
                return position;
            }

        }
    }

    static HashMap<HashSet<Nanobot>, Volume> findNewOverlaps(
            HashMap<HashSet<Nanobot>, Volume> oldOverlaps, List<Nanobot> bots) {
        HashMap<HashSet<Nanobot>, Volume> newOverlaps = new HashMap<>();
        for (var entry : oldOverlaps.entrySet()) {
            for (Nanobot b : bots) {
                if (entry.getKey().contains(b))
                    continue;

                Optional<Volume> signalOverlap = entry.getValue()
                        .intersection(b.signalRangeVolume());
                if (signalOverlap.isPresent()) {
                    HashSet<Nanobot> newEntry = Sets.newHashSet(b);
                    newEntry.addAll(entry.getKey());
                    newOverlaps.put(newEntry, signalOverlap.get());
                    System.out.println(
                            newEntry.size() + " : " + newOverlaps.size());
                }
            }
        }

        if (newOverlaps.size() > 0) {
            newOverlaps.putAll(findNewOverlaps(newOverlaps, bots));
        }

        return newOverlaps;
    }

    static HashSet<Nanobot> mostIntersections(HashSet<Nanobot> soFar,
            Volume volume, List<Nanobot> bots) {
        HashSet<Nanobot> bestCandidate = new HashSet<>();
        for (int i = 0; i < bots.size(); ++i) {
            Nanobot b = bots.get(i);
            if (!soFar.contains(b)) {
                Optional<Volume> intersection = volume
                        .intersection(b.signalRangeVolume());
                if (intersection.isPresent()) {
                    HashSet<Nanobot> newSoFar = new HashSet<>(soFar);
                    newSoFar.add(b);

                    HashSet<Nanobot> candidate = mostIntersections(newSoFar,
                            intersection.get(),
                            bots.subList(i + 1, bots.size()));
                    if (candidate.size() > bestCandidate.size())
                        bestCandidate = candidate;
                    System.out.println(
                            newSoFar.size() + ":" + bestCandidate.size() + ":"
                                    + volume.cubicVolume());
                }
            }
        }
        bestCandidate.addAll(soFar);
        return bestCandidate;
    }

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("data", "day23.txt"));
        List<Nanobot> bots = lines.stream().map(Nanobot::parse)
                .collect(toList());

        Nanobot strongestSignal = bots.stream().max(
                (b1, b2) -> Integer.compare(b1.signalRadius, b2.signalRadius))
                .get();
        long numInRange = bots.stream().filter(strongestSignal::inRange)
                .count();
        System.out.println("Part one: " + numInRange);

        int n = 5;
        ArrayList<Nanobot> mostConnectedBots = bots.stream()
                .sorted((b1, b2) -> Long.compare(botsInRange(bots, b1),
                        botsInRange(bots, b2)))
                .skip(bots.size() - n)
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(mostConnectedBots);
        for (var b : mostConnectedBots)
            System.out.println(botsInRange(bots, b) + ":" + b);
        System.out.println(
                mostIntersections(Sets.newHashSet(mostConnectedBots.get(0)),
                        mostConnectedBots.get(0).signalRangeVolume(), bots));
    }

    private static long botsInRange(List<Nanobot> bots, Nanobot b) {
        long botsInRange = bots.stream()
                .filter(b2 -> b.signalRangeVolume()
                        .intersection(b2.signalRangeVolume()).isPresent())
                .count();
        return botsInRange;
    }
}
