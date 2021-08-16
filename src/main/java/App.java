import javax.swing.ViewportLayout;

/**
 * The main class for the program, interacting with the other major classes 
 * in order to create a final solution.
 */
public class App {

    

    public static void main(String[] args) {

        
        
        try {
            checkArgs(args);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }

        String outputFileName = "";
        if (args.length == 4) {
            if (args[2].equals("-o")) {
                outputFileName = args[3] + ".dot";
            }
        }
        else {
            outputFileName = args[0].replaceAll("(.dot)$", "-output.dot");
        }

        /*
        The name of the input graph should be passed in as an argument.
        Should be passed in as a commandline argument later.
         */
        Graph graph = new Graph(args[0], outputFileName);

        /*
         The number of processors.
         Should be passed in as a commandline argument later.
         */
        int processorCount = 1;
        if (!args[1].matches("1")) {
            processorCount = 2;
        }

        Visualiser.launch(Visualiser.class);

        // Start searching the solutions tree.
        TreeSearch testSearch = new TreeSearch(graph, processorCount, true);

        Node node = testSearch.idaStar();
        graph.generateOutputGraph(node);
        System.out.println("output file generated.");
        //graph.printBottomLevels();
    }

    /**
     * Checks the args that are provided in the input to ensure that they are valid,
     * throwing an exception if they are not
     * 
     * @param args The args to be checked
     * @throws IllegalArgumentException an exception indicating that the args are not valid
     */
    public static void checkArgs(String[] args) throws IllegalArgumentException {

        if (args.length < 2) { // exactly two args for Milestone 1
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