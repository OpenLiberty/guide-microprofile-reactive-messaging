@ECHO OFF
@ECHO starting the servingwindow container > servingWindow.tmp

docker run -d ^
  -e MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS=%KAFKA_SERVER% ^
  --network=%NETWORK% ^
  --name=servingwindow ^
  --rm ^
  servingwindow:1.0-SNAPSHOT

DEL servingWindow.tmp