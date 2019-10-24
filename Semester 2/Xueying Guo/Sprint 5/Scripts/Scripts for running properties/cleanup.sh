#!/bin/sh

#This is a script for cleaning up data

#usage: ./cleanup.sh
#example: nohup ./cleanup.sh &

#Parameter setting

PARAM_PATH="/home/hadoop/Desktop/scripts_hadoop/param"
. $PARAM_PATH/env.param

NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`

timestamp=`date "+%Y%m%d%H%M%S%s"`
LOG_FILE="${LOG_PATH}/${NAME}_${timestamp}.log"

exec 1>>${LOG_FILE}
exec 2>>${LOG_FILE}

rm ${UTILI_PATH}/Utilization_*.txt
rm ${TIME_PATH}/*timestamp*.txt
rm ${TIME_PATH}/*Timestamp*.log
rm ${STATISTIC_PATH}/*statsitic_collection_worker*.csv
rm ${RESULT_PATH}/*result*.txt

echo "already clean up"

   echo "Job Finish"
