
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
     * @param task
     * @param start_time
     * @param processor
     */
    public Node(Node parent, int cost, State state) {
        this.parent = parent;
        this.state = state
        this.cost = cost;
    }

    /**
     * Constructor for the root node, sets time and processor to zero.
     * @param task
     */
    public Node(Task task) {
        this.parent = null;
        this.state = new State(task, 0, 0);
        this.cost = task.get_compute_time();
    }

    public State get_state() {
        return this.state;
    }

    public int get_cost() {
        return this.cost;
    }

    public void set_cost(int cost) {
        this.cost = cost;
    }

    public Node get_parent() {
        return this.parent;
    }

    public Node[] 
}