package adventofcode2018;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

import com.google.common.collect.Lists;

import adventofcode2018.Day7.Dependency;

public class Day7Test {

    @Test
    public void test() {
        var dependencies = getSampleDependencies();
        assertEquals("CABDFE",
                new Day7.SledBuildScheduler(dependencies).build());
    }

    public static ArrayList<Dependency> getSampleDependencies() {
        var dependencies = new ArrayList<Dependency>();
        dependencies.add(Dependency
                .parse("Step C must be finished before step A can begin."));

        dependencies.add(Dependency
                .parse("Step C must be finished before step F can begin."));
        dependencies.add(Dependency
                .parse("Step A must be finished before step B can begin."));
        dependencies.add(Dependency
                .parse("Step A must be finished before step D can begin."));
        dependencies.add(Dependency
                .parse("Step B must be finished before step E can begin."));
        dependencies.add(Dependency
                .parse("Step D must be finished before step E can begin."));
        dependencies.add(Dependency
                .parse("Step F must be finished before step E can begin."));
        return dependencies;
    }

}
