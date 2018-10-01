#!/bin/bash
./clean.sh
docker rmi iot-cassandra zookeeper:3.4 anapsix/alpine-java:8 ches/kafka:latest cassandra:3.11.3