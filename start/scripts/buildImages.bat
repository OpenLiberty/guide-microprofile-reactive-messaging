@ECHO Starting Scripts
@ECHO OFF

start /b docker build -q -t bar:1.0-SNAPSHOT bar\.
start /b docker build -q -t kitchen:1.0-SNAPSHOT kitchen\.
start /b docker build -q -t servingwindow:1.0-SNAPSHOT servingWindow\.
start /b docker build -q -t order:1.0-SNAPSHOT order\.
start /b docker build -q -t status:1.0-SNAPSHOT status\.
start /b docker build -q -t openlibertycafe:1.0-SNAPSHOT openLibertyCafe\.