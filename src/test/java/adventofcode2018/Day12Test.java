package adventofcode2018;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.TreeMap;

import org.junit.Test;

import adventofcode2018.Day12.PlantRow;

public class Day12Test {

    @SuppressWarnings("serial")
    static class TestRules extends TreeMap<String, String> {
        public TestRules() {
            put("...##", "#");
            put("..#..", "#");
            put(".#...", "#");
            put(".#.#.", "#");
            put(".#.##", "#");
            put(".##..", "#");
            put(".####", "#");
            put("#.#.#", "#");
            put("#.###", "#");
            put("##.#.", "#");
            put("##.##", "#");
            put("###..", "#");
            put("###.#", "#");
            put("####.", "#");
        }

        @Override
        public String get(Object key) {
            if (super.containsKey(key))
                return super.get(key);
            else
                return ".";
        }
    }

    @Test
    public void testSampleData() throws IOException {
        PlantRow plants = new Day12.PlantRow(new TestRules(),
                "#..#.#..##......###...###");
        plants.updateState();
        assertTrue(plants.getState().startsWith("#...#....#.....#..#..#..#"));
        plants.updateState();
        assertTrue(plants.getState().startsWith("##..##...##....#..#..#..##"));
        plants.updateState();
        assertTrue(plants.getState().startsWith("#.#...#..#.#....#..#..#...#"));
        assertEquals(-1, plants.getFirstPlantNumber());
    }

    @Test
    public void plantRow_usingSampleValues_sumsToExpectedValue() {
        PlantRow plants = new Day12.PlantRow(new TestRules(),
                "#..#.#..##......###...###");
        for (int i = 0; i < 20; ++i) {
            plants.updateState();
        }
        assertEquals(325, plants.stateVal());
    }

}
