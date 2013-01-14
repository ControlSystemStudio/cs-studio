#!/bin/sh

cd current

echo "Starting SDDS-Server"
nohup ./sdds-server > server.out 2> server.err < /dev/null &

