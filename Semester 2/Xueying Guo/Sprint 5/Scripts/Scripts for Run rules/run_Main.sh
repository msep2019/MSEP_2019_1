#!/bin/sh

#This is a script for run package and get run time

#usage: ./run_Main.sh
#example: nohup ./run_Main.sh &

#Parameter setting

PARAM_PATH="/home/ubuntu/hadoop/scripts/param"
LOG_PATH="/home/ubuntu/hadoop/scripts/log"
JOB_LOGS="/home/ubuntu/hadoop/scripts/temp/joblogs"

#PARAM_PATH="/home/hadoop/Desktop/scripts_hadoop/param"
#. $PARAM_PATH/env.param

NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`

timestamp=`date "+%Y%m%d%H%M%S%s"`
LOG_FILE="${LOG_PATH}/${NAME}_${timestamp}.log"

exec 1>>${LOG_FILE}
exec 2>>${LOG_FILE}

TEST_AL_PARAM="${PARAM_PATH}/test_algorithm.param"


Jobtime="${JOB_LOGS}/Job_Timestamp.log"

cmd="rm ${Jobtime}"
${cmd}


for AL in `cat ${TEST_AL_PARAM}`
do

 algorithm=`echo ${AL} | cut -d "-" -f1`
 dataset=`echo ${AL} | cut -d "-" -f2`
 input_tr=`echo ${AL} | cut -d "-" -f3`

 echo "running ${dataset} dataset by ${algorithm} algorithm."
 

#run default value first

 echo "running default value.."
 ./run_without_properties.sh ${algorithm} ${dataset}



#run detect system to turn value
 echo "running detect system to tune value.."
 ./run_detect_system.sh ${algorithm} ${dataset} ${input_tr}



done


   echo "Job Finish"
