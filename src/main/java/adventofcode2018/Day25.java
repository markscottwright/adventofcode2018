package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Day25 {
    public static class Constellation extends HashSet<Position> {

        public Constellation(Position pos) {
            add(pos);
        }

        public void addConnected(HashSet<Position> remaining) {
            for (Position pos : this) {
                addConnected(pos, remaining);
            }
        }

        private void addConnected(Position pos, HashSet<Position> remaining) {
            HashSet<Position> justAdded = new HashSet<>();
            for (Position maybeConnected : remaining) {
                if (pos.distance(maybeConnected) <= 3) {
                    add(maybeConnected);
                    justAdded.add(maybeConnected);
                }
            }

            remaining.removeAll(justAdded);
            for (Position pos2 : justAdded) {
                addConnected(pos2, remaining);
            }
        }

        static HashSet<Constellation> build(Set<Position> points) {
            HashSet<Constellation> constellations = new HashSet<>();
            HashSet<Position> remaining = new HashSet<>(points);
            while (remaining.size() > 0) {
                Position next = remaining.iterator().next();
                remaining.remove(next);
                Constellation constellation = new Constellation(next);
                constellation.addConnected(remaining);
                constellations.add(constellation);
            }
            return constellations;
        }
    }

    /**
     * A 4D position in space, measuring distances in "manhattan" measurements
     * 
     * @author mwright
     *
     */
    public static class Position {

        final int x;
        final int y;
        final int z;
        final int t;

        public Position(int x, int y, int z, int t) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.t = t;
        }

        int distance(Position o) {
            int distance = Math.abs(x - o.x) + Math.abs(y - o.y)
                    + Math.abs(z - o.z) + Math.abs(t - o.t);
            return distance;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + t;
            result = prime * result + x;
            result = prime * result + y;
            result = prime * result + z;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Position other = (Position) obj;
            if (t != other.t)
                return false;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            if (z != other.z)
                return false;
            return true;
        }

        static Position parse(String line) {
            String[] fields = line.split(",");
            return new Position(Integer.valueOf(fields[0].trim()),
                    Integer.valueOf(fields[1].trim()),
                    Integer.valueOf(fields[2].trim()),
                    Integer.valueOf(fields[3].trim()));
        }

        @Override
        public String toString() {
            return "Position [x=" + x + ", y=" + y + ", z=" + z + ", t=" + t
                    + "]";
        }
    }

    public static void main(String[] args) throws IOException {
        Set<Position> points = Files.readAllLines(Paths.get("data", "day25.txT")).stream()
                .map(Position::parse).collect(Collectors.toSet());
        
        System.out.println("part one: " + Constellation.build(points).size());
    }
}
