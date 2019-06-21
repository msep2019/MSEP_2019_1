#!/bin/sh

#usage: ./cal_scalability.sh <number_of_pair> <node_number1> <runtime_1> ... <node_number_n> <runtime_n>
#example: ./cal_scalability.sh 3 2 22345 4 13456 6 23456 8 23456 10 23456
#Description: This program for calculate the scalability by using Least Squares Method

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

#parameter setting
#ideal decrease 50% runtime compare with the original time
threshold=0.5
decthreshold=1
inc=0.1
dec=0.1

slpwht=0.75
incnodewht=0.1
ttldecwht=0.1
stablescorewht=0.05


#Calculate actual slope for fitting curve

sumx=$(awk 'BEGIN{print '${x1}' + '${x2}' + '${x3}' + '${x4}' + '${x5}'}')
sumy=$(awk 'BEGIN{print '${y1}' + '${y2}' + '${y3}' + '${y4}' + '${y5}'}')

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


#Calculate ideal slope by using Least Squares Method
sy1=${y1}
sy2=$(awk 'BEGIN{print '${y1}'*'${threshold}' }')

idealk=$(awk 'BEGIN{print ('${sy2}'-'${sy1}')/('${x5}'-'${x1}') }')
#idealk=$(awk 'BEGIN{print ('${sy2}'-'${sy1}') }')

echo "sy1=${sy1}" 
echo "sy2=${sy2}" 
echo "idealk=${idealk}" 

#Calculate weight slope score

# if [ $(echo "${k} > 0"|bc) -eq 1 ]; then
#  slopescore=0
# else
  slopescore=$(awk 'BEGIN{print '${k}' / '${idealk}' }')
# fi

wslopescore=$(awk 'BEGIN{print '${slopescore}' * '${slpwht}' }')

echo "slope score=${slopescore}" 
echo "slope score weight=${wslopescore}" 

#Calculate total decrease score

idealdec=$(awk 'BEGIN{print '${y1}'*'${decthreshold}' }')
actdec=$(awk 'BEGIN{print '${y1}'-'${y5}' }')

decscore=$(awk 'BEGIN{print '${actdec}' / '${idealdec}' }')
wdecscore=$(awk 'BEGIN{print '${decscore}' * '${ttldecwht}' }')

echo "total decrease score=${decscore}" 
echo "total decrease score weight=${wdecscore}" 

#Calculate decrease each node score

incnodescore=0

gap=$(awk 'BEGIN{print '${y5}'-'${y4}' }')
nodegap=$(awk 'BEGIN{print '${x5}'-'${x4}' }')
maxgap=$(awk 'BEGIN{print '${y4}'*'${inc}' * '${nodegap}' }')
 if [ $(echo "${maxgap} <= ${gap}"|bc) -eq 1 ]; then
  incnodescore=$(awk 'BEGIN{print '${incnodescore}' - 0.25 }')
 fi

gap=$(awk 'BEGIN{print '${y4}'-'${y3}' }')
nodegap=$(awk 'BEGIN{print '${x4}'-'${x3}' }')
maxgap=$(awk 'BEGIN{print '${y3}'*'${inc}' * '${nodegap}' }')
 if [ $(echo "${maxgap} <= ${gap}"|bc) -eq 1 ]; then
  incnodescore=$(awk 'BEGIN{print '${incnodescore}' - 0.25 }')
 fi

gap=$(awk 'BEGIN{print '${y3}'-'${y2}' }')
nodegap=$(awk 'BEGIN{print '${x3}'-'${x2}' }')
maxgap=$(awk 'BEGIN{print '${y2}'*'${inc}' * '${nodegap}' }')
 if [ $(echo "${maxgap} <= ${gap}"|bc) -eq 1 ]; then
  incnodescore=$(awk 'BEGIN{print '${incnodescore}' - 0.25 }')
 fi

