package adventofcode2018;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Day11Test {

    @Test
    public void testHundredthsDigit() {
        assertEquals(1, Day11.FuelCellGrid.hundredthsDigit(100));
        assertEquals(0, Day11.FuelCellGrid.hundredthsDigit(99));
        assertEquals(2, Day11.FuelCellGrid.hundredthsDigit(1200));
        assertEquals(9, Day11.FuelCellGrid.hundredthsDigit(9999));
        assertEquals(9, Day11.FuelCellGrid.hundredthsDigit(-9999));
    }

    @Test
    public void testSamplePowerLevels() {
        assertEquals(4, Day11.FuelCellGrid.powerLevel(8, 3, 5));
        assertEquals(-5, Day11.FuelCellGrid.powerLevel(57, 122, 79));
        assertEquals(0, Day11.FuelCellGrid.powerLevel(39, 217, 196));
    }

    @Test
    public void testSampleGrid() {
        assertArrayEquals(new int[] { 21, 61 },
                new Day11.FuelCellGrid(42).maxPower3x3());
        assertArrayEquals(new int[] { 33, 45 },
                new Day11.FuelCellGrid(18).maxPower3x3());
    }

}
