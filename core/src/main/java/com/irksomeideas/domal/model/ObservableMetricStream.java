/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package com.irksomeideas.domal.model;

import javafx.beans.value.ObservableValue;

public interface ObservableMetricStream extends MetricStream {

    public ObservableValue<Number> dateProperty();
    public ObservableValue<String> idProperty();
    public ObservableValue<String> deviceNameProperty();
    public ObservableValue<String> synopsisProperty();
    public ObservableValue<String> descriptionProperty();
}
