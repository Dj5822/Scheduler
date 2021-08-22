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
    private HashMap<String, Task> tasks;
    private HashMap<String, GraphNode> nodes;
    private DotParser parser;

    /**
     * Creates a graph based on a dot file.
     * @param inputFile the dot file used to create the graph.
     */
    public Graph(String inputFile, String outputFileName) {
        try {
            this.parser = new DotParser(new FileInputStream(inputFile), outputFileName);
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
            short weight = Short.parseShort((String) node.getAttribute("Weight"));
            Task task = new Task(weight, node.getId());
            tasks.put(node.getId(), task);
            nodes.put(node.getId(), node);
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
            String parentId = parentNode.getId();
            String childId = childNode.getId();

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
    public ArrayList<Task>  getStartTasks() {
        ArrayList<Task> rootTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.isRootTask()) {
                rootTasks.add(task);
            }
        }
        return rootTasks;
    }

    public Task getDummyStart() {
        Task dummy = new Task((short) 0, "%%%");
        dummy.findBottomLevel();
        for (Task startTask : getStartTasks()) {
            dummy.addChild(new Edge(startTask, dummy, 0));
        }
        return dummy;
    }

    /**
     * Converts the graph back into a dot file.
     * @param node used to find processor and start times.
     */
    public void generateOutputGraph(Node<?> node) {
        for (TaskVariant state : node.getSchedule().getScheduledTasks().values()) {
            GraphNode mappedNode = nodes.get(state.getTask().getId());
            if (mappedNode != null) {
                mappedNode.setAttribute("Start",state.getStartTime());
                mappedNode.setAttribute("Processor",state.getProcessor());
            }
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
    public Collection<GraphNode> getGraphNodes() { return nodes.values(); }
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

    public int getTaskCount() {
        return tasks.size();
    }

}
