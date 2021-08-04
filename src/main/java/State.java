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

    public int getFinishTime() {
        return this.startTime + task.getWeight();
    }

    public int getProcessor() {
        return this.processor;
    }

}
