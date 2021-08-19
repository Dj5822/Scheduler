import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

abstract class Node<N extends Node<N>> {
    protected short cost;
    protected Schedule schedule;

    public Node(short cost, Schedule schedule) {
        this.schedule = schedule;
    }

    public Node(Schedule schedule) {
        this.schedule = schedule;
    }

    public Node(Task task, ArrayList<Task> startTasks)  {
        this.schedule = new Schedule(task, startTasks);
    }

    public Node(ArrayList<Task> startTasks) {
        this.schedule = new Schedule(startTasks);
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public short getCost() {
        return this.cost;
    }

    public void setCost(short cost) {
        this.cost = cost;
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
    public ArrayList<Schedule> expandNode(int processorCount) {
        int processorsInUse = schedule.getProcessorFinishTimes().length;

        // attempt to minimise repeated branches by limiting duplicate empty processors
        if (processorsInUse < processorCount) {
            processorsInUse += 1;
        }

        ArrayList<Schedule> successorList = new ArrayList<Schedule>();
        // make a child node for every processor * schedulable task
        for (Task task : schedule.getSchedulableTasks()) {
            for (byte processor = 0; processor < processorsInUse; processor++) {
                Schedule newSchedule = new Schedule(task, processor, schedule);
                successorList.add(newSchedule);
            }
        }
        return successorList;
    }

    abstract public ArrayList<N> getSuccessors(int processorCount);
}

/**
 * A Node in the search tree. Represents a possible scheduling State for a single Task.
 */
class TaskNode extends Node<TaskNode> {

    public TaskNode(short cost, Schedule schedule) {
        super(cost, schedule);
    }

    public TaskNode(Schedule schedule) {
        super(schedule);
    }

    public TaskNode(Task task, ArrayList<Task> startTasks) {
        super(task, startTasks);
    }

    public TaskNode(ArrayList<Task> startTasks) {
        super(startTasks);
    } 

    public ArrayList<TaskNode> getSuccessors(int processorCount) {
        ArrayList<TaskNode> successorList = new ArrayList<TaskNode>();
        for (Schedule newSchedule : expandNode(processorCount)) {
            TaskNode node = new TaskNode(newSchedule);
            successorList.add(node);
        }
        return successorList;
    }
}

class BoundedNode extends Node<BoundedNode> {
    private HashMap<Schedule, Short> forgottenMap = new HashMap<Schedule, Short>();
    private int numChildren = 0;
    private BoundedNode parent;
    
    public BoundedNode(BoundedNode parent, short cost, Schedule schedule) {
        super(cost, schedule);
        this.parent = parent;
    }
    public BoundedNode(BoundedNode parent,Schedule schedule) {
        super(schedule);
        this.parent = parent;
    }
    public BoundedNode(Task task, ArrayList<Task> startTasks) {
        super(task, startTasks);
        this.parent = null;
    }

    public boolean hasBeenExpanded() {
        return !forgottenMap.isEmpty();
    }

    public void removeForgottenSuccessor(BoundedNode node) {
        forgottenMap.remove(node.getSchedule());
    }

    public boolean hasForgottenSuccessor(BoundedNode node) {
        return forgottenMap.containsKey(node.getSchedule());
    }

    public void addForgottenSuccessor(BoundedNode node) {
        if (cost > node.getCost() || forgottenMap.isEmpty()) {
            cost = node.getCost();
        }
        forgottenMap.put(node.getSchedule(), node.getCost());
        numChildren--;
    }

    public ArrayList<BoundedNode> getForgottenSuccessors() {
        ArrayList<BoundedNode> successors = new ArrayList<BoundedNode>();
        for (Entry<Schedule, Short> entry : forgottenMap.entrySet()) {
            BoundedNode successor = new BoundedNode(this, entry.getValue(), entry.getKey());
            successors.add(successor);
        }
        numChildren+=successors.size();
        return successors;
    }

    public ArrayList<BoundedNode> getSuccessors(int processorCount) {
        ArrayList<BoundedNode> successorList = new ArrayList<BoundedNode>();
        for (Schedule newSchedule : expandNode(processorCount)) {
            BoundedNode node = new BoundedNode(this, newSchedule);
            successorList.add(node);
        }
        numChildren+=successorList.size();
        return successorList;
    }

    public boolean isLeafNode() {
        return numChildren == 0;
    }

    public int getChildCount() {
        return numChildren;
    }

    public BoundedNode getParent() {
        return parent;
    }

}