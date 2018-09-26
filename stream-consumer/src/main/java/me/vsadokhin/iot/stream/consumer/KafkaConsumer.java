package me.vsadokhin.iot.stream.consumer;

import me.vsadokhin.iot.data.SensorRepository;
import me.vsadokhin.iot.data.domain.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private final SensorRepository sensorRepository;

    @Autowired
    public KafkaConsumer(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @KafkaListener(topics = "sensor", groupId = "stream")
    public void processMessage(Sensor sensor) {
        sensorRepository.insert(sensor);
    }

}
