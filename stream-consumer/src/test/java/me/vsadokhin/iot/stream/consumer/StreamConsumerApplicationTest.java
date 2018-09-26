package me.vsadokhin.iot.stream.consumer;

import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.SpringApplication;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SpringApplication.class)
public class StreamConsumerApplicationTest {
    @Test
    public void testMain_callSpringApplicationRun() {
        // setup
        mockStatic(SpringApplication.class);

        // act
        StreamConsumerApplication.main("arg", "1", "2");

        // verify
        verifyStatic(SpringApplication.class);
        SpringApplication.run(StreamConsumerApplication.class, "arg", "1", "2");
    }
}