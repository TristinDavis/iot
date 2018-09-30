package me.vsadokhin.iot.stream.consumer;

import javax.annotation.PreDestroy;

import me.vsadokhin.iot.data.utility.CassandraClusterUtility;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = { "me.vsadokhin.iot.data", "me.vsadokhin.iot.stream.consumer" })
public class StreamConsumerApplication {

    private static String[] args;
    private static ConfigurableApplicationContext context;

    public static void main(String... args) {
        StreamConsumerApplication.args = args;
        context = SpringApplication.run(StreamConsumerApplication.class, args);
    }

    public static void restart() {
        context.close();
        context = SpringApplication.run(StreamConsumerApplication.class, args);
    }

    @PreDestroy
    public void destroy() {
        CassandraClusterUtility.closeCluster();
    }

    static void setArgs(String[] args) {
        StreamConsumerApplication.args = args;
    }

    static String[] getArgs() {
        return args;
    }

    static void setContext(ConfigurableApplicationContext context) {
        StreamConsumerApplication.context = context;
    }

    static ConfigurableApplicationContext getContext() {
        return context;
    }
}
