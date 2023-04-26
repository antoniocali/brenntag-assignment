#!/bin/bash

export SPARK_PROVIDED=true
sbt assembly

cp ./target/scala-2.12/spark-jobs-scala-0.1.0-SNAPSHOT.jar ../spark-apps/