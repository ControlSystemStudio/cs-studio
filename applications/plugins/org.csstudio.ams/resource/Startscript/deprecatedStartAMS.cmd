@echo off

rem
rem Starting script for the AMS applications.
rem
rem Possible application names:
rem
rem org.csstudio.ams.filterManager.FilterManager
rem org.csstudio.ams.distributor.Distributor
rem org.csstudio.ams.connector.sms.SmsConnector
rem org.csstudio.ams.connector.email.EMailConnector
rem org.csstudio.ams.connector.voicemail.VoicemailConnector
rem org.csstudio.ams.connector.jms.JMSConnector
rem

cls

if "%1" == "" goto noParam

set WORKING_DIR=D:\css-ams
set JAVA_CMD=java.exe
set EQUINOX_LAUNCHER=org.eclipse.equinox.launcher_1.0.1.R33x_v20070828.jar
set LAUNCHER_CLASS=org.eclipse.equinox.launcher.Main
set LAUNCHER_PARAMS=-nosplash
set APPLICATION=

if "%1" == "FilterManager" set APPLICATION=org.csstudio.ams.filterManager.FilterManager
if "%1" == "Distributor" set APPLICATION=org.csstudio.ams.distributor.Distributor
if "%1" == "SmsConnector" set APPLICATION=org.csstudio.ams.connector.sms.SmsConnector
if "%1" == "EMailConnector" set APPLICATION=org.csstudio.ams.connector.email.EMailConnector
if "%1" == "VoicemailConnector" set APPLICATION=org.csstudio.ams.connector.voicemail.VoicemailConnector
if "%1" == "JMSConnector" set APPLICATION=org.csstudio.ams.connector.jms.JMSConnector
if "%1" == "MessageMinder" set APPLICATION=org.csstudio.ams.messageminder.MessageMinder
if "%APPLICATION%" == "" goto noParam

:startApplication
if "%1" == "EMailConnector" set JAVA_CMD=java.exe
start ""%1"" /D%WORKING_DIR% %JRE_HOME%\bin\%JAVA_CMD% -cp plugins/%EQUINOX_LAUNCHER% %LAUNCHER_CLASS% %LAUNCHER_PARAMS% -application %APPLICATION%

goto end

:noParam
echo.
echo Starting script for AMS
echo -----------------------
echo.
echo Usage: startAMS Application_Id 
echo.
echo        Possible values for Application_Id:
echo.
echo        FilterManager
echo        Distributor
echo        MessageMinder
echo        SmsConnector
echo        EMailConnector
echo        VoicemailConnector
echo        JMSConnector
echo.
echo        Example: startAMS FilterManager
echo.
echo.

pause

:end
set WORKING_DIR=
set JAVA_CMD=
set EQUINOX_LAUNCHER=
set LAUNCHER_CLASS=
set LAUNCHER_PARAMS=
set APPLICATION=
