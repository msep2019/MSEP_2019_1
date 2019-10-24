#!/bin/sh

#This is a script for run package and get run time

#usage: ./run_with_properties.sh <algorithm> <dataset> <name_of_configuration> <value_of_setting>
#example: ./run_with_properties.sh RF CIDD mapreduce.output.fileoutputformat.compress true

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
LOG_FILE="${LOG_PATH}/${NAME}_${alg}_${dataset}_${parameter}_${value}_${timestamp}.log"

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
if [ "${parameter}" = "mapreduce.output.fileoutputformat.compress.type" -o "${parameter}" = "mapreduce.output.fileoutputformat.compress.codec"  ]; then
 cmd="hadoop jar ${jar} ${code} -D mapreduce.output.fileoutputformat.compress=true -D ${parameter}=${value}"
elif [ "${parameter}" = "mapreduce.map.output.compress.codec" ]; then
 cmd="hadoop jar ${jar} ${code} -D mapreduce.map.output.compress=true -D ${parameter}=${value}"
elif [ "${parameter}" = "mapreduce.task.io.sort.mb" ]; then
 cmd="hadoop jar ${jar} ${code} -D mapred.child.java.opts=-Xmx1639m -D ${parameter}=${value}"
else
 cmd="hadoop jar ${jar} ${code} -D ${parameter}=${value}"
fi
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

Jobtime="${JOB_LOGS}/Job_Timestamp.log"

echo "Algorithm is ${alg}" >> ${Jobtime}
echo "dataset is ${dataset}" >> ${Jobtime}
echo "change ${parameter} to ${value}" >> ${Jobtime}

if [ "${alg}" = "NB" ]; then
 echo "[${alg}] [${dataset}] [${parameter}:${value}] Date is ${getdate}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] job start time is ${jobStart}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] got new compressor is ${newCom}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] got new decompressor is ${newDeCom}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] training start time is ${trainStart}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] training end time is ${trainEnd}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] test start time is ${testStart}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] test end time is ${testEnd}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] job end time is ${jobEnd}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] mapreduce job1 start time=${mapredstart1}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] mapreduce job2 start time=${mapredstart2}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] mapreduce job1 end time=${mapredend1}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] mapreduce job2 end time=${mapredend2}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] number of splits1=${tasksnum1}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] number of splits2=${tasksnum2}" >> ${Jobtime}
elif [ "${alg}" = "RF" ]; then
 echo "[${alg}] [${dataset}] [${parameter}:${value}] Date is ${getdate}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] job start time is ${jobStart}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] training start time is ${trainStart}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] training end time is ${trainEnd}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] test start time is ${testStart}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] test end time is ${testEnd}" >> ${Jobtime}
 echo "[${alg}] [${dataset}] [${parameter}:${value}] job end time is ${jobEnd}" >> ${Jobtime}
fi

echo "[${alg}] [${dataset}] [${parameter}:${value}] training time is ${trainingtime}" >> ${Jobtime} 
echo "[${alg}] [${dataset}] [${parameter}:${value}] testing time is ${testingtime}" >> ${Jobtime}

exit 0
