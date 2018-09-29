package me.vsadokhin.iot.statistics.api;


import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.SpringApplication;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SpringApplication.class)
public class StatisticsApplicationTest {

    @Test
    public void testMain_callSpringApplicationRun() {
        // setup
        mockStatic(SpringApplication.class);
        
        // act
        StatisticsApplication.main("arg", "1", "2");

        // verify
        verifyStatic(SpringApplication.class);
        SpringApplication.run(StatisticsApplication.class, "arg", "1", "2");
    }

}