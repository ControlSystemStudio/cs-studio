@echo off

set APP_DIR=D:\AMS\Application

if exist %APP_DIR%\AmsDepartmentDecision (
	echo Copy to AmsDepartmentDecision
	echo.
	copy New-AMS\plugin_customization.ini %APP_DIR%\AmsDepartmentDecision
)

if exist %APP_DIR%\AmsDistributor (
	echo Copy to AmsDistributor
	echo.
	copy Old-AMS\plugin_customization.ini %APP_DIR%\AmsDistributor
)

if exist %APP_DIR%\AmsEMailConnector (
	echo Copy to AmsEMailConnector
	echo.
	copy Old-AMS\plugin_customization.ini %APP_DIR%\AmsEMailConnector
	copy Old-AMS\startEMailConnector.cmd %APP_DIR%\AmsEMailConnector
)

if exist %APP_DIR%\AmsJmsConnector (
	echo Copy to AmsJmsConnector
	echo.
	copy Old-AMS\plugin_customization.ini %APP_DIR%\AmsJmsConnector
)

if exist %APP_DIR%\AmsMessageMinder (
	echo Copy to AmsMessageMinder
	echo.
	copy Old-AMS\plugin_customization.ini %APP_DIR%\AmsMessageMinder
)

if exist %APP_DIR%\AmsSmsConnector (
	echo Copy to AmsSmsConnector
	echo.
	copy Old-AMS\plugin_customization.ini %APP_DIR%\AmsSmsConnector
)

if exist %APP_DIR%\AmsVoicemailConnector (
	echo Copy to AmsVoicemailConnector
	echo.
	copy Old-AMS\plugin_customization.ini %APP_DIR%\AmsVoicemailConnector
	copy Old-AMS\jcapi.dll %APP_DIR%\AmsVoicemailConnector
)

set APP_DIR=

pause
