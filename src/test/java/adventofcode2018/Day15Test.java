package adventofcode2018;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
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
            Arena arena, TreeSet<Combatant> currentCombatants) {
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
        TreeSet<Combatant> fixedHitPoints = new TreeSet<>();
        Iterator<Combatant> it = arena.getCombatants().iterator();
        Combatant c = it.next();
        fixedHitPoints.add(new Combatant(c.type, c.point.x, c.point.y, 9));
        c = it.next();
        fixedHitPoints.add(new Combatant(c.type, c.point.x, c.point.y, 4));
        Combatant elf = it.next();
        c = it.next();
        fixedHitPoints.add(new Combatant(c.type, c.point.x, c.point.y, 2));
        c = it.next();
        fixedHitPoints.add(new Combatant(c.type, c.point.x, c.point.y, 2));
        c = it.next();
        fixedHitPoints.add(new Combatant(c.type, c.point.x, c.point.y, 1));

        assertEquals(Type.ELF, elf.type);

        Combatant attackedGnome = elf
                .attack(elf.adjacentEnemies(fixedHitPoints));
        assertTrue(attackedGnome.isDead());
        assertEquals(3, attackedGnome.point.x);
        assertEquals(2, attackedGnome.point.y);
    }
}
