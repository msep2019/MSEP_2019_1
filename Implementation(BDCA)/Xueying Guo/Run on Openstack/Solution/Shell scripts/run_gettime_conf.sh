#!/bin/sh

#This is a script for run package and get run time

#usage: ./run_gettime_conf.sh <number_of_executor> <dataset_type> <name_of_configuration> <value_of_setting>
#example: ./run_gettime_conf.sh 2 kdd spark.shuffle.memoryFraction 0.4

#Parameter setting

PARAM_PATH="/home/hadoop/Desktop/scripts/param"
. $PARAM_PATH/env.param

NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`

timestamp=`date "+%Y%m%d%H%M%S%s"`
LOG_FILE="${LOG_PATH}/${2}_${1}executors_$3_$4_${NAME}_${timestamp}.log"

exec 1>>${LOG_FILE}
exec 2>>${LOG_FILE}

NUM_EXECUTOR=$1
PACKAGE_NAME=`cat ${PARAM_PATH}/package_name.param | grep $2 | cut -d ":" -f2`
LOG=`cat ${PARAM_PATH}/package_name.param | grep $2 | cut -d ":" -f3`
CONF=$3
VALUE=$4

CONF_CMD="--conf ${CONF}=${VALUE}"
echo "${CONF_CMD}"

#Run package

# Login Node / Cluster

echo "Run $2 dataset on ${NUM_EXECUTOR} node(s)."

echo "connect to MASTER node"
IP=`cat $PARAM_PATH/node.param | grep "MASTER" | cut -d ":" -f2`

cmd="$SPARK_HOME/bin/spark-submit --master yarn --deploy-mode cluster --num-executors ${NUM_EXECUTOR} --executor-cores 1 ${CONF_CMD} $PACKAGE_NAME"

echo "Job is running..."

ssh -i $KEY_PATH/keypair1.pem $USER_NAME@$IP > /dev/null 2>&1 << master

cd $LOG

echo ${cmd} > command.txt
echo ${scpcmd} >> command.txt

${cmd} > Output.txt

cat Output.txt | grep "ApplicationMaster host: cluster-worker" > result.txt
cat Output.txt | grep "tracking URL:" >> result.txt

exit

master

#get runtime

 # grep cluster & logfile

scpcmd=`scp -i $KEY_PATH/keypair1.pem $USER_NAME@${IP}:${LOG}/result.txt ${LOG_TMP_PATH}`

NODE=`cat ${LOG_TMP_PATH}/result.txt | grep "ApplicationMaster host: cluster-worker" | cut -d "-" -f3`
NODE=`echo ${NODE} | cut -d " " -f2 `
NODE=NODE${NODE}

LP=` cat ${LOG_TMP_PATH}/result.txt | grep "tracking URL:" | cut -d "/" -f5 `
LP=` echo ${LP} | cut -d " " -f3 `

#login to Cluster

str="${NODE}:"
IP=`cat $PARAM_PATH/node.param | grep ${str} | cut -d ":" -f2`

echo "Log is on the cluster ${NODE}, (${IP}) and path is ${LP}"

LOGP="${CLUSTER_LOG_PATH}/$LP/*"

echo "connect to cluster ${NODE} "
ssh -i $KEY_PATH/keypair1.pem $USER_NAME@$IP > /dev/null 2>&1 << cluster

#cd ${CLUSTER_LOG_PATH}
#cd ${LP}/*

cd ${LOGP}

cat stdout | grep "Training Time" > runtime.txt
cat stdout | grep "prediction Time" >> runtime.txt

exit

cluster

scpcmd=`scp -i $KEY_PATH/keypair1.pem $USER_NAME@${IP}:${LOGP}/runtime.txt ${LOG_TMP_PATH}`

echo "Program completed"

training=`cat ${LOG_TMP_PATH}/runtime.txt | grep "Training Time" | cut -d " " -f4`
#testing=`cat ${LOG_TMP_PATH}/runtime.txt | grep "prediction Time" | cut -d " " -f4`
#runtime=`expr ${training} + ${testing}`

echo ${training}
#echo ${testing}
#echo ${runtime}

#training=`expr ${training} + 0`

#return ${training}

