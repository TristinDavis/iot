package me.vsadokhin.iot.data.utility;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import com.datastax.driver.core.Cluster;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CassandraClusterUtility.class, CassandraConfig.class, Cluster.class })
public class CassandraClusterUtilityTest {

    @Test
    public void getCluster_twice_callCreateClusterOnce() {
        // setup
        CassandraClusterUtility.setCluster(null);
        mockStatic(CassandraClusterUtility.class);
        when(CassandraClusterUtility.getCluster()).thenCallRealMethod();
        when(CassandraClusterUtility.getCluster()).thenReturn(mock(Cluster.class));

        // act
        CassandraClusterUtility.getCluster();
        CassandraClusterUtility.getCluster();

        // verify
        verifyStatic(CassandraClusterUtility.class, times(1));
        CassandraClusterUtility.createCluster();
    }

    @Test
    public void getCluster_clusterIsClosed_callCreateClusterOnce() {
        // setup
        Cluster mockCluster = mock(Cluster.class);
        when(mockCluster.isClosed()).thenReturn(true);
        CassandraClusterUtility.setCluster(mockCluster);

        mockStatic(CassandraClusterUtility.class);
        when(CassandraClusterUtility.getCluster()).thenCallRealMethod();
        when(CassandraClusterUtility.getCluster()).thenReturn(mock(Cluster.class));

        // act
        CassandraClusterUtility.getCluster();

        // verify
        verifyStatic(CassandraClusterUtility.class);
        CassandraClusterUtility.createCluster();
    }

    @Test
    public void getCluster_clusterIsClosed_checkResult() {
        // setup
        Cluster mockClosedCluster = mock(Cluster.class);
        when(mockClosedCluster.isClosed()).thenReturn(true);
        CassandraClusterUtility.setCluster(mockClosedCluster);

        mockStatic(CassandraClusterUtility.class);
        when(CassandraClusterUtility.getCluster()).thenCallRealMethod();
        Cluster mockCluster = mock(Cluster.class);
        when(CassandraClusterUtility.getCluster()).thenReturn(mockCluster, mock(Cluster.class));

        // act
        Cluster result = CassandraClusterUtility.getCluster();

        // verify
        assertThat(result, is(mockCluster));
    }

    @Test
    public void getCluster_twice_checkResult() {
        // setup
        CassandraClusterUtility.setCluster(null);
        mockStatic(CassandraClusterUtility.class);
        when(CassandraClusterUtility.getCluster()).thenCallRealMethod();
        Cluster mockCluster = mock(Cluster.class);
        when(CassandraClusterUtility.getCluster()).thenReturn(mockCluster, mock(Cluster.class));

        // act
        Cluster result1 = CassandraClusterUtility.getCluster();
        Cluster result2 = CassandraClusterUtility.getCluster();

        // verify
        assertThat(result1, is(mockCluster));
        assertThat(result2, is(mockCluster));
    }

    @Test
    public void createCluster_callClusterBuilderAddContactPoints() {
        // setup
        Cluster.Builder mockClusterBuilder = prepareToTestCreateCluster();
        String[] cassandraContactPoints = { "hots1", "host2" };
        when(CassandraConfig.getContactPoints()).thenReturn(cassandraContactPoints);

        // act
        CassandraClusterUtility.createCluster();

        // verify
        verify(mockClusterBuilder).addContactPoints(cassandraContactPoints);
    }

    private Cluster.Builder prepareToTestCreateCluster() {
        Cluster.Builder mockClusterBuilder = mock(Cluster.Builder.class);
        mockStatic(Cluster.class, CassandraConfig.class);
        when(Cluster.builder()).thenReturn(mockClusterBuilder);
        return mockClusterBuilder;
    }

    @Test
    public void createCluster_callClusterBuilderWithPort() {
        // setup
        Cluster.Builder mockClusterBuilder = prepareToTestCreateCluster();
        int port = 12345;
        when(CassandraConfig.getPort()).thenReturn(port);

        // act
        CassandraClusterUtility.createCluster();

        // verify
        verify(mockClusterBuilder).withPort(port);
    }

    @Test
    public void createCluster_checkResult() {
        // setup
        Cluster.Builder mockClusterBuilder = prepareToTestCreateCluster();
        Cluster mockCluster = mock(Cluster.class);
        when(mockClusterBuilder.build()).thenReturn(mockCluster);

        // act
        Cluster result = CassandraClusterUtility.createCluster();

        // verify
        assertThat(result, is(mockCluster));
    }

    @Test
    public void closeCluster_callClusterClose() {
        // setup
        Cluster mockCluster = mock(Cluster.class);
        CassandraClusterUtility.setCluster(mockCluster);
        
        // act
        CassandraClusterUtility.closeCluster();

        // verify
        verify(mockCluster).close();
    }

    @Test
    public void closeCluster_clusterIsClosed_doNotCallClusterClose() {
        // setup
        Cluster mockCluster = mock(Cluster.class);
        CassandraClusterUtility.setCluster(mockCluster);
        when(mockCluster.isClosed()).thenReturn(true);
        
        // act
        CassandraClusterUtility.closeCluster();

        // verify
        verify(mockCluster, never()).close();
    }

    @Test
    public void closeCluster_noCluster_noNPE() {
        // setup
        CassandraClusterUtility.setCluster(null);

        // act
        CassandraClusterUtility.closeCluster();

        // verify
        // no NPE
    }

}