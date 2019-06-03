#!/bin/bash 
#!/usr/bin/expect -f

#This is a script for login master or cluster node

#Parameter setting

PARAM_PATH="/home/hadoop/Desktop/scripts/param"
. $PARAM_PATH/env.param

NAME=`basename $0`
NAME=`echo ${NAME} | cut -d "." -f1`

timestamp=`date "+%Y%m%d%H%M%S%s"`
LOG_FILE="${LOG_PATH}/${NAME}_${timestamp}.log"

exec 1>${LOG_FILE}
exec 2>>${LOG_FILE}

if [ $# != 1 ]; then
 echo "invalid number of parameters"
 exit 1
else 
 echo "Set parameter complete and start program"
fi


# Login Node / Cluster

echo "connect to $1 node"
str="$1:"
IP=`cat $PARAM_PATH/node.param | grep $str | cut -d ":" -f2`

if [ -z "$IP" ]; then
 echo "Node does not exist, please varify"
 exit 1
fi

cmd="ssh -i $KEY_PATH/keypair1.pem $USER_NAME@$IP"

echo ${cmd}

#return ${cmd}

#ssh -i $KEY_PATH/keypair1.pem $USER_NAME@$IP > /dev/null 2>&1 << aa


#cd /home/ubuntu/svm_test/kdd
#touch test.txt

#exit

#aa

#if [ $? != 0 ]; then 
#  echo "Login $1 failed"
#  exit 1
# else 
#  echo "Login Master node successfully!"
#fi



