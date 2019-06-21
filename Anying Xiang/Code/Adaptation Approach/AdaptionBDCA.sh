#!/usr/bin/env bash

startTime=$(date)

# how to use this script: ./AdaptionBDCA-v2.sh jarfile(parameter1) trainfile(parameter2) testfile(parameter3) datasetType(parameter4: 1 / 2 / 3 / 4) responseTimeType(parameter5: train / test)
# e.g.:
# ./AdaptionBDCA-v2.sh sep_spark_bayes_v50.jar KDDTrain.txt KDDTest.txt 1 train
# ./AdaptionBDCA-v2.sh sep_spark_bayes_v50_cv3.jar preprd-train-CICIDS2017-0.6.txt none 4 train
# ./AdaptionBDCA-v3.sh sep_spark_bayes_v53_cidds_saveModel.jar preprd-train-CIDIDS.txt preprd-test-CIDIDS.txt 3 test

echo "Jar: $1"
echo "Train file: $2"
echo "Test file: $3"
echo "DataSet Type: $4"
echo "Response Time Type: $5"

jarfile=$1
trainfile=$2
testfile=$3
datasetType=$4
responseTimeType=$5

params=("--executor-memory" "--conf spark.shuffle.sort.bypassMergeThreshold=" "--conf spark.shuffle.compress=" "--conf spark.memory.storageFraction=" "--conf spark.shuffle.file.buffer=" "--conf spark.reducer.maxSizeInFlight=" "--conf spark.memory.fraction=" "--conf spark.serializer.objectStreamReset=")
paramVA=(" 1024m" "200" "true" "0.5" "32k" "48m" "0.6" "100")
paramVB=(" 1250m" "400" "false" "0.7" "64k" "96m" "0.8" "-1")
CPV=("A" "A" "A" "A" "A" "A" "A" "A")
w1=0.2
w2=0.2
w3=0.2
w4=0.2
w5=0.2
CPVBest=(`echo ${CPV[*]}`)
scalabilityBest=0
ct8Best=0
trendBest=0
scalabilityStart=0
flag=0

preThreshold=-1.5
scaThreshold=0.55
result="ineffective"

