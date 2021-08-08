public class App {
    public static void main(String[] args) {

        try {
            checkArgs(args);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }

        /*
        The name of the input graph should be passed in as an argument.
        Should be passed in as a commandline argument later.
         */
        Graph graph = new Graph(args[0]);

        /*
         The number of processors.
         Should be passed in as a commandline argument later.
         */
        int processorCount = Integer.parseInt(args[1]);

        // Start searching the solutions tree.
        TreeSearch testSearch = new TreeSearch(graph, processorCount);

        Node node = testSearch.aStar();
        graph.generateOutputGraph(node);
        System.out.println("output.dot generated.");
        //graph.printBottomLevels();
    }

    public static void checkArgs(String[] args) throws IllegalArgumentException {

        if (args.length != 2) { // exactly two args for Milestone 1
            throw new IllegalArgumentException("Please enter two arguments: input dot file and processor count.");
        }
        else if (!args[0].matches("[^.]+\\.dot$")) {
            throw new IllegalArgumentException("Please enter a valid dot file.");
        }
        else {
            int p = Integer.parseInt(args[1]);
            if (p < 0 || p > 10) {
                throw new IllegalArgumentException("Please enter a valid number of processors.");
            }
        }

    }
}