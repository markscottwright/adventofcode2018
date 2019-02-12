package adventofcode2018;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import adventofcode2018.Day23.Nanobot;
import adventofcode2018.Day23.Volume;
import adventofcode2018.Day23.VolumeAndNumConnections;

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

    static public class VolumeAndNumConnections
            implements Comparable<VolumeAndNumConnections> {
        final Volume volume;
        final List<Nanobot> connectedBots;
        final static Position ORIGIN = new Position(0, 0, 0);

        public VolumeAndNumConnections(Volume volume, List<Nanobot> bots) {
            this.volume = volume;
            this.connectedBots = bots;
        }

        public Volume getVolume() {
            return volume;
        }

        @Override
        public int compareTo(VolumeAndNumConnections o) {
            int compare = Integer.compare(connectedBots.size(),
                    o.connectedBots.size());
            if (compare == 0)
                return Integer.compare(volume.distanceFrom(ORIGIN),
                        o.volume.distanceFrom(ORIGIN));
            else
                return compare;
        }

        public boolean isBetter(VolumeAndNumConnections o) {
            return compareTo(o) < 0;
        }

    }

    public static class Volume {
        final Position minCorner;

        // these are always at least 1
        final int xSide;
        final int ySide;
        final int zSide;

        public Volume(Position center, int radius) {
            this.minCorner = new Position(center.x - radius, center.y - radius,
                    center.z - radius);
            // (+1 to include center point)
            this.xSide = this.ySide = this.zSide = radius * 2 + 1;
        }

        boolean isPoint() {
            return xSide == 1 && ySide == 1 && zSide == 1;
        }

        Volume(Position minCorner, int xSide, int ySide, int zSide) {
            assert xSide > 0;
            assert ySide > 0;
            assert zSide > 0;
            this.minCorner = minCorner;
            this.xSide = xSide;
            this.ySide = ySide;
            this.zSide = zSide;
        }

        /** distance from the volume. 0 if within the volume */
        int distanceFrom(Position p) {
            int minX = minCorner.x;
            int maxX = minCorner.x + xSide - 1;
            int minY = minCorner.y;
            int maxY = minCorner.y + ySide - 1;
            int minZ = minCorner.z;
            int maxZ = minCorner.z + zSide - 1;

            int distance = 0;
            if (p.x < minX)
                distance += minX - p.x;
            else if (p.x > maxX)
                distance += p.x - maxX;
            if (p.y < minY)
                distance += minY - p.y;
            else if (p.y > maxY)
                distance += p.y - maxY;
            if (p.z < minZ)
                distance += minZ - p.z;
            else if (p.z > maxZ)
                distance += p.z - maxZ;
            return distance;
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
            int xSide1 = xSide / 2 + xSide % 2;
            int ySide1 = ySide / 2 + ySide % 2;
            int zSide1 = zSide / 2 + zSide % 2;
            //@formatter:off
            quadrants.add(new Volume(minCorner, xSide1, ySide1, zSide1));
            if (xSide > 1)
                quadrants.add(new Volume(new Position(minCorner.x+xSide1, minCorner.y, minCorner.z), xSide/2, ySide1, zSide1));
            if (ySide > 1)
                quadrants.add(new Volume(new Position(minCorner.x, minCorner.y+ySide1, minCorner.z), xSide1, ySide/2, zSide1));
            if (zSide > 1)
                quadrants.add(new Volume(new Position(minCorner.x, minCorner.y, minCorner.z+zSide1), xSide1, ySide1, zSide/2));
            if (xSide > 1 && ySide > 1)
                quadrants.add(new Volume(new Position(minCorner.x+xSide1, minCorner.y+ySide1, minCorner.z), xSide/2, ySide/2, zSide1));
            if (ySide > 1 && zSide > 1)
                quadrants.add(new Volume(new Position(minCorner.x, minCorner.y+ySide1, minCorner.z+zSide1), xSide1, ySide/2, zSide/2));
            if (xSide > 1 && zSide > 1)
                quadrants.add(new Volume(new Position(minCorner.x+xSide1, minCorner.y, minCorner.z+zSide1), xSide/2, ySide1, zSide/2));
            if (xSide > 1 && ySide > 1 && zSide > 1)
                quadrants.add(new Volume(new Position(minCorner.x+xSide1, minCorner.y+ySide1, minCorner.z+zSide1), xSide/2, ySide/2, zSide/2));
            //@formatter:on
            return quadrants.toArray(new Volume[0]);
        }

        /**
         * Points are inclusive here.
         */
        public static int[] intersection(int[] a, int[] b) {
            assert a[0] <= a[1];
            assert b[0] <= b[1];
            if (a[1] < b[0] || b[1] < a[0])
                return null;
            else {
                int[] intersection = new int[] { Integer.max(a[0], b[0]),
                        Integer.min(a[1], b[1]) };
                assert intersection[0] <= intersection[1];
                assert intersection[1] - intersection[0] <= a[1] - a[0];
                assert intersection[1] - intersection[0] <= b[1] - b[0];
                return intersection;
            }
        }

        Optional<Volume> intersection(Volume c) {

            // sides are "inclusive". A point as a side of 1, so subtract 1 from
            // side to get the point value of a point one side away
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
                // again, inclusive points, so sides need to have one added
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
            int distance = Math.abs(x - o.x) + Math.abs(y - o.y)
                    + Math.abs(z - o.z);
            return distance;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            result = prime * result + z;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Position other = (Position) obj;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            if (z != other.z)
                return false;
            return true;
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

        Volume totalVolume = Nanobot.containingVolume(bots);
        BestPositionFinder finder = new BestPositionFinder();
        finder.find2(totalVolume, bots);
        System.out.println(finder.getMostBotsInRange());
    }

    static class BestPositionFinder {
        private static final Position ORIGIN = new Position(0, 0, 0);
        private int mostBotsInRange = 0;
        private ArrayList<Position> mostConnectedPositions = new ArrayList<>();
        private Position mostConnectedPosition = null;

        void find3(List<Nanobot> bots) {
            Volume containingVolume = Nanobot.containingVolume(bots);
            PriorityQueue<VolumeAndNumConnections> considering = new PriorityQueue<>();

            VolumeAndNumConnections solution = null;
            considering
                    .add(new VolumeAndNumConnections(containingVolume, bots));

            while (considering.size() > 0) {
                VolumeAndNumConnections next = considering.remove();
                if (solution.isBetter(next))
                    continue;

                Volume[] quadrants = next.getVolume().quadrants();
                for (Volume quadrant : quadrants) {
                    if (quadrant.isPoint()) {
                        List<Nanobot> botsInRange = botsInRange(bots,
                                quadrant.minCorner);
                        VolumeAndNumConnections maybeNext = new VolumeAndNumConnections(
                                quadrant, botsInRange);
                        if (solution == null || maybeNext .isBetter(solution)) {
                            solution = maybeNext;
                        }
                    }
                }
            }
        }

        void find2(Volume vol, List<Nanobot> bots) {

            Volume[] quadrants = vol.quadrants();
            ArrayList<Pair<Volume, List<Nanobot>>> botsInRangeOfQuadrant = new ArrayList<>();
            for (Volume quadrant : quadrants) {
                if (quadrant.isPoint()) {
                    // use the real value instead of an estimate
                    botsInRangeOfQuadrant.add(Pair.with(quadrant,
                            botsInRange(bots, quadrant.minCorner)));
                } else {
                    botsInRangeOfQuadrant.add(Pair.with(quadrant,
                            bots.stream()
                                    .filter(b -> quadrant
                                            .distanceFrom(b) <= b.signalRadius)
                                    .collect(Collectors.toList())));
                }
            }

            // sort from most predicted quadrant
            botsInRangeOfQuadrant.sort((b1, b2) -> -Integer
                    .compare(b1.getValue1().size(), b2.getValue1().size()));

            // no quadrant has more than our best solution so far
            if (botsInRangeOfQuadrant.size() < 1 || botsInRangeOfQuadrant.get(0)
                    .getValue1().size() < mostBotsInRange)
                return;

            for (var volumeAndBots : botsInRangeOfQuadrant) {
                // if a volume isn't any better that a found solution, and its
                // further from the origin than a solution we know, ignore it
                if (mostConnectedPosition != null
                        && volumeAndBots.getValue1()
                                .size() == this.mostBotsInRange
                        && volumeAndBots.getValue0()
                                .distanceFrom(ORIGIN) > ORIGIN
                                        .distance(mostConnectedPosition))
                    continue;

                // done as best we can, drop out
                if (volumeAndBots.getValue1().size() == 0
                        || volumeAndBots.getValue1().size() < mostBotsInRange) {
                    return;
                }

                // found another equally good point
                else if (volumeAndBots.getValue0().isPoint() && volumeAndBots
                        .getValue1().size() == mostBotsInRange) {
                    if (volumeAndBots.getValue0().minCorner.distance(
                            ORIGIN) < mostConnectedPosition.distance(ORIGIN))
                        mostConnectedPosition = volumeAndBots
                                .getValue0().minCorner;
                }

                // even better solution
                else if (volumeAndBots.getValue0().isPoint()
                        && volumeAndBots.getValue1().size() > mostBotsInRange) {
                    mostBotsInRange = volumeAndBots.getValue1().size();
                    mostConnectedPosition = volumeAndBots.getValue0().minCorner;
                    System.out.println(this.mostBotsInRange + ":"
                            + this.mostConnectedPosition + ":" + vol);
                }

                // potentially better solution, but need to drill down
                else {
                    assert !volumeAndBots.getValue0().isPoint();
                    assert volumeAndBots.getValue1().size() >= mostBotsInRange;

                    find2(volumeAndBots.getValue0(), volumeAndBots.getValue1());
                }
            }
        }

        void find(Volume vol, List<Nanobot> bots) {

            Volume[] quadrants = vol.quadrants();
            ArrayList<Pair<Volume, List<Nanobot>>> botsInRangeOfQuadrant = new ArrayList<>();
            for (Volume quadrant : quadrants) {
                if (quadrant.isPoint()) {
                    // use the real value instead of an estimate
                    botsInRangeOfQuadrant.add(Pair.with(quadrant,
                            botsInRange(bots, quadrant.minCorner)));
                } else {
                    botsInRangeOfQuadrant.add(
                            Pair.with(quadrant, botsInRange(bots, quadrant)));
                }
            }

            // sort from most predicted quadrant
            botsInRangeOfQuadrant.sort((b1, b2) -> -Integer
                    .compare(b1.getValue1().size(), b2.getValue1().size()));

            // no quadrant has more than our best solution so far
            if (botsInRangeOfQuadrant.size() < 1 || botsInRangeOfQuadrant.get(0)
                    .getValue1().size() < mostBotsInRange)
                return;

            for (var volumeAndBots : botsInRangeOfQuadrant) {
                // done as best we can, drop out
                if (volumeAndBots.getValue1().size() == 0
                        || volumeAndBots.getValue1().size() < mostBotsInRange) {
                    return;
                }

                // found another equally good point
                else if (volumeAndBots.getValue0().isPoint() && volumeAndBots
                        .getValue1().size() == mostBotsInRange) {
                    mostConnectedPositions
                            .add(volumeAndBots.getValue0().minCorner);
                }

                // even better solution
                else if (volumeAndBots.getValue0().isPoint()
                        && volumeAndBots.getValue1().size() > mostBotsInRange) {
                    mostBotsInRange = volumeAndBots.getValue1().size();
                    mostConnectedPositions = new ArrayList<>();
                    mostConnectedPositions
                            .add(volumeAndBots.getValue0().minCorner);
                }

                // potentially better solution, but need to drill down
                else {
                    assert !volumeAndBots.getValue0().isPoint();
                    assert volumeAndBots.getValue1().size() >= mostBotsInRange;

                    find(volumeAndBots.getValue0(), volumeAndBots.getValue1());
                }
            }
        }

        public int getMostBotsInRange() {
            return mostBotsInRange;
        }

        public ArrayList<Position> getMostConnectedPositions() {
            return mostConnectedPositions;
        }

    }

    static Pair<Integer, ArrayList<Position>> findMostConnectedPositions(
            Volume totalVolume, List<Nanobot> bots) {
        Volume[] quadrants = totalVolume.quadrants();
        ArrayList<Pair<Volume, List<Nanobot>>> botsInRangeOfQuadrant = new ArrayList<>();
        for (Volume quadrant : quadrants) {
            if (quadrant.isPoint()) {
                // use the real value instead of an estimate
                botsInRangeOfQuadrant.add(Pair.with(quadrant,
                        botsInRange(bots, quadrant.minCorner)));
            } else {
                botsInRangeOfQuadrant
                        .add(Pair.with(quadrant, botsInRange(bots, quadrant)));
            }
        }

        // sort from most predicted quadrant
        botsInRangeOfQuadrant.sort((b1, b2) -> -Integer
                .compare(b1.getValue1().size(), b2.getValue1().size()));

        ArrayList<Position> solution = new ArrayList<>();
        int numConnectionsInBestSolution = 0;
        for (int i = 0; i < botsInRangeOfQuadrant.size(); ++i) {
            Pair<Volume, List<Nanobot>> volumeAndBots = botsInRangeOfQuadrant
                    .get(i);
            // done as best we can, drop out
            if (volumeAndBots.getValue1().size() == 0 || volumeAndBots
                    .getValue1().size() < numConnectionsInBestSolution) {
                return Pair.with(numConnectionsInBestSolution, solution);
            }

            // found another equally good point
            else if (volumeAndBots.getValue0().isPoint() && volumeAndBots
                    .getValue1().size() == numConnectionsInBestSolution) {
                solution.add(volumeAndBots.getValue0().minCorner);
            }

            // even better solution
            else if (volumeAndBots.getValue0().isPoint() && volumeAndBots
                    .getValue1().size() > numConnectionsInBestSolution) {
                numConnectionsInBestSolution = volumeAndBots.getValue1().size();
                solution = new ArrayList<>();
                solution.add(volumeAndBots.getValue0().minCorner);
            }

            // potentially better solution, but need to drill down
            else {
                assert !volumeAndBots.getValue0().isPoint();
                assert volumeAndBots.getValue1()
                        .size() >= numConnectionsInBestSolution;

                Pair<Integer, ArrayList<Position>> potentialSolution = findMostConnectedPositions(
                        volumeAndBots.getValue0(), volumeAndBots.getValue1());

                // found a better solution
                if (potentialSolution
                        .getValue0() > numConnectionsInBestSolution) {
                    numConnectionsInBestSolution = potentialSolution
                            .getValue0();
                    solution = potentialSolution.getValue1();
                }

                // found more points equally good
                else if (potentialSolution
                        .getValue0() == numConnectionsInBestSolution) {
                    solution.addAll(potentialSolution.getValue1());
                }
            }
        }

        return Pair.with(numConnectionsInBestSolution, solution);
    }

    private static List<Nanobot> botsInRange(List<Nanobot> bots, Position pos) {
        List<Nanobot> botsInRangeOfPoint = bots.stream()
                .filter(b -> b.inRange(pos)).collect(Collectors.toList());
        return botsInRangeOfPoint;
    }

    static List<Nanobot> botsInRange(List<Nanobot> bots,
            Volume signalRangeVolume) {
        return bots.stream().filter(b2 -> {
            boolean present = signalRangeVolume
                    .intersection(b2.signalRangeVolume()).isPresent();
            return present;
        }).collect(Collectors.toList());
    }
}
