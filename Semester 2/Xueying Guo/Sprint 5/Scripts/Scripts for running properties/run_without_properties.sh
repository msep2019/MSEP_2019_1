#!/bin/sh

#This is a script for run package and get run time

#usage: ./run_without_properties.sh <algorithm> <dataset>
#example: ./run_without_properties.sh RF CIDD

#Parameter setting

PARAM_PATH="/home/ubuntu/hadoop/scripts/param"
LOG_PATH="/home/ubuntu/hadoop/scripts/log"
JOB_LOGS="/home/ubuntu/hadoop/scripts/temp/joblogs"
START_TIME_LOGS="${JOB_LOGS}/Jobname.txt"

NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`

alg=`echo $1 | tr 'a-z' 'A-Z'`
dataset=`echo $2 | tr 'a-z' 'A-Z'`
parameter=$3
value=$4

timestamp=`date "+%Y%m%d%H%M%S%s"`
getdate=`date "+%d-%m"`
date=`date "+%Y%m%d"`
LOG_FILE="${LOG_PATH}/${NAME}_${alg}_${dataset}_${timestamp}.log"

exec 1>>${LOG_FILE}
exec 2>>${LOG_FILE}

#echo "algorithm is ${alg}"
#echo "dataset is ${dataset}"
#echo "parameter is ${parameter}"
#echo "value is ${value}"

#Run package

jar=`cat ${PARAM_PATH}/package_name.param | grep ${alg}_${dataset} | cut -d ":" -f2`
code=`cat ${PARAM_PATH}/package_name.param | grep ${alg}_${dataset} | cut -d ":" -f3`

#handle command

Jobtime="${JOB_LOGS}/Job_Timestamp.log"

 cmd="hadoop jar ${jar} ${code} "

echo "Command is: ${cmd}"

${cmd}


if [ "${alg}" = "NB" ]; then
 part=`cat ${LOG_FILE} | grep "Submitted application" | cut -d "_"  -f2`
 echo ${part} > ${START_TIME_LOGS}

 temp=`cat ${START_TIME_LOGS}`
 start1=`echo ${temp} | cut -d " " -f1`
 start2=`echo ${temp} | cut -d " " -f2`

 part=`cat ${LOG_FILE} | grep "completed successfully" | cut -d "_"  -f3 | cut -d " " -f1` 
 echo ${part} > ${START_TIME_LOGS}

 temp=`cat ${START_TIME_LOGS}`
 end1=`echo ${temp} | cut -d " " -f1`
 end2=`echo ${temp} | cut -d " " -f2`

# part=`cat ${LOG_FILE} | grep "Launched map tasks" | cut -d "="  -f2` 
part=`cat ${LOG_FILE} | grep "number of splits:" | cut -d ":"  -f5` 
 echo ${part} > ${START_TIME_LOGS}

 temp=`cat ${START_TIME_LOGS}`
 tasksnum1=`echo ${temp} | cut -d " " -f1`
 tasksnum2=`echo ${temp} | cut -d " " -f2`

 job1="${start1}_${end1}"
 job2="${start2}_${end2}"

echo ${job1}
echo ${job2}

 mapredstart1=`cat ${LOG_FILE} | grep "Submitted application" | grep ${job1} | cut -d " " -f2`
 mapredstart2=`cat ${LOG_FILE} | grep "Submitted application" | grep ${job2} | cut -d " " -f2`
 mapredend1=`cat ${LOG_FILE} | grep "completed successfully" | grep ${job1} | cut -d " " -f2`
 mapredend2=`cat ${LOG_FILE} | grep "completed successfully" | grep ${job2} | cut -d " " -f2`

 jobStart=`cat ${LOG_FILE} | grep "Job Start" | cut -d " " -f2`
 newCom=`cat ${LOG_FILE} | grep "Got brand-new compressor" | cut -d " " -f2`
 newDeCom=`cat ${LOG_FILE} | grep "Got brand-new decompressor" | cut -d " " -f2`
 trainStart=`cat ${LOG_FILE} | grep "Start Training" | cut -d " " -f2`
 trainEnd=`cat ${LOG_FILE} | grep "End Training" | cut -d " " -f2`
 testStart=`cat ${LOG_FILE} | grep "Start Prediction" | cut -d " " -f2`
 testEnd=`cat ${LOG_FILE} | grep "End Prediction" | cut -d " " -f2`
 jobEnd=`cat ${LOG_FILE} | grep "Job Finished" | cut -d " " -f2`
elif [ "${alg}" = "RF" ]; then
 jobStart=`cat ${LOG_FILE} | grep "Job Start" | cut -d " " -f2`
 trainStart=`cat ${LOG_FILE} | grep "Start Training" | cut -d " " -f2`
 trainEnd=`cat ${LOG_FILE} | grep "End Training" | cut -d " " -f2`
 testStart=`cat ${LOG_FILE} | grep "Start Testing" | cut -d " " -f2`
 testEnd=`cat ${LOG_FILE} | grep "End testing" | cut -d " " -f2`
 jobEnd=`cat ${LOG_FILE} | grep "Job Finished" | cut -d " " -f5`
fi
trainingtime=`cat ${LOG_FILE} | grep "Training Time" | cut -d " " -f4`
testingtime=`cat ${LOG_FILE} | grep "Predict Time" | cut -d " " -f4`

echo "Algorithm is ${alg}" >> ${Jobtime}
echo "dataset is ${dataset}" >> ${Jobtime}

if [ "${alg}" = "NB" ]; then
 echo "<Default>[${alg}] [${dataset}] Date is ${getdate}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] job start time is ${jobStart}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] got new compressor is ${newCom}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] got new decompressor is ${newDeCom}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] training start time is ${trainStart}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] training end time is ${trainEnd}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] test start time is ${testStart}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] test end time is ${testEnd}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] job end time is ${jobEnd}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] mapreduce job1 start time=${mapredstart1}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] mapreduce job2 start time=${mapredstart2}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] mapreduce job1 end time=${mapredend1}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] mapreduce job2 end time=${mapredend2}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] number of splits1=${tasksnum1}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] number of splits2=${tasksnum2}" >> ${Jobtime}
elif [ "${alg}" = "RF" ]; then
 echo "<Default>[${alg}] [${dataset}] Date is ${getdate}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] job start time is ${jobStart}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] training start time is ${trainStart}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] training end time is ${trainEnd}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] test start time is ${testStart}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] test end time is ${testEnd}" >> ${Jobtime}
 echo "<Default>[${alg}] [${dataset}] job end time is ${jobEnd}" >> ${Jobtime}
fi

echo "<Default>[${alg}] [${dataset}] training time is ${trainingtime}" >> ${Jobtime} 
echo "<Default>[${alg}] [${dataset}] testing time is ${testingtime}" >> ${Jobtime}

exit 0
