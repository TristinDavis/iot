#!/bin/bash
../gradlew clean bootJar
docker rm -f iot-stream-consumer 2>/dev/null
docker run --rm -d --name iot-stream-consumer \
--link iot-kafka:iot-kafka \
--link iot-cassandra:iot-cassandra \
-v `pwd`/build/libs/stream-consumer-0.0.1-SNAPSHOT.jar:/tmp/stream-consumer.jar \
anapsix/alpine-java:8 \
java \
-Dcassandra.contact.points=iot-cassandra \
-Dkafka.endpoints=iot-kafka:9092 \
-jar /tmp/stream-consumer.jar
