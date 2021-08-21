import java.util.Arrays;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javafx.beans.NamedArg;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/*
Source: https://stackoverflow.com/questions/27975898/gantt-chart-from-scratch
 */

public class GanttChart<X,Y> extends XYChart<X,Y> {

    public static class ExtraData {

        public long length;
        public String color;
        public Task task;
        public short startTime;
        public byte processor;


        public ExtraData(long lengthMs, String color, Task task, short startTime, byte processor) {
            super();
            this.length = lengthMs;
            this.color = color;
            this.task = task;
            this.startTime = startTime;
            this.processor = processor;
        }
        public byte getProcessor(){
            return processor;
        }
        public long getLength() {
            return length;
        }
        public void setLength(long length) {
            this.length = length;
        }
        public String getColor() {
            return color;
        }
        public void setColor(String color) {
            this.color = color;
        }
        public Task getTask() {
            return task;
        }
        public short getStartTime() {
            return startTime;
        }


    }

    private double blockHeight = 10;

    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
    }

    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
        super(xAxis, yAxis);
        if (!(xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis)) {
            throw new IllegalArgumentException("Axis type incorrect, X and Y should both be NumberAxis");
        }
        setData(data);
    }

    private static String getColor( Object obj) {
        return ((ExtraData) obj).getColor();
    }

    private static double getLength( Object obj) {
        return ((ExtraData) obj).getLength();
    }

    private static Task getTask( Object obj) {
        return ((ExtraData) obj).getTask();
    }
    
    private static short getStartTime( Object obj){
        return ((ExtraData) obj).getStartTime();
    }

    private static byte getProcessor( Object obj){
        return ((ExtraData) obj).getProcessor();
    }

    @Override protected void layoutPlotChildren() {

        for (int seriesIndex=0; seriesIndex < getData().size(); seriesIndex++) {

            Series<X,Y> series = getData().get(seriesIndex);

            Iterator<Data<X,Y>> iter = getDisplayedDataIterator(series);
            while(iter.hasNext()) {
                Data<X,Y> item = iter.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }
                Node block = item.getNode();
                Rectangle ellipse;
                if (block != null) {
                    if (block instanceof StackPane) {
                        StackPane region = (StackPane)item.getNode();
                        if (region.getShape() == null) {
                            ellipse = new Rectangle( getLength( item.getExtraValue()), getBlockHeight());
                        } else if (region.getShape() instanceof Rectangle) {
                            ellipse = (Rectangle)region.getShape();
                        } else {
                            return;
                        }
                        ellipse.setWidth( getLength( item.getExtraValue()) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getXAxis()).getScale()) : 1));
                        ellipse.setHeight(getBlockHeight() * ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getYAxis()).getScale()) : 1));
                        y -= getBlockHeight() / 2.0;
                        

                        // Note: workaround for RT-7689 - saw this in ProgressControlSkin
                        // The region doesn't update itself when the shape is mutated in place, so we
                        // null out and then restore the shape in order to force invalidation.
                        region.setShape(null);
                        region.setShape(ellipse);
                        region.setScaleShape(false);
                        region.setCenterShape(false);
                        region.setCacheShape(false);

                        block.setLayoutX(x);
                        block.setLayoutY(y);
                    }
                }
            }
        }
    }

    public double getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight( double blockHeight) {
        this.blockHeight = blockHeight;
    }

    @Override protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
        Node block = createContainer(series, getData().indexOf(series), item, itemIndex);
        getPlotChildren().add(block);
    }

    @Override protected  void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
        final Node block = item.getNode();
        getPlotChildren().remove(block);
        removeDataItemFromDisplay(series, item);
    }

    @Override protected void dataItemChanged(Data<X, Y> item) {
    }

    @Override protected  void seriesAdded(Series<X,Y> series, int seriesIndex) {
        for (int j=0; j<series.getData().size(); j++) {
            Data<X,Y> item = series.getData().get(j);
            Node container = createContainer(series, seriesIndex, item, j);
            getPlotChildren().add(container);
        }
    }

    @Override protected  void seriesRemoved(final Series<X,Y> series) {
        for (XYChart.Data<X,Y> d : series.getData()) {
            final Node container = d.getNode();
            getPlotChildren().remove(container);
        }
        removeSeriesFromDisplay(series);

    }


    private Node createContainer(Series<X, Y> series, int seriesIndex, final Data<X,Y> item, int itemIndex) {

        Node container = item.getNode();

        if (container == null) {
            container = new StackPane();
            item.setNode(container);
        }
        //System.out.println("made container");
        container.setStyle("-fx-background-color:" + getColor( item.getExtraValue()));

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        container.setEffect(colorAdjust);

        container.setOnMouseEntered(e -> {   

            /** 
            //need to determine the location of center top side of the gantt chart element
            Visualiser visualiser = Visualiser.getVisualiser();

            int startTime = getStartTime( item.getExtraValue());
            int processor = getProcessor( item.getExtraValue());
            double length = getLength( item.getExtraValue());
            int processorCount = visualiser.getProcessorCount();
            double totalHeight = getHeight();

            double yvalue = (getYAxis().getHeight())*(processorCount - processor)/(visualiser.getProcessorCount());
            //System.out.println(visualiser.getheight());
            System.out.println(processor+1);
            System.out.println(visualiser.getProcessorCount());
            System.out.println(getYAxis().getHeight());
            //System.out.println(yvalue);
            visualiser.moveTaskInfo(200, yvalue);
            */

            /**Visualiser visualiser = Visualiser.getVisualiser();
            double mouseX = e.getSceneX();
            double mouseY = e.getSceneY();
            System.out.println(mouseY);
            if (mouseY < visualiser.getheight()/2){
                visualiser.moveTaskInfo(mouseX, mouseY+50);
            }
            else {
                visualiser.moveTaskInfo(mouseX, mouseY-260);
            }**/


            //https://stackoverflow.com/questions/29879023/javafx-transition-darken-button-on-hover

            Timeline fadeInTimeline = new Timeline(

                        new KeyFrame(Duration.seconds(0), 
                                new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.LINEAR)), 
                                new KeyFrame(Duration.seconds(0.3), new KeyValue(colorAdjust.brightnessProperty(), -0.4, Interpolator.LINEAR)
                                ));
                fadeInTimeline.setCycleCount(1);
                fadeInTimeline.setAutoReverse(false);
                fadeInTimeline.play();

            Visualiser.getVisualiser().setTaskLabelInfo(getTask( item.getExtraValue()), getStartTime( item.getExtraValue()));
            Visualiser.getVisualiser().showTaskInfo();
        });
        container.setOnMouseExited(e -> {
            Visualiser.getVisualiser().hideTaskInfo();

            Timeline fadeOutTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(0), 
                                new KeyValue(colorAdjust.brightnessProperty(), colorAdjust.brightnessProperty().getValue(), Interpolator.LINEAR)), 
                                new KeyFrame(Duration.seconds(0.3), new KeyValue(colorAdjust.brightnessProperty(), 0, Interpolator.LINEAR)
                                ));
                fadeOutTimeline.setCycleCount(1);
                fadeOutTimeline.setAutoReverse(false);
                fadeOutTimeline.play();
        });

        return container;
    }

    @Override protected void updateAxisRange() {
        final Axis<X> xa = getXAxis();
        final Axis<Y> ya = getYAxis();
        List<X> xData = null;
        List<Y> yData = null;
        if(xa.isAutoRanging()) xData = new ArrayList<X>();
        if(ya.isAutoRanging()) yData = new ArrayList<Y>();
        if(xData != null || yData != null) {
            for(Series<X,Y> series : getData()) {
                for(Data<X,Y> data: series.getData()) {
                    if(xData != null) {
                        xData.add(data.getXValue());
                        xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) + getLength(data.getExtraValue())));
                    }
                    if(yData != null){
                        yData.add(data.getYValue());
                    }
                }
            }
            if(xData != null) xa.invalidateRange(xData);
            if(yData != null) ya.invalidateRange(yData);
        }
    }

}