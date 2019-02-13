package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import adventofcode2018.Day24.Group.Type;

public class Day24 {
    public static class Group {
        enum Type {
            INFECTION, IMMUNE_SYSTEM
        };

        static int nextImmuneSystemGroupNumber = 1;
        static int nextInfectionGroupNumber = 1;

        private final class BestTargetComparator implements Comparator<Group> {
            @Override
            public int compare(Group g1, Group g2) {
                // pick most damage
                int damage1 = damageTo(g1);
                int damage2 = damageTo(g2);
                int compare = Integer.compare(damage1, damage2);
                if (compare == 0) {
                    // or least effective power
                    int effectivePower1 = g1.effectivePower();
                    int effectivePower2 = g2.effectivePower();
                    compare = Integer.compare(effectivePower1, effectivePower2);
                    if (compare == 0) {
                        // or highest initiative
                        return Integer.compare(g1.initiative, g2.initiative);
                    } else
                        return compare;
                } else
                    return compare;
            }
        }

        static final Pattern PARSING_PATTERN = Pattern.compile(
                "([0-9]+) units each with ([0-9]+) hit points (\\(.+\\) )?"
                        + "with an attack that does ([0-9]+) ([^ ]+) damage at initiative ([0-9]+)");

        final private Type type;
        final private int groupId;
        private int numUnits;
        final private int hitPoints;
        final private String attackType;
        private int attackValue;
        final private int initiative;
        final private List<String> weaknesses;
        final private List<String> immunities;

        public Group(Type type, int numUnits, int hitPoints, int attackValue,
                String attackType, int initiative, List<String> weaknesses,
                List<String> immunities) {
            if (type == Type.IMMUNE_SYSTEM)
                groupId = nextImmuneSystemGroupNumber++;
            else
                groupId = nextInfectionGroupNumber++;
            this.type = type;
            this.numUnits = numUnits;
            this.hitPoints = hitPoints;
            this.attackType = attackType;
            this.attackValue = attackValue;
            this.initiative = initiative;
            this.weaknesses = weaknesses;
            this.immunities = immunities;
        }

        public Group copyWithBoostedImmuneSystem(int boost) {
            if (type == Type.IMMUNE_SYSTEM)
                return new Group(type, numUnits, hitPoints, attackValue + boost,
                        attackType, initiative, weaknesses, immunities);
            else
                return new Group(type, numUnits, hitPoints, attackValue,
                        attackType, initiative, weaknesses, immunities);
        }

        public void boostImmuneSystem(int boostValue) {
            if (this.type == Type.IMMUNE_SYSTEM)
                this.attackValue += boostValue;
        }

        public static Group parse(Type type, String line) {
            Matcher matcher = PARSING_PATTERN.matcher(line);
            matcher.matches();

            int numUnits = Integer.valueOf(matcher.group(1));
            int hitPoints = Integer.valueOf(matcher.group(2));
            String weaknessesAndImmunities = matcher.group(3);
            int attackValue = Integer.valueOf(matcher.group(4));
            String attackType = matcher.group(5);
            int initiative = Integer.valueOf(matcher.group(6));

            if (weaknessesAndImmunities == null)
                weaknessesAndImmunities = "";

            String[] weaknessesAndImmunitiesArray = weaknessesAndImmunities
                    .replace("(", "").replace(")", "").split(";");
            List<String> weaknesses = Arrays
                    .stream(weaknessesAndImmunitiesArray).map(String::trim)
                    .filter(s -> s.startsWith("weak to "))
                    .map(s -> s.substring("weak to ".length()))
                    .flatMap(s -> Arrays.stream(s.split(","))).map(String::trim)
                    .collect(Collectors.toList());
            List<String> immunities = Arrays
                    .stream(weaknessesAndImmunitiesArray).map(String::trim)
                    .filter(s -> s.startsWith("immune to "))
                    .map(s -> s.substring("immune to ".length()))
                    .flatMap(s -> Arrays.stream(s.split(","))).map(String::trim)
                    .collect(Collectors.toList());
            return new Group(type, numUnits, hitPoints, attackValue, attackType,
                    initiative, weaknesses, immunities);
        }

        public Optional<Group> selectTarget(Collection<Group> groups) {
            return groups.stream().filter(g -> g.type != type)
                    .filter(g -> damageTo(g) > 0)
                    .max(new BestTargetComparator());
        }

        private int effectivePower() {
            return numUnits * attackValue;
        }

        private int damageTo(Group target) {
            if (target.immunities.contains(attackType))
                return 0;
            else if (target.weaknesses.contains(attackType))
                return effectivePower() * 2;
            else
                return effectivePower();
        }

        public boolean isDead() {
            return this.numUnits <= 0;
        }

        int getNumUnits() {
            return numUnits;
        }

