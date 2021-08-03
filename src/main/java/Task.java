import java.util.ArrayList;
import java.util.HashMap;

/**
 * Task defines a task from the graph that is inputted into the system,
 * it stores its bottomlevel and weight as well as the list of child edges and map of parent tasks
 */
public class Task {
    private HashMap<Task,Integer> parentMap;
    private ArrayList<Edge> children;
    private int bottomLevel;
    private int weight;
    private String id;

    /**
     * Constructor for a Task, defining the weight and id
     * 
     * @param weight
     * @param id
     */
    Task(int weight, String id){
        this.weight=weight;
        this.id=id;
        this.children = new ArrayList<Edge>();
        this.parentMap = new HashMap<Task,Integer>();
    }

    /**
     * Sets the bottom level for the task
     * 
     * @param bottomLevel the value to set the bottom level as
     */
    void setBottomLevel(int bottomLevel){
        this.bottomLevel=bottomLevel;
    }

    /**
     * Adds a parent to the map of parents
     * 
     * @param parentEdge the edge leading to the parent to be added
     */
    void addParent(Edge parentEdge){
        parentMap.put(parentEdge.getParent(),parentEdge.getCommunicationTime());
    }

    /**
     * Adds a child edge to the tasks list of children
     * 
     * @param childEdge the edge to be added
     */
    void addChild(Edge childEdge){
        children.add(childEdge);
    }

    /**
     * Gets the Id of the current task
     * 
     * @return The Id of this task
     */
    String getId(){return id;}

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
     * @param parent a candidate parent task
     * @return True if the input task is a parent of this task,
     *  false otherwise
     */
    public boolean hasParent(Task parent) {
        return parentMap.containsKey(parent);
    }

    /**
     * @param parent a candidate parent task
     * @return communication time of parent task to this task
     */
    public int getParentCommunicationTime(Task parent) {
        return parentMap.get(parent);
    }
}
