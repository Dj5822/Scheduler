/**
 * The visualiser class manages the visualisation
 * of the search being done by TreeSearch, giving live
 * visual feedback to the user about the current state
 * of the search.
 */

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Visualiser extends Application{
    /**
     * Things to consider: 
     * 
     * What information do we want the visualiser to have?
     * -Current best solution
     * -Search tree coverage
     * ??
     * 
     * How do we get the information to visualise?
     * -The open list is currently located within a method in 
     * the TreeSearch class, meaning that it cannot be easily fetched
     * -the treesearch object could have its own visualiser that it updates
     * each time the open list is added to, allowing visualiser to get
     * up to date information
     */

    private Stage stage;
    private Scene scene;
    private VisualiserController controller;
    private static Visualiser visualiser;

    public static Visualiser getVisualiser(){
        return visualiser;
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            visualiser=this;
            stage = new Stage();
            String location = "views/test_page.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(location));
            AnchorPane pane = loader.load();
            controller = loader.getController();
            scene = new Scene(pane);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setExpandedNodesCount(int count) {
        controller.setExpandedNodeCountLabel(count);
    }
}