        public void attack(Group target) {
            assert target.type != type;
            int damageTo = damageTo(target);
            int unitsKilled = damageTo / target.hitPoints;
            target.numUnits = target.numUnits - unitsKilled;

            if (isDead()) {
                System.out.println(this + " is dead");
            }
        }

        @Override
        public String toString() {
            return "Group [type=" + type + ", groupId=" + groupId
                    + ", numUnits=" + numUnits + ", hitPoints=" + hitPoints
                    + ", attackType=" + attackType + ", attackValue="
                    + attackValue + ", initiative=" + initiative
                    + ", weaknesses=" + weaknesses + ", immunities="
                    + immunities + "]";
        }

    }

    static class Armies {
        private final class EffectivePowerInitiativeOrder
                implements Comparator<Group> {

            @Override
            public int compare(Group g1, Group g2) {
                // by most effective power
                int compare = -Integer.compare(g1.effectivePower(),
                        g2.effectivePower());
                if (compare != 0)
                    return compare;
                else {
                    // or most initiative
                    return -Integer.compare(g1.initiative, g2.initiative);
                }
            }
        }

        private ArrayList<Group> groups = new ArrayList<>();

        static Armies parse(List<String> lines) {
            ArrayList<Group> groups = new ArrayList<>();
            boolean inImmuneSystem = false;
            for (String line : lines) {
                if (line.startsWith("Immune System:"))
                    inImmuneSystem = true;
                else if (line.startsWith("Infection:"))
                    inImmuneSystem = false;
                else if (line.trim().isEmpty())
                    continue;
                else {
                    Group group = Group
                            .parse(inImmuneSystem ? Group.Type.IMMUNE_SYSTEM
                                    : Type.INFECTION, line);
                    groups.add(group);
                }
            }

            Armies armies = new Armies();
            armies.groups = groups;
            return armies;
        }

        public void fight() {
            while (numImmuneSystemUnits() > 0 && numInfectionUnits() > 0) {
                int numImmuneSystemUnitsBefore = numImmuneSystemUnits();
                int numInfectionUnitsBefore = numInfectionUnits();

                // attackers, sorted by effective power/initiative
                ArrayList<Group> attackers = new ArrayList<>();
                attackers.addAll(groups);
                attackers.sort(new EffectivePowerInitiativeOrder());

                // attackers to targets, sorted by initiative
                TreeMap<Group, Group> groupToTarget = new TreeMap<>((g1,
                        g2) -> -Integer.compare(g1.initiative, g2.initiative));

                // target selection
                HashSet<Group> potentialTargets = new HashSet<Group>(groups);
                for (Group g : attackers) {
                    Optional<Group> maybeTarget = g
                            .selectTarget(potentialTargets);
                    if (maybeTarget.isPresent()) {
                        groupToTarget.put(g, maybeTarget.get());
                        potentialTargets.remove(maybeTarget.get());
                    }
                }

                // attacking
                for (Group g : groupToTarget.keySet()) {
                    if (!g.isDead()) {
                        Group target = groupToTarget.get(g);
                        g.attack(target);
                    }
                }

                // remove the dead
                groups = groups.stream().filter(g -> !g.isDead())
                        .collect(Collectors.toCollection(ArrayList::new));
                
                // did no change take place?  Then we're locked and need to exit
                if (numImmuneSystemUnitsBefore == numImmuneSystemUnits()
                        && numInfectionUnitsBefore == numInfectionUnits()) {
                    System.out.println("Locked");
                    return;
                }
            }

        }

        int numImmuneSystemUnits() {
            return groups.stream().filter(g -> g.type == Type.IMMUNE_SYSTEM)
                    .mapToInt(Group::getNumUnits).sum();
        }

        int numInfectionUnits() {
            return groups.stream().filter(g -> g.type == Type.INFECTION)
                    .mapToInt(Group::getNumUnits).sum();
        }

        public Armies copy() {
            return withBoostedImmuneSystem(0);
        }

        public Armies withBoostedImmuneSystem(int boostValue) {
            Armies armies = new Armies();
            armies.groups = groups.stream()
                    .map(g -> g.copyWithBoostedImmuneSystem(boostValue))
                    .collect(Collectors.toCollection(ArrayList::new));
            return armies;
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("data", "day24.txt"));

        Armies armiesTemplate = Armies.parse(lines);
        {
            Armies armies1 = armiesTemplate.copy();
            armies1.fight();

            System.out.println("Part one:");
            System.out.println(
                    "Immune system units:" + armies1.numImmuneSystemUnits());
            System.out
                    .println("Infection units:" + armies1.numInfectionUnits());
            System.out.println();
        }

        for (int i = 1; i < 1000; ++i) {
            Armies armies = armiesTemplate.withBoostedImmuneSystem(i);
            armies.fight();
            if (armies.numInfectionUnits() <= 0) {
                System.out.println("Boost:" + i);
                System.out.println("Part two:" + armies.numImmuneSystemUnits());
                break;
            }
        }
    }
}
