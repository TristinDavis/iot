package me.vsadokhin.iot.data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.TimeZone;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import me.vsadokhin.iot.data.domain.Metric;
import me.vsadokhin.iot.data.utility.CassandraSessionUtility;

public class MetricRepository {

    public void insert(Metric metric, MetricTable table) {
        Session session = CassandraSessionUtility.getSession();
        Insert insert = prepareInsert(metric, table);
        session.execute(insert);
    }

    private Insert prepareInsert(Metric metric, MetricTable table) {
        Insert insert = QueryBuilder.insertInto(table.getTable());
        insert.value("sensor_id", metric.getSensorId());
        insert.value("type", metric.getType());
        insert.value("when", metric.getWhen());
        insert.value("value", metric.getValue());
        if (MetricTable.METRIC_BY_SENSOR.equals(table)) {
            insert.value("week", getWeekStart(metric.getWhen()));
        } else {
            long dayStart = Instant.ofEpochMilli(metric.getWhen()).truncatedTo(ChronoUnit.DAYS).toEpochMilli();
            insert.value("day", dayStart);
        }
        return insert;
    }

    private long getWeekStart(long milliseconds) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(milliseconds);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTimeInMillis();
    }
}