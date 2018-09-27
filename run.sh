#!/bin/bash
./run-kafka.sh
./run-cassandra.sh
(cd receive-api && ./run.sh)
(cd stream-consumer && ./run.sh)