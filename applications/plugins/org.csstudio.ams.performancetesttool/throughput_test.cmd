@echo off

set SERVER=-uri tcp://192.168.11.7:62616 -uri tcp://192.168.11.7:64616
set COUNT=5000

echo.
echo Testing throughput of AMS
java -jar amsperftest.jar %SERVER% -count %COUNT% -template templates/ams.txt

echo.
echo Testing throughput of JMS Connector
rem java -jar amsperftest.jar %SERVER% -count %COUNT% -template templates/jmsconnector.txt -component jmsconnector -receive Test_1 -receive Test_2 -receive Test_3 -receive Test_4 -receive Test_5 -receive Test_6 -receive Test_7 -receive Test_8 -receive Test_9 -receive Test_10 -receive Test_11 -receive Test_12 -receive Test_13 -receive Test_14 -receive Test_15 -receive Test_16 -receive Test_17 -receive Test_18 -receive Test_19 -receive Test_20 -receive Test_21 -receive Test_22 -receive Test_23 -receive Test_24 -receive Test_25 -receive Test_26 -receive Test_27 -receive Test_28 -receive Test_29 -receive Test_30 -receive Test_31 -receive Test_32 -receive Test_33 -receive Test_34 -receive Test_35 -receive Test_36 -receive Test_37 -receive Test_38 -receive Test_39 -receive Test_40 -receive Test_41 -receive Test_42 -receive Test_43 -receive Test_44 -receive Test_45 -receive Test_46 -receive Test_47 -receive Test_48 -receive Test_49 -receive Test_50 -receive Test_51 -receive Test_52 -receive Test_53 -receive Test_54 -receive Test_55 -receive Test_56 -receive Test_57 -receive Test_58 -receive Test_59 -receive Test_60 -receive Test_61 -receive Test_62 -receive Test_63 -receive Test_64 -receive Test_65 -receive Test_66 -receive Test_67 -receive Test_68 -receive Test_69 -receive Test_70 -receive Test_71 -receive Test_72 -receive Test_73 -receive Test_74 -receive Test_75 -receive Test_76 -receive Test_77 -receive Test_78 -receive Test_79 -receive Test_80 -receive Test_81 -receive Test_82 -receive Test_83 -receive Test_84 -receive Test_85 -receive Test_86 -receive Test_87 -receive Test_88 -receive Test_89 -receive Test_90 -receive Test_91 -receive Test_92 -receive Test_93 -receive Test_94 -receive Test_95 -receive Test_96 -receive Test_97 -receive Test_98 -receive Test_99 -receive Test_100
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
