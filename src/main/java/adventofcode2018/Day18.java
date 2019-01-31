package adventofcode2018;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day18 {

    public static Character[][] parse(List<String> lines) {
        ArrayList<Character[]> lumberArea = new ArrayList<>();
        for (String line : lines) {
            // this nightmare is one way to convert a string to an array of
            // Character
            lumberArea.add(line.trim().chars().mapToObj(c -> (char) c)
                    .toArray(Character[]::new));
        }
        return lumberArea.toArray(new Character[0][]);
    }

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("data", "day18.txt"));
        Character[][] lumberArea = parse(lines);
        for (int i = 0; i < 10; ++i)
            lumberArea = evolveLumberArea(lumberArea);
        long numLumberYards = Arrays.stream(lumberArea)
                .flatMap(e -> Arrays.stream(e)).filter(c -> c == '#').count();
        long numTrees = Arrays.stream(lumberArea).flatMap(e -> Arrays.stream(e))
                .filter(c -> c == '|').count();

        System.out.println(String.format("Part one: %d * %d = %d", numTrees,
                numLumberYards, numTrees * numLumberYards));

        lumberArea = parse(lines);
        for (int i = 0; i < 2000; ++i) {

            numLumberYards = Arrays.stream(lumberArea)
                    .flatMap(e -> Arrays.stream(e)).filter(c -> c == '#')
                    .count();
            numTrees = Arrays.stream(lumberArea).flatMap(e -> Arrays.stream(e))
                    .filter(c -> c == '|').count();

            // 28 was empirically determined to be the cycle
            if (i > 500 && (i % 28L == 1000000000L % 28)) {
                System.out.println(String.format("Part two: %d",
                        numTrees * numLumberYards));
                break;
            }
            lumberArea = evolveLumberArea(lumberArea);
        }
    }

    public static Character[][] evolveLumberArea(Character[][] lumberArea) {
        Character[][] nextState = new Character[lumberArea.length][lumberArea[0].length];
        for (int y = 0; y < lumberArea.length; ++y) {
            for (int x = 0; x < lumberArea[0].length; ++x) {
                Character[] adjacentSquares = getAdjacent(lumberArea, x, y);
                nextState[y][x] = applyRules(lumberArea[y][x], adjacentSquares);
            }
        }
        return nextState;
    }

    private static Character applyRules(Character acre,
            Character[] adjacentSquares) {
        if (acre == '.') {
            if (numAdjacent('|', adjacentSquares) >= 3) {
                return '|';
            } else {
                return '.';
            }
        } else if (acre == '|') {
            if (numAdjacent('#', adjacentSquares) >= 3)
                return '#';
            else
                return '|';
        } else {
            // '#'
            if (numAdjacent('#', adjacentSquares) >= 1
                    && numAdjacent('|', adjacentSquares) >= 1)
                return '#';
            else
                return '.';
        }
    }

    private static long numAdjacent(Character acreType,
            Character[] adjacentSquares) {
        return Arrays.stream(adjacentSquares).filter(c -> c == acreType)
                .count();
    }

    private static Character[] getAdjacent(Character[][] lumberArea, int x,
            int y) {
        // using <= below, so max's are one less
        int maxY = lumberArea.length - 1;
        int maxX = lumberArea[0].length - 1;

        ArrayList<Character> s = new ArrayList<>();
        for (int yPos = Integer.max(0, y - 1); yPos <= Integer.min(maxY,
                y + 1); ++yPos) {
            for (int xPos = Integer.max(0, x - 1); xPos <= Integer.min(maxX,
                    x + 1); ++xPos) {
                if (xPos != x || yPos != y)
                    s.add(lumberArea[yPos][xPos]);
            }
        }
        // System.out.println(x + "," + y + " = " + lumberArea[y][x] + " -> " +
        // s);
        return s.toArray(new Character[0]);
    }

    public static String printLumberArea(Character[][] nextState) {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(s);
        for (int y = 0; y < nextState.length; ++y) {
            for (int x = 0; x < nextState[0].length; ++x) {
                out.write(nextState[y][x]);
            }
            out.println();
        }
        return new String(s.toByteArray()).replace("\r", "");
    }
}
