import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The AppTest class contains unit test cases for the App class.
 * This class tests for the coverage and validates functionality of class methods
 */
public class AppTest {

    @Test
    public void testCheckArgsArgumentLengthLessThanTwoAndValidFile() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"input.dot"});
        });
    }
    @Test
    public void testCheckArgsInvalidArgumentLengthAndFile() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"input.png"});
        });
    }
    @Test
    public void testCheckArgsArgumentLengthLessThanTwoAndValidProcessor() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"1"});
            App.checkArgs(new String[] {"5"});
            App.checkArgs(new String[] {"10"});
        });
    }
    @Test
    public void testCheckArgsInvalidArgumentLengthAndProcessor() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"-50"});
            App.checkArgs(new String[] {"50"});
        });
    }
    @Test   //Valid argument length
    public void testCheckArgsInvalidFileAndValidProcessor() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"input.png", "5"});
            App.checkArgs(new String[] {"input.jpg", "5"});
            App.checkArgs(new String[] {"input.exe", "5"});
        });
    }
    @Test   //Valid argument length
    public void testCheckArgsMoreThanTenProcessorsAndValidFile() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"input.dot", "20"});
        });
    }
    @Test   //Valid argument length
    public void testCheckArgsLessZeroThanTenProcessorsAndValidFile() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"input.dot", "-20"});
        });
    }
    @Test   //Valid argument length
    public void testCheckArgsValidFileAndProcessor() throws IllegalArgumentException{
        App.checkArgs(new String[] {"input.dot", "1"});
        App.checkArgs(new String[] {"input.dot", "5"});
        App.checkArgs(new String[] {"input.dot", "10"});
    }
    @Test   //Valid argument length
    public void testCheckArgsInvalidFileAndProcessor() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"input.png", "-50"});
            App.checkArgs(new String[] {"input.png", "50"});
        });
    }
}
