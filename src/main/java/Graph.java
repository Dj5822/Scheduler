import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Representation of Graph structure as a set of tasks.
 * In our problem, the graph is read-only.
 */
public class Graph {
    private HashMap<Character, Task> tasks;
    private HashMap<Character, GraphNode> nodes;
    private DotParser parser;

    public Graph(String inputFile) {
        try {
            this.parser = new DotParser(new FileInputStream(inputFile), "output.dot");
            tasks = new HashMap<>();
            nodes = new HashMap<>();
            assignTasks();
            assignEdges();
            setBottomLevels();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    private void assignTasks() {
        // create tasks from parsed nodes
        for (GraphNode node : parser.parseNodes()) {
            int weight = Integer.parseInt((String) node.getAttribute("Weight"));
            Task task = new Task(weight, node.getId());
            tasks.put(node.getId().charAt(0), task);
            nodes.put(node.getId().charAt(0), node);
        }
    }

    /**
     * Assigns edges to tasks from a map of nodes to tasks
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

    public void generateOutputGraph() {
        Node node = generateDebugSchedule();

        while (node != null) {
            State state = node.getState();
            Task task = state.getTask();

            GraphNode mappedNode = nodes.get(task.getId().charAt(0));
            if (mappedNode != null) {
                mappedNode.setAttribute("Start",state.getStartTime());
                mappedNode.setAttribute("Processor",state.getProcessor());
            }

            node = node.getParent();
        }
        parser.writeScheduleToDot();
    }

    /**
     * @return list of start tasks (tasks with no parents)
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
     * Find bottom level of all start tasks,
     * And therefore the whole graph.
     */
    private void setBottomLevels() {
        for (Task task : getStartTasks()) {
            task.findBottomLevel();
        }
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

    // For testing
    public Node generateDebugSchedule() {
        Node old_node = null;
        Node node = null;
        int time = 2;
        for (Task task : tasks.values()) {
            time += 2;
            old_node = node;
            node = new Node(old_node, 0, new State(task, time, time-1));
        }
        return node;
    }
}
