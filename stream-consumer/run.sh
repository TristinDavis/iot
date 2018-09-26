#!/bin/bash
../gradlew clean bootJar
docker rm -f iot-stream-consumer
docker run --rm -d --name iot-stream-consumer \
-p 8081:8080 \
--link iot-kafka:iot-kafka \
--link iot-cassandra:iot-cassandra \
-v `readlink -f build/libs/stream-consumer-0.0.1-SNAPSHOT.jar`:/tmp/stream-consumer.jar \
anapsix/alpine-java:8 \
java -jar /tmp/stream-consumer.jar \
-Dcassandra.contact.points=iot-cassandra \
-Dkafka.endpoints=iot-kafka