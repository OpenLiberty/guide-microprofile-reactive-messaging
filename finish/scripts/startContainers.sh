#!/bin/bash

KAFKA_SERVER=kafka:9092
NETWORK=reactive-app

ORDER_SERVICE_PORT=9081
SERVINGWINDOW_SERVICE_PORT=9082

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
  --name=kitchen \
  --rm \
  kitchen:1.0-SNAPSHOT &

docker run -d \
  -e MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS=$KAFKA_SERVER \
  --network=$NETWORK \
  --name=bar \
  --rm \
  bar:1.0-SNAPSHOT &

docker run -d \
  -e MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS=$KAFKA_SERVER \
  --network=$NETWORK \
  --name=servingwindow \
  --rm \
  servingwindow:1.0-SNAPSHOT &

docker run -d \
  -e MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS=$KAFKA_SERVER \
  --network=$NETWORK \
  --name=order \
  --rm \
  order:1.0-SNAPSHOT &

docker run -d \
  -e ORDER_SERVICE_HOSTNAME="order" \
  -e ORDER_SERVICE_PORT=$ORDER_SERVICE_PORT \
  -e SERVINGWINDOW_SERVICE_HOSTNAME="servingwindow" \
  -e SERVINGWINDOW_SERVICE_PORT=$SERVINGWINDOW_SERVICE_PORT \
  -p 9080:9080 \
  --network=$NETWORK \
  --name=restaurantbff \
  --rm \
  restaurantbff:1.0-SNAPSHOT &
  
wait