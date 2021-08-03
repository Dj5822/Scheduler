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
        Collection<GraphNode> nodes = parser.parseNodes();
        Collection<GraphEdge> edges = parser.parseEdges();
        Map<GraphNode,Task> taskMap = mapNodesToTasks(nodes);
        this.tasks =  assignEdgesToTasks(taskMap, edges);
    }

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

    private static HashSet<Task> assignEdgesToTasks(Map<GraphNode,Task> taskMap, Collection<GraphEdge> edges) {
                 // create edges from parsed edges and add to tasks
                 for (GraphEdge parsedEdge : edges) {
                    int communicationTime = Integer.parseInt((String) parsedEdge.getAttribute("Weight"));
        
                    GraphNode parentNode = parsedEdge.getNode1();
                    GraphNode childNode = parsedEdge.getNode2();
        
                    Task parent = taskMap.get(parentNode);
                    Task child = taskMap.get(childNode);
        
                    Edge edge = new Edge(child, parent, communicationTime);
        
                    child.addChild(edge);
                    parent.addParent(edge);
                }
                HashSet<Task> tasks = new HashSet<Task>(taskMap.values());
                return tasks;
    }
}
