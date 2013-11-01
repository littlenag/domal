package copper;

public interface ObservationStream {
    public InstanceId instance();
    public String host();
    public String witness();
    public String name();
}
