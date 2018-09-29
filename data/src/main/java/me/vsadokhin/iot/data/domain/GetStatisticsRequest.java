package me.vsadokhin.iot.data.domain;

import java.util.List;

import me.vsadokhin.iot.data.MetricTable;

public class GetStatisticsRequest {

    private String aggregator;
    private MetricTable metricTable;
    private String type;
    private List<String> sensorIds;
    private long from;
    private long to;

    public String getAggregator() {
        return aggregator;
    }

    public void setAggregator(String aggregator) {
        this.aggregator = aggregator;
    }

    public MetricTable getMetricTable() {
        return metricTable;
    }

    public void setMetricTable(MetricTable metricTable) {
        this.metricTable = metricTable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
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
}
