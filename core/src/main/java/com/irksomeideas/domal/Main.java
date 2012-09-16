package com.irksomeideas.domal;

import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;

import org.joda.time.Instant;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main extends Application {
  
  private static Persist persist;

  public void setDb(Persist p) {
    persist = p;
  }
  
  public static void main(String[] args) {
    ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring/**/*.xml");
    persist = (Persist)context.getBean("persist");
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
  private Timeline animation;
  private ConcurrentLinkedQueue<Metric> dataQ = new ConcurrentLinkedQueue<Metric>();
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public void start(Stage primaryStage) throws Exception {
    Group root = new Group();
    this.primaryStage = primaryStage;
    this.primaryStage.setScene(new Scene(root));
    NumberAxis timeAxis = new NumberAxis();
    timeAxis.setLabel("Time");
    timeAxis.setAutoRanging(true);
    timeAxis.setForceZeroInRange(false);
    timeAxis.setTickLabelFormatter(new StringConverter<Number>() {

      @Override
      public Number fromString(String arg0) {
        Instant i = Instant.parse(arg0);
        return i.getMillis();
      }

      @Override
      public String toString(Number arg0) {
        // TODO Auto-generated method stub
        Instant i = new Instant(arg0.longValue());
        return i.toString();
      }
      
    });
    
    NumberAxis dataAxis = new NumberAxis();
    // label would be the reported units
    dataAxis.setLabel("Load");
    dataAxis.setLowerBound(0);
    dataAxis.setUpperBound(1);
 
    LineChart chart = new LineChart(timeAxis, dataAxis);
    
    hourDataSeries = new XYChart.Series<Number,Number>();
    hourDataSeries.setName("15 minute load");
    
    chart.getData().add(hourDataSeries);
    
    root.getChildren().add(chart);
    this.primaryStage.show();
    
    // create timeline to add new data every 60th of second
    animation = new Timeline();
    animation.getKeyFrames().add(new KeyFrame(Duration.millis(100), new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent actionEvent) {
          while (!dataQ.isEmpty()) {
            Metric m = dataQ.remove();
            hourDataSeries.getData().add(new XYChart.Data<Number, Number>(m.timestamp.toInstant().getMillis(), m.value));
          }
        }
    }));
    animation.setCycleCount(Animation.INDEFINITE);
        
    persist.addMetricHandler(new MetricHandler() {      
      @Override
      public void handleMetric(Metric m) {
        System.out.println("Ading metric to queue.");
        dataQ.add(m);
      }
    });
    
    animation.play();

  }

}
