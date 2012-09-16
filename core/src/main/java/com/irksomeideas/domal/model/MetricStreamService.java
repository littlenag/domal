/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package com.irksomeideas.domal.model;

import javafx.collections.ObservableList;

public interface MetricStreamService {

  // enumerate devices
  public ObservableList<String> getDeviceNames();
  
  // enumerate metric streams being reported for a given device
  public ObservableList<String> getMetricStreamIds(String deviceName);
  
  // get a single metric stream
  public ObservableMetricStream getMetricStream(String metricStreamId);
}
