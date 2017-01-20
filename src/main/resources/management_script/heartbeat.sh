#!/bin/sh
exec 3<>/dev/tcp/127.0.0.1/8989
echo -e "heartbeat" >&3
MESSAGE=$(cat <&3)
echo $MESSAGE

if [[ $MESSAGE == *Server*is*ok* ]]
then
    # do nothing
    echo "Server is doing ok!"
else
    echo "$MESSAGE"
    echo "Server is not happy"
    echo "stop" > /dev/tcp/localhost/8989
fi
