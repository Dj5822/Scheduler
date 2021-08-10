import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

abstract class Node<S extends State, N extends Node<S,N>> {
    protected N parent;
    protected short cost;
    protected S state;
    protected byte depth;

    public Node(N parent, short cost, S state) {
        this.parent = parent;
        this.state = state;
        this.cost = cost;
        this.depth = (byte) (parent.depth + 1);
    }

    public Node(N parent, S state) {
        this.parent = parent;
        this.state = state;
        this.depth = (byte) (parent.depth + 1);
    }

    public Node(S state) {
        this.parent = null;
        this.state = state;
        this.depth = 1;
    }

    public S getState() {
        return this.state;
    }

    public short getCost() {
        return this.cost;
    }

    public void setCost(short cost) {
        this.cost = cost;
    }

    public N getParent() {
        return this.parent;
    }

    public byte getDepth() {
        return depth;
    }

    /**
     * Expands a node, adding children nodes to the priority queue
     * child nodes are every viable schedule that we can reach by adding one
     * additional task to the current schedule (the input node)
     * Number of child nodes of a node is 
     * (number of non-empty processors +1) * (number of schedulable tasks)
     * 
     * @param queue the priority queue of nodes
     * @param node the node to be expanded
     */
    public ArrayList<State> expandNode(Schedule schedule, int processorCount) {
        int processorsInUse = 0;
        for (int i = 0; i < schedule.getProcessorFinishTimes().length; i++) {
            if (schedule.getProcessorFinishTimes()[i] > 0) {
                processorsInUse++;
            }
        }

        // attempt to minimise repeated branches by limiting duplicate empty processors
        if (processorsInUse < processorCount) {
            processorsInUse += 1;
        }

        ArrayList<State> successorList = new ArrayList<State>();
        // make a child node for every processor * schedulable task
        for (Task task : schedule.getSchedulableTasks()) {
            for (byte processor = 0; processor < processorsInUse; processor++) {
                short startTime = schedule.getProcessorFinishTimes()[processor];
                
                // child should not be scheduled before parent finish time (+ communication time)
                for (Task parentTask : task.getParents()) {
                    State parentState = schedule.getScheduledTasks().get(parentTask);
                    short parentFinishTime = parentState.getFinishTime();
                    if (parentState.getProcessor() != processor) {
                        parentFinishTime += task.getParentCommunicationTime(parentTask);
                    }
                    if (startTime < parentFinishTime) {
                        startTime = parentFinishTime;
                    }
                }
                State newState = new State(task, startTime, processor);
                successorList.add(newState);
            }
        }
        return successorList;
    }

    /**
     * Calculates the backwards cost for the input node
     * The backwards cost is
     * Max (start time of scheduled tasks plus their bottom level)
     * Second example heuristic from lectures
     * @param node the node to appraise
     * @return the backwards cost of the node
     */
    protected short getBackwardsCost() {
        short maxCost = 0;
        Node<S,N> node = this;
        while (node != null) {
            short bottomLevel = node.getState().getTask().getBottomLevel();
            short startTime = node.getState().getStartTime();
            short pathCost = (short) (bottomLevel + startTime);

            if (pathCost > maxCost) {
                maxCost = pathCost;
            }
            node = node.getParent();
        }
        return maxCost;
    }

    abstract public ArrayList<N> getSuccessors(Schedule schedule, int processorCount);
}

/**
 * A Node in the search tree. Represents a possible scheduling State for a single Task.
 */
class TaskNode extends Node<State,TaskNode> {

    public TaskNode(TaskNode parent, short cost, State state) {
        super(parent, cost, state);
    }

    public TaskNode(TaskNode parent, State state) {
        super(parent, state);
        short cost = state.getFinishTime();
        if (parent.getCost() > cost) {
            cost = parent.getCost();
        }
        this.cost = (short) (cost + getBackwardsCost());
    }

    public TaskNode(State state) {
        super(state);
        this.cost = (short) (getBackwardsCost() + state.getFinishTime());
    }

    public ArrayList<TaskNode> getSuccessors(Schedule schedule, int processorCount) {
        ArrayList<TaskNode> successorList = new ArrayList<TaskNode>();
        for (State newState : expandNode(schedule, processorCount)) {
            TaskNode node = new TaskNode(this, newState);
            successorList.add(node);
        }
        return successorList;
    }
}

class BoundedNode extends Node<State,BoundedNode> {
    private HashMap<State, Short> forgottenMap = new HashMap<State, Short>();
    
    public BoundedNode(BoundedNode parent, short cost, State state) {
        super(parent, cost, state);
    }
    public BoundedNode(BoundedNode parent,State state) {
        super(parent, state);
    }
    public BoundedNode(State state) {
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

    public ArrayList<BoundedNode> getForgottenSuccessors() {
        ArrayList<BoundedNode> successors = new ArrayList<BoundedNode>();
        for (Entry<State, Short> entry : forgottenMap.entrySet()) {
            BoundedNode successor = new BoundedNode(this, entry.getValue(), entry.getKey());
            successors.add(successor);
        }
        return successors;
    }

    public ArrayList<BoundedNode> getSuccessors(Schedule schedule, int processorCount) {
        ArrayList<BoundedNode> successorList = new ArrayList<BoundedNode>();
        for (State newState : expandNode(schedule, processorCount)) {
            BoundedNode node = new BoundedNode(this, newState);
            successorList.add(node);
        }
        return successorList;
    }

}