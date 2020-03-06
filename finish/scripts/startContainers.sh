#!/bin/bash

KAFKA_SERVER=kafka:9092
NETWORK=reactive-app

ORDER_SERVICE_URL="http://order:9081"
SERVINGWINDOW_SERVICE_URL="http://servingwindow:9082"
STATUS_SERVICE_URL="http://status:9085"

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
  -e MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS=$KAFKA_SERVER \
  --network=$NETWORK \
  --name=status \
  --rm \
  status:1.0-SNAPSHOT &

docker run -d \
  -e OrderClient_mp_rest_url=$ORDER_SERVICE_URL \
  -e ServingWindowClient_mp_rest_url=$SERVINGWINDOW_SERVICE_URL \
  -e StatusClient_mp_rest_url=$STATUS_SERVICE_URL \
  -p 9080:9080 \
  --network=$NETWORK \
  --name=openlibertycafe \
  --rm \
  openlibertycafe:1.0-SNAPSHOT &
  
wait