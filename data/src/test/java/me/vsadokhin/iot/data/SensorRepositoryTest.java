package me.vsadokhin.iot.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.Collections;

import me.vsadokhin.iot.data.domain.Sensor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootApplication
public class SensorRepositoryTest {

    @Autowired
    private SensorRepository sensorRepository;

    @Before
    public void setUp() {
        sensorRepository.deleteAll();
        sensorRepository.insert(new Sensor(1, "sensor1", 1.1));
        sensorRepository.insert(new Sensor(2, "sensor1", 1.5));
        sensorRepository.insert(new Sensor(3, "sensor1", 2.0));
        sensorRepository.insert(new Sensor(2, "sensor2", 4.4));
        sensorRepository.insert(new Sensor(1, "sensor3", 5.2));
    }

    @Test
    public void getMaxByNamesAndCreatedRange_withSingleName() {
        // act
        double result = sensorRepository.getMaxByNamesAndCreatedRange(Collections.singletonList("sensor1"), 1, 3);

        // verify
        assertThat(result, is(1.5));
    }

    @Test
    public void getMaxByNamesAndCreatedRange_withMultipleNames() {
        // act
        double result = sensorRepository.getMaxByNamesAndCreatedRange(Arrays.asList("sensor1", "sensor2"), 1, 4);

        // verify
        assertThat(result, is(4.4));
    }

    @Test
    public void getMinByNamesAndCreatedRange_withSingleName() {
        // act
        double result = sensorRepository.getMinByNamesAndCreatedRange(Collections.singletonList("sensor1"), 2, 4);

        // verify
        assertThat(result, is(1.5));
    }

    @Test
    public void getMinByNamesAndCreatedRange_withMultipleNames() {
        // act
        double result = sensorRepository.getMinByNamesAndCreatedRange(Arrays.asList("sensor1", "sensor2"), 1, 4);

        // verify
        assertThat(result, is(1.1));
    }

    @Test
    public void getAvgByNamesAndCreatedRange_withSingleName() {
        // act
        double result = sensorRepository.getAvgByNamesAndCreatedRange(Collections.singletonList("sensor1"), 1, 3);

        // verify
        assertThat(result, is(1.3));
    }

    @Test
    public void getAvgByNamesAndCreatedRange_withMultipleNames() {
        // act
        double result = sensorRepository.getAvgByNamesAndCreatedRange(Arrays.asList("sensor1", "sensor2"), 2, 3);

        // verify
        assertThat(result, is(2.95));
    }
}