runBDCA() {
	echo "debug-info: start running BDCA with "$1" executors"
	startBDCATime=$(date)
	echo "debug-info: start running BDCA time: "${startBDCATime}
	echo "debug-info: start running BDCA time: "${startBDCATime} >> /home/jzd/SEP/backup/collectDataSolution.txt

	echo "debug-info: clean cache"
	./CleanCache.sh
	
	nodenum=$1
	nodenumprop="--num-executors "${nodenum}
	BDCAcmd="/home/ubuntu/Cloud/OpenStack/spark-2.4.0-bin-without-hadoop/bin/spark-submit --master yarn --deploy-mode cluster "${nodenumprop}" --executor-cores 1 "${CPVcmd}"/home/ubuntu/Cloud/OpenStack/nb_spark_output/"${jarfile}" /ubuntu/dataset/"${trainfile}" /ubuntu/dataset/"${testfile}" "${datasetType}" &> ~/Cloud/OpenStack/adaption/log.txt"
	echo ${BDCAcmd}
	ssh -o ServerAliveInterval=60 -i chadni.pem -tt ubuntu@10.33.184.57 << exitserver
		echo "debug-info: on server"
		${BDCAcmd}
		exit
exitserver
#	ssh -i chadni.pem ubuntu@10.33.184.57 ${BDCAcmd}


	scp -i chadni.pem ubuntu@10.33.184.57:/home/ubuntu/Cloud/OpenStack/adaption/log.txt /home/jzd/SEP/adaption/
	if ! grep -q "final status: SUCCEEDED" /home/jzd/SEP/adaption/log.txt;then
		echo "debug-error: BDCA final status not SUCCEEDED!!!"
		flag=-1
		return	
	fi
	echo "debug-info: finish BDCA with "$1" executors SUCCEEDED"
	endBDCATime=$(date)
	echo "debug-info: finish running BDCA time: "${endBDCATime}
	echo "debug-info: finish running BDCA time: "${endBDCATime} >> /home/jzd/SEP/backup/collectDataSolution.txt

	driver=$(grep "ApplicationMaster host" /home/jzd/SEP/adaption/log.txt | head -2)
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
	appId=$(grep "Application report for" /home/jzd/SEP/adaption/log.txt | head -1)
	appId=${appId#*_}
	appId=${appId% (*}
	echo "debug-info: appId: "${appId}

	getTimecmd="scp -i chadni.pem ubuntu@"${driver}":/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/yarn-log/application_"${appId}"/container_"${appId}"_01_000001/stdout /home/jzd/SEP/adaption/"

#	echo "debug-info: getTimecmd: "${getTimecmd}
#	if [ ! -f "/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/yarn-log/application_"${appId}"/container_"${appId}"_01_000001/stdout" ]; then
#		echo "debug-error: no stdout file!!!"
#		return
#	fi
	${getTimecmd}

	if [ "${responseTimeType}" == "train" ];then

		trainTime=$(grep "Train Time:" /home/jzd/SEP/adaption/stdout)
		if [ "${trainTime}" == "" ];then
			echo "debug-error: run BDCA with "$1" executors FAILED: No Train Time"
			flag=-1
			return
		else
			trainTime=${trainTime#*: }
			testTime=$(grep "Test Time:" /home/jzd/SEP/adaption/stdout)
			testTime=${testTime#*: }
		
			echo "debug-info: trainTime: "${trainTime}
			echo "debug-info: testTime: "${testTime}
			compFirst=`awk -v train="${trainArr[$1-1]}" -v a=-1 'BEGIN{print(train==a)?"1":"0"}'`
			if [ 1 -eq ${compFirst} ];then
				trainArr[$1-1]=${trainTime}
				testArr[$1-1]=${testTime}
			else
#				trainArr[$1-1]=${trainArr[$1-1]}+${trainTime}
#				testArr[$1-1]=${testArr[$1-1]}+${testTime}
				trainArr[$1-1]=`awk -v old="${trainArr[$1-1]}" -v trainTime="${trainTime}" 'BEGIN{printf "%.2f\n",old+trainTime}'`
				testArr[$1-1]=`awk -v old="${testArr[$1-1]}" -v testTime="${testTime}" 'BEGIN{printf "%.2f\n",old+testTime}'`
			fi
		fi
	else
		testTime=$(grep "Test Time:" /home/jzd/SEP/adaption/stdout)
		if [ "${testTime}" == "" ];then
			echo "debug-error: run BDCA with "$1" executors FAILED: No Test Time"
			flag=-1
			return
		else
			testTime=$(grep "Test Time:" /home/jzd/SEP/adaption/stdout)
			testTime=${testTime#*: }
		
			echo "debug-info: testTime: "${testTime}
			compFirst=`awk -v test="${testArr[$1-1]}" -v a=-1 'BEGIN{print(test==a)?"1":"0"}'`
			if [ 1 -eq ${compFirst} ];then
				testArr[$1-1]=${testTime}
			else
				testArr[$1-1]=`awk -v old="${testArr[$1-1]}" -v testTime="${testTime}" 'BEGIN{printf "%.2f\n",old+testTime}'`
			fi
		fi
	fi

	echo "debug-info: train time with 1 executor: "${trainArr[0]}
	echo "debug-info: train time with 2 executors: "${trainArr[1]}
	echo "debug-info: train time with 4 executors: "${trainArr[3]}
	echo "debug-info: train time with 6 executors: "${trainArr[5]}
	echo "debug-info: train time with 8 executors: "${trainArr[7]}
	echo "debug-info: test time with 1 executor: "${testArr[0]}
	echo "debug-info: test time with 2 executors: "${testArr[1]}
	echo "debug-info: test time with 4 executors: "${testArr[3]}
	echo "debug-info: test time with 6 executors: "${testArr[5]}
	echo "debug-info: test time with 8 executors: "${testArr[7]}
}

for ((i=-1; i<=8; i++))
do
	echo "debug-info: Start changing the P"${i}" param"
	if [ ${i} -ne -1 ];then
		CPV[${i}]="B"
	fi

	trainArr=(-1 -1 -1 -1 -1 -1 -1 -1)
	testArr=(-1 -1 -1 -1 -1 -1 -1 -1)
	
	CPVcmd=""
	for ((j=0; j<=7; j++))
	do
		if [ "${CPV[${j}]}" == "A" ];then
			CPVcmd=${CPVcmd}${params[${j}]}${paramVA[${j}]}" "
		else
			CPVcmd=${CPVcmd}${params[${j}]}${paramVB[${j}]}" "
		fi
	done



	if [ "${responseTimeType}" == "train" ];then
		for ((j=1; j<=3; j++))
		do
			runBDCA 6
			if [ -1 -eq ${flag} ];then
				echo "debug-error: next i"
				if [ ${i} -ne -1 ];then
					CPV[${i}]="A"
				fi
				break
			fi
		done
		if [ -1 -eq ${flag} ];then
			flag=0
			continue
		fi

		for ((j=1; j<=3; j++))
		do
			runBDCA 8
			if [ -1 -eq ${flag} ];then
				echo "debug-error: next i"
				if [ ${i} -ne -1 ];then
					CPV[${i}]="A"
				fi
				break
			fi
		done
		if [ -1 -eq ${flag} ];then
			flag=0
			continue
		fi

		for ((j=1; j<=3; j++))
		do
			runBDCA 1
			if [ -1 -eq ${flag} ];then
				echo "debug-error: next i"
				if [ ${i} -ne -1 ];then
					CPV[${i}]="A"
				fi
				break
			fi
		done
		if [ -1 -eq ${flag} ];then
			flag=0
			continue
		fi

		t1=`awk -v ct="${trainArr[0]}" -v a=3 'BEGIN{printf "%.2f\n",ct/a}'`
		t2=`awk -v x="${t1}" -v y=2 'BEGIN{printf "%.2f\n",x/y}'`
		t4=`awk -v x="${t1}" -v y=4 'BEGIN{printf "%.2f\n",x/y}'`
		t6=`awk -v x="${t1}" -v y=6 'BEGIN{printf "%.2f\n",x/y}'`
		t8=`awk -v x="${t1}" -v y=8 'BEGIN{printf "%.2f\n",x/y}'`
		ct1=`awk -v ct="${trainArr[0]}" -v a=3 'BEGIN{printf "%.2f\n",ct/a}'`
		ct6=`awk -v ct="${trainArr[5]}" -v a=3 'BEGIN{printf "%.2f\n",ct/a}'`
		ct8=`awk -v ct="${trainArr[7]}" -v a=3 'BEGIN{printf "%.2f\n",ct/a}'`
		trend=`awk -v ct6="${ct6}" -v ct8="${ct8}" -v t6="${t6}" -v t8="${t8}" 'BEGIN{printf "%.2f\n",(ct6-ct8)/(t6-t8)}'`
		echo "debug-info: pre trend: "${trend}
		compPreSca=`awk -v trend="${trend}" -v preThreshold="${preThreshold}" 'BEGIN{print(preThreshold>trend)?"1":"0"}'`
		if [ 1 -eq ${compPreSca} ];then
			if [ ${i} -ne -1 ];then
				echo "debug-info: pre_scalability failed"
				if [ ${i} -ne -1 ];then
					CPV[${i}]="A"
				fi
				continue
			fi
		fi

		for ((j=1; j<=3; j++))
		do
			runBDCA 2
			if [ -1 -eq ${flag} ];then
				echo "debug-error: next i"
				if [ ${i} -ne -1 ];then
					CPV[${i}]="A"
				fi
				break
			fi
		done
		if [ -1 -eq ${flag} ];then
			flag=0
			continue
		fi

		for ((j=1; j<=3; j++))
		do
			runBDCA 4
			if [ -1 -eq ${flag} ];then
				echo "debug-error: next i"
				if [ ${i} -ne -1 ];then
					CPV[${i}]="A"
				fi
				break
			fi
		done
		if [ -1 -eq ${flag} ];then
			flag=0
			continue
		fi

	#	trainArr=(395477 340914 -1 368373 -1 308035 -1 331717)


		ct2=`awk -v ct="${trainArr[1]}" -v a=3 'BEGIN{printf "%.2f\n",ct/a}'`
		ct4=`awk -v ct="${trainArr[3]}" -v a=3 'BEGIN{printf "%.2f\n",ct/a}'`

	else

		for ((j=1; j<=10; j++))
		do
			runBDCA 6
			if [ -1 -eq ${flag} ];then
				echo "debug-error: next i"
				if [ ${i} -ne -1 ];then
					CPV[${i}]="A"
				fi
				break
			fi
		done
		if [ -1 -eq ${flag} ];then
			flag=0
			continue
		fi

		for ((j=1; j<=10; j++))
		do
			runBDCA 8
			if [ -1 -eq ${flag} ];then
				echo "debug-error: next i"
				if [ ${i} -ne -1 ];then
					CPV[${i}]="A"
				fi
				break
			fi
		done
		if [ -1 -eq ${flag} ];then
			flag=0
			continue
		fi

		for ((j=1; j<=10; j++))
		do
			runBDCA 1
			if [ -1 -eq ${flag} ];then
				echo "debug-error: next i"
				if [ ${i} -ne -1 ];then
					CPV[${i}]="A"
				fi
				break
			fi
		done
		if [ -1 -eq ${flag} ];then
			flag=0
			continue
		fi

		t1=`awk -v ct="${testArr[0]}" -v a=10 'BEGIN{printf "%.2f\n",ct/a}'`
		t2=`awk -v x="${t1}" -v y=2 'BEGIN{printf "%.2f\n",x/y}'`
		t4=`awk -v x="${t1}" -v y=4 'BEGIN{printf "%.2f\n",x/y}'`
		t6=`awk -v x="${t1}" -v y=6 'BEGIN{printf "%.2f\n",x/y}'`
		t8=`awk -v x="${t1}" -v y=8 'BEGIN{printf "%.2f\n",x/y}'`
		ct1=`awk -v ct="${testArr[0]}" -v a=10 'BEGIN{printf "%.2f\n",ct/a}'`
		ct6=`awk -v ct="${testArr[5]}" -v a=10 'BEGIN{printf "%.2f\n",ct/a}'`
		ct8=`awk -v ct="${testArr[7]}" -v a=10 'BEGIN{printf "%.2f\n",ct/a}'`
		trend=`awk -v ct6="${ct6}" -v ct8="${ct8}" -v t6="${t6}" -v t8="${t8}" 'BEGIN{printf "%.2f\n",(ct6-ct8)/(t6-t8)}'`
		echo "debug-info: pre trend: "${trend}
		compPreSca=`awk -v trend="${trend}" -v preThreshold="${preThreshold}" 'BEGIN{print(preThreshold>trend)?"1":"0"}'`
		if [ 1 -eq ${compPreSca} ];then
			if [ ${i} -ne -1 ];then
				echo "debug-info: pre_scalability failed"
				if [ ${i} -ne -1 ];then
					CPV[${i}]="A"
				fi
				continue
			fi
		fi

		for ((j=1; j<=10; j++))
		do
			runBDCA 2
			if [ -1 -eq ${flag} ];then
				echo "debug-error: next i"
				if [ ${i} -ne -1 ];then
					CPV[${i}]="A"
				fi
				break
			fi
		done
		if [ -1 -eq ${flag} ];then
			flag=0
			continue
		fi


		for ((j=1; j<=10; j++))
		do
			runBDCA 4
			if [ -1 -eq ${flag} ];then
				echo "debug-error: next i"
				if [ ${i} -ne -1 ];then
					CPV[${i}]="A"
				fi
				break
			fi
		done
		if [ -1 -eq ${flag} ];then
			flag=0
			continue
		fi

	#	trainArr=(395477 340914 -1 368373 -1 308035 -1 331717)

		ct2=`awk -v ct="${testArr[1]}" -v a=10 'BEGIN{printf "%.2f\n",ct/a}'`
		ct4=`awk -v ct="${testArr[3]}" -v a=10 'BEGIN{printf "%.2f\n",ct/a}'`
	fi


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

	if [ ${i} -eq -1 ];then
		scalabilityStart=${scalability}
	fi

	echo ${CPV[*]} >> /home/jzd/SEP/backup/collectDataSolution.txt
	echo ${trainArr[*]} >> /home/jzd/SEP/backup/collectDataSolution.txt
	echo ${testArr[*]} >> /home/jzd/SEP/backup/collectDataSolution.txt
	echo ${scalability} >> /home/jzd/SEP/backup/collectDataSolution.txt


	compSca=`awk -v scalability="${scalability}" -v scalabilityBest="${scalabilityBest}" 'BEGIN{print(scalability>scalabilityBest)?"1":"0"}'`
	if [ 1 -eq ${compSca} ];then
		echo "debug-info: scalability: "${scalability}" better than scalabilityBest: "${scalabilityBest}
		scalabilityBest=${scalability}
		CPVBest=(`echo ${CPV[*]}`)
		ct8Best=${ct8}
		trendBest=${trend}
		echo "debug-info: current scalabilityBest: "${scalabilityBest}
		echo "debug-info: current CPVBest: "${CPVBest[*]}

		compTh=`awk -v scaThreshold="${scaThreshold}" -v scalabilityBest="${scalabilityBest}" 'BEGIN{print(scalabilityBest>scaThreshold)?"1":"0"}'`
		if [ 1 -eq ${compTh} ];then
			result="effective"
			break
		fi
	else
		if [ ${i} -ne -1 ];then
			CPV[${i}]="A"
		fi

	fi
done

endTime=$(date)

echo "debug-info RESULT: start adaption time: "${startTime}
echo "debug-info RESULT: finish adaption time: "${endTime}

echo "debug-info RESULT: result: "${result}
echo "debug-info RESULT: final scalabilityBest: "${scalabilityBest}
echo "debug-info RESULT: final scalabilityStart: "${scalabilityStart}
improvement=`awk -v scalabilityBest="${scalabilityBest}" -v scalabilityStart="${scalabilityStart}" 'BEGIN{printf "%.2f\n",scalabilityBest-scalabilityStart}'`
echo "debug-info RESULT: improvement: "${improvement}
echo "debug-info RESULT: final CPVBest: "${CPVBest[*]}

echo "result for: "${jarfile}" trainfile: "${trainfile}" testfile: "${testfile}" responseTimeType: "${responseTimeType} >> /home/jzd/SEP/backup/collectDataSolution.txt

echo ${CPVBest[*]} >> /home/jzd/SEP/backup/collectDataSolution.txt
echo ${startTime} >> /home/jzd/SEP/backup/collectDataSolution.txt
echo ${endTime} >> /home/jzd/SEP/backup/collectDataSolution.txt
echo ${scalabilityStart} >> /home/jzd/SEP/backup/collectDataSolution.txt
echo ${scalabilityBest} >> /home/jzd/SEP/backup/collectDataSolution.txt


