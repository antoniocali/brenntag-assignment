#!/bin/bash

CLUSTER_ID=$(./bin/kafka-storage.sh random-uuid)
./bin/kafka-storage.sh format -t $CLUSTER_ID -c ./config/kraft/server.properties
# TODO: make overides come from environment variables
./bin/kafka-server-start.sh /usr/local/kafka/config/server.properties
