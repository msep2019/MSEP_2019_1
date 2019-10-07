#!/bin/sh

#This is a script for getting utilization for each node (i.e. CPU, training time, I/O etc.)

#usage: ./get_utilization_all.sh 
#example: nohup ./get_utilization_all.sh &

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

count=8
flag=0

REULST_FILE="${RESULT_PATH}/result_${kdate}.txt"

# collect and calculate statistic

for AL in `cat ${CL_AL_PARAM}`
do

 flag=0

 algorithm=`echo ${AL} | cut -d "-" -f1`
 dataset=`echo ${AL} | cut -d "-" -f2`

 echo "get statistic for ${dataset} dataset with ${algorithm} algorithm."


 for LINE in `cat ${ACTUAL_CHANGE_PARAM}`
 do

  PSB_NUM=` cat ${ALL_CONF_PARAM} | grep "${LINE}:" | grep -o '/' | wc -l`
  echo "collecting statistic when run with hadoop property ${LINE}, there are (is) ${PSB_NUM} possible value except for the default value"

  ROW_END=`expr ${PSB_NUM} + 1`

  i=1
 
  #calculate statistic from all possible value 
   while [ ${i} -le ${ROW_END} ]
   do

    a="-f${i}"

    VALUE=` cat ${ALL_CONF_PARAM} | grep "${LINE}:" | cut -d ":" -f2 | cut -d "/" ${a} `

echo "i = ${i}"
echo "value = ${VALUE}"

    echo "collecting statistic..."

    files=$( ls ${UTILI_PATH} )

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

     prefix="<${algorithm}>\s<${dataset}>\s<${LINE}:${VALUE}>"

echo "i=${i}"
echo "flag=${flag}"

if [ ${i} -eq 1 ]; then
 echo "default"
 prefix="<default>\s<${algorithm}>\s<${dataset}>"
fi

#if [ ${i} -eq 1 -a ${flag} -eq 0 ]; then
# echo "default"
# prefix="<default>\s<${algorithm}>\s<${dataset}>"
# flag=1
#elif [ ${i} -eq 1 -a ${flag} -eq 1 ]; then
# echo "already calculate value for default setting with ${algorithm} and ${dataset}. "
# break
#fi


    cpu=` cat ${utilization_file} | grep ${prefix} | grep "cpu average" | cut -d "=" -f2 | sed 's/ //g' `
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

    done

    if [ -z "${cpu}" ]; then
     i=`expr ${i} + 1` 
     continue
#    elif [ ${i} -eq 1 -a ${flag} -eq 1 ]; then
#     echo "already calculate value for default default setting with ${algorithm} and ${dataset}. " >> ${REULST_FILE}
#     i=`expr ${i} + 1` 
#     continue
    fi

   cpuavg=$(awk 'BEGIN{print '${cputtl}' / '${count}'}')
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
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> cpu average = ${cpuavg} " >> ${REULST_FILE}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> memory average = ${memoryvag} " >> ${REULST_FILE}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> disk read average = ${dsk_readavg} " >> ${REULST_FILE}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> disk write average = ${dsk_writavg} " >> ${REULST_FILE}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> net receive average = ${net_recvavg} " >> ${REULST_FILE}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> net send average = ${net_sendavg} " >> ${REULST_FILE}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> io read average = ${io_readavg} " >> ${REULST_FILE}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> io write average = ${io_writeavg} " >> ${REULST_FILE}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> block io read average = ${blo_io_readavg} " >> ${REULST_FILE}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> block io write average = ${blo_io_writeavg} " >> ${REULST_FILE}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> training time = ${trainTtl} " >> ${REULST_FILE}
   echo "<${algorithm}> <${dataset}> <${LINE}:${VALUE}> testing time = ${testTtl} " >> ${REULST_FILE}
   echo "  " >> ${REULST_FILE}

  i=`expr ${i} + 1` 
  done

   echo ""
   echo "Already collect statistic for all possible value of setting ${LINE}"
   echo ""


done


done

echo " Job finished"


echo " All Statistic Collected " >> ${REULST_FILE}
exit 0
