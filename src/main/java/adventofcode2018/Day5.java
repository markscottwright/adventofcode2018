package adventofcode2018;

import static java.lang.Character.toUpperCase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class Day5 {
    static class Polymer {

        public String getPolymerString() {
            StringBuffer b = new StringBuffer();
            for (Character c : components) {
                b.append(c);
            }
            return b.toString();
        }

        @Override
        public String toString() {
            return "Polymer [components=" + components + "]";
        }

        // well, Stroustop says arrays are always faster than linked lists on
        // modern processors. Not sure if boxed java arrays qualify...
        final ArrayList<Character> components;
        private boolean trace = false;

        public Polymer(String components) {
            this.components = new ArrayList<Character>();
            for (char c : components.toCharArray())
                this.components.add(c);
            isValid();
        }
        
        public void isValid() {
            for (char c : components)
                if (!Character.isLetter(c))
                    throw new InvalidParameterException("Non letter data found: '" + c + "'");
        }

        public int numComponents() {
            return components.size();
        }

        public boolean destroyComponentAt(int i) {
            if (i < components.size() - 1) {
                char prev = components.get(i);
                char next = components.get(i + 1);
                if (toUpperCase(prev) == toUpperCase(next) && prev != next) {
                    if (trace ) {
                        System.out.println(String.format(
                                "Destroying components %d and %d (%c and %c)",
                                i, i + 1, prev, next));
                    }
                    components.remove(i+1);
                    components.remove(i);
                    return true;
                }
            }
            return false;
        }

        public char getComponent(int i) {
            return (char) components.get(i);
        }

        public void removeComponentsWithOppositePolarity() {
            boolean atLeastOneComponentDestroyed = false;
            do {
                atLeastOneComponentDestroyed = false;
                for (int i = 0; i < numComponents(); ++i) {
                    if (destroyComponentAt(i)) {
                        atLeastOneComponentDestroyed = true;
                        break;
                    }
                }
            } while (atLeastOneComponentDestroyed);
        }

    }

    public static void main(String[] args) throws IOException {
        String componentsString = new String(
                Files.readAllBytes(Paths.get("data", "day5.txt"))).trim();
        
        var polymer = new Polymer(componentsString);
        polymer.removeComponentsWithOppositePolarity();

        for (int i = 0; i < polymer.numComponents() - 1; ++i)
            if (toUpperCase(polymer.getComponent(i)) == toUpperCase(
                    polymer.getComponent(i + 1))
                    && polymer.getComponent(i) != polymer.getComponent(i + 1))
                throw new RuntimeException(String.format("%d %d", i, i + 1));
        System.out.println(polymer.numComponents());
    }
}
