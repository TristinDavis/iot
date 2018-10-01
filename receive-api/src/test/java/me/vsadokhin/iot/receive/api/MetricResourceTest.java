package me.vsadokhin.iot.receive.api;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

import me.vsadokhin.iot.data.domain.Metric;
import me.vsadokhin.iot.stream.producer.KafkaProducer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MetricResourceTest {

    private MetricResource resource;
    private KafkaProducer mockKafkaProducer;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        mockKafkaProducer = mock(KafkaProducer.class);
        resource = new MetricResource(mockKafkaProducer);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void create_callKafkaTemplateSendAsync() {
        // setup
        Metric mockMetric = mock(Metric.class);

        // act
        resource.create(mockMetric);

        // verify
        verify(mockKafkaProducer).sendAsync("metric", mockMetric);
    }

    @Test
    public void create_checkResult() {
        // act
        ResponseEntity result = resource.create(new Metric());

        // verify
        assertThat(result.getStatusCode(), is(HttpStatus.ACCEPTED));
    }
}