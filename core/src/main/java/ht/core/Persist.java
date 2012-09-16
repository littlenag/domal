package ht.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

public class Persist {
  
  private BlockingQueue<Metric> metricsCollected;
  
  public void setQueue(BlockingQueue<Metric> queue) {
    metricsCollected = queue;
  }

  List<MetricHandler> handlers = new ArrayList<MetricHandler>();
  
  public void addMetricHandler(MetricHandler handler) {
    handlers.add(handler);
  }
  
  @PostConstruct
  public void init() {
    
    ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
    
    Runnable gen = new Runnable() {
      @Override
      public void run() {
        
        Metric m = metricsCollected.poll();
        
        if (m != null) {
          for (MetricHandler handler : handlers) {
            handler.handleMetric(m);
          }
        }
      }};
    
    ses.scheduleAtFixedRate(gen, 1, 1, TimeUnit.SECONDS);
  }
}
