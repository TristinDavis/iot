package me.vsadokhin.iot.receive.api;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import me.vsadokhin.iot.data.domain.Sensor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class SensorResourceTest {

    private SensorResource resource;

    @Before
    public void setUp() {
        resource = new SensorResource();
    }

    @Test
    public void create_checkResult() {
        // act
        ResponseEntity result = resource.create(new Sensor());

        // verify
        assertThat(result.getStatusCode(), is(HttpStatus.CREATED));
    }
}