

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {

    private TreeSearch treeSearch;

    @FXML
    protected Label ExpandedNodeCountLabel;

    public void setExpandedNodeCountLabel(int count) {
        ExpandedNodeCountLabel.setText("Explored Nodes Count: " + count);
    }
}
