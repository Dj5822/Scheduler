import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class App {
    public static void main(String[] args) {
        try {
            DotParser parser = new DotParser(new FileInputStream("example.dot"));
            Graph graph = parser.getConvertedGraph();
            graph.printBottomLevels();
        } catch(FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }
}
