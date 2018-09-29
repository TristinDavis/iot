package me.vsadokhin.iot.statistics.api;

import me.vsadokhin.iot.data.MetricRepository;
import me.vsadokhin.iot.data.domain.GetStatisticsRequest;
import me.vsadokhin.iot.data.exception.GetStatisticsException;
import me.vsadokhin.iot.data.exception.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsResource {

    private final MetricRepository metricRepository;

    @Autowired
    public StatisticsResource(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    @GetMapping("/statistics")
    ResponseEntity getStatistics(GetStatisticsRequest getStatisticsRequest) {
        try {
            float result = metricRepository.getStatistics(getStatisticsRequest);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (StorageException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (GetStatisticsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
