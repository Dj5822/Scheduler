import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

class Node {
    protected short cost;
    protected Schedule schedule;

    public Node(short cost, Schedule schedule) {
        this.schedule = schedule;
        this.cost = cost;
    }

    public Node(Schedule schedule, Graph graph, int numProcessors) {
        this.schedule = schedule;
        this.cost = schedule.getCost(graph, numProcessors);
    }

    public Node(Task task, ArrayList<Task> startTasks, Graph graph, int numProcessors)  {
        this.schedule = new Schedule(task, startTasks);
        this.cost = schedule.getCost(graph, numProcessors);
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

    public ArrayList<Node> getSuccessors(int processorCount, Graph graph) {
        ArrayList<Node> successorList = new ArrayList<Node>();
        for (Schedule newSchedule : expandNode(processorCount)) {
            Node node = new Node(newSchedule, graph, processorCount);
            successorList.add(node);
        }
        return successorList;
    }

}