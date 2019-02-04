package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import adventofcode2018.Day16.OpCode;
import adventofcode2018.Day19.Instruction;
import adventofcode2018.Day19.WristDeviceProgram;

public class Day21 {

    static public class ExitOnRepeat extends Instruction {
        int lastR2Seen = 0;
        LinkedHashSet<Integer> r2Seen = new LinkedHashSet<Integer>();

        public ExitOnRepeat(Instruction a) {
            super(a.opcodeName, a.inputA, a.inputB, a.outputC);
        }

        @Override
        public void apply(int[] r) {
            super.apply(r);
            if (r2Seen.contains(r[2])) {
                r[5] = 1000;
                System.out.println("Part two:" + lastR2Seen);
            } else {
                r2Seen.add(r[2]);
                lastR2Seen = r[2];
            }
        }
    }

    static public class DivideBy256 extends Instruction {

        public DivideBy256() {
            super("d256", 1, 0, 3);
        }

        @Override
        public void apply(int[] r) {
            r[4] = r[1] / 256;
            r[5] += 8;
        }
    }

    static class PrintAndExitInstruction extends Instruction {

        public PrintAndExitInstruction(Instruction a) {
            super(a.opcodeName, a.inputA, a.inputB, a.outputC);
        }

        @Override
        public void apply(int[] r) {
            super.apply(r);
            System.out.println(Arrays.toString(r));
            System.out.println("Part one:" + r[2]);
            r[5] = 1000;
        }

    }

    public static void main(String[] args) throws IOException {
        int finalComparisonInstructionNumber = 28;
        int divideInstructionNumber = 17;
        Map<String, OpCode> opCodes = Day16.setupOpCodes();

        List<String> lines = Files.readAllLines(Paths.get("data", "day21.txt"));
        String ipLine = lines.get(0);
        int instructionPointer = Integer.valueOf(ipLine.split("\\s+")[1]);

        List<Instruction> instructions = lines.stream().skip(1)
                .map(Instruction::parse).collect(Collectors.toList());
        instructions.set(divideInstructionNumber, new DivideBy256());
        instructions.set(finalComparisonInstructionNumber,
                new PrintAndExitInstruction(
                        instructions.get(finalComparisonInstructionNumber)));
        WristDeviceProgram program = new WristDeviceProgram(opCodes,
                instructions, instructionPointer);
        program.runWithRegisters(new int[] { 0, 0, 0, 0, 0, 0 });

        // part two
        List<Instruction> instructions2 = lines.stream().skip(1)
                .map(Instruction::parse).collect(Collectors.toList());
        instructions2.set(divideInstructionNumber, new DivideBy256());
        instructions2.set(finalComparisonInstructionNumber, new ExitOnRepeat(
                instructions2.get(finalComparisonInstructionNumber)));
        var program2 = new WristDeviceProgram(opCodes, instructions2,
                instructionPointer);
        program2.runWithRegisters(new int[] { 0, 0, 0, 0, 0, 0 });
    }
}
