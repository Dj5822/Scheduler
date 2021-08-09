import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

abstract class Node<S extends State, N extends Node<S,N>> {
    protected N parent;
    protected int cost;
    protected S state;

    public Node(N parent, int cost, S state) {
        this.parent = parent;
        this.state = state;
        this.cost = cost;
    }

    public Node(N parent, S state) {
        this.parent = parent;
        this.state = state;
    }

    public Node(S state) {
        this.parent = null;
        this.state = state;
    }

    public S getState() {
        return this.state;
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public N getParent() {
        return this.parent;
    }
}

/**
 * A Node in the search tree. Represents a possible scheduling State for a single Task.
 */
class TaskNode extends Node<State,TaskNode> {
    public TaskNode(TaskNode parent, int cost, State state) {
        super(parent, cost, state);
    }

    public TaskNode(TaskNode parent, State state) {
        super(parent, state);
    }

    public TaskNode(State state) {
        super(state);
    }
}

class BoundedNode extends Node<Schedule,BoundedNode> {
    private HashMap<Schedule, Integer> forgottenMap = new HashMap<Schedule, Integer>();
    
    public BoundedNode(BoundedNode parent, int cost, Schedule state) {
        super(parent, cost, state);
    }
    public BoundedNode(BoundedNode parent,Schedule state) {
        super(parent, state);
    }
    public BoundedNode(Schedule state) {
        super(state);
    }

    public boolean hasBeenExpanded() {
        return !forgottenMap.isEmpty();
    }

    public int updateForgottenSuccessor(BoundedNode node) {
        int cost = forgottenMap.get(node.getState());
        forgottenMap.remove(node.getState());
        return cost;
    }

    public boolean hasForgottenSuccessor(BoundedNode node) {
        return forgottenMap.containsKey(node.getState());
    }

    public void addForgottenSuccessor(BoundedNode node) {
        if (cost > node.getCost() || forgottenMap.isEmpty()) {
            cost = node.getCost();
        }
        forgottenMap.put(node.getState(), node.getCost());
    }

    public int calculateCost() {
        return state.getBackwardsCost() + state.getScheduleFinishTime();
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