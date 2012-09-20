/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package com.irksomeideas.domal.model;

import java.util.ArrayList;
import java.util.List;
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
  
  /**
   * This is the way that the model is told about new metrics.
   * @param m
   */
  @ServiceActivator
  public void persistMetric(Metric m) {
    if (!deviceNames.contains(m.deviceName)) {
      deviceNames.add(m.deviceName);
      devicesMap.put(m.deviceName, FXCollections.observableList(new ArrayList<String>()));
      logger.info("Adding to devices list");
    }
    
    if (!devicesMap.get(m.deviceName).contains(m.metricName)) {
      devicesMap.get(m.deviceName).add(m.metricName);
      metricStreamsMap.put(m.metricName, m);
      logger.info("Adding to metric streams list");
    }
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
