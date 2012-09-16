package com.irksomeideas.domal;

import java.util.Random;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

public class SnmpCollector {
  
  private Logger logger = Logger.getLogger(SnmpCollector.class);
 
  public SnmpCollector() {
    logger.info("Collector created");
  }
  
  public Metric getNextMetric() {
    final Random r = new Random();
    Metric m = new Metric();
    m.name = "cpu-load";
    m.value = r.nextDouble();
    m.timestamp = new DateTime();
    return m;
  }
}
