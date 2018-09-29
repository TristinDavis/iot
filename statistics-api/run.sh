#!/bin/bash
../gradlew clean bootJar
docker rm -f iot-statistics-api 2>/dev/null
docker run --rm -d --name iot-statistics-api \
-p 8081:8080 \
--link iot-cassandra:iot-cassandra \
-v `pwd`/build/libs/statistics-api-0.0.1-SNAPSHOT.jar:/tmp/statistics-api.jar \
anapsix/alpine-java:8 java -Dcassandra.contact.points=iot-cassandra -jar /tmp/statistics-api.jar