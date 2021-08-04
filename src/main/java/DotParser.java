
import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphParser;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

    /**
     * Derives tasks from GraphNodes and returns a map between them
     * @param nodes collection of GraphNodes to derive tasks from
     * @return Map of GraphNodes to Tasks
     */
    public static Map<GraphNode,Task> mapNodesToTasks(Collection<GraphNode> nodes) {
        // Add created tasks to a map to speed up edge assignment
        Map<GraphNode,Task> taskMap = new HashMap<GraphNode,Task>();

        // create tasks from parsed nodes
        for (GraphNode node : nodes) {
            int weight = Integer.parseInt((String) node.getAttribute("Weight"));
            Task task = new Task(weight, node.getId());
            taskMap.put(node, task);
         }
        
        return taskMap;
    }

    /**
    * Assigns edges to tasks from a map of nodes to tasks
    * @param taskMap map of GraphNodes to tasks
    * @param edges collection of edges to convert
    * @return Set of tasks with edges assigned
    */
    public static HashSet<Task> assignEdgesToTasks(Map<GraphNode,Task> taskMap, Collection<GraphEdge> edges) {
        for (GraphEdge parsedEdge : edges) {
            int communicationTime = Integer.parseInt((String) parsedEdge.getAttribute("Weight"));

            // get nodes from edges
            GraphNode parentNode = parsedEdge.getNode1();
            GraphNode childNode = parsedEdge.getNode2();

            //find corresponding tasks on map
            Task parent = taskMap.get(parentNode);
            Task child = taskMap.get(childNode);

            //convert edge to our desired form
            Edge edge = new Edge(child, parent, communicationTime);
                    
            // assign edge to tasks
            child.addParent(edge);
            parent.addChild(edge);
        }

        // return tasks as a set
        HashSet<Task> tasks = new HashSet<Task>(taskMap.values());
        return tasks;
    }

    /**
    * Constructs tasks and populates relationships
    * In accordance with parser's input file
    * @param parser DotParser parsing a graph file
    * @return set of initialised tasks
    */
    public HashSet<Task> getConvertedTasks() {
        Collection<GraphNode> nodes = parseNodes();
        Collection<GraphEdge> edges = parseEdges();
        Map<GraphNode,Task> taskMap = mapNodesToTasks(nodes);
        return assignEdgesToTasks(taskMap, edges);
    }

    public Graph getConvertedGraph() {
        return new Graph(getConvertedTasks());
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
