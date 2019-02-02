package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import adventofcode2018.Day16.OpCode;
import adventofcode2018.Day19.Instruction;
import adventofcode2018.Day19.WristDeviceProgram;

public class Day21 {
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

    static class PrintAndContinueInstruction extends Instruction {
        int lastAnswer = 0;

        public static String padLeft(String s, int n) {
            return String.format("%" + n + "s", s);
        }

        int count = 0;

        public PrintAndContinueInstruction(Instruction a) {
            super(a.opcodeName, a.inputA, a.inputB, a.outputC);
        }

        @Override
        public void apply(int[] r) {
            super.apply(r);
            System.out.println(
                    "Part two:    " + padLeft(Integer.toBinaryString(r[2]), 30));
            if (lastAnswer != 0) {
                System.out.println("part two (*):" + r[2] / lastAnswer);
                System.out.println("part two (*):" + padLeft(
                        Integer.toBinaryString(r[2] / lastAnswer), 30));
                System.out.println("part two (-):" + (r[2] - lastAnswer));
                System.out.println("part two (-):" + padLeft(
                        Integer.toBinaryString(r[2] - lastAnswer), 30));
            }
            lastAnswer = r[2];
            if (count++ >= 1000)
                r[5] = 1000;
        }

    }

    public static void main(String[] args) throws IOException {
        int finalComparisonInstructionNumber = 28;

        List<String> lines = Files.readAllLines(Paths.get("data", "day21.txt"));

        String ipLine = lines.get(0);
        int instructionPointer = Integer.valueOf(ipLine.split("\\s+")[1]);
        List<Instruction> instructions = lines.stream().skip(1)
                .map(Instruction::parse).collect(Collectors.toList());
        instructions.set(finalComparisonInstructionNumber,
                new PrintAndExitInstruction(
                        instructions.get(finalComparisonInstructionNumber)));
        Map<String, OpCode> opCodes = Day16.setupOpCodes();

        WristDeviceProgram program = new WristDeviceProgram(opCodes,
                instructions, instructionPointer);
        program.runWithRegisters(new int[] { 0, 0, 0, 0, 0, 0 });

        instructions.set(finalComparisonInstructionNumber,
                new PrintAndContinueInstruction(
                        instructions.get(finalComparisonInstructionNumber)));
        WristDeviceProgram program2 = new WristDeviceProgram(opCodes,
                instructions, instructionPointer);
        program2.runWithRegisters(new int[] { 0, 0, 0, 0, 0, 0 });
    }
}
