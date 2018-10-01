package me.vsadokhin.iot.data.domain;

import javax.validation.constraints.NotNull;

public class Metric {

    @NotNull
    private String sensorId;

    @NotNull
    private String type;

    @NotNull
    private Long when;

    @NotNull
    private Float value;

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

    public Long getWhen() {
        return when;
    }

    public void setWhen(Long when) {
        this.when = when;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
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