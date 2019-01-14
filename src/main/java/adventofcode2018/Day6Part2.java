package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import adventofcode2018.Day6.Area;
import adventofcode2018.Day6.Coordinate;

public class Day6Part2 {
    public static void main(String[] args) throws IOException {
        List<Coordinate> coordinates = Files
                .lines(Paths.get("data", "day6.txt")).map(Coordinate::parse)
                .collect(Collectors.toList());

        int n = 10000;

        Area area = findMaximumArea(coordinates, n);

        int regionInRangeSize = 0;
        for (int x = area.xMin; x <= area.xMax; ++x) {
            for (int y = area.yMin; y <= area.yMax; ++y) {
                final int finalX = x;
                final int finalY = y;
                boolean isInRange = totalManhattanDistance(coordinates, finalX,
                        finalY) < n;
                if (isInRange)
                    regionInRangeSize++;
            }
        }
        System.out.println(regionInRangeSize);
    }

    private static Integer totalManhattanDistance(List<Coordinate> coordinates,
            final int finalX, final int finalY) {
        return coordinates.stream()
                .map(c -> c.manhattanDistance(finalX, finalY))
                .reduce((a, b) -> a + b).get();
    }

    /**
     * This was a much faster method than areaAround. Find a middle coordinate
     * and then move out until you find maximums
     * 
     * @param coordinates
     * @param n
     * @return
     */
    private static Area findMaximumArea(List<Coordinate> coordinates, int n) {
        var middleX = coordinates.stream()
                .collect(Collectors.averagingInt(c -> c.x)).intValue();
        var middleY = coordinates.stream()
                .collect(Collectors.averagingInt(c -> c.y)).intValue();

        int minX = middleX;
        while (totalManhattanDistance(coordinates, minX, middleY) < n)
            minX--;
        int maxX = middleX;
        while (totalManhattanDistance(coordinates, maxX, middleY) < n)
            maxX++;
        int minY = middleY;
        while (totalManhattanDistance(coordinates, middleX, minY) < n)
            minY--;
        int maxY = middleY;
        while (totalManhattanDistance(coordinates, middleX, maxY) < n)
            maxY++;

        var area = new Area();
        area.xMax = maxX;
        area.xMin = minX;
        area.yMax = maxY;
        area.yMin = minY;
        return area;

    }
}
