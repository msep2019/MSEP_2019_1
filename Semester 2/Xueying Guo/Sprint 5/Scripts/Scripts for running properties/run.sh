#!/bin/sh

#This is a script for run package and get run time

#usage: ./run_gettime_conf.sh <number_of_executor> <dataset_type> <name_of_configuration> <value_of_setting>
#example: ./run_gettime_conf.sh 2 kdd spark.shuffle.memoryFraction 0.4

#Parameter setting

PARAM_PATH="/home/ubuntu/hadoop/scripts/param"
LOG_PATH="/home/ubuntu/hadoop/scripts/log"
JOB_LOGS="/home/ubuntu/hadoop/scripts/temp/joblogs"

NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`

timestamp=`date "+%Y%m%d%H%M%S%s"`
LOG_FILE="${LOG_PATH}/${NAME}_${timestamp}.log"

exec 1>>${LOG_FILE}
exec 2>>${LOG_FILE}

ACTUAL_CHANGE_PARAM="${PARAM_PATH}/actual_change.param"
ALL_CONF_PARAM="${PARAM_PATH}/all_conf.param"
RUN_AL_PARAM="${PARAM_PATH}/algorithm.param"


Jobtime="${JOB_LOGS}/Job_Timestamp.log"

cmd="rm ${Jobtime}"
${cmd}


for AL in `cat ${RUN_AL_PARAM}`
do

 algorithm=`echo ${AL} | cut -d "-" -f1`
 dataset=`echo ${AL} | cut -d "-" -f2`


 echo "running ${dataset} dataset by ${algorithm} algorithm."
 

#run default value first

 ./run_without_properties.sh ${algorithm} ${dataset}
 echo "running default value.."

 for LINE in `cat ${ACTUAL_CHANGE_PARAM}`
 do

  PSB_NUM=` cat ${ALL_CONF_PARAM} | grep "${LINE}:" | grep -o '/' | wc -l`
  echo "Setting hadoop property ${LINE}, there are (is) ${PSB_NUM} possible value except for the default value"

  ROW_END=`expr ${PSB_NUM} + 1`

  i=2
 
  #test all possible value of a setting
   while [ ${i} -le ${ROW_END} ]
   do

    a="-f${i}"

    VALUE=` cat ${ALL_CONF_PARAM} | grep "${LINE}:" | cut -d ":" -f2 | cut -d "/" ${a} `

    echo "Set ${LINE} = ${VALUE}"
    echo "Run Job.."

     ./run_with_properties.sh ${algorithm} ${dataset} ${LINE} ${VALUE}

   i=`expr ${i} + 1`

  done
 
   echo ""
   echo "Already try all possible value of setting ${LINE}"
   echo ""

 done

done


   echo "Job Finish"
