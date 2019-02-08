package adventofcode2018;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
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
        final int xSide;
        final int ySide;
        final int zSide;

        public Volume(Position center, int radius) {
            this.minCorner = new Position(center.x - radius, center.y - radius,
                    center.z - radius);
            this.xSide = this.ySide = this.zSide = radius * 2 + 1; // (+1 to
                                                                   // include
                                                                   // center
                                                                   // point)
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

        public boolean contains(Volume v) {
            return (minCorner.x <= v.minCorner.x)
                    & (minCorner.y <= v.minCorner.y)
                    & (minCorner.z <= v.minCorner.z)
                    & (minCorner.x + xSide >= v.minCorner.x + v.xSide)
                    & (minCorner.y + ySide >= v.minCorner.y + v.ySide)
                    & (minCorner.z + zSide >= v.minCorner.z + v.zSide);
        }

        public Volume[] quadrants() {
            ArrayList<Volume> quadrants = new ArrayList<>();
            //@formatter:off
            quadrants.add(new Volume(minCorner, xSide/2, ySide/2, zSide/2));
            if (xSide > 0)
                quadrants.add(new Volume(new Position(minCorner.x+xSide/2+xSide%2, minCorner.y, minCorner.z), xSide/2, ySide/2, zSide/2));
            if (ySide > 0)
                quadrants.add(new Volume(new Position(minCorner.x, minCorner.y+ySide/2+ySide%2, minCorner.z), xSide/2, ySide/2, zSide/2));
            if (zSide > 0)
                quadrants.add(new Volume(new Position(minCorner.x, minCorner.y, minCorner.z+zSide/2+zSide%2), xSide/2, ySide/2, zSide/2));
            if (xSide > 0 && ySide > 0)
                quadrants.add(new Volume(new Position(minCorner.x+xSide/2+xSide%2, minCorner.y+ySide/2+ySide%2, minCorner.z), xSide/2, ySide/2, zSide/2));
            if (ySide > 0 && zSide > 0)
                quadrants.add(new Volume(new Position(minCorner.x, minCorner.y+ySide/2+ySide%2, minCorner.z+zSide/2+zSide%2), xSide/2, ySide/2, zSide/2));
            if (xSide > 0 && zSide > 0)
                quadrants.add(new Volume(new Position(minCorner.x+xSide/2+xSide%2, minCorner.y, minCorner.z+zSide/2+zSide%2), xSide/2, ySide/2, zSide/2));
            if (xSide > 0 && ySide > 0 && zSide > 0)
                quadrants.add(new Volume(new Position(minCorner.x+xSide/2+xSide%2, minCorner.y+ySide/2+ySide%2, minCorner.z+zSide/2+zSide%2), xSide/2, ySide/2, zSide/2));
            //@formatter:on
            return quadrants.toArray(new Volume[0]);
        }

        /**
         * Points are inclusive here.
         */
        public static int[] intersection(int[] a, int[] b) {
            assert a[0] <= a[1];
            assert b[0] <= b[1];
            if ((b[0] <= a[0] && a[0] <= b[1])
                    || (b[0] <= a[1] && a[1] <= b[1])) {
                int[] intersection = new int[] { Integer.max(a[0], b[0]),
                        Integer.min(a[1], b[1]) };
                assert intersection[0] <= intersection[1];
                assert intersection[1] - intersection[0] <= a[1] - a[0];
                assert intersection[1] - intersection[0] <= b[1] - b[0];
                return intersection;
            } else
                return null;
        }

        Optional<Volume> intersection(Volume c) {
            int[] xIntersection = intersection(
                    new int[] { minCorner.x, minCorner.x + xSide - 1 },
                    new int[] { c.minCorner.x, c.minCorner.x + c.xSide - 1 });
            int[] yIntersection = intersection(
                    new int[] { minCorner.y, minCorner.y + ySide - 1 },
                    new int[] { c.minCorner.y, c.minCorner.y + c.ySide - 1 });
            int[] zIntersection = intersection(
                    new int[] { minCorner.z, minCorner.z + zSide - 1 },
                    new int[] { c.minCorner.z, c.minCorner.z + c.zSide - 1 });
            if (xIntersection == null || yIntersection == null
                    || zIntersection == null) {
                return Optional.empty();
            } else {
                return Optional.of(new Volume(
                        new Position(xIntersection[0], yIntersection[0],
                                zIntersection[0]),
                        xIntersection[1] - xIntersection[0] + 1,
                        yIntersection[1] - yIntersection[0] + 1,
                        zIntersection[1] - zIntersection[0] + 1));
            }

        }

        BigInteger cubicVolume() {
            return BigInteger.valueOf(xSide).multiply(BigInteger.valueOf(ySide)
                    .multiply(BigInteger.valueOf(zSide)));
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

        public static Volume containingVolume(Collection<Nanobot> bots) {
            int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE,
                    maxZ = Integer.MIN_VALUE, minX = Integer.MAX_VALUE,
                    minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
            for (Nanobot b : bots) {
                maxX = Integer.max(maxX, b.x);
                maxY = Integer.max(maxY, b.y);
                maxZ = Integer.max(maxZ, b.z);
                minX = Integer.min(minX, b.x);
                minY = Integer.min(minY, b.y);
                minZ = Integer.min(minZ, b.z);
            }
            return new Volume(new Position(minX, minY, minZ), maxX - minX + 1,
                    maxY - minY + 1, maxZ - minZ + 1);
        }
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

        // remove any bots contained in another
        HashSet<Nanobot> toRemove = new HashSet<>();
        HashMap<Nanobot, Integer> botsContainedBy = new HashMap<>();
        for (Nanobot b1 : bots) {
            for (Nanobot b2 : bots) {
                if (!b1.equals(b2) && b1.signalRangeVolume()
                        .contains(b2.signalRangeVolume())) {
                    toRemove.add(b2);
                    Integer b1Count = botsContainedBy.get(b1);
                    if (b1Count == null)
                        botsContainedBy.put(b1, 1);
                    else
                        botsContainedBy.put(b1, b1Count + 1);
                }
            }
        }

        System.out.println("Bots contained in other bots:" + toRemove.size());
        System.out.println("Bot contained count:" + botsContainedBy);

        int n = 5;
        ArrayList<Nanobot> mostConnectedBots = bots.stream()
                .sorted((b1, b2) -> Long.compare(botsInRange(bots, b1),
                        botsInRange(bots, b2)))
                .skip(bots.size() - n)
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(mostConnectedBots);
        for (var b : mostConnectedBots)
            System.out.println(botsInRange(bots, b) + ":" + b);

        // HashSet<HashSet<Nanobot>> solutions = new HashSet<>();
        // for (int i = 0; i < mostConnectedBots.size(); ++i) {
        // System.out.println(i);
        // solutions.add(
        // mostIntersections(Sets.newHashSet(mostConnectedBots.get(0)),
        // mostConnectedBots.get(0).signalRangeVolume(),
        // bots.subList(i + 1, bots.size())));
        // }
    }

    private static long botsInRange(List<Nanobot> bots, Nanobot b) {
        long botsInRange = bots.stream()
                .filter(b2 -> b.signalRangeVolume()
                        .intersection(b2.signalRangeVolume()).isPresent())
                .count();
        return botsInRange;
    }
}
