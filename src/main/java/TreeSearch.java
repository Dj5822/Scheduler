import java.sql.Struct;
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
        bruteForceTest();
    }

    /**
     * For testing purposes.
     */
    private void bruteForceTest() {
        ArrayList<Node> openNodeList = new ArrayList<>();

        for (Task task : graph.getStartTasks()) {
            State state = new State(task, 0, 0);
            openNodeList.add(new Node(null, state.getFinishTime(), state));
        }

        while (!openNodeList.isEmpty()) {
            ArrayList<Node> newOpenNodeList = new ArrayList<>();

            // Go through every node in the openNodeList.
            for (Node node: openNodeList) {
                // Figure out which nodes are schedulable.
                Map<Task, State> scheduled = new HashMap<>();
                ArrayList<Task> schedulable = new ArrayList<>();
                int[] processorFinishTimes = new int[processorCount];

                Node currentNode = node;

                // Get scheduled tasks.
                while (currentNode != null) {
                    scheduled.put(currentNode.getState().getTask(), currentNode.getState());
                    if (processorFinishTimes[currentNode.getState().getProcessor()] == 0) {
                        processorFinishTimes[currentNode.getState().getProcessor()] = currentNode.getState().getFinishTime();
                    }
                    currentNode = currentNode.getParent();
                }

                // Get schedulable tasks.
                for (Task task: scheduled.keySet()) {
                    for (Edge edge: task.getChildren()) {
                        if (checkParentsVisited(edge.getChild(), scheduled)) {
                            if (!scheduled.containsKey(edge.getChild())) {
                                schedulable.add(edge.getChild());
                            }
                        }
                    }
                }

                // Loop through schedulable tasks.
                for (Task task: schedulable) {
                    for (int i=0; i<processorCount; i++) {
                        /*
                         1) Task must be scheduled after the finish time of the processor
                         it is going to get assigned to.

                         2) Task must be scheduled after the latest finish time
                         of all the parents.

                         3) If the task is being assigned to a process that is different
                         to that of it's parent, you must add in the communication time.
                         */
                        int startTime = processorFinishTimes[i];

                        for (Task parentTask: task.getParents()) {
                            int parentFinishTime = scheduled.get(parentTask).getFinishTime();
                            if (scheduled.get(parentTask).getProcessor() != i) {
                                parentFinishTime += task.getParentCommunicationTime(parentTask);
                            }
                            if (parentFinishTime > startTime) {
                                startTime = parentFinishTime;
                            }
                        }

                        State newState = new State(task, startTime, i);
                        int newCost = Math.max(node.getCost(), newState.getFinishTime());

                        newOpenNodeList.add(new Node(node, newCost, newState));
                    }
                }
            }
            if (newOpenNodeList.isEmpty()) {
                break;
            }
            else {
                openNodeList = newOpenNodeList;
            }
        }

        System.out.println(openNodeList);
    }

    // Parents visited constraint.
    private boolean checkParentsVisited(Task task, Map<Task, State> scheduled) {
        for (Task parent: task.getParents()) {
            if (!scheduled.containsKey(parent)) {
                return false;
            }
        }
        return true;
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
