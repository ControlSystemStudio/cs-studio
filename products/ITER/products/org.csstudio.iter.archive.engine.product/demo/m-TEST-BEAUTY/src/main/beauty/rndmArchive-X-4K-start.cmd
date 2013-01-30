cd src/main/beauty/

archive-configtool -engine Archive_Engine_X -port 4912 -import -config rndm-X-4K-beauty.xml -replace_engine -steal_channels

archive-engine -port 4912 -engine Archive_Engine_X&

