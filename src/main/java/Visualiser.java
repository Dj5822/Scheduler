/**
 * The visualiser class manages the visualisation
 * of the search being done by TreeSearch, giving live
 * visual feedback to the user about the current state
 * of the search.
 */

import java.io.IOException;
import java.util.Arrays;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
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

    private int width;
    private int height;

    private GanttChart<Number,String> ganttChart;
    private XYChart.Series[] processorSchedule;
    private NumberAxis xAxis;
    private CategoryAxis yAxis;

    private Label expandedNodesLabel;
    private Label memoryUsageLabel;
    private Label searchTimeLabel;

    private Stage stage;
    private Scene scene;
    private VisualiserController controller;
    private static Visualiser visualiser;
    private GridPane mainPane;

    private int processorCount;

    public static Visualiser getVisualiser(){
        return visualiser;
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
            visualiser=this;

            Rectangle2D screenBounds = Screen.getPrimary().getBounds();

            height = (int) (screenBounds.getHeight()/1.1);
            width = (int) screenBounds.getWidth();

            GridPane mainPane = new GridPane();
            mainPane.setPrefWidth(width);
            mainPane.setPrefHeight(height);

            setupView(mainPane, width, height);

            scene = new Scene(mainPane);
            stage.setTitle("Scheduler Visualiser");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupView(GridPane mainPane, int width, int height) {
        this.width = width;
        this.height = height;

        this.mainPane = mainPane;

        // Create pane.
        GridPane statsPane = new GridPane();

        // Create components.
        expandedNodesLabel = new Label("Expanded Nodes: 0");
        memoryUsageLabel = new Label("Memory usage: 0/4 GB");
        searchTimeLabel = new Label("Search time: 0 min 0 sec");

        // Button sizes.
        expandedNodesLabel.setPrefHeight(height/5);
        expandedNodesLabel.setPrefWidth(width/6);
        memoryUsageLabel.setPrefHeight(height/5);
        memoryUsageLabel.setPrefWidth(width/6);
        searchTimeLabel.setPrefHeight(height/5);
        searchTimeLabel.setPrefWidth(width/6);

        statsPane.add(expandedNodesLabel, 0, 0);
        statsPane.add(memoryUsageLabel, 0, 1);
        statsPane.add(searchTimeLabel, 0, 2);

        mainPane.add(statsPane,5,0, 1, 1);
    }

    public void setupGanttChart(int processorCount) {
        this.processorCount = processorCount;
        String[] processors = new String[processorCount];

        xAxis = new NumberAxis();
        yAxis = new CategoryAxis();
        ganttChart = new GanttChart<Number,String>(xAxis,yAxis);

        for (int i=0; i<processors.length; i ++) {
            processors[i] = "processor " + (i + 1);
        }

        xAxis.setLabel("Allocated Time");
        xAxis.setTickLabelFill(Color.GRAY);
        xAxis.setMinorTickCount(4);

        yAxis.setLabel("Processor");
        yAxis.setTickLabelFill(Color.GRAY);
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(processors)));

        ganttChart.setTitle("Current Best Schedule");
        ganttChart.setLegendVisible(false);
        ganttChart.setBlockHeight( 50);

        processorSchedule = new XYChart.Series[processorCount];
        for (int i=0; i<processors.length; i ++) {
            processorSchedule[i] = new XYChart.Series();
        }

        ganttChart.getStylesheets().add(getClass().getResource("views/ganttchart.css").toExternalForm());

        ganttChart.getData().addAll(processorSchedule);

        ganttChart.setPrefWidth(width*5/6);
        ganttChart.setPrefHeight(height);

        mainPane.add(ganttChart, 0, 0, 1, 4);
    }


}
