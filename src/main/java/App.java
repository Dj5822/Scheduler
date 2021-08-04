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

        graph.generateOutputGraph();
        graph.printBottomLevels();
    }
}
