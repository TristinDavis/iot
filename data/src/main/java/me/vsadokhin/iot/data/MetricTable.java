package me.vsadokhin.iot.data;

public enum MetricTable {

    METRIC_BY_SENSOR("metric_by_sensor"),
    METRIC_BY_TYPE("metric_by_type");

    private final String table;

    MetricTable(String table) {
        this.table = table;
    }

    public String getTable() {
        return table;
    }
}
