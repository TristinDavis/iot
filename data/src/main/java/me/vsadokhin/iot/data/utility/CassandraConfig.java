package me.vsadokhin.iot.data.utility;

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
}