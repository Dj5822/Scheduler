import java.util.ArrayList;
import java.util.HashSet;


/**
 * Representation of Graph structure as a set of tasks.
 * In our problem, the graph is read-only.
 */
public class Graph {
    public HashSet<Task> tasks;

    public Graph(HashSet<Task> tasks) {
        this.tasks = tasks;
        setBottomLevels();
    }

    /**
     * @return list of start tasks (tasks with no parents)
     */
    public ArrayList<Task>  getStartTasks() {
        ArrayList<Task> rootTasks = new ArrayList<Task>();
        for (Task task : tasks) {
            if (task.isRootTask()) {
                rootTasks.add(task);
            }
        }
        return rootTasks;
    }

    /**
     * Find bottom level of all start tasks,
     * And therefore the whole graph.
     */
    private void setBottomLevels() {
        for (Task task : getStartTasks()) {
            task.findBottomLevel();
        }
    }

    /**
     * Debugging tool - prints out the bottom level of every task
     */
    public void printBottomLevels() {
        System.out.println("Bottom Levels:");
        for (Task task : tasks) {
            if (task.getBottomLevel() != null) {
                System.out.println(task.getId() + ": " + task.getBottomLevel());
            }
        }
    }
}
