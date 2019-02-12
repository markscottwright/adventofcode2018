package adventofcode2018;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day23 {

    /**
     * A 3D volume and all of the Nanobots that can reach it
     * 
     * @author mwright
     *
     */
    static public class VolumeAndConnections
            implements Comparable<VolumeAndConnections> {
        @Override
        public String toString() {
            return "VolumeAndNumConnections [volume=" + volume
                    + ", numConnectedBots=" + connectedBots.size() + "]";
        }

        final Volume volume;
        final List<Nanobot> connectedBots;
        final static Position ORIGIN = new Position(0, 0, 0);

        public VolumeAndConnections(Volume volume, List<Nanobot> bots) {
            this.volume = volume;
            this.connectedBots = bots;
        }

        public Volume getVolume() {
            return volume;
        }

        @Override
        public int compareTo(VolumeAndConnections o) {
            int compare = Integer.compare(connectedBots.size(),
                    o.connectedBots.size());
            if (compare == 0)
                return -Integer.compare(volume.distanceFrom(ORIGIN),
                        o.volume.distanceFrom(ORIGIN));
            else
                return -compare;
        }

        public boolean isBetter(VolumeAndConnections o) {
            return compareTo(o) < 0;
        }

        public int distanceFromOrigin() {
            return ORIGIN.distance(this.volume.minCorner);
        }

    }

    /**
     * A 3d area of space. Measured in "blocks".
     * 
     * @author mwright
     *
     */
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

    /**
     * A 3D position in space, measuring distances in "manhattan" measurements
     * 
     * @author mwright
     *
     */
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

    /** Essentially, a position and a signal radius */
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

        VolumeAndConnections solution = findMostConnectedSpaceNearestOrigin(
                bots);
        System.out.println("Part two: " + solution.distanceFromOrigin());
    }

    static VolumeAndConnections findMostConnectedSpaceNearestOrigin(
            List<Nanobot> bots) {
        Volume containingVolume = Nanobot.containingVolume(bots);
        PriorityQueue<VolumeAndConnections> considering = new PriorityQueue<>();

        VolumeAndConnections solution = null;
        considering.add(new VolumeAndConnections(containingVolume, bots));

        while (considering.size() > 0) {
            VolumeAndConnections next = considering.remove();
            if (solution != null && solution.isBetter(next)) {
                continue;
            }

            Volume[] quadrants = next.getVolume().quadrants();
            for (Volume quadrant : quadrants) {
                if (quadrant.isPoint()) {
                    // use distance from point here
                    List<Nanobot> botsInRange = bots.stream()
                            .filter(b -> b.inRange(quadrant.minCorner))
                            .collect(Collectors.toList());
                    VolumeAndConnections maybeNext = new VolumeAndConnections(
                            quadrant, botsInRange);
                    if (solution == null || maybeNext.isBetter(solution)) {
                        solution = maybeNext;
                    }
                } else {
                    List<Nanobot> botsInRange = bots.stream().filter(b -> {
                        return quadrant.distanceFrom(b) <= b.signalRadius;
                    }).collect(Collectors.toList());
                    VolumeAndConnections maybeNext = new VolumeAndConnections(
                            quadrant, botsInRange);
                    if (solution == null) {
                        considering.add(maybeNext);
                    } else if (!solution.isBetter(maybeNext)) {
                        considering.add(maybeNext);
                    }
                }
            }
        }
        return solution;
    }

}
