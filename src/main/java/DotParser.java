
import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class DotParser extends GraphParser{
    private final String outputFile;

    public DotParser(InputStream is, String outputFile) {
        super(is);
        this.outputFile = outputFile;
    }

    /**
     * return the nodes as a collection
     * @return Collection of GraphNodes
     */
    public Collection<GraphNode> parseNodes() {
        return getNodes().values();
    }
    /**
     * return the edges as a collection
     * @return Collection of GraphNodes
     */
    public Collection<GraphEdge> parseEdges() {
        return getEdges().values();
    }

    public void writeScheduleToDot(Node node, Map<Task,GraphNode> nodeMap) {
        while (node != null) {
            State state = node.getState();
            Task task = state.getTask();

            GraphNode mappedNode = nodeMap.get(task);
            if (mappedNode != null) {
                mappedNode.setAttribute("Start",state.getStartTime());
                mappedNode.setAttribute("Processor",state.getProcessor());
            }

            node = node.getParent();
        }

        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write("digraph  \""+ outputFile.replace(".dot","") +"\" {\n");

            for (GraphNode graphNode : parseNodes()) {
                //String attr = graphNode.getAttributes().toString().replace("{", "[").replace("}", "]");
                String weight = "[Weight=" + graphNode.getAttribute("Weight").toString();
                String start = ",Start=" + graphNode.getAttribute("Start").toString();
                String processor = ",Processor=" + graphNode.getAttribute("Processor").toString();
                String attr = weight + start + processor + "]";
                writer.write("\t" + graphNode.getId() + "\t" + attr + ";\n");
            }

            for (GraphEdge edge : parseEdges()) {
                String attr = edge.getAttributes().toString().replace("{", "[").replace("}", "]");
                writer.write("\t" + edge.getNode1().getId() + " -> " + edge.getNode2().getId() + "\t" + attr + ";\n");
            }
            writer.write("}");
            writer.close();
        } catch (IOException e) {
            System.out.println("Output error occurred.");
            e.printStackTrace();
        }
    }
}
