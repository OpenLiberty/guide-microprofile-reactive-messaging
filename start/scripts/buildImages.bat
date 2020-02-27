@ECHO Starting Scripts
@ECHO OFF

start /b scripts\buildImagesBatScripts\kitchen.bat
start /b scripts\buildImagesBatScripts\bar.bat
start /b scripts\buildImagesBatScripts\servingWindow.bat
start /b scripts\buildImagesBatScripts\order.bat
start /b scripts\buildImagesBatScripts\restaurantBFF.bat

TIMEOUT /t 1 /nobreak > NUL

@ECHO Waiting...
:LOOP
if not exist *.tmp goto :FINISH
    TIMEOUT /t 1 /nobreak > NUL
    goto LOOP

:FINISH
@ECHO Images building completed