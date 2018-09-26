package me.vsadokhin.iot.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.cassandra.config.SchemaAction;

public class CassandraConfigTest {

    private CassandraConfig config;

    @Before
    public void setUp() {
        config = new CassandraConfig();
    }

    @Test
    public void getKeyspaceName() {
        assertThat(config.getKeyspaceName(), is("iot"));
    }

    @Test
    public void getContactPoints() {
        // setup
        System.clearProperty("cassandra.contact.points");

        // verify
        assertThat(config.getContactPoints(), is("localhost"));
    }

    @Test
    public void getContactPoints_() {
        // setup
        String customContactPoints = "customCassandraHost1,customCassandraHost2";
        System.setProperty("cassandra.contact.points", customContactPoints);
        
        // verify
        assertThat(config.getContactPoints(), is(customContactPoints));
    }

    @Test
    public void getPort() {
        assertThat(config.getPort(), is(9042));
    }

    @Test
    public void getSchemaAction() {
        assertThat(config.getSchemaAction(), is(SchemaAction.CREATE_IF_NOT_EXISTS));
    }

    @Test
    public void getKeyspaceCreations_checkSize() {
        assertThat(config.getKeyspaceCreations().size(), is(1));
    }

    @Test
    public void getKeyspaceCreations_checkName() {
        assertThat(config.getKeyspaceCreations().get(0).getName(), is("iot"));
    }

    @Test
    public void getKeyspaceCreations_checkIfNotExists() {
        assertThat(config.getKeyspaceCreations().get(0).getIfNotExists(), is(true));
    }
}