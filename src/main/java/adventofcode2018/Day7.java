package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

        static List<Day7.Dependency> loadFile(Path dependencyDataFile)
                throws IOException {
            return Files.lines(dependencyDataFile).map(Day7.Dependency::parse)
                    .collect(Collectors.toList());
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

    /**
     * A class that keeps track of dependency graph, steps remaining and steps
     * completeds.
     * 
     * @author wrightm
     *
     */
    static class SledBuildScheduler {
        private Multimap<String, String> stepToDependencies = MultimapBuilder
                .hashKeys().hashSetValues().build();
        private HashSet<String> stepsRemaining = new HashSet<>();
        private HashSet<String> completedSteps = new HashSet<>();
        private HashSet<String> stepsInProgress = new HashSet<>();
        private String stepOrder = "";

        public SledBuildScheduler(List<Dependency> dependencies) {
            for (Dependency d : dependencies) {
                stepToDependencies.put(d.step, d.dependency);

                // keep track of all possible steps and dependencies
                stepsRemaining.add(d.step);
                stepsRemaining.add(d.dependency);
            }
        }

        public String getStepOrder() {
            return stepOrder;
        }

        public boolean isComplete() {
            return stepsRemaining.size() == 0 && stepsInProgress.size() == 0;
        }

        public void complete(String step) {
            stepsInProgress.remove(step);
            completedSteps.add(step);
        }

        public Optional<String> nextStep() {
            assert !isComplete();

            TreeSet<String> stepsWithFufilledDependencies = new TreeSet<>();
            for (String step : stepsRemaining) {
                if (completedSteps.containsAll(stepToDependencies.get(step))) {
                    stepsWithFufilledDependencies.add(step);
                }
            }
            if (stepsWithFufilledDependencies.isEmpty())
                return Optional.empty();
            String stepToComplete = stepsWithFufilledDependencies.first();
            stepOrder += stepToComplete;
            stepsRemaining.remove(stepToComplete);
            stepsInProgress.add(stepToComplete);

            return Optional.of(stepToComplete);
        }

        /** run to completion */
        public String build() {
            while (!isComplete())
                complete(nextStep().get());
            return getStepOrder();
        }
    }

    public static void main(String[] args) throws IOException {
        Path dependencyDataFile = Paths.get("data", "day7.txt");
        List<Dependency> dependencies = Dependency.loadFile(dependencyDataFile);
        var scheduler = new SledBuildScheduler(dependencies);
        System.out.println(scheduler.build());
    }
}
