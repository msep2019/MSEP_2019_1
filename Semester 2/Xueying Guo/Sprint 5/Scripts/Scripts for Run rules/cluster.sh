#!/bin/sh

#This is a script for getting dstat statistic

#usage: ./cluster.sh <number of cluster>
#example: nohup ./cluster.sh 1 &

if [ $# != 1 ]; then
 echo "invalid parameter number"
 exit 1
else 
 echo "Set parameter complete and start program"
fi

#Start Program
#Parameter setting

NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`

worker=$1

if [ "${worker}" = "master" ]; then
 SCR_PATH="/home/ubuntu/hadoop/scripts"
else
 SCR_PATH="/home/ubuntu/scripts"
fi

file="${SCR_PATH}/statsitic_collection_worker_${worker}.csv"
output="${SCR_PATH}/nohup.out"

rm ${file}
rm ${output}

cmd="dstat --time -c -m -d -n --io --top-bio --output ${file}"

${cmd}

exit 0
