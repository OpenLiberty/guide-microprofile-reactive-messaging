#!/bin/bash
set -euxo pipefail

##############################################################################
##
##  Travis CI test script
##
##############################################################################

./scripts/packageApps.sh

mvn -pl order verify
mvn -pl bar verify
mvn -pl kitchen verify
mvn -pl servingWindow verify
mvn -pl status verify
mvn -pl openLibertyCafe verify

./scripts/buildImages.sh
./scripts/startContainers.sh

sleep 120

cafeOrderStatus="$(curl --write-out "%{http_code}" --silent --output /dev/null "http://localhost:9080/api/status")"
cafeServingWindowStatus="$(curl --write-out "%{http_code}" --silent --output /dev/null "http://localhost:9080/api/servingWindow")"

if [ "$cafeOrderStatus" == "200" ] && [ "$cafeServingWindowStatus" == "200" ]
then
  echo BFF OK
  
  orderStatus="$(docker exec -it order curl --write-out "%{http_code}" --silent --output /dev/null "http://status:9085/status")"
  servingWindowStatus="$(docker exec -it servingwindow curl --write-out "%{http_code}" --silent --output /dev/null "http://servingwindow:9082/servingWindow")"
  kitchenStatus="$(docker exec -it kitchen curl --write-out "%{http_code}" --silent --output /dev/null "http://kitchen:9083/health/ready")"
  barStatus="$(docker exec -it bar curl --write-out "%{http_code}" --silent --output /dev/null "http://bar:9084/health/ready")"

  if [ "$orderStatus" == "200" ] && [ "$servingWindowStatus" == "200" ] && [ "$kitchenStatus" == "200" ] && [ "$barStatus" == "200" ]
  then
    echo ENDPOINT OK
  else
    echo order status:
    echo "$orderStatus"
    echo serving window status:
    echo "$servingWindowStatus"
    echo kitchen status:
    echo "$kitchenStatus"
    echo bar status:
    echo "$barStatus"
    echo ENDPOINT
    exit 1
  fi
else
  echo bff order status:
  echo "$bffOrderStatus"
  echo bff serving window status:
  echo "$bffServingWindowStatus"
  echo ENDPOINT
  exit 1
fi

./scripts/stopContainers.sh
