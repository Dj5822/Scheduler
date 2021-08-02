import java.util.ArrayList;

/**
 * Task defines a task from the graph that is inputted into the system,
 * it stores its bottomlevel and weight as well as the list of both 
 * parent and children edges
 */
public class Task {
    private ArrayList<Edge> parents;
    private ArrayList<Edge> children;
    private int bottomLevel;
    private int weight;

    /**
     * Gets the BottomLevel of the task, which is the 
     * maximum cost to travel to the end point from the current task
     * 
     * @return The bottom level of this task
     */
    int getBottomLevel(){return bottomLevel;}

    /**
     * Gets the weight of the task, which is the
     * amount of time that the task takes to execute
     * 
     * @return the weight of this task
     */
    int getWeight(){return weight;}

    /**
     * Gets a list of the tasks children, as a list
     * of edges, based off the list of edges that the
     * task stores
     * 
     * @return The list of edges that point to children 
     * of the current task
     */
    public ArrayList<Edge> getChildren() {
        return children;
    }

     /**
     * Gets a list of the tasks parents, as a list
     * of edges, based off the list of edges that the
     * task stores
     * 
     * @return The list of edges that point to parents 
     * of the current task
     */
    public ArrayList<Task> getParents() {
        return parents;
    }
}
