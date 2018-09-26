#!/bin/bash
docker run --rm -d --name iot-zookeeper zookeeper:3.4
docker run --rm -d --name iot-kafka -p 9092:9092 \
--link iot-zookeeper:iot-zookeeper \
--env ZOOKEEPER_IP=iot-zookeeper \
ches/kafka