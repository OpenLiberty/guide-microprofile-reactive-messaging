#!/bin/bash

cd ../kitchen
docker build -t kitchen:1.0-SNAPSHOT .

cd ../bar
docker build -t bar:1.0-SNAPSHOT .

cd ../servingWindow
docker build -t servingwindow:1.0-SNAPSHOT .

cd ../order
docker build -t order:1.0-SNAPSHOT .

cd ../restaurantBFF
docker build -t restaurantbff:1.0-SNAPSHOT .