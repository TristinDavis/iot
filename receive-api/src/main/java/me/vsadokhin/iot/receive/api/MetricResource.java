package me.vsadokhin.iot.receive.api;

import javax.validation.Valid;

import me.vsadokhin.iot.data.domain.Metric;
import me.vsadokhin.iot.stream.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MetricResource {

    private final KafkaProducer kafkaProducer;

    @Autowired
    public MetricResource(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @PostMapping("/metric")
    ResponseEntity create(@Valid @RequestBody Metric metric) {
        kafkaProducer.sendAsync("metric", metric);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
