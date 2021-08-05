import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        /*
        The name of the input graph should be passed in as an argument.
        Should be passed in as a commandline argument later.
         */
        Graph graph = new Graph("examples/Nodes_7_OutTree.dot");

        /*
         The number of processors.
         Should be passed in as a commandline argument later.
         */
        int processorCount = 2;

        // Start searching the solutions tree.
        TreeSearch testSearch = new TreeSearch(graph, processorCount);
        Node node = testSearch.bruteForceTest();
        graph.generateOutputGraph(node);

        //graph.printBottomLevels();
    }
}
