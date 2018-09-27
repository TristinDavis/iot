package me.vsadokhin.iot.stream.producer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import me.vsadokhin.iot.data.domain.Sensor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@RunWith(PowerMockRunner.class)
@PrepareForTest({KafkaProducer.class, LoggerFactory.class })
public class KafkaProducerTest {

    private KafkaProducer kafkaProducer;

    private KafkaTemplate mockKafkaTemplate;

    private static final Logger MOCK_LOGGER = mock(Logger.class);

    @BeforeClass
    public static void setUpClass() {
        mockStatic(LoggerFactory.class);
        when(LoggerFactory.getLogger(KafkaProducer.class)).thenReturn(MOCK_LOGGER);
    }

    @Before
    public void setUp() {
        reset(MOCK_LOGGER);
        kafkaProducer = new KafkaProducer();
        mockKafkaTemplate = mock(KafkaTemplate.class);
        Whitebox.setInternalState(kafkaProducer, "kafkaTemplate", mockKafkaTemplate);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void sendAsync_callKafkaTemplateSend() {
        // setup
        String topic = "some topic";
        Sensor mockSensor = mock(Sensor.class);
        when(mockKafkaTemplate.send(anyString(), anyObject())).thenReturn(mock(ListenableFuture.class));

        // act
        kafkaProducer.sendAsync(topic, mockSensor);
        
        // verify
        verify(mockKafkaTemplate).send(topic, mockSensor);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void sendAsync_callListenableFutureAddCallback() {
        // setup
        ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
        when(mockKafkaTemplate.send(anyString(), anyObject())).thenReturn(mockListenableFuture);

        // act
        kafkaProducer.sendAsync("topic", new Sensor());

        // verify
        verify(mockListenableFuture).addCallback(any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void sendAsync_checkListenableFutureCallback_onSuccess_callLoggerDebug() {
        // setup
        ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
        when(mockKafkaTemplate.send(anyString(), anyObject())).thenReturn(mockListenableFuture);
        ArgumentCaptor<ListenableFutureCallback> captor = ArgumentCaptor.forClass(ListenableFutureCallback.class);
        doNothing().when(mockListenableFuture).addCallback(captor.capture());
        kafkaProducer.sendAsync("topic", new Sensor());
        SendResult mockSendResult = mock(SendResult.class);

        // act
        captor.getValue().onSuccess(mockSendResult);

        // verify
        verify(MOCK_LOGGER).debug("Sensor data is sent: " + mockSendResult);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void sendAsync_checkListenableFutureCallback_onFailure_callLoggerError() {
        // setup
        ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
        when(mockKafkaTemplate.send(anyString(), anyObject())).thenReturn(mockListenableFuture);
        ArgumentCaptor<ListenableFutureCallback> captor = ArgumentCaptor.forClass(ListenableFutureCallback.class);
        doNothing().when(mockListenableFuture).addCallback(captor.capture());
        Sensor mockSensor = mock(Sensor.class);
        kafkaProducer.sendAsync("topic", mockSensor);
        Throwable mockThrowable = mock(Throwable.class);

        // act
        captor.getValue().onFailure(mockThrowable);

        // verify
        verify(MOCK_LOGGER).error("Failed to sendAsync message: " + mockSensor, mockThrowable);
    }

 }