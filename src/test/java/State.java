/**
 * Represents a possible scheduling State for a single Task.
 **/
public class State {
    private Task task;
    private int startTime;
    private int processor;

    public State(Task task, int startTime, int processor) {
        this.task = task;
        this.startTime = startTime;
        this.processor = processor;
    }

    /**
     * @return The start time for this scheduled task
     */
    public int getStartTime() {
        return this.startTime;
    }

    /**
     * @return The finish time for this scheduled task
     */
    public int getFinishTime() {
        return this.startTime + task.getWeight();
    }

    /**
     * @return The processor that the task is scheduled on
     */
    public int getProcessor() {
        return this.processor;
    }

    /**
     * @return The task that is scheduled in this state
     */
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
