@ECHO OFF
@ECHO starting the kitchen container > kitchen.tmp

docker run -d ^
  -e MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS=%KAFKA_SERVER% ^
  --network=%NETWORK% ^
  --name=kitchen ^
  --rm ^
  kitchen:1.0-SNAPSHOT

DEL kitchen.tmp