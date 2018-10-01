package me.vsadokhin.iot.qa.load;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import me.vsadokhin.iot.data.MetricTable;
import me.vsadokhin.iot.data.domain.Metric;
import me.vsadokhin.iot.data.domain.MetricBuilder;
import me.vsadokhin.iot.data.utility.CassandraClusterUtility;
import me.vsadokhin.iot.data.utility.CassandraSessionUtility;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class SimulateSimultaneouslyMetricsTest {

    private static final String PROPERTY_NAME_SIMULTANEOUS_METRICS = "simultaneous.metrics";
    private static final String PROPERTY_NAME_DURATION = "duration";
    private static RestTemplate REST_TEMPLATE = new RestTemplate();

    private static Random RANDOM = new Random();
    private static Session SESSION = CassandraSessionUtility.getSession();

    @BeforeClass
    public static void setUpClass() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        REST_TEMPLATE.setMessageConverters(messageConverters);

        for (MetricTable metricTable : MetricTable.values()) {
            SESSION.execute(QueryBuilder.truncate(metricTable.getTable()));
        }
    }

    @AfterClass
    public static void afterClass() {
        CassandraClusterUtility.closeCluster();
    }

    class MetricPoster implements Runnable {

        private Metric metric;
        private int duration;
        private CountDownLatch countDownLatch;

        MetricPoster(Metric metric, int duration, CountDownLatch countDownLatch) {
            this.duration = duration;
            this.metric = metric;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Metric> entity = new HttpEntity<>(metric, headers);

            for (int i = 0; i < duration; i++) {
                tryToPost(entity);
            }
            countDownLatch.countDown();
        }

        private void tryToPost(HttpEntity<Metric> entity) {
            metric.setValue(-100 + RANDOM.nextFloat() * 200);
            metric.setWhen(System.currentTimeMillis());

            try {
                System.out.println(new Date() + " Sending " + metric);
                REST_TEMPLATE.postForEntity("http://localhost:8080/metric", entity, String.class);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (RestClientException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void validateLaunchProperty(int simultaneousMetrics, String propertyName) {
        if (simultaneousMetrics <= 0) {
            fail(simultaneousMetrics + " is invalid " + propertyName + " value, it has to be greater than 0");
        }
    }

    @Test
    public void sendSimultaneousMetrics() throws InterruptedException {
        // setup
        int simultaneousMetrics = Integer.parseInt(System.getProperty("simultaneous.metrics", "3"));
        int duration = Integer.parseInt(System.getProperty("duration", "60"));
        System.out.println("simultaneous.metrics: " + simultaneousMetrics);
        System.out.println("duration: " + duration);
        validateLaunchProperty(simultaneousMetrics, PROPERTY_NAME_SIMULTANEOUS_METRICS);
        validateLaunchProperty(duration, PROPERTY_NAME_DURATION);

        CountDownLatch countDownLatch = new CountDownLatch(simultaneousMetrics);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < simultaneousMetrics; i++) {
            String type = i % 2 == 0 ? "type1" : "type2";
            Metric metric = new MetricBuilder("sensor" + (i + 1), type).build();
            threads.add(new Thread(new MetricPoster(metric, duration, countDownLatch)));
        }

        // act
        threads.forEach(Thread::start);
        countDownLatch.await();

        // verify
        for (MetricTable table : MetricTable.values()) {
            waitMetricsCount(table.getTable(), (long) simultaneousMetrics * duration);
        }
    }

    private void waitMetricsCount(String table, long expectedMetricsCount) {
        long startTime = System.currentTimeMillis();
        int tenSecondsTimeout = 10000;
        long count = 0;
        while (System.currentTimeMillis() - startTime < tenSecondsTimeout) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ResultSet resultSet = SESSION.execute("SELECT COUNT(*) as count FROM " + table);
            Row row = resultSet.iterator().next();
            count = row.getLong("count");
            System.out.println(new Date() + " " + table + " table contains " + count + " metrics, expected " + expectedMetricsCount);
            if (expectedMetricsCount == count) {
                break;
            }
        }
        
        if (count != expectedMetricsCount) {
            fail(count + " metrics found in " + table + ", expected " + expectedMetricsCount);
        }
    }
}
