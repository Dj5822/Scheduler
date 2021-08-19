/**
 * The visualiser class manages the visualisation
 * of the search being done by TreeSearch, giving live
 * visual feedback to the user about the current state
 * of the search.
 */

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ContentDisplay;
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
    private Label expandedNodesValueLabel;
    private Label memoryUsageValueLabel;
    private Label searchTimeValueLabel;

    private Stage stage;
    private Scene scene;
    private VisualiserController controller;
    private static Visualiser visualiser;
    private GridPane mainPane;

    private int processorCount;

    private DecimalFormat df;

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

            df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);

            scene = new Scene(mainPane);
            File styleFile = new File("src/main/resources/views/ganttchart.css");
            scene.getStylesheets().clear();
            scene.getStylesheets().add("file:///" + styleFile.getAbsolutePath().replace("\\", "/"));
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
        expandedNodesLabel = new Label("Expanded Nodes");
        expandedNodesValueLabel = new Label("0");
        memoryUsageLabel = new Label("Memory Usage");
        memoryUsageValueLabel = new Label("0/4 GB");
        searchTimeLabel = new Label("Search Time");
        searchTimeValueLabel = new Label("0 min 0 sec");

        // Label sizes.
        expandedNodesLabel.setPrefHeight(height/6);
        expandedNodesLabel.setPrefWidth(width/7);
        expandedNodesValueLabel.setPrefHeight(height/6);
        expandedNodesValueLabel.setPrefWidth(width/7);
        memoryUsageLabel.setPrefHeight(height/6);
        memoryUsageLabel.setPrefWidth(width/7);
        memoryUsageValueLabel.setPrefHeight(height/6);
        memoryUsageValueLabel.setPrefWidth(width/7);
        searchTimeLabel.setPrefHeight(height/6);
        searchTimeLabel.setPrefWidth(width/7);
        searchTimeValueLabel.setPrefHeight(height/6);
        searchTimeValueLabel.setPrefWidth(width/7);

        // Label alignment.
        expandedNodesLabel.setAlignment(Pos.BOTTOM_CENTER);
        expandedNodesValueLabel.setAlignment(Pos.TOP_CENTER);
        memoryUsageLabel.setAlignment(Pos.BOTTOM_CENTER);
        memoryUsageValueLabel.setAlignment(Pos.TOP_CENTER);
        searchTimeLabel.setAlignment(Pos.BOTTOM_CENTER);
        searchTimeValueLabel.setAlignment(Pos.TOP_CENTER);

        // Apply styling.
        expandedNodesLabel.getStyleClass().add("boldLabel");
        memoryUsageLabel.getStyleClass().add("boldLabel");
        searchTimeLabel.getStyleClass().add("boldLabel");

        // Add to the pane.
        statsPane.add(expandedNodesLabel, 0, 0);
        statsPane.add(expandedNodesValueLabel, 0, 1);
        statsPane.add(memoryUsageLabel, 0, 2);
        statsPane.add(memoryUsageValueLabel, 0, 3);
        statsPane.add(searchTimeLabel, 0, 4);
        statsPane.add(searchTimeValueLabel, 0, 5);
        statsPane.setPadding(new Insets(10, 0, 0, 0));
        mainPane.add(statsPane,5,0, 1, 1);
    }

    public void setupGanttChart(int processorCount) {
        this.processorCount = processorCount;
        String[] processors = new String[processorCount];

        xAxis = new NumberAxis();
        yAxis = new CategoryAxis();
        ganttChart = new GanttChart<Number,String>(xAxis,yAxis);

        for (int i=0; i<processors.length; i ++) {
            processors[i] = "processor " + i;
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

    /**
     * The control layer method that updates the view components.
     * @param expandedNodesCount the number of expanded nodes.
     * @param timePassed the amount of time passed.
     */
    public void updateVisualiser(Schedule schedule, int expandedNodesCount, long totalMemory, long freeMemory, long timePassed) {
        expandedNodesValueLabel.setText(Integer.toString(expandedNodesCount));
        memoryUsageValueLabel.setText(df.format((double)freeMemory/1000000000) + "/" + df.format((double)totalMemory/1000000000) + " GB");
        searchTimeValueLabel.setText(String.valueOf(timePassed/1000) + " sec");

        updateGanttChart(schedule);
    }

    private void updateGanttChart(Schedule schedule) {
        // clear the existing data.
        ganttChart.getData().clear();

        HashMap<Task,TaskVariant> scheduledTasks = schedule.getScheduledTasks();

        // Clear every row.
        for (XYChart.Series processor : processorSchedule) {
            processor.getData().clear();
        }

        /*
        We need the following information for each scheduled task:
        - Processor
        - Start time
        - Weight
         */
        for (Task scheduledTask : scheduledTasks.keySet()) {
            byte processor = scheduledTasks.get(scheduledTask).getProcessor();
            short startTime = scheduledTasks.get(scheduledTask).getStartTime();
            short weight = scheduledTask.getWeight();

            processorSchedule[processor].getData().add(new XYChart.Data(startTime, "processor " + processor,
                    new GanttChart.ExtraData( weight, "blue")));
        }


        // add the new data.
        ganttChart.getData().addAll(processorSchedule);
    }
}
