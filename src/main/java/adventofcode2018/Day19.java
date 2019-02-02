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

    /**
     * The trick here is that lines 1-11 are adding up the sum of all even
     * divisors of r[1] and putting it in r[0]. But using a O(n^2) algorithm. So
     * if we replace the opcode starting at position 1 with Factorizer - which
     * puts the sum of factors of r[1] in r[0] and skips ahead to instruction 12
     * - this runs instantaneously.
     * 
     * @author wrightm
     *
     */
    public static class WristDeviceProgram {

        final private int instructionPointerRegister;
        final private List<Instruction> instructions;
        private boolean isTracing = false;

        public WristDeviceProgram(Map<String, OpCode> opCodes,
                List<Instruction> instructions,
                int instructionPointerRegister) {
            this.instructions = instructions;
            this.instructionPointerRegister = instructionPointerRegister;
            for (Instruction i : instructions)
                i.compile(opCodes);
        }

        void enableSpeedup() {
            instructions.set(1, new Factorizer());
        }

        public int[] run(int register0Value) {
            int[] registers = new int[] { register0Value, 0, 0, 0, 0, 0 };
            return runWithRegisters(registers);
        }

        public int[] runWithRegisters(int[] registers) {

            int instructionPointer = 0;
            while (instructionPointer < instructions.size()) {
                trace(String.format("ip = %d:", instructionPointer));
                registers[instructionPointerRegister] = instructionPointer;
                Instruction currentInstruction = instructions
                        .get(instructionPointer);
                trace(" " + currentInstruction);
                int[] nextRegisters = Arrays.copyOf(registers, 6);
                trace(" " + Arrays.toString(registers));
                instructions.get(instructionPointer).apply(nextRegisters);
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

        public void setTrace() {
            isTracing = true;
        }
    }

    public static class Instruction {
        protected final String opcodeName;
        protected final int inputA;
        protected final int inputB;
        protected final int outputC;
        protected OpCode opcode;

        public Instruction(String opcodeName, int inputA, int inputB,
                int outputC) {
            this.opcodeName = opcodeName;
            this.inputA = inputA;
            this.inputB = inputB;
            this.outputC = outputC;
        }

        void compile(Map<String, OpCode> mapping) {
            this.opcode = mapping.get(this.opcodeName);
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

        public void apply(int[] r) {
            this.opcode.apply(r, inputA, inputB, outputC);
        }
    }

    static int sumOfFactors(int n) {
        int sum = 0;
        for (int i = 1; i <= n; ++i) {
            if (n % i == 0) {
                sum += i;
            }
        }
        return sum;
    }

    static class Factorizer extends Day19.Instruction {
        public Factorizer() {
            super("fact", 0, 0, 0);
        }

        @Override
        public void apply(int[] r) {
            r[2] = r[2] + 10;
            r[5] = r[1] + 1;
            r[3] = r[1] + 1;
            r[0] = sumOfFactors(r[1]);
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
        program.enableSpeedup();
        int[] results = program.run();
        System.out.println("Part one:" + Arrays.toString(results));
        System.out.println("Part one:" + results[0]);
        results = program.run(1);
        System.out.println("Part two:" + Arrays.toString(results));
        System.out.println("Part two:" + results[0]);
    }
}
