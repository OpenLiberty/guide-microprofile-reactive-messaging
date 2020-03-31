#!/bin/bash

KAFKA_SERVER=kafka:9092
NETWORK=reactive-app

JOB_SERVICE_URL="http://job:9081"
INVENTORY_SERVICE_URL="http://inventory:9085"

docker network create $NETWORK

docker run -d \
  -e ALLOW_ANONYMOUS_LOGIN=yes \
  --network=$NETWORK \
  --name=zookeeper \
  --rm \
  bitnami/zookeeper:3 &

docker run -d \
  -e KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e ALLOW_PLAINTEXT_LISTENER=yes \
  -e KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 \
  --hostname=kafka \
  --network=$NETWORK \
  --name=kafka \
  --rm \
  bitnami/kafka:2 &

docker run -d \
  -e MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS=$KAFKA_SERVER \
  --network=$NETWORK \
  --name=system \
  --rm \
  system:1.0-SNAPSHOT &

docker run -d \
  -e MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS=$KAFKA_SERVER \
  --network=$NETWORK \
  --name=job \
  --rm \
  job:1.0-SNAPSHOT &
  
docker run -d \
  -e MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS=$KAFKA_SERVER \
  --network=$NETWORK \
  --name=inventory \
  --rm \
  inventory:1.0-SNAPSHOT &

docker run -d \
  -e JobClient_mp_rest_url=$JOB_SERVICE_URL \
  -e InventoryClient_mp_rest_url=$INVENTORY_SERVICE_URL \
  -p 9080:9080 \
  --network=$NETWORK \
  --name=gateway \
  --rm \
  gateway:1.0-SNAPSHOT &
  
wait