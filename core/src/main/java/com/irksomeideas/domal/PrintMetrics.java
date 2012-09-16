package com.irksomeideas.domal;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

public class PrintMetrics {
  private Logger logger = Logger.getLogger(PrintMetrics.class);
  
  private Persist persist;
  
  public void setDb(Persist p) {
    persist = p;
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
  }

}
