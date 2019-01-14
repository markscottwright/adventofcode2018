package adventofcode2018;

import static org.junit.Assert.*;

import org.junit.Test;

public class Day14Test {

    @Test
    public void testSampleData() {
        var b = new Day14.RecipeBoard();
        b.makeRecipes();
        assertEquals("(3)[7] 1  0 ", b.toString());
        b.makeRecipes();
        assertEquals(" 3  7  1 [0](1) 0 ", b.toString());
    }

}
