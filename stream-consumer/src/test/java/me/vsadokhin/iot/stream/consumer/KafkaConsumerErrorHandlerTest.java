package me.vsadokhin.iot.stream.consumer;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;

@RunWith(PowerMockRunner.class)
@PrepareForTest({StreamConsumerApplication.class, LoggerFactory.class })
public class KafkaConsumerErrorHandlerTest {

    private KafkaConsumerErrorHandler handler;
    private static Logger MOCK_LOGGER= mock(Logger.class);

    @BeforeClass
    public static void setUpClass() {
        mockStatic(LoggerFactory.class);
        when(LoggerFactory.getLogger(KafkaConsumerErrorHandler.class)).thenReturn(MOCK_LOGGER);
    }

    @Before
    public void setUp() {
        reset(MOCK_LOGGER);
        mockStatic(StreamConsumerApplication.class);
        handler = new KafkaConsumerErrorHandler();
    }

    @Test
    public void handleError_callLoggerError() {
        // setup
        Message mockMessage = mock(Message.class);
        ListenerExecutionFailedException mockListenerExecutionFailedException = mock(ListenerExecutionFailedException.class);

        // act
        handler.handleError(mockMessage, mockListenerExecutionFailedException);

        // verify
        verify(MOCK_LOGGER).error("Exception occurred on metric receiving: " + mockMessage, mockListenerExecutionFailedException);
    }
    
    @Test
    public void handleError_callStreamConsumerApplicationError() {
        // act
        handler.handleError(mock(Message.class), new ListenerExecutionFailedException(null));
        
        // verify
        verifyStatic(StreamConsumerApplication.class);
        StreamConsumerApplication.restart();
    }

    @Test
    public void handleError_checkResultIsNull() {
        // act
        Object result = handler.handleError(mock(Message.class), new ListenerExecutionFailedException(null));

        // verify
        assertThat(result, is(nullValue()));
    }
}