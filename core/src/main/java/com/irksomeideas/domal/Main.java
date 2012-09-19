package com.irksomeideas.domal;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main extends Application {
  
  private Logger logger = Logger.getLogger(Main.class);
  
  public static void main(String[] args) {
    Application.launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    try {
      System.out.println("Domal has started");
      ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring/**/*.xml");

      // AnchorPane page = (AnchorPane) FXMLLoader.load(Main.class.getResource("/Dashboard.fxml"));
      // FXMLLoader loader = new FXMLLoader();
      // DashboardController controller = context.getBean(DashboardController.class);
      // loader.setController(controller);
      // AnchorPane page = (AnchorPane)loader.load(Main.class.getResource("/Dashboard.fxml"));

      SpringFxmlLoader loader = new SpringFxmlLoader(context);
      Parent page = (Parent) loader.load("/Dashboard.fxml", DashboardController.class);
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
    // TODO Cleanly exit when we close the window. Will want to turn into a
    // background
    // daemon later.
    System.exit(0);
  }
}
