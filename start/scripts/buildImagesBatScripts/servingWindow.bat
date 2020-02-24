@ECHO OFF
@ECHO building servingWindow image > servingWindow.tmp
docker build -q -t servingwindow:1.0-SNAPSHOT servingWindow\.
DEL servingWindow.tmp