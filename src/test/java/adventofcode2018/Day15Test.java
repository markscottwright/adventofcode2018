package adventofcode2018;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import adventofcode2018.Day15.Arena;
import adventofcode2018.Day15.Combatant;
import adventofcode2018.Day15.Combatant.Type;
import adventofcode2018.Day15.Point;

public class Day15Test {

    @Test
    public void testEmptyNeighbors() {
        //@formatter:off
        var lines1 = 
         ("#####\n"
        + "#...#\n"
        + "#...#\n"
        + "#####\n").split("\n");
        //@formatter:on
        Arena arena = Day15.Arena.parse(Arrays.asList(lines1));
        Assert.assertEquals(0, arena.numElves());
        Assert.assertEquals(0, arena.numGoblins());
        assertEquals(2,
                new Day15.Point(1, 1).emptyNeighbors(arena.getMap()).size());
        assertEquals(3,
                new Day15.Point(2, 1).emptyNeighbors(arena.getMap()).size());

        ArrayList<String> lines2 = new ArrayList<>();
        lines2.add("...");
        Arena arena2 = Day15.Arena.parse(lines2);
        assertEquals(1,
                new Day15.Point(0, 0).emptyNeighbors(arena2.getMap()).size());
        assertEquals(2,
                new Day15.Point(1, 0).emptyNeighbors(arena2.getMap()).size());
    }

    @Test
    public void testCombatantParse() {
        //@formatter:off
        var lines2 = 
         ("#####\n"
        + "#.G.#\n"
        + "#EEE#\n"
        + "#####\n").split("\n");
        //@formatter:on
        Arena arena2 = Day15.Arena.parse(Arrays.asList(lines2));
        Assert.assertEquals(3, arena2.numElves());
        Assert.assertEquals(1, arena2.numGoblins());
    }

    @Test
    public void testEmptySquares() {
        //@formatter:off
        var lines2 = 
         ("#####\n"
        + "#.G.#\n"
        + "#EEE#\n"
        + "#####\n").split("\n");
        //@formatter:on

        Arena arena = Day15.Arena.parse(Arrays.asList(lines2));

        List<Point> emptySpaces = empySpacesAdjacentToEnemyOfType(Type.ELF,
                arena, arena.getCombatants());
        assertEquals(2, emptySpaces.size());
    }

    static List<Point> empySpacesAdjacentToEnemyOfType(Type enemyType,
            Arena arena, ArrayList<Combatant> currentCombatants) {
        Set<Point> occupiedSpaces = currentCombatants.stream()
                .map(Combatant::getPoint).collect(Collectors.toSet());
        TreeSet<Point> spaces = arena.nonWallSquaresAdjacentToEnemyOf(enemyType,
                currentCombatants);
        List<Point> emptySpaces = spaces.stream()
                .filter(p -> !occupiedSpaces.contains(p))
                .collect(Collectors.toList());
        return emptySpaces;
    }

    @Test
    public void testSampleData() {
        //@formatter:off
        var lines = (
                "#######\n" + 
                "#E..G.#\n" + 
                "#...#.#\n" + 
                "#.G.#G#\n" + 
                "#######");
        //@formatter:on

        Arena arena = parseString(lines);
        TreeSet<Point> adjacentSquares = arena.nonWallSquaresAdjacentToEnemyOf(
                Type.ELF, arena.getCombatants());
        assertEquals(6, adjacentSquares.size());
        Point point = Arena.firstStepTowardsNearestOf(arena.getMap(),
                arena.getCombatants(), new Point(1, 1), adjacentSquares);
        assertEquals(new Point(2, 1), point);

        arena.printGrid(arena.getCombatants(), System.out);
    }

    private Arena parseString(String lines, int elfattackPower) {
        return Arena.parse(Arrays.asList(lines.split("\n")), elfattackPower);
    }

    private Arena parseString(String lines) {
        return Arena.parse(Arrays.asList(lines.split("\n")));
    }

    @Test
    public void testMovement() {
        var lines =
        //@formatter:off
                "#########\n" + 
                "#G..G..G#\n" + 
                "#.......#\n" + 
                "#.......#\n" + 
                "#G..E..G#\n" + 
                "#.......#\n" + 
                "#.......#\n" + 
                "#G..G..G#\n" + 
                "#########";
                //@formatter:on
        Arena arena = parseString(lines);
        arena.takeTurn();
        arena.takeTurn();
        arena.takeTurn();
        //@formatter:off
        assertEquals(
                "#########\r\n" + 
                "#.......#\r\n" + 
                "#..GGG..#\r\n" + 
                "#..GEG..#\r\n" + 
                "#G..G...#\r\n" + 
                "#......G#\r\n" + 
                "#.......#\r\n" + 
                "#.......#\r\n" + 
                "#########\r\n", gridString(arena));
        //@formatter:on
    }

    private String gridString(Arena arena) {
        ByteArrayOutputStream gridStringStream = new ByteArrayOutputStream();
        try (PrintStream out = new PrintStream(gridStringStream)) {
            arena.printGrid(arena.getCombatants(), out);
        }
        return gridStringStream.toString();
    }

    @Test
    public void testSampleAttack() throws Exception {
        //@formatter:off
        var arena = parseString(
                "G....\r\n" + 
                "..G..\r\n" + 
                "..EG.\r\n" + 
                "..G..\r\n" + 
                "...G.\r\n"); 
        //@formatter:on

        // assign hit points per test data
        ArrayList<Combatant> fixedHitPoints = new ArrayList<>();
        Iterator<Combatant> it = arena.getCombatants().iterator();
        Combatant c = it.next();
        fixedHitPoints.add(
                new Combatant(c.type, c.getPoint().x, c.getPoint().y, 9, 3));
        c = it.next();
        fixedHitPoints.add(
                new Combatant(c.type, c.getPoint().x, c.getPoint().y, 4, 3));
        Combatant elf = it.next();
        c = it.next();
        fixedHitPoints.add(
                new Combatant(c.type, c.getPoint().x, c.getPoint().y, 2, 3));
        c = it.next();
        fixedHitPoints.add(
                new Combatant(c.type, c.getPoint().x, c.getPoint().y, 2, 3));
        c = it.next();
        fixedHitPoints.add(
                new Combatant(c.type, c.getPoint().x, c.getPoint().y, 1, 3));

        assertEquals(Type.ELF, elf.type);

        Combatant attackedGnome = elf
                .selectCombatantToAttack(elf.adjacentEnemies(fixedHitPoints));
        attackedGnome.takeDamage(elf.getAttackPower());
        assertTrue(attackedGnome.isDead());
        assertEquals(3, attackedGnome.getPoint().x);
        assertEquals(2, attackedGnome.getPoint().y);
    }

    @Test
    public void testSampleCombat() {
        //@formatter:off
        var arena = parseString(
                "#######\n" + 
                "#.G...#\n" + 
                "#...EG#\n" + 
                "#.#.#G#\n" + 
                "#..G#E#\n" + 
                "#.....#\n" + 
                "#######\n"); 
        //@formatter:on

        while (!arena.combatComplete())
            arena.takeTurn();

        assertEquals(47, arena.getRoundsTaken());
        assertEquals(590, arena.totalHitPoints());
    }

    @Test
    public void testSampleCombatPart2() {
        //@formatter:off
        var arena = parseString(
                "#######\n" + 
                "#.G...#\n" + 
                "#...EG#\n" + 
                "#.#.#G#\n" + 
                "#..G#E#\n" + 
                "#.....#\n" + 
                "#######\n", 15); 
        //@formatter:on

        while (!arena.combatComplete())
            arena.takeTurn();

        assertEquals(29, arena.getRoundsTaken());
        assertEquals(172, arena.totalHitPoints());
    }

    @Test
    public void testSampleCombat2() {
        //@formatter:off
        var arena = parseString(
                "#######\n" + 
                "#G..#E#\n" + 
                "#E#E.E#\n" + 
                "#G.##.#\n" + 
                "#...#E#\n" + 
                "#...E.#\n" + 
                "#######\n" 
                );
        //@formatter:on

        while (!arena.combatComplete())
            arena.takeTurn();

        assertEquals(37, arena.getRoundsTaken());
        assertEquals(982, arena.totalHitPoints());
    }

    @Test
    public void testSampleCombat3() {
        //@formatter:off
        var arena = parseString(
                "#######\n" + 
                "#E..EG#\n" + 
                "#.#G.E#\n" + 
                "#E.##E#\n" + 
                "#G..#.#\n" + 
                "#..E#.#\n" + 
                "#######\n"  
                );
        //@formatter:on

        while (!arena.combatComplete())
            arena.takeTurn();

        assertEquals(46, arena.getRoundsTaken());
        assertEquals(859, arena.totalHitPoints());
    }

    @Test
    public void testSampleCombat4() {
        //@formatter:off
        var arena = parseString(
                "#######\n" + 
                "#.E...#\n" + 
                "#.#..G#\n" + 
                "#.###.#\n" + 
                "#E#G#G#\n" + 
                "#...#G#\n" + 
                "#######\n"  
                );
        //@formatter:on

        while (!arena.combatComplete())
            arena.takeTurn();

        assertEquals(54, arena.getRoundsTaken());
        assertEquals(536, arena.totalHitPoints());
    }

    @Test
    public void testSampleCombatPart2Example4() {
        //@formatter:off
        var arena = parseString(
                "#######\n" + 
                "#.E...#\n" + 
                "#.#..G#\n" + 
                "#.###.#\n" + 
                "#E#G#G#\n" + 
                "#...#G#\n" + 
                "#######\n", 12); 
        //@formatter:on

        while (!arena.combatComplete()) {
            arena.takeTurn();
            printGridState(arena);
        }

        assertEquals(39, arena.getRoundsTaken());
        assertEquals(166, arena.totalHitPoints());
    }
    @Test
    public void testSampleCombatPart2Example5() {
        //@formatter:off
        var arenaTemplate = parseString(
                "#########\n" + 
                "#G......#\n" + 
                "#.E.#...#\n" + 
                "#..##..G#\n" + 
                "#...##..#\n" + 
                "#...#...#\n" + 
                "#.G...G.#\n" + 
                "#.....G.#\n" + 
                "#########\n"); 
        //@formatter:on
        
        var arena = arenaTemplate.copy(34);
        while (!arena.combatComplete()) {
            arena.takeTurn();
            System.out.println(arena.numElves());
        }

        assertEquals(30, arena.getRoundsTaken());
        assertEquals(38, arena.totalHitPoints());
        
        System.out.println(Arena.outcomeWithNoElfLosses(arenaTemplate.copy(3)));
    }
    private void printGridState(Arena arena) {
        System.out.println("round:" + arena.getRoundsTaken());
        arena.printGrid(arena.getCombatants(), System.out);
        arena.getCombatants().stream().forEach(c -> System.out
                .println(c.type + "(" + c.getHitPoints() + ") "));
    }
}
