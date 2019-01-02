package adventofcode2018;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import adventofcode2018.Day7.SledBuildScheduler;

public class Day7Part2 {
    public static class ElfTeam {

        private SledBuildScheduler builder;
        private int numWorkers;

        public ElfTeam(SledBuildScheduler builder, int numWorkers) {
            this.builder = builder;
            this.numWorkers = numWorkers;
        }

        public int build() {
            int second = 0;
            HashMap<String, Integer> stepsInProgress = new HashMap<>();
            while (!builder.isComplete() || stepsInProgress.size() != 0) {
                System.out.println("Second = " + second);

                // add new steps
                while (!builder.isComplete()
                        && stepsInProgress.size() < numWorkers) {
                    
                    // no steps to schedule right now
                    Optional<String> nextStep = builder.nextStep();
                    if (!nextStep.isPresent())
                        break;

                    System.out.println("Started: " + nextStep.get());
                    stepsInProgress.put(nextStep.get(),
                            secondsToComplete(nextStep.get()));
                }
                System.out
                        .println("Steps being worked on = " + stepsInProgress);

                second++;

                // work on each step
                for (String stepInProgress : stepsInProgress.keySet()) {
                    stepsInProgress.put(stepInProgress,
                            stepsInProgress.get(stepInProgress) - 1);
                }

                // complete any steps with remaining work = 0
                Set<String> completedSteps = stepsInProgress.keySet().stream()
                        .filter(s -> stepsInProgress.get(s) == 0)
                        .collect(Collectors.toSet());
                System.out.println("Steps completed = " + completedSteps);
                for (String step : completedSteps) {
                    builder.complete(step);
                    stepsInProgress.remove(step);
                }
            }
            return second;
        }

        public int secondsToComplete(String nextStep) {
            return 60 + (nextStep.charAt(0) - 'A' + 1);
        }
    }

    public static void main(String[] args) throws IOException {
        var dependencies = Day7.Dependency
                .loadFile(Paths.get("data", "day7.txt"));
        SledBuildScheduler builder = new Day7.SledBuildScheduler(dependencies);
        ElfTeam elfTeam = new ElfTeam(builder, 5);

        System.out.println("Total seconds = " + elfTeam.build());
    }

}
