package me.vsadokhin.iot.stream.consumer;

import me.vsadokhin.iot.data.MetricRepository;
import me.vsadokhin.iot.data.MetricTable;
import me.vsadokhin.iot.data.domain.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final MetricRepository metricRepository;

    @Autowired
    public KafkaConsumer(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    @KafkaListener(id = "metric-listener", topics = "metric", groupId = "stream", errorHandler = "metricListenerErrorHandler")
    public void processMessage(Metric metric, Acknowledgment acknowledgment) {
        for (MetricTable metricTable : MetricTable.values()) {
            metricRepository.insert(metric, metricTable);
        }
        acknowledgment.acknowledge();
    }

}
