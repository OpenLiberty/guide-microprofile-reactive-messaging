#!/bin/bash

brew services start zookeeper
brew services start kafka

cd models
mvn clean install

cd ../food
mvn clean install liberty:start

cd ../beverage
mvn clean install liberty:start

cd ../server
mvn clean install liberty:start

cd ../order
mvn clean install liberty:start

cd ../restaurant
mvn clean install liberty:start
