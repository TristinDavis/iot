package me.vsadokhin.iot.stream.consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import me.vsadokhin.iot.data.MetricRepository;
import me.vsadokhin.iot.data.MetricTable;
import me.vsadokhin.iot.data.domain.Metric;
import org.junit.Before;
import org.junit.Test;

public class KafkaConsumerTest {

    private MetricRepository mockMetricRepository;
    private KafkaConsumer kafkaConsumer;

    @Before
    public void setUp() {
        mockMetricRepository = mock(MetricRepository.class);
        kafkaConsumer = new KafkaConsumer(mockMetricRepository);
    }
    
    @Test
    public void processMessage_callSensorRepositoryInsert_withMetricBySensor() {
        // setup
        Metric mockMetric = mock(Metric.class);
        
        // act
        kafkaConsumer.processMessage(mockMetric);
        
        // verify
        verify(mockMetricRepository).insert(mockMetric, MetricTable.METRIC_BY_SENSOR);
    }

    @Test
    public void processMessage_callSensorRepositoryInsert_withMetricBySensorType() {
        // setup
        Metric mockMetric = mock(Metric.class);

        // act
        kafkaConsumer.processMessage(mockMetric);

        // verify
        verify(mockMetricRepository).insert(mockMetric, MetricTable.METRIC_BY_SENSOR_TYPE);
    }

}