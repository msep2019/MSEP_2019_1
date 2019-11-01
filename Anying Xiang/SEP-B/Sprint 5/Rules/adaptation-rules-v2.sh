#!/usr/bin/env bash

# ./adaptation-rules-v2.sh /input/NSLKDD/training/pre-KDDTrain+2.csv /input/NSLKDD/testing/pre-KDDTest+2.csv sep_spark_dt_nslkdd_cv5_v1.jar

current=`date "+%Y-%m-%d %H:%M:%S"`
time=`date -d "$current" +%s` 
startTimeStamp=$((time*1000+`date "+%N"`/1000000))
echo 'Start adaptation time: '$startTimeStamp

echo "Train file: $1"
echo "Test file: $2"
echo "Jar file: $3"

trainfile=$1
testfile=$2
jarfile=$3

columnNo=`hadoop fs -cat $testfile | head -1 | sed 's/[^,]//g' | wc -c`
featureNo=$((columnNo - 1))

echo "featureNo: $featureNo"

trainInstanceNo=`hadoop fs -cat $trainfile | wc -l`
testInstanceNo=`hadoop fs -cat $testfile | wc -l`

instanceNo=$((trainInstanceNo + testInstanceNo))

echo "trainInstanceNo: $trainInstanceNo"
echo "testInstanceNo: $testInstanceNo"
echo "instanceNo: $instanceNo"

# featureNo=100
# instanceNo=7000000

featureMemFuncVal=0
featureMemFuncSmallPercentage=0
featureMemFuncMediumPercentage=0
featureMemFuncLargePercentage=0

if (( $featureNo < 20 )); then
	featureMemFunc=1
	featureMemFuncSmallPercentage=1
	featureMemFuncMediumPercentage=0
	featureMemFuncLargePercentage=0
fi
if (( $featureNo >= 20 )) && (( $featureNo <= 40 )); then
	featureMemFunc=`awk -v a=1 -v b=20 -v c=-1 -v x="${featureNo}" 'BEGIN{printf "%.4f\n",a/b*x+c}'`
	featureMemFuncMediumPercentage=$featureMemFunc

	featureMemFunc=`awk -v a=-1 -v b=20 -v c=2 -v x="${featureNo}" 'BEGIN{printf "%.4f\n",a/b*x+c}'`
	featureMemFuncSmallPercentage=$featureMemFunc
fi
if (( $featureNo > 40 )) && (( $featureNo <= 60 )); then
	featureMemFunc=`awk -v a=-1 -v b=20 -v c=3 -v x="${featureNo}" 'BEGIN{printf "%.4f\n",a/b*x+c}'`
	featureMemFuncMediumPercentage=$featureMemFunc

	featureMemFunc=`awk -v a=1 -v b=20 -v c=-2 -v x="${featureNo}" 'BEGIN{printf "%.4f\n",a/b*x+c}'`
	featureMemFuncLargePercentage=$featureMemFunc
fi
if (( $featureNo > 60 )); then
	featureMemFunc=1
	featureMemFuncSmallPercentage=0
	featureMemFuncMediumPercentage=0
	featureMemFuncLargePercentage=1
fi

featureMemFuncSmallorMediumPercentage=`awk -v featureMemFuncSmallPercentage="${featureMemFuncSmallPercentage}" -v featureMemFuncMediumPercentage="${featureMemFuncMediumPercentage}" 'BEGIN{printf "%.4f\n",featureMemFuncSmallPercentage+featureMemFuncMediumPercentage}'`

echo "featureMemFuncSmallorMediumPercentage: $featureMemFuncSmallorMediumPercentage"
echo "featureMemFuncLargePercentage: $featureMemFuncLargePercentage"


instanceMemFuncVal=0
instanceMemFuncSmallPercentage=0
instanceMemFuncMediumPercentage=0
instanceMemFuncLargePercentage=0

if (( $instanceNo < 1500000 )); then
	instanceMemFunc=1
	instanceMemFuncSmallPercentage=1
	instanceMemFuncMediumPercentage=0
	instanceMemFuncLargePercentage=0
fi
if (( $instanceNo >= 1500000 )) && (( $instanceNo <= 3000000 )); then
	instanceMemFunc=`awk -v a=1 -v b=1500000 -v c=-1 -v x="${instanceNo}" 'BEGIN{printf "%.4f\n",a/b*x+c}'`
	instanceMemFuncMediumPercentage=$instanceMemFunc

	instanceMemFunc=`awk -v a=-1 -v b=1500000 -v c=2 -v x="${instanceNo}" 'BEGIN{printf "%.4f\n",a/b*x+c}'`
	instanceMemFuncSmallPercentage=$instanceMemFunc
