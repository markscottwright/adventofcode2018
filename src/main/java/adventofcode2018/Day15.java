package adventofcode2018;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import adventofcode2018.Day15.Combatant.Type;

public class Day15 {
    public static class Point implements Comparable<Point> {

        final int x;
        final int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Point o) {
            if (y < o.y || (y == o.y && x < o.x))
                return -1;
            else if (y == o.y && x == o.x)
                return 0;
            return 1;
        }

        TreeSet<Point> emptyNeighbors(char[][] grid) {
            TreeSet<Point> squares = new TreeSet<>();
            if (x > 0 && grid[x - 1][y] == '.')
                squares.add(new Point(x - 1, y));
            if (x < grid.length - 1 && grid[x + 1][y] == '.')
                squares.add(new Point(x + 1, y));
            if (y > 0 && grid[x][y - 1] == '.')
                squares.add(new Point(x, y - 1));
            if (y < grid[x].length - 1 && grid[x][y + 1] == '.')
                squares.add(new Point(x, y + 1));
            return squares;
        }

        boolean isAdjacent(Point p) {
            //@formatter:off
            boolean isAdjacent = 
                       p.x == x && p.y == y - 1 
                    || p.x == x && p.y == y + 1
                    || p.x == x - 1 && p.y == y 
                    || p.x == x + 1 && p.y == y;
            //@formatter:on
            return isAdjacent;
        }

        @Override
        public String toString() {
            return "[x=" + x + ", y=" + y + "]";
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

    public static class Combatant implements Comparable<Combatant> {
        enum Type {
            GOBLIN, ELF;

            public Type enemyType() {
                if (this == GOBLIN)
                    return ELF;
                else
                    return GOBLIN;
            }
        }

        private static final int HIT_POWER = 3;

        final Type type;
        final Point point;
        final int hitPoints;

        public Combatant(Type type, int x, int y, int hitPoints) {
            this.type = type;
            this.point = new Point(x, y);
            this.hitPoints = hitPoints;
        }

        @Override
        public String toString() {
            return "Combatant [type=" + type + ", point=" + point
                    + ", hitPoints=" + hitPoints + "]";
        }

        @Override
        public int compareTo(Combatant o) {
            return getPoint().compareTo(o.getPoint());
        }

        Point getPoint() { return point; }

        public TreeSet<Combatant> adjacentEnemies(
                TreeSet<Combatant> combatants) {
            return combatants.stream().filter(c -> !c.type.equals(type))
                    .filter(c -> getPoint().isAdjacent(c.getPoint()))
                    .collect(Collectors.toCollection(TreeSet::new));
        }

        public Combatant attack(TreeSet<Combatant> adjacentEnemies) {
            ArrayList<Combatant> orderedAdjacentEnemies = new ArrayList<>(
                    adjacentEnemies);
            orderedAdjacentEnemies.sort((e1, e2) -> {
                if (e1.hitPoints < e2.hitPoints)
                    return -1;
                else if (e1.hitPoints > e2.hitPoints)
                    return 1;
                else
                    return e1.point.compareTo(e2.point);
            });

            if (orderedAdjacentEnemies.size() == 0)
                return null;
            else {
                return orderedAdjacentEnemies.get(0).hit(HIT_POWER);
            }
        }

        private Combatant hit(int hitPower) {
            return new Combatant(type, point.x, point.y,
                    Math.max(0, hitPoints - hitPower));
        }

        public boolean isDead() { return hitPoints <= 0; }

        public Combatant moveTo(Point p) {
            return new Combatant(type, p.x, p.y, hitPoints);
        }
    }

    public static class Arena {
        private TreeSet<Combatant> combatants;
        final private char[][] map;

        public Arena(char[][] map, TreeSet<Combatant> combatants) {
            this.combatants = combatants;
            this.map = map;
        }

        void printGrid(TreeSet<Combatant> combatants, PrintStream out) {
            TreeMap<Point, Character> combatantPositions = new TreeMap<>();
            for (Combatant c : combatants)
                combatantPositions.put(c.getPoint(),
                        c.type.equals(Type.ELF) ? 'E' : 'G');
            for (int y = 0; y < map[0].length; ++y) {
                for (int x = 0; x < map.length; ++x) {
                    Character combatant = combatantPositions
                            .get(new Point(x, y));
                    if (combatant == null)
                        out.print(map[x][y]);
                    else
                        out.print(combatant);
                }
                out.println();
            }
        }

        public static Arena parse(List<String> readAllLines) {
            int maxX = readAllLines.stream().map(String::length)
                    .collect(Collectors.maxBy(Integer::compare)).get();
            int maxY = readAllLines.size();

            TreeSet<Combatant> combatants = new TreeSet<>();
            char[][] map = new char[maxX][maxY];
            for (int x = 0; x < maxX; ++x) {
                for (int y = 0; y < maxY; ++y) {
                    char mapSquare = readAllLines.get(y).length() >= maxX
                            ? readAllLines.get(y).charAt(x)
                            : '#';
                    if (mapSquare == 'G') {
                        combatants.add(new Combatant(Combatant.Type.GOBLIN, x,
                                y, 200));
                        map[x][y] = '.';
                    } else if (mapSquare == 'E') {
                        combatants.add(
                                new Combatant(Combatant.Type.ELF, x, y, 200));
                        map[x][y] = '.';
                    } else
                        map[x][y] = mapSquare;
                }
            }

            return new Arena(map, combatants);
        }

        public int numElves() {
            return (int) combatants.stream().filter(c -> c.type == Type.ELF)
                    .count();
        }

        public int numGoblins() {
            return (int) combatants.stream().filter(c -> c.type == Type.GOBLIN)
                    .count();
        }

        void takeTurn() {
            TreeSet<Combatant> pending = new TreeSet<>(combatants);
            TreeSet<Combatant> remainingCombatants = new TreeSet<>(pending);

            for (Combatant c : pending) {
                if (numOfType(Type.GOBLIN, pending) == 0
                        || numOfType(Type.ELF, pending) == 0) {
                    return;
                }

                // this combatant was killed
                if (!remainingCombatants.contains(c))
                    continue;

                TreeSet<Combatant> adjacentEnemies = c
                        .adjacentEnemies(remainingCombatants);

                // there are enemies nearby - attack
                if (adjacentEnemies.size() > 0) {
                    Combatant attackedCombatant = c.attack(adjacentEnemies);
                    if (attackedCombatant != null) {
                        remainingCombatants.remove(attackedCombatant);
                        if (!attackedCombatant.isDead())
                            remainingCombatants.add(attackedCombatant);
                    }
                }

                // no enemies nearby, move towards one
                else {
                    TreeSet<Point> possibleDestinations = nonWallSquaresAdjacentToEnemyOf(
                            c.type, remainingCombatants);
                    Point p = firstStepTowardsNearestOf(map,
                            remainingCombatants, c.getPoint(),
                            possibleDestinations);
                    if (p != null) {
                        remainingCombatants.remove(c);
                        remainingCombatants.add(c.moveTo(p));
                    }
                }
            }

            combatants = remainingCombatants;
        }

        public static Point firstStepTowardsNearestOf(char[][] map,
                TreeSet<Combatant> remainingCombatants, Point startingPoint,
                TreeSet<Point> possibleDestinations) {

            // keep track of where all combatants are, since they block our path
            TreeSet<Point> combatantPositions = remainingCombatants.stream()
                    .map(Combatant::getPoint)
                    .collect(Collectors.toCollection(TreeSet::new));

            // keep track of the path to a point
            TreeMap<Point, ArrayList<Point>> pathTo = new TreeMap<>();

            // next step in BFS algorithm
            Queue<Point> toVisit = new LinkedList<>();

            // populate the toVisit queue and the first step map with the
            // neighbors of startingPoint
            startingPoint.emptyNeighbors(map).stream()
                    .filter(p -> !combatantPositions.contains(p)).forEach(p -> {
                        var pathToP = new ArrayList<Point>();
                        pathToP.add(p);
                        pathTo.put(p, pathToP);
                        toVisit.add(p);
                    });

            HashSet<ArrayList<Point>> candidatePaths = new HashSet<>();
            while (!toVisit.isEmpty()) {
                Point step = toVisit.remove();

                ArrayList<Point> firstStepTowardsCurrent = pathTo.get(step);
                if (candidatePaths.size() > 0 && candidatePaths.iterator()
                        .next().size() < firstStepTowardsCurrent.size()) {
                    // we have found a least one path shorter than the one we're
                    // working on, we're done.
                    break;
                }

                // found a path to a possible destination
                if (possibleDestinations.contains(step)) {
                    candidatePaths.add(firstStepTowardsCurrent);
                }

                // move from here
                else {
                    step.emptyNeighbors(map).stream()
                            // someone in the way?
                            .filter(p -> !combatantPositions.contains(p))

                            // already been here?
                            .filter(p -> !pathTo.containsKey(p))

                            // add to to-visit spaces and the path we're working
                            // on
                            .forEach(p -> {
                                var pathToP = new ArrayList<>(
                                        firstStepTowardsCurrent);
                                pathToP.add(p);
                                pathTo.put(p, pathToP);
                                toVisit.add(p);
                            });
                }
            }

            // didn't find a path to any of the possible destinations
            if (candidatePaths.size() == 0)
                return null;

            // found a path. Return best "first step"
            TreeSet<Point> firstSteps = new TreeSet<>();
            for (ArrayList<Point> path : candidatePaths)
                firstSteps.add(path.get(0));

            return firstSteps.first();
        }

        static private int numOfType(Type t, TreeSet<Combatant> pending) {
            return (int) pending.stream().filter(c -> c.type == t).count();
        }

        TreeSet<Point> nonWallSquaresAdjacentToEnemyOf(Type t,
                TreeSet<Combatant> currentCombatants) {
            Type enemyType = t == Type.ELF ? Type.GOBLIN : Type.ELF;
            TreeSet<Point> adjacentPoints = new TreeSet<>();
            currentCombatants.stream().filter(c -> c.type == enemyType)
                    .map(Combatant::getPoint)
                    .flatMap(p -> p.emptyNeighbors(map).stream())
                    .forEach(p -> adjacentPoints.add(p));
            return adjacentPoints;
        }

        public char[][] getMap() { return map; }

        public TreeSet<Combatant> getCombatants() { return combatants; }
    }

    public static void main(String[] args) throws IOException {
        Arena arena = Arena
                .parse(Files.readAllLines(Paths.get("data", "day15.txt")));
        System.out.println(arena.numElves());
        System.out.println(arena.numGoblins());
    }
}
