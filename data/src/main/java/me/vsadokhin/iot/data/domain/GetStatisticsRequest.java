package me.vsadokhin.iot.data.domain;

public class GetStatisticsRequest {

    private String aggregator;
    private String type;
    private String sensorId;
    private long from;
    private long to;

    public String getAggregator() {
        return aggregator;
    }

    public void setAggregator(String aggregator) {
        this.aggregator = aggregator;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "GetStatisticsRequest{" +
                "aggregator='" + aggregator + '\'' +
                ", type='" + type + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
