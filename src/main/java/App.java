import java.util.ArrayList;

public class App {
    public static void main(String[] args) {
        /*
        The name of the input graph should be passed in as an argument.
        Should be passed in as a commandline argument later.
         */
        Graph graph = new Graph("examples/" + args[0]);

        /*
         The number of processors.
         Should be passed in as a commandline argument later.
         */
        int processorCount = Integer.parseInt(args[1]);

        // Start searching the solutions tree.
        TreeSearch testSearch = new TreeSearch(graph, processorCount);

        Node node = testSearch.aStar();
        graph.generateOutputGraph(node);
        graph.printBottomLevels();
    }
}
