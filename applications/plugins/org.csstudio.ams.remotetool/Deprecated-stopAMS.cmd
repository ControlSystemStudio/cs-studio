REM Skript, das alle AMS-Anwendungen stoppt
REM Benoetigt AmsRemoteTool.
REM Markus Moeller, MKS 2
REM 2009-02-16

@echo off

echo.
echo Stopping AMS
echo.

set JAVA_CMD=java.exe
set LAUNCHER=org.eclipse.equinox.launcher_1.0.101.R34x_v20080819.jar
set MAINCLASS=org.eclipse.equinox.launcher.Main
set ECLIPSEAPPLIC=org.csstudio.ams.remotetool.AmsRemoteToolApplication

set LOCAL_USERNAME=applic
set LOCAL_HOSTNAME=krykams

REM Wenn wir die Anwendung ueber die EXE starten, wird immer javaw.exe benutzt. Wird als Rueckgabewert
REM eine Eclipse-Application ein Integerwert zurueckgegegeben, öffnet javaw immer eine Dialogbox.
REM Wir wollen den Rueckgabewert aber im Skript auswerten und benutzen daher java.exe!!!!

echo Stopping ams-jms-connector
%JRE_HOME%\bin\%JAVA_CMD% -cp plugins/%LAUNCHER% %MAINCLASS% -application %ECLIPSEAPPLIC% -plugincustomization plugin_customization.ini -host %LOCAL_HOSTNAME% -applicname ams-jms-connector -username %LOCAL_USERNAME% -pw admin4AMS
if errorlevel 0 goto ams-voicemail-connector
if errorlevel 1 goto stage1_error1
if errorlevel 2 goto stage1_error2
if errorlevel 3 goto stage1_error3
if errorlevel 4 goto stage1_error4
if errorlevel 5 goto stage1_error5

:stage1_error1
echo FEHLER: AmsRemoteTool: Fehlende oder fehlerhafte Parameter
goto ams-voicemail-connector

:stage1_error2
echo FEHLER: Anwendung ams-jms-connector nicht gefunden oder nicht aktiv
goto ams-voicemail-connector

:stage1_error3
echo FEHLER: Administrationspasswort fuer ams-jms-connector ist nicht gueltig
goto ams-voicemail-connector

:stage1_error4
echo FEHLER: XMPP-Verzeichnis nicht gefunden oder nicht aktiv
goto ams-voicemail-connector

:stage1_error5
echo FEHLER: Unbekannte Ursache


:ams-voicemail-connector
echo.
echo Stopping ams-voicemail-connector
%JRE_HOME%\bin\%JAVA_CMD% -cp plugins/%LAUNCHER% %MAINCLASS% -application %ECLIPSEAPPLIC% -plugincustomization plugin_customization.ini -host %LOCAL_HOSTNAME% -applicname ams-voicemail-connector -username %LOCAL_USERNAME% -pw admin4AMS
if errorlevel 0 goto ams-mail-connector
if errorlevel 1 goto stage2_error1
if errorlevel 2 goto stage2_error2
if errorlevel 3 goto stage2_error3
if errorlevel 4 goto stage2_error4
if errorlevel 5 goto stage2_error5

:stage2_error1
echo FEHLER: AmsRemoteTool: Fehlende oder fehlerhafte Parameter
goto ams-mail-connector

:stage2_error2
echo FEHLER: Anwendung ams-voicemail-connector nicht gefunden oder nicht aktiv
goto ams-mail-connector

:stage2_error3
echo FEHLER: Administrationspasswort fuer ams-voicemail-connector ist nicht gueltig
goto ams-mail-connector

:stage2_error4
echo FEHLER: XMPP-Verzeichnis nicht gefunden oder nicht aktiv
goto ams-mail-connector

:stage2_error5
echo FEHLER: Unbekannte Ursache


:ams-mail-connector
echo.
echo Stopping ams-mail-connector
%JRE_HOME%\bin\%JAVA_CMD% -cp plugins/%LAUNCHER% %MAINCLASS% -application %ECLIPSEAPPLIC% -plugincustomization plugin_customization.ini -host %LOCAL_HOSTNAME% -applicname ams-mail-connector -username %LOCAL_USERNAME% -pw admin4AMS
if errorlevel 0 goto ams-sms-connector
if errorlevel 1 goto stage3_error1
if errorlevel 2 goto stage3_error2
if errorlevel 3 goto stage3_error3
if errorlevel 4 goto stage3_error4
if errorlevel 5 goto stage3_error5

:stage3_error1
echo FEHLER: AmsRemoteTool: Fehlende oder fehlerhafte Parameter
goto ams-sms-connector

:stage3_error2
echo FEHLER: Anwendung ams-mail-connector nicht gefunden oder nicht aktiv
goto ams-sms-connector

