package com.irksomeideas.domal.model;

import org.joda.time.DateTime;

/**
 * A single collected instance of a metric
 * Metrics form a stream over which analytics can be run
 * All metrics in a window for a DataSet
 * Metrics are numeric in nature and contain a single real value along with a timestamp 
 * @author mark
 * 
 * reported by could be optional if the software already knows about the IP/host mapping
 * or included if another system, like zenoss, is acting as a data proxy, !name is treated
 * as a host name in the hierarchy
 * <device-metric host="ward.local" name="cpu/5minload" value="2.3"/>
 * <device-metric host="/Server/Linux/!ward.local" name="cpu/15minload" value="2.3"/>
 * <device-metric class="/Server/Linux" host="ward.local" name="ups-status" value="2.3"/>
 * 
 * 
 * <app-metric application="/Server/Linux/ward" name="cpu/15minload" value="2.3"/>
 * 
 * two base concepts: sensors and metrics
 *  -> sensors create streams of metrics
 *  -> some metrics are numeric, other can contain more free-form data
 *
 */
public class Metric {
  
  private String deviceName;       // device that generated this metric
  private String metricName;       // name of this metric
  private Double value;
  private DateTime timestamp = new DateTime();
  private String units = "%";      // reported units
  private String tags = "rack=2";  // metadata about either the device or the stream itself
  
  public Metric(String device, String metric, Double value) {
    this.deviceName = device;
    this.metricName = metric;
    this.value = value;
  }
  
  public String getMetricName() {
    return metricName;
  }
  
  public String getDeviceName() {
    return deviceName;
  }
  
  public Double getValue() {
    return value;
  }
  
  public DateTime getTimestamp() {
    return timestamp;
  }
  
  public String getUnits() {
    return units;
  }
  
  public String getTags() {
    return tags;
  }

  /**
   * other properties
   * universe id
   * tags & k/v pairs (e.g. S=foo P=bar C=baz)
   * units
   * scale factor
   */
  
  @Override
  public String toString() {
    return deviceName + "/" + metricName + "#" + value;
  }

}
