import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Task defines a task from the graph that is inputted into the system,
 * it stores its bottomlevel and weight as well as the list of child edges and map of parent tasks
 */
public class Task {
    private final short weight;
    private final String id;

    /*
    The tasks that point towards this task
    and the communication time.
     */
    private final HashMap<Task,Integer> parentMap;
    // The tasks that this task points towards.
    private final ArrayList<Edge> children;

    private Short bottomLevel;


    /**
     * Constructor for a Task, defining the weight and id
     * @param weight the amount of time needed for the task to finish
     * @param id the character used to identify the task.
     */
    Task(short weight, String id) {
        this.weight = weight;
        this.id = id;

        this.children = new ArrayList<>();
        this.parentMap = new HashMap<>();

        this.bottomLevel = null;
    }

    /**
     * Recursively set the bottom level of this task and all children
     * Assumes unset bottom levels are null
     */
    public Short findBottomLevel() {
        
        // bottom level may have already been set by a parent task
        // do not recalculate, as bottom level never changes
        if (bottomLevel != null) {
            return bottomLevel;
        }
        
        // find longest path length
        short criticalPathTime = 0;
        for (Edge childEdge : getChildren()) {
            Task child = childEdge.getChild();
            // recursively call this function on all children
            short pathTime = child.findBottomLevel();
            // update longest path
            if (pathTime > criticalPathTime) {
                criticalPathTime = pathTime;
            }
        }

        bottomLevel = (short) (criticalPathTime + weight);
        return getBottomLevel();
    }

    /**
     * Adds a parent to the map of parents
     * 
     * @param parentEdge the edge leading to the parent to be added
     */
    void addParent(Edge parentEdge) {
        parentMap.put(parentEdge.getParent(),parentEdge.getCommunicationTime());
    }

    /**
     * Adds a child edge to the tasks list of children
     * 
     * @param childEdge the edge to be added
     */
    void addChild(Edge childEdge) {
        children.add(childEdge);
    }

    /**
     * Gets the Id of the current task
     * 
     * @return The Id of this task
     */
    String getId(){ return id; }

    /**
     * Gets the BottomLevel of the task, which is the 
     * maximum cost to travel to the end point from the current task
     * 
     * @return The bottom level of this task
     */
    Short getBottomLevel() {return bottomLevel;}

    /**
     * Gets the weight of the task, which is the
     * amount of time that the task takes to execute
     * 
     * @return the weight of this task
     */
    short getWeight() {return weight;}

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

    /**
     * @return this task's number of parent tasks
     */
    public int parentCount() {
        return parentMap.size();
    }

    /**
     * @return True if this task is a root task, false otherwise
     */
    public boolean isRootTask() {
        return parentCount() == 0;
    }

    /**
     * @return set of parent tasks
     */
    public Set<Task> getParents() {
        return parentMap.keySet();
    }
    
}
