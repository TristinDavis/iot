package me.vsadokhin.iot.data.domain;

import static org.springframework.data.cassandra.core.cql.PrimaryKeyType.PARTITIONED;

import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class SensorId {

    @PrimaryKeyColumn
    private long created;

    @PrimaryKeyColumn(type = PARTITIONED)
    private String name;

    public SensorId() {
    }

    public SensorId(long created, String name) {
        this.created = created;
        this.name = name;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}