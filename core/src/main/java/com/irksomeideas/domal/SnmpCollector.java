package com.irksomeideas.domal;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

public class SnmpCollector {
  
  private Logger logger = Logger.getLogger(SnmpCollector.class);
 
  private BlockingQueue<Metric> metricsCollected = new ArrayBlockingQueue<Metric>(1000);
  private Persist persist;
  
  public SnmpCollector() {
    logger.info("Collector created");
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    
    final Random r = new Random();
    
    Runnable gen = new Runnable() {
      @Override
      public void run() {
        Metric m = new Metric();
        m.name = "cpu-load";
        m.value = r.nextDouble();
            
        metricsCollected.add(m);
      }};
    
    ses.scheduleAtFixedRate(gen, 0, 5, TimeUnit.SECONDS);
  }
  
  @PostConstruct
  public void init() {
    logger.info("Collector starting");
    persist.setQueue(metricsCollected);
  }
  
  public BlockingQueue<Metric> getQueue() {
    return metricsCollected;
  }
  
  public void setDb(Persist p) {
    persist = p;
  }
  
}
