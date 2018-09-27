package me.vsadokhin.iot.receive.api;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

import me.vsadokhin.iot.data.domain.Sensor;
import me.vsadokhin.iot.stream.producer.KafkaProducer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SensorResourceTest {

    private SensorResource resource;
    private KafkaProducer mockKafkaProducer;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        mockKafkaProducer = mock(KafkaProducer.class);
        resource = new SensorResource(mockKafkaProducer);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void create_callKafkaTemplateSendAsync() {
        // setup
        Sensor mockSensor = mock(Sensor.class);

        // act
        resource.create(mockSensor);

        // verify
        verify(mockKafkaProducer).sendAsync("sensor", mockSensor);
    }

    @Test
    public void create_checkResult() {
        // act
        ResponseEntity result = resource.create(new Sensor());

        // verify
        assertThat(result.getStatusCode(), is(HttpStatus.ACCEPTED));
    }
}