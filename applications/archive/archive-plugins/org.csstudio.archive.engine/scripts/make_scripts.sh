# ../ArchiveEngine/ArchiveEngine -pluginCustomization plugin_customization_old_rdb.ini -engine diag_test -data diag_test -port 4590 -consoleLog >diag_test.log 2>&1  &

engines=( iocs llrf hprf refline pps  mps  rccs ps   chmk timing vacuum blm  cryo20 cryo30 cryo40 cryo50-55 cryo56-57 cryo-soft fe-ioc1 fe-ioc2 fe-ioc3 cf-cc cf-kl cf-rn cf-ta tgt-loops tgt-rid tgt-mcb tgt-shut tgt-h2 ldmp scraper pwr_mon cec)
ports=(   4501 4502 4503 4504    4505 4506 4507 4508 4509 4510   4511   4512 4513   4514   4515   4516      4517      4518      4519    4520    4521    4522  4523  4524  4525  4526      4527    4528    4529     4530   4531 4532    4533   4534 )

N=${#engines[*]}
i=0
echo >start_all.sh
echo >stop_all.sh
while [ $i -lt $N ]
do
    engine=${engines[$i]}
    port=${ports[$i]}

    cd $engine
    echo >start.sh nohup ../../ArchiveEngine/ArchiveEngine -pluginCustomization ../plugin_customization_new_rdb.ini -engine $engine -data . -port $port -consoleLog \>out.log 2\>\&1  \&
    echo >view.sh lynx http://localhost:$port/main
    echo >stop.sh lynx -dump http://localhost:$port/stop
    chmod +x *.sh
    cd ..

    echo >>start_all.sh echo Starting $engine ...
    echo >>start_all.sh cd $engine
    echo >>start_all.sh sh start.sh
    echo >>start_all.sh cd ..
    echo >>start_all.sh sleep 10
    echo >>start_all.sh 

    echo >>stop_all.sh cd $engine
    echo >>stop_all.sh sh stop.sh
    echo >>stop_all.sh cd ..
    echo >>stop_all.sh 

    i=`expr $i + 1`
done

