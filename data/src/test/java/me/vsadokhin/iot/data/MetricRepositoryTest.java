package me.vsadokhin.iot.data;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.TimeZone;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import me.vsadokhin.iot.data.domain.Metric;
import me.vsadokhin.iot.data.utility.CassandraClusterUtility;
import me.vsadokhin.iot.data.utility.CassandraSessionUtility;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class MetricRepositoryTest {

    private MetricRepository metricRepository;
    private Session session;

    @Before
    public void setUp() {
        metricRepository = new MetricRepository();
        session = CassandraSessionUtility.getSession();
        for (MetricTable metricTable : MetricTable.values()) {
            session.execute(QueryBuilder.truncate(metricTable.getTable()));
        }
    }

    @AfterClass
    public static void afterClass() {
        CassandraClusterUtility.closeCluster();
    }

    private void verifyRow(Metric metric, ResultSet result) {
        assertThat(result.getAvailableWithoutFetching(), is(1));
        Row row = result.iterator().next();
        assertThat(row.getString("sensor_id"), is(metric.getSensorId()));
        assertThat(row.getString("type"), is(metric.getType()));
        assertThat(row.getTimestamp("when").getTime(), is(metric.getWhen()));
        assertThat(row.getFloat("value"), is(metric.getValue()));
    }

    @Test
    public void insert_metricsBySensorType() {
        // setup
        Metric metric = new Metric();
        metric.setSensorId("sensor1");
        metric.setType("type1");
        metric.setWhen(System.currentTimeMillis());
        metric.setValue((float) 1.1);

        // act
        metricRepository.insert(metric, MetricTable.METRIC_BY_SENSOR_TYPE);

        // verify
        long dayStart = Instant.ofEpochMilli(metric.getWhen()).truncatedTo(ChronoUnit.DAYS).toEpochMilli();
        Select select = QueryBuilder.select().from(MetricTable.METRIC_BY_SENSOR_TYPE.getTable());
        select.where(QueryBuilder.eq("type", metric.getType())).and(QueryBuilder.eq("day", dayStart));
        ResultSet result = session.execute(select);

        verifyRow(metric, result);
    }

    @Test
    public void insert_metricsBySensor() {
        // setup
        Metric metric = new Metric();
        metric.setSensorId("sensor2");
        metric.setType("type2");
        metric.setWhen(System.currentTimeMillis());
        metric.setValue((float) 2.5);

        // act
        metricRepository.insert(metric, MetricTable.METRIC_BY_SENSOR);

        // verify
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(metric.getWhen());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        Select select = QueryBuilder.select().from(MetricTable.METRIC_BY_SENSOR.getTable());
        select.where(QueryBuilder.eq("sensor_id", metric.getSensorId())).and(QueryBuilder.eq("week", calendar.getTime()));
        ResultSet result = session.execute(select);

        verifyRow(metric, result);
    }

}