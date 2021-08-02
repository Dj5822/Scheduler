/**
 * Edge defines an edge in the graph between two tasks.
 * This stores the child and parent tasks as well as the communication
 * time between them if they are on different processors
 */
public class Edge {
    private Task child;
    private Task parent;
    private int communicationTime;

    public Task getChild() {return child;}

    public Task getParent() {return parent;}

    public int getCommunicationTime() {return communicationTime;}

}
