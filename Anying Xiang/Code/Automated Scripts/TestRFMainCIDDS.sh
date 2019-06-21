#!/usr/bin/env bash

startTime=$(date)

flag=0
w1=0.2
w2=0.2
w3=0.2
w4=0.2
w5=0.2

runBDCA() {
  echo "debug-info: start running BDCA with "$1" executors"
  startBDCATime=$(date)
  echo "debug-info: start running BDCA time: "${startBDCATime}


#  echo "debug-info: clean cache"
#  ./CleanCache.sh

#  "sep_spark_bayes_v50_kdd_disc.jar"
  nodenum=$1
  nodenumprop="--num-executors "${nodenum}
  BDCAcmd="/home/ubuntu/Cloud/OpenStack/spark-2.4.0-bin-without-hadoop/bin/spark-submit --master yarn --deploy-mode cluster "${nodenumprop}" --executor-cores 1 /home/ubuntu/Cloud/OpenStack/nb_spark_output/sep_spark_rf_v1.jar /ubuntu/dataset/preprd-train-CIDIDS.txt /ubuntu/dataset/preprd-test-CIDIDS.txt 3 &> /home/ubuntu/Cloud/OpenStack/adaptionRF/log.txt"
  echo ${BDCAcmd}
  ssh -o ServerAliveInterval=60 -i chadni.pem -tt ubuntu@10.33.184.57 << exitserver
    echo "debug-info: on server"
    ${BDCAcmd}
    exit
exitserver
#  ssh -i chadni.pem ubuntu@10.33.184.57 ${BDCAcmd}


  scp -i chadni.pem ubuntu@10.33.184.57:/home/ubuntu/Cloud/OpenStack/adaptionRF/log.txt /home/jzd/SEP/adaptionRF/
  if ! grep -q "final status: SUCCEEDED" /home/jzd/SEP/adaptionRF/log.txt;then
    echo "debug-error: BDCA final status not SUCCEEDED!!!"
    flag=-1
    return  
  fi
  echo "debug-info: finish BDCA with "$1" executors SUCCEEDED"
  endBDCATime=$(date)
  echo "debug-info: finish running BDCA time: "${endBDCATime}

  driver=$(grep "ApplicationMaster host" /home/jzd/SEP/adaptionRF/log.txt | head -2)
  [[ "${driver}" =~ "7-0" ]] && driver="10.33.184.91"
  [[ "${driver}" =~ "7-1" ]] && driver="10.33.184.73"
  [[ "${driver}" =~ "7-2" ]] && driver="10.33.184.86"
  [[ "${driver}" =~ "7-3" ]] && driver="10.33.184.116"
  [[ "${driver}" =~ "7-4" ]] && driver="10.33.184.108"

  [[ "${driver}" =~ "8-0" ]] && driver="10.33.184.120"
  [[ "${driver}" =~ "8-1" ]] && driver="10.33.184.67"
  [[ "${driver}" =~ "8-2" ]] && driver="10.33.184.70"
  [[ "${driver}" =~ "8-3" ]] && driver="10.33.184.111"
  [[ "${driver}" =~ "8-4" ]] && driver="10.33.184.81"
  [[ "${driver}" =~ "N/A" ]] && driver="N/A"
  echo "debug-info: driver: "${driver}
  if [ "${driver}" == "N/A" ];then
    echo "debug-error: driver is N/A!!!" 
    flag=-1
    return
  fi
  appId=$(grep "Application report for" /home/jzd/SEP/adaptionRF/log.txt | head -1)
  appId=${appId#*_}
  appId=${appId% (*}
  echo "debug-info: appId: "${appId}

  getTimecmd="scp -i chadni.pem ubuntu@"${driver}":/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/yarn-log/application_"${appId}"/container_"${appId}"_01_000001/stdout /home/jzd/SEP/adaptionRF/"

#  echo "debug-info: getTimecmd: "${getTimecmd}
#  if [ ! -f "/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/yarn-log/application_"${appId}"/container_"${appId}"_01_000001/stdout" ]; then
#    echo "debug-error: no stdout file!!!"
#    return
#  fi
  ${getTimecmd}
  trainTime=$(grep "Train Time:" /home/jzd/SEP/adaptionRF/stdout)
  if [ "${trainTime}" == "" ];then
    echo "debug-error: run BDCA with "$1" executors FAILED: No Train Time"
    flag=-1
    return
  else
    trainTime=${trainTime#*: }
    testTime=$(grep "Test Time:" /home/jzd/SEP/adaptionRF/stdout)
    testTime=${testTime#*: }
  
    echo "debug-info: trainTime: "${trainTime}
    echo "debug-info: testTime: "${testTime}

    compFirst=`awk -v train="${trainArr[$1-1]}" -v a=-1 'BEGIN{print(train==a)?"1":"0"}'`
    if [ 1 -eq ${compFirst} ];then
      trainArr[$1-1]=${trainTime}
      testArr[$1-1]=${testTime}
    else
      trainArr[$1-1]=`awk -v old="${trainArr[$1-1]}" -v trainTime="${trainTime}" 'BEGIN{printf "%.2f\n",old+trainTime}'`
      testArr[$1-1]=`awk -v old="${testArr[$1-1]}" -v testTime="${testTime}" 'BEGIN{printf "%.2f\n",old+testTime}'`
    fi

    echo "debug-info: train time with 1 executor: "${trainArr[0]}
    echo "debug-info: train time with 2 executors: "${trainArr[1]}
    echo "debug-info: train time with 3 executors: "${trainArr[2]}
    echo "debug-info: train time with 4 executors: "${trainArr[3]}
    echo "debug-info: train time with 5 executors: "${trainArr[4]}
    echo "debug-info: train time with 6 executors: "${trainArr[5]}
    echo "debug-info: train time with 7 executors: "${trainArr[6]}
    echo "debug-info: train time with 8 executors: "${trainArr[7]}
    echo "debug-info: train time with 9 executors: "${trainArr[8]}
  fi
}

trainArr=(-1 -1 -1 -1 -1 -1 -1 -1)
testArr=(-1 -1 -1 -1 -1 -1 -1 -1)

for ((i=1; i<=1; i++))
do
  runBDCA 1
  if [ -1 -eq ${flag} ];then
    echo "debug-error"
    flag=0
  fi
done

for ((i=1; i<=1; i++))
do
  runBDCA 2
  if [ -1 -eq ${flag} ];then
    echo "debug-error"
    flag=0
  fi
done

for ((i=1; i<=1; i++))
do
  runBDCA 3
  if [ -1 -eq ${flag} ];then
    echo "debug-error"
    flag=0
  fi
done

for ((i=1; i<=1; i++))
do
  runBDCA 4
  if [ -1 -eq ${flag} ];then
    echo "debug-error"
    flag=0
  fi
done

for ((i=1; i<=1; i++))
do
  runBDCA 5
  if [ -1 -eq ${flag} ];then
    echo "debug-error"
    flag=0
  fi
done

for ((i=1; i<=1; i++))
do
  runBDCA 6
  if [ -1 -eq ${flag} ];then
    echo "debug-error"
    flag=0
  fi
done

for ((i=1; i<=1; i++))
do
  runBDCA 7
  if [ -1 -eq ${flag} ];then
    echo "debug-error"
    flag=0
  fi
done

for ((i=1; i<=1; i++))
do
  runBDCA 8
  if [ -1 -eq ${flag} ];then
    echo "debug-error"
    flag=0
  fi
done

for ((i=1; i<=1; i++))
do
  runBDCA 9
  if [ -1 -eq ${flag} ];then
    echo "debug-error"
    flag=0
  fi
done

t1=`awk -v ct="${trainArr[0]}" -v a=1 'BEGIN{printf "%.2f\n",ct/a}'`
t2=`awk -v x="${t1}" -v y=2 'BEGIN{printf "%.2f\n",x/y}'`
t4=`awk -v x="${t1}" -v y=4 'BEGIN{printf "%.2f\n",x/y}'`
t6=`awk -v x="${t1}" -v y=6 'BEGIN{printf "%.2f\n",x/y}'`
t8=`awk -v x="${t1}" -v y=8 'BEGIN{printf "%.2f\n",x/y}'`
ct1=`awk -v ct="${trainArr[0]}" -v a=1 'BEGIN{printf "%.2f\n",ct/a}'`
ct2=`awk -v ct="${trainArr[1]}" -v a=1 'BEGIN{printf "%.2f\n",ct/a}'`
ct4=`awk -v ct="${trainArr[3]}" -v a=1 'BEGIN{printf "%.2f\n",ct/a}'`
ct6=`awk -v ct="${trainArr[5]}" -v a=1 'BEGIN{printf "%.2f\n",ct/a}'`
ct8=`awk -v ct="${trainArr[7]}" -v a=1 'BEGIN{printf "%.2f\n",ct/a}'`

trend=`awk -v ct6="${ct6}" -v ct8="${ct8}" -v t6="${t6}" -v t8="${t8}" 'BEGIN{printf "%.2f\n",(ct6-ct8)/(t6-t8)}'`
echo "debug-info: trend: "${trend}

g2=`awk -v ct2="${ct2}" -v t2="${t2}" -v t1="${t1}" 'BEGIN{printf "%.2f\n",(ct2-t2)/(t1-t2)}'`
echo "debug-info: g2: "${g2}
g4=`awk -v ct4="$ct4" -v t4="${t4}" -v t1="${t1}" 'BEGIN{printf "%.2f\n",(ct4-t4)/(t1-t4)}'`
echo "debug-info: g4: "${g4}
g6=`awk -v ct6="$ct6" -v t6="${t6}" -v t1="${t1}" 'BEGIN{printf "%.2f\n",(ct6-t6)/(t1-t6)}'`
echo "debug-info: g6: "${g6}
g8=`awk -v ct8="$ct8" -v t8="${t8}" -v t1="${t1}" 'BEGIN{printf "%.2f\n",(ct8-t8)/(t1-t8)}'`
echo "debug-info: g8: "${g8}

gap=`awk -v w1="${w1}" -v w2="${w2}" -v w3="${w3}" -v w4="${w4}" -v g2="${g2}" -v g4="${g4}" -v g6="${g6}" -v g8="${g8}" 'BEGIN{printf "%.2f\n",w1*g2+w2*g4+w3*g6+w4*g8}'`
echo "debug-info: gap: "${gap}
scalability=`awk -v gap="${gap}" -v w5="${w5}" -v a=1 -v trend="${trend}" 'BEGIN{printf "%.2f\n",a-gap-w5+w5*trend}'`
echo "debug-info: scalability: "${scalability}

echo ${trainArr[*]} >> /home/jzd/SEP/backup/collectDataRFCIDDSMain.txt
echo ${testArr[*]} >> /home/jzd/SEP/backup/collectDataRFCIDDSMain.txt
echo ${gap} >> /home/jzd/SEP/backup/collectDataRFCIDDSMain.txt
echo ${trend} >> /home/jzd/SEP/backup/collectDataRFCIDDSMain.txt
echo ${scalability} >> /home/jzd/SEP/backup/collectDataRFCIDDSMain.txt

