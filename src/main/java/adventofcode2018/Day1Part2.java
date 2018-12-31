package adventofcode2018;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * https://adventofcode.com/2018/day/1
 * 
 * @author wrightm
 *
 */
public class Day1Part2 {
    public static void main(String[] args) throws IOException {

        boolean duplicateFound = false;
        Long freqency = 0L;
        Set<Long> frequenciesSeen = new HashSet<>();
        while (!duplicateFound) {
            try (var dataStream = new FileReader("data/day1.txt");
                    var data = new BufferedReader(dataStream);) {
                String line;
                while ((line = data.readLine()) != null) {
                    freqency += Long.parseLong(line);
                    if (frequenciesSeen.contains(freqency)) {
                        System.out.println(freqency);
                        duplicateFound = true;
                        break;
                    }
                    frequenciesSeen.add(freqency);
                }
            }
        }
    }
}
