#!/bin/bash

SPARK_VERSION=3.5.1
SPARK_HOME=/opt/spark-${SPARK_VERSION}

SCALA_VERSION=2.12
PACKAGE_NAME=consumer
PACKAGE_VERSION=0.1.0-SNAPSHOT
TARGET_PACKAGE=${PWD}/target/scala-${SCALA_VERSION}/${PACKAGE_NAME}_${SCALA_VERSION}-${PACKAGE_VERSION}.jar

function run() {
    sbt clean package && $SPARK_HOME/bin/spark-submit \
        --packages \
            org.apache.spark:spark-sql-kafka-0-10_${SCALA_VERSION}:${SPARK_VERSION} \
        --deploy-mode client \
        ${TARGET_PACKAGE}
}

run