fi
if (( $instanceNo > 3000000 )) && (( $instanceNo <= 4500000 )); then
	instanceMemFunc=`awk -v a=-1 -v b=1500000 -v c=3 -v x="${instanceNo}" 'BEGIN{printf "%.4f\n",a/b*x+c}'`
	instanceMemFuncMediumPercentage=$instanceMemFunc

	instanceMemFunc=`awk -v a=1 -v b=1500000 -v c=-2 -v x="${instanceNo}" 'BEGIN{printf "%.4f\n",a/b*x+c}'`
	instanceMemFuncLargePercentage=$instanceMemFunc
fi
if (( $instanceNo > 4500000 )); then
	instanceMemFunc=1
	instanceMemFuncSmallPercentage=0
	instanceMemFuncMediumPercentage=0
	instanceMemFuncLargePercentage=1
fi

instanceMemFuncSmallorMediumPercentage=`awk -v instanceMemFuncSmallPercentage="${instanceMemFuncSmallPercentage}" -v instanceMemFuncMediumPercentage="${instanceMemFuncMediumPercentage}" 'BEGIN{printf "%.4f\n",instanceMemFuncSmallPercentage+instanceMemFuncMediumPercentage}'`

echo "instanceMemFuncSmallorMediumPercentage: $instanceMemFuncSmallorMediumPercentage"
echo "instanceMemFuncLargePercentage: $instanceMemFuncLargePercentage"


minimum=(0 0 0 0)
minimum[0]=`awk -v featureMemFuncSmallorMediumPercentage="${featureMemFuncSmallorMediumPercentage}" -v instanceMemFuncSmallorMediumPercentage="${instanceMemFuncSmallorMediumPercentage}" 'BEGIN{printf "%.4f\n",featureMemFuncSmallorMediumPercentage*instanceMemFuncSmallorMediumPercentage}'`

minimum[1]=`awk -v featureMemFuncSmallorMediumPercentage="${featureMemFuncSmallorMediumPercentage}" -v instanceMemFuncLargePercentage="${instanceMemFuncLargePercentage}" 'BEGIN{printf "%.4f\n",featureMemFuncSmallorMediumPercentage*instanceMemFuncLargePercentage}'`

minimum[2]=`awk -v featureMemFuncLargePercentage="${featureMemFuncLargePercentage}" -v instanceMemFuncSmallorMediumPercentage="${instanceMemFuncSmallorMediumPercentage}" 'BEGIN{printf "%.4f\n",featureMemFuncLargePercentage*instanceMemFuncSmallorMediumPercentage}'`

minimum[3]=`awk -v featureMemFuncLargePercentage="${featureMemFuncLargePercentage}" -v instanceMemFuncLargePercentage="${instanceMemFuncLargePercentage}" 'BEGIN{printf "%.4f\n",featureMemFuncLargePercentage*instanceMemFuncLargePercentage}'`

echo ${minimum[*]}


params=("--executor-cores" "--conf spark.io.compression.codec=" "--conf spark.serializer=" "--conf spark.io.compression.lz4.blockSize=" "--conf spark.shuffle.spill.compress=" "--conf spark.reducer.maxSizeInFlight=" "--conf spark.shuffle.file.buffer=" "--conf spark.shuffle.compress=" "--conf spark.broadcast.blockSize=" "--conf spark.locality.wait=" "--conf spark.memory.fraction=" "--conf spark.memory.storageFraction=")
paramVA=(" 1" "lz4" "org.apache.spark.serializer.JavaSerializer" "32k" "true" "48m" "32k" "true" "4m" "3s" "0.6" "0.5")
paramVB=(" 2" "lzf" "org.apache.spark.serializer.KryoSerializer" "16k" "false" "24m" "16k" "false" "2m" "1s" "0.4" "0.3")
paramVC=("" "snappy" "" "64k" "" "72m" "48k" "" "8m" "5s" "0.8" "0.7")

# RULES
rules=()
rules=("B" "B" "B" "B" "" "C" "B" "C" "A" "" "B" "B" "A" "A" "" "C" "B" "A" "A" "" "B" "B" "A" "A" "" "B" "C" "A" "A" "" "B" "B" "A" "A" "" "B" "B" "A" "B" "" "B" "C" "B" "A" "" "B" "B" "B" "A" "" "C" "C" "C" "A" "" "C" "B" "C" "A" "")

