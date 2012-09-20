package com.irksomeideas.domal.collector;

import java.util.Random;

import org.apache.log4j.Logger;

import com.irksomeideas.domal.model.Metric;

public class SnmpCollector {
  
  private Logger logger = Logger.getLogger(SnmpCollector.class);
 
  final Random r = new Random();

  public SnmpCollector() {
    logger.info("Collector created");
  }
  
  public Metric getNextMetric() {
    Metric m = new Metric("zenoss.local", "cpuload", r.nextDouble());
    return m;
  }
}
