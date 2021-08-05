import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;

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
        bruteForceTest();
    }

    /**
     * For testing purposes.
     */
    private void bruteForceTest() {
        ArrayList<Task> startTasks = graph.getStartTasks();
        Map<Task, State> scheduled = new HashMap<>();
        ArrayList<Task> schedulable = new ArrayList<>();
        ArrayList<Node> openNodeList = new ArrayList<>();
        schedulable.addAll(startTasks);

        openNodeList.add(null);

        while (!schedulable.isEmpty()) {
            Task currentTask = schedulable.get(0);
            State state = null;

            ArrayList<Node> newOpenNodeList = new ArrayList<>();

            // Go through every node in the openNodeList.
            for (Node node: openNodeList) {
                if (node == null) {
                    state = new State(currentTask, 0, 0);
                    newOpenNodeList.add(new Node(null, state.getFinishTime(), state));

                    state.printState();
                    scheduled.put(schedulable.get(0), state);
                }
                else {
                    for (int i=0; i<processorCount; i++) {
                        state = new State(currentTask, node.getCost(), i);
                        newOpenNodeList.add(new Node(node, state.getFinishTime(), state));

                        state.printState();
                        scheduled.put(schedulable.get(0), state);
                    }
                }
            }

            openNodeList = newOpenNodeList;

            // Parents visited constraint.
            ArrayList<Edge> edges = currentTask.getChildren();
            for (Edge edge: edges) {
                Task candidate = edge.getChild();
                boolean allParentsScheduled = true;
                for (Task parent : candidate.getParents()) {
                    if (!scheduled.containsKey(parent)) {
                        allParentsScheduled = false;
                    }
                }

                if (allParentsScheduled){
                    schedulable.add(edge.getChild());
                }
            }

            // Each task can only be scheduled once.
            schedulable.remove(0);
        }
    }


    /**
     * Implementation of the A* algorithm, traversing the tree and creating a
     * viable schedule from it
     * 
     * @return The node that is at the end of the created schedule
     */
    public Node aStar() {
        PriorityQueue<Node> openList = new PriorityQueue<>(new NodeComparator());
        return openList.poll();
    }

    /**
     * Expands a node, adding children nodes to the priority queue
     * child nodes are every viable schedule that we can reach by adding one
     * additional task to the current schedule (the input node)
     * Number of child nodes of a node is 
     * (number of non-empty processors +1) * (number of child tasks)
     * 
     * @param queue the priority queue of nodes
     * @param node the node to be expanded
     */
    private void expandNode(PriorityQueue queue, Node node) {

    }

    /**
     * Calculates the backwards cost for the input node
     * The backwards cost is
     * Max (start time of scheduled tasks plus their bottom level)
     * 
     * @param node the node to appraise
     * @return the backwards cost of the node
     */
    private int getBackwardsCost(Node node) {
        int backwardsCost = 0;
        return backwardsCost;
    }

    /**
     * Calculates the forwards cost for the input node
     * 
     * 
     * @param node the node to appraise
     * @return the forwards cost of the node
     */
    private int getForwardsCost(Node node) {
        return 0;
    }


    /**
     * Move up schedule, creating map of scheduled tasks to states
     * and a set of children tasks
     * 
     * go through set of children, check if they exist in scheduled map
     * and if parents exist in scheduled
     * 
     * if all parents are scheduled and child isn't, add to schedulable list
     * 
     * @param node The node to get the lists of tasks from
     * 
     * @return A pair containing the list of all schedulable tasks and
     * a map of each scheduled task to its state
     */
    private Pair getTaskLists(Node node) {
        ArrayList<Task> schedulable = new ArrayList<>();
        Map<Task, State> scheduled = new HashMap<>();

        Pair pair = new Pair(schedulable, scheduled);
        return pair;
    }

    /**
     * A inner class that packages the list of schedulable tasks and the
     * map of scheduled tasks, in order to be outputted cleanly by getTaskLists
     */
    private class Pair {
        public ArrayList<Task> schedulable;
        public Map<Task, State> scheduled;
        public Pair(ArrayList<Task> schedulable,Map<Task, State> scheduled){
            this.schedulable=schedulable;
            this.scheduled=scheduled;
        }
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
