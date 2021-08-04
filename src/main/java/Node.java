
/**
 * A Node in the search tree. Represents a possible scheduling State for a single Task.
 */
class Node {
    private Node parent;
    private int cost;
    private State state;

    /**
     * Constructor for a search tree node
     * @param parent
     * @param cost
     * @param state
     */
    public Node(Node parent, int cost, State state) {
        this.parent = parent;
        this.state = state;
        this.cost = cost;
    }

    /**
     * Constructor for the root node, sets time and processor to zero.
     * @param task
     */
    public Node(Task task) {
        this.parent = null;
        this.state = new State(task, 0, 0);
        this.cost = task.getWeight();
    }

    public State getState() {
        return this.state;
    }

    public int getCost() {
        return this.cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public Node getParent() {
        return this.parent;
    }

}