package me.vsadokhin.iot.data.utility;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class CassandraConfigTest {

    @Test
    public void getKeyspaceName() {
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
        assertThat(CassandraConfig.getPort(), is(9042));
    }
}