package adventofcode2018;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day14 {
    public static class PatternFinder {

        private List<Integer> pattern;
        private int previousStart;
        public int foundAt = -1;

        public PatternFinder(Integer... pattern) {
            this.pattern = Arrays.asList(pattern);
            previousStart = 0;
        }

        public boolean found(ArrayList<Integer> recipeScores) {
            for (int i = previousStart; i < recipeScores.size(); ++i) {
                if (equals(pattern, recipeScores, i)) {
                    foundAt = i;
                    return true;
                }
            }
            previousStart = Math.max(recipeScores.size() - pattern.size(), 0);
            return false;
        }

        private static boolean equals(List<Integer> fragment,
                ArrayList<Integer> list, int listStart) {
            for (int i = 0; i < fragment.size(); ++i)
                if (i + listStart >= list.size())
                    return false;
                else if (!fragment.get(i).equals(list.get(i + listStart)))
                    return false;
            return true;
        }
    }

    public static class RecipeBoard {

        ArrayList<Integer> recipeScores = new ArrayList<>();
        private int elf1Pos;
        private int elf2Pos;

        public RecipeBoard() {
            recipeScores.add(3);
            recipeScores.add(7);
            elf1Pos = 0;
            elf2Pos = 1;
        }

        public int numRecipeScores() {
            return recipeScores.size();
        }

        public void makeRecipes() {
            int score = recipeScores.get(elf1Pos) + recipeScores.get(elf2Pos);
            if (score >= 10) {
                recipeScores.add(score / 10);
                recipeScores.add(score % 10);
            } else
                recipeScores.add(score % 10);
            elf1Pos = (elf1Pos + 1 + recipeScores.get(elf1Pos))
                    % recipeScores.size();
            elf2Pos = (elf2Pos + 1 + recipeScores.get(elf2Pos))
                    % recipeScores.size();
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < recipeScores.size(); ++i) {
                if (i == elf1Pos)
                    s.append(String.format("(%d)", recipeScores.get(i)));
                else if (i == elf2Pos)
                    s.append(String.format("[%d]", recipeScores.get(i)));
                else
                    s.append(String.format(" %d ", recipeScores.get(i)));
            }
            return s.toString();
        }

        public ArrayList<Integer> getRecipeScores() {
            return recipeScores;
        }

    }

    public static void main(String[] args) {
        int n = 513411;
        var board = new RecipeBoard();
        while (board.numRecipeScores() < n) {
            board.makeRecipes();
        }
        ArrayList<Integer> scores = board.getRecipeScores();
        System.out.println("Part 1:");
        for (int i = 513401; i < 513401 + 10; ++i) {
            System.out.print(scores.get(i));
        }
        System.out.println();

        System.out.println("Part 2:");
        var finder = new PatternFinder(5, 1, 3, 4, 0, 1);
        while (!finder.found(board.getRecipeScores())) {
            board.makeRecipes();
        }
        System.out.println(finder.foundAt);
    }
}
