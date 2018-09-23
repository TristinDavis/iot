package me.vsadokhin.iot.data;

import java.util.List;

import me.vsadokhin.iot.data.domain.Sensor;
import me.vsadokhin.iot.data.domain.SensorId;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends CassandraRepository<Sensor, SensorId> {

    @Query(value = "SELECT MAX(value) FROM Sensor WHERE name IN :names AND created >= :createdStart AND created < :createdEnd")
    double getMaxByNamesAndCreatedRange(@Param("names") List<String> sensorNames,
                                        @Param("createdStart") long createdStart,
                                        @Param("createdEnd") long createdEnd);

    @Query(value = "SELECT MIN(value) FROM Sensor WHERE name IN :names AND created >= :createdStart AND created < :createdEnd")
    double getMinByNamesAndCreatedRange(@Param("names") List<String> sensorNames,
                                        @Param("createdStart") long createdStart,
                                        @Param("createdEnd") long createdEnd);

    @Query(value = "SELECT AVG(value) FROM Sensor WHERE name IN :names AND created >= :createdStart AND created < :createdEnd")
    double getAvgByNamesAndCreatedRange(@Param("names") List<String> sensorNames,
                                        @Param("createdStart") long createdStart,
                                        @Param("createdEnd") long createdEnd);
}