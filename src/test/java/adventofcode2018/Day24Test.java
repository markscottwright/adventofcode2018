package adventofcode2018;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import adventofcode2018.Day24.Armies;

public class Day24Test {

    @Test
    public void testSampleData() {
        String input =
        //@formatter:off
        "Immune System:\r\n" + 
        "17 units each with 5390 hit points (weak to radiation, bludgeoning) with an attack that does 4507 fire damage at initiative 2\r\n" + 
        "989 units each with 1274 hit points (immune to fire; weak to bludgeoning, slashing) with an attack that does 25 slashing damage at initiative 3\r\n" + 
        "\r\n" + 
        "Infection:\r\n" + 
        "801 units each with 4706 hit points (weak to radiation) with an attack that does 116 bludgeoning damage at initiative 1\r\n" + 
        "4485 units each with 2961 hit points (immune to radiation; weak to fire, cold) with an attack that does 12 slashing damage at initiative 4";
        //@formatter:on

        Armies armies = Armies.parse(Arrays.asList(input.split("\r\n")));
        armies.fight();
        assertEquals(5216, armies.numInfectionUnits());
    }

}