gap=$(awk 'BEGIN{print '${y2}'-'${y1}' }')
nodegap=$(awk 'BEGIN{print '${x2}'-'${x1}' }')
maxgap=$(awk 'BEGIN{print '${y1}'*'${inc}' * '${nodegap}' }')
 if [ $(echo "${maxgap} <= ${gap}"|bc) -eq 1 ]; then
  incnodescore=$(awk 'BEGIN{print '${incnodescore}' - 0.25 }')
 fi

wincnodescore=$(awk 'BEGIN{print '${incnodescore}' * '${incnodewht}' }')

echo "increase score=${incnodescore}" 
echo "increase score weight=${wincnodescore}" 

#Calculate stable decrease

stablescore=0

gap=$(awk 'BEGIN{print '${y4}'-'${y5}' }')
nodegap=$(awk 'BEGIN{print '${x5}'-'${x4}' }')
maxgap=$(awk 'BEGIN{print '${y4}'*'${dec}' * '${nodegap}' }' )
 if [ $(echo "${maxgap} <= ${gap}"|bc) -eq 1 ]; then
  stablescore=$(awk 'BEGIN{print '${stablescore}' + 0.25 }')
 fi

gap=$(awk 'BEGIN{print '${y3}'-'${y4}' }')
nodegap=$(awk 'BEGIN{print '${x4}'-'${x3}' }')
maxgap=$(awk 'BEGIN{print '${y3}'*'${dec}' * '${nodegap}' }')
 if [ $(echo "${maxgap} <= ${gap}"|bc) -eq 1 ]; then
  stablescore=$(awk 'BEGIN{print '${stablescore}' + 0.25 }')
 fi

gap=$(awk 'BEGIN{print '${y2}'-'${y3}' }')
nodegap=$(awk 'BEGIN{print '${x3}'-'${x2}' }')
maxgap=$(awk 'BEGIN{print '${y2}'*'${dec}' * '${nodegap}' }')
 if [ $(echo "${maxgap} <= ${gap}"|bc) -eq 1 ]; then
  stablescore=$(awk 'BEGIN{print '${stablescore}' + 0.25 }')
 fi

gap=$(awk 'BEGIN{print '${y1}'-'${y2}' }')
nodegap=$(awk 'BEGIN{print '${x2}'-'${x1}' }')
maxgap=$(awk 'BEGIN{print '${y1}'*'${dec}'*'${nodegap}' }')
 if [ $(echo "${maxgap} <= ${gap}"|bc) -eq 1 ]; then
  stablescore=$(awk 'BEGIN{print '${stablescore}' + 0.25 }')
 fi

# if [ $(echo "${y5} < ${y4}"|bc) -eq 1 ]; then
#  stablescore=$(awk 'BEGIN{print '${stablescore}' + 0.25 }')
# fi

# if [ $(echo "${y4} < ${y3}"|bc) -eq 1 ]; then
#  stablescore=$(awk 'BEGIN{print '${stablescore}' + 0.25 }')
# fi

# if [ $(echo "${y3} < ${y2}"|bc) -eq 1 ]; then
#  stablescore=$(awk 'BEGIN{print '${stablescore}' + 0.25 }')
# fi

# if [ $(echo "${y2} < ${y1}"|bc) -eq 1 ]; then
#  stablescore=$(awk 'BEGIN{print '${stablescore}' + 0.25 }')
# fi

wstablescore=$(awk 'BEGIN{print '${stablescore}' * '${stablescorewht}' }')

echo "stable score=${stablescore}" 
echo "stable score weight=${wstablescore}" 


#Calculate final score for scalability
scalability=$(awk 'BEGIN{print '${wslopescore}' + '${wdecscore}' + '${wincnodescore}' + '${wstablescore}' }')
echo "scalability=${scalability}" 

#echo "k=${k}" > ${LOG_TMP_PATH}/scalability.txt
#echo "b=${b}" >> ${LOG_TMP_PATH}/scalability.txt
#echo "formular is:y=${k}x+${b}" >> ${LOG_TMP_PATH}/scalability.txt
echo "scalability=${scalability}" > ${LOG_TMP_PATH}/scalability.txt

exit 0


