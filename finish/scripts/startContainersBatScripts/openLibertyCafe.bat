@ECHO OFF
@ECHO starting the openLibertyCafe container > openLibertyCafe.tmp

set ORDER_SERVICE_URL="http://order:9081"
set SERVINGWINDOW_SERVICE_URL="http://servingwindow:9082"

docker run -d ^
  -e OrderClient_mp_rest_url=%ORDER_SERVICE_URL% ^
  -e ServingWindowClient_mp_rest_url=%SERVINGWINDOW_SERVICE_URL% ^
  -p 9080:9080 ^
  --network=%NETWORK% ^
  --name=openlibertycafe ^
  --rm ^
  openlibertycafe:1.0-SNAPSHOT

DEL openLibertyCafe.tmp