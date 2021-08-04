/**
 * Represents a possible scheduling State for a single Task.
 **/
public class State {
    private Task task;
    private int start_time;
    private int processor;

    public State(Task task, int start_time, int processor) {
        this.task = task;
        this.start_time = start_time;
        this.processor = processor;
    }

    public int get_finish_time() {
        return this.start_time + task.get_compute_time();
    }

    public int get_processor() {
        return this.processor;
    }

}
