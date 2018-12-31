package adventofcode2018;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day3 {

    static public class Claim {
        public Claim(String id, int bottomX, int bottomY, int topX, int topY) {
            this.id = id;
            this.bottomX = bottomX;
            this.bottomY = bottomY;
            this.topX = topX;
            this.topY = topY;
        }

        @Override
        public String toString() {
            return "Claim [id=" + id + ", bottomX=" + bottomX + ", bottomY="
                    + bottomY + ", topX=" + topX + ", topY=" + topY + "]";
        }

        final String id;
        final int bottomX;
        final int bottomY;
        final int topX;
        final int topY;

        /**
         * Increment the usage count for this Claim on the sheet and return the
         * number of blocks that this claim overlaps with another claim, but
         * only the first time.
         * 
         * @param sheet
         * @return
         */
        public int recordUsageAndReturnOverlap(short[][] sheet) {
            int overlap = 0;
            for (int i = bottomX; i < topX; ++i) {
                for (int j = bottomY; j < topY; ++j) {
                    // only record overlap the first time it occurs
                    if (sheet[i][j] == 1)
                        overlap++;
                    sheet[i][j]++;
                }
            }

            return overlap;
        }

        /**
         * Is the area taken by the current Claim only marked once on sheet?
         * Then it must not overlap with any other claim.
         * 
         * @param sheet sheet already marked by all claims
         * @return Is the area for the claim marked one and only one time?
         */
        public boolean areaOnlyMarkedOnce(short[][] sheet) {
            for (int i = bottomX; i < topX; ++i) {
                for (int j = bottomY; j < topY; ++j) {
                    if (sheet[i][j] != 1)
                        return false;
                }
            }
            return true;
        }
    }

    private static Pattern CLAIM_PATTERN = Pattern
            .compile("#([0-9]+)" + "\\s*@\\s*" + "([0-9]+)\\s*,\\s*([0-9]+)"
                    + "\\s*:\\s*" + "([0-9]+)\\s*x\\s*([0-9]+)");

    static Claim parse(String line) {
        var matcher = CLAIM_PATTERN.matcher(line);
        if (!matcher.matches())
            throw new InvalidParameterException("couldnt match:" + line);
        int bottomX = Integer.parseInt(matcher.group(2));
        int bottomY = Integer.parseInt(matcher.group(3));
        int topX = bottomX + Integer.parseInt(matcher.group(4));
        int topY = bottomY + Integer.parseInt(matcher.group(5));
        return new Claim(matcher.group(1), bottomX, bottomY, topX, topY);
    }

    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        List<Claim> claims = Files.lines(Paths.get("data", "day3.txt"))
                .map(Day3::parse).collect(Collectors.toList());

        short[][] sheet = buildEmptySheet(claims);
        Arrays.toString(sheet);

        int overlap = 0;
        for (Claim claim : claims) {
            overlap += claim.recordUsageAndReturnOverlap(sheet);
        }
        System.out.println(overlap);
    }

    static short[][] buildEmptySheet(List<Claim> claims) {
        int width = 0;
        int height = 0;
        for (Claim claim : claims) {
            width = Math.max(width, claim.topX);
            height = Math.max(height, claim.topY);
        }

        return new short[width][height];
    }
}
