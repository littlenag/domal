package com.irksomeideas.domal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.integration.annotation.Transformer;

public class Persist {
 
  List<MetricHandler> handlers = new ArrayList<MetricHandler>();
  
  public void addMetricHandler(MetricHandler handler) {
    handlers.add(handler);
  }
  
  @Transformer
  public Metric persistMetric(Metric m) {
    // TODO save in a hash map
    
    // TODO write a notification engine. Notify subscribers that a new metric has arrived.
    for (MetricHandler handler : handlers) {
      handler.handleMetric(m);
    }
    return m;
  }
}
