/**
 * Edge defines an edge in the graph between two tasks.
 * This stores the child and parent tasks as well as the communication
 * time between them if they are on different processors
 */
public class Edge {
    private Task child;
    private Task parent;
    private int communicationTime;

    /**
     * Constructor for Edge, defining the child task, parent task and communication time
     * 
     * @param child
     * @param parent
     * @param communicationTime
     */
    Edge(Task child, Task parent, int communicationTime){
        this.child=child;
        this.parent=parent;
        this.communicationTime=communicationTime;
    }

    /**
     * Gets the child of this edge, which is the task that the
     * edge points towards
     * 
     * @return the child task of this edge
     */
    public Task getChild() {return child;}

     /**
     * Gets the parent of this edge, which is the task that the
     * edge points from
     * 
     * @return the parent task of this edge
     */
    public Task getParent() {return parent;}

     /**
     * Gets the communication time of this edge, whcih is the
     * time that is spent if the tasks of the edge are scheduled
     * on different processors
     * 
     * @return the communication time of this edge
     */
    public int getCommunicationTime() {return communicationTime;}

}
