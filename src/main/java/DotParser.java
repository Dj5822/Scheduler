
import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;

import java.io.*;
import java.util.Collection;
import java.util.Map;

public class DotParser extends GraphParser{
    private String outputFile;

    public DotParser(InputStream is) {
        super(is);
        this.outputFile = "output.dot";
    }

    public DotParser(InputStream is, String outputFile) {
        super(is);
        this.outputFile = outputFile;
    }

    /**
     * Uses the paypal library to read a .dot file and return the graph as a list of maps
     * @param dotFile input graph file with .dot extension
     * @return List of collections where the first collection contains the nodes and the second contains the edges
     * @throws FileNotFoundException
     */
    public Collection<GraphNode> parseNodes() {
        return getNodes().values();
    }

    public Collection<GraphEdge> parseEdges() {
        return getEdges().values();
    }






    public void writeDot(Map<String, GraphNode> nodes, Map<String, GraphEdge> edges) {

        try {
            File myObj = new File(outputFile);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }

            FileWriter myWriter = new FileWriter(outputFile);
            myWriter.write("digraph  \"outputExample\" {\n");

            for (GraphNode node : nodes.values()) {
                String attr = node.getAttributes().toString().replace("{", "[").replace("}", "]");
                myWriter.write("\t" + node.getId() + "\t" + attr + ";\n");
            }

            for (GraphEdge edge : edges.values()) {
                String attr = edge.getAttributes().toString().replace("{", "[").replace("}", "]");
                myWriter.write("\t" + edge.getNode1().getId() + " -> " + edge.getNode2().getId() + "\t" + attr + ";\n");
            }
            myWriter.write("}");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();

        }
    }


}
