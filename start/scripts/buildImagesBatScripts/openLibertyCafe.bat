@ECHO OFF
@ECHO building openLibertyCafe image > openLibertyCafe.tmp
docker build -q -t openlibertycafe:1.0-SNAPSHOT openLibertyCafe\.
DEL openLibertyCafe.tmp