package adventofcode2018;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.junit.Test;

import adventofcode2018.Day20.Chain;
import adventofcode2018.Day20.ParsingException;
import adventofcode2018.Day20.Point;
import adventofcode2018.Day20.PointPair;

public class Day20Test {

    @Test
    public void testTokenization() throws IOException {
        StringTokenizer tokenizer = new StringTokenizer(
                new String(Files.readAllBytes(Paths.get("data", "day20.txt"))),
                "()|^$", true);
        while (tokenizer.hasMoreTokens()) {
            System.out.println(tokenizer.nextToken());
        }

    }

    @Test
    public void testSimpleChainParsing() throws ParsingException {
        Chain chain = Day20.Chain.parse("^ab$");
        assertEquals("ab", chain.segment);
        assertEquals(Chain.END, chain.next);
    }

    @Test
    public void testOneBranch() throws ParsingException {
        Chain chain = Day20.Chain.parse("^ab(x|y)$");
        assertEquals("ab", chain.segment);
        assertEquals(null, chain.next.segment);
        assertEquals("x", chain.next.choices.get(0).segment);
        assertEquals("y", chain.next.choices.get(1).segment);
        assertEquals(Chain.END, chain.next.next);
    }

    @Test
    public void testTwoBranches() throws ParsingException {
        Chain chain = Day20.Chain.parse("^ab(x|y(1|2)z)$");
        assertEquals("ab", chain.segment);
        assertEquals(null, chain.next.segment);
        assertEquals("x", chain.next.choices.get(0).segment);
        assertEquals("y", chain.next.choices.get(1).segment);
        assertEquals(null, chain.next.choices.get(1).next.segment);
        assertEquals("1",
                chain.next.choices.get(1).next.choices.get(0).segment);
        assertEquals(Chain.END, chain.next.next);
        System.out.println(chain);
    }

    @Test
    public void testEmptyBranches() throws ParsingException {
        Chain chain = Day20.Chain.parse("^ab(x|)$");
        assertEquals("ab", chain.segment);
        assertEquals(null, chain.next.segment);
        assertEquals("x", chain.next.choices.get(0).segment);
        assertEquals("", chain.next.choices.get(1).segment);
        System.out.println(chain);
    }

    @Test
    public void testGeneratingStrings() throws ParsingException {
        assertEquals(1, Chain.parse("^ab$").allStrings().size());
        assertEquals(3, Chain.parse("^a(1|2|3)b$").allStrings().size());
        assertEquals(6, Chain.parse("^a(1|2|3)b(x|)$").allStrings().size());
        assertEquals(8,
                Chain.parse("^a(1|2|3(4|5))b(x|)$").allStrings().size());
    }

    @Test
    public void testParsingInputData() throws ParsingException, IOException {
        Chain c = Chain.parse(
                new String(Files.readAllBytes(Paths.get("data", "day20.txt"))));
    }

    @Test
    public void testSampleData() throws ParsingException {
        Chain chain = Chain
                .parse("^ESSWWN(E|NNENN(EESS(WNSE|)SSS|WWWSSSSE(SW|NNNE)))$");
        System.out.println(chain.allStrings().size());
        System.out.println(chain.toString());
        
        HashSet<PointPair> doors = new HashSet<>();
        chain.walkPath(new Point(0,0), doors);
        System.out.println(doors);
        
        Day20.printMap(new Point(0,0), doors, System.out);
    }

}
