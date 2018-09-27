package me.vsadokhin.iot.receive.api;

import me.vsadokhin.iot.data.domain.Sensor;
import me.vsadokhin.iot.stream.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SensorResource {

    private final KafkaProducer kafkaProducer;

    @Autowired
    public SensorResource(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/sensor")
    ResponseEntity create(@RequestBody Sensor sensor) {
        // TODO Vasiliy validate
        kafkaProducer.sendAsync("sensor", sensor);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
