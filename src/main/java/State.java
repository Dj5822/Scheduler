import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a possible scheduling State for a single Task.
 **/
public class State {
    protected Task task;
    protected int startTime;
    protected int processor;

    public State(Task task, int startTime, int processor) {
        this.task = task;
        this.startTime = startTime;
        this.processor = processor;
    }

    public State(Task task) {
        this.task = task;
        this.startTime = 0;
        this.processor = 0;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public int getFinishTime() {
        return this.startTime + task.getWeight();
    }

    public int getProcessor() {
        return this.processor;
    }

    public Task getTask() {
        return this.task;
    }

    // For testing
    public void printState() {
        System.out.println("Task ID: " + task.getId());
        System.out.println("start time: " + startTime);
        System.out.println("processor: " + processor);
        System.out.println();
    }

}

class Schedule extends State {
    private HashMap<Task,State> scheduled;
    private ArrayList<Task> schedulable;
    private int[] processorFinishTimes;

    /**
     * Constructor for a schedule derived from a parent schedule
     * @param task most recently scheduled task
     * @param startTime start time of parameter task
     * @param processor processor assigned to parameter task
     * @param parentSchedule parent schedule to derive from
     */
    public Schedule(Task task, int startTime, int processor, Schedule parentSchedule) {
        super(task, startTime, processor);

        scheduled = new HashMap<Task,State>(parentSchedule.getScheduledTasks());
        scheduled.put(task, this);

        schedulable = new ArrayList<Task>(parentSchedule.getSchedulableTasks());
        schedulable.remove(task);

        processorFinishTimes = parentSchedule.getProcessorFinishTimes().clone();
        processorFinishTimes[processor] = processorFinishTimes[getFinishTime()];

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

    /**
     * Constructor for a start task schedule
     * @param task start task
     * @param processorCount number of processors to schedule for
     * @param startTasks array of every start task
     */
    public Schedule(Task task, short processorCount, ArrayList<Task> startTasks) {
        super(task);

        scheduled = new HashMap<Task,State>();
        scheduled.put(task, this);

        schedulable = new ArrayList<Task>(startTasks);
        schedulable.remove(task);
        
        processorFinishTimes = new int[processorCount];
        processorFinishTimes[processor] = processorFinishTimes[getFinishTime()];

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

    public int[] getProcessorFinishTimes() {
        return processorFinishTimes;
    }
}