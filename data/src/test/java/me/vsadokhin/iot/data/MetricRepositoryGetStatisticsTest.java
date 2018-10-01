package me.vsadokhin.iot.data;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import me.vsadokhin.iot.data.domain.GetStatisticsRequest;
import me.vsadokhin.iot.data.domain.Metric;
import me.vsadokhin.iot.data.domain.MetricBuilder;
import me.vsadokhin.iot.data.exception.GetStatisticsException;
import me.vsadokhin.iot.data.utility.CassandraClusterUtility;
import me.vsadokhin.iot.data.utility.CassandraSessionUtility;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MetricRepositoryGetStatisticsTest {

    private static final MetricRepository METRIC_REPOSITORY = new MetricRepository();
    
    private GetStatisticsRequest getStatisticsRequest;

    @BeforeClass
    public static void beforeClass() {
        Session session = CassandraSessionUtility.getSession();
        for (MetricTable metricTable : MetricTable.values()) {
            session.execute(QueryBuilder.truncate(metricTable.getTable()));
        }

        List<Metric> metrics = new ArrayList<>();
        metrics.add(new MetricBuilder("sensor1","type1")
                .setWhen(1L).setValue(Float.MAX_VALUE)
                .build());

        metrics.add(new MetricBuilder("sensor2","type1")
                .setWhen(1L).setValue(Float.MIN_VALUE)
                .build());

        metrics.add(new MetricBuilder("sensor1","type1")
                .setWhen(2L).setValue(1F)
                .build());

        metrics.add(new MetricBuilder("sensor2","type1")
                .setWhen(2L).setValue(0.1F)
                .build());

        metrics.add(new MetricBuilder("sensor3","type2")
                .setWhen(2L).setValue(Float.MIN_VALUE)
                .build());

        metrics.add(new MetricBuilder("sensor4","type2")
                .setWhen(2L).setValue(Float.MAX_VALUE)
                .build());

        metrics.add(new MetricBuilder("sensor1","type1")
                .setWhen(3L).setValue(0.2F)
                .build());

        metrics.add(new MetricBuilder("sensor2","type1")
                .setWhen(3L).setValue(0.3F)
                .build());

        metrics.add(new MetricBuilder("sensor1","type1")
                .setWhen(4L).setValue(Float.MIN_VALUE)
                .build());

        metrics.add(new MetricBuilder("sensor2","type1")
                .setWhen(4L).setValue(Float.MAX_VALUE)
                .build());

        for (Metric metric : metrics) {
            METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);
            METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);
        }
    }

    @AfterClass
    public static void afterClass() {
        CassandraClusterUtility.closeCluster();
    }

    @Before
    public void setUp() {
        getStatisticsRequest = new GetStatisticsRequest();
        getStatisticsRequest.setFrom(2);
        getStatisticsRequest.setTo(3);
    }

    @Test
    public void getStatistics_min_bySensor() {
        // setup
        getStatisticsRequest.setSensorId("sensor1");
        getStatisticsRequest.setAggregator("min");

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.2F));

        // setup
        getStatisticsRequest.setSensorId("sensor2");

        // act
        result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.1F));
    }

    @Test
    public void getStatistics_max_bySensor() {
        // setup
        getStatisticsRequest.setSensorId("sensor1");
        getStatisticsRequest.setAggregator("max");

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(1F));

        // setup
        getStatisticsRequest.setSensorId("sensor2");

        // act
        result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.3F));
    }

    @Test
    public void getStatistics_avg_bySensor() {
        // setup
        getStatisticsRequest.setSensorId("sensor1");
        getStatisticsRequest.setAggregator("avg");

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.6F));

        // setup
        getStatisticsRequest.setSensorId("sensor2");

        // act
        result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.2F));
    }

    @Test
    public void getStatistics_min_byType() {
        // setup
        getStatisticsRequest.setType("type1");
        getStatisticsRequest.setAggregator("min");

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.1F));

        // setup
        getStatisticsRequest.setType("type2");

        // act
        result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(Float.MIN_VALUE));
    }

    @Test
    public void getStatistics_max_byType() {
        // setup
        getStatisticsRequest.setType("type1");
        getStatisticsRequest.setAggregator("max");

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(1F));

        // setup
        getStatisticsRequest.setType("type2");

        // act
        result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(Float.MAX_VALUE));
    }

    @Test
    public void getStatistics_avg_byType() {
        // setup
        getStatisticsRequest.setType("type1");
        getStatisticsRequest.setAggregator("avg");

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.4F));
    }
    
    @Test
    public void getStatistics_noType_noSensor_checkGetStatisticsException() {
        try {
            // act
            METRIC_REPOSITORY.getStatistics(new GetStatisticsRequest());
            fail();
        } catch (GetStatisticsException e) {
            // verify
            assertThat(e.getMessage(), is("Type or sensorId must be specified"));
        }
    }

    @Test
    public void getStatistics_bothTypeAndSensorAreSpecified_checkGetStatisticsException() {
        // setup
        getStatisticsRequest.setSensorId("sensor");
        getStatisticsRequest.setType("type");
        
        try {
            // act
            METRIC_REPOSITORY.getStatistics(getStatisticsRequest);
            fail();
        } catch (GetStatisticsException e) {
            // verify
            assertThat(e.getMessage(), is("Only type or only sensorId must be specified"));
        }
    }
    
    @Test
    public void getStatistics_noAggregator_checkGetStatisticsException() {
        // setup
        getStatisticsRequest.setSensorId("sensor");
        getStatisticsRequest.setAggregator(null);
        
        try {
            // act
            METRIC_REPOSITORY.getStatistics(getStatisticsRequest);
            fail();
        } catch (GetStatisticsException e) {
            // verify
            assertThat(e.getMessage(), is("null is not supported aggregator. Supported values: min, max, avg"));
        }
    }

    @Test
    public void getStatistics_wrongAggregator_checkGetStatisticsException() {
        // setup
        getStatisticsRequest.setSensorId("sensor");
        getStatisticsRequest.setAggregator("wrongAggr");

        try {
            // act
            METRIC_REPOSITORY.getStatistics(getStatisticsRequest);
            fail();
        } catch (GetStatisticsException e) {
            // verify
            assertThat(e.getMessage(), is("wrongAggr is not supported aggregator. Supported values: min, max, avg"));
        }
    }

    @Test
    public void getStatistics_toBeforeFrom_checkGetStatisticsException() {
        // setup
        getStatisticsRequest.setSensorId("sensor");
        getStatisticsRequest.setAggregator("min");
        getStatisticsRequest.setTo(1);
        getStatisticsRequest.setFrom(2);

        try {
            // act
            METRIC_REPOSITORY.getStatistics(getStatisticsRequest);
            fail();
        } catch (GetStatisticsException e) {
            // verify
            assertThat(e.getMessage(), is("To must be greater than From"));
        }
    }

    @Test
    public void getStatistics_bySensor_fromAndToAreNotInTheSameWeek_checkGetStatisticsException() {
        // setup
        getStatisticsRequest.setSensorId("sensor");
        getStatisticsRequest.setAggregator("min");
        getStatisticsRequest.setTo(System.currentTimeMillis());
        getStatisticsRequest.setFrom(LocalDateTime.now().minusDays(8).toInstant(ZoneOffset.UTC).toEpochMilli());

        try {
            // act
            METRIC_REPOSITORY.getStatistics(getStatisticsRequest);
            fail();
        } catch (GetStatisticsException e) {
            // verify
            assertThat(e.getMessage(), is("From and To must point to the same week for aggregate by sensor query"));
        }
    }

    @Test
    public void getStatistics_bySensor_fromAndToAreNotInTheSameDay_checkGetStatisticsException() {
        // setup
        getStatisticsRequest.setType("type");
        getStatisticsRequest.setAggregator("min");
        getStatisticsRequest.setTo(System.currentTimeMillis());
        getStatisticsRequest.setFrom(LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli());

        try {
            // act
            METRIC_REPOSITORY.getStatistics(getStatisticsRequest);
            fail();
        } catch (GetStatisticsException e) {
            // verify
            assertThat(e.getMessage(), is("From and To must point to the same day for aggregate by type query"));
        }
    }
}