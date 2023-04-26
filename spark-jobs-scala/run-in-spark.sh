#!/bin/bash

JOB_CLASS=$1

docker exec -it spark-iceberg spark-submit \
  --class ${JOB_CLASS} \
  --master local[*] \
  /home/iceberg/spark-apps/spark-jobs-scala-0.1.0-SNAPSHOT.jar
