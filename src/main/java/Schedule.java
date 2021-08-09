import java.util.ArrayList;
import java.util.HashMap;

class Schedule {
    private HashMap<Task,State> scheduled;
    private ArrayList<Task> schedulable;
    private short[] processorFinishTimes;
    private int backwardsCost = 0;

    public Schedule(ArrayList<Task> schedulable, HashMap<Task,State> scheduled, short[] processorFinishTimes, int backwardsCost) {
        this.schedulable = schedulable;
        this.scheduled = scheduled;
        this.processorFinishTimes = processorFinishTimes;
        this.backwardsCost = backwardsCost;
    }

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

        scheduled = new HashMap<Task,State>(parentSchedule.getScheduledTasks());

        short[] parentTimes = parentSchedule.getProcessorFinishTimes();
        if (processor + 1 < parentSchedule.getProcessorFinishTimes().length) {
            processorFinishTimes = parentSchedule.getProcessorFinishTimes().clone();
        } else {
            processorFinishTimes = new short[processor + 1];
            System.arraycopy(parentTimes, 0, processorFinishTimes, 0, parentTimes.length);
        }

        short startTime = processorFinishTimes[processor];
        for (Task parentTask : task.getParents()) {
            State parentState = getTaskState(parentTask);
            short parentFinishTime = parentState.getFinishTime();
            if (parentState.getProcessor() != processor) {
                parentFinishTime += task.getParentCommunicationTime(parentTask);
            }
            if (startTime < parentFinishTime) {
                startTime = parentFinishTime;
            }
        }
        State state = new State(task, startTime, processor);
        scheduled.put(task, state);

        processorFinishTimes[processor] = state.getFinishTime();

        int tempBackwardsCost = startTime + task.getBottomLevel();
        if (tempBackwardsCost > backwardsCost) {
            backwardsCost = tempBackwardsCost;
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

    public boolean taskisScheduled(Task task) {
        return scheduled.containsKey(task);
    }

    public State getTaskState(Task task) {
        return scheduled.get(task);
    }

    public HashMap<Task,State> getScheduledTasks() {
        return scheduled;
    }

    public ArrayList<Task> getSchedulableTasks() {
        return schedulable;
    }

    public short[] getProcessorFinishTimes() {
        return processorFinishTimes;
    }

    public int getBackwardsCost() {
        return backwardsCost;
    }

    public int getFinishTime() {
        int max = 0;
        for (int i = 0; i < processorFinishTimes.length; i++) {
            if (max < processorFinishTimes[i]) {
                max = processorFinishTimes[i];
            }
        }
        return max;
    }
}