public class App {
    public static void main(String[] args) {
        Graph graph = new Graph("example.dot");
        graph.generateOutputGraph();
        graph.printBottomLevels();
    }
}
