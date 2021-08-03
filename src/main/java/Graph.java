import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphNode;

public class Graph {
    public HashSet<Task> tasks;

    public Graph(String inputFile, String outputFile) throws FileNotFoundException {
        DotParser parser = new DotParser(new FileInputStream(inputFile), outputFile);
        this.tasks =  initialiseTasks(parser);
    }

    /**
     * Constructs tasks and populates relationships
     * In accordance with parser's input file
     * @param parser DotParser parsing a graph file
     * @return set of initialised tasks
     */
    private static HashSet<Task> initialiseTasks(DotParser parser) {
        Collection<GraphNode> nodes = parser.parseNodes();
        Collection<GraphEdge> edges = parser.parseEdges();
        Map<GraphNode,Task> taskMap = mapNodesToTasks(nodes);
        return assignEdgesToTasks(taskMap, edges);
    }

    /**
     * Converts GraphNodes to Tasks and returns a map between them
     * @param nodes collection of GraphNodes to convert
     * @return Map of GraphNodes to Tasks
     */
    private static Map<GraphNode,Task> mapNodesToTasks(Collection<GraphNode> nodes) {
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
    private static HashSet<Task> assignEdgesToTasks(Map<GraphNode,Task> taskMap, Collection<GraphEdge> edges) {
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
                    child.addChild(edge);
                    parent.addParent(edge);
                }

                // return tasks as a set
                HashSet<Task> tasks = new HashSet<Task>(taskMap.values());
                return tasks;
    }
}
