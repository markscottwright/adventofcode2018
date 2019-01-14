package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;

public class Day12 {

    static class PlantRow {
        @Override
        public String toString() {
            return "PlantRow [state=" + state + ", rules=" + rules
                    + ", firstPlantNumber=" + firstPlantNumber + "]";
        }

        String state = "";
        Map<String, String> rules;
        int firstPlantNumber = 0;

        public PlantRow(Map<String, String> rules, String initialState) {
            state = initialState;
            this.rules = rules;
        }

        public String getState() {
            return state;
        }

        public int getFirstPlantNumber() {
            return firstPlantNumber;
        }

        private void trim() {
            while (firstPlantNumber < 0 && state.charAt(0) == '.') {
                firstPlantNumber++;
                state = state.substring(1);
            }
        }

        // #....
        // ....#..
        //
        public void updateState() {
            String nextState = "";

            firstPlantNumber -= 2;
            String tempState = "...." + this.state + "....";
            for (int i = 2; i < tempState.length() - 2; ++i)
                nextState += rules.get(tempState.substring(i - 2, i + 3));
            this.state = nextState;
            trim();
        }

        public int stateVal() {
            int val = 0;
            int pos = firstPlantNumber;
            for (Character c : state.toCharArray()) {
                if (c == '#')
                    val += pos;
                pos++;
            }
            return val;
        }

        public int stateValFrom0ToN(int n) {
            int val = 0;
            int pos = Math.abs(firstPlantNumber);
            for (int i = pos; i < n; ++i) {
                if (state.charAt(i) == '#')
                    val += i;
            }
            return val;
        }

        public void setState(String string) {
            this.state = string;
        }

        public Map<String, String> getRules() {
            return rules;
        }
    }

    public static void main(String[] args) throws IOException {
        Path inputFile = Paths.get("data", "day12.txt");
        var plants = parse(inputFile);
        var initialState = plants.getState();
        for (int i = 0; i < 20; ++i) {
            plants.updateState();
        }
        System.out.println("part 1 = " + plants.stateVal());

        var plants2 = new PlantRow(plants.getRules(), initialState);
        int lastVal = plants2.stateVal();
        for (int i = 0; i < 1000; ++i) {
            System.out.print("part 2 : " + i + " = " + plants2.stateVal());
            plants2.updateState();
            System.out.println(" diff = " + (plants2.stateVal() - lastVal));
            lastVal = plants2.stateVal();
        }
        
        long n = 50_000_000_000L;
        System.out.println((n - 150) * 102 + 16677);
    }

    static PlantRow parse(Path inputFile) throws IOException {
        TreeMap<String, String> rules = new TreeMap<>();
        String initialState = "";
        for (String s : Files.readAllLines(inputFile)) {
            if (s.startsWith("initial state: ")) {
                initialState = s.substring("initial state: ".length()).trim();
            } else if (s.contains("=>")) {
                rules.put(s.substring(0, 5), s.substring(9));
            }
        }

        PlantRow plants = new PlantRow(rules, initialState);
        return plants;
    }
}