CPVs=("" "" "" "" "" "" "" "" "" "" "" "")
echo ""
for ((i=0; i<=11; i++))
do
	num=$((i + 1))
	echo "combine the results: (parameter-"${num}")"
	
	Acnt=()
	Bcnt=()
	Ccnt=()
	for ((j=0; j<=3; j++))
	do
		# echo "${i}*5+${j}: "${rules[${i}*5+${j}]}
		if [ "${rules[${i}*5+${j}]}" == "A" ];then
			Acnt+=(${minimum[${j}]})
			
		elif [ "${rules[${i}*5+${j}]}" == "B" ];then
			Bcnt+=(${minimum[${j}]})
			
		elif [ "${rules[${i}*5+${j}]}" == "C" ];then
			Ccnt+=(${minimum[${j}]})
			
		fi
	done
	

	paramName=${params[${i}]}
	
	echo "Acnt: "${Acnt[*]} 
	echo "Bcnt: "${Bcnt[*]} 
	echo "Ccnt: "${Ccnt[*]} 
	x=0
	numerator=0
	for ((j=0; j<${#Acnt[@]}; j++))
	do
		temp=`awk -v a="${Acnt[${j}]}" -v b=0 'BEGIN{printf "%.4f\n",a*b}'`
		x=`awk -v x="${x}" -v temp="${temp}" 'BEGIN{printf "%.4f\n",x+temp}'`
		
		numerator=`awk -v numerator="${numerator}" -v a="${Acnt[${j}]}" 'BEGIN{printf "%.4f\n",numerator+a}'`
	done
	for ((j=0; j<${#Bcnt[@]}; j++))
	do
		temp=`awk -v a="${Bcnt[${j}]}" -v b=0.5 'BEGIN{printf "%.4f\n",a*b}'`
		x=`awk -v x="${x}" -v temp="${temp}" 'BEGIN{printf "%.4f\n",x+temp}'`
		
		numerator=`awk -v numerator="${numerator}" -v a="${Bcnt[${j}]}" 'BEGIN{printf "%.4f\n",numerator+a}'`
	done
	for ((j=0; j<${#Ccnt[@]}; j++))
	do
		temp=`awk -v a="${Ccnt[${j}]}" -v b=-0.5 'BEGIN{printf "%.4f\n",a*b}'`
		x=`awk -v x="${x}" -v temp="${temp}" 'BEGIN{printf "%.4f\n",x+temp}'`
		
		numerator=`awk -v numerator="${numerator}" -v a="${Ccnt[${j}]}" 'BEGIN{printf "%.4f\n",numerator+a}'`
	done
	echo $x
	echo ${numerator}
	centroid=`awk -v x="$x" -v numerator="${numerator}" 'BEGIN{printf "%.4f\n",x/numerator}'`

	selected=${paramVA[${i}]}
	selectedNo="A"
	comp1=`awk -v centroid="${centroid}" -v val=0.25 'BEGIN{print(centroid>val)?"1":"0"}'`
	if [ 1 -eq ${comp1} ];then
		selected=${paramVB[${i}]}
		selectedNo="B"
	fi
	comp1=`awk -v centroid="${centroid}" -v val=-0.25 'BEGIN{print(centroid<val)?"1":"0"}'`
	if [ 1 -eq ${comp1} ];then
		selected=${paramVC[${i}]}
		selectedNo="C"
	fi
	

	# tune the (i-1)th parameter
	CPVs[${i}]=${selectedNo}

	echo "fuzzy centroid: "${paramName}${selected}" at "${centroid}
	echo ""
done

echo "tuned parameters: "${CPVs[*]}

current=`date "+%Y-%m-%d %H:%M:%S"`
time=`date -d "$current" +%s` 
endTimeStamp=$((time*1000+`date "+%N"`/1000000))
echo 'End adaptation time: '$endTimeStamp

duration=$((endTimeStamp - startTimeStamp))
echo 'Duration: '$duration' ms'

params=("--executor-cores" "--conf spark.io.compression.codec=" "--conf spark.serializer=" "--conf spark.io.compression.lz4.blockSize=" "--conf spark.shuffle.spill.compress=" "--conf spark.reducer.maxSizeInFlight=" "--conf spark.shuffle.file.buffer=" "--conf spark.shuffle.compress=" "--conf spark.broadcast.blockSize=" "--conf spark.locality.wait=" "--conf spark.memory.fraction=" "--conf spark.memory.storageFraction=")
paramVA=(" 1" "lz4" "org.apache.spark.serializer.JavaSerializer" "32k" "true" "48m" "32k" "true" "4m" "3s" "0.6" "0.5")
paramVB=(" 2" "lzf" "org.apache.spark.serializer.KryoSerializer" "16k" "false" "24m" "16k" "false" "2m" "1s" "0.4" "0.3")
paramVC=("" "snappy" "" "64k" "" "72m" "48k" "" "8m" "5s" "0.8" "0.7")


CPVcmd=""
for ((j=0; j<=11; j++))
do
	if [ "${CPVs[${j}]}" == "A" ];then
		echo "${j}: A"
		CPVcmd=${CPVcmd}${params[${j}]}${paramVA[${j}]}" "
	elif [ "${CPVs[${j}]}" == "B" ];then
		echo "${j}: B"
		CPVcmd=${CPVcmd}${params[${j}]}${paramVB[${j}]}" "
	else
		echo "${j}: C"
		CPVcmd=${CPVcmd}${params[${j}]}${paramVC[${j}]}" "
	fi
done

BDCAcmd="/home/ubuntu/Cloud/OpenStack/spark-2.4.0-bin-without-hadoop/bin/spark-submit --master yarn --deploy-mode cluster --num-executors 3 "${CPVcmd}"/home/ubuntu/Cloud/OpenStack/nb_spark_output/"${jarfile}
${BDCAcmd}

