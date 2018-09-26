package me.vsadokhin.iot.receive.api;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

import me.vsadokhin.iot.data.domain.Sensor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

public class SensorResourceTest {

    private SensorResource resource;
    private KafkaTemplate mockKafkaTemplate;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        mockKafkaTemplate = mock(KafkaTemplate.class);
        resource = new SensorResource(mockKafkaTemplate);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void create_callKafkaTemplateSend() {
        // setup
        Sensor mockSensor = mock(Sensor.class);

        // act
        resource.create(mockSensor);

        // verify
        verify(mockKafkaTemplate).send("sensor", mockSensor);
    }

    @Test
    public void create_checkResult() {
        // act
        ResponseEntity result = resource.create(new Sensor());

        // verify
        assertThat(result.getStatusCode(), is(HttpStatus.CREATED));
    }
}