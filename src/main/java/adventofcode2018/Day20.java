package adventofcode2018;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.javatuples.Pair;

import adventofcode2018.Day20.Point;

public class Day20 {

    public static class NorthPoleMap {
        private char[][] map;
        private int minX;
        private int minY;
        private int maxX;
        private int maxY;

        public NorthPoleMap(Point origin, Set<PointPair> doors) {
            maxX = maxY = Integer.MIN_VALUE;
            minX = minY = Integer.MAX_VALUE;
            for (PointPair pair : doors) {
                maxX = Math.max(maxX, Math.max(pair.x1, pair.x2));
                maxY = Math.max(maxY, Math.max(pair.y1, pair.y2));
                minX = Math.min(minX, Math.min(pair.x1, pair.x2));
                minY = Math.min(minY, Math.min(pair.y1, pair.y2));
            }

            int mapWidth = maxX - minX + 2;
            int mapHeight = maxY - minY + 2;
            System.out.println("map bounds:" + mapWidth + "," + mapHeight);
            map = new char[mapWidth * 2 - 1][mapHeight * 2 - 1];
            for (int x = 0; x < map.length; x++)
                for (int y = 0; y < map[0].length; y++)
                    map[x][y] = '?';

            for (PointPair p : doors) {
                markPointPair(p);
            }
            Point shiftedOrigin = new Point(origin.x - minX, origin.y - minY);
            map[shiftedOrigin.x * 2 + 1][shiftedOrigin.y * 2 + 1] = 'X';

            // anything that didn't get marked is a wall
            for (int x = 0; x < map.length; x++)
                for (int y = 0; y < map[0].length; y++)
                    if (map[x][y] == '?')
                        map[x][y] = '#';
        }

        private void markPointPair(PointPair original) {

            PointPair p = original.shift(minX, minY);

            // so, if there is a point at 0,0, its room marker is at 1,1, and
            // its corner walls are 0,0, 0,2, 2,0 and 2,2. Possible doors are
            // 0,1, 1,0, 2,1 and 1,2.

            // corners
            map[p.x1 * 2][p.y1 * 2] = '#';
            map[p.x1 * 2 + 2][p.y1 * 2] = '#';
            map[p.x1 * 2][p.y1 * 2 + 2] = '#';
            map[p.x1 * 2 + 2][p.y1 * 2 + 2] = '#';
            map[p.x2 * 2][p.y2 * 2] = '#';
            map[p.x2 * 2 + 2][p.y2 * 2] = '#';
            map[p.x2 * 2][p.y2 * 2 + 2] = '#';
            map[p.x2 * 2 + 2][p.y2 * 2 + 2] = '#';

            // room marker
            map[p.x1 * 2 + 1][p.y1 * 2 + 1] = '.';
            map[p.x2 * 2 + 1][p.y2 * 2 + 1] = '.';

            // pair above each other - horizontal divider
            if (p.x1 == p.x2) {
                map[p.x1 * 2 + 1][p.y1 * 2 + 2] = '-';
            }
            // pair next to each other - vertical divider
            else {
                map[p.x1 * 2 + 2][p.y1 * 2 + 1] = '|';
            }
        }

        void print(PrintStream out) {
            for (int y = 0; y < map[0].length; ++y) {
                for (int x = 0; x < map.length; ++x) {
                    out.print(map[x][y]);
                }
                out.println();
            }
        }
    }

    public static class PointPair {

        final int x1, y1, x2, y2;

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
        }

        private PointPair(int x1, int y1, int x2, int y2, int xOffset,
                int yOffset) {
            this.x1 = x1 - xOffset;
            this.x2 = x2 - xOffset;
            this.y1 = y1 - yOffset;
            this.y2 = y2 - yOffset;
        }

        public PointPair shift(int xOffset, int yOffset) {
            return new PointPair(x1, y1, x2, y2, xOffset, yOffset);
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

    }

    @SuppressWarnings("serial")
    public static class ParsingException extends Exception {

    }

    public static class Point {
        final int x;
        final int y;

        public Point(int x, int y) {
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
    }

    /**
     * Regular expressions are represented by a "chain", which is a linked list
     * of either Segments or lists of Chains.
     * 
     * @author wrightm
     *
     */
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

