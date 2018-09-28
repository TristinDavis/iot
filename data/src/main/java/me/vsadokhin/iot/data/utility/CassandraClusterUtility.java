package me.vsadokhin.iot.data.utility;

import com.datastax.driver.core.Cluster;

public final class CassandraClusterUtility {

    private static Cluster cluster;

    private CassandraClusterUtility(){}

    static Cluster getCluster() {
        if (cluster == null) {
            cluster = createCluster();
        }
        return cluster;
    }

    static Cluster createCluster() {
        Cluster.Builder builder = Cluster.builder();
        builder.addContactPoints(CassandraConfig.getContactPoints());
        builder.withPort(CassandraConfig.getPort());
        return builder.build();
    }

    public static void closeCluster() {
        if (cluster != null && !cluster.isClosed()) {
            cluster.close();
        }
    }

    static void setCluster(Cluster cluster) {
        CassandraClusterUtility.cluster = cluster;
    }
}
