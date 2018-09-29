package me.vsadokhin.iot.data;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import me.vsadokhin.iot.data.domain.GetStatisticsRequest;
import me.vsadokhin.iot.data.domain.Metric;
import me.vsadokhin.iot.data.domain.MetricBuilder;
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
    public static  void beforeClass() {
        Session session = CassandraSessionUtility.getSession();
        for (MetricTable metricTable : MetricTable.values()) {
            session.execute(QueryBuilder.truncate(metricTable.getTable()));
        }

        List<Metric> metrics = new ArrayList<>();
        metrics.add(new MetricBuilder("sensor1","type1")
                .setWhen(1).setValue(Float.MAX_VALUE)
                .build());

        metrics.add(new MetricBuilder("sensor2","type1")
                .setWhen(1).setValue(Float.MIN_VALUE)
                .build());

        metrics.add(new MetricBuilder("sensor1","type1")
                .setWhen(2).setValue(1F)
                .build());

        metrics.add(new MetricBuilder("sensor2","type1")
                .setWhen(2).setValue(0.1F)
                .build());

        metrics.add(new MetricBuilder("sensor3","type2")
                .setWhen(2).setValue(Float.MIN_VALUE)
                .build());

        metrics.add(new MetricBuilder("sensor4","type2")
                .setWhen(2).setValue(Float.MAX_VALUE)
                .build());

        metrics.add(new MetricBuilder("sensor1","type1")
                .setWhen(3).setValue(0.2F)
                .build());

        metrics.add(new MetricBuilder("sensor2","type1")
                .setWhen(3).setValue(0.3F)
                .build());

        metrics.add(new MetricBuilder("sensor1","type1")
                .setWhen(4).setValue(Float.MIN_VALUE)
                .build());

        metrics.add(new MetricBuilder("sensor2","type1")
                .setWhen(4).setValue(Float.MAX_VALUE)
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
        getStatisticsRequest.setTo(4);
    }

    @Test
    public void getStatistics_min_bySensor() {
        // setup
        getStatisticsRequest.setSensorIds(Collections.singletonList("sensor1"));
        getStatisticsRequest.setAggregator("min");
        getStatisticsRequest.setMetricTable(MetricTable.METRIC_BY_SENSOR);

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.2F));

        // setup
        getStatisticsRequest.setSensorIds(Collections.singletonList("sensor2"));

        // act
        result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.1F));
    }

    @Test
    public void getStatistics_max_bySensor() {
        // setup
        getStatisticsRequest.setSensorIds(Collections.singletonList("sensor1"));
        getStatisticsRequest.setAggregator("max");
        getStatisticsRequest.setMetricTable(MetricTable.METRIC_BY_SENSOR);

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(1F));

        // setup
        getStatisticsRequest.setSensorIds(Collections.singletonList("sensor2"));

        // act
        getStatisticsRequest.setSensorIds(Collections.singletonList("sensor2"));
        result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.3F));
    }

    @Test
    public void getStatistics_avg_bySensor() {
        // setup
        getStatisticsRequest.setSensorIds(Collections.singletonList("sensor1"));
        getStatisticsRequest.setAggregator("avg");
        getStatisticsRequest.setMetricTable(MetricTable.METRIC_BY_SENSOR);

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.6F));

        // setup
        getStatisticsRequest.setSensorIds(Collections.singletonList("sensor2"));

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
        getStatisticsRequest.setMetricTable(MetricTable.METRIC_BY_TYPE);

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
        getStatisticsRequest.setMetricTable(MetricTable.METRIC_BY_TYPE);

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
        getStatisticsRequest.setMetricTable(MetricTable.METRIC_BY_TYPE);

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.4F));
    }

    @Test
    public void getStatistics_min_byTypeAndSensors() {
        // setup
        getStatisticsRequest.setType("type1");
        getStatisticsRequest.setSensorIds(Arrays.asList("sensor1", "sensor2"));
        getStatisticsRequest.setAggregator("min");
        getStatisticsRequest.setMetricTable(MetricTable.METRIC_BY_TYPE);

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.1F));

        // setup
        getStatisticsRequest.setSensorIds(Arrays.asList("sensor1", "sensor3"));

        // act
        result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.2F));
    }

    @Test
    public void getStatistics_max_byTypeAndSensors() {
        // setup
        getStatisticsRequest.setType("type1");
        getStatisticsRequest.setSensorIds(Arrays.asList("sensor1", "sensor2"));
        getStatisticsRequest.setAggregator("max");
        getStatisticsRequest.setMetricTable(MetricTable.METRIC_BY_TYPE);

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(1F));

        // setup
        getStatisticsRequest.setSensorIds(Arrays.asList("sensor2", "sensor3"));

        // act
        result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.3F));
    }

    @Test
    public void getStatistics_avg_byTypeAndSensors() {
        // setup
        getStatisticsRequest.setType("type1");
        getStatisticsRequest.setSensorIds(Arrays.asList("sensor1", "sensor2"));
        getStatisticsRequest.setAggregator("avg");
        getStatisticsRequest.setMetricTable(MetricTable.METRIC_BY_TYPE);

        // act
        float result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.4F));

        // setup
        getStatisticsRequest.setSensorIds(Arrays.asList("sensor2"));

        // act
        result = METRIC_REPOSITORY.getStatistics(getStatisticsRequest);

        // verify
        assertThat(result, is(0.2F));
    }
}