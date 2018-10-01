package me.vsadokhin.iot.qa.receive.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import me.vsadokhin.iot.data.MetricTable;
import me.vsadokhin.iot.data.domain.Metric;
import me.vsadokhin.iot.data.domain.MetricBuilder;
import me.vsadokhin.iot.data.utility.CassandraClusterUtility;
import me.vsadokhin.iot.data.utility.CassandraSessionUtility;
import me.vsadokhin.iot.data.utility.DateUtility;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class ReceiveMetricTest {

    private static RestTemplate REST_TEMPLATE = new RestTemplate();

    private static Session SESSION = CassandraSessionUtility.getSession();
    public static final HttpHeaders HEADERS = new HttpHeaders();

    @BeforeClass
    public static void setUpClass() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);

        REST_TEMPLATE.setMessageConverters(messageConverters);
        HEADERS.setContentType(MediaType.APPLICATION_JSON);

        for (MetricTable metricTable : MetricTable.values()) {
            SESSION.execute(QueryBuilder.truncate(metricTable.getTable()));
        }
    }

    @AfterClass
    public static void afterClass() {
        CassandraClusterUtility.closeCluster();
    }


    private Row waitMetricInDB(Select select) {
        long startTime = System.currentTimeMillis();
        int tenSecondsTimeout = 5000;
        while (System.currentTimeMillis() - startTime < tenSecondsTimeout) {
            ResultSet resultSet = SESSION.execute(select);
            int availableCount = resultSet.getAvailableWithoutFetching();
            if (availableCount == 1) {
                return resultSet.iterator().next();
            } else if (availableCount > 1) {
                fail("More than 1 metric is found");
            }
        }
        throw new AssertionError("Metric is not found, finished by timeout");
    }

    @Test
    public void sendMetric_checkMetricInDB() {
        // setup
        Metric metric = new MetricBuilder("sensor1", "type1")
                .setValue(1.23F)
                .setWhen(System.currentTimeMillis()).build();
        HttpEntity<Metric> entity = new HttpEntity<>(metric, HEADERS);

        // act
        ResponseEntity<ResponseEntity> result = REST_TEMPLATE.exchange("http://localhost:8080/metric", HttpMethod.POST, entity, ResponseEntity.class);

        // verify
        assertThat(result.getStatusCode(), is(HttpStatus.ACCEPTED));
        assertThat(result.getBody(), is(nullValue()));

        Select select = QueryBuilder.select().from(MetricTable.METRIC_BY_TYPE.getTable());
        select.where(QueryBuilder.eq("type", metric.getType()))
                .and(QueryBuilder.eq("day", DateUtility.getDayStart(metric.getWhen())));

        Row row = waitMetricInDB(select);
        assertThat(row.getString("sensor_id"), is(metric.getSensorId()));
        assertThat(row.getTimestamp("when").getTime(), is(metric.getWhen()));
        assertThat(row.getFloat("value"), is(metric.getValue()));

        select = QueryBuilder.select().from(MetricTable.METRIC_BY_SENSOR.getTable());
        select.where(QueryBuilder.eq("sensor_id", metric.getSensorId()))
                .and(QueryBuilder.eq("week", DateUtility.getWeekStart(metric.getWhen())));
        row = waitMetricInDB(select);
        assertThat(row.getTimestamp("when").getTime(), is(metric.getWhen()));
        assertThat(row.getFloat("value"), is(metric.getValue()));
    }
    
}
