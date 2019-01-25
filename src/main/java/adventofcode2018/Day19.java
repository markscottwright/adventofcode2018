package adventofcode2018;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import adventofcode2018.Day16.OpCode;

public class Day19 {

    public static class WristDeviceProgram {

        final private int instructionPointerRegister;
        final private Map<String, OpCode> opCodes;
        final private List<Instruction> instructions;
        private boolean isTracing = false;

        public WristDeviceProgram(Map<String, OpCode> opCodes,
                List<Instruction> instructions,
                int instructionPointerRegister) {
            this.opCodes = opCodes;
            this.instructions = instructions;
            this.instructionPointerRegister = instructionPointerRegister;
        }

        public int[] run(int register0Value) {
            int instructionPointer = 0;
            int[] registers = new int[] { register0Value, 0, 0, 0, 0, 0 };

            while (instructionPointer < instructions.size()) {
                if (registers[5] == 13944 && instructionPointer == 11) {
                    registers[5] = registers[1];
                    isTracing = true;
                }
                trace(String.format("ip = %d:", instructionPointer));
                registers[instructionPointerRegister] = instructionPointer;
                Instruction currentInstruction = instructions
                        .get(instructionPointer);
                trace(" " + currentInstruction);
                int[] nextRegisters = Arrays.copyOf(registers, 6);
                trace(" " + Arrays.toString(registers));
                opCodes.get(currentInstruction.opcodeName).apply(nextRegisters,
                        currentInstruction.inputA, currentInstruction.inputB,
                        currentInstruction.outputC);
                registers = nextRegisters;
                trace(" -> " + Arrays.toString(registers));
                traceNewLine();
                instructionPointer = registers[instructionPointerRegister];
                instructionPointer++;
            }
            return registers;
        }

        private void traceNewLine() {
            if (isTracing) {
                System.out.println();
            }

        }

        private void trace(String msg) {
            if (isTracing) {
                System.out.print(msg);
            }
        }

        public int[] run() {
            return run(0);
        }
    }

    public static class Instruction {
        final String opcodeName;
        final int inputA;
        final int inputB;
        final int outputC;

        public Instruction(String opcodeName, int inputA, int inputB,
                int outputC) {
            this.opcodeName = opcodeName;
            this.inputA = inputA;
            this.inputB = inputB;
            this.outputC = outputC;
        }

        public static Instruction parse(String line) {
            String[] fields = line.split("\\s+");
            return new Instruction(fields[0], Integer.valueOf(fields[1]),
                    Integer.valueOf(fields[2]), Integer.valueOf(fields[3]));
        }

        @Override
        public String toString() {
            return "Instruction [opcodeName=" + opcodeName + ", inputA="
                    + inputA + ", inputB=" + inputB + ", outputC=" + outputC
                    + "]";
        }
    }

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("data", "day19.txt"));

        String ipLine = lines.get(0);
        int instructionPointer = Integer.valueOf(ipLine.split("\\s+")[1]);
        List<Instruction> instructions = lines.stream().skip(1)
                .map(Instruction::parse).collect(Collectors.toList());
        Map<String, OpCode> opCodes = Day16.setupOpCodes();

        WristDeviceProgram program = new WristDeviceProgram(opCodes,
                instructions, instructionPointer);
        int[] results = program.run();
        System.out.println("Part one:" + Arrays.toString(results));
        System.out.println("Part one:" + results[0]);

        results = program.run(1);
        System.out.println("Part two:" + Arrays.toString(results));
        System.out.println("Part two:" + results[0]);
    }
}
