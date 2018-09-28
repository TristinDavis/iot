package me.vsadokhin.iot.data.utility;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CassandraSessionUtility.class, CassandraClusterUtility.class, CassandraConfig.class})
public class CassandraSessionUtilityTest {

    @Test
    public void getSession_twice_callCreateSessionOnce() {
        // setup
        CassandraSessionUtility.setSession(null);
        mockStatic(CassandraSessionUtility.class);
        when(CassandraSessionUtility.getSession()).thenCallRealMethod();
        when(CassandraSessionUtility.getSession()).thenReturn(mock(Session.class));

        // act
        CassandraSessionUtility.getSession();
        CassandraSessionUtility.getSession();

        // verify
        verifyStatic(CassandraSessionUtility.class, times(1));
        CassandraSessionUtility.createSession();
    }

    @Test
    public void getSession_twice_checkResult() {
        // setup
        CassandraSessionUtility.setSession(null);
        mockStatic(CassandraSessionUtility.class);
        when(CassandraSessionUtility.getSession()).thenCallRealMethod();
        Session mockSession = mock(Session.class);
        when(CassandraSessionUtility.getSession()).thenReturn(mockSession, mock(Session.class));
        
        // act
        Session result1 = CassandraSessionUtility.getSession();
        Session result2 = CassandraSessionUtility.getSession();

        // verify
        assertThat(result1, is(mockSession));
        assertThat(result2, is(mockSession));
    }
    
    @Test
    public void createSession_callClusterConnect() {
        // setup
        Cluster mockCluster = mock(Cluster.class);
        String keyspaceName = "some keyspace name";
        
        mockStatic(CassandraSessionUtility.class, CassandraClusterUtility.class, CassandraConfig.class);
        when(CassandraConfig.getKeyspaceName()).thenReturn(keyspaceName);
        when(CassandraSessionUtility.createSession()).thenCallRealMethod();
        when(CassandraClusterUtility.getCluster()).thenReturn(mockCluster);
        
        // act
        CassandraSessionUtility.createSession();

        // verify
        verify(mockCluster).connect(keyspaceName);
    }

    @Test
    public void createSession_checkResult() {
        // setup
        mockStatic(CassandraSessionUtility.class, CassandraClusterUtility.class, CassandraConfig.class);
        when(CassandraSessionUtility.createSession()).thenCallRealMethod();

        Cluster mockCluster = mock(Cluster.class);
        when(CassandraClusterUtility.getCluster()).thenReturn(mockCluster);

        Session mockSession = mock(Session.class);
        when(mockCluster.connect(anyString())).thenReturn(mockSession);

        // act
        Session result = CassandraSessionUtility.createSession();

        // verify
        assertThat(result, is(mockSession));
    }
}