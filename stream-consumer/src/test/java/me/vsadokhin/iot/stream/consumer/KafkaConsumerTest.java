package me.vsadokhin.iot.stream.consumer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import me.vsadokhin.iot.data.MetricRepository;
import me.vsadokhin.iot.data.MetricTable;
import me.vsadokhin.iot.data.domain.Metric;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.kafka.support.Acknowledgment;

public class KafkaConsumerTest {

    private MetricRepository mockMetricRepository;
    private KafkaConsumer kafkaConsumer;

    @Before
    public void setUp() {
        mockMetricRepository = mock(MetricRepository.class);
        kafkaConsumer = new KafkaConsumer(mockMetricRepository);
    }
    
    @Test
    public void processMessage_callMetricRepositoryInsert_withMetricByMetric() {
        // setup
        Metric mockMetric = mock(Metric.class);
        
        // act
        kafkaConsumer.processMessage(mockMetric, mock(Acknowledgment.class));
        
        // verify
        verify(mockMetricRepository).insert(mockMetric, MetricTable.METRIC_BY_SENSOR);
    }

    @Test
    public void processMessage_callMetricRepositoryInsert_withMetricByMetricType() {
        // setup
        Metric mockMetric = mock(Metric.class);

        // act
        kafkaConsumer.processMessage(mockMetric, mock(Acknowledgment.class));

        // verify
        verify(mockMetricRepository).insert(mockMetric, MetricTable.METRIC_BY_TYPE);
    }
    
    @Test
    public void processMessage_callAcknowledgmentAcknowledge() {
        // setup
        Acknowledgment mockAcknowledgment = mock(Acknowledgment.class);

        // act
        kafkaConsumer.processMessage(new Metric(), mockAcknowledgment);

        // verify
        verify(mockAcknowledgment).acknowledge();
    }
    
    @Test
    public void processMessage_callAcknowledgmentAcknowledgeAfterCallMetricRepositoryInsert() {
        // setup
        Acknowledgment mockAcknowledgment = mock(Acknowledgment.class);

        // act
        kafkaConsumer.processMessage(new Metric(), mockAcknowledgment);

        // verify
        InOrder inOrder = Mockito.inOrder(mockMetricRepository, mockAcknowledgment);
        inOrder.verify(mockMetricRepository, times(MetricTable.values().length)).insert(any(), any());
        inOrder.verify(mockAcknowledgment).acknowledge();
    }
}