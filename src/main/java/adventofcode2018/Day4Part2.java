package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.google.common.primitives.Ints;

public class Day4Part2 {
    public static void main(String[] args) throws IOException {
        var lineIterator = Files.lines(Paths.get("data", "day4.txt"))
                .iterator();
        var guardSchedule = Day4.buildPerGuardSchedule(lineIterator);

        // find the guard who is most asleep at one minute
        String maxGuardId = "";
        int maxSleepTime = 0;
        for (var guardId : guardSchedule.keySet()) {
            int guardsLargestMinutesAsleep = Arrays
                    .stream(guardSchedule.get(guardId)).max().getAsInt();
            if (guardsLargestMinutesAsleep > maxSleepTime) {
                maxSleepTime = guardsLargestMinutesAsleep;
                maxGuardId = guardId;
            }
        }

        int sleepiestMinute = Ints.indexOf(guardSchedule.get(maxGuardId),
                maxSleepTime);

        System.out.println(maxGuardId + "'s most time asleep " + maxSleepTime);
        System.out
                .println(maxGuardId + "'s sleepiest minute " + sleepiestMinute);
        System.out.print(maxGuardId + " * " + sleepiestMinute);
        System.out.println(
                " = " + Integer.parseInt(maxGuardId) * sleepiestMinute);
    }
}
