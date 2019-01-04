package adventofcode2018;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import adventofcode2018.Day9.MarbleCircle;

public class Day9Test {

    int counterClockwise(int ringSize, int currentPosition, int move) {
        move = move % ringSize;
        if (currentPosition >= move)
            return (currentPosition - move);
        else
            return ringSize - (move - currentPosition);
    }

    int clockwise(int ringSize, int currentPosition, int move) {
        return (currentPosition + move) % ringSize;
    }

    @Test
    public void testCounterClockwiseMethod() {
        assertEquals(1, counterClockwise(7, 2, 1));
        assertEquals(0, counterClockwise(7, 2, 2));
        assertEquals(6, counterClockwise(7, 2, 3));
        assertEquals(2, counterClockwise(7, 2, 7));
    }

    @Test
    public void testClockwiseMethod() {
        assertEquals(3, clockwise(7, 2, 1));
        assertEquals(6, clockwise(7, 2, 4));
        assertEquals(0, clockwise(7, 2, 5));
    }

    @Test
    public void testSampleData() {
        MarbleCircle game = new Day9.MarbleCircle();
        assertEquals(32L, (long) MarbleCircle.highestScore(game.play(9, 25)));
        assertEquals(8317L,
                (long) MarbleCircle.highestScore(game.play(10, 1618)));
        assertEquals(146373L,
                (long) MarbleCircle.highestScore(game.play(13, 7999)));
        assertEquals(2764L,
                (long) MarbleCircle.highestScore(game.play(17, 1104)));
        assertEquals(54718L,
                (long) MarbleCircle.highestScore(game.play(21, 6111)));
        assertEquals(37305L,
                (long) MarbleCircle.highestScore(game.play(30, 5807)));
    }

}
