
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
    private Map<GraphNode,Task> taskMap;
    private Map<Task,GraphNode> nodeMap;

    public DotParser(InputStream is) {
        super(is);
        this.outputFile = "output.dot";
        mapNodesToTasks();
    }

    public DotParser(InputStream is, String outputFile) {
        super(is);
        this.outputFile = outputFile;
        mapNodesToTasks();
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
    public void mapNodesToTasks() {
        // Add created tasks to a map to speed up edge assignment
        Map<GraphNode,Task> taskMap = new HashMap<GraphNode,Task>();
        Map<Task,GraphNode> nodeMap = new HashMap<Task,GraphNode>();

        // create tasks from parsed nodes
        for (GraphNode node : parseNodes()) {
            int weight = Integer.parseInt((String) node.getAttribute("Weight"));
            Task task = new Task(weight, node.getId());
            taskMap.put(node, task);
            nodeMap.put(task, node);
        }

        this.taskMap = taskMap;
        this.nodeMap = nodeMap;
    }

    /**
    * Assigns edges to tasks from a map of nodes to tasks
    * @param taskMap map of GraphNodes to tasks
    * @param edges collection of edges to convert
    * @return Set of tasks with edges assigned
    */
    public HashSet<Task> assignEdges() {
        for (GraphEdge parsedEdge : parseEdges()) {
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
    */
    public Graph getConvertedGraph() {
        return new Graph(assignEdges());
    }

    /**
     * Given a node, binds the start times and processor times
     * of that node's state as attributes on the graph.
     * @param Node at head of schedule linked list
     */
    public void bindSchedule(Node node) {
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
    }

    public void writeScheduleToDot(Node node) {
        bindSchedule(node);

        try {
            FileWriter writer = new FileWriter(outputFile);
            writer.write("digraph  \"outputExample\" {\n");

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

    public Node generateDebugSchedule() {
        Node old_node = null;
        Node node = null;
        int time = 2;
        for (Task task : taskMap.values()) {
            time += 2;
            old_node = node;
            node = new Node(old_node, 0, new State(task, time, time-1));
        }
        return node;
    }

}
