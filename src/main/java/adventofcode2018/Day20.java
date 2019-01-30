package adventofcode2018;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class Day20 {

    public static class PointPair {

        final int x1, y1, x2, y2;
        final char doorSymbol;

        public PointPair(Point p1, Point p2) {
            if (p1.x < p2.x || p1.y < p2.y) {
                x1 = p1.x;
                y1 = p1.y;
                x2 = p2.x;
                y2 = p2.y;
            } else {
                x1 = p2.x;
                y1 = p2.y;
                x2 = p1.x;
                y2 = p1.y;
            }
            if (p1.y == p2.y)
                doorSymbol = '-';
            else
                doorSymbol = '|';
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x1;
            result = prime * result + x2;
            result = prime * result + y1;
            result = prime * result + y2;
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
            PointPair other = (PointPair) obj;
            if (x1 != other.x1)
                return false;
            if (x2 != other.x2)
                return false;
            if (y1 != other.y1)
                return false;
            if (y2 != other.y2)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "PointPair [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2="
                    + y2 + "]";
        }

        public Point doorLocationRelativeTo(int minX, int minY) {
            if (y1 == y2) {
                int x = x1 - minX;
                int y = y1 - minY;
                return new Point(x * 2 + 1, y * 2);
            } else {
                int x = x1 - minX;
                int y = y1 - minY;
                return new Point(x * 2, y * 2 + 1);
            }
        }

    }

    public static class ParsingException extends Exception {

    }

    public static class Point {
        final int x;
        final int y;

        public Point(int x, int y) {
            super();
            this.x = x;
            this.y = y;
        }

        Point n() {
            return new Point(x, y - 1);
        }

        Point s() {
            return new Point(x, y + 1);
        }

        Point e() {
            return new Point(x + 1, y);
        }

        Point w() {
            return new Point(x - 1, y);
        }

        public Point walk(Character c) {
            if (c == 'N')
                return n();
            else if (c == 'E')
                return e();
            else if (c == 'S')
                return s();
            else
                return w();
        }

        @Override
        public String toString() {
            return "Point [x=" + x + ", y=" + y + "]";
        }
    }

    public static class Chain {

        static final Chain END = new Chain();
        static final Chain END_CHOICE = new Chain();
        static final Chain END_CHOICES = new Chain();

        String segment = null;
        ArrayList<Chain> choices = new ArrayList<>();
        Chain next = null;

        public Chain() {

        }

        public Chain(String segment, Chain next) {
            this.segment = segment;
            this.next = next;
        }

        public Chain(ArrayList<Chain> choices, Chain next) {
            this.choices = choices;
            this.next = next;
        }

        static Chain parse(String s) throws ParsingException {
            var tokenizer = new StringTokenizer(s, "()|^$", true);
            String firstToken = tokenizer.nextToken();
            if (!firstToken.equals("^"))
                throw new ParsingException();
            return parse(tokenizer);
        }

        static Chain parse(StringTokenizer tokenizer) throws ParsingException {
            String token = tokenizer.nextToken();
            if (!"()|$^".contains(token)) {
                return new Chain(token, parse(tokenizer));
            } else if (token.equals("(")) {
                return new Chain(parseChoices(tokenizer), parse(tokenizer));
            } else if (token.equals("$")) {
                return END;
            } else if (token.equals("|")) {
                return END_CHOICE;
            } else if (token.equals(")")) {
                return END_CHOICES;
            } else {
                throw new ParsingException();
            }
        }

        static private ArrayList<Chain> parseChoices(StringTokenizer tokenizer)
                throws ParsingException {
            ArrayList<Chain> choices = new ArrayList<>();
            while (tokenizer.hasMoreElements()) {
                String token = tokenizer.nextToken();

                // special case - empty choice "|)"
                if (token.equals(")")) {
                    choices.add(new Chain("", END_CHOICES));
                    return choices;
                }

                Chain tail = parse(tokenizer);
                choices.add(new Chain(token, tail));
                if (tail.isLastChoice())
                    return choices;
            }
            throw new ParsingException();
        }

        private boolean isLastChoice() {
            if (this.equals(END_CHOICES))
                return true;
            else if (next == null)
                return false;
            else
                return next.isLastChoice();
        }

        ArrayList<String> allStrings() {
            if (segment != null) {
                return next.allStrings().stream()
                        .map(suffix -> segment + suffix)
                        .collect(Collectors.toCollection(ArrayList::new));
            } else if (choices.size() > 0) {
                ArrayList<String> choiceStrings = new ArrayList<>();
                for (Chain chain : choices) {
                    for (String choiceString : chain.allStrings()) {
                        for (String suffix : next.allStrings()) {
                            choiceStrings.add(choiceString + suffix);
                        }
                    }
                }
                return choiceStrings;
            } else {
                ArrayList<String> oneEmptyString = new ArrayList<>();
                oneEmptyString.add("");
                return oneEmptyString;
            }
        }

        Point walkPath(Point loc, Set<PointPair> doors) {
            if (segment != null) {
                for (Character c : segment.toCharArray()) {
                    Point nextPoint = loc.walk(c);
                    doors.add(new PointPair(loc, nextPoint));
                    loc = nextPoint;
                }
                if (next != null)
                    return next.walkPath(loc, doors);
                else
                    return loc;
            } else {
                for (Chain choice : choices) {
                    Point locAtEndOfChoice = choice.walkPath(loc, doors);
                    next.walkPath(locAtEndOfChoice, doors);
                }
                return loc;
            }

        }

        @Override
        public String toString() {
            if (segment != null)
                return segment + next.toString();
            else if (choices.size() > 0) {
                String s = "(" + choices.get(0);
                for (int i = 1; i < choices.size(); ++i) {
                    s += "|" + choices.get(i);
                }
                return s + ")" + next.toString();
            } else
                return "";
        }

    }

    public static void main(String[] args) throws IOException {
        String path = new String(
                Files.readAllBytes(Paths.get("data", "day20.txt")));
        System.out.println(path);
    }

    public static void printMap(Point point, HashSet<PointPair> doors,
            PrintStream out) {
        int maxX, maxY, minX, minY;
        maxX = maxY = Integer.MIN_VALUE;
        minX = minY = Integer.MAX_VALUE;
        for (PointPair pair : doors) {
            maxX = Math.max(maxX, Math.max(pair.x1, pair.x2));
            maxY = Math.max(maxY, Math.max(pair.y1, pair.y2));
            minX = Math.min(minX, Math.min(pair.x1, pair.x2));
            minY = Math.min(minY, Math.min(pair.y1, pair.y2));
        }

        int mapWidth = maxX - minX + 1;
        int mapHeight = maxY - minY + 1;
        char[][] map = new char[mapWidth * 2][mapHeight * 2];
        for (int x = 0; x < mapWidth * 2; x++) {
            for (int y = 0; y < mapHeight * 2; y++) {
                if (x % 2 == 0 && y % 2 == 0) {
                    map[x][y] = '.';
                } else if (x % 2 == 1 && y % 2 == 1) {
                    map[x][y] = '#';
                } else {
                    map[x][y] = '?';
                }
            }
        }

        out.println("min = " + minX + "," + minY);
        out.println("max = " + maxX + "," + maxY);
        out.println("extent = " + mapWidth * 2 + "," + mapHeight * 2);
        for (PointPair pair : doors) {
            System.out.println(pair);
            Point doorLocation = pair.doorLocationRelativeTo(minX, minY);
            System.out.println(doorLocation);
            map[doorLocation.x][doorLocation.y] = pair.doorSymbol;
        }

        for (int y = 0; y < map[0].length; ++y) {
            for (int x = 0; x < map.length; ++x) {
                out.print(map[x][y]);
            }
            out.println();
        }
    }
}