        Point walkPath(Point loc, Set<PointPair> doors,
                HashMap<Pair<Point, Chain>, Point> alreadyWalked) {
            Pair<Point, Chain> memoize = Pair.with(loc, this);
            if (alreadyWalked.containsKey(memoize))
                return alreadyWalked.get(memoize);

            if (segment != null) {
                for (Character c : segment.toCharArray()) {
                    Point nextPoint = loc.walk(c);
                    int doorsBefore = doors.size();
                    doors.add(new PointPair(loc, nextPoint));
                    loc = nextPoint;
                }
                if (next != null) {
                    var endLoc = next.walkPath(loc, doors, alreadyWalked);
                    alreadyWalked.put(memoize, endLoc);
                    return endLoc;
                } else {
                    alreadyWalked.put(memoize, loc);
                    return loc;
                }
            } else {
                for (Chain choice : choices) {
                    Point locAtEndOfChoice = choice.walkPath(loc, doors,
                            alreadyWalked);
                    next.walkPath(locAtEndOfChoice, doors, alreadyWalked);
                }
                alreadyWalked.put(memoize, loc);
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

        public HashSet<PointPair> walkPath(Point point) {
            HashSet<PointPair> doors = new HashSet<>();
            HashMap<Pair<Point, Chain>, Point> alreadyWalked = new HashMap<Pair<Point, Chain>, Point>();
            walkPath(point, doors, alreadyWalked);
            return doors;
        }

    }

    public static void main(String[] args)
            throws IOException, ParsingException {
        String path = new String(
                Files.readAllBytes(Paths.get("data", "day20.txt")));
        Chain chain = Chain.parse(path);
        Point origin = new Point(0, 0);
        HashSet<PointPair> doors = chain.walkPath(origin);
        // new NorthPoleMap(origin, doors).print(System.out);

        HashMap<Point, HashSet<Point>> roomConnections = doorsToRoomConnections(
                doors);
        HashMap<Point, Integer> distancesFromOrigin = distancesFromOrigin(
                origin, roomConnections);
        Integer furthestDistanceFromOrigin = distancesFromOrigin.values()
                .stream().max(Integer::compare).get();
        System.out.println("part one:" + furthestDistanceFromOrigin);
        long roomsAtLeast1000DoorsAway = distancesFromOrigin.entrySet().stream()
                .filter(e -> e.getValue() >= 1000).count();
        System.out.println("part two:" + roomsAtLeast1000DoorsAway);
    }

    private static HashMap<Point, HashSet<Point>> doorsToRoomConnections(
            HashSet<PointPair> doors) {
        HashMap<Point, HashSet<Point>> roomConnections = new HashMap<>();
        for (PointPair p : doors) {
            Point p1 = new Point(p.x1, p.y1);
            Point p2 = new Point(p.x2, p.y2);
            HashSet<Point> neighbors = roomConnections.getOrDefault(p1,
                    new HashSet<>());
            neighbors.add(p2);
            roomConnections.put(p1, neighbors);
            neighbors = roomConnections.getOrDefault(p2, new HashSet<>());
            neighbors.add(p1);
            roomConnections.put(p2, neighbors);
        }
        return roomConnections;
    }

    private static HashMap<Point, Integer> distancesFromOrigin(Point origin,
            HashMap<Point, HashSet<Point>> roomConnections) {
        HashMap<Point, Integer> distanceFromOrigin = new HashMap<>();
        int currentDistance = 0;
        distanceFromOrigin.put(origin, currentDistance);
        for (Point p : roomConnections.get(origin)) {
            buildGraph(p, roomConnections, distanceFromOrigin, 1);
        }
        return distanceFromOrigin;
    }

    private static void buildGraph(Point p,
            HashMap<Point, HashSet<Point>> roomConnections,
            HashMap<Point, Integer> distanceFromOrigin, int distance) {

        if (distanceFromOrigin.containsKey(p))
            return;
        distanceFromOrigin.put(p, distance);
        for (Point p2 : roomConnections.get(p))
            buildGraph(p2, roomConnections, distanceFromOrigin, distance + 1);
    }
}
