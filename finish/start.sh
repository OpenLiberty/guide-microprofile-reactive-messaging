#!/bin/bash

brew services start zookeeper
brew services start kafka

cd models
mvn clean install

cd ../food
mvn clean package liberty:create liberty:install-feature liberty:deploy liberty:start

cd ../beverage
mvn clean package liberty:create liberty:install-feature liberty:deploy liberty:start

cd ../server
mvn clean package liberty:create liberty:install-feature liberty:deploy liberty:start

cd ../order
mvn clean package liberty:create liberty:install-feature liberty:deploy liberty:start

cd ../restaurant
mvn clean package liberty:create liberty:install-feature liberty:deploy liberty:start
