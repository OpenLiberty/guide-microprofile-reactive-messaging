#!/bin/bash

cd food
mvn liberty:stop

cd ../beverage
mvn liberty:stop

cd ../server
mvn liberty:stop

cd ../order
mvn liberty:stop

cd ../restaurant
mvn liberty:stop

brew services stop zookeeper
brew services stop kafka