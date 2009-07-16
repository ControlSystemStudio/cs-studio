@echo off

REM ACHTUNG:
REM Dieses Skript benoetigt ein kleines Tool(sleep.exe), das innerhalb eines Skripts eine Pause einlegt.
REM Gibt es z.B. hier: http://wiki.winboard.org/index.php/Kleine_Pause_in_einer_Batch-Datei_einlegen
REM 
REM Eine Alternative waere z.B. ping /n 11 localhost >nul -> Wartet ca. 10 Sekunden

echo.
echo Starte AMS
echo.

schtasks /Run /TN AmsDistributor
sleep 6

schtasks /Run /TN AmsMessageMinder
sleep 6

schtasks /Run /TN AmsDepartmentDecision
sleep 6

schtasks /Run /TN AmsEMailConnector
sleep 4

schtasks /Run /TN AmsSmsConnector
sleep 4

schtasks /Run /TN AmsJmsConnector
sleep 4

schtasks /Run /TN AmsVoicemailConnector

echo.
echo Bitte im Task Manager die laufenden Prozesse ueberpruefen.
echo.

pause
