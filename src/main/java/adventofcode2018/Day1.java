package adventofcode2018;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * https://adventofcode.com/2018/day/1
 * @author wrightm
 *
 */
public class Day1 {
    public static void main(String[] args) throws IOException {

        long freqency = 0;
        try (var dataStream = new FileReader("data/day1.txt");
                var data = new BufferedReader(dataStream);) {
            String line;
            while ((line = data.readLine()) != null)
                freqency += Long.parseLong(line);
        }
        System.out.println(freqency);
    }
}
