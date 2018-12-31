package adventofcode2018;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import adventofcode2018.Day3.Claim;

public class Day3Part2 {

    public static void main(String[] args)
            throws FileNotFoundException, IOException {
        List<Claim> claims = Files.lines(Paths.get("data", "day3.txt"))
                .map(Day3::parse).collect(Collectors.toList());

        short[][] sheet = Day3.buildEmptySheet(claims);

        // record all overlap areas
        for (Claim claim : claims) {
            claim.recordUsageAndReturnOverlap(sheet);
        }
        
        for (Claim claim : claims) {
            if (claim.areaOnlyMarkedOnce(sheet))
                System.out.println(claim);
        }
    }
}
