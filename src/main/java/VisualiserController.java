

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class VisualiserController {

    private TreeSearch treeSearch;

    @FXML
    protected Label expandedNodeCountLabel;

    public void setExpandedNodeCountLabel(int count) {
        expandedNodeCountLabel.setText("Explored Nodes Count: " + count);
    }
}
