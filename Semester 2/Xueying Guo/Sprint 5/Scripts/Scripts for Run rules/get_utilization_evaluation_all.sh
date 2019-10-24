#!/bin/sh

#This is a script for getting utilization for each node (i.e. CPU, training time, I/O etc.)

#usage: ./get_utilization_all.sh 
#example: nohup ./get_utilization_evaluation_all.sh &

#Parameter setting

PARAM_PATH="/home/hadoop/Desktop/scripts_hadoop/param"
. $PARAM_PATH/env.param

NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`

timestamp=`date "+%Y%m%d%H%M%S%s"`
LOG_FILE="${LOG_PATH}/${NAME}_${timestamp}.log"
kdate=`date "+%Y%m%d"`

Jobtime="${TIME_PATH}/Job_Timestamp.log"

exec 1>>${LOG_FILE}
exec 2>>${LOG_FILE}


flag=0

REULST_FILE="${RESULT_PATH}/result_${kdate}.txt"
REULST_CSV="${RESULT_PATH}/result_${kdate}.csv"
cmd="rm ${REULST_FILE}"
${cmd}

# collect and calculate statistic
TEST_AL_PARAM="${PARAM_PATH}/test_algorithm.param"

for AL in `cat ${TEST_AL_PARAM}`
do
 flag=0

 algorithm=`echo ${AL} | cut -d "-" -f1`
 dataset=`echo ${AL} | cut -d "-" -f2`

 echo "get statistic for ${dataset} dataset with ${algorithm} algorithm."



  i=1
 
  #calculate statistic from all possible value 
   while [ ${i} -le 2 ]
   do



    echo "collecting statistic..."

    files=$( ls ${UTILI_PATH} )

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
    durttl=0

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

    for filename in ${files}
    do

     worker=` echo ${filename} | cut -d "." -f1 | cut -d "_" -f2`

     if [ "${worker}" = "master" ]; then   #skip master file
	echo "skip master file."
        continue
     fi

     echo " "
     echo "getting from ${worker} node... "
     echo " "

     utilization_file="${UTILI_PATH}/Utilization_${worker}.txt"

     echo "utilization_file is ${utilization_file}"
     echo " "



if [ ${i} -eq 1 ]; then
 echo "default"
 prefix="<default>\s<${algorithm}>\s<${dataset}>"
 outputprefix="<default> <${algorithm}> <${dataset}>"
else
 prefix="<TunedbyRules>\s<${algorithm}>\s<${dataset}>"
 outputprefix="<TunedbyRules> <${algorithm}> <${dataset}>"
fi


    cpu=` cat ${utilization_file} | grep ${prefix} | grep "cpu average" | cut -d "=" -f2 | sed 's/ //g' `
    usr=` cat ${utilization_file} | grep ${prefix} | grep "usr average" | cut -d "=" -f2 | sed 's/ //g' `
    sys=` cat ${utilization_file} | grep ${prefix} | grep "sys average" | cut -d "=" -f2 | sed 's/ //g' `
    memory=` cat ${utilization_file} | grep ${prefix} | grep "memory average" | cut -d "=" -f2 | sed 's/ //g' `
    dsk_read=` cat ${utilization_file} | grep ${prefix} | grep "disk read average" | cut -d "=" -f2 | sed 's/ //g' `
    dsk_writ=` cat ${utilization_file} | grep ${prefix} | grep "disk write average" | cut -d "=" -f2 | sed 's/ //g' `
    net_recv=` cat ${utilization_file} | grep ${prefix} | grep "net receive average" | cut -d "=" -f2 | sed 's/ //g' `
    net_send=` cat ${utilization_file} | grep ${prefix} | grep "net send average" | cut -d "=" -f2 | sed 's/ //g' `
    io_read=` cat ${utilization_file} | grep ${prefix} | grep "> io read average" | cut -d "=" -f2 | sed 's/ //g' `
    io_write=` cat ${utilization_file} | grep ${prefix} | grep "> io write average" | cut -d "=" -f2 | sed 's/ //g' `
    blo_io_read=` cat ${utilization_file} | grep ${prefix} | grep "block io read average" | cut -d "=" -f2 | sed 's/ //g' `
    blo_io_write=` cat ${utilization_file} | grep ${prefix} | grep "block io write average" | cut -d "=" -f2 | sed 's/ //g' `
    trainTtl=` cat ${utilization_file} | grep ${prefix} | grep "training time" | cut -d "=" -f2 | sed 's/ //g' `
    testTtl=` cat ${utilization_file} | grep ${prefix} | grep "testing time" | cut -d "=" -f2 | sed 's/ //g' `
#    dur=` cat ${utilization_file} | grep ${prefix} | grep "over 35% second" | cut -d "=" -f2 | cut -d " " -f2 `
    total=` cat ${utilization_file} | grep ${prefix} | grep "total seconds for mapreduce" | cut -d "=" -f2 | cut -d " " -f2 `


#echo "cpu is ${cpu}"
#echo "memory is ${memory}"
#echo "dsk_read is ${dsk_read}"
#echo "dsk_writ is ${dsk_writ}"
#echo "net_recv is ${net_recv}"
#echo "net_send is ${net_send}"
#echo "io_read is ${io_read}"
#echo "io_write is ${io_write}"
#echo "blo_io_read is ${blo_io_read}"
#echo "blo_io_write is ${blo_io_write}"

if [ -z "${cpu}" ]; then
 echo "${prefix}"
 echo " "
 echo "there is no statistic in this setting, the job may fail when run with this property, please check log."
 echo "${prefix} BLANK " >> ${REULST_FILE}
 echo " "
 break
fi

# read each file of statistic

#echo "dur is ${dur}"
# if [ $(echo " 0 < ${dur}"|bc) -eq 1 ]; then
    cputtl=$(awk 'BEGIN{print '${cputtl}' + '${cpu}'}')
    usrttl=$(awk 'BEGIN{print '${usrttl}' + '${usr}'}')
    systtl=$(awk 'BEGIN{print '${systtl}' + '${sys}'}')
    memoryttl=$(awk 'BEGIN{print '${memoryttl}' + '${memory}'}')
    dsk_readttl=$(awk 'BEGIN{print '${dsk_readttl}' + '${dsk_read}'}')
    dsk_writttl=$(awk 'BEGIN{print '${dsk_writttl}' + '${dsk_writ}'}')
    net_recvttl=$(awk 'BEGIN{print '${net_recvttl}' + '${net_recv}'}')
    net_sendttl=$(awk 'BEGIN{print '${net_sendttl}' + '${net_send}'}')
    io_readttl=$(awk 'BEGIN{print '${io_readttl}' + '${io_read}'}')
    io_writettl=$(awk 'BEGIN{print '${io_writettl}' + '${io_write}'}')
    blo_io_readttl=$(awk 'BEGIN{print '${blo_io_readttl}' + '${blo_io_read}'}')
    blo_io_writettl=$(awk 'BEGIN{print '${blo_io_writettl}' + '${blo_io_write}'}')
 #   durttl=$(awk 'BEGIN{print '${durttl}' + '${dur}'}')
 #   count=`expr ${count} + 1`
#fi
    done

    if [ -z "${cpu}" ]; then
     i=`expr ${i} + 1` 
     continue
    fi

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

   echo "  " >> ${REULST_FILE}
   echo "${outputprefix} cpu average = ${cpuavg} " >> ${REULST_FILE}
   echo "${outputprefix} usr average = ${usravg} " >> ${REULST_FILE}
   echo "${outputprefix} sys average = ${sysavg} " >> ${REULST_FILE}
   echo "${outputprefix} memory average = ${memoryvag} " >> ${REULST_FILE}
   echo "${outputprefix} disk read average = ${dsk_readavg} " >> ${REULST_FILE}
   echo "${outputprefix} disk write average = ${dsk_writavg} " >> ${REULST_FILE}
   echo "${outputprefix} net receive average = ${net_recvavg} " >> ${REULST_FILE}
   echo "${outputprefix} net send average = ${net_sendavg} " >> ${REULST_FILE}
   echo "${outputprefix} io read average = ${io_readavg} " >> ${REULST_FILE}
   echo "${outputprefix} io write average = ${io_writeavg} " >> ${REULST_FILE}
   echo "${outputprefix} block io read average = ${blo_io_readavg} " >> ${REULST_FILE}
   echo "${outputprefix} block io write average = ${blo_io_writeavg} " >> ${REULST_FILE}
   echo "${outputprefix} training time = ${trainTtl} ms " >> ${REULST_FILE}
   echo "${outputprefix} testing time = ${testTtl} ms " >> ${REULST_FILE}
   echo "${outputprefix} total map reduce = ${total} seconds " >> ${REULST_FILE}
 #  echo "${outputprefix} total over 35% = ${durttl} seconds " >> ${REULST_FILE}
 #  echo "${outputprefix} number of nodes over 35% = ${count} " >> ${REULST_FILE}
   echo "  " >> ${REULST_FILE}

echo "${outputprefix},${cpuavg},${usravg},${sysavg},${memoryvag},${dsk_readavg},${dsk_writavg},${net_recvavg},${net_sendavg},${io_readavg},${io_writeavg},${blo_io_readavg},${blo_io_writeavg},${trainTtl},${total},${testTtl}" >> ${REULST_CSV}

  i=`expr ${i} + 1` 

  done



done

echo " Job finished"


echo " All Statistic Collected " >> ${REULST_FILE}
exit 0
