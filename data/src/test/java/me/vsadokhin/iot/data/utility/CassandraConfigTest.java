package me.vsadokhin.iot.data.utility;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.PlainTextAuthProvider;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CassandraConfig.class)
public class CassandraConfigTest {

    @AfterClass
    public static void afterClass() {
        System.clearProperty("cassandra.contact.points");
        System.clearProperty("cassandra.username");
        System.clearProperty("cassandra.password");
    }

    @Test
    public void getKeyspaceName() {
        // verify
        assertThat(CassandraConfig.getKeyspaceName(), is("iot"));
    }

    @Test
    public void getContactPoints() {
        // setup
        System.clearProperty("cassandra.contact.points");

        // act
        String[] result = CassandraConfig.getContactPoints();

        // verify
        assertThat(result.length, is(1));
        assertThat(result[0], is("localhost"));
    }

    @Test
    public void getContactPoints_cassandraEndpointsAreSpecifiedAsSystemProperty_checkResult() {
        // setup
        String customContactPoints = "customCassandraHost1,customCassandraHost2";
        System.setProperty("cassandra.contact.points", customContactPoints);

        // act
        String[] result = CassandraConfig.getContactPoints();

        // verify
        assertThat(result.length, is(2));
        assertThat(result[0], is("customCassandraHost1"));
        assertThat(result[1], is("customCassandraHost2"));
    }

    @Test
    public void getPort() {
        // verify
        assertThat(CassandraConfig.getPort(), is(9042));
    }

    @Test
    public void getAuthProvider() throws Exception {
        // setup
        System.clearProperty("cassandra.username");
        System.clearProperty("cassandra.password");
        PlainTextAuthProvider mockPlainTextAuthProvider = mock(PlainTextAuthProvider.class);
        whenNew(PlainTextAuthProvider.class).withArguments("cassandra", "cassandra").thenReturn(mockPlainTextAuthProvider);

        // act
        AuthProvider result = CassandraConfig.getAuthProvider();

        // verify
        assertThat(result, is(mockPlainTextAuthProvider));
    }

    @Test
    public void getAuthProvider_cassandraCredentialsSpecifiedAsSystemProperties_checkResult() throws Exception {
        // setup
        System.setProperty("cassandra.username", "custom username");
        System.setProperty("cassandra.password", "custom password");
        PlainTextAuthProvider mockPlainTextAuthProvider = mock(PlainTextAuthProvider.class);
        whenNew(PlainTextAuthProvider.class).withArguments("custom username", "custom password").thenReturn(mockPlainTextAuthProvider);

        // act
        AuthProvider result = CassandraConfig.getAuthProvider();

        // verify
        assertThat(result, is(mockPlainTextAuthProvider));
    }
}