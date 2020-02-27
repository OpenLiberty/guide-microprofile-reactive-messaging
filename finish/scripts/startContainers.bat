@ECHO OFF
set KAFKA_SERVER=kafka:9092
set NETWORK=reactive-app

docker network create %NETWORK%

start /b scripts\startContainersBatScripts\kafka.bat
start /b scripts\startContainersBatScripts\kitchen.bat
start /b scripts\startContainersBatScripts\bar.bat
start /b scripts\startContainersBatScripts\servingWindow.bat
start /b scripts\startContainersBatScripts\order.bat
start /b scripts\startContainersBatScripts\restaurantBFF.bat

TIMEOUT /t 1 /nobreak > NUL

@ECHO Waiting...
:LOOP
if not exist *.tmp goto :FINISH
    TIMEOUT /t 1 /nobreak > NUL
    goto LOOP

:FINISH
@ECHO All containers have started
