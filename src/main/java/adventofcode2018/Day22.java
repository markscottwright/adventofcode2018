package adventofcode2018;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

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
        MOVE_UP, MOVE_RIGHT, MOVE_DOWN, MOVE_LEFT, EQUIP_TORCH,
        EQUIP_CLIMBING_GEAR, EQUIP_NOTHING;

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
    
    static class Point extends adventofcode2018.Point {

        public Point(int x, int y) {
            super(x, y);
        }
        
        private Point doMove(Step move) {
            switch (move) {
            case MOVE_DOWN:
                return new Point(x, y + 1);
            case MOVE_LEFT:
                return new Point(x - 1, y);
            case MOVE_RIGHT:
                new Point(x + 1, y);
            case MOVE_UP:
                return new Point(x, y - 1);
            default:
                return this;
            }
        }
    }

    static class Cave {
        final private HashMap<Point, Integer> geologicIndices;
        final private int maxX;
        final private int maxY;
        final private int depth;

        public Cave(int maxX, int maxY, int depth) {
            this.maxX = maxX;
            this.maxY = maxY;
            this.depth = depth;

            geologicIndices = new HashMap<>();
            geologicIndices.put(new Point(0, 0), 0);
            geologicIndices.put(new Point(maxX, maxY), 0);
            for (int x = 0; x <= maxX; x++)
                geologicIndices.put(new Point(x, 0), x * 16807);
            for (int y = 0; y <= maxY; ++y)
                geologicIndices.put(new Point(0, y), y * 48271);

            for (int x = 1; x <= maxX; x++) {
                for (int y = 1; y <= maxY; y++) {
                    if (x == maxX && y == maxY)
                        break;

                    geologicIndices.put(new Point(x, y),
                            erosionLevel(x - 1, y) * erosionLevel(x, y - 1));
                }
            }
        }

        int erosionLevel(int x, int y) {
            return (getGeologicIndex(x, y) + this.depth) % 20183;
        }

        private Integer getGeologicIndex(int x, int y) {
            Point point = new Point(x, y);
            if (!geologicIndices.containsKey(point))
                geologicIndices.put(point,
                        erosionLevel(x - 1, y) * erosionLevel(x, y - 1));
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

        Path findPath() {

            HashMap<Pair<Point, Equipment>, Path> shortedPathAtPoint = new HashMap<>();
            Path emptyPath = new Path();
            shortedPathAtPoint.put(Pair.with(emptyPath.getFinalPoint(), emptyPath.getEquipment()),
                    emptyPath);

            PriorityQueue<Day22.Path> toConsider = new PriorityQueue<>(comparator) 
            // found a path
            if (pos.equals(new Point(maxX, maxY))
                    && equipped.equals(Equipment.TORCH))
                return stepsSoFar;

            // pick next move
            for (Step move : Step.values()) {
                Point nextPos = doMove(move, pos);
                Equipment nextEquipment = changeEquipment(move, equipped);

                if (nextPos.x < 0 || nextPos.y < 0)
                    continue;
                if (nextPos.equals(pos) && nextEquipment.equals(equipped))
                    continue;
                if (!equipmentAllowed(nextEquipment, pos)
                        || !equipmentAllowed(nextEquipment, nextPos))
                    continue;
            }
        }

        

        

        private boolean equipmentAllowed(Equipment value1, Point position) {
            if (getType(position.x, position.y) == RegionType.narrow) {
                return value1.equals(Equipment.TORCH)
                        || value1.equals(Equipment.NOTHING);
            } else if (getType(position.x, position.y) == RegionType.rocky) {
                return value1.equals(Equipment.TORCH)
                        || value1.equals(Equipment.CLIMBING_GEAR);
            } else {
                return value1.equals(Equipment.CLIMBING_GEAR)
                        || value1.equals(Equipment.NOTHING);
            }
        }
    }

    static class Path {
        final ArrayList<Step> steps;
        final int weight;
    
        Path() {
            steps = new ArrayList<>();
            weight = 0;
        }
    
        public Object getEquipment() {
            // TODO Auto-generated method stub
            return null;
        }

        public Object getFinalPoint() {
            // TODO Auto-generated method stub
            return null;
        }

        private Path(ArrayList<Step> steps, int weight) {
            this.steps = steps;
            this.weight = weight;
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

        // part two:
        // Breadth-first search? Order next choice by weight of move into space,
        // nearness of
        // map<tuple<Point,Equipped>, Route> metadata;
        // route = findRoute(cave, point, equipped, metadata)
        // if point == endPoint && equipment == torch)
        // return metadata.get(point, equipment)
        // for step in (moveUp, moveRight, moveDown, moveLeft, equipTorch,
        // equipClimbingGear, equipNothing)
        // potentialPosition = move.getPosition(p)
        // potentialEquipment = move.getEquipment()
        // if isAllowed(p, potentialEquipment) && isAllowed(potentialPosition)
        // && isAllowed(potentialPosition, potentialEquipment)
        // moves.add(step)
        // sortMoves
        // for move in moves:
        // if betterMove(): metadata(potentialPosition, potentialEquipment) =
        // metadata(p, equipped) + move
        // findPath(cave, move.getPoint(p), move.getEquipment(), metadata)
    }
}
