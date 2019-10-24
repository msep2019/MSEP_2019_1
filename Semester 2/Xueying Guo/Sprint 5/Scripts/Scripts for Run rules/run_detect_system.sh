#!/bin/sh

#This is a script for run package and get run time

#usage: ./run_detect_system.sh <algorithm> <dataset> <training set>
#example: nohup ./run_detect_system.sh NB NSLKDD /user/hadoop/nb/nslkdd/train/NSLKDDTrain.txt &

#Parameter setting

PARAM_PATH="/home/ubuntu/hadoop/scripts/param"
LOG_PATH="/home/ubuntu/hadoop/scripts/log"
JOB_LOGS="/home/ubuntu/hadoop/scripts/temp/joblogs"

#PARAM_PATH="/home/hadoop/Desktop/scripts_hadoop/param"
#. $PARAM_PATH/env.param

NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`

algorithm=${1}
dataset=${2}
input_tr=${3}

timestamp=`date "+%Y%m%d%H%M%S%s"`
LOG_FILE="${LOG_PATH}/${NAME}_${algorithm}_${dataset}_${timestamp}.log"

exec 1>>${LOG_FILE}
exec 2>>${LOG_FILE}

ALL_CONF_PARAM="${PARAM_PATH}/all_conf.param"
RULE_PARAM="${PARAM_PATH}/rules.param"


# detect size of dataset

echo "detecting size of the dataset and assigning fuzzy region..."

instance_num=`hadoop fs -cat ${input_tr} | wc -l`
all=`hadoop fs -cat ${input_tr} | grep -o ',' | wc -l`
feature_num=$(awk 'BEGIN{print '${all}' / '${instance_num}'}')

if [ "${dataset}" = "NSLKDD" ]; then
 feature_num=20
elif [ "${dataset}" = "CICIDS2017" ]; then
 feature_num=43
elif [ "${dataset}" = "CIC2017" ]; then
 feature_num=35
elif [ "${dataset}" = "DDOS2019" ]; then
 feature_num=72
elif [ "${dataset}" = "UNSWNB15" ]; then
 feature_num=31
fi

# calculate fuzzy region for feature size
if [ $(echo " ${feature_num} <= 6"|bc) -eq 1 ]; then

 feature_size="S"

elif [ $(echo " 6 < ${feature_num}"|bc) -eq 1 -a $(echo " ${feature_num} <= 24"|bc) -eq 1 ]; then

 perc=$(awk 'BEGIN{print ('${feature_num}' - 6) / 18 }')

 if [ $(echo " 0.5 <= ${perc}"|bc) -eq 1 ]; then
  feature_size="M"
 else
  feature_size="S"
 fi

elif [ $(echo " 24 < ${feature_num}"|bc) -eq 1 -a $(echo " ${feature_num} < 42"|bc) -eq 1 ]; then

 perc=$(awk 'BEGIN{print (42 - '${feature_num}') / 18 }')

 if [ $(echo " 0.5 <= ${perc}"|bc) -eq 1 ]; then
  feature_size="M"
 else
  feature_size="L"
 fi

else 

feature_size="L"

fi

# calculate fuzzy region for No. of instances
if [ $(echo " ${instance_num} <= 490000"|bc) -eq 1 ]; then

 instance_size="S"

elif [ $(echo " 490000 < ${instance_num}"|bc) -eq 1 -a $(echo " ${instance_num} <= 3245000"|bc) -eq 1 ]; then

 perc=$(awk 'BEGIN{print ('${instance_num}' - 490000) / 2755000 }')

 if [ $(echo " 0.5 <= ${perc} "|bc) -eq 1 ]; then
  instance_size="M"
 else
  instance_size="S"
 fi

elif [ $(echo " 3245000 < ${instance_num}"|bc) -eq 1 -a $(echo " ${instance_num} < 6000000"|bc) -eq 1 ]; then

 perc=$(awk 'BEGIN{print (6000000 - '${instance_num}') / 2755000 }')

 if [ $(echo " 0.5 <= ${perc}"|bc) -eq 1 ]; then
  instance_size="M"
 else
  instance_size="L"
 fi

else 

instance_size="L"

fi

echo "finish detecting size of the dataset, configuring properties... "

echo "${dataset}: the No. of feature is ${feature_num} and the No. of instance is ${instance_num}"
echo "${dataset}: the feature size is ${feature_size} and the instance size is ${instance_size}"

size="${feature_size}${instance_size}"


# Apply rules for sepecific datasets
echo "Apply rules for sepecific datasets... "

allrules=`cat ${RULE_PARAM} | grep ":" | grep "${size}" `

parcmd=""
  for LINE in `echo ${allrules}`
  do

    param_num=` echo ${LINE} | cut -d ":" -f1 | cut -d "V" -f1 | cut -d "P" -f2 `
    value_num=` echo ${LINE} | cut -d ":" -f1 | cut -d "V" -f2 `

echo "param number is ${param_num}, value number is ${value_num}"

value_num=`expr ${value_num} + 1 `
a="-f${value_num}"


    param=` sed -n "${param_num}, ${param_num}p" ${ALL_CONF_PARAM} | cut -d ":" -f1 `
    value=` cat ${ALL_CONF_PARAM} | grep "${param}:" | cut -d ":" -f2 | cut -d "/" ${a} `

echo "will turn ${param} to ${value}"

parcmd="${parcmd}+-D+${param}=${value}"

  done


echo "try to tune value for ${algorithm} and ${dataset}"

 ./run_with_rules.sh ${algorithm} ${dataset} ${parcmd}
 

   echo "Job Finish"
