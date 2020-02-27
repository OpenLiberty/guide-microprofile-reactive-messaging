@ECHO OFF
@ECHO building order image > order.tmp
docker build -q -t order:1.0-SNAPSHOT order\.
DEL order.tmp