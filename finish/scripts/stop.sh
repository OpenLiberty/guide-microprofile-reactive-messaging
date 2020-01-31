#!/bin/bash

cd ../kitchen
mvn liberty:stop

cd ../bar
mvn liberty:stop

cd ../servingWindow
mvn liberty:stop

cd ../order
mvn liberty:stop

cd ../restaurantBFF
mvn liberty:stop

brew services stop zookeeper
brew services stop kafka