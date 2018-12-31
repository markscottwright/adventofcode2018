package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

public class Day7 {
    public static class Dependency {
        final String step;
        final String dependency;
        private static Pattern DEPENDENCY_PATTERN = Pattern.compile(
                "Step.([A-Za-z]+).must.be.finished.before.step.([A-Za-z]+).can.begin.");

        @Override
        public String toString() {
            return "Dependency [step=" + step + ", dependency=" + dependency
                    + "]";
        }

        public Dependency(String step, String dependency) {
            this.step = step;
            this.dependency = dependency;
        }

        static Dependency parse(String line) {
            Matcher m = DEPENDENCY_PATTERN.matcher(line);
            m.matches();
            return new Dependency(m.group(2), m.group(1));
        }
    }

    public static void main(String[] args) throws IOException {
        List<Dependency> dependencies = Files
                .lines(Paths.get("data", "day7.txt")).map(Dependency::parse)
                .collect(Collectors.toList());

        String stepOrder = determineCompletionOrder(dependencies);
        System.out.println(stepOrder);
    }

    static String determineCompletionOrder(List<Dependency> dependencies) {
        Multimap<String, String> stepToDependencies = MultimapBuilder.hashKeys()
                .hashSetValues().build();
        HashSet<String> stepsRemaining = new HashSet<>();
        for (Dependency d : dependencies) {
            stepToDependencies.put(d.step, d.dependency);

            // keep track of all possible steps and dependencies
            stepsRemaining.add(d.step);
            stepsRemaining.add(d.dependency);
        }

        String stepOrder = "";
        HashSet<String> completedSteps = new HashSet<>();
        while (stepsRemaining.size() > 0) {
            TreeSet<String> stepsWithFufilledDependencies = new TreeSet<>();
            for (String step : stepsRemaining) {
                if (completedSteps.containsAll(stepToDependencies.get(step))) {
                    stepsWithFufilledDependencies.add(step);
                }
            }
            String stepToComplete = stepsWithFufilledDependencies.first();
            System.out.println(stepToComplete);
            stepOrder += stepToComplete;
            stepsRemaining.remove(stepToComplete);
            completedSteps.add(stepToComplete);
        }
        return stepOrder;
    }
}
