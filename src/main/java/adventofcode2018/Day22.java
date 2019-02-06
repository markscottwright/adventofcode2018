package adventofcode2018;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.javatuples.Pair;

public class Day22 {
    enum RegionType {
        rocky(0), wet(1), narrow(2);

        private final int riskLevel;

        RegionType(int riskLevel) {
            this.riskLevel = riskLevel;
        }

        int getRiskLevel() {
            return riskLevel;
        }
    };

    enum Equipment {
        TORCH, CLIMBING_GEAR, NOTHING;

        Equipment change(Step move) {
            if (move.equals(Step.EQUIP_CLIMBING_GEAR))
                return CLIMBING_GEAR;
            else if (move.equals(Step.EQUIP_TORCH))
                return TORCH;
            else if (move.equals(Step.EQUIP_NOTHING))
                return NOTHING;
            else
                return this;
        }
    }

    enum Step {
        MOVE_UP, MOVE_RIGHT, MOVE_DOWN, MOVE_LEFT, EQUIP_TORCH, EQUIP_CLIMBING_GEAR, EQUIP_NOTHING;

        static int weight(ArrayList<Step> steps) {
            return steps.stream().mapToInt(s -> Step.weight(s)).sum();
        }

        static int weight(Step s) {
            switch (s) {
            case MOVE_LEFT:
            case MOVE_DOWN:
            case MOVE_UP:
            case MOVE_RIGHT:
                return 1;
            default:
                return 7;
            }
        }
    }

    static class CavePosition extends adventofcode2018.Point {

        public CavePosition(int x, int y) {
            super(x, y);
        }

        private CavePosition doMove(Step move) {
            switch (move) {
            case MOVE_DOWN:
                return new CavePosition(x, y + 1);
            case MOVE_LEFT:
                return new CavePosition(x - 1, y);
            case MOVE_RIGHT:
                return new CavePosition(x + 1, y);
            case MOVE_UP:
                return new CavePosition(x, y - 1);
            default:
                return this;
            }
        }
    }

    static class Cave {
        final private HashMap<CavePosition, Integer> geologicIndices;
        final private int goalX;
        final private int goalY;
        final private int depth;

        public Cave(int goalX, int goalY, int depth) {
            this.goalX = goalX;
            this.goalY = goalY;
            this.depth = depth;

            geologicIndices = new HashMap<>();
            geologicIndices.put(new CavePosition(0, 0), 0);
            geologicIndices.put(new CavePosition(goalX, goalY), 0);
            for (int x = 0; x <= goalX; x++)
                geologicIndices.put(new CavePosition(x, 0), x * 16807);
            for (int y = 0; y <= goalY; ++y)
                geologicIndices.put(new CavePosition(0, y), y * 48271);

            for (int x = 1; x <= goalX; x++) {
                for (int y = 1; y <= goalY; y++) {
                    if (x == goalX && y == goalY)
                        break;

                    geologicIndices.put(new CavePosition(x, y),
                            erosionLevel(x - 1, y) * erosionLevel(x, y - 1));
                }
            }
        }

        int erosionLevel(int x, int y) {
            return (getGeologicIndex(x, y) + this.depth) % 20183;
        }

        private Integer getGeologicIndex(int x, int y) {
            CavePosition point = new CavePosition(x, y);
            if (!geologicIndices.containsKey(point)) {
                if (x == 0)
                    geologicIndices.put(point, y * 48271);
                else if (y == 0)
                    geologicIndices.put(point, x * 16807);
                else
                    geologicIndices.put(point,
                            erosionLevel(x - 1, y) * erosionLevel(x, y - 1));
            }
            return geologicIndices.get(point);
        }

        public RegionType getType(int x, int y) {
            int valMod3 = erosionLevel(x, y) % 3;
            if (valMod3 == 0)
                return RegionType.rocky;
            else if (valMod3 == 1)
                return RegionType.wet;
            else
                return RegionType.narrow;
        }

        public int totalRiskLevel() {
            return geologicIndices.keySet().stream().map(p -> getType(p.x, p.y))
                    .mapToInt(RegionType::getRiskLevel).sum();
        }

