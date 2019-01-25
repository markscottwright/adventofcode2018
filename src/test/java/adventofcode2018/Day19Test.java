package adventofcode2018;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

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

}
