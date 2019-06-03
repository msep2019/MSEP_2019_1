#!/bin/sh

#Parameter setting

#usage: ./cal_scalability.sh <number_of_pair> <node_number1> <runtime_1> ... <node_number_n> <runtime_n>
#example: ./cal_scalability.sh 3 2 22345 4 13456 6 23456 8 23456 10 23456
#Description: This program for calculate the scalability by using Least Squares Method

PARAM_PATH="/home/hadoop/Desktop/scripts/param"
. $PARAM_PATH/env.param

NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`

timestamp=`date "+%Y%m%d%H%M%S%s"`
LOG_FILE="${LOG_PATH}/${NAME}_${timestamp}.log"

exec 1>${LOG_FILE}
exec 2>>${LOG_FILE}

#Start Program

if [ $# != 11 ]; then
 echo "invalid parameter number"
 exit 1
else 
 echo "Set parameter complete and start program"
fi

PAIRS=${1}
x1=$2
x2=$4
x3=$6
x4=$8
x5=${10}
y1=$3
y2=$5
y3=$7
y4=$9
y5=${11}

sumx=$((${x1}+${x2}+${x3}+${x4}+${x5}))
sumy=$((${y1}+${y2}+${y3}+${y4}+${y5}))

avgx=$(awk 'BEGIN{print '${sumx}'/'${PAIRS}' }')
avgy=$(awk 'BEGIN{print '${sumy}'/'${PAIRS}' }')

part1=$(awk 'BEGIN{print '${x1}'*'${y1}'+'${x2}'*'${y2}'+'${x3}'*'${y3}'+'${x4}'*'${y4}'+'${x5}'*'${y5}' }')
part2=$(awk 'BEGIN{print '${x1}'*'${x1}'+'${x2}'*'${x2}'+'${x3}'*'${x3}'+'${x4}'*'${x4}'+'${x5}'*'${x5}' }')

up=$(awk 'BEGIN{print '${part1}' - '${PAIRS}' * '${avgx}' * '${avgy}' }')
down=$(awk 'BEGIN{print '${part2}' - '${PAIRS}' * '${avgx}' * '${avgx}' }')

k=$(awk 'BEGIN{print '${up}'/'${down}' }')
b=$(awk 'BEGIN{print '${avgy}' - '${k}' * '${avgx}' }')

echo "sumx=$sumx" 
echo "sumy=$sumy" 
echo "avgx=$avgx" 
echo "avgy=$avgy" 
echo "part1=$part1" 
echo "part2=$part2" 
echo "up=${up}" 
echo "down=${down}" 
echo "k=${k}" 
echo "b=${b}" 
echo "formular is: y=${k}x+${b}"

echo "k=${k}" > ${LOG_TMP_PATH}/scalability.txt
echo "b=${b}" >> ${LOG_TMP_PATH}/scalability.txt
echo "formular is:y=${k}x+${b}" >> ${LOG_TMP_PATH}/scalability.txt

exit 0


