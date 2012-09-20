/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package com.irksomeideas.domal.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import org.apache.log4j.Logger;
import org.springframework.integration.annotation.ServiceActivator;

public class MetricStreamServiceImpl implements MetricStreamService {
  
  private Logger logger = Logger.getLogger(MetricStreamServiceImpl.class);

  // You add a device by adding an entry with an empty observable array list
  // of issue IDs in the projects Map.
  final ObservableList<String> deviceNames = FXCollections.<String> observableArrayList();
  final ObservableMap<String, ObservableList<String>> devicesMap = FXCollections.observableMap(new TreeMap<String, ObservableList<String>>());
  final ObservableMap<String, Metric> metricStreamsMap = FXCollections.observableMap(new TreeMap<String, Metric>());
   
  List<MetricHandler> handlers = new ArrayList<MetricHandler>();
  
  public void addMetricHandler(MetricHandler handler) {
    handlers.add(handler);
  }
  
  // TODO replace this with a real db at some point
  private Map<String, Map<String, List<Metric>>> dataPoints = new HashMap<String, Map<String, List<Metric>>>(); 
  
  /**
   * This is the way that the model is told about new metrics.
   * @param m
   */
  @ServiceActivator
  public void persistMetric(Metric m) {
    if (!deviceNames.contains(m.getDeviceName())) {
      deviceNames.add(m.getDeviceName());
      devicesMap.put(m.getDeviceName(), FXCollections.observableList(new ArrayList<String>()));
      logger.info("Adding to devices list");
    }
    
    if (!devicesMap.get(m.getDeviceName()).contains(m.getMetricName())) {
      devicesMap.get(m.getDeviceName()).add(m.getMetricName());
      metricStreamsMap.put(m.getMetricName(), m);
      logger.info("Adding to metric streams list");
    }
    
    Map<String, List<Metric>> deviceData = dataPoints.get(m.getDeviceName());
    if (deviceData == null) {
      deviceData = new HashMap<String, List<Metric>>();
      dataPoints.put(m.getDeviceName(), deviceData);
    }
    
    List<Metric> metricData = deviceData.get(m.getMetricName());
    if (metricData == null) {
      metricData = new ArrayList<Metric>();
      deviceData.put(m.getMetricName(), metricData);
    }
    
    // Finally append this new metric to the list
    logger.info("Persist new metric from " + m + " at " + m.getTimestamp());
    metricData.add(m);
  }
  
  @Override
  public ObservableList<String> getDeviceNames() {
    return deviceNames;
  }

  @Override
  public ObservableList<String> getMetricStreamIds(String projectName) {
    return devicesMap.get(projectName);
  }

  @Override
  public Metric getMetricStream(String issueId) {
    return metricStreamsMap.get(issueId);
  }

  @Override
  public void refresh() {
    // TODO Auto-generated method stub
    
  }
}
