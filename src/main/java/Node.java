import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * A Node in the search tree. Represents a possible scheduling State for a single Task.
 */
class Node {
    protected Node parent;
    protected int cost;
    protected State state;

    /**
     * Constructor for a search tree node
     * @param parent
     * @param cost
     * @param state
     */
    public Node(Node parent, int cost, State state) {
        this.parent = parent;
        this.state = state;
        this.cost = cost;
    }

    public Node(Node parent, State state) {
        this.parent = parent;
        this.state = state;
    }

    /**
     * Constructor for the root node, sets time and processor to zero.
     * @param task
     */
    public Node(Task task) {
        this.parent = null;
        this.state = new State(task, 0, 1);
    }

    public State getState() {
        return (Schedule) this.state;
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Node getParent() {
        return this.parent;
    }

    /**
     * Calculates the estimated remaining time
     * Max (start time of scheduled tasks plus their bottom level)
     * Second example heuristic from lectures
     * @return the backwards cost of the node
     */
    protected int getBackwardsCost() {
        int maxCost = 0;
        Node node = this;
        while (node != null) {
            int bottomLevel = node.getState().getTask().getBottomLevel();
            int startTime = node.getState().getStartTime();
            int pathCost = bottomLevel + startTime;

            if (pathCost > maxCost) {
                maxCost = pathCost;
            }
            node = getParent();
        }
        return maxCost;
    }
}

class BoundedNode extends Node {
    private Schedule state;
    private HashMap<Schedule, Integer> forgottenMap = new HashMap<Schedule, Integer>();
    
    public BoundedNode(Node parent, int cost, Schedule state) {
        super(parent, cost, state);
    }
    public BoundedNode(Node parent, Schedule state) {
        super(parent, state);
        setCost(calculateCost());
    }
    public BoundedNode(Task task) {
        super(task);
    }

    public Schedule getState() {
        return state;
    }

    public boolean hasBeenExpanded() {
        return forgottenMap.isEmpty();
    }

    public int updateForgottenSuccessor(BoundedNode node) {
        int cost = forgottenMap.get(node.getState());
        forgottenMap.remove(node.getState());
        return cost;
    }

    public boolean hasForgottenSuccessor(BoundedNode node) {
        return forgottenMap.containsKey(node.getState());
    }

    private int calculateCost() {
        return getBackwardsCost() + state.getScheduleFinishTime();
    }

    public ArrayList<BoundedNode> getForgottenSuccessors() {
        ArrayList<BoundedNode> successors = new ArrayList<BoundedNode>();
        for (Entry<Schedule, Integer> entry : forgottenMap.entrySet()) {
            BoundedNode successor = new BoundedNode(this, entry.getValue(), entry.getKey());
            successors.add(successor);
        }
        return successors;
    }

    public ArrayList<BoundedNode> getSuccessors(int processorCount) {
        ArrayList<BoundedNode> successors = new ArrayList<BoundedNode>();
        int processorsInUse = state.getProcessorFinishTimes().length;
        if (processorsInUse < processorCount) {
            processorsInUse++;
        }

        for (Task childTask : state.getSchedulableTasks()) {
            for (int processor = 0; processor < processorsInUse; processor++) {
                Schedule newState = new Schedule(childTask, processor, state);
                BoundedNode newNode = new BoundedNode(this, newState);
                successors.add(newNode);
            }
        }

        return successors;
    }
}