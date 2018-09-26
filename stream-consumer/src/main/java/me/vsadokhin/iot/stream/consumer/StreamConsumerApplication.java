package me.vsadokhin.iot.stream.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"me.vsadokhin.iot.data","me.vsadokhin.iot.stream.consumer"})
public class StreamConsumerApplication {

    public static void main(String... args) {
   		SpringApplication.run(StreamConsumerApplication.class, args);
   	}
   	
}
