import java.util.ArrayList;
import java.util.Set;

class Node {
    protected short cost;
    protected Schedule schedule;
    protected ArrayList<Node> successors = null;
    private String stringForm = null;
    protected Set<Task> schedulable;

    public Node(short cost, Schedule schedule) {
        this.schedule = schedule;
        this.cost = cost;
        this.schedulable = schedule.getSchedulableTasks().keySet();
    }

    public Node(Schedule schedule, Graph graph, int numProcessors) {
        this.schedule = schedule;
        this.cost = schedule.getCost(graph, numProcessors);
        this.schedulable = schedule.getSchedulableTasks().keySet();
    }

    public Node(Task task, ArrayList<Task> startTasks, Graph graph, int numProcessors)  {
        this.schedule = new Schedule(task, startTasks);
        this.cost = schedule.getCost(graph, numProcessors);
        this.schedulable = schedule.getSchedulableTasks().keySet();
    }

    public Node() {
        this.cost = Short.MAX_VALUE;
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
    public ArrayList<Schedule> expandNode(int processorCount, Graph graph) {
        int processorsInUse = schedule.getProcessorFinishTimes().length;

        // attempt to minimise repeated branches by limiting duplicate empty processors
        if (processorsInUse < processorCount) {
            processorsInUse += 1;
        }

        ArrayList<Schedule> successorList = new ArrayList<Schedule>();
        // make a child node for every processor * schedulable task
        boolean tight = false;
        ArrayList<Task> examinedTasks = new ArrayList<Task>();
        for (Task task : schedulable) {
            for (byte processor = 0; processor < processorsInUse; processor++) {
                Schedule newSchedule = new Schedule(task, processor, schedule);
                successorList.add(newSchedule);
                if (newSchedule.getCost(graph, processorCount) == schedule.getCost(graph, processorCount)) {
                    tight = true;
                }
            }
            examinedTasks.add(task);
            if (tight) {
                break;
            }
        }
        schedulable.removeAll(examinedTasks);
        return successorList;
    }

    public ArrayList<Node> getSuccessors(int processorCount, Graph graph) {
        ArrayList<Node> successorList = new ArrayList<Node>();
        for (Schedule newSchedule : expandNode(processorCount, graph)) {
            Node node = new Node(newSchedule, graph, processorCount);
            successorList.add(node);
        }
        successors = successorList;
        return successorList;
    }

    public String toString() {
        if (this.stringForm == null) {
            this.stringForm = buildString();
        }
        return this.stringForm;
    }

    private String buildString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TaskVariant task : getSchedule().scheduleOrder) {
            stringBuilder.append(task.getTask().getId());
            stringBuilder.append('%');
            stringBuilder.append(task.getStartTime());
            stringBuilder.append('@');
            stringBuilder.append(task.getProcessor());
            stringBuilder.append('`');
        }
        return stringBuilder.toString();
    }

}