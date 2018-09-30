package me.vsadokhin.iot.stream.consumer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.HashMap;
import java.util.Map;

import me.vsadokhin.iot.data.MetricRepository;
import me.vsadokhin.iot.data.domain.Metric;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@RunWith(PowerMockRunner.class)
@PrepareForTest(KafkaConsumerConfig.class)
public class KafkaConsumerConfigTest {

    private KafkaConsumerConfig kafkaConsumerConfig;

    @Before
    public void setUp() {
        kafkaConsumerConfig = new KafkaConsumerConfig();
    }

    @Test
    public void consumerConfigs_checkResultIsNewMap() throws Exception {
        // setup
        HashMap mockHashMap = mock(HashMap.class);
        whenNew(HashMap.class).withNoArguments().thenReturn(mockHashMap);

        // act
        Map result = kafkaConsumerConfig.consumerConfigs();

        // verify
        assertThat(result, is(mockHashMap));
    }

    @Test
    public void consumerConfigs_checkValueDeserializerClassConfigKey() {
        // act
        Map<String, Object> result = kafkaConsumerConfig.consumerConfigs();

        // verify
        assertThat(result.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG), is((Object) JsonDeserializer.class));
    }

    @Test
    public void consumerConfigs_checkResultKeyDeserializerClassConfigKey() {
        // act
        Map<String, Object> result = kafkaConsumerConfig.consumerConfigs();

        // verify
        assertThat(result.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG), is((Object) StringDeserializer.class));
    }

    @Test
    public void consumerConfigs_checkResultBootstrapServersConfigKey() {
        // setup
        System.clearProperty("kafka.endpoints");

        // act
        Map result = kafkaConsumerConfig.consumerConfigs();

        // verify
        assertThat(result.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG), is("localhost:9092"));
    }

    @Test
    public void consumerConfigs_kafkaEndpointsAreSpecifiedAsSystemProperty_checkResultBootstrapServersConfigKey() {
        // setup
        String customKafkaEndpoints = "host1:123,host2:890";
        System.setProperty("kafka.endpoints", customKafkaEndpoints);

        // act
        Map result = kafkaConsumerConfig.consumerConfigs();

        // verify
        assertThat(result.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG), is(customKafkaEndpoints));
    }

    @Test
    public void consumerConfigs_checkEnableAutoCommitConfigKey() {
        // act
        Map result = kafkaConsumerConfig.consumerConfigs();

        // verify
        assertThat(result.get(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG), is(false));
    }

    @Test
    public void consumerFactory_checkResult() throws Exception {
        // setup
        Map mockMap = mock(Map.class);
        DefaultKafkaConsumerFactory mockDefaultKafkaConsumerFactory = mock(DefaultKafkaConsumerFactory.class);
        StringDeserializer mockStringDeserializer = mock(StringDeserializer.class);
        JsonDeserializer mockJsonDeserializer = mock(JsonDeserializer.class);

        kafkaConsumerConfig = spy(kafkaConsumerConfig);
        doReturn(mockMap).when(kafkaConsumerConfig).consumerConfigs();
        whenNew(StringDeserializer.class).withNoArguments().thenReturn(mockStringDeserializer);
        whenNew(JsonDeserializer.class).withArguments(Metric.class).thenReturn(mockJsonDeserializer);
        whenNew(DefaultKafkaConsumerFactory.class).withArguments(mockMap, mockStringDeserializer, mockJsonDeserializer)
                .thenReturn(mockDefaultKafkaConsumerFactory);

        // act
        ConsumerFactory<String, Metric> result = kafkaConsumerConfig.consumerFactory();

        // verify
        assertThat(result, is(mockDefaultKafkaConsumerFactory));
    }

    @Test
    public void kafkaListenerContainerFactory_checkResult() throws Exception {
        // setup
        ConcurrentKafkaListenerContainerFactory mockConcurrentKafkaListenerContainerFactory = mock(ConcurrentKafkaListenerContainerFactory.class);
        whenNew(ConcurrentKafkaListenerContainerFactory.class).withNoArguments().thenReturn(mockConcurrentKafkaListenerContainerFactory);
        when(mockConcurrentKafkaListenerContainerFactory.getContainerProperties()).thenReturn(new ContainerProperties(""));
        kafkaConsumerConfig = spy(kafkaConsumerConfig);
        doReturn(mock(ConsumerFactory.class)).when(kafkaConsumerConfig).consumerFactory();

        // act
        ConcurrentKafkaListenerContainerFactory<String, Metric> result = kafkaConsumerConfig.kafkaListenerContainerFactory();

        // verify
        assertThat(result, is(mockConcurrentKafkaListenerContainerFactory));
    }

    @Test
    public void kafkaListenerContainerFactory_checkResultConsumerFactory() {
        // setup
        kafkaConsumerConfig = spy(kafkaConsumerConfig);
        ConsumerFactory mockConsumerFactory = mock(ConsumerFactory.class);
        doReturn(mockConsumerFactory).when(kafkaConsumerConfig).consumerFactory();

        // act
        ConcurrentKafkaListenerContainerFactory<String, Metric> result = kafkaConsumerConfig.kafkaListenerContainerFactory();

        // verify
        assertThat(result.getConsumerFactory(), is(mockConsumerFactory));
    }

    @Test
    public void kafkaListenerContainerFactory_checkResultAckMode() {
        // setup
        kafkaConsumerConfig = spy(kafkaConsumerConfig);
        ConsumerFactory mockConsumerFactory = mock(ConsumerFactory.class);
        doReturn(mockConsumerFactory).when(kafkaConsumerConfig).consumerFactory();

        // act
        ConcurrentKafkaListenerContainerFactory<String, Metric> result = kafkaConsumerConfig.kafkaListenerContainerFactory();

        // verify
        assertThat(result.getContainerProperties().getAckMode(), is(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE));
    }

    @Test
    public void metricRepository() throws Exception {
        // setup
        MetricRepository mockMetricRepository = mock(MetricRepository.class);
        whenNew(MetricRepository.class).withNoArguments().thenReturn(mockMetricRepository);

        // act
        MetricRepository result = kafkaConsumerConfig.sensorRepository();

        // verify
        assertThat(result, is(mockMetricRepository));
    }
}