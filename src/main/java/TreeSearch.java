import java.util.*;

/**
 * The method responsible for the creation and traversal of the
 * schedule tree, a tree of each possible schedule and partial schedule
 */
public class TreeSearch {

    private Graph graph;
    private int processorCount;

    TreeSearch(Graph graph, int processorCount){
        this.graph = graph;
        this.processorCount = processorCount;
    }

    /**
     * Implementation of the A* algorithm, traversing the tree and creating a
     * viable schedule from it
     * 
     * @return The node that is at the end of the created schedule
     */
    public Node aStar() {
        PriorityQueue<Node> openList = new PriorityQueue<Node>(new NodeComparator());
        HashSet<String> createdNodes = new HashSet<String>();
        
        for (Task startTask : graph.getStartTasks()) {
            Node rootNode = new Node(startTask, graph.getStartTasks(), graph, processorCount);
            openList.add(rootNode);
        }
        while (!openList.isEmpty()) {
            Node node = openList.poll();
            if (node.getSchedule().getScheduledTasks().size() == graph.getTasks().size()) {
                return node;
            }

            ArrayList<Node> successorList = node.getSuccessors(processorCount, graph);
            for (Node successorNode : successorList) {
                if (!NodeAlreadyExists(createdNodes, successorNode)) {
                    openList.add(successorNode);
                }
                createdNodes.add(successorNode.toString());
            }
        }
        return null;
    }

    private boolean NodeAlreadyExists(HashSet<String> createdNodes, Node node) {
        return createdNodes.contains(node.toString());
    }

    /**
     * The comparator for ordering of nodes in the priority queue
     * Nodes with a lower cost will be prioritised, putting them at
     * the front of the priority queue
     */
    private class NodeComparator implements Comparator<Node> {
        public int compare(Node node1, Node node2) {
            return node1.getCost() - (node2.getCost());
        }
    }
}