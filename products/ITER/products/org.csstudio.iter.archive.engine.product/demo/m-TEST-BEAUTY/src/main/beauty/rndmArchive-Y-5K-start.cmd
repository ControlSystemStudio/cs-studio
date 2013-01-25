cd src/main/beauty/

archive-configtool -engine Archive_Engine_Y -port 4914 -import -config rndm-Y-5K-beauty.xml -replace_engine -steal_channels

archive-engine -port 4914 -engine Archive_Engine_Y&

