package copper;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
  
  private static final Logger logger = Logger.getLogger(Main.class);
  
  public static void main(String[] args) {
    try {
      System.out.println("Copper has started");
      ApplicationContext context = new ClassPathXmlApplicationContext("classpath:/core/**/*.xml");

    } catch (Exception ex) {
      logger.error("Oops!", ex);
    }
  }
}
