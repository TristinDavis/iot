package me.vsadokhin.iot.receive.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;

@SpringBootApplication(exclude = CassandraDataAutoConfiguration.class,
        scanBasePackages = { "me.vsadokhin.iot.receive.api", "me.vsadokhin.iot.stream.producer" })
public class ReceiveMetricApplication {

    public static void main(String... args) {
        SpringApplication.run(ReceiveMetricApplication.class, args);
    }

}
