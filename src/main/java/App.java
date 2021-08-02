import java.io.FileNotFoundException;

public class App {
    public static void main(String[] args) {
        try {
            DotParser dotParser = new DotParser("example.dot");
        } catch(FileNotFoundException e) {
            System.out.println("File not found");
        }
    }
}
