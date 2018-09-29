package me.vsadokhin.iot.data.domain;

public class Metric {

    private String sensorId;

    private String type;

    private long when;

    private float value;

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getWhen() {
        return when;
    }

    public void setWhen(long when) {
        this.when = when;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Metric{" +
                "sensorId='" + sensorId + '\'' +
                ", type='" + type + '\'' +
                ", when=" + when +
                ", value=" + value +
                '}';
    }
}