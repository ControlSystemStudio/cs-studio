@echo off

REM startAMS, 2012-04-05
REM
REM ACHTUNG:
REM Dieses Skript benoetigt ein kleines Tool(sleep.exe), das innerhalb eines Skripts eine Pause einlegt.
REM Gibt es z.B. hier: http://wiki.winboard.org/index.php/Kleine_Pause_in_einer_Batch-Datei_einlegen
REM 
REM Eine Alternative waere z.B. ping /n 11 localhost >nul -> Wartet ca. 10 Sekunden

echo.
echo Starte AMS
echo.

schtasks /Run /TN \AMS\Applications\AmsDistributor
D:\AMS\Administration\sleep 6

schtasks /Run /TN \AMS\Applications\AmsMessageMinder
D:\AMS\Administration\sleep 6

schtasks /Run /TN \AMS\Applications\AmsDepartmentDecision
D:\AMS\Administration\sleep 10

schtasks /Run /TN \AMS\Applications\AmsDeliverySystem

echo.
echo Bitte im Task Manager die laufenden Prozesse ueberpruefen.
echo.

pause
