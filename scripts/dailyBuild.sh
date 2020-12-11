#!/bin/bash
while getopts t:d:b:u: flag;
do
    case "${flag}" in
        t) DATE="${OPTARG}";;
        d) DRIVER="${OPTARG}";;
        b) BUILD="${OPTARG}";;
        u) DOCKER_USERNAME="${OPTARG}"
    esac
done

echo "Testing daily OpenLiberty image"
docker ps
sed -i "\#<artifactId>liberty-maven-plugin</artifactId>#a<configuration><install><runtimeUrl>https://public.dhe.ibm.com/ibmdl/export/pub/software/openliberty/runtime/nightly/"$DATE"/"$DRIVER"</runtimeUrl></install></configuration>" system/pom.xml inventory/pom.xml
cat system/pom.xml
cat inventory/pom.xml

sed -i "s;FROM openliberty/open-liberty:kernel-java8-openj9-ubi;FROM "$DOCKER_USERNAME"/olguides:"$BUILD";g" system/Dockerfile inventory/Dockerfile
cat system/Dockerfile
cat inventory/Dockerfile
 
docker pull $DOCKER_USERNAME"/olguides:"$BUILD

docker ps
sudo ../scripts/testApp.sh

docker ps
sleep 120
docker ps
echo "Testing daily Docker image"

sed -i "s;FROM "$DOCKER_USERNAME"/olguides:"$BUILD";FROM openliberty/daily:latest;g" inventory/Dockerfile system/Dockerfile

cat inventory/Dockerfile system/Dockerfile

docker pull "openliberty/daily:latest"

../scripts/testApp.sh
