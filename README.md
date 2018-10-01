# iot

## Conceptual model 
Load Balancer 1 <-> Receive Cluster -> Broker/streaming -> Consumers Cluster -> Storage

Load Balancer 2 <-> Reading Cluster <-> Storage

## Implementation
*receive-api*, *statistics-api*, *stream-consumer* are java applications. First two are web applications to create metric and get statistics. Being packed in docker they can be scaled, for instance, in AWS ECS on production.

*data* module encapsulates read/write feature with storage. It is used by *stream-consumer* to write and *statistics-api* to query statistics.

Broker is *Kafka*, Storage is *Cassandra*. I have chosen both for scalability, high availability, performance, fault-tolerance. Both can be run and scaled, for example, in AWS ECS.  

Note that nothing above is carved in stone. For example, receiving/reading can be switched to AWS API Gateway, broker/streaming can be AWS Kinesis, reading and stream consumer might be AWS Lambdas based on my *data* module etc. 

The implementation does not include a load balancer setup/pick. This can be done with a cloud-based approach, like AWS Application/Elastic Load Balancer or Google Cloud Load Balancer. Alternatively, it can be a handmade approach with Nginx, HAProxy etc. 

## How to run
Requirements: Java 8, Docker, Bash, open ports 8080 and 8081.

Execute from the root folder to build modules and start everything in docker:
```bash
./run.sh
```
 
## How to access the service 
### Create metric

Perform a POST request to localhost:8080/metric with a JSON body like
```json 
{"sensorId":"sensor123", "type":"thermostat", "when":1538139260752, "value":1.1}
```
**sensorId** is a string value  
**type** is a string value  
**when** is a long value for milliseconds  
**value** has float type  
All fields are required. Content type has to be application/json. 

The request can be made with curl:
```bash
curl -XPOST -d '{"sensorId":"s2", "type":"t1", "when":"1538139260752", "value":1.1}' -H "Content-Type: application/json" localhost:8080/metric
```

### Get statistics

Perform а GET request to localhost:8081/statistics with **getStatisticsUser** as username and **statistics123** as password and query parameters:
 
**aggregator** is required, supported values: min, max, avg  
**type** is optional, specify to aggregate by type    
**sensorId** is optional, specify to aggregate by sensorId  
**from** is required, milliseconds for timeframe start, must be lower than **to**  
**to** is required, milliseconds for timeframe stop, must be greater than **from**    

Either **type** or **sensorId** must be specified with a single value, multiple types/sensorIds won't be processed by the API.
 
The request can be made with curl:
```bash 
curl -u getStatisticsUser:statistics123 localhost:8081/statistics?aggregator=min\&type=t1\&from=1538139260752\&to=1538139260753
curl -u getStatisticsUser:statistics123 localhost:8081/statistics?aggregator=min\&sensorId=mySensor\&from=123\&to=456
```

### Simulate at least 3 IoT devices sending data every second
Note that the author introduces the term *metric* considering that 1 IoT device might send multiple metrics. This test simulates incoming metrics, running simultaneously. 
Run from the root folder:
```bash
./gradlew -i :qa:load:test -Pqa-tests -Dsimultaneous.metrics=3 -Dduration=60
```
**simultaneous.metrics** is the number of metrics has to be sent, 3 is default
**duration** determines the period to send  the data in seconds, 60 is default

The test will fail if count of metrics doesn't match with **simultaneous.metrics** * **duration** at the end.
The reading API is not checked in this test, feel free to query manually.     

## Limitations
### Receiving format
Currently JSON, but it's subject to change, depending on real production cases.  E.g., the author believes that different IoT devices may send metrics in different formats. Also, I have a feeling that some devices might send measurements in bulk. **receive-api** is good enough to be enhanced and meet both cases on demand.

### Float value
Current metric is hardcoded with the float type, which might not be sufficient in some cases. I also think IoT devices might send not only single value but also more complex data, e.g coordinates like latitude/longitude. It might even happen that value is not a number at all. Both Cassandra and my implementation are fine with tuning type or even supporting multiple types if necessary.
 
### Readings
Only min, max, avg are implemented. Median or other percentile statistics can be implemented with [custom aggregate functions](https://stackoverflow.com/questions/52528838/how-to-get-x-percentile-in-cassandra).
    
Readings are only provided either by one *type* or one *sensorId*. Also, *To* and *From* has belongs to the same day for reading by *type* or to the same week for reading by *sensorId*. Those limitations are subject to change depending on production cases.

*Milliseconds* can be fine for robots but it is still not human readable and not convenient format. I would change it to be a formatted date string like *yyyy-MM-dd HH:mm:ss.SSSZ* or even to support multiple formats.
   
*statistics-api* / *data* modules are flexible enough to be enhanced to support more readings and multiple readings at time.  

### Scalability, high availability, performance, fault-tolerance
These will depend on a particular infrastructure implementation, e.g. clusters' setup, nodes amount, auto scaling, cross datacenter replication etc.

### Secure Web Service
The implementation contains only three things, regarding to the topic:
  
a) *statistics-api* requires basic authorization. Even so there is only one in-memory user, which can be switched to real DB storage.
  
b) I have created two roles in Cassandra, called *iot_write_role* and *iot_statistics_role* for writing (*stream-consumer*) and selecting (*statistics-api*) respectively.

c) Because of using datastax cassadra driver library and its query builder, I wasn't able to perform an CQL injection via my reading API. So the code can be considered CQL injection free.
        
If in-transfer security is important it can be achieved with SSL certificates and proper configuration for Kafka, Cassandra and *statistics-api* module.

It seems to me that Cassandra does not provide in-rest encryption out of the box, but [people say it can be done one way or another](https://stackoverflow.com/questions/47046285/encrypting-the-database-at-rest-without-paying). 
  
Also, going with AWS-based solution, there might be IAM Roles properly configured (not) to provide an access to different model components.
