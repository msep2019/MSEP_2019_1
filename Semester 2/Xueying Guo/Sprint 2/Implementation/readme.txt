Job Run Command


::::::::::::::Naive Bayes::::::::::::::
Format
hadoop jar <name of jar> <class name> <sequence generated>  <output path> <temporary path> <training set>  <test set>

-----------Psuedo mode-----------
CIDDS dataset
hadoop jar hadoop_nb-0.0.1-SNAPSHOT-shade_package.jar naivebayes.hadoop_nb.naivebayes /user/hadoop/nb/CIDD/train/seq /user/hadoop/nb/CIDD/output /user/hadoop/nb/CIDD/temp /user/hadoop/nb/CIDD/train/tr1.csv /user/hadoop/nb/CIDD/test/te1.csv

Iot dataset
hadoop jar hadoop_nb-0.0.1-SNAPSHOT-shade_package.jar naivebayes.hadoop_nb.nb_botnet /user/hadoop/nb/Iot/train/seq /user/hadoop/nb/Iot/output /user/hadoop/nb/Iot/temp /user/hadoop/nb/Iot/train/tr1.csv /user/hadoop/nb/Iot/test/te1.csv
-------------Cluster--------------
CIDDS
hadoop jar NB_Cluster.jar naivebayes.hadoop_nb.naivebayes /user/ubuntu/Hadoop/nb/CIDD/train/seq /user/ubuntu/Hadoop/nb/CIDD/output /user/ubuntu/Hadoop/nb/CIDD/temp /user/ubuntu/Hadoop/nb/CIDD/train/CIDD_train.csv /user/ubuntu/Hadoop/nb/CIDD/test/CIDD_test.csv

Iot
hadoop jar NB_Cluster.jar naivebayes.hadoop_nb.nb_botnet /user/ubuntu/Hadoop/nb/Iot/train/seq /user/ubuntu/Hadoop/nb/Iot/output /user/ubuntu/Hadoop/nb/Iot/temp /user/ubuntu/Hadoop/nb/Iot/train/tr1.csv /user/ubuntu/Hadoop/nb/Iot/test/te1.csv


::::::::::::::Random Forest::::::::::::::
Format
hadoop jar <jar name> <class name> -Dmapred.max.split.size=<max size each partition> <trainset> <testingset> <descriptorfile> <path of generated model> <prediction file>

-----------Psuedo mode-----------
CIDDS dataset
hadoop jar random-0.0.1-SNAPSHOT-shade_package.jar randomforest.random.Cidd /user/hadoop/rf/CIDD/train/tr1.csv /user/hadoop/rf/CIDD/test/te1.csv /user/hadoop/rf/CIDD/train/descriptor.info /user/hadoop/rf/CIDD/output /user/hadoop/rf/CIDD/prediction -Dmapred.max.split.size=62914560 

Iot dataset
hadoop jar random-0.0.1-SNAPSHOT-shade_package.jar randomforest.random.Iot /user/hadoop/rf/Iot/train/tr1.csv /user/hadoop/rf/Iot/test/te1.csv /user/hadoop/rf/Iot/train/descriptor.info /user/hadoop/rf/Iot/output /user/hadoop/rf/Iot/prediction


-------------Cluster--------------
CIDDS dataset
hadoop jar RF_Cluster.jar randomforest.random.Cidd /user/ubuntu/Hadoop/nb/CIDD/train/tr1.csv /user/ubuntu/Hadoop/nb/CIDD/test/te1.csv /user/ubuntu/Hadoop/rf/CIDD/train/descriptor.info /user/ubuntu/Hadoop/rf/CIDD/output /user/ubuntu/Hadoop/rf/CIDD/prediction

Iot dataset
hadoop jar RF_Cluster.jar randomforest.random.Iot /user/ubuntu/Hadoop/nb/Iot/train/tr1.csv /user/ubuntu/Hadoop/nb/Iot/test/te1.csv /user/ubuntu/Hadoop/rf/Iot/train/descriptor.info /user/ubuntu/Hadoop/rf/Iot/output /user/ubuntu/Hadoop/rf/Iot/prediction


