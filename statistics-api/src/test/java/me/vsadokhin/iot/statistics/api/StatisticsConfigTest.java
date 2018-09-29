package me.vsadokhin.iot.statistics.api;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import me.vsadokhin.iot.data.MetricRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StatisticsConfig.class)
public class StatisticsConfigTest {

    private StatisticsConfig statisticsConfig;

    @Before
    public void setUp() {
        statisticsConfig = new StatisticsConfig();
    }

    @Test
    public void getMetricRepository() throws Exception {
        // setup
        MetricRepository mockMetricRepository = mock(MetricRepository.class);
        whenNew(MetricRepository.class).withNoArguments().thenReturn(mockMetricRepository);

        // act
        MetricRepository result = statisticsConfig.metricRepository();

        // verify
        assertThat(result, is(mockMetricRepository));
    }
    
}