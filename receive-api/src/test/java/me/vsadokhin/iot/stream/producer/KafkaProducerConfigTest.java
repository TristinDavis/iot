package me.vsadokhin.iot.stream.producer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.HashMap;
import java.util.Map;

import me.vsadokhin.iot.data.domain.Metric;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@RunWith(PowerMockRunner.class)
@PrepareForTest(KafkaProducerConfig.class)
public class KafkaProducerConfigTest {

    private KafkaProducerConfig kafkaProducerConfig;

    @Before
    public void setUp() {
        kafkaProducerConfig = new KafkaProducerConfig();
    }

    @Test
    public void producerConfigs_checkResultIsNewMap() throws Exception {
        // setup
        HashMap mockHashMap = mock(HashMap.class);
        whenNew(HashMap.class).withNoArguments().thenReturn(mockHashMap);

        // act
        Map result = kafkaProducerConfig.producerConfigs();

        // verify
        assertThat(result, is(mockHashMap));
    }
    
    @Test
    public void producerConfigs_checkResultAddTypeInfoHeadersKey() {
        // act
        Map<String, Object> result = kafkaProducerConfig.producerConfigs();

        // verify
        assertThat(result.get(JsonSerializer.ADD_TYPE_INFO_HEADERS), is(false));
    }

    @Test
    public void producerConfigs_checkResultKeySerializerClassConfigKey() {
        // act
        Map<String, Object> result = kafkaProducerConfig.producerConfigs();

        // verify
        assertThat(result.get(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG), is((Object)StringSerializer.class));
    }
    
    @Test
    public void producerConfigs_checkValueSerializerClassConfigKey() {
        // act
        Map<String, Object> result = kafkaProducerConfig.producerConfigs();

        // verify
        assertThat(result.get(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG), is((Object)JsonSerializer.class));
    }
    
    @Test
    public void producerConfigs_checkBootstrapServersConfigKey() {
        // setup
        System.clearProperty("kafka.endpoints");
        
        // act
        Map<String, Object> result = kafkaProducerConfig.producerConfigs();

        // verify
        assertThat(result.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG), is("localhost:9092"));
    }

    @Test
    public void producerConfigs_kafkaEndpointsAreSpecifiedAsSystemProperty_checkBootstrapServersConfigKey() {
        // setup
        String customKafkaEndpoints = "kafka-host1:123,kafka-host2:456";
        System.setProperty("kafka.endpoints", customKafkaEndpoints);

        // act
        Map<String, Object> result = kafkaProducerConfig.producerConfigs();

        // verify
        assertThat(result.get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG), is(customKafkaEndpoints));
    }

    @Test
    public void producerFactory() throws Exception {
        // setup
        Map mockMap = mock(Map.class);
        kafkaProducerConfig =spy(kafkaProducerConfig);
        doReturn(mockMap).when(kafkaProducerConfig).producerConfigs();
        DefaultKafkaProducerFactory mockDefaultKafkaProducerFactory = mock(DefaultKafkaProducerFactory.class);
        whenNew(DefaultKafkaProducerFactory.class).withArguments(mockMap).thenReturn(mockDefaultKafkaProducerFactory);

        // act
        ProducerFactory<String, Metric> result = kafkaProducerConfig.producerFactory();

        // verify
        assertThat(result, is(mockDefaultKafkaProducerFactory));
    }

    @Test
    public void kafkaTemplate() throws Exception {
        // setup
        ProducerFactory mockProducerFactory = mock(ProducerFactory.class);
        kafkaProducerConfig =spy(kafkaProducerConfig);
        doReturn(mockProducerFactory).when(kafkaProducerConfig).producerFactory();
        KafkaTemplate mockKafkaTemplate = mock(KafkaTemplate.class);
        whenNew(KafkaTemplate.class).withArguments(mockProducerFactory).thenReturn(mockKafkaTemplate);

        // act
        KafkaTemplate<String, Metric> result = kafkaProducerConfig.kafkaTemplate();
        
        // verify
        assertThat(result, is(mockKafkaTemplate));
    }

    @Test
    public void producer() throws Exception {
        // setup
        KafkaProducer mockKafkaProducer = mock(KafkaProducer.class);
        whenNew(KafkaProducer.class).withNoArguments().thenReturn(mockKafkaProducer);

        // act
        KafkaProducer result = kafkaProducerConfig.producer();

        // verify
        assertThat(result, is(mockKafkaProducer));
    }
}