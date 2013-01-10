@echo off

set SERVER=-uri tcp://192.168.11.7:62616 -uri tcp://192.168.11.7:64616
set COUNT=2100
set RATE=7

echo.
echo Testing latency of AMS
java -jar amsperftest.jar %SERVER% -count %COUNT% -rate %RATE% -template templates/ams.txt

echo.
echo Testing latency of JMS Connector
java -jar amsperftest.jar %SERVER% -count %COUNT% -rate %RATE% -template templates/jmsconnector.txt -component jmsconnector

echo.
echo Testing latency of Distributor
java -jar amsperftest.jar %SERVER% -count %COUNT% -rate %RATE% -template templates/distributor.txt -component distributor

echo.
echo Testing latency of Message Minder
java -jar amsperftest.jar %SERVER% -count %COUNT% -rate %RATE% -template templates/minder.txt -component minder

echo.
echo Testing latency of Department Decision
java -jar amsperftest.jar %SERVER% -count %COUNT% -rate %RATE% -template templates/decision.txt -component decision

set SERVER=
pause
