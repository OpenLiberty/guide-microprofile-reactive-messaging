@ECHO OFF
@ECHO building kitchen image > kitchen.tmp
docker build -q -t kitchen:1.0-SNAPSHOT kitchen\.
DEL kitchen.tmp