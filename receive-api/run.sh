#!/bin/bash
../gradlew clean bootJar
docker rm -f iot-receive-api
docker run --rm -d --name iot-receive-api \
-p 8080:8080 \
--link iot-cassandra:iot-cassandra \
-v `readlink -f build/libs/receive-api-0.0.1-SNAPSHOT.jar`:/tmp/receive-api.jar \
anapsix/alpine-java:8 java -jar /tmp/receive-api.jar -Dcassandra.contact.points=iot-cassandra