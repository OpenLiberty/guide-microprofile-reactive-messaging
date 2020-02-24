@ECHO OFF
@ECHO starting the bar container > bar.tmp

docker run -d ^
  -e MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS=%KAFKA_SERVER% ^
  --network=%NETWORK% ^
  --name=bar ^
  --rm ^
  bar:1.0-SNAPSHOT 

DEL bar.tmp