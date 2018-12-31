package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.javatuples.Pair;

import adventofcode2018.Day5.Polymer;

public class Day5Part2 {
    public static void main(String[] args) throws IOException {
        String componentsString = new String(
                Files.readAllBytes(Paths.get("data", "day5.txt"))).trim();

        ArrayList<Pair<String, Integer>> componentCounts = new ArrayList<>();
        for (char problematicComponent : "abcdefghijklmnopqrstuvwxyz"
                .toCharArray()) {
            String regex = "[" + problematicComponent
                    + Character.toUpperCase(problematicComponent) + "]";
            Polymer polymer = new Day5.Polymer(componentsString
                    .replaceAll("[" + new String(regex) + "]", ""));
            polymer.removeComponentsWithOppositePolarity();
            Pair<String, Integer> removedAndCount = new Pair<>(regex,
                    polymer.numComponents());
            System.out.println(removedAndCount);
            componentCounts.add(removedAndCount);
        }

        var smallestCount = componentCounts.stream()
                .min((c1, c2) -> c1.getValue1().compareTo(c2.getValue1()))
                .get();
        System.out.println("\nmin = " + smallestCount);
    }
}
