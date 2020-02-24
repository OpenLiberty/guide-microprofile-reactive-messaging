@ECHO OFF
@ECHO building bar image > bar.tmp
docker build -q -t bar:1.0-SNAPSHOT bar\.
DEL bar.tmp