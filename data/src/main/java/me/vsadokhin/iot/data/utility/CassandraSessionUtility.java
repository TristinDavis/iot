package me.vsadokhin.iot.data.utility;

import com.datastax.driver.core.Session;

public final class CassandraSessionUtility {

    private static Session session;

    private CassandraSessionUtility(){}

    public static Session getSession() {
        if (session == null) {
            session = createSession();
        }
        return session;
    }

    static Session createSession() {
        return CassandraClusterUtility.getCluster().connect(CassandraConfig.getKeyspaceName());
    }

    static void setSession(Session session) {
        CassandraSessionUtility.session = session;
    }
}
