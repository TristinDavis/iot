package me.vsadokhin.iot.receive.api;

import me.vsadokhin.iot.data.domain.Sensor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SensorResource {

    @PostMapping("/sensor")
    ResponseEntity create(@RequestBody Sensor sensor) {
        // TODO Vasiliy validate
        // TODO Vasiliy call cassandra repository to create sensor OR call producer to send sensor as message
   		return new ResponseEntity<>(HttpStatus.CREATED);
   	}
   	
}
