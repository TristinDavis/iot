package me.vsadokhin.iot.data;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import me.vsadokhin.iot.data.domain.Metric;
import me.vsadokhin.iot.data.domain.MetricBuilder;
import me.vsadokhin.iot.data.exception.StorageException;
import me.vsadokhin.iot.data.utility.CassandraClusterUtility;
import me.vsadokhin.iot.data.utility.CassandraSessionUtility;
import me.vsadokhin.iot.data.utility.DateUtility;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class MetricRepositoryInsertTest {

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
        assertThat(row.getTimestamp("when").getTime(), is(metric.getWhen()));
        assertThat(row.getFloat("value"), is(metric.getValue()));
    }

    @Test
    public void insert_metricByType() {
        // setup
        Metric metric = new MetricBuilder("sensor1", "type1")
                .setWhen(System.currentTimeMillis())
                .setValue(1.1F)
                .build();

        // act
        metricRepository.insert(metric, MetricTable.METRIC_BY_TYPE);

        // verify
        Select select = QueryBuilder.select().from(MetricTable.METRIC_BY_TYPE.getTable());
        select.where(QueryBuilder.eq("type", metric.getType()))
                .and(QueryBuilder.eq("day", DateUtility.getDayStart(metric.getWhen())));
        ResultSet result = session.execute(select);

        verifyRow(metric, result);
    }

    @Test
    public void insert_metricBySensor() {
        // setup
        Metric metric = new MetricBuilder("sensor2")
                .setWhen(System.currentTimeMillis())
                .setValue(2.5F)
                .build();

        // act
        metricRepository.insert(metric, MetricTable.METRIC_BY_SENSOR);

        // verify
        Select select = QueryBuilder.select().from(MetricTable.METRIC_BY_SENSOR.getTable());
        select.where(QueryBuilder.eq("sensor_id", metric.getSensorId()))
                .and(QueryBuilder.eq("week", DateUtility.getWeekStart(metric.getWhen())));
        ResultSet result = session.execute(select);

        verifyRow(metric, result);
    }

    @Test
    public void insert_metricByType_noType_checkStorageException() {
        // setup
        Metric metric = new MetricBuilder("sensor2")
                .setWhen(System.currentTimeMillis())
                .setValue(2.5F)
                .build();
        metric.setType(null);

        try {
            // act
            metricRepository.insert(metric, MetricTable.METRIC_BY_TYPE);
            fail();
        } catch (StorageException e) {
            // verify
            assertThat(e.getMessage(), is("Failed to insert metric: " + metric));
        }
    }

    @Test
    public void insert_metricBySensor_noSensor_checkStorageException() {
        // setup
        Metric metric = new MetricBuilder(null)
                .setWhen(System.currentTimeMillis())
                .setValue(2.5F)
                .build();

        try {
            // act
            metricRepository.insert(metric, MetricTable.METRIC_BY_TYPE);
            fail();
        } catch (StorageException e) {
            // verify
            assertThat(e.getMessage(), is("Failed to insert metric: " + metric));
        }
    }
}