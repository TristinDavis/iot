package me.vsadokhin.iot.statistics.api;

import me.vsadokhin.iot.data.MetricRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StatisticsConfig {

    @Bean
    public MetricRepository metricRepository() {
        return new MetricRepository();
    }

}
