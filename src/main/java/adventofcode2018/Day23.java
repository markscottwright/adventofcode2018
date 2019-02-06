package adventofcode2018;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.iterators.IteratorChain;

/**
 * Definitely going about part two the wrong way. If I had looked at the input
 * data, it would be obvious that enumerating all points in range was not
 * feasible.
 * 
 * Check out the Separating Axis Theory
 * <p>
 * http://www.dyn4j.org/2010/01/sat/
 * <p>
 * https://en.wikipedia.org/wiki/Hyperplane_separation_theorem
 * 
 * @author mwright
 *
 */
public class Day23 {

    public static class Position {
        @Override
        public String toString() {
            return "Position [x=" + x + ", y=" + y + ", z=" + z + "]";
        }

        final int x;
        final int y;
        final int z;

        public Position(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        int distance(Position o) {
            return Math.abs(x - o.x) + Math.abs(y - o.y) + Math.abs(z - o.z);
        }
    }

    public static class Nanobot extends Position {
        @Override
        public String toString() {
            return "Nanobot [x=" + x + ", y=" + y + ", z=" + z
                    + ", signalRadius=" + signalRadius + "]";
        }

        final int signalRadius;

        public Nanobot(int x, int y, int z, int signalRadius) {
            super(x, y, z);
            this.signalRadius = signalRadius;
        }

        boolean inRange(Position o) {
            return distance(o) <= signalRadius;
        }

        PointsInRange pointsInRange() {
            return new PointsInRange(this);
        }

        public static Nanobot parse(String line) {
            Pattern pattern = Pattern
                    .compile("pos=<([0-9-]+),([0-9-]+),([0-9-]+)>, r=(\\d+)");
            Matcher matcher = pattern.matcher(line);
            matcher.matches();
            return new Nanobot(Integer.valueOf(matcher.group(1)),
                    Integer.valueOf(matcher.group(2)),
                    Integer.valueOf(matcher.group(3)),
                    Integer.valueOf(matcher.group(4)));
        }

        static class PointsInRange
                implements Iterable<Position>, Iterator<Position> {

            private int x, y, z;
            private Nanobot nanobot;

            PointsInRange(Nanobot b) {
                this.nanobot = b;
                this.x = b.x - b.signalRadius;
                this.y = b.y - b.signalRadius;
                this.z = b.z - b.signalRadius;

                while (!inRange())
                    if (!advance())
                        break;
            }

            private boolean inRange() {
                return nanobot.inRange(new Position(this.x, this.y, this.z));
            }

            /**
             * Advance to next allowed position, not necessarily in range.
             * Returns false at end of iteration.
             * 
             * TODO: this is inefficient.
             * 
             * @return
             */
            private boolean advance() {
                if (x == nanobot.signalRadius + nanobot.x
                        && y == nanobot.signalRadius + nanobot.y
                        && z == nanobot.signalRadius + nanobot.z)
                    return false;

                x += 1;
                if (x > nanobot.x + nanobot.signalRadius) {
                    x = nanobot.x - nanobot.signalRadius;
                    y += 1;
                    if (y > nanobot.y + nanobot.signalRadius) {
                        y = nanobot.y - nanobot.signalRadius;
                        z += 1;
                    }
                }
                return true;
            }

            @Override
            public Iterator<Position> iterator() {
                return this;
            }

            @Override
            public boolean hasNext() {
                return inRange();
            }

            @Override
            public Position next() {
                if (!inRange())
                    return null;
                Position position = new Position(x, y, z);

                // advance to next valid state
                do {
                    if (!advance())
                        break;
                } while (!inRange());
                return position;
            }

        }

    }

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("data", "day23.txt"));
        List<Nanobot> bots = lines.stream().map(Nanobot::parse)
                .collect(toList());

        Nanobot strongestSignal = bots.stream().max(
                (b1, b2) -> Integer.compare(b1.signalRadius, b2.signalRadius))
                .get();
        long numInRange = bots.stream().filter(strongestSignal::inRange)
                .count();
        System.out.println("Part one: " + numInRange);

        IteratorChain<Position> allInRangePositions = new IteratorChain<>();
        allInRangePositions.addIterator(bots.get(0).pointsInRange());
        allInRangePositions.addIterator(bots.get(1).pointsInRange());
        allInRangePositions.forEachRemaining(p -> System.out.println(p));
    }
}
