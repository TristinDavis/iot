package me.vsadokhin.iot.stream.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;

public class KafkaConsumerErrorHandler implements KafkaListenerErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerErrorHandler.class);

    @Override
    public Object handleError(Message<?> message, ListenerExecutionFailedException exception) {
        LOGGER.error("Exception occurred on metric receiving: " + message, exception);
        StreamConsumerApplication.restart();
        return null;
    }
}
