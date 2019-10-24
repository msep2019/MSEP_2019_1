#!/bin/sh

#This is a script for getting utilization for each node (i.e. CPU, training time, I/O etc.)

#usage: ./get_utilization.sh 
#example: nohup ./get_utilization.sh &

#Parameter setting

#PARAM_PATH="/home/ubuntu/hadoop/scripts/param"
#LOG_PATH="/home/ubuntu/hadoop/scripts/log"
#JOB_LOGS="/home/ubuntu/hadoop/scripts/temp/joblogs"

#worker=` echo $1 | tr 'a-z' 'A-Z'`

#if [ "${worker}" = "MASTER" ]; then
# STAT_PATH="/home/ubuntu/hadoop/scripts"
#else
# STAT_PATH="/home/ubuntu/scripts"
#fi

PARAM_PATH="/home/hadoop/Desktop/scripts_hadoop/param"
. $PARAM_PATH/env.param

NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`

timestamp=`date "+%Y%m%d%H%M%S%s"`
LOG_FILE="${LOG_PATH}/${NAME}_${timestamp}.log"

Jobtime="${TIME_PATH}/Job_Timestamp.log"
temp="${TIME_PATH}/temp_timestamp.txt"

exec 1>>${LOG_FILE}
exec 2>>${LOG_FILE}

#ACTUAL_CHANGE_PARAM="${PARAM_PATH}/actual_change.param"
#ALL_CONF_PARAM="${PARAM_PATH}/all_conf.param"
#RUN_AL_PARAM="${PARAM_PATH}/algorithm.param"

# download statstic from clusters

isempty=` ls -A ${STATISTIC_PATH}|wc -w `  # whether file exist

if [ ${isempty} -eq 0 ]; then

 echo "download statstic from clusters.."

 IP=`cat $PARAM_PATH/node.param | grep "MASTER" | cut -d ":" -f2`
 scpcmd=`scp -i ${KEY_PATH}/keypair1.pem ${USER_NAME}@${IP}:${MASTER_SCR_PATH}/statsitic_collection_worker_master.csv ${STATISTIC_PATH}`
 scpcmd=`scp -i ${KEY_PATH}/keypair1.pem ${USER_NAME}@${IP}:${TIME_STAM_PATH}/*.log ${TIME_PATH}`

 IP=`cat $PARAM_PATH/node.param | grep "NODE1:" | cut -d ":" -f2`
 scpcmd=`scp -i ${KEY_PATH}/keypair1.pem ${USER_NAME}@${IP}:${CLUSTER_SCR_PATH}/statsitic_collection_worker_1.csv ${STATISTIC_PATH}`

 IP=`cat $PARAM_PATH/node.param | grep "NODE2:" | cut -d ":" -f2`
 scpcmd=`scp -i ${KEY_PATH}/keypair1.pem ${USER_NAME}@${IP}:${CLUSTER_SCR_PATH}/statsitic_collection_worker_2.csv ${STATISTIC_PATH}`

 IP=`cat $PARAM_PATH/node.param | grep "NODE3:" | cut -d ":" -f2`
 scpcmd=`scp -i ${KEY_PATH}/keypair1.pem ${USER_NAME}@${IP}:${CLUSTER_SCR_PATH}/statsitic_collection_worker_3.csv ${STATISTIC_PATH}`

 IP=`cat $PARAM_PATH/node.param | grep "NODE6:" | cut -d ":" -f2`
 scpcmd=`scp -i ${KEY_PATH}/keypair1.pem ${USER_NAME}@${IP}:${CLUSTER_SCR_PATH}/statsitic_collection_worker_6.csv ${STATISTIC_PATH}`

 IP=`cat $PARAM_PATH/node.param | grep "NODE7:" | cut -d ":" -f2`
 scpcmd=`scp -i ${KEY_PATH}/keypair1.pem ${USER_NAME}@${IP}:${CLUSTER_SCR_PATH}/statsitic_collection_worker_7.csv ${STATISTIC_PATH}`

 IP=`cat $PARAM_PATH/node.param | grep "NODE8:" | cut -d ":" -f2`
 scpcmd=`scp -i ${KEY_PATH}/keypair1.pem ${USER_NAME}@${IP}:${CLUSTER_SCR_PATH}/statsitic_collection_worker_8.csv ${STATISTIC_PATH}`

 IP=`cat $PARAM_PATH/node.param | grep "NODE9:" | cut -d ":" -f2`
 scpcmd=`scp -i ${KEY_PATH}/keypair1.pem ${USER_NAME}@${IP}:${CLUSTER_SCR_PATH}/statsitic_collection_worker_9.csv ${STATISTIC_PATH}`

 IP=`cat $PARAM_PATH/node.param | grep "NODE10:" | cut -d ":" -f2`
 scpcmd=`scp -i ${KEY_PATH}/keypair1.pem ${USER_NAME}@${IP}:${CLUSTER_SCR_PATH}/statsitic_collection_worker_10.csv ${STATISTIC_PATH}`

 echo "end download statstic from clusters.."

else 
 echo "statistic files already exist."
fi


# end download statstic from clusters


# collect and calculate statistic

echo "Calculate statistics."

files=$( ls ${STATISTIC_PATH} )

for filename in ${files}
do

worker=` echo ${filename} | cut -d "." -f1 | cut -d "_" -f4`

echo "calculating from ${worker} node... "

STATIS_FILE="${STATISTIC_PATH}/${filename}"
utilization_file="/home/hadoop/Desktop/scripts_hadoop/temp/utilization/Utilization_${worker}.txt"
`rm ${utilization_file}`

echo "STATIS_FILE is ${STATIS_FILE}"
echo "utilization_file is ${utilization_file}"

for AL in `cat ${CL_AL_PARAM}`
do

 algorithm=`echo ${AL} | cut -d "-" -f1`
 dataset=`echo ${AL} | cut -d "-" -f2`

 echo "get statistic for ${dataset} dataset with ${algorithm} algorithm."

    default_prefix="<Default>\[${algorithm}\]\s\[${dataset}\]"

    echo "  "
    echo "default prefix is <Default>[${algorithm}] [${dataset}]"
    echo "collecting statistic..."

    detraingDate=` cat ${Jobtime} | grep ${default_prefix} | grep "Date is" | cut -d " " -f5 `
    detrainStart=` cat ${Jobtime} | grep ${default_prefix} | grep "training start time" | cut -d " " -f7 `
    detrainEnd=` cat ${Jobtime} | grep ${default_prefix} | grep "training end time" | cut -d " " -f7 `
    detrainTtl=` cat ${Jobtime} | grep ${default_prefix} | grep "training time is" | cut -d " " -f6 `
    detestTtl=` cat ${Jobtime} | grep ${default_prefix} | grep "testing time is" | cut -d " " -f6 `
 
    Job1Start=` cat ${Jobtime} | grep ${default_prefix} | grep "mapreduce job1 start time" | cut -d "=" -f2 `
    Job1End=` cat ${Jobtime} | grep ${default_prefix} | grep "mapreduce job1 end time" | cut -d "=" -f2 `
    Job2Start=` cat ${Jobtime} | grep ${default_prefix} | grep "mapreduce job2 start time" | cut -d "=" -f2 `
    Job2End=` cat ${Jobtime} | grep ${default_prefix} | grep "mapreduce job2 end time" | cut -d "=" -f2 `

#echo "Job1Start = ${Job1Start}"
#echo "Job1End = ${Job1End}"
#echo "Job2Start = ${Job2Start}"
#echo "Job2End = ${Job2End}"

if [ -z "${detrainEnd}" ]; then
 echo " "
 echo "<default> <${algorithm}> <${dataset}>"
 echo "there is no statistic in this setting, the job may fail when run with this property, please check log."
 echo "<default> <${algorithm}> <${dataset}> BLANK " >> ${utilization_file}
 echo " "
else
    deTrstart="${detraingDate} ${detrainStart}"
    deTrend="${detraingDate} ${detrainEnd}"
#    Trstart="${traingDate} ${trainStart}"
#    Trend="${traingDate} ${trainEnd}"
    deTaskst1="${detraingDate} ${Job1Start}"
    deTasken1="${detraingDate} ${Job1End}"
    deTaskst2="${detraingDate} ${Job2Start}"
    deTasken2="${detraingDate} ${Job2End}"

    startrow=` cat -n ${STATIS_FILE} | grep "${deTaskst1}" | awk '{print $1}' `
    endrow=` cat -n ${STATIS_FILE} | grep "${deTasken2}" | awk '{print $1}' `
    pass=` sed -n "${startrow}, ${endrow}p" ${STATIS_FILE}  > ${temp}`
    countttl=`cat ${temp} | wc -l`

# put the statistic during the training period to temp file.
    startrow=` cat -n ${STATIS_FILE} | grep "${deTrstart}" | awk '{print $1}' `
    endrow=` cat -n ${STATIS_FILE} | grep "${deTrend}" | awk '{print $1}' `
    pass=` sed -n "${startrow}, ${endrow}p" ${STATIS_FILE}  > ${temp}`
    count=`cat ${temp} | wc -l`

#    count=0
    cputtl=0
    memoryttl=0
    dsk_readttl=0
    dsk_writttl=0
    net_recvttl=0
    net_sendttl=0
    io_readttl=0
    io_writettl=0
    blo_io_readttl=0
    blo_io_writettl=0
    usrttl=0
    systtl=0

    cpuavg=0
    memoryvag=0
    dsk_readavg=0
    dsk_writavg=0
    net_recvavg=0
    net_sendavg=0
    io_readavg=0
    io_writeavg=0
    blo_io_readavg=0
    blo_io_writeavg=0
    usravg=0
    sysavg=0

# read each line of statistic
    for statistic in `cat ${temp} | sed 's/ //g'`
    do
    usr=` echo  ${statistic} | cut -d "," -f2  `
    sys=` echo  ${statistic} | cut -d "," -f3  `

    cpu=$(awk 'BEGIN{print '${usr}' + '${sys}'}')

    memory=` echo  ${statistic} | cut -d "," -f8 `
    dsk_read=` echo  ${statistic} | cut -d "," -f12 `
    dsk_writ=` echo  ${statistic} | cut -d "," -f13 `
    net_recv=` echo  ${statistic} | cut -d "," -f14 `
    net_send=` echo  ${statistic} | cut -d "," -f15 `
    io_read=` echo  ${statistic} | cut -d "," -f16 `
    io_write=` echo  ${statistic} | cut -d "," -f17 `
    blo_io_read=` echo  ${statistic} | cut -d "/" -f2 | cut -d ":" -f1`
    blo_io_write=` echo  ${statistic} | cut -d "/" -f2 | cut -d ":" -f2`

#echo "usr is ${usr}"
#echo "sys is ${sys}"
#echo "cpu is ${cpu}"

#if [ $(echo " 35 <= ${usr}"|bc) -eq 1 ]; then
#    count=`expr ${count} + 1`
    usrttl=$(awk 'BEGIN{print '${usrttl}' + '${usr}'}')
    systtl=$(awk 'BEGIN{print '${systtl}' + '${sys}'}')
    cputtl=$(awk 'BEGIN{print '${cputtl}' + '${cpu}'}')
    memoryttl=$(awk 'BEGIN{print '${memoryttl}' + '${memory}'}')
    dsk_readttl=$(awk 'BEGIN{print '${dsk_readttl}' + '${dsk_read}'}')
    dsk_writttl=$(awk 'BEGIN{print '${dsk_writttl}' + '${dsk_writ}'}')
    net_recvttl=$(awk 'BEGIN{print '${net_recvttl}' + '${net_recv}'}')
    net_sendttl=$(awk 'BEGIN{print '${net_sendttl}' + '${net_send}'}')
    io_readttl=$(awk 'BEGIN{print '${io_readttl}' + '${io_read}'}')
    io_writettl=$(awk 'BEGIN{print '${io_writettl}' + '${io_write}'}')
    blo_io_readttl=$(awk 'BEGIN{print '${blo_io_readttl}' + '${blo_io_read}'}')
    blo_io_writettl=$(awk 'BEGIN{print '${blo_io_writettl}' + '${blo_io_write}'}')
#fi
    done

   cpuavg=$(awk 'BEGIN{print '${cputtl}' / '${count}'}')
   usravg=$(awk 'BEGIN{print '${usrttl}' / '${count}'}')
   sysavg=$(awk 'BEGIN{print '${systtl}' / '${count}'}')

   memoryvag=$(awk 'BEGIN{print '${memoryttl}' / '${count}'}')
   dsk_readavg=$(awk 'BEGIN{print '${dsk_readttl}' / '${count}'}')
   dsk_writavg=$(awk 'BEGIN{print '${dsk_writttl}' / '${count}'}')
   net_recvavg=$(awk 'BEGIN{print '${net_recvttl}' / '${count}'}')
   net_sendavg=$(awk 'BEGIN{print '${net_sendttl}' / '${count}'}')
   io_readavg=$(awk 'BEGIN{print '${io_readttl}' / '${count}'}')
   io_writeavg=$(awk 'BEGIN{print '${io_writettl}' / '${count}'}')
   blo_io_readavg=$(awk 'BEGIN{print '${blo_io_readttl}' / '${count}'}')
   blo_io_writeavg=$(awk 'BEGIN{print '${blo_io_writettl}' / '${count}'}')


   echo "<default> <${algorithm}> <${dataset}> cpu average = ${cpuavg} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> usr average = ${usravg} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> sys average = ${sysavg} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> memory average = ${memoryvag} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> disk read average = ${dsk_readavg} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> disk write average = ${dsk_writavg} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> net receive average = ${net_recvavg} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> net send average = ${net_sendavg} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> io read average = ${io_readavg} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> io write average = ${io_writeavg} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> block io read average = ${blo_io_readavg} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> block io write average = ${blo_io_writeavg} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> training time = ${detrainTtl} " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> testing time = ${detestTtl} " >> ${utilization_file}
#   echo "<default> <${algorithm}> <${dataset}> over 35% second = ${count} s " >> ${utilization_file}
   echo "<default> <${algorithm}> <${dataset}> total seconds for mapreduce = ${countttl} s " >> ${utilization_file}
 
fi

 for LINE in `cat ${ACTUAL_CHANGE_PARAM}`
 do

  PSB_NUM=` cat ${ALL_CONF_PARAM} | grep "${LINE}:" | grep -o '/' | wc -l`
  echo "collecting statistic when run with hadoop property ${LINE}, there are (is) ${PSB_NUM} possible value except for the default value"

  ROW_END=`expr ${PSB_NUM} + 1`

  i=2
 
  #collect statistic from all possible value 
   while [ ${i} -le ${ROW_END} ]
   do

    a="-f${i}"

    VALUE=` cat ${ALL_CONF_PARAM} | grep "${LINE}:" | cut -d ":" -f2 | cut -d "/" ${a} `

     prefix="\[${algorithm}\]\s\[${dataset}\]\s\[${LINE}:${VALUE}\]"

 #   echo "prefix is ${prefix}"
    echo "collecting statistic..."


    traingDate=` cat ${Jobtime} | grep ${prefix} | grep "Date is" | cut -d " " -f6 `
    trainStart=` cat ${Jobtime} | grep ${prefix} | grep "training start time" | cut -d " " -f8 `
    trainEnd=` cat ${Jobtime} | grep ${prefix} | grep "training end time" | cut -d " " -f8 `
    trainTtl=` cat ${Jobtime} | grep ${prefix} | grep "training time is" | cut -d " " -f7 `
    testTtl=` cat ${Jobtime} | grep ${prefix} | grep "testing time is" | cut -d " " -f7 `

    Job1Start=` cat ${Jobtime} | grep ${prefix} | grep "mapreduce job1 start time" | cut -d "=" -f2 `
    Job1End=` cat ${Jobtime} | grep ${prefix} | grep "mapreduce job1 end time" | cut -d "=" -f2 `
    Job2Start=` cat ${Jobtime} | grep ${prefix} | grep "mapreduce job2 start time" | cut -d "=" -f2 `
    Job2End=` cat ${Jobtime} | grep ${prefix} | grep "mapreduce job2 end time" | cut -d "=" -f2 `

if [ -z "${trainEnd}" ]; then
 echo " "
 echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}>"
 echo "there is no statistic in this setting, the job may fail when run with this property, please check log."
 echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> BLANK " >> ${utilization_file}
 echo " "
 i=`expr ${i} + 1`
 continue
fi


echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}>"
#echo "detraingDate: ${traingDate}"
#echo "detrainStart: ${trainStart}"
#echo "detrainEnd: ${trainEnd}"
#echo "detrainTtl: ${trainTtl}"
#echo "detestTtl: ${testTtl}"

    Trstart="${traingDate} ${trainStart}"
    Trend="${traingDate} ${trainEnd}"
    Taskst1="${traingDate} ${Job1Start}"
    Tasken1="${traingDate} ${Job1End}"
    Taskst2="${traingDate} ${Job2Start}"
    Tasken2="${traingDate} ${Job2End}"

#echo "Trstart: ${Trstart}"
#echo "Trend: ${Trend}"

    startrow=` cat -n ${STATIS_FILE} | grep "${Taskst1}" | awk '{print $1}' `
    endrow=` cat -n ${STATIS_FILE} | grep "${Tasken2}" | awk '{print $1}' `
    sed -n "${startrow}, ${endrow}p" ${STATIS_FILE}  > ${temp}
    countttl=`cat ${temp} | wc -l`



#echo "startrow: ${startrow}"
#echo "endrow: ${endrow}"
echo " "

# put the statistic during the training period to temp file.
    startrow=` cat -n ${STATIS_FILE} | grep "${Trstart}" | awk '{print $1}' `
    endrow=` cat -n ${STATIS_FILE} | grep "${Trend}" | awk '{print $1}' `
    sed -n "${startrow}, ${endrow}p" ${STATIS_FILE}  > ${temp}
    count=`cat ${temp} | wc -l`

#    count=0
    cputtl=0
    memoryttl=0
    dsk_readttl=0
    dsk_writttl=0
    net_recvttl=0
    net_sendttl=0
    io_readttl=0
    io_writettl=0
    blo_io_readttl=0
    blo_io_writettl=0
    usrttl=0
    systtl=0

    cpuavg=0
    memoryvag=0
    dsk_readavg=0
    dsk_writavg=0
    net_recvavg=0
    net_sendavg=0
    io_readavg=0
    io_writeavg=0
    blo_io_readavg=0
    blo_io_writeavg=0
    usravg=0
    sysavg=0

# read each line of statistic
    for statistic in `cat ${temp} | sed 's/ //g'`
    do
    usr=` echo  ${statistic} | cut -d "," -f2  `
    sys=` echo  ${statistic} | cut -d "," -f3  `

    cpu=$(awk 'BEGIN{print '${usr}' + '${sys}'}')
    memory=` echo  ${statistic} | cut -d "," -f8 `
    dsk_read=` echo  ${statistic} | cut -d "," -f12 `
    dsk_writ=` echo  ${statistic} | cut -d "," -f13 `
    net_recv=` echo  ${statistic} | cut -d "," -f14 `
    net_send=` echo  ${statistic} | cut -d "," -f15 `
    io_read=` echo  ${statistic} | cut -d "," -f16 `
    io_write=` echo  ${statistic} | cut -d "," -f17 `
    blo_io_read=` echo  ${statistic} | cut -d "/" -f2 | cut -d ":" -f1`
    blo_io_write=` echo  ${statistic} | cut -d "/" -f2 | cut -d ":" -f2`

    
# if [ $(echo " 35 <= ${usr}"|bc) -eq 1 ]; then
#    count=`expr ${count} + 1`
    usrttl=$(awk 'BEGIN{print '${usrttl}' + '${usr}'}')
    systtl=$(awk 'BEGIN{print '${systtl}' + '${sys}'}')
    cputtl=$(awk 'BEGIN{print '${cputtl}' + '${cpu}'}')
    memoryttl=$(awk 'BEGIN{print '${memoryttl}' + '${memory}'}')
    dsk_readttl=$(awk 'BEGIN{print '${dsk_readttl}' + '${dsk_read}'}')
    dsk_writttl=$(awk 'BEGIN{print '${dsk_writttl}' + '${dsk_writ}'}')
    net_recvttl=$(awk 'BEGIN{print '${net_recvttl}' + '${net_recv}'}')
    net_sendttl=$(awk 'BEGIN{print '${net_sendttl}' + '${net_send}'}')
    io_readttl=$(awk 'BEGIN{print '${io_readttl}' + '${io_read}'}')
    io_writettl=$(awk 'BEGIN{print '${io_writettl}' + '${io_write}'}')
    blo_io_readttl=$(awk 'BEGIN{print '${blo_io_readttl}' + '${blo_io_read}'}')
    blo_io_writettl=$(awk 'BEGIN{print '${blo_io_writettl}' + '${blo_io_write}'}')
#fi
    done

#echo "count is ${count}"
#echo "usrttl is ${usrttl}"
#echo "systtl is ${systtl}"
   cpuavg=$(awk 'BEGIN{print '${cputtl}' / '${count}'}')
   usravg=$(awk 'BEGIN{print '${usrttl}' / '${count}'}')
   sysavg=$(awk 'BEGIN{print '${systtl}' / '${count}'}')
   memoryvag=$(awk 'BEGIN{print '${memoryttl}' / '${count}'}')
   dsk_readavg=$(awk 'BEGIN{print '${dsk_readttl}' / '${count}'}')
   dsk_writavg=$(awk 'BEGIN{print '${dsk_writttl}' / '${count}'}')
   net_recvavg=$(awk 'BEGIN{print '${net_recvttl}' / '${count}'}')
   net_sendavg=$(awk 'BEGIN{print '${net_sendttl}' / '${count}'}')
   io_readavg=$(awk 'BEGIN{print '${io_readttl}' / '${count}'}')
   io_writeavg=$(awk 'BEGIN{print '${io_writettl}' / '${count}'}')
   blo_io_readavg=$(awk 'BEGIN{print '${blo_io_readttl}' / '${count}'}')
   blo_io_writeavg=$(awk 'BEGIN{print '${blo_io_writettl}' / '${count}'}')


   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> cpu average = ${cpuavg} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> usr average = ${usravg} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> sys average = ${sysavg} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> memory average = ${memoryvag} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> disk read average = ${dsk_readavg} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> disk write average = ${dsk_writavg} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> net receive average = ${net_recvavg} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> net send average = ${net_sendavg} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> io read average = ${io_readavg} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> io write average = ${io_writeavg} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> block io read average = ${blo_io_readavg} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> block io write average = ${blo_io_writeavg} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> training time = ${trainTtl} " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> testing time = ${testTtl} " >> ${utilization_file}
#   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> over 35% second = ${count} s " >> ${utilization_file}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> total seconds for mapreduce = ${countttl} s " >> ${utilization_file}

   i=`expr ${i} + 1`

  done
 
   echo ""
   echo "Already collect statistic for all possible value of setting ${LINE}"
   echo ""

 done

done

done

echo " Job finished"

exit 0
