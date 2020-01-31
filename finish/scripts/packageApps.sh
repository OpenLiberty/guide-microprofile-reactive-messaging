#!/bin/bash

cd ../models
mvn install

cd ../kitchen
mvn package

cd ../bar
mvn package

cd ../servingWindow
mvn package

cd ../order
mvn package

cd ../restaurantBFF
mvn package