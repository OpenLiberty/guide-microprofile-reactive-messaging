#!/bin/bash

docker stop kafka zookeeper &
#mvn -pl system liberty:stop &
#mvn -pl inventory liberty:stop & 
wait

docker network rm reactive-app
