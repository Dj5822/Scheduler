/**
 * Represents a possible scheduling State for a single Task.
 **/
public class State {
    protected Task task;
    protected short startTime;
    protected byte processor;

    public State(Task task, short startTime, byte processor) {
        this.task = task;
        this.startTime = startTime;
        this.processor = processor;
    }

    public State(Task task, byte processor) {
        this.task = task;
        this.processor = processor;
    }

    public State(Task task) {
        this.task = task;
        this.startTime = 0;
        this.processor = 0;
    }

    public short getStartTime() {
        return this.startTime;
    }

    public short getFinishTime() {
        return (short) (this.startTime + task.getWeight());
    }

    public byte getProcessor() {
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