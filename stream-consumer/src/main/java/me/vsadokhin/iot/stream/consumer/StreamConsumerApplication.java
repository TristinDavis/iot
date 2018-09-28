package me.vsadokhin.iot.stream.consumer;

import javax.annotation.PreDestroy;

import me.vsadokhin.iot.data.utility.CassandraClusterUtility;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "me.vsadokhin.iot.data", "me.vsadokhin.iot.stream.consumer" })
public class StreamConsumerApplication {

    public static void main(String... args) {
        SpringApplication.run(StreamConsumerApplication.class, args);
    }

    @PreDestroy
    public void destroy() {
        CassandraClusterUtility.closeCluster();
    }

}
