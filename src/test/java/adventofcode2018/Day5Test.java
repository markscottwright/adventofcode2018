package adventofcode2018;

import static org.junit.Assert.*;

import org.junit.Test;

public class Day5Test {

    @Test
    public void testSampleData() {
        var polymer = new Day5.Polymer("dabAcCaCBAcCcaDA");
        polymer.removeComponentsWithOppositePolarity();
        assertEquals(10, polymer.numComponents());
        assertEquals("dabCBAcaDA", polymer.getPolymerString());
    }

}
