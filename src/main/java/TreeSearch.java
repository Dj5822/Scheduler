import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
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
     * Second example heuristic from lectures
     * @param node the node to appraise
     * @return the backwards cost of the node
     */
    private int getBackwardsCost(Node node) {
        int maxCost = 0;
        while (node != null) {
            int bottomLevel = node.getState().getTask().getBottomLevel();
            int startTime = node.getState().getStartTime();
            int pathCost = bottomLevel + startTime;

            if (pathCost > maxCost) {
                maxCost = pathCost;
            }
            node = node.getParent();
        }
        return maxCost;
    }

    /**
     * Calculates the forwards cost for the input node
     * 
     * 
     * @param node the node to appraise
     * @return the forwards cost of the node
     */
    private int getForwardsCost(Node node) {
        return 0; // Greedy search
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
    private ScheduleData getScheduleData(Node node) {

        // initialise collections
        Map<Task, State> scheduled = new HashMap<>();
        Set<Task> children = new HashSet<Task>();
        int[] processorFinishTimes = new int[processorCount];
        ArrayList<Task> schedulable = new ArrayList<>();

        // loop through all nodes in schedule
        while (node != null) {
            State state = node.getState();
            Task task = node.getState().getTask();
            // map scheduled tasks to states
            scheduled.put(task, state);

            // add child tasks to child set
            for (Edge childTaskEdge : task.getChildren()) {
                Task childTask = childTaskEdge.getChild();
                children.add(childTask);
            }

            // set processor finish time to the max finish time of index processor
            int processor = state.getProcessor();
            if (state.getFinishTime() > processorFinishTimes[processor]) {
                processorFinishTimes[processor] = state.getFinishTime();
            }

            node = node.getParent();
        }

        // loop through set of children of scheduled tasks
        // if child is not scheduled and all parents are, child can be scheduled.
        for (Task child : children) {
            if (!scheduled.containsKey(child)) {
                boolean allParentsScheduled = true;
                for (Task parent : child.getParents()) {
                    if (!scheduled.containsKey(parent)) {
                        allParentsScheduled = false;
                        break;
                    }
                }

                if (allParentsScheduled) {
                    schedulable.add(child);
                }
            }
        }

        ScheduleData scheduleData = new ScheduleData(schedulable, scheduled, processorFinishTimes);
        return scheduleData;
    }

    /**
     * An inner class that packages information about a schedule in a quickly accessible structure
     */
    private class ScheduleData {
        public ArrayList<Task> schedulable; // list of tasks able to be scheduled
        public Map<Task, State> scheduled; // map of scheduled tasks to their states
        public int[] processorFinishTimes; // the finish time of each processor, by index.

        public ScheduleData(ArrayList<Task> schedulable,Map<Task, State> scheduled, int[] processorFinishTimes){
            this.schedulable=schedulable;
            this.scheduled=scheduled;
            this.processorFinishTimes = processorFinishTimes;
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
