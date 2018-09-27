package me.vsadokhin.iot.stream.producer;

import me.vsadokhin.iot.data.domain.Sensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

public class KafkaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    @Autowired
    private KafkaTemplate<String, Sensor> kafkaTemplate;

    @Async
    public void sendAsync(String topic, Sensor sensor) {
        ListenableFuture<SendResult<String, Sensor>> future = kafkaTemplate.send(topic, sensor);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Sensor>>() {

            @Override
            public void onSuccess(SendResult<String, Sensor> result) {
                LOGGER.debug("Sensor data is sent: " + result.toString());
            }

            @Override
            public void onFailure(final Throwable throwable) {
                LOGGER.error("Failed to sendAsync message: " + sensor, throwable);
            }

        });
    }
}
