#!/bin/sh

#This is a script for killing statistic collection process.

#usage: ./kill_cluster.sh <number of cluster>
#example: ./kill_cluster.sh 1

if [ $# != 1 ]; then
 echo "invalid parameter number"
 exit 1
else 
 echo "Set parameter complete and start program"
fi



NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`
worker=$1

if [ "${worker}" = "master" ]; then
 LOG_PATH="/home/ubuntu/hadoop/scripts/log"
else
 LOG_PATH="/home/ubuntu/scripts/log"
fi

timestamp=`date "+%Y%m%d%H%M%S%s"`
LOG_FILE="${LOG_PATH}/${NAME}_worker_${worker}_${timestamp}.log"

exec 1>>${LOG_FILE}
exec 2>>${LOG_FILE}


dstatpid=`ps -ef | grep "dstat" | grep -v grep | awk '{print $2}'`
scrpid=`ps -ef | grep "cluster.sh" | grep -v grep | awk '{print $2}'`

echo "dstat pid is ${dstatpid}."
echo "scripts pid is ${scrpid}."

kill_cmd="kill -9 ${dstatpid}"
${kill_cmd}

kill_cmd="kill -9 ${scrpid}"
${kill_cmd}

exit 0
