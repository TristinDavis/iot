package me.vsadokhin.iot.data.domain;

public class MetricBuilder {

    private Metric metric = new Metric();

    public MetricBuilder(String sensorId) {
        metric.setSensorId(sensorId);
    }

    public MetricBuilder(String sensorId, String type) {
        this(sensorId);
        metric.setType(type);
    }

    public MetricBuilder setValue(Float value) {
        metric.setValue(value);
        return this;
    }

    public MetricBuilder setWhen(Long when) {
        metric.setWhen(when);
        return this;
    }

    public Metric build() {
        return metric;
    }
}