        /**
         * Find minimum series of steps from 0,0 with a TORCH to goal position
         * with a TORCH
         * 
         * @return
         */
        Path findPath() {
            final CavePosition goalPosition = new CavePosition(goalX, goalY);
            final Equipment goalEquipment = Equipment.TORCH;

            HashMap<Pair<CavePosition, Equipment>, Path> shortedPathAtPoint = new HashMap<>();
            PriorityQueue<Path> toConsider = new PriorityQueue<>(
                    (p1, p2) -> Integer.compare(p1.weight, p2.weight));
            Set<Path> solutions = new HashSet<>();

            // add initial state
            Path emptyPath = new Path();
            shortedPathAtPoint.put(
                    Pair.with(emptyPath.position, emptyPath.equipment),
                    emptyPath);
            toConsider.add(emptyPath);

            while (!toConsider.isEmpty()) {
                Path p = toConsider.remove();
                if (!solutions.isEmpty()) {

                    // we have a solution and its better than any path remaining
                    // to consider
                    if (solutions.stream().anyMatch(s -> s.weight < p.weight)) {
                        break;
                    }
                }

                // got a solution
                if (p.equipment == goalEquipment
                        && p.position.equals(goalPosition)) {
                    solutions.add(p);
                } else {
                    // pick next move
                    for (Step move : Step.values()) {
                        Path nextPath = p.addStep(move);
                        CavePosition nextPos = nextPath.position;
                        Equipment nextEquipment = nextPath.equipment;

                        if (nextPos.x < 0 || nextPos.y < 0)
                            continue;

                        // no change
                        if (nextPos.equals(p.position)
                                && nextEquipment.equals(p.equipment))
                            continue;

                        // change not allowed
                        if (!equipmentAllowed(nextEquipment, p.position)
                                || !equipmentAllowed(nextEquipment, nextPos))
                            continue;

                        // this is a valid move - if makes a better path, add it
                        // to consideration list
                        var currentPathAtPoint = shortedPathAtPoint
                                .get(Pair.with(nextPos, nextEquipment));
                        if (currentPathAtPoint == null) {
                            shortedPathAtPoint.put(
                                    Pair.with(nextPos, nextEquipment),
                                    nextPath);
                            toConsider.add(nextPath);
                        } else if (currentPathAtPoint.weight > nextPath.weight) {
                            shortedPathAtPoint.replace(
                                    Pair.with(nextPos, nextEquipment),
                                    nextPath);
                            toConsider.add(nextPath);
                        }
                    }
                }
            }

            // return the first solution in the set - they should all have the
            // same weight
            return solutions.iterator().next();
        }

        /**
         * Is equipment allowed at position?
         */
        private boolean equipmentAllowed(Equipment equipment,
                CavePosition position) {
            if (getType(position.x, position.y) == RegionType.narrow) {
                return equipment.equals(Equipment.TORCH)
                        || equipment.equals(Equipment.NOTHING);
            } else if (getType(position.x, position.y) == RegionType.rocky) {
                return equipment.equals(Equipment.TORCH)
                        || equipment.equals(Equipment.CLIMBING_GEAR);
            } else {
                return equipment.equals(Equipment.CLIMBING_GEAR)
                        || equipment.equals(Equipment.NOTHING);
            }
        }
    }

    /**
     * I represent a series of steps from 0,0 with a TORCH to a
     * (position,equipment).
     * 
     * @author mwright
     *
     */
    static class Path {
        @Override
        public String toString() {
            return "Path [weight=" + weight + ", position=" + position
                    + ", equipment=" + equipment + ", steps=" + steps + "]";
        }

        final ArrayList<Step> steps;
        final int weight;
        final Equipment equipment;
        final CavePosition position;

        Path() {
            steps = new ArrayList<>();
            weight = 0;

            this.equipment = Equipment.TORCH;
            this.position = new CavePosition(0, 0);

        }

        private Path(ArrayList<Step> steps, int weight) {
            this.steps = steps;
            this.weight = weight;
            Equipment equipment = Equipment.TORCH;
            CavePosition pos = new CavePosition(0, 0);
            for (Step step : steps) {
                equipment = equipment.change(step);
                pos = pos.doMove(step);
            }
            this.equipment = equipment;
            this.position = pos;
        }

        Path addStep(Step step) {
            var newSteps = new ArrayList<>(steps);
            newSteps.add(step);
            int newWeight = weight + Step.weight(step);
            return new Path(newSteps, newWeight);
        }
    }

    public static void main(String[] args) {
        var cave = new Cave(14, 709, 6084);
        System.out.println("Part one: " + cave.totalRiskLevel());

        Path path = cave.findPath();
        System.out.println("Part two: " + path);
        System.out.println("Part two: " + path.weight);
    }
}
