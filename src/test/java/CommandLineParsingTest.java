import com.paypal.digraph.parser.GraphNode;
import com.paypal.digraph.parser.GraphEdge;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The CommandLineParsingTest class contains tests to check the command line
 * argument parsing functionality for App.checkArgs(String []) method
 */
public class CommandLineParsingTest {
    @BeforeAll
    public void setUp() {

    }


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
    @Test   //With valid argument length
    public void testCheckArgsInvalidFileAndValidProcessor() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"input.png", "5"});
            App.checkArgs(new String[] {"input.jpg", "5"});
            App.checkArgs(new String[] {"input.exe", "5"});
        });
    }
    @Test   //With valid argument length
    public void testCheckArgsMoreThanTenProcessorsAndValidFile() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"input.dot", "20"});
        });
    }
    @Test   //With valid argument length
    public void testCheckArgsLessZeroThanTenProcessorsAndValidFile() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"input.dot", "-20"});
        });
    }
    @Test   //With valid argument length
    public void testCheckArgsValidFileAndProcessor() throws IllegalArgumentException{
        App.checkArgs(new String[] {"input.dot", "1"});
        App.checkArgs(new String[] {"input.dot", "5"});
        App.checkArgs(new String[] {"input.dot", "10"});
    }
    @Test   //With valid argument length
    public void testCheckArgsInvalidFileAndProcessor() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"input.png", "-50"});
            App.checkArgs(new String[] {"input.png", "50"});
        });
    }
    @Test
    public void testCheckArgsInvalidCoreNumberOption() {
        assertThrows(IllegalArgumentException.class, () -> {
            App.checkArgs(new String[] {"input.dot", "1", "-p"});
            App.checkArgs(new String[] {"input.dot", "1", "-p", "-v"});
            App.checkArgs(new String[] {"input.dot", "1", "-p", "-o", "output.dot"});
        });
        assertThrows(NumberFormatException.class, () -> {
            App.checkArgs(new String[] {"input.dot", "1", "-p", "error"});
            App.checkArgs(new String[] {"input.dot", "1", "-p", ".,~!?"});
        });
    }
    @Test
    public void testCheckArgsValidCoreNumberOptions() throws IllegalArgumentException, NumberFormatException{
        App.checkArgs(new String[] {"input.dot", "1", "-p", "8"});
        App.checkArgs(new String[] {"input.dot", "1", "-v", "-p", "8"});
        App.checkArgs(new String[] {"input.dot", "1", "-v", "-p", "8", "-o", "output.dot"});
    }
    @Test
    public void testCheckArgsVisualiserOption() {
        App.checkArgs(new String[] {"input.dot", "1", "-v"});
    }


}
