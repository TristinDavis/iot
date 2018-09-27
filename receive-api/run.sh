#!/bin/bash
../gradlew clean bootJar
docker rm -f iot-receive-api
docker run --rm -d --name iot-receive-api \
-p 8080:8080 \
--link iot-kafka:iot-kafka \
-v `readlink -f build/libs/receive-api-0.0.1-SNAPSHOT.jar`:/tmp/receive-api.jar \
anapsix/alpine-java:8 java -Dkafka.endpoints=iot-kafka:9092 -jar /tmp/receive-api.jar