#!/bin/bash

brew services start zookeeper
brew services start kafka

cd ../kitchen
mvn liberty:create liberty:install-feature liberty:deploy liberty:start

cd ../bar
mvn liberty:create liberty:install-feature liberty:deploy liberty:start

cd ../servingWindow
mvn liberty:create liberty:install-feature liberty:deploy liberty:start

cd ../order
mvn liberty:create liberty:install-feature liberty:deploy liberty:start

cd ../restaurantBFF
mvn liberty:create liberty:install-feature liberty:deploy liberty:start
