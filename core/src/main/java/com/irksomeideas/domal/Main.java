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
  
  private static Persist persist;
  
  public void setDb(Persist p) {
    persist = p;
  }
  
  public static void main(String[] args) {
    ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring/**/*.xml");
    persist = (Persist)context.getBean("db");
    launch(args);
  }
  
  @Override
  public void stop() {
    // TODO Cleanly exit when we close the window. Will want to turn into a background
    // daemon later.
    System.exit(0);
  }
  
  private Stage primaryStage;
  
  public Stage getPrimaryStage() {
    return primaryStage;
  }
  
  private XYChart.Series<Number,Number> hourDataSeries;
  
  @Override
  public void start(Stage primaryStage) throws Exception {
    Group root = new Group();
    this.primaryStage = primaryStage;
    this.primaryStage.setScene(new Scene(root));
    NumberAxis timeAxis = new NumberAxis();
    timeAxis.setLabel("Time");
    NumberAxis dataAxis = new NumberAxis();
    // label would be the reported units
    dataAxis.setLabel("Load");
 
    LineChart chart = new LineChart(timeAxis, dataAxis);
    
    hourDataSeries = new XYChart.Series<Number,Number>();
    hourDataSeries.setName("15 minute load");
    
    chart.getData().add(hourDataSeries);
        
    persist.addMetricHandler(new MetricHandler() {      
      int ticks = 0;
      @Override
      public void handleMetric(Metric m) {
        System.out.println("Got metric.");
        hourDataSeries.getData().add(new XYChart.Data<Number, Number>(ticks++, m.value));
      }
    });
    
    root.getChildren().add(chart);
    this.primaryStage.show();
  }

}
