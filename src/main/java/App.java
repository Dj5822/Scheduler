import java.io.FileNotFoundException;

public class App {
    public static void main(String[] args) {
        try {
            Graph graph = new Graph("example.dot", "output.dot");
        } catch(FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }
}
