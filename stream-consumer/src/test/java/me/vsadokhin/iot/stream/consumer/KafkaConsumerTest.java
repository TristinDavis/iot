package me.vsadokhin.iot.stream.consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import me.vsadokhin.iot.data.SensorRepository;
import me.vsadokhin.iot.data.domain.Sensor;
import org.junit.Before;
import org.junit.Test;

public class KafkaConsumerTest {

    private SensorRepository mockSensorRepository;
    private KafkaConsumer kafkaConsumer;

    @Before
    public void setUp() {
        mockSensorRepository = mock(SensorRepository.class);
        kafkaConsumer = new KafkaConsumer(mockSensorRepository);
    }
    
    @Test
    public void processMessage_callSensorRepositoryInsert() {
        // setup
        Sensor mockSensor = mock(Sensor.class);
        
        // act
        kafkaConsumer.processMessage(mockSensor);
        
        // verify
        verify(mockSensorRepository).insert(mockSensor);
    }

}