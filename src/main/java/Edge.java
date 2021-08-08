/**
 * Edge defines an edge in the graph between two tasks.
 * This stores the child and parent tasks as well as the communication
 * time between them if they are on different processors
 */
public class Edge {
    private final Task child;
    private final Task parent;
    private final int communicationTime;

    /**
     * Constructor for Edge, defining the child task, parent task and communication time.
     * 
     * @param child
     * The task that the edge is pointing to.
     * @param parent
     * The task that the edge is coming out of.
     * @param communicationTime
     * Extra time taken when a task is not scheduled on the same processor.
     */
    Edge(Task child, Task parent, int communicationTime){
        this.child=child;
        this.parent=parent;
        this.communicationTime=communicationTime;
    }

    /**
     * @return This edges child task
     */
    public Task getChild() {
        return child;
    }

    /**
     * @return This edges parent task
     */
    public Task getParent() {
        return parent;
    }

    /**
     * @return This edges communication time
     */
    public int getCommunicationTime() {
        return communicationTime;
    }

}
