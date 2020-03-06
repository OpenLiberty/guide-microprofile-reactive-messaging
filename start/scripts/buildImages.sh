#!/bin/bash

echo Building images

docker build -t kitchen:1.0-SNAPSHOT kitchen/. &
docker build -t bar:1.0-SNAPSHOT bar/. &
docker build -t servingwindow:1.0-SNAPSHOT servingWindow/. &
docker build -t order:1.0-SNAPSHOT order/. &
docker build -t status:1.0-SNAPSHOT status/. &
docker build -t openlibertycafe:1.0-SNAPSHOT openLibertyCafe/. &

wait
echo Images building completed
