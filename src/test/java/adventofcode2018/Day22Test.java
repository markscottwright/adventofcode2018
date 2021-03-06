package adventofcode2018;

import static org.junit.Assert.*;

import org.junit.Test;

import adventofcode2018.Day22.Equipment;
import adventofcode2018.Day22.Path;
import adventofcode2018.Day22.Step;

public class Day22Test {

    @Test
    public void testExamples() {
        Day22.Cave cave = new Day22.Cave(10, 10, 510);
        assertEquals(510, cave.erosionLevel(0, 0));
        assertEquals(Day22.RegionType.rocky, cave.getType(0, 0));
        
        assertEquals(17317, cave.erosionLevel(1, 0));
        assertEquals(Day22.RegionType.wet, cave.getType(1, 0));

        assertEquals(8415, cave.erosionLevel(0, 1));
        assertEquals(Day22.RegionType.rocky, cave.getType(0, 1));
        
        assertEquals(1805, cave.erosionLevel(1, 1));
        assertEquals(Day22.RegionType.narrow, cave.getType(1, 1));
        
        assertEquals(510, cave.erosionLevel(10, 10));
        assertEquals(Day22.RegionType.rocky, cave.getType(10, 10));
        
        assertEquals(114, cave.totalRiskLevel());
    }

    @Test
    public void testPaths() {
        
        Path p = new Path();
        Path p2 = p.addStep(Step.MOVE_RIGHT);
        assertEquals(new Day22.CavePosition(1,0), p2.position);
        assertEquals(Equipment.TORCH, p2.equipment);
    }
}
