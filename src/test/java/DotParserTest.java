import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphEdge;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The AppTest class contains unit test cases for the DotParser class.
 * This class tests for the coverage and validates functionality of class methods
 */
public class DotParserTest {

    private static DotParser parser;
    private Collection<GraphNode> outputNodes;
    private Collection<GraphEdge> outputEdges;

    @BeforeAll
    public static void setUp() throws FileNotFoundException {
        parser = new DotParser(new FileInputStream("src/test/Nodes_7_OutTree.dot"), "src/test/Nodes_7_OutTree-output.dot");
    }
    @Test
    public void testParseNodes() {
        outputNodes = parser.parseNodes();
        assertEquals("[GraphNode-0{Weight=5}, GraphNode-1{Weight=6}, GraphNode-2{Weight=5}, " +
                "GraphNode-3{Weight=6}, GraphNode-4{Weight=4}, GraphNode-5{Weight=7}, " +
                "GraphNode-6{Weight=7}]", outputNodes.toString());
    }
    @Test
    public void testParseEdges() {
        outputEdges = parser.parseEdges();
        assertEquals("[GraphEdge-0-1{Weight=15}, GraphEdge-0-2{Weight=11}, GraphEdge-0-3{Weight=11}, " +
                "GraphEdge-1-4{Weight=19}, GraphEdge-1-5{Weight=4}, " +
                "GraphEdge-1-6{Weight=21}]", outputEdges.toString());
    }
    @Test
    public void testWriteToScheduleDot() throws IOException {
        File expectedFile = new File("src/test/DotParserTestExpectedFile.dot");
        File outputFile = new File("src/test/Nodes_7_OutTree-output.dot");
        parser.writeScheduleToDot();
        assertEquals(FileUtils.readFileToString(expectedFile, "utf-8"),
                FileUtils.readFileToString(outputFile, "utf-8"));
    }
}
