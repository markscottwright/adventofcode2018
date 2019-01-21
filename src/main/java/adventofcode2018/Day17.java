package adventofcode2018;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import adventofcode2018.Day17.SeepResults;

public class Day17 {

    public enum SeepResults {
        FREE

        , BLOCKED
    }

    public static class Point {
        final int x;
        final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
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
            Point other = (Point) obj;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "Point [x=" + x + ", y=" + y + "]";
        }

        public Point below() {
            return new Point(x, y + 1);
        }

        public Point left() {
            return new Point(x - 1, y);
        }

        public Point right() {
            return new Point(x + 1, y);
        }

        public Point above() {
            assert y >= 0;
            return new Point(x, y - 1);
        }
    }

    public static class Ground {
        HashSet<Point> clay = new HashSet<>();
        final int maxY;
        final int minX;
        final int maxX;
        private Point spring = new Point(500, 0);
        private HashSet<Point> fallingWater = new HashSet<>();
        private HashSet<Point> restingWater = new HashSet<>();
        private Integer minY;

        public Ground(HashSet<Point> clay) {
            this.clay = clay;
            this.maxY = clay.stream().map(p -> p.y).max(Integer::compareTo)
                    .get();
            this.minY = clay.stream().map(p -> p.y).min(Integer::compareTo)
                    .get();
            this.minX = Integer.min(spring.x,
                    clay.stream().map(p -> p.x).min(Integer::compareTo).get())
                    - 1;
            this.maxX = Integer.max(spring.x,
                    clay.stream().map(p -> p.x).max(Integer::compareTo).get())
                    + 1;
        }

        static Ground parseScan(List<String> lines) {
            HashSet<Point> clay = new HashSet<>();
            for (String line : lines) {
                Pattern pattern = Pattern
                        .compile(".=([0-9]+), .=([0-9]+)\\.\\.([0-9]+)");
                Matcher matcher = pattern.matcher(line);
                if (!matcher.matches())
                    throw new RuntimeException(line);
                if (line.startsWith("x")) {
                    int x = Integer.valueOf(matcher.group(1));
                    int minY = Integer.valueOf(matcher.group(2));
                    int maxY = Integer.valueOf(matcher.group(3));
                    IntStream.rangeClosed(minY, maxY)
                            .mapToObj(y -> new Point(x, y)).forEach(clay::add);
                } else {
                    int y = Integer.valueOf(matcher.group(1));
                    int minX = Integer.valueOf(matcher.group(2));
                    int maxX = Integer.valueOf(matcher.group(3));
                    IntStream.rangeClosed(minX, maxX)
                            .mapToObj(x -> new Point(x, y)).forEach(clay::add);
                }
            }

            return new Ground(clay);
        }

        public void print(PrintStream out) {
            System.out.println(clay);
            for (int y = 0; y <= maxY; ++y) {
                for (int x = minX; x <= maxX; ++x) {
                    if (clay.contains(new Point(x, y))) {
                        out.print("#");
                    } else if (fallingWater.contains(new Point(x, y))) {
                        out.print("|");
                    } else if (restingWater.contains(new Point(x, y))) {
                        out.print("~");
                    } else {
                        out.print(".");
                    }
                }
                out.println();
            }

        }

        public void seep() {
            restingWater.clear();
            fallingWater.clear();
            seepFrom(spring);
        }

        public SeepResults seepFrom(Point point) {
            if (!inRange(point))
                return SeepResults.FREE;
            else if (clay.contains(point))
                return SeepResults.BLOCKED;
            else if (restingWater.contains(point))
                return SeepResults.BLOCKED;
            else if (fallingWater.contains(point))
                return SeepResults.FREE;

            if (seepFrom(point.below()) == SeepResults.FREE) {
                fallingWater.add(point);
                return SeepResults.FREE;
            } else {
                HashSet<Point> pointsRight = new HashSet<>();
                HashSet<Point> pointsLeft = new HashSet<>();
                var resultsRight = seepRight(point.right(), pointsRight);
                var resultsLeft = seepLeft(point.left(), pointsLeft);

                if (resultsRight == SeepResults.BLOCKED
                        && resultsLeft == SeepResults.BLOCKED) {
                    restingWater.addAll(pointsRight);
                    restingWater.addAll(pointsLeft);
                    restingWater.add(point);
                    return SeepResults.BLOCKED;
                } else {
                    fallingWater.addAll(pointsRight);
                    fallingWater.addAll(pointsLeft);
                    fallingWater.add(point);
                    return SeepResults.FREE;
                }
            }

        }

        private SeepResults seepRight(Point point,
                HashSet<Point> pointsVisited) {
            if (clay.contains(point))
                return SeepResults.BLOCKED;
            pointsVisited.add(point);
            if (seepFrom(point.below()) == SeepResults.BLOCKED
                    && seepRight(point.right(),
                            pointsVisited) == SeepResults.BLOCKED) {
                return SeepResults.BLOCKED;
            } else {
                return SeepResults.FREE;
            }
        }

        private SeepResults seepLeft(Point point,
                HashSet<Point> pointsVisited) {
            if (clay.contains(point))
                return SeepResults.BLOCKED;
            pointsVisited.add(point);
            if (seepFrom(point.below()) == SeepResults.BLOCKED
                    && seepLeft(point.left(),
                            pointsVisited) == SeepResults.BLOCKED) {
                return SeepResults.BLOCKED;
            } else {
                return SeepResults.FREE;
            }
        }

        private boolean inRange(Point point) {
            return point.x >= minX && point.x <= maxX && point.y >= 0
                    && point.y <= maxY;
        }

        public long numWaterPointsInScanRange() {
            return fallingWater.stream().filter(p -> p.y >= minY && p.y <= maxY)
                    .count()
                    + restingWater.stream()
                            .filter(p -> p.y >= minY && p.y <= maxY).count();
        }

        public long numRestingWaterPoints() {
            return restingWater.size();
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("data", "day17.txt"));
        Ground ground = Ground.parseScan(lines);
        ground.seep();
        System.out.println("Part one: " + ground.numWaterPointsInScanRange());
        System.out.println("Part two: " + ground.numRestingWaterPoints());
    }
}
