#!/bin/bash
./run-kafka.sh
(cd cassandra && ./run.sh)
(cd receive-api && ./run.sh)
(cd stream-consumer && ./run.sh)
(cd statistics-api && ./run.sh)