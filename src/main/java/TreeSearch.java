import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * The method responsible for the creation and traversal of the
 * schedule tree, a tree of each possible schedule and partial schedule
 */
public class TreeSearch {

    private Graph graph;
    private int processorCount;

    private Node incumbent = new Node();
    private int activeThreads = 0;

    private synchronized void incrementActiveThreads() {
        activeThreads++;
    }

    private synchronized void decrementActiveThreads() {
        activeThreads--;
    }
    
    private synchronized void updateEncumbent(Node candidate) {
        incumbent = candidate;
    }

    public synchronized Node getEncumbent() {
        return incumbent;
    }

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
            Node node = openList.peek();
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
            if (node.getSchedule().getSchedulableTasks().isEmpty()) {
                openList.poll();
            }
        }
        return null;
    }

    /**
     * Simple Centralised Parallel A*
     * @return goal node, or null if goal not found
     */
    public Node aStarCentralized(int threadCount) {
        PriorityBlockingQueue<Node> openList = new PriorityBlockingQueue<Node>(100000, new NodeComparator());
        Set<String> createdNodes = Collections.synchronizedSet(new HashSet<String>());

        // Add start tasks
        for (Task startTask : graph.getStartTasks()) {
            Node rootNode = new Node(startTask, graph.getStartTasks(), graph, processorCount);
            openList.add(rootNode);
            createdNodes.add(rootNode.toString());
        }

        activeThreads = threadCount;

        /**
         * Main A* loop for each thread
         */
        Runnable searchLoop = () -> {
            boolean active = true;
            while (activeThreads != 0) {
                // check if there are nodes available for expansion in the central list
                Node node = openList.poll();

                // mark this thread's activity status
                if (node == null || node.getCost() >= incumbent.getCost()) {
                    if (active) {
                        active = false;
                        decrementActiveThreads();
                    }
                    continue;
                }

                if (!active) {
                    active = true;
                    incrementActiveThreads();
                }

                // check if goal node
                if (node.getSchedule().getScheduledTasks().size() == graph.getTasks().size()) {
                    updateEncumbent(node);
                }

                // partial expansion - see Oliver's research
                for (Node successorNode : node.getSuccessors(processorCount, graph)) {
                    if (!createdNodes.contains(successorNode.toString())) {
                        openList.add(successorNode);
                    }
                    createdNodes.add(successorNode.toString());
                }
                // if fully expanded, remove the node from the open list
                if (!node.getSchedule().getSchedulableTasks().isEmpty()) {
                    openList.add(node);
                }
            }
        };

        // initialise threads
        for (int i = 0; i < threadCount-1; i++) {
            Thread thread = new Thread(searchLoop);
            thread.start();
        }
        searchLoop.run();

        // return solution
        Node solution = getEncumbent();
        if (solution.getCost() == 0) {
            return null;
        } else {
            return solution;
        }
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