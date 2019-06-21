#!/usr/bin/env bash

runBDCA() {
	echo "debug-info: start running BDCA with "$1" executors"
	nodenum=$1
	BDCAcmd="hadoop jar /home/ubuntu/Cloud/OpenStack/nb_hadoop/hadoop_kdd_v5.jar > /home/ubuntu/Cloud/OpenStack/output/output_kdd_"$1".txt 2>&1"
	ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	    echo "debug-info: on server"
	    ${BDCAcmd}
	    exit
exitserver

	scp -i hadoopkey.pem ubuntu@10.33.184.100:/home/ubuntu/Cloud/OpenStack/output/output_kdd_"$1".txt /home/jzd/SEP/backupHadoop/output_kdd.txt

	trainTime=$(grep "Train Time:" /home/jzd/SEP/backupHadoop/output_kdd.txt)
	trainTime=${trainTime#*: }
	testTime=$(grep "Test Time:" /home/jzd/SEP/backupHadoop/output_kdd.txt)
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

	echo ${trainArr[*]}
	echo ${testArr[*]}
}


trainArr=(-1 -1 -1 -1 -1 -1 -1 -1 -1)
testArr=(-1 -1 -1 -1 -1 -1 -1 -1 -1)


ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "clear dfs"
	hadoop fs -rm -r /ubuntu

	echo "stop dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-yarn.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-dfs.sh

	exit
exitserver

echo "change slaves"
scp -i hadoopkey.pem /home/jzd/SEP/backupHadoop/slaves1 ubuntu@10.33.184.100:/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/etc/hadoop/slaves

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "start dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-dfs.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-yarn.sh

	echo "add dfs"
	hadoop dfsadmin -safemode leave
	hadoop fs -mkdir /ubuntu
	hadoop fs -mkdir /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTrain.txt /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTest.txt /ubuntu/input

	exit
exitserver

for ((j=1; j<=10; j++))
do
	runBDCA 1
done

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "clear dfs"
	hadoop fs -rm -r /ubuntu

	echo "stop dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-yarn.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-dfs.sh

	exit
exitserver

echo "change slaves"
scp -i hadoopkey.pem /home/jzd/SEP/backupHadoop/slaves2 ubuntu@10.33.184.100:/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/etc/hadoop/slaves

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "start dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-dfs.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-yarn.sh

	echo "add dfs"
	hadoop dfsadmin -safemode leave
	hadoop fs -mkdir /ubuntu
	hadoop fs -mkdir /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTrain.txt /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTest.txt /ubuntu/input

	exit
exitserver

for ((j=1; j<=10; j++))
do
	runBDCA 2
done

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "clear dfs"
	hadoop fs -rm -r /ubuntu

	echo "stop dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-yarn.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-dfs.sh

	exit
exitserver

echo "change slaves"
scp -i hadoopkey.pem /home/jzd/SEP/backupHadoop/slaves3 ubuntu@10.33.184.100:/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/etc/hadoop/slaves

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "start dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-dfs.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-yarn.sh

	echo "add dfs"
	hadoop dfsadmin -safemode leave
	hadoop fs -mkdir /ubuntu
	hadoop fs -mkdir /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTrain.txt /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTest.txt /ubuntu/input

	exit
exitserver

for ((j=1; j<=10; j++))
do
	runBDCA 3
done

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "clear dfs"
	hadoop fs -rm -r /ubuntu

	echo "stop dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-yarn.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-dfs.sh

	exit
exitserver

echo "change slaves"
scp -i hadoopkey.pem /home/jzd/SEP/backupHadoop/slaves4 ubuntu@10.33.184.100:/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/etc/hadoop/slaves

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "start dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-dfs.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-yarn.sh

	echo "add dfs"
	hadoop dfsadmin -safemode leave
	hadoop fs -mkdir /ubuntu
	hadoop fs -mkdir /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTrain.txt /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTest.txt /ubuntu/input

	exit
exitserver

for ((j=1; j<=10; j++))
do
	runBDCA 4
done

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "clear dfs"
	hadoop fs -rm -r /ubuntu

	echo "stop dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-yarn.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-dfs.sh

	exit
exitserver

echo "change slaves"
scp -i hadoopkey.pem /home/jzd/SEP/backupHadoop/slaves5 ubuntu@10.33.184.100:/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/etc/hadoop/slaves

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "start dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-dfs.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-yarn.sh

	echo "add dfs"
	hadoop dfsadmin -safemode leave
	hadoop fs -mkdir /ubuntu
	hadoop fs -mkdir /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTrain.txt /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTest.txt /ubuntu/input

	exit
exitserver

for ((j=1; j<=10; j++))
do
	runBDCA 5
done

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "clear dfs"
	hadoop fs -rm -r /ubuntu

	echo "stop dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-yarn.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-dfs.sh

	exit
exitserver

echo "change slaves"
scp -i hadoopkey.pem /home/jzd/SEP/backupHadoop/slaves6 ubuntu@10.33.184.100:/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/etc/hadoop/slaves

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "start dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-dfs.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-yarn.sh

	echo "add dfs"
	hadoop dfsadmin -safemode leave
	hadoop fs -mkdir /ubuntu
	hadoop fs -mkdir /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTrain.txt /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTest.txt /ubuntu/input

	exit
exitserver

for ((j=1; j<=10; j++))
do
	runBDCA 6
done

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "clear dfs"
	hadoop fs -rm -r /ubuntu

	echo "stop dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-yarn.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-dfs.sh

	exit
exitserver

echo "change slaves"
scp -i hadoopkey.pem /home/jzd/SEP/backupHadoop/slaves7 ubuntu@10.33.184.100:/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/etc/hadoop/slaves

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "start dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-dfs.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-yarn.sh

	echo "add dfs"
	hadoop dfsadmin -safemode leave
	hadoop fs -mkdir /ubuntu
	hadoop fs -mkdir /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTrain.txt /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTest.txt /ubuntu/input

	exit
exitserver

for ((j=1; j<=10; j++))
do
	runBDCA 7
done

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "clear dfs"
	hadoop fs -rm -r /ubuntu

	echo "stop dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-yarn.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-dfs.sh

	exit
exitserver

echo "change slaves"
scp -i hadoopkey.pem /home/jzd/SEP/backupHadoop/slaves8 ubuntu@10.33.184.100:/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/etc/hadoop/slaves

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "start dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-dfs.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-yarn.sh

	echo "add dfs"
	hadoop dfsadmin -safemode leave
	hadoop fs -mkdir /ubuntu
	hadoop fs -mkdir /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTrain.txt /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTest.txt /ubuntu/input

	exit
exitserver

for ((j=1; j<=10; j++))
do
	runBDCA 8
done

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "clear dfs"
	hadoop fs -rm -r /ubuntu

	echo "stop dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-yarn.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/stop-dfs.sh

	exit
exitserver

echo "change slaves"
scp -i hadoopkey.pem /home/jzd/SEP/backupHadoop/slaves9 ubuntu@10.33.184.100:/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/etc/hadoop/slaves

ssh -o ServerAliveInterval=60 -i hadoopkey.pem -tt ubuntu@10.33.184.100 << exitserver
	echo "start dfs"
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-dfs.sh
	/home/ubuntu/Cloud/OpenStack/hadoop-2.9.2/sbin/start-yarn.sh

	echo "add dfs"
	hadoop dfsadmin -safemode leave
	hadoop fs -mkdir /ubuntu
	hadoop fs -mkdir /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTrain.txt /ubuntu/input
	hadoop fs -put /home/ubuntu/Cloud/OpenStack/DS/KDDTest.txt /ubuntu/input

	exit
exitserver

for ((j=1; j<=10; j++))
do
	runBDCA 9
done

echo ${trainArr[*]}
echo ${testArr[*]}
