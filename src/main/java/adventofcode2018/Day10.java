package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

public class Day10 {

    private static final int MIN_POINTS_IN_LINE = 8;

    public static class PointAndVelocity {
        int x;
        int y;
        final int xVelocity;
        final int yVelocity;

        public PointAndVelocity(int x, int y, int xVelocity, int yVelocity) {
            this.x = x;
            this.y = y;
            this.xVelocity = xVelocity;
            this.yVelocity = yVelocity;
        }

        void move() {
            x = x + xVelocity;
            y = y + yVelocity;
        }

        final static Pattern LIGHT_PATTERN = Pattern.compile(
                "position=<\\s*([-0-9]+),\\s*([-0-9]+)> velocity=<\\s*([-0-9]+),\\s*([-0-9]+)>");

        public static PointAndVelocity parse(String line) {
            Matcher matcher = LIGHT_PATTERN.matcher(line);
            matcher.matches();
            return new PointAndVelocity(Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4)));
        }
    };

    public static void main(String[] args) throws IOException {
        List<PointAndVelocity> points = Files
                .lines(Paths.get("data", "day10.txt"))
                .map(PointAndVelocity::parse).collect(Collectors.toList());

        findMessage(points, 20000);
    }

    static boolean findMessage(List<PointAndVelocity> points, int maxSeconds) {
        boolean found = false;
        for (int i = 0; i < maxSeconds; ++i) {
            if (wordsFound(points)) {
                System.out.println("At second " + i);
                printPoints(points);
                found = true;
            }
            points.stream().forEach(PointAndVelocity::move);
        }
        return found;
    }

    private static void printPoints(List<PointAndVelocity> points) {
        int minX = points.stream().map(p -> p.x).min(Integer::compare).get();
        int maxX = points.stream().map(p -> p.x).max(Integer::compare).get();
        int minY = points.stream().map(p -> p.y).min(Integer::compare).get();
        int maxY = points.stream().map(p -> p.y).max(Integer::compare).get();

        for (int j = minY; j <= maxY; ++j) {
            for (int i = minX; i <= maxX; ++i) {
                final int finalI = i;
                final int finalJ = j;
                if (points.stream()
                        .anyMatch(p -> finalI == p.x && finalJ == p.y))
                    System.out.print("#");
                else
                    System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Are there any connected vertical lines in points?
     */
    private static boolean wordsFound(List<PointAndVelocity> points) {
        // fill out x to y mapping
        Multimap<Integer, Integer> pointXAndY = MultimapBuilder.hashKeys()
                .treeSetValues().build();
        for (var point : points)
            pointXAndY.put(point.x, point.y);
        
        // check for vertical lines
        for (Integer x : pointXAndY.keySet()) {
            var ys = pointXAndY.get(x);
            if (runExists(ys, MIN_POINTS_IN_LINE))
                return true;
        }
        return false;
    }

    /**
     * Is there count integers in a row in ys?
     * 
     * @param ys    ordered set of integers
     * @param count looked for count of incrementing number
     * @return true if count numbers in a row exist
     */
    public static boolean runExists(Collection<Integer> ys, int count) {
        if (ys.size() < count)
            return false;
        for (Integer y : ys) {
            boolean runExists = IntStream.range(y + 1, y + count)
                    .allMatch(i -> ys.contains(i));
            if (runExists)
                return true;
        }
        return false;
    }

}
