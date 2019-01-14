package adventofcode2018;

import java.io.StringWriter;
import java.util.Arrays;

public class Day11 {

    public static class FuelCellGrid {
        // x axis is first
        int[][] cells = new int[300][300];

        public FuelCellGrid(int serialNumber) {
            for (int x = 0; x < 300; ++x)
                for (int y = 0; y < 300; ++y)
                    cells[x][y] = powerLevel(serialNumber, x + 1, y + 1);
        }

        int powerIn3x3(int x, int y) {
            int power = 0;
            for (int x2 = x; x2 < x + 3; x2++) {
                for (int y2 = y; y2 < y + 3; ++y2) {
                    power += cells[x2][y2];
                }
            }
            return power;
        }

        int powerInSquare(int x, int y, int edgeLen) {
            int power = 0;
            for (int x2 = x; x2 < x + edgeLen; x2++) {
                for (int y2 = y; y2 < y + edgeLen; ++y2) {
                    power += cells[x2][y2];
                }
            }
            return power;
        }

        public static int powerLevel(int serialNumber, int x, int y) {
            int rackId = x + 10;
            int powerLevel = rackId * y;
            powerLevel += serialNumber;
            powerLevel *= rackId;
            powerLevel = hundredthsDigit(powerLevel);
            powerLevel -= 5;
            return powerLevel;
        }

        public static int hundredthsDigit(int n) {
            return Math.abs((n / 100) % 10);
        }

        public int[] maxPowerSquare() {
            int maxPowerX = 0;
            int maxPowerY = 0;
            int maxPowerZ = 0;
            int maxPower = Integer.MIN_VALUE;
            for (int x = 0; x < 300 - 1; ++x) {
                System.out.print('.');
                for (int y = 0; y < 300 - 1; ++y) {
                    int largestPossibleSquareEdge = 300 - Math.max(x, y);
                    for (int z = 1; z < largestPossibleSquareEdge; ++z) {
                        int squarePower = powerInSquare(x, y, z);
                        if (squarePower > maxPower) {
                            maxPowerX = x;
                            maxPowerY = y;
                            maxPowerZ = z;
                            maxPower = squarePower;
                        }
                    }
                }
            }

            System.out.print("x,y,z = " + (maxPowerX + 1) + ","
                    + (maxPowerY + 1) + "," + maxPowerZ + ", power = ");
            System.out.println(maxPower);
            return new int[] { maxPowerX + 1, maxPowerY + 1, maxPowerZ };
        }

        public int[] maxPower3x3() {
            int maxPowerX = 0;
            int maxPowerY = 0;
            int maxPower = Integer.MIN_VALUE;
            for (int x = 0; x < 300 - 3; ++x) {
                for (int y = 0; y < 300 - 3; ++y) {
                    int squarePower = powerInSquare(x, y, 3);
                    if (squarePower > maxPower) {
                        maxPowerX = x;
                        maxPowerY = y;
                        maxPower = squarePower;
                    }
                }
            }

            System.out.print("x,y = " + (maxPowerX + 1) + "," + (maxPowerY + 1)
                    + ", power = ");
            System.out.println(maxPower);
            return new int[] { maxPowerX + 1, maxPowerY + 1 };
        }

        public String gridString() {
            StringWriter s = new StringWriter();
            for (int x = 0; x < 300; ++x) {
                for (int y = 0; y < 300; ++y) {
                    s.append(String.format("%3d", cells[x][y]));
                }
                s.append("\n");
            }
            return s.toString();
        }
    }

    public static void main(String[] args) {
        int[] xyOfMaxPower3x3 = new FuelCellGrid(4172).maxPower3x3();
        System.out.println("part 1 = " + Arrays.toString(xyOfMaxPower3x3));
        int[] xyzOfMaxPowerSquare = new FuelCellGrid(4172).maxPowerSquare();
        System.out.println("part 2 = " + Arrays.toString(xyzOfMaxPowerSquare));
        
        
    }
}
