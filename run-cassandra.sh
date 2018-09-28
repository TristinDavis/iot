#!/bin/bash
docker rm -f iot-cassandra 2>/dev/null
startDate=`date +%s`
echo 'cassandra iot start'
docker run --rm -d --name iot-cassandra -p 9042:9042 cassandra:3.11.3
while true; do
    sleep 1
    status=`docker exec iot-cassandra cqlsh 2>&1`
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
docker cp ./init-cassandra iot-cassandra:/tmp
docker exec iot-cassandra cqlsh --file=/tmp/init-cassandra
status=`docker exec iot-cassandra cqlsh -e "DESCRIBE TABLE iot.metric_by_sensor_type"`
if [[ ${status} == *"CREATE TABLE iot.metric_by_sensor_type"* ]]; then
    echo 'cassandra iot init is done'
else
    echo 'cassandra iot init failed'
fi