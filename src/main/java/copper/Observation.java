package copper;

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
public class Observation implements ObservationStream {

  private InstanceId instance;   // instance of copper that this observation is tied to
  private String host;           // hostname of the device that generated this metric
  private String witness;        // software that detected this observation
  private String name;           // name of this observation

  private Double value;
  private DateTime timestamp = new DateTime();
  private String units = "%";      // reported units
  private String tags = "rack=2";  // metadata about either the device or the stream itself
  
  public Observation(InstanceId instance, String host, String witness, String name, Double value, DateTime timestamp) {
    this.instance = instance;
    this.host = host;
    this.witness = witness;
    this.name = name;
    this.value = value;
    this.timestamp = timestamp;
  }

  public InstanceId instance() {
    return instance;
  }
  
  public String host() {
    return host;
  }
  
  public String witness() {
    return witness;
  }

  public String name() {
    return name;
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
    return host + "/" + witness + "/" + name + "#" + value;
  }
}
