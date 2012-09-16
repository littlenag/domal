package com.irksomeideas.domal;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.NumberAxis;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main extends Application {
  
  public static void main(String[] args) {
    ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring/**/*.xml");
    launch(args);
  }
  
  @Override
  public void stop() {
    // TODO Cleanly exit when we close the window. Will want to turn into a background
    // daemon later.
    System.exit(0);
  }  
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    Group root = new Group();
    primaryStage.setScene(new Scene(root));
    NumberAxis timeAxis = new NumberAxis();
    timeAxis.setLabel("Time");
    NumberAxis dataAxis = new NumberAxis();
    // Would be the reported units
    dataAxis.setLabel("Load");
    
    ObservableList<XYChart.Series<Double,Double>> chartData = FXCollections.observableArrayList(
        // Name would be the metric name, or maybe sensor name / metric name
        new LineChart.Series<Double,Double>("15 minute load", FXCollections.observableArrayList(
            new XYChart.Data<Double,Double>(0.0, 1.0),
            new XYChart.Data<Double,Double>(1.2, 1.4),
            new XYChart.Data<Double,Double>(2.2, 1.9),
            new XYChart.Data<Double,Double>(2.7, 2.3),
            new XYChart.Data<Double,Double>(2.9, 0.5)
        ))
    );
    
    LineChart chart = new LineChart(timeAxis, dataAxis, chartData);
    root.getChildren().add(chart);
    primaryStage.show();
  }

}
