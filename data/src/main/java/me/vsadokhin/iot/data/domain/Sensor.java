package me.vsadokhin.iot.data.domain;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("sensor")
public class Sensor {

    @PrimaryKey
    private SensorId id;

    @Column
    private double value;

    public Sensor() {
    }

    public Sensor(long created, String name, double value) {
        id = new SensorId(created, name);
        this.value = value;
    }
}