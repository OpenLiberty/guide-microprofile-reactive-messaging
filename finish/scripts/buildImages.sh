#!/bin/bash

cd ..
echo Building images
docker build -t kitchen:1.0-SNAPSHOT kitchen/. &
docker build -t bar:1.0-SNAPSHOT bar/. &
docker build -t servingwindow:1.0-SNAPSHOT servingWindow/. &
docker build -t order:1.0-SNAPSHOT order/. &
docker build -t restaurantbff:1.0-SNAPSHOT restaurantBFF/.
wait
echo Image building complete