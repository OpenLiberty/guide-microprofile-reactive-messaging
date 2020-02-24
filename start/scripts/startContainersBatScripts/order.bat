@ECHO OFF
@ECHO starting the order container > order.tmp

docker run -d ^
  -e MP_MESSAGING_CONNECTOR_LIBERTY_KAFKA_BOOTSTRAP_SERVERS=%KAFKA_SERVER% ^
  --network=%NETWORK% ^
  --name=order ^
  --rm ^
  order:1.0-SNAPSHOT 

DEL order.tmp