import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class App {
    public static void main(String[] args) {
        try {
            DotParser parser = new DotParser(new FileInputStream("example.dot"), "output.dot");
            Graph graph = parser.getConvertedGraph();
            graph.printBottomLevels();

            Node debugSchedule = parser.generateDebugSchedule();
            parser.writeScheduleToDot(debugSchedule);

        } catch(FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }
}
