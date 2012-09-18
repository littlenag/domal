/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package com.irksomeideas.domal.model;

import javafx.beans.value.ObservableValue;

public interface ObservableMetricStream extends MetricStream {
    public ObservableValue<String> deviceNameProperty();
    public ObservableValue<String> metricNameProperty();
    public ObservableValue<String> unitsProperty();
}
