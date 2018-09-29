package me.vsadokhin.iot.data.utility;


import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.PlainTextAuthProvider;

final class CassandraConfig {

    private CassandraConfig() {
    }

    static String getKeyspaceName() {
        return "iot";
    }

    static String[] getContactPoints() {
        return System.getProperty("cassandra.contact.points","localhost").split(",");
    }

    static int getPort() {
        return 9042;
    }

    static AuthProvider getAuthProvider() {
        return new PlainTextAuthProvider(
                System.getProperty("cassandra.username","cassandra"),
                System.getProperty("cassandra.password","cassandra")
        );
    }
}