import java.util.ArrayList;
import java.util.HashMap;

class Schedule {
    private HashMap<Task,TaskVariant> scheduled;
    private ArrayList<Task> schedulable;
    private short[] processorFinishTimes;
    protected short bottomLevelHeuristic = 0;
    private boolean expanded = false;
    protected short idleTime = 0;

    /**
     * Constructor for a schedule derived from a parent schedule
     * @param task most recently scheduled task
     * @param startTime start time of parameter task
     * @param processor processor assigned to parameter task
     * @param parentSchedule parent schedule to derive from
     */
    public Schedule(Task task, byte processor, Schedule parentSchedule) {

        schedulable = new ArrayList<Task>(parentSchedule.getSchedulableTasks());
        schedulable.remove(task);

        scheduled = new HashMap<Task, TaskVariant>(parentSchedule.getScheduledTasks());

        short[] parentTimes = parentSchedule.getProcessorFinishTimes();
        if (parentTimes == null) {
            parentTimes = new short[1];
        }
        if (processor < parentSchedule.getProcessorFinishTimes().length) {
            processorFinishTimes = parentSchedule.getProcessorFinishTimes().clone();
        } else {
            processorFinishTimes = new short[processor + 1];
            System.arraycopy(parentTimes, 0, processorFinishTimes, 0, parentTimes.length);
        }

        short startTime = processorFinishTimes[processor];
        for (Task parentTask : task.getParents()) {
            TaskVariant parentState = getTaskState(parentTask);
            short parentFinishTime = parentState.getFinishTime();
            if (parentState.getProcessor() != processor) {
                parentFinishTime += task.getParentCommunicationTime(parentTask);
            }
            if (startTime < parentFinishTime) {
                startTime = parentFinishTime;
            }
        }
        TaskVariant state = new TaskVariant(task, startTime, processor);
        scheduled.put(task, state);

        this.idleTime = (short)(parentSchedule.idleTime + state.getFinishTime() - processorFinishTimes[processor]);
        processorFinishTimes[processor] = state.getFinishTime();

        bottomLevelHeuristic = parentSchedule.bottomLevelHeuristic;
        short tempBackwardsCost = (short) (startTime + task.getBottomLevel());
        if (tempBackwardsCost > bottomLevelHeuristic) {
            bottomLevelHeuristic = tempBackwardsCost;
        }

        // mark children whose parents are all scheduled for scheduling
        for (Edge childEdge : task.getChildren()) {
            Task child = childEdge.getChild();
            boolean allParentsScheduled = true;
            for  (Task parentTask : child.getParents()) {
                if (!taskisScheduled(parentTask)) {
                    allParentsScheduled = false;
                    break;
                }
            }
            if (allParentsScheduled) {
                schedulable.add(child);
            }
        }
    }

    public Schedule(Task task, ArrayList<Task> startTasks) {
        this.scheduled = new HashMap<Task, TaskVariant>();
        scheduled.put(task, new TaskVariant(task));
        
        schedulable = new ArrayList<Task>(startTasks);
        schedulable.remove(task);

        processorFinishTimes = new short[1];
        processorFinishTimes[0] = task.getWeight();

        this.bottomLevelHeuristic = task.getBottomLevel();

        // mark children whose parents are all scheduled for scheduling
        for (Edge childEdge : task.getChildren()) {
            Task child = childEdge.getChild();
            boolean allParentsScheduled = true;
            for  (Task parentTask : child.getParents()) {
                if (!taskisScheduled(parentTask)) {
                    allParentsScheduled = false;
                    break;
                }
            }
            if (allParentsScheduled) {
                schedulable.add(child);
            }
        }
    }

    public boolean taskisScheduled(Task task) {
        return scheduled.containsKey(task);
    }

    public TaskVariant getTaskState(Task task) {
        return scheduled.get(task);
    }

    public HashMap<Task,TaskVariant> getScheduledTasks() {
        return scheduled;
    }

    public ArrayList<Task> getSchedulableTasks() {
        return schedulable;
    }

    public short[] getProcessorFinishTimes() {
        return processorFinishTimes;
    }

    public short getBackwardsCost(Graph graph, int numProcessors) {
        return (short)Math.max(bottomLevelHeuristic, (graph.getTotalWeight() + idleTime)/numProcessors);
    }

    public short getCost(Graph graph, int numProcessors) {
        return (short) (getBackwardsCost(graph, numProcessors) + getFinishTime());
    }

    public short getFinishTime() {
        short max = 0;
        for (int i = 0; i < processorFinishTimes.length; i++) {
            if (max < processorFinishTimes[i]) {
                max = processorFinishTimes[i];
            }
        }
        return max;
    }

    public boolean hasBeenExpanded() {
        return expanded;
    }

    public void setExpanded() {
        expanded = true;
    }
}