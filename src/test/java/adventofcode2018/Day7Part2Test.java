package adventofcode2018;

import static org.junit.Assert.*;

import org.junit.Test;

import adventofcode2018.Day7.SledBuildScheduler;

public class Day7Part2Test {

    public static class TestElfTeam extends Day7Part2.ElfTeam {
        public TestElfTeam(SledBuildScheduler builder, int numWorkers) {
            super(builder, numWorkers);
        }

        @Override
        public int secondsToComplete(String nextStep) {
            return super.secondsToComplete(nextStep) - 60;
        }
    }

    @Test
    public void testSampleData() {
        var scheduler = new Day7.SledBuildScheduler(
                Day7Test.getSampleDependencies());
        var team = new TestElfTeam(scheduler, 2);
        assertEquals(15, team.build());
    }

}
