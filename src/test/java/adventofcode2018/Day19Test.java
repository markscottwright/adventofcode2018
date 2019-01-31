package adventofcode2018;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import adventofcode2018.Day16.OpCode;
import adventofcode2018.Day19.Instruction;
import adventofcode2018.Day19.WristDeviceProgram;

public class Day19Test {

    @Test
    public void testWithSampleData() {
        List<Instruction> instructions = Arrays.asList(
                Instruction.parse("seti 5 0 1"),
                Instruction.parse("seti 6 0 2"),
                Instruction.parse("addi 0 1 0"),
                Instruction.parse("addr 1 2 3"),
                Instruction.parse("setr 1 0 0"),
                Instruction.parse("seti 8 0 4"),
                Instruction.parse("seti 9 0 5"));
        WristDeviceProgram program = new Day19.WristDeviceProgram(
                Day16.setupOpCodes(), instructions, 0);
        assertTrue(
                Arrays.equals(new int[] { 6, 5, 6, 0, 0, 9 }, program.run()));
    }

    @Test
    public void jitExploration() {
        
        // after loop below (1-15):
        // r3 = r1+1
        // r5 = r1+1
        // r0 = r0 + sum of factors of r1
        // r4 = 1

        //@formatter:off
        String source = "noop 2 16 2\r\n" + 
                "seti 1 1 3\r\n" + 
                "seti 1 7 5\r\n" + 
                "mulr 3 5 4\r\n" + 
                "eqrr 4 1 4\r\n" + 
                "addr 4 2 2\r\n" + 
                "addi 2 1 2\r\n" + 
                "addr 3 0 0\r\n" + 
                "addi 5 1 5\r\n" + 
                "gtrr 5 1 4\r\n" + 
                "addr 2 4 2\r\n" + 
                "seti 2 3 2\r\n" + 
                "addi 3 1 3\r\n" + 
                "gtrr 3 1 4\r\n" + 
                "addr 4 2 2\r\n" + 
                "seti 1 9 2";
        //@formatter:on
        List<Instruction> instructions = new ArrayList<>();
        Arrays.stream(source.split("\r\n"))
                .forEach(l -> instructions.add(Instruction.parse(l)));
        System.out.println(instructions);
        Map<String, OpCode> opCodes = Day16.setupOpCodes();
        opCodes.put("noop", (r, a, b, c) -> {
        });
        WristDeviceProgram program = new WristDeviceProgram(opCodes,
                instructions, 2);
        program.setTrace();
        int[] results = program.runWithRegisters(new int[] { 0, 10, 0, 0, 0, 0 });
        System.out.println(Arrays.toString(results));
    }
    
    @Test
    public void sumOfFactorsOf10Is18() throws Exception {
        assertEquals(18, Day19.sumOfFactors(10));
    }
}
