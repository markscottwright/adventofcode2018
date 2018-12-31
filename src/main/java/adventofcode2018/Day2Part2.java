package adventofcode2018;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * https://adventofcode.com/2018/day/2#part2
 * 
 * @author wrightm
 *
 */
public class Day2Part2 {
    public static void main(String[] args) throws IOException {

        List<String> boxIds = new ArrayList<>();
        try (var dataStream = new FileReader("data/day2.txt");
                var data = new BufferedReader(dataStream)) {
            String line = null;
            while ((line = data.readLine()) != null)
                boxIds.add(line);
        }

        // find the two box ids that only differ by one character
        String boxId1 = null;
        String boxId2 = null;
        Outer: for (int i = 0; i < boxIds.size(); ++i) {
            for (int j = i + 1; j < boxIds.size(); ++j) {
                if (differByOne(boxIds.get(i), boxIds.get(j))) {
                    boxId1 = boxIds.get(i);
                    boxId2 = boxIds.get(j);
                    break Outer;
                }
            }
        }

        // print out only common characters
        for (int i = 0; i < boxId1.length(); ++i)
            if (boxId1.charAt(i) == boxId2.charAt(i))
                System.out.print(boxId1.charAt(i));
    }

    static boolean differByOne(String string, String string2) {
        int differences = 0;
        for (int i = 0; i < string.length() && differences < 2; ++i) {
            if (string.charAt(i) != string2.charAt(i))
                differences++;
        }
        return differences == 1;
    }

    static int checksum(Reader dataStream) throws IOException {

        try (var data = new BufferedReader(dataStream);) {
            Set<String> contains2 = new HashSet<>();
            Set<String> contains3 = new HashSet<>();

            String line;
            while ((line = data.readLine()) != null) {
                if (boxIdContains(line, 2))
                    contains2.add(line);
                if (boxIdContains(line, 3))
                    contains3.add(line);
            }

            return contains2.size() * contains3.size();
        }
    }

    public static boolean boxIdContains(String line, int desiredCount) {
        Multiset<Character> charCounts = HashMultiset.create();
        for (char c : line.toCharArray()) {
            charCounts.add(c);
        }

        for (var charCount : charCounts.entrySet()) {
            if (charCount.getCount() == desiredCount) {
                return true;
            }
        }
        return false;
    }
}
