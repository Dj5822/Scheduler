//import javax.swing.ViewportLayout;

import java.util.HashMap;

/**
 * The main class for the program, interacting with the other major classes
 * in order to create a final solution.
 */
public class App {
    public static void main(String[] args) {
        int threadCount = 1;
        boolean doVisualise = false;
        String outputFileName = "";
        try {
            HashMap<Integer, String> options = checkArgs(args);
            for (Integer key : options.keySet()) {
                String value = options.get(key);
                switch (key.toString()) {
                    case ("1"):
                        threadCount = Integer.parseInt(value);
                        break;
                    case ("2"):
                        doVisualise = true;

                    case ("3"):
                        outputFileName = value;
                }
            }
            /*
                The number of processors.
                */
            int processorCount = Integer.parseInt(args[1]);

                /*
                The name of the input graph should be passed in as an argument.
                Should be passed in as a commandline argument later.
                */
            if (outputFileName.isEmpty()) {
                outputFileName = args[0].replaceAll("(.dot)$", "-output.dot");
            }
            Graph graph = new Graph("examples/" + args[0], outputFileName);

            // Start searching the solutions tree.
            TreeSearch testSearch = new TreeSearch(graph, processorCount, doVisualise);

            Node node = testSearch.aStarCentralized(threadCount);
            graph.generateOutputGraph(node);
            System.out.println("\nFinish Time: \n" + node.getSchedule().getFinishTime() + "\n");

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Checks the args that are provided in the input to ensure that they are valid,
     * throwing an exception if they are not
     *
     * @param args The args to be checked
     * @throws IllegalArgumentException an exception indicating that the args are not valid
     */
    public static HashMap<Integer, String> checkArgs(String[] args) throws IllegalArgumentException {

        if (args.length < 2) { // exactly two args for Milestone 1
            throw new IllegalArgumentException("Please enter two arguments: input dot file and processor count.");
        }
        else if (!args[0].matches("[^.]+\\.dot$")) {
            throw new IllegalArgumentException("Please enter a valid dot file.");
        }
        else {
            int p = Integer.parseInt(args[1]);
            if (p < 1 || p > 10) {
                throw new IllegalArgumentException("Please enter a valid number of processors.");
            }
        }
        HashMap<Integer, String> options = new HashMap<Integer, String>();
        if (args.length > 2) {
            int i = 2;
            while (i < args.length) {
                String option = args[i];
                switch (option) {
                    case ("-p"):
                        int threadCount = 1;
                        if ((i == (args.length - 1)) || args[i+1].equals("-v") || args[i+1].equals("-o")) {
                            throw new IllegalArgumentException("Please enter desired number of cores with option -p");
                        } else {
                            try {
                                threadCount = Integer.parseInt(args[i+1]);
                                options.put(1, args[i+1]);
                                i = i + 2;
                            } catch (NumberFormatException e) {
                                throw new NumberFormatException("Please enter an integer for number of cores");
                            }
                        }
                        break;
                    case ("-v"):
                        options.put(2, "true");
                        i++;
                        break;
                    case ("-o"):
                        String outputFileName = "";
                        if ((i == (args.length - 1)) || args[i+1].equals("-p") || args[i+1].equals("-v")) {
                            throw new IllegalArgumentException("Please enter desired OUTPUT filename with option -o");
                        } else {
                            if (!args[i+1].endsWith(".dot")) {
                                outputFileName = args[i+1] + ".dot";
                            } else {
                                outputFileName = args[i+1];
                            }
                            i = i + 2;
                        }
                        options.put(3, outputFileName);
                        break;
                    default:
                        i++;
                        break;

                }
            }
        }
        return options;

    }
}