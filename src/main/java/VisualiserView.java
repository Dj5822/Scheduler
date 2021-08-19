import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class VisualiserView {

    private Label expandedNodesLabel;
    private Label memoryUsageLabel;
    private Label searchTimeLabel;

    public VisualiserView(GridPane mainPane, int width, int height) {
        final int LABEL_WIDTH = width/10;
        final int LABEL_HEIGHT = height/10;

        // Create pane.
        GridPane statsPane = new GridPane();

        // Create components.
        expandedNodesLabel = new Label("Expanded Nodes: 0");
        memoryUsageLabel = new Label("Memory usage: 0/4 GB");
        searchTimeLabel = new Label("Search time: 0 min 0 sec");

        // Button sizes.
        expandedNodesLabel.setPrefHeight(LABEL_HEIGHT);
        expandedNodesLabel.setPrefWidth(LABEL_WIDTH);
        memoryUsageLabel.setPrefHeight(LABEL_HEIGHT);
        memoryUsageLabel.setPrefWidth(LABEL_WIDTH);
        searchTimeLabel.setPrefHeight(LABEL_HEIGHT);
        searchTimeLabel.setPrefWidth(LABEL_WIDTH);

        statsPane.add(expandedNodesLabel, 0, 0);
        statsPane.add(memoryUsageLabel, 0, 1);
        statsPane.add(searchTimeLabel, 0, 2);

        mainPane.add(statsPane,0,0);

    }
}
