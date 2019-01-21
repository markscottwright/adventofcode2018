package adventofcode2018;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day16 {

    public static class WristDeviceProgram {
        ArrayList<int[]> instructions;
        Map<String, OpCode> opCodes;
        HashMap<Integer, String> assignments;

        public WristDeviceProgram(ArrayList<int[]> instructions2,
                Map<String, OpCode> opCodes2,
                HashMap<Integer, String> assignments2) {
            instructions = instructions2;
            opCodes = opCodes2;
            assignments = assignments2;
        }

        int[] run() {
            int[] registers = new int[4];
            for (int[] instruction : instructions) {
                int opCodeNum = instruction[0];
                String opCode = assignments.get(opCodeNum);
                System.out.print(
                        String.format("%s (%2d) %d %d %d : %d %d %d %d ->",
                                opCode, opCodeNum, instruction[1],
                                instruction[2], instruction[3], registers[0],
                                registers[1], registers[2], registers[3]));
                opCodes.get(opCode).apply(registers, instruction[1],
                        instruction[2], instruction[3]);
                System.out.println(String.format(" %d %d %d %d", registers[0],
                        registers[1], registers[2], registers[3]));
            }
            return registers;
        }

        public static WristDeviceProgram parse(List<String> lines,
                Map<String, OpCode> opCodes,
                HashMap<Integer, String> assignments) {
            // skip until three lines
            int i;
            for (i = 2; i < lines.size(); ++i) {
                if (lines.get(i).trim().isEmpty()
                        && lines.get(i - 1).trim().isEmpty()
                        && lines.get(i - 2).trim().isEmpty())
                    break;
            }

            ArrayList<int[]> instructions = new ArrayList<>();
            i++;
            for (; i < lines.size(); ++i) {
                instructions.add(Arrays.stream(lines.get(i).split("\\s+"))
                        .mapToInt(Integer::valueOf).toArray());
            }

            return new WristDeviceProgram(instructions, opCodes, assignments);
        }

    }

    public static interface OpCode {
        void apply(int[] registers, int inputA, int inputB, int outputC);
    }

    static Map<String, OpCode> setupOpCodes() {
        var opcodes = new HashMap<String, OpCode>();

        opcodes.put("addr", (r, a, b, c) -> r[c] = r[a] + r[b]);
        opcodes.put("addi", (r, a, b, c) -> r[c] = r[a] + b);
        opcodes.put("mulr", (r, a, b, c) -> r[c] = r[a] * r[b]);
        opcodes.put("muli", (r, a, b, c) -> r[c] = r[a] * b);
        opcodes.put("banr", (r, a, b, c) -> r[c] = r[a] & r[b]);
        opcodes.put("bani", (r, a, b, c) -> r[c] = r[a] & b);
        opcodes.put("borr", (r, a, b, c) -> r[c] = r[a] | r[b]);
        opcodes.put("bori", (r, a, b, c) -> r[c] = r[a] | b);
        opcodes.put("setr", (r, a, b, c) -> r[c] = r[a]);
        opcodes.put("seti", (r, a, b, c) -> r[c] = a);
        opcodes.put("gtir", (r, a, b, c) -> r[c] = a > r[b] ? 1 : 0);
        opcodes.put("gtri", (r, a, b, c) -> r[c] = r[a] > b ? 1 : 0);
        opcodes.put("gtrr", (r, a, b, c) -> r[c] = r[a] > r[b] ? 1 : 0);
        opcodes.put("eqir", (r, a, b, c) -> r[c] = a == r[b] ? 1 : 0);
        opcodes.put("eqri", (r, a, b, c) -> r[c] = r[a] == b ? 1 : 0);
        opcodes.put("eqrr", (r, a, b, c) -> r[c] = r[a] == r[b] ? 1 : 0);

        return opcodes;
    }

    static class Sample {
        private final int opcodeNumber;
        private final int inputA;
        private final int inputB;
        private final int outputC;
        private final int[] beforeRegisters;
        private final int[] afterRegisters;

        public Sample(int opcodeNumber, int inputA, int inputB, int outputC,
                int[] beforeRegisters, int[] afterRegisters) {
            this.opcodeNumber = opcodeNumber;
            this.inputA = inputA;
            this.inputB = inputB;
            this.outputC = outputC;
            this.beforeRegisters = beforeRegisters;
            this.afterRegisters = afterRegisters;
        }

        public static ArrayList<Sample> parse(List<String> lines) {
            ArrayList<Sample> samples = new ArrayList<>();
            for (int i = 0; i < lines.size(); i += 4) {
                if (!lines.get(i).startsWith("Before"))
                    break;

                int[] beforeRegisters = parseRegisterLine(lines.get(i));

                int[] opCode = Arrays
                        .stream(lines.get(i + 1).trim().split("\\s+"))
                        .mapToInt(Integer::valueOf).toArray();
                int[] afterRegisters = parseRegisterLine(lines.get(i + 2));
                samples.add(new Sample(opCode[0], opCode[1], opCode[2],
                        opCode[3], beforeRegisters, afterRegisters));
            }
            return samples;
        }

        static private int[] parseRegisterLine(String line) {
            return Arrays
                    .stream(line
                            .substring(line.indexOf('[') + 1, line.indexOf(']'))
                            .split(",\\s*"))
                    .mapToInt(Integer::valueOf).toArray();
        }

        public Set<String> matchingOpCodes(Map<String, OpCode> opCodes) {
            HashSet<String> matchingOpCodeNames = new HashSet<>();
            for (var opCodeEntry : opCodes.entrySet()) {
                int[] registers = Arrays.copyOf(beforeRegisters,
                        beforeRegisters.length);
//                System.out.print(opCodeEntry.getKey() + " ");
//                System.out.print(Arrays.toString(registers));
                opCodeEntry.getValue().apply(registers, inputA, inputB,
                        outputC);
//                System.out.println(" " + Arrays.toString(registers));
                if (Arrays.equals(registers, afterRegisters)) {
                    matchingOpCodeNames.add(opCodeEntry.getKey());
                }
            }
            return matchingOpCodeNames;
        }
    }

    public static Set<String> commonElements(Set<String> a, Set<String> b) {
        var c = new HashSet<String>();
        for (String e : a) {
            if (b.contains(e))
                c.add(e);
        }
        return c;
    }

    public static void main(String[] args) throws java.lang.Exception {
        var lines = Files.readAllLines(Paths.get("data", "day16.txt"));
        ArrayList<Sample> samples = Sample.parse(lines);

        var opCodes = setupOpCodes();
        int numWith3OrMoreMatches = (int) samples.stream()
                .map(s -> s.matchingOpCodes(opCodes)).mapToInt(Set::size)
                .filter(numMatches -> numMatches >= 3).count();
        System.out.println("Part one: " + numWith3OrMoreMatches);

        HashMap<Integer, Set<String>> possibleAssignments = findPossibleAssignments(
                samples, opCodes);
        HashMap<Integer, String> assignments = assignOpCodes(
                possibleAssignments);
        if (assignments == null)
            throw new Exception("Unable to make assignments");
        System.out.println("Part two - opCode assignments:" + assignments);

        int[] results = WristDeviceProgram.parse(lines, opCodes, assignments)
                .run();

        System.out.println("Part two:" + results[0]);
    }

    private static HashMap<Integer, String> assignOpCodes(
            HashMap<Integer, Set<String>> possibleAssignments) {
        ArrayList<Integer> opCodes = possibleAssignments.keySet().stream()
                .collect(Collectors.toCollection(ArrayList::new));
        HashMap<Integer, String> currentAssignments = new HashMap<>();
        return assign(opCodes, currentAssignments, possibleAssignments);
    }

    private static HashMap<Integer, String> assign(ArrayList<Integer> opCodes,
            HashMap<Integer, String> currentAssignments,
            HashMap<Integer, Set<String>> possibleAssignments) {
        // all opcode numbers have been assigned - we're done
        if (opCodes.isEmpty())
            return currentAssignments;

        else {
            Integer opCode = opCodes.get(0);
            ArrayList<Integer> remainingOpCodes = opCodes.stream().skip(1)
                    .collect(Collectors.toCollection(ArrayList::new));

            for (String opCodeName : possibleAssignments.get(opCode)) {
                // already assigned?
                if (currentAssignments.values().contains(opCodeName))
                    continue;

                // try assigning it and continuing
                currentAssignments.put(opCode, opCodeName);
                HashMap<Integer, String> solution = assign(remainingOpCodes,
                        currentAssignments, possibleAssignments);

                // found a solution
                if (solution != null)
                    return solution;

                // try again
                currentAssignments.remove(opCode);
            }

            // never found a solution. Backtrack.
            return null;
        }
    }

    private static HashMap<Integer, Set<String>> findPossibleAssignments(
            ArrayList<Sample> samples, Map<String, OpCode> opCodes) {
        HashMap<Integer, Set<String>> possibleOpCodes = new HashMap<>();
        for (Sample s : samples) {
            if (possibleOpCodes.containsKey(s.opcodeNumber)) {
                possibleOpCodes.put(s.opcodeNumber,
                        commonElements(possibleOpCodes.get(s.opcodeNumber),
                                s.matchingOpCodes(opCodes)));
            } else {
                possibleOpCodes.put(s.opcodeNumber, s.matchingOpCodes(opCodes));
            }
        }
        return possibleOpCodes;
    }
}
