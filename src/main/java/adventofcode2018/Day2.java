package adventofcode2018;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * https://adventofcode.com/2018/day/1
 * 
 * @author wrightm
 *
 */
public class Day2 {
    public static void main(String[] args) throws IOException {

        int checksum = 0;
        try (var dataStream = new FileReader("data/day2.txt");) {
            checksum = checksum(dataStream);
        }

        System.out.println(checksum);
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
