@echo off

set SERVER=-uri tcp://192.168.11.7:62616 -uri tcp://192.168.11.7:64616
set COUNT=5000

echo.
echo Testing throughput of AMS
java -jar amsperftest.jar %SERVER% -count %COUNT% -template templates/ams.txt

echo.
echo Testing throughput of JMS Connector
java -jar amsperftest.jar %SERVER% -count %COUNT% -template templates/jmsconnector.txt -component jmsconnector

echo.
echo Testing throughput of Distributor
java -jar amsperftest.jar %SERVER% -count %COUNT% -template templates/distributor.txt -component distributor

echo.
echo Testing throughput of Message Minder
java -jar amsperftest.jar %SERVER% -count %COUNT% -template templates/minder.txt -component minder

echo.
echo Testing throughput of Department Decision
java -jar amsperftest.jar %SERVER% -count %COUNT% -template templates/decision.txt -component decision


echo.

set SERVER=
pause
