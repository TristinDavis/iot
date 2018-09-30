# iot
###Conceptual model 
Load Balancer 1 <-> Receive Cluster -> Broker/streaming -> Consumers Cluster -> Storage

Load Balancer 2 <-> Reading Cluster <-> Storage

###Implementation
*receive-api*, *statistics-api*, *stream-consumer* are java applications. First two are web applications to create metric and get statistics. Being packed in docker they can be scaled, for instance, in AWS ECS on production.

*data* module encapsulates read/write feature with storage. It is used by *stream-consumer* to write and *statistics-api* to query statistics.

Broker is *Kafka*, Storage is *Cassandra*. I chose both for scalability, high availability, performance, fault-tolerance. Both can be run and scaled, for example, in AWS ECS.  

Note that nothing above is carved in stone. For example, receiving/reading can be switched to AWS API Gateway, broker/streaming can be AWS Kinesis, reading and stream consumer might be AWS Lambdas based on my *data* module etc. 

The implementation does not include load balancer setup/pick. It can be cloud based approach like AWS Application/Elastic Load Balancer or Google Cloud Load Balancer. Alternatively, it can be a handmade approach with Nginx, HAProxy etc. 

###How to run
Requirements: Java 8, Docker, Bash, open ports 8080 and 8081.

Execute from root folder to build modules and start everything in docker:
```bash
./run.sh
```
 
###How to access the service 
##### 1. Create metric

Perform POST request to localhost:8080/metric with JSON body like
```json 
{"sensorId":"sensor123", "type":"thermostat", "when":1538139260752, "value":1.1}
```
**sensorId** is a string value  
**type** is string value  
**when** is long for milliseconds  
**value**  has float type  
All fields are required. Content type has to be application/json. 

The request can be made with curl:
```bash
curl -XPOST -d '{"sensorId":"s2", "type":"t1", "when":"1538139260752", "value":1.1}' -H "Content-Type: application/json" localhost:8080/metric
```

##### 2. Get statistics

Perform GET request to localhost:8081/statistics with username **getStatisticsUser** and password **statistics123** and query parameters:
 
**aggregator** is required, supported values: min, max, avg  
**type** is optional, specify to aggregate by type    
**sensorId** is optional, specify to aggregate by sensorId  
**from** is required, milliseconds for timeframe start, must be lower than to  
**to** is required, milliseconds for timeframe stop, must be greater than from    

*type* or *sensorId* must be specified, only one of them and only one value, multiple types/sensorIds won't be considered by API.
 
The request can be made with curl:
```bash 
curl -u getStatisticsUser:statistics123 localhost:8081/statistics?aggregator=min\&type=t1\&from=1538139260752\&to=1538139260753
curl -u getStatisticsUser:statistics123 localhost:8081/statistics?aggregator=min\&sensorId=mySensor\&from=123\&to=456
```

##### 3. Simulate at least 3 IoT devices sending data every second
Note that author manipulates *metric* term considering that 1 IoT device might send multiple metrics. This test simulates 3 simultaneously incoming metrics.  
Run from root folder:
```bash
./gradlew :qa:load test -Pqa-tests -Dsimultaneous.metrics=3 -Dduration=60
```
**simultaneous.metrics** is number of metrics has to be sent, 3 is default  
**duration** determines how long to send data in seconds, 60 is default

The test will fail if count of metrics don't match with **simultaneous.metrics** * **duration** at the end.  
The test does not check reading API. Feel free to query manually.         

###Limitations
**Receiving format**   
It is JSON now but it is a subject to change depending on real production cases. For example, author believes that different IoT devices might send metrics in different formats. Also I have a feeling that some devices might send measurements in bulk. **receive-api** is good enough to be enhanced and meet both cases on demand.  

**Float value**  
Current metric is hardcoded with float type. Float might not be sufficient in some cases. I also think that IoT devices might send not only single value but also more complex data, for example, coordinates like latitude/longitude. It might even happen that value is not a number at all. Both Cassandra and my implementation are fine with tuning type or even supporting multiple types if it is required. 
 
**Readings**  
Only min, max, avg are implemented. Median or other percentile statistics can be implemented with [custom aggregate functions](https://stackoverflow.com/questions/52528838/how-to-get-x-percentile-in-cassandra).
    
Readings are only provided either by one *type* or one *sensorId*. Also, getting statistics by type is limited by one week range and selecting by sensorId by one day. Those limitations are subjects to change depending on production cases.

*Milliseconds* can be fine for robots but it is still not human readable and not convenient format. I would change it to be a formatted date string like *yyyy-MM-dd HH:mm:ss.SSSZ* or even to support multiple formats.
   
*statistics-api*/*data* modules are flexible enough to be enhanced to support more readings and multiple readings at time.  

**Scalability, high availability, performance, fault-tolerance**  
They will depend on a particular infrastructure implementation, e.x. clusters' setup, nodes amount, auto scaling, cross datacenter replication etc.

**Secure Web Service**  
The implementation contains only three things regarding the topic:
  
a) *statistics-api* requires basic authorization. Even so there is only one in memory user, it can be switched to real DB storage
  
b) I created two roles in Cassandra called *iot_write_role* and *iot_statistics_role* for writing (*stream-consumer*) and selecting (*statistics-api*) respectively

c) I was not able to perform CQL injection via my reading API probably because of using datastax cassadra driver library and its query builder. So I claim here it is CQL injection free.  
        
If in-transfer security is important it can be achieved with SSL certificates and proper configuration for Kafka, Cassandra and *statistics-api* module.

It seems to me that Cassandra does not provide in-rest encryption out of the box but [people say it can be done one way or another](https://stackoverflow.com/questions/47046285/encrypting-the-database-at-rest-without-paying). 
  
Also, going with AWS based solution there might be IAM Roles properly configured (not) to provide an access to different system parts.