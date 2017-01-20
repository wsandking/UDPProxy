#!/bin/sh
java -jar /opt/udpmapper/UDPPortMapper-jar-with-dependencies.jar & (sleep 20;echo "start" > /dev/tcp/localhost/8989)