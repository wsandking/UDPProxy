#!/bin/bash

TYPE=$1
NAME=$2
STATE=$3

case $STATE in
        "MASTER") /opt/udpmapper/managment-scripts/start.sh
                  exit 0
                  ;;
        "BACKUP") /opt/udpmapper/managment-scripts/stop.sh
                  exit 0
                  ;;
        "FAULT")  /opt/udpmapper/managment-scripts/stop.sh
                  exit 0
                  ;;
        *)        echo "unknown state"
                  exit 1
                  ;;
esac