:stage3_error3
echo FEHLER: Administrationspasswort fuer ams-mail-connector ist nicht gueltig
goto ams-sms-connector

:stage3_error4
echo FEHLER: XMPP-Verzeichnis nicht gefunden oder nicht aktiv
goto ams-sms-connector

:stage3_error5
echo FEHLER: Unbekannte Ursache


:ams-sms-connector
echo.
echo Stopping ams-sms-connector
%JRE_HOME%\bin\%JAVA_CMD% -cp plugins/%LAUNCHER% %MAINCLASS% -application %ECLIPSEAPPLIC% -plugincustomization plugin_customization.ini -host %LOCAL_HOSTNAME% -applicname ams-sms-connector -username %LOCAL_USERNAME% -pw admin4AMS
if errorlevel 0 goto ams-department-decision
if errorlevel 1 goto stage4_error1
if errorlevel 2 goto stage4_error2
if errorlevel 3 goto stage4_error3
if errorlevel 4 goto stage4_error4
if errorlevel 5 goto stage4_error5

:stage4_error1
echo FEHLER: AmsRemoteTool: Fehlende oder fehlerhafte Parameter
goto ams-department-decision

:stage4_error2
echo FEHLER: Anwendung ams-sms-connector nicht gefunden oder nicht aktiv
goto ams-department-decision

:stage4_error3
echo FEHLER: Administrationspasswort fuer ams-sms-connector ist nicht gueltig
goto ams-department-decision

:stage4_error4
echo FEHLER: XMPP-Verzeichnis nicht gefunden oder nicht aktiv
goto ams-department-decision

:stage4_error5
echo FEHLER: Unbekannte Ursache


:ams-department-decision
REM echo.
REM echo Stopping ams-department-decision
REM %JRE_HOME%\bin\%JAVA_CMD% -cp plugins/%LAUNCHER% %MAINCLASS% -console -application %ECLIPSEAPPLIC% -plugincustomization plugin_customization.ini -host %LOCAL_HOSTNAME% -applicname ams-department-decision -username %LOCAL_USERNAME% -pw admin4AMS
REM echo.

:ams-message-minder
echo.
echo Stopping ams-message-minder
%JRE_HOME%\bin\%JAVA_CMD% -cp plugins/%LAUNCHER% %MAINCLASS% -application %ECLIPSEAPPLIC% -plugincustomization plugin_customization.ini -host %LOCAL_HOSTNAME% -applicname ams-message-minder -username %LOCAL_USERNAME% -pw admin4AMS
if errorlevel 0 goto ams-distributor
if errorlevel 1 goto stage6_error1
if errorlevel 2 goto stage6_error2
if errorlevel 3 goto stage6_error3
if errorlevel 4 goto stage6_error4
if errorlevel 5 goto stage6_error5

:stage6_error1
echo FEHLER: AmsRemoteTool: Fehlende oder fehlerhafte Parameter
goto ams-distributor

:stage6_error2
echo FEHLER: Anwendung ams-message-minder nicht gefunden oder nicht aktiv
goto ams-distributor

:stage6_error3
echo FEHLER: Administrationspasswort fuer ams-message-minder ist nicht gueltig
goto ams-distributor

:stage6_error4
echo FEHLER: XMPP-Verzeichnis nicht gefunden oder nicht aktiv
goto ams-distributor

:stage6_error5
echo FEHLER: Unbekannte Ursache

:ams-distributor
echo.
echo Stopping ams-distributor
%JRE_HOME%\bin\%JAVA_CMD% -cp plugins/%LAUNCHER% %MAINCLASS% -application %ECLIPSEAPPLIC% -plugincustomization plugin_customization.ini -host %LOCAL_HOSTNAME% -applicname ams-distributor -username %LOCAL_USERNAME% -pw admin4AMS
if errorlevel 0 goto end
if errorlevel 1 goto stage7_error1
if errorlevel 2 goto stage7_error2
if errorlevel 3 goto stage7_error3
if errorlevel 4 goto stage7_error4
if errorlevel 5 goto stage7_error5

:stage7_error1
echo FEHLER: AmsRemoteTool: Fehlende oder fehlerhafte Parameter
goto end

:stage7_error2
echo FEHLER: Anwendung ams-distributor nicht gefunden oder nicht aktiv
goto end

:stage7_error3
echo FEHLER: Administrationspasswort fuer ams-distributor ist nicht gueltig
goto end

:stage7_error4
echo FEHLER: XMPP-Verzeichnis nicht gefunden oder nicht aktiv
goto end

:stage7_error5
echo FEHLER: Unbekannte Ursache


:end
set LOCAL_USERNAME=
set LOCAL_HOSTNAME=
set JAVA_CMD=
set LAUNCHER=
set MAINCLASS=
set ECLIPSEAPPLIC=

echo.
pause
