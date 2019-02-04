package adventofcode2018;

import java.util.HashMap;

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
    }

    public static void main(String[] args) {
        var cave = new Cave(14, 709, 6084);
        System.out.println("Part one: " + cave.totalRiskLevel());
        
        // part two:
        // Breadth-first search?  Order next choice by weight of move into space, nearness of
        // map<tuple<Point,Equipped>, Route> metadata;
        // route = findRoute(cave, point, equipped, metadata)
        // if point == endPoint && equipment == torch)
        //      return metadata.get(point, equipment)
        // for step in (moveUp, moveRight, moveDown, moveLeft, equipTorch, equipClimbingGear, equipNothing)
        //      potentialPosition = move.getPosition(p)
        //      potentialEquipment = move.getEquipment()
        //      if isAllowed(p, potentialEquipment) && isAllowed(potentialPosition) && isAllowed(potentialPosition, potentialEquipment) 
        //          moves.add(step)
        // sortMoves
        // for move in moves:
        //      if betterMove(): metadata(potentialPosition, potentialEquipment) = metadata(p, equipped) + move
        //     findPath(cave, move.getPoint(p), move.getEquipment(), metadata)
    }
}
