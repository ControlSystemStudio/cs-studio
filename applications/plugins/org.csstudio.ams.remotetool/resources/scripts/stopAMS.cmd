@echo off

REM stopAMS, 2012-04-05
REM
REM Skript, das alle AMS-Anwendungen stoppt
REM Benoetigt AmsRemoteTool.
REM stopAMS.cmd Name1 [Name2] [Name3] [Name4] [...]
REM Markus Moeller, MKS 2
REM 2009-02-18

REM ams-jms-connector ams-voicemail-connector ams-mail-connector ams-sms-connector ams-department-decision ams-message-minder ams-distributor

echo.
echo Stopping AMS
echo.

set JAVA_CMD=java.exe
set LAUNCHER=org.eclipse.equinox.launcher_1.1.1.R36x_v20101122_1400.jar
set MAINCLASS=org.eclipse.equinox.launcher.Main
set ECLIPSEAPPLIC=org.csstudio.ams.remotetool.AmsRemoteToolApplication

set LOCAL_USERNAME=applic
set LOCAL_HOSTNAME=krykamsa

if not "%1" == "" goto begin
echo.
echo Usage: stopAMS.cmd Name1 [Name2] [Name3] [Name4] [...]
echo.
goto end 
 
REM Wenn wir die Anwendung ueber die EXE starten, wird immer javaw.exe benutzt. Wird als Rueckgabewert
REM eine Eclipse-Application ein Integerwert zurueckgegegeben, öffnet javaw immer eine Dialogbox.
REM Wir wollen den Rueckgabewert aber im Skript auswerten und benutzen daher java.exe!!!!

:begin
echo.
echo %1
echo.
%JRE_HOME%\bin\%JAVA_CMD% -cp plugins/%LAUNCHER% %MAINCLASS% -application %ECLIPSEAPPLIC% -plugincustomization plugin_customization.ini -host %LOCAL_HOSTNAME% -applicname %1 -username %LOCAL_USERNAME% -pw admin4AMS
if "%ERRORLEVEL%" == "0" goto continue
if "%ERRORLEVEL%" == "1" goto error1
if "%ERRORLEVEL%" == "2" goto error2
if "%ERRORLEVEL%" == "3" goto error3
if "%ERRORLEVEL%" == "4" goto error4
if "%ERRORLEVEL%" == "5" goto error5

color cf
echo.
echo Nicht definierter Fehler.
echo.
pause
color 07
goto continue

:error1
color cf
echo.
echo FEHLER: AmsRemoteTool: Fehlende oder fehlerhafte Parameter
echo.
pause
color 07
goto continue

:error2
color cf
echo.
echo FEHLER: Anwendung %1 nicht gefunden oder nicht aktiv
echo.
pause
color 07
goto continue

:error3
color cf
echo.
echo FEHLER: Administrationspasswort fuer %1 ist nicht gueltig
echo.
pause
color 07
goto continue

:error4
color cf
echo.
echo FEHLER: XMPP-Verzeichnis nicht gefunden oder nicht aktiv
echo.
pause
color 07
goto continue

:error5
color cf
echo.
echo FEHLER: Unbekannte Ursache
echo.
pause
color 07
goto continue

:continue:
shift
if not "%1" == "" goto begin

:end
set LOCAL_USERNAME=
set LOCAL_HOSTNAME=
set JAVA_CMD=
set LAUNCHER=
set MAINCLASS=
set ECLIPSEAPPLIC=

echo.
pause
