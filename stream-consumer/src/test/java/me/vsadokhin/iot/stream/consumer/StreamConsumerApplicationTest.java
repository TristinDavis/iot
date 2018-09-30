package me.vsadokhin.iot.stream.consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import me.vsadokhin.iot.data.utility.CassandraClusterUtility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SpringApplication.class, CassandraClusterUtility.class })
public class StreamConsumerApplicationTest {

    @Before
    public void setUp() {
        mockStatic(SpringApplication.class);
    }

    @Test
    public void main_callSpringApplicationRun() {
        // act
        StreamConsumerApplication.main("arg", "1", "2");

        // verify
        verifyStatic(SpringApplication.class);
        SpringApplication.run(StreamConsumerApplication.class, "arg", "1", "2");
    }

    @Test
    public void main_checkArgsProperty() {
        // act
        StreamConsumerApplication.main("args", "1", "2");

        // verify
        assertThat(StreamConsumerApplication.getArgs(), is(new String[]{ "args", "1", "2" }));
    }

    @Test
    public void main_checkContextProperty() {
        // setup
        ConfigurableApplicationContext mockConfigurableApplicationContext = mock(ConfigurableApplicationContext.class);
        when(SpringApplication.run(StreamConsumerApplication.class, "args")).thenReturn(mockConfigurableApplicationContext);

        // act
        StreamConsumerApplication.main("args");

        // verify
        assertThat(StreamConsumerApplication.getContext(), is(mockConfigurableApplicationContext));
    }

    @Test
    public void restart_callContextClose() {
        // setup
        ConfigurableApplicationContext mockConfigurableApplicationContext = mock(ConfigurableApplicationContext.class);
        StreamConsumerApplication.setContext(mockConfigurableApplicationContext);

        // act
        StreamConsumerApplication.restart();

        // verify
        verify(mockConfigurableApplicationContext).close();
    }

    @Test
    public void restart_callSpringApplicationRun() {
        // setup
        StreamConsumerApplication.setContext(mock(ConfigurableApplicationContext.class));
        StreamConsumerApplication.setArgs(new String[]{ "1", "2", "3" });

        // act
        StreamConsumerApplication.restart();

        // verify
        verifyStatic(SpringApplication.class);
        SpringApplication.run(StreamConsumerApplication.class, "1", "2", "3");
    }

    @Test
    public void restart_checkContextProperty() {
        // setup
        StreamConsumerApplication.setArgs(new String[]{ "1", "2", "3" });
        StreamConsumerApplication.setContext(mock(ConfigurableApplicationContext.class));
        ConfigurableApplicationContext mockConfigurableApplicationContext = mock(ConfigurableApplicationContext.class);
        when(SpringApplication.run(StreamConsumerApplication.class, "1", "2", "3")).thenReturn(mockConfigurableApplicationContext);

        // act
        StreamConsumerApplication.restart();

        // verify
        assertThat(StreamConsumerApplication.getContext(), is(mockConfigurableApplicationContext));
    }

    @Test
    public void destroy_callCassandraClusterUtilityCloseCluster() {
        // setup
        mockStatic(CassandraClusterUtility.class);

        // act
        new StreamConsumerApplication().destroy();

        // verify
        verifyStatic(CassandraClusterUtility.class);
        CassandraClusterUtility.closeCluster();
    }
}