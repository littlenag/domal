package ht.core;

import javax.annotation.PostConstruct;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;

import org.apache.log4j.Logger;

public class GraphMetrics extends Application {
  private Logger logger = Logger.getLogger(GraphMetrics.class);
  
  private Persist persist;
  
  public void setDb(Persist p) {
    persist = p;
  }
  
  @Override 
  public void start(Stage primaryStage) throws Exception {
    Group root = new Group();
    primaryStage.setScene(new Scene(root));
    NumberAxis xAxis = new NumberAxis("Values for X-Axis", 0, 3, 1);
    NumberAxis yAxis = new NumberAxis("Values for Y-Axis", 0, 3, 1);
    ObservableList<XYChart.Series<Double,Double>> lineChartData = FXCollections.observableArrayList(
        new LineChart.Series<Double,Double>("Series 1", FXCollections.observableArrayList(
            new XYChart.Data<Double,Double>(0.0, 1.0),
            new XYChart.Data<Double,Double>(1.2, 1.4),
            new XYChart.Data<Double,Double>(2.2, 1.9),
            new XYChart.Data<Double,Double>(2.7, 2.3),
            new XYChart.Data<Double,Double>(2.9, 0.5)
        )),
        new LineChart.Series<Double,Double>("Series 2", FXCollections.observableArrayList(
            new XYChart.Data<Double,Double>(0.0, 1.6),
            new XYChart.Data<Double,Double>(0.8, 0.4),
            new XYChart.Data<Double,Double>(1.4, 2.9),
            new XYChart.Data<Double,Double>(2.1, 1.3),
            new XYChart.Data<Double,Double>(2.6, 0.9)
        ))
    );
    LineChart chart = new LineChart(xAxis, yAxis, lineChartData);
    root.getChildren().add(chart);

    primaryStage.show();
  }
  
  @PostConstruct
  public void init() {    
    persist.addMetricHandler(new MetricHandler() {
      @Override
      public void handleMetric(Metric m) {
        System.out.println("Got metric: " + m);
        logger.info("Got metric: " + m);

      }
    });
    
    launch();    
  }
}
