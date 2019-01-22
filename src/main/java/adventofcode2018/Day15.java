package adventofcode2018;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
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

    public static class Combatant {
        enum Type {
            GOBLIN, ELF;

            public Type enemyType() {
                if (this == GOBLIN)
                    return ELF;
                else
                    return GOBLIN;
            }
        }

        final Type type;
        final int attackPower;
        private Point point;
        private int hitPoints;

        public Combatant(Type type, int x, int y, int hitPoints,
                int attackPower) {
            this.type = type;
            this.point = new Point(x, y);
            this.hitPoints = hitPoints;
            this.attackPower = attackPower;
        }

        @Override
        public String toString() {
            return "Combatant [type=" + type + ", point=" + point
                    + ", hitPoints=" + hitPoints + "]";
        }

        public List<Combatant> adjacentEnemies(
                ArrayList<Combatant> combatants) {
            var orderdCombatants = readingOrder(combatants);
            return orderdCombatants.stream().filter(c -> !c.type.equals(type))
                    .filter(c -> getPoint().isAdjacent(c.getPoint()))
                    .collect(Collectors.toList());
        }

        public static ArrayList<Combatant> readingOrder(
                Collection<Combatant> combatants) {
            var ordered = new ArrayList<>(combatants);
            ordered.sort((c1, c2) -> c1.getPoint().compareTo(c2.getPoint()));
            return ordered;
        }

        public Combatant selectCombatantToAttack(
                Collection<Combatant> adjacentEnemies) {
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

            return orderedAdjacentEnemies.get(0);
        }

        public void takeDamage(int attackPower) {
            hitPoints = Math.max(0, hitPoints - attackPower);
        }

        public boolean isDead() { return hitPoints <= 0; }

        public void moveTo(Point p) {
            point = p;
        }

        public Point getPoint() { return point; }

        public int getHitPoints() { return hitPoints; }

        public int getAttackPower() { return attackPower; }
    }

    public static class Arena {
        private ArrayList<Combatant> combatants;
        final private char[][] map;
        private int roundsTaken = 0;

        public Arena(char[][] map, ArrayList<Combatant> combatants) {
            this.combatants = combatants;
            this.map = map;
        }

        public Arena copy(int elfAttackPower) {
            ArrayList<Combatant> combatantsCopy = combatants.stream()
                    .map(c -> new Combatant(c.type, c.point.x, c.point.y,
                            c.hitPoints,
                            c.type.equals(Type.ELF) ? elfAttackPower
                                    : c.attackPower))
                    .collect(Collectors.toCollection(ArrayList::new));
            return new Arena(map, combatantsCopy);
        }

        static int outcomeWithNoElfLosses(Arena arena) {
            int originalElfCount = arena.numElves();
            int hitPower = 4;
            while (true) {
                var currentArena = arena.copy(hitPower);
                while (!currentArena.combatComplete()
                        && currentArena.numElves() == originalElfCount) {
                    currentArena.takeTurn();
                }
                if (currentArena.numElves() == originalElfCount)
                    return currentArena.roundsTaken
                            * currentArena.totalHitPoints();

                hitPower++;
            }
        }

        void printGrid(ArrayList<Combatant> combatants, PrintStream out) {
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

        public static Arena parse(List<String> mapLines) {
            return parse(mapLines, 3);
        }

        public static Arena parse(List<String> mapLines, int elfattackPower) {
            int maxX = mapLines.stream().map(String::length)
                    .collect(Collectors.maxBy(Integer::compare)).get();
            int maxY = mapLines.size();

            ArrayList<Combatant> combatants = new ArrayList<>();
            char[][] map = new char[maxX][maxY];
            for (int x = 0; x < maxX; ++x) {
                for (int y = 0; y < maxY; ++y) {
                    char mapSquare = mapLines.get(y).length() >= maxX
                            ? mapLines.get(y).charAt(x)
                            : '#';
                    if (mapSquare == 'G') {
                        combatants.add(new Combatant(Combatant.Type.GOBLIN, x,
                                y, 200, 3));
                        map[x][y] = '.';
                    } else if (mapSquare == 'E') {
                        combatants.add(new Combatant(Combatant.Type.ELF, x, y,
                                200, elfattackPower));
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
            ArrayList<Combatant> pending = Combatant.readingOrder(combatants);
            for (Combatant c : pending) {

                // return before completing the round if there are no combatants
                // left
                if (combatComplete())
                    return;

                if (c.isDead())
                    continue;

                // if there are no adjacent enemies, move towards one
                List<Combatant> adjacentEnemies = c.adjacentEnemies(combatants);
                if (adjacentEnemies.size() == 0) {
                    TreeSet<Point> possibleDestinations = nonWallSquaresAdjacentToEnemyOf(
                            c.type, combatants);
                    Point p = firstStepTowardsNearestOf(map, combatants,
                            c.getPoint(), possibleDestinations);
                    if (p != null) {
                        // System.out.println(c + " moves to " + p);
                        c.moveTo(p);
                    }
                }

                // if there are adjacent enemies, attack
                adjacentEnemies = c.adjacentEnemies(combatants);
                if (adjacentEnemies.size() > 0) {
                    Combatant combatantToAttack = c
                            .selectCombatantToAttack(adjacentEnemies);
                    // System.out.println(c + " attacks " + combatantToAttack);
                    combatantToAttack.takeDamage(c.getAttackPower());
                    if (combatantToAttack.isDead())
                        combatants.remove(combatantToAttack);
                }
            }

            // only increment if a "full round" was taken
            roundsTaken++;
        }

        public boolean combatComplete() {
            return numOfType(Type.GOBLIN, combatants) == 0
                    || numOfType(Type.ELF, combatants) == 0;
        }

        public static Point firstStepTowardsNearestOf(char[][] map,
                ArrayList<Combatant> remainingCombatants, Point startingPoint,
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

            TreeMap<Point, ArrayList<Point>> candidatePaths = new TreeMap<>();
            while (!toVisit.isEmpty()) {
                Point step = toVisit.remove();

                ArrayList<Point> firstStepTowardsCurrent = pathTo.get(step);
                if (candidatePaths.size() > 0 && candidatePaths.firstEntry()
                        .getValue().size() < firstStepTowardsCurrent.size()) {
                    // we have found a least one path shorter than the one we're
                    // working on, we're done.
                    break;
                }

                // found a path to a possible destination
                if (possibleDestinations.contains(step)) {
                    candidatePaths.put(step, firstStepTowardsCurrent);
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

            // found a path. Return first step towards "best" destination
            return candidatePaths.firstEntry().getValue().get(0);
        }

        static private int numOfType(Type t, Collection<Combatant> pending) {
            return (int) pending.stream().filter(c -> c.type == t).count();
        }

        TreeSet<Point> nonWallSquaresAdjacentToEnemyOf(Type t,
                ArrayList<Combatant> currentCombatants) {
            Type enemyType = t == Type.ELF ? Type.GOBLIN : Type.ELF;
            TreeSet<Point> adjacentPoints = new TreeSet<>();
            currentCombatants.stream().filter(c -> c.type == enemyType)
                    .map(Combatant::getPoint)
                    .flatMap(p -> p.emptyNeighbors(map).stream())
                    .forEach(p -> adjacentPoints.add(p));
            return adjacentPoints;
        }

        public char[][] getMap() { return map; }

        public ArrayList<Combatant> getCombatants() {
            return Combatant.readingOrder(combatants);
        }

        public int getRoundsTaken() { return roundsTaken; }

        public int totalHitPoints() {
            return getCombatants().stream().mapToInt(Combatant::getHitPoints)
                    .sum();
        }
    }

    @SuppressWarnings("unused")
    static private void printGridState(Arena arena) {
        System.out.println("round:" + arena.getRoundsTaken());
        arena.printGrid(arena.getCombatants(), System.out);
        arena.getCombatants().stream().forEach(c -> System.out
                .println(c.type + "(" + c.getHitPoints() + ") "));
    }

    public static void main(String[] args) throws IOException {
        List<String> mapLines = Files
                .readAllLines(Paths.get("data", "day15.txt"));
        Arena arena = Arena.parse(mapLines);
        while (!arena.combatComplete())
            arena.takeTurn();
        System.out.println(arena.getRoundsTaken());
        System.out.println(arena.totalHitPoints());
        System.out.println("part one:" + (arena.totalHitPoints() * arena.getRoundsTaken()));
        
        arena = Arena.parse(mapLines);
        System.out.println("part two:" + Arena.outcomeWithNoElfLosses(arena));
    }
}
