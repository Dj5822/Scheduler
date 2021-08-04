import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 * Representation of Graph structure as a set of tasks.
 * In our problem, the graph is read-only.
 */
public class Graph {
    private Map<GraphNode,Task> taskMap;
    private Map<Task,GraphNode> nodeMap;

    private HashSet<Task> tasks;
    private DotParser parser;

    public Graph(String inputFile) {
        try {
            this.parser = new DotParser(new FileInputStream(inputFile), "output.dot");

            this.taskMap = new HashMap<>();
            this.nodeMap = new HashMap<>();

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
            this.taskMap.put(node, task);
            this.nodeMap.put(task, node);
        }
    }

    /**
     * Assigns edges to tasks from a map of nodes to tasks
     * @return Set of tasks with edges assigned
     */
    private void assignEdges() {
        for (GraphEdge parsedEdge : parser.parseEdges()) {
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
        tasks = new HashSet<Task>(taskMap.values());
    }

    public void generateOutputGraph() {
        Node node = generateDebugSchedule();

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
        parser.writeScheduleToDot();
    }

    /**
     * @return list of start tasks (tasks with no parents)
     */
    public ArrayList<Task>  getStartTasks() {
        ArrayList<Task> rootTasks = new ArrayList<Task>();
        for (Task task : tasks) {
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
        for (Task task : tasks) {
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
        for (Task task : taskMap.values()) {
            time += 2;
            old_node = node;
            node = new Node(old_node, 0, new State(task, time, time-1));
        }
        return node;
    }
}
