package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;

public class Day6 {

    static class Area {
        @Override
        public String toString() {
            return "Area [xMin=" + xMin + ", yMin=" + yMin + ", xMax=" + xMax
                    + ", yMax=" + yMax + "]";
        }

        int xMin;
        int yMin;
        int xMax;
        int yMax;
    }

    static class Coordinate {
        final int x;
        final int y;

        @Override
        public String toString() {
            return "Coordinate [x=" + x + ", y=" + y + "]";
        }

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        int manhattanDistance(int x, int y) {
            return Math.abs(this.x - x) + Math.abs(this.y - y);
        }

        static Coordinate parse(String line) {
            var xAndY = Splitter.on(",").trimResults().split(line).iterator();
            return new Coordinate(Integer.parseInt(xAndY.next()),
                    Integer.parseInt(xAndY.next()));
        }

        static Coordinate closest(List<Coordinate> coordinates, int x, int y) {
            return coordinates.stream()
                    .min((c1, c2) -> Integer.compare(c1.manhattanDistance(x, y),
                            c2.manhattanDistance(x, y)))
                    .get();
        }

        static Set<Coordinate> allClosest(List<Coordinate> coordinates, int x,
                int y) {
            int minDist = coordinates.stream()
                    .map(c -> c.manhattanDistance(x, y)).min(Integer::compare)
                    .get();
            return coordinates.stream()
                    .filter(c -> c.manhattanDistance(x, y) == minDist)
                    .collect(Collectors.toSet());
        }

        static Area enclosingArea(List<Coordinate> cs) {
            Area a = new Area();
            a.xMin = cs.stream().map(c -> c.x).min(Integer::compare).get();
            a.yMin = cs.stream().map(c -> c.y).min(Integer::compare).get();
            a.xMax = cs.stream().map(c -> c.x).max(Integer::compare).get();
            a.yMax = cs.stream().map(c -> c.y).max(Integer::compare).get();
            return a;
        }
    }

    public static void main(String[] args) throws IOException {
        List<Coordinate> coordinates = Files
                .lines(Paths.get("data", "day6.txt")).map(Coordinate::parse)
                .collect(Collectors.toList());
        Area coordinateArea = Coordinate.enclosingArea(coordinates);

        HashMap<Coordinate, Integer> timesClosest = new HashMap<>();
        for (int x = coordinateArea.xMin; x <= coordinateArea.xMax; ++x) {
            for (int y = coordinateArea.yMin; y <= coordinateArea.yMax; ++y) {
                Set<Coordinate> closestCoordinates = Coordinate
                        .allClosest(coordinates, x, y);

                // if more than one is equally close, don't count
                if (closestCoordinates.size() == 1) {
                    Coordinate closestCoordinate = closestCoordinates.iterator()
                            .next();
                    timesClosest.put(closestCoordinate,
                            timesClosest.getOrDefault(closestCoordinate, 0)
                                    + 1);
                }
            }
        }

        System.out.println(timesClosest);
        Integer maxArea = timesClosest.values().stream().max(Integer::compare)
                .get();
        System.out.println(maxArea);
    }
}
