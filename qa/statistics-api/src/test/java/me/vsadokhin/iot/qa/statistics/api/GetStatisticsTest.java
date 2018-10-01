package me.vsadokhin.iot.qa.statistics.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import me.vsadokhin.iot.data.MetricRepository;
import me.vsadokhin.iot.data.MetricTable;
import me.vsadokhin.iot.data.domain.Metric;
import me.vsadokhin.iot.data.domain.MetricBuilder;
import me.vsadokhin.iot.data.utility.CassandraClusterUtility;
import me.vsadokhin.iot.data.utility.CassandraSessionUtility;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class GetStatisticsTest {

    private static final String ENDPOINT = "http://localhost:8081/statistics";
    
    private static RestTemplate REST_TEMPLATE = new RestTemplate();

    private static Session SESSION = CassandraSessionUtility.getSession();

    private static final HttpHeaders HEADERS = new HttpHeaders();

    private static final MetricRepository METRIC_REPOSITORY = new MetricRepository();
    
    private HttpEntity<Metric> entity;

    @BeforeClass
    public static void setUpClass() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);

        REST_TEMPLATE.setMessageConverters(messageConverters);
        HEADERS.setContentType(MediaType.APPLICATION_JSON);
        HEADERS.setBasicAuth("getStatisticsUser", "statistics123");
    }

    @AfterClass
    public static void afterClass() {
        CassandraClusterUtility.closeCluster();
    }

    @Before
    public void setUp() {
        entity = new HttpEntity<>(HEADERS);
        for (MetricTable metricTable : MetricTable.values()) {
            SESSION.execute(QueryBuilder.truncate(metricTable.getTable()));
        }
    }

    @Test
    public void getStatistics_min_bySensor() {
        // setup
        Metric metric = new MetricBuilder("sensor1", "type1")
                .setValue(Float.MIN_VALUE).setWhen(0L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(1.0F).setWhen(1L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        metric = new MetricBuilder("sensor2", "type1")
                        .setValue(Float.MIN_VALUE).setWhen(1L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(2.0F).setWhen(2L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(Float.MIN_VALUE).setWhen(3L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        // act
        UriComponentsBuilder uirBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8081/statistics")
                .queryParam("sensorId", "sensor1")
                .queryParam("aggregator", "min")
                .queryParam("from", 1)
                .queryParam("to", 2);
        ResponseEntity<Float> result = REST_TEMPLATE.exchange(uirBuilder.toUriString(),
                HttpMethod.GET, entity, Float.class);

        // verify
        assertThat(result.getBody(), is(1.0F));
    }

    @Test
    public void getStatistics_max_bySensor() {
        // setup
        Metric metric = new MetricBuilder("sensor1", "type1")
                .setValue(Float.MAX_VALUE).setWhen(0L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(1.0F).setWhen(1L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        metric = new MetricBuilder("sensor2", "type1")
                        .setValue(Float.MAX_VALUE).setWhen(1L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(2.0F).setWhen(2L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(Float.MAX_VALUE).setWhen(3L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        // act
        UriComponentsBuilder uirBuilder = UriComponentsBuilder.fromHttpUrl(ENDPOINT)
                .queryParam("sensorId", "sensor1")
                .queryParam("aggregator", "max")
                .queryParam("from", 1)
                .queryParam("to", 2);
        ResponseEntity<Float> result = REST_TEMPLATE.exchange(uirBuilder.toUriString(),
                HttpMethod.GET, entity, Float.class);

        // verify
        assertThat(result.getBody(), is(2.0F));
    }

    @Test
    public void getStatistics_avg_bySensor() {
        // setup
        Metric metric = new MetricBuilder("sensor1", "type1")
                .setValue(123F).setWhen(0L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(1.0F).setWhen(1L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        metric = new MetricBuilder("sensor2", "type1")
                        .setValue(123F).setWhen(1L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(2.0F).setWhen(2L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(123F).setWhen(3L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_SENSOR);

        // act
        UriComponentsBuilder uirBuilder = UriComponentsBuilder.fromHttpUrl(ENDPOINT)
                .queryParam("sensorId", "sensor1")
                .queryParam("aggregator", "avg")
                .queryParam("from", 1)
                .queryParam("to", 2);
        ResponseEntity<Float> result = REST_TEMPLATE.exchange(uirBuilder.toUriString(),
                HttpMethod.GET, entity, Float.class);

        // verify
        assertThat(result.getBody(), is(1.5F));
    }

    @Test
    public void getStatistics_min_byType() {
        // setup
        Metric metric = new MetricBuilder("sensor1", "type1")
                .setValue(Float.MIN_VALUE).setWhen(0L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        metric = new MetricBuilder("sensor2", "type1")
                        .setValue(1.0F).setWhen(1L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        metric = new MetricBuilder("sensor3", "type2")
                        .setValue(Float.MIN_VALUE).setWhen(1L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(2.0F).setWhen(2L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(Float.MIN_VALUE).setWhen(3L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        // act
        UriComponentsBuilder uirBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:8081/statistics")
                .queryParam("type", "type1")
                .queryParam("aggregator", "min")
                .queryParam("from", 1)
                .queryParam("to", 2);
        ResponseEntity<Float> result = REST_TEMPLATE.exchange(uirBuilder.toUriString(),
                HttpMethod.GET, entity, Float.class);

        // verify
        assertThat(result.getBody(), is(1.0F));
    }

    @Test
    public void getStatistics_max_byType() {
        // setup
        Metric metric = new MetricBuilder("sensor1", "type1")
                .setValue(Float.MAX_VALUE).setWhen(0L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        metric = new MetricBuilder("sensor2", "type1")
                        .setValue(1.0F).setWhen(1L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        metric = new MetricBuilder("sensor3", "type2")
                        .setValue(Float.MAX_VALUE).setWhen(1L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(2.0F).setWhen(2L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(Float.MAX_VALUE).setWhen(3L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        // act
        UriComponentsBuilder uirBuilder = UriComponentsBuilder.fromHttpUrl(ENDPOINT)
                .queryParam("type", "type1")
                .queryParam("aggregator", "max")
                .queryParam("from", 1)
                .queryParam("to", 2);
        ResponseEntity<Float> result = REST_TEMPLATE.exchange(uirBuilder.toUriString(),
                HttpMethod.GET, entity, Float.class);

        // verify
        assertThat(result.getBody(), is(2.0F));
    }

    @Test
    public void getStatistics_avg_byType() {
        // setup
        Metric metric = new MetricBuilder("sensor1", "type1")
                .setValue(123F).setWhen(0L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        metric = new MetricBuilder("sensor2", "type1")
                        .setValue(1.0F).setWhen(1L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        metric = new MetricBuilder("sensor3", "type2")
                        .setValue(123F).setWhen(1L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(2.0F).setWhen(2L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        metric = new MetricBuilder("sensor1", "type1")
                        .setValue(123F).setWhen(3L).build();
        METRIC_REPOSITORY.insert(metric, MetricTable.METRIC_BY_TYPE);

        // act
        UriComponentsBuilder uirBuilder = UriComponentsBuilder.fromHttpUrl(ENDPOINT)
                .queryParam("type", "type1")
                .queryParam("aggregator", "avg")
                .queryParam("from", 1)
                .queryParam("to", 2);
        ResponseEntity<Float> result = REST_TEMPLATE.exchange(uirBuilder.toUriString(),
                HttpMethod.GET, entity, Float.class);

        // verify
        assertThat(result.getBody(), is(1.5F));
    }
    
}
