import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

class Schedule {
    private HashMap<Task,TaskVariant> scheduled;
    private HashMap<Task,Short> schedulable;
    protected ArrayList<TaskVariant> scheduleOrder;
    private short[] processorFinishTimes;
    protected short bottomLevelHeuristic = 0;
    protected short dataReadyHeuristic = 0;
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

        schedulable = new HashMap<Task,Short>(parentSchedule.getSchedulableTasks());
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

        this.scheduleOrder = new ArrayList<TaskVariant>(parentSchedule.scheduleOrder);
        int i = 0;
        for (; i< scheduleOrder.size(); i++) {
            if (scheduleOrder.get(i).getTask().getId().compareTo(task.getId()) > 0) {
                scheduleOrder.add(i, state);
                break;
            }
        }
        if (i == scheduleOrder.size()) {
            scheduleOrder.add(state);
        }

        this.idleTime = (short)(parentSchedule.idleTime + state.getStartTime() - processorFinishTimes[processor]);
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
                schedulable.put(child,getMinDataReadyTime(child));
            }
        }
    }

    public Schedule(Task task, ArrayList<Task> startTasks) {
        this.scheduled = new HashMap<Task, TaskVariant>();
        TaskVariant state = new TaskVariant(task);
        scheduled.put(task, state);

        this.scheduleOrder = new ArrayList<TaskVariant>(1);
        scheduleOrder.add(state);
        
        schedulable = new HashMap<Task,Short>(startTasks.size());
        for (Task startTask : startTasks) {
            schedulable.put(startTask, (short)0);
        }
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
                schedulable.put(child,getMinDataReadyTime(child));
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

    public HashMap<Task,Short> getSchedulableTasks() {
        return schedulable;
    }

    public short[] getProcessorFinishTimes() {
        return processorFinishTimes;
    }

    public short getBackwardsCost(Graph graph, int numProcessors) {
        return (short)Math.max(Math.max(bottomLevelHeuristic,DataReadyHeuristicStart()), (graph.getTotalWeight() + idleTime)/numProcessors);
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

    private short getdataReadyTime(Task task, byte processor) {
        short maxDrt = 0;
        for (Task parentTask : task.getParents()) {
            TaskVariant parentState = getTaskState(parentTask);
            short drt = parentState.getFinishTime();
            if (parentState.getProcessor() != processor) {
                drt = (short)(drt + task.getParentCommunicationTime(parentTask));
            }
            if (drt > maxDrt) {
                maxDrt = drt;
            }
        }
        return maxDrt;
    }

    private short getMinDataReadyTime(Task task) {
        short minDRT = Short.MAX_VALUE;
        for (byte processor=0; processor < processorFinishTimes.length;processor++) {
            short maxDrt = getdataReadyTime(task, processor);
            if (maxDrt < minDRT) {
                minDRT = maxDrt;
            }
        }
        return minDRT;
    }

    private short DataReadyHeuristicStart() {
        short max = 0;
        for (Entry<Task,Short> entry : getSchedulableTasks().entrySet()) {
            short heuristicValue = (short)(entry.getKey().getBottomLevel() + entry.getValue());
            if (heuristicValue > max) {
                max = heuristicValue;
            }
        }
        dataReadyHeuristic = max;
        return max;
    }
}