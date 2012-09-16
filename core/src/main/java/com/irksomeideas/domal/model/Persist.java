package com.irksomeideas.domal.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.integration.annotation.ServiceActivator;


public class Persist {
 
  List<MetricHandler> handlers = new ArrayList<MetricHandler>();
  
  public void addMetricHandler(MetricHandler handler) {
    handlers.add(handler);
  }
  
  @ServiceActivator
  public void persistMetric(Metric m) {
    // TODO save in a hash map
    
    // TODO write a notification engine. Notify subscribers that a new metric has arrived.
    for (MetricHandler handler : handlers) {
      handler.handleMetric(m);
    }
  }
}
