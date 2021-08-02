
import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;

import java.io.*;
import java.util.Map;

public class DotParser {

    private static final String OUTPUTFILE = "output.dot";

    public DotParser(String dotFile) throws FileNotFoundException {
    GraphParser parser = new GraphParser(new FileInputStream(dotFile));
        Map<String, GraphNode> nodes = parser.getNodes();
        Map<String, GraphEdge> edges = parser.getEdges();

        System.out.println("--- nodes:");
        for (GraphNode node : nodes.values()) {
            System.out.println(node.getId() + " " + node.getAttributes());
        }

        System.out.println("--- edges:");
        for (GraphEdge edge : edges.values()) {
            System.out.println(edge.getNode1().getId() + "->" + edge.getNode2().getId() + " " + edge.getAttributes());
        }

        writeDot(nodes, edges);
    }


    public static void writeDot(Map<String, GraphNode> nodes, Map<String, GraphEdge> edges) {

        try {
            File myObj = new File(OUTPUTFILE);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }

            FileWriter myWriter = new FileWriter(OUTPUTFILE);
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
