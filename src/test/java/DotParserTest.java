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
        parser = new DotParser(new FileInputStream("examples/dot-inputs/Nodes_25_2p.dot"), "src/test/Nodes_25_2p-output.dot");
    }
    @Test
    public void testWriteToScheduleDot() throws IOException {
        File expectedFile = new File("src/test/DotParserTestExpectedFile.dot");
        File outputFile = new File("src/test/Nodes_25_2p-output.dot");
        parser.writeScheduleToDot();
        assertEquals(FileUtils.readFileToString(expectedFile, "utf-8"),
                FileUtils.readFileToString(outputFile, "utf-8"));
    }
}
