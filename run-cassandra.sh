#!/bin/bash
docker rm -f cassandra-iot
startDate=`date +%s`
echo 'cassandra iot start'
docker run --rm --name cassandra-iot -d -p 7000:7000 cassandra:3.11.3
while true; do
    sleep 1
    status=`docker exec cassandra-iot cqlsh 2>&1`
    if [ -z  "$status" ]; then
        echo 'cassandra iot is started'
        break
    fi
    time=`date +%s`
    if [ $(($time-$startDate)) -gt 60 ]; then
      echo 'cassandra iot is not started in 60s'
      exit 1
    fi
    echo '.'
done

echo 'cassandra iot init start'
docker cp ./init-cassandra cassandra-iot:/tmp
docker exec cassandra-iot cqlsh --file=/tmp/init-cassandra
status=`docker exec cassandra-iot cqlsh -e "DESCRIBE TABLE iot.sensor"`
echo ${status}
if [[ ${status} == *"CREATE TABLE iot.sensor"* ]]; then
    echo 'cassandra iot init is done'
else
    echo 'cassandra iot init failed'
fi