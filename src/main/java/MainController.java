

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    private TreeSearch treeSearch;

    @FXML
    protected Label TestLabel;

    public void changeLabel(String newText) {
        TestLabel.setText(newText);
    }

    public void initialize(){
        //treeSearch=TreeSearch.GetTreeSearch();
        changeLabel("random words");
    }

    
}
