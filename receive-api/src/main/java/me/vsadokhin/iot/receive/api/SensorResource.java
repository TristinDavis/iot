package me.vsadokhin.iot.receive.api;

import me.vsadokhin.iot.data.domain.Sensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SensorResource {

    private final KafkaTemplate<String, Sensor> kafkaTemplate;

    @Autowired
    public SensorResource(KafkaTemplate<String, Sensor> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/sensor")
    ResponseEntity create(@RequestBody Sensor sensor) {
        // TODO Vasiliy validate
        kafkaTemplate.send("sensor", sensor);
   		return new ResponseEntity<>(HttpStatus.CREATED);
   	}
   	
}
