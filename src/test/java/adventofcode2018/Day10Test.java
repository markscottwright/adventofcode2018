package adventofcode2018;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.Test;

public class Day10Test {

    @Test
    public void runExistsWorks() {
        TreeSet<Integer> nums = new TreeSet<>();
        nums.add(1);
        nums.add(2);
        nums.add(3);
        nums.add(4);
        assertFalse(Day10.runExists(nums, 5));
        nums.add(8);
        assertFalse(Day10.runExists(nums, 5));
        nums.add(5);
        assertTrue(Day10.runExists(nums, 5));
    }

    @Test
    public void testWithSampleData() {
        String sampleData = "position=< 9,  1> velocity=< 0,  2>\n"
                + "position=< 7,  0> velocity=<-1,  0>\n"
                + "position=< 3, -2> velocity=<-1,  1>\n"
                + "position=< 6, 10> velocity=<-2, -1>\n"
                + "position=< 2, -4> velocity=< 2,  2>\n"
                + "position=<-6, 10> velocity=< 2, -2>\n"
                + "position=< 1,  8> velocity=< 1, -1>\n"
                + "position=< 1,  7> velocity=< 1,  0>\n"
                + "position=<-3, 11> velocity=< 1, -2>\n"
                + "position=< 7,  6> velocity=<-1, -1>\n"
                + "position=<-2,  3> velocity=< 1,  0>\n"
                + "position=<-4,  3> velocity=< 2,  0>\n"
                + "position=<10, -3> velocity=<-1,  1>\n"
                + "position=< 5, 11> velocity=< 1, -2>\n"
                + "position=< 4,  7> velocity=< 0, -1>\n"
                + "position=< 8, -2> velocity=< 0,  1>\n"
                + "position=<15,  0> velocity=<-2,  0>\n"
                + "position=< 1,  6> velocity=< 1,  0>\n"
                + "position=< 8,  9> velocity=< 0, -1>\n"
                + "position=< 3,  3> velocity=<-1,  1>\n"
                + "position=< 0,  5> velocity=< 0, -1>\n"
                + "position=<-2,  2> velocity=< 2,  0>\n"
                + "position=< 5, -2> velocity=< 1,  2>\n"
                + "position=< 1,  4> velocity=< 2,  1>\n"
                + "position=<-2,  7> velocity=< 2, -2>\n"
                + "position=< 3,  6> velocity=<-1, -1>\n"
                + "position=< 5,  0> velocity=< 1,  0>\n"
                + "position=<-6,  0> velocity=< 2,  0>\n"
                + "position=< 5,  9> velocity=< 1, -2>\n"
                + "position=<14,  7> velocity=<-2,  0>\n"
                + "position=<-3,  6> velocity=< 2, -1>\n";

        var pointsAndVelocities = Arrays.stream(sampleData.split("\n"))
                .map(Day10.PointAndVelocity::parse)
                .collect(Collectors.toList());
        assertTrue(Day10.findMessage(pointsAndVelocities, 5));
    }

}
