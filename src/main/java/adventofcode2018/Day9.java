package adventofcode2018;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

public class Day9 {

    /**
     * Keep marbles in a linked list, with the current marble always being the
     * first element in the list.
     * 
     * @author wrightm
     *
     */
    public static class MarbleCircle {
        private LinkedList<Long> marbles = new LinkedList<>();

        public long insertMarble(long marbleValue) {
            if (marbleValue == 0) {
                marbles.add(marbleValue);
                return 0;
            }

            else if (marbleValue % 23 == 0) {

                // move 7 marbles from the end to the first (aka move 7 marbles
                // counter-clockwise)
                for (int i = 0; i < 7; ++i) {
                    marbles.addFirst(marbles.removeLast());
                }
                Long removedMarbleValue = marbles.removeFirst();
                return marbleValue + removedMarbleValue;
            }

            else {
                // move 2 marbles from the first to the last (aka move 2 marbles
                // clockwise)
                marbles.addLast(marbles.removeFirst());
                marbles.addLast(marbles.removeFirst());
                marbles.addFirst(marbleValue);
                return 0;
            }
        }

        @Override
        public String toString() {
            StringWriter s = new StringWriter();
            boolean first = true;
            for (Long l : marbles) {
                if (first) {
                    s.append(String.format(" (%2ld)", l));
                } else {
                    s.append(String.format("  %2ld ", l));
                    first = false;
                }

            }
            return s.toString();
        }

        public List<Long> play(int numPlayers, int highestMarble) {
            marbles.clear();

            List<Long> playersScore = new ArrayList<>();
            for (int i = 0; i < numPlayers; ++i)
                playersScore.add(0L);

            int marbleValue = 0;
            int player = 0;
            while (marbleValue <= highestMarble) {
                playersScore.set(player,
                        playersScore.get(player) + insertMarble(marbleValue));

                marbleValue++;
                player = (player + 1) % numPlayers;
            }
            return playersScore;
        }

        public static Integer highestScorePlayer(List<Long> playersScore) {
            return IntStream.range(0, playersScore.size()).boxed()
                    .max((i1, i2) -> Long.compare(playersScore.get(i1),
                            playersScore.get(i2)))
                    .get();
        }

        public static Long highestScore(List<Long> playersScore) {
            return playersScore.get(highestScorePlayer(playersScore));
        }
    }

    public static void main(String[] args) {
        StopWatch w = new StopWatch();
        
        MarbleCircle c = new MarbleCircle();
        List<Long> scores = c.play(404, 71852);
        System.out.println("part 1 = " + MarbleCircle.highestScore(scores));
        System.out.println(w.stop());
        
        w.reset();
        scores = c.play(404, 71852 * 100);
        System.out.println("part 2 = " + MarbleCircle.highestScore(scores));
        System.out.println(w.stop());
    }
}
