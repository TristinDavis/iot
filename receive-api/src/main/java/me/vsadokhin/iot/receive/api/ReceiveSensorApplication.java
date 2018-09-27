package me.vsadokhin.iot.receive.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;

@SpringBootApplication(exclude = CassandraDataAutoConfiguration.class)
public class ReceiveSensorApplication {

    public static void main(String... args) {
   		SpringApplication.run(ReceiveSensorApplication.class, args);
   	}
   	
}
