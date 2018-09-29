package me.vsadokhin.iot.stream.consumer;

import me.vsadokhin.iot.data.MetricRepository;
import me.vsadokhin.iot.data.MetricTable;
import me.vsadokhin.iot.data.domain.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final MetricRepository metricRepository;

    @Autowired
    public KafkaConsumer(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    @KafkaListener(topics = "metric", groupId = "stream")
    public void processMessage(Metric metric) {
        metricRepository.insert(metric, MetricTable.METRIC_BY_SENSOR);
        metricRepository.insert(metric, MetricTable.METRIC_BY_TYPE);
    }

}
