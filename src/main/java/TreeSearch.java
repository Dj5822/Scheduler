import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * The method responsible for the creation and traversal of the
 * schedule tree, a tree of each possible schedule and partial schedule
 */
public class TreeSearch {

    private Graph graph;
    private int processorCount;
    private boolean visualize;

    private Visualiser visualiser;

    private Node incumbent = new Node();
    private int activeThreads = 0;

    private long startTime;
    private int expandedNodesCount;

    private Schedule currentSchedule;
    private Timer updateTimer;


    public TreeSearch(Graph graph, int processorCount, boolean visualize){
        this.graph = graph;
        this.processorCount = processorCount;
        this.visualize = visualize;

        this.expandedNodesCount = 0;

        if (this.visualize) {
            // Sets up the visualiser.
            new Thread(() -> {
                Visualiser.launch(Visualiser.class, "" + processorCount);
            }).start();
            while (visualiser == null) {
                this.visualiser = Visualiser.getVisualiser();
            }

            // Record the start time.
            startTime = System.currentTimeMillis();

            // Updates the visualiser every second.
            updateTimer = new Timer();
            updateTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateVisualiser();
                }
            }, 0, 150);
        }
    }

    private void updateVisualiser() {
        Platform.runLater(() -> visualiser.updateVisualiser(currentSchedule, expandedNodesCount,
                Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory(),
                (new Date()).getTime() - startTime));
    }

    private void endVisualiser() {
        Platform.runLater(() -> visualiser.finish(currentSchedule, expandedNodesCount,
                Runtime.getRuntime().totalMemory(), Runtime.getRuntime().freeMemory(),
                (new Date()).getTime() - startTime));
    }

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
            expandedNodesCount ++;
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

                currentSchedule = node.getSchedule();

                // partial expansion - see Oliver's research
                for (Node successorNode : node.getSuccessors(processorCount, graph)) {
                    if (!createdNodes.contains(successorNode.toString())) {
                        openList.add(successorNode);
                        expandedNodesCount ++;
                    }
                    createdNodes.add(successorNode.toString());
                }
                // if fully expanded, remove the node from the open list
                if (!node.getSchedule().getSchedulableTasks().isEmpty()) {
                    openList.add(node);
                    expandedNodesCount ++;
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
            currentSchedule = solution.getSchedule();

            if (visualize) {
                endVisualiser();
                updateTimer.cancel();
            }
            
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