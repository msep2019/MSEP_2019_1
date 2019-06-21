#!/bin/sh

#usage: ./main.sh <dataset_type>
#example: ./main.sh kdd

#Parameter setting

PARAM_PATH="/home/hadoop/Desktop/scripts/param"
. $PARAM_PATH/env.param

NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`

timestamp=`date "+%Y%m%d%H%M%S%s"`
LOG_FILE="${LOG_PATH}/${NAME}_${timestamp}.log"

exec 1>${LOG_FILE}
exec 2>>${LOG_FILE}

#Start Program

#if [ $# != 3 ]; then
# echo "invalid parameter number"
# exit 1
#else 
# echo "Set parameter complete and start program"
#fi

flag=0
standard=-3000
threshold=0.8

PACKAGE=$1

# Calculate

echo "Start Run package"

cmd=`./run_gettime.sh 2 ${PACKAGE}`
runtime1=`cat ${LOG_TMP_PATH}/runtime.txt | grep "Training Time" | cut -d " " -f4`
echo "Finished run 2 nodes, runtime1 = ${runtime1} ms"

cmd=`./run_gettime.sh 4 ${PACKAGE}`
runtime2=`cat ${LOG_TMP_PATH}/runtime.txt | grep "Training Time" | cut -d " " -f4`
echo "Finished run 4 nodes, runtime2 = ${runtime2} ms"

cmd=`./run_gettime.sh 6 ${PACKAGE}`
runtime3=`cat ${LOG_TMP_PATH}/runtime.txt | grep "Training Time" | cut -d " " -f4`
echo "Finished run 6 nodes, runtime3 = ${runtime3} ms"

cmd=`./run_gettime.sh 8 ${PACKAGE}`
runtime4=`cat ${LOG_TMP_PATH}/runtime.txt | grep "Training Time" | cut -d " " -f4`
echo "Finished run 8 nodes, runtime4 = ${runtime4} ms"

cmd=`./run_gettime.sh 10 ${PACKAGE}`
runtime5=`cat ${LOG_TMP_PATH}/runtime.txt | grep "Training Time" | cut -d " " -f4`
echo "Finished run 10 nodes, runtime5 = ${runtime5} ms"

echo "calculating scalability..."

./cal_scalability.sh 5 2 ${runtime1} 4 ${runtime2} 6 ${runtime3} 8 ${runtime4} 10 ${runtime5}

if [ $? != 0 ]; then
 echo "failed to calculate scalability, please check log"
 exit 1
fi

#original=0.3
#original=`cat ${LOG_TMP_PATH}/scalability.txt | grep "k" | cut -d "=" -f2`
#formular=`cat ${LOG_TMP_PATH}/scalability.txt | grep "formular" | cut -d ":" -f2`
original=`cat ${LOG_TMP_PATH}/scalability.txt | grep "scalability" | cut -d "=" -f2`

#echo "the original scalability is ${original} and the original formular is ${formular} "
echo "the original scalability is ${original} "

 if [ $(echo "${original} < ${threshold}"|bc) -eq 1 ]; then
  echo "tested scalability is worse than the standard, need to update"
# else
#  echo "tested scalability is good, do not need to update"
#  exit 0
 fi

echo ""
value=$original
loop=0
ttl_loop=10

# try to improve scalability by updating default value
#if [  ${loop} -lt ${ttl_loop} -a $(echo "${original} < ${standard}"|bc) -eq 1 ]; then
#while [  ${loop} -lt ${ttl_loop} -a $(echo "${original} < ${standard}"|bc) -eq 1 ]
#do


 for LINE in `cat ${ACTUAL_CHANGE_PARAM}`
 do

  PSB_NUM=` cat ${ALL_CONF_PARAM} | grep "${LINE}" | grep -o '/' | wc -l`
  echo "Setting ${LINE}: There are ${PSB_NUM} possible value except for the default value"

  ROW_END=`expr ${PSB_NUM} + 1`

  i=2
 
  #test each possible value of a setting
   while [ ${i} -le ${ROW_END} ]
   do

    a="-f${i}"

    #CONF=` cat ${ALL_CONF_PARAM} | sed -n '3p' `
    VALUE=` cat ${ALL_CONF_PARAM} | grep "${LINE}" | cut -d ":" -f2 | cut -d "/" ${a} `

    echo "Set ${LINE} = ${VALUE}"
    echo "Run and test scalability.."

     ./run_gettime_conf.sh 2 ${PACKAGE} ${LINE} ${VALUE}
      runtime1=`cat ${LOG_TMP_PATH}/runtime.txt | grep "Training Time" | cut -d " " -f4`
      echo "Finished run 2 nodes, runtime1 = ${runtime1} ms"
     
      ./run_gettime_conf.sh 4 ${PACKAGE} ${LINE} ${VALUE}
      runtime2=`cat ${LOG_TMP_PATH}/runtime.txt | grep "Training Time" | cut -d " " -f4`
      echo "Finished run 4 nodes, runtime2 = ${runtime2} ms"
     
      ./run_gettime_conf.sh 6 ${PACKAGE} ${LINE} ${VALUE}
      runtime3=`cat ${LOG_TMP_PATH}/runtime.txt | grep "Training Time" | cut -d " " -f4`
      echo "Finished run 6 nodes, runtime3 = ${runtime3} ms"
    
      ./run_gettime_conf.sh 8 ${PACKAGE} ${LINE} ${VALUE}
      runtime4=`cat ${LOG_TMP_PATH}/runtime.txt | grep "Training Time" | cut -d " " -f4`
      echo "Finished run 8 nodes, runtime4 = ${runtime4} ms"

     ./run_gettime_conf.sh 10 ${PACKAGE} ${LINE} ${VALUE}
      runtime5=`cat ${LOG_TMP_PATH}/runtime.txt | grep "Training Time" | cut -d " " -f4`
      echo "Finished run 10 nodes, runtime5 = ${runtime5} ms"

     # Calculate

   ./cal_scalability.sh 5 2 ${runtime1} 4 ${runtime2} 6 ${runtime3} 8 ${runtime4} 10 ${runtime5}

   if [ $? != 0 ]; then
 	echo "failed to calculate scalability, please check log"
 	exit 1
   fi

   #new_scalability=5
   #new_scalability=`cat ${LOG_TMP_PATH}/scalability.txt | grep "k" | cut -d "=" -f2`
   #new_formular=`cat ${LOG_TMP_PATH}/scalability.txt | grep "formular" | cut -d ":" -f2`
new_scalability=`cat ${LOG_TMP_PATH}/scalability.txt | grep "scalability" | cut -d "=" -f2`

   if [ $(echo "${new_scalability} > ${original}"|bc) -eq 1 ]; then
    echo "scalability is improved successfully by setting ${LINE} as ${VALUE}, from original ${original} to new ${new_scalability}."
    #echo "the increased formular is ${new_formular} "
    flag=1
   else
    echo "scalability can not be improved by setting ${LINE} as ${VALUE}, since the original scalability is ${original} while the new one is ${new_scalability}. "
   fi

   i=`expr ${i} + 1`

  done
 
   echo ""
   echo "Already try all possible value of setting ${LINE}"
   echo ""
   #loop=`expr ${loop} + 1`

 done

#done
#fi

if [ ${flag} -eq 1 ]; then
 echo "Successfully to improve scalability by setting parameter(s)."
else 
 echo "scalability cannot be improved by current parameter, please change the parameter."
fi

exit 0


