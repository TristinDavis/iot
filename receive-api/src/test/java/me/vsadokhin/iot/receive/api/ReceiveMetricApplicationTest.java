package me.vsadokhin.iot.receive.api;


import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.SpringApplication;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SpringApplication.class)
public class ReceiveMetricApplicationTest {

    @Test
    public void testMain_callSpringApplicationRun() {
        // setup
        mockStatic(SpringApplication.class);
        
        // act
        ReceiveSensorApplication.main("arg", "1", "2");

        // verify
        verifyStatic(SpringApplication.class);
        SpringApplication.run(ReceiveSensorApplication.class, "arg", "1", "2");
    }

}