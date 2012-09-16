/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package com.irksomeideas.domal.model;

public interface MetricStream {

    public long getDate();
    public String getId();
    public String getDeviceName();
    public String getMetricName();
    public String getUnits();
}
