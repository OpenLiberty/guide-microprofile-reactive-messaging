#!/bin/bash

cd ..
mvn -pl models clean install
mvn clean package