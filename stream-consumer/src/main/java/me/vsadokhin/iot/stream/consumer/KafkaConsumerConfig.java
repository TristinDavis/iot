package me.vsadokhin.iot.stream.consumer;

import java.util.HashMap;
import java.util.Map;

import me.vsadokhin.iot.data.SensorRepository;
import me.vsadokhin.iot.data.domain.Sensor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("kafka.endpoints", "localhost:9092"));
        return props;
    }

    @Bean
    public ConsumerFactory<String, Sensor> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(Sensor.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Sensor> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Sensor> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public SensorRepository sensorRepository() {
        return new SensorRepository();
    }
}
