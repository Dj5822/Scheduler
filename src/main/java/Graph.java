import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Representation of Graph structure as a set of tasks.
 * In our problem, the graph is read-only.
 */
public class Graph {
    private HashMap<Character, Task> tasks;
    private HashMap<Character, GraphNode> nodes;
    private DotParser parser;

    /**
     * Creates a graph based on a dot file.
     * @param inputFile the dot file used to create the graph.
     */
    public Graph(String inputFile) {
        try {
            this.parser = new DotParser(new FileInputStream(inputFile), "output.dot");
            tasks = new HashMap<>();
            nodes = new HashMap<>();
            setupGraph();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    /**
     * Call all the methods required to setup
     * the graph.
     */
    private void setupGraph() {
        assignTasks();
        assignEdges();
        setBottomLevels();
    }

    /**
     * Uses the information from DotParser to
     * create graph tasks.
     */
    private void assignTasks() {
        // create tasks from parsed nodes
        for (GraphNode node : parser.parseNodes()) {
            int weight = Integer.parseInt((String) node.getAttribute("Weight"));
            Task task = new Task(weight, node.getId().charAt(0));
            tasks.put(node.getId().charAt(0), task);
            nodes.put(node.getId().charAt(0), node);
        }
    }

    /**
     * Uses the information from DotParser to
     * create graph edges.
     */
    private void assignEdges() {
        for (GraphEdge parsedEdge : parser.parseEdges()) {
            int communicationTime = Integer.parseInt((String) parsedEdge.getAttribute("Weight"));
            GraphNode parentNode = parsedEdge.getNode1();
            GraphNode childNode = parsedEdge.getNode2();
            char parentId = parentNode.getId().charAt(0);
            char childId = childNode.getId().charAt(0);

            Edge edge = new Edge(tasks.get(childId), tasks.get(parentId), communicationTime);

            tasks.get(parentId).addChild(edge);
            tasks.get(childId).addParent(edge);
        }
    }

    /**
     * Find bottom level of all start tasks,
     * And therefore the whole graph.
     */
    private void setBottomLevels() {
        for (Task task : getStartTasks()) {
            task.findBottomLevel();
        }
    }

    /**
     * Used to get all the start tasks
     * (tasks with no parents).
     * @return list of start tasks
     */
    private ArrayList<Task>  getStartTasks() {
        ArrayList<Task> rootTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.isRootTask()) {
                rootTasks.add(task);
            }
        }
        return rootTasks;
    }

    /**
     * Converts the graph back into a dot file.
     * @param node used to find processor and start times.
     */
    public void generateOutputGraph(Node node) {
        while (node != null) {
            State state = node.getState();
            Task task = state.getTask();

            GraphNode mappedNode = nodes.get(task.getId());
            if (mappedNode != null) {
                mappedNode.setAttribute("Start",state.getStartTime());
                mappedNode.setAttribute("Processor",state.getProcessor());
            }

            node = node.getParent();
        }
        parser.writeScheduleToDot();
    }

    /**
     * Gets all the tasks in the graph.
     * @return a collection of all the tasks in the graph.
     */
    public Collection<Task> getTasks() {
        return tasks.values();
    }

    /**
     * Debugging tool - prints out the bottom level of every task
     */
    public void printBottomLevels() {
        System.out.println("Bottom Levels:");
        for (Task task : tasks.values()) {
            if (task.getBottomLevel() != null) {
                System.out.println(task.getId() + ": " + task.getBottomLevel());
            }
        }
    }
}
