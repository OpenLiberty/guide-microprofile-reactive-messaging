#!/bin/bash

brew services start zookeeper
brew services start kafka

cd models
mvn clean install

cd ../kitchen
mvn clean package liberty:create liberty:install-feature liberty:deploy liberty:start

cd ../bar
mvn clean package liberty:create liberty:install-feature liberty:deploy liberty:start

cd ../servingWindow
mvn clean package liberty:create liberty:install-feature liberty:deploy liberty:start

cd ../order
mvn clean package liberty:create liberty:install-feature liberty:deploy liberty:start

cd ../restaurantBFF
mvn clean package liberty:create liberty:install-feature liberty:deploy liberty:start
