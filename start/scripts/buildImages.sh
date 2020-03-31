#!/bin/bash

echo Building images

docker build -t system:1.0-SNAPSHOT system/. &
docker build -t job:1.0-SNAPSHOT job/. &
docker build -t inventory:1.0-SNAPSHOT inventory/. &
docker build -t gateway:1.0-SNAPSHOT gateway/. &

wait
echo Images building completed
