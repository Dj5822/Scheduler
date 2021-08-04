public class App {
    public static void main(String[] args) {
        /*
        The name of the input graph should be passed in as an argument.
        Should be passed in as a commandline argument later.
         */
        Graph graph = new Graph("example.dot");

        /*
         The number of processors.
         Should be passed in as a commandline argument later.
         */
        int processorCount = 2;

        // Start searching the solutions tree.
        TreeSearch testSearch = new TreeSearch(graph, processorCount);

        Node node = generateDebugSchedule(graph);
        graph.generateOutputGraph(node);
        graph.printBottomLevels();
    }

    // For testing
    private static Node generateDebugSchedule(Graph graph) {
        Node old_node = null;
        Node node = null;
        int time = 2;
        for (Task task : graph.getTasks()) {
            time += 2;
            old_node = node;
            node = new Node(old_node, 0, new State(task, time, time-1));
        }
        return node;
    }
}
