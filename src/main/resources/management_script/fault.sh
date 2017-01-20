#!/bin/sh
PROCESS=$(ps -aux | grep UDPPortMapper-jar-with-dependencies.jar | awk -F " " 'NR==1{print $2}')
kill -9 $PROCESS
/opt/udpmapper/managment-scripts/start.sh