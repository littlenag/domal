package com.irksomeideas.domal;

import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main extends Application {
  
  private Logger logger = Logger.getLogger(Main.class);
  
  public static void main(String[] args) {
    //ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring/**/*.xml");
    Application.launch(Main.class, (java.lang.String[])null);
  }
  
  @Override
  public void start(Stage primaryStage) {
      try {
          System.out.println("started");
          AnchorPane page = (AnchorPane) FXMLLoader.load(Main.class.getResource("/Dashboard.fxml"));
          Scene scene = new Scene(page);
          primaryStage.setScene(scene);
          primaryStage.setTitle("Domal Dashboard");
          primaryStage.show();
      } catch (Exception ex) {
        logger.error("What?", ex);
        stop();
      }
  }
  
  @Override
  public void stop() {
    // TODO Cleanly exit when we close the window. Will want to turn into a background
    // daemon later.
    System.exit(0);
  }
}
