The Runnable Jars folder contains four Jar files (i.e., BDCA systems). Two Jar files can use KDD99, DARPA, CIDDS and CICIDS2017 with Naïve Bayes and Random Forest respectively on Spark (i.e., BDCA_Spark_NaiveBayes.jar and BDCA_Spark_RandomForest.jar). Two Jar files can use KDD99 and CIDDS respectively with Naïve Bayes on Hadoop (i.e., BDCA_Hadoop_KDD99.jar and BDCA_Hadoop_CIDDS.jar).

Instructions on How to Run the Operational Artefacts
The BDCA systems run in distributed mode on cloud. The files involved in the system (i.e., input data sets) should be put onto the HDFS. 
The Jar files in Runnable Jars are produced by compiling source code files in Code folder. The .scala file is compiled to Jar file with sbt. The .java file can be exported to Jar file in Eclipse. 
A.	The steps to run Spark BDCA systems (i.e., BDCA_Spark_NaiveBayes.jar and BDCA_Spark_RandomForest.jar) are as follows.
a)	Start Hadoop, Yarn and Spark:
  $HADOOP_HOME/sbin/start-dfs.sh
  $HADOOP_HOME/sbin/start-yarn.sh
  $SPARK_HOME/sbin/start-all.sh
b)	Run cmd on Master node: $SPARK_HOME/bin/spark-submit --master yarn --deploy-mode cluster --num-executors n path_of_Jar_file path_of_train_file path_of_test_file dataset_identifier
  n: number of executors (worker nodes in Yarn)
  path_of_Jar_file: path in master node
  path_of_train_file, path_of_test_file: path in HDFS
  dataset_identifier: KDD99 (1), DARPA (2), CIDDS (3), CICIDS2017(4)
Example: $SPARK_HOME/bin/spark-submit --master yarn --deploy-mode cluster --num-executors 5 /home/ubuntu/BDCA_Spark_NaiveBayes.jar /ubuntu/dataset/preprd-train-CIDIDS.txt /ubuntu/dataset/preprd-test-CIDIDS.txt 3
c)	The outcome is like:
2019-05-05 02:37:03 INFO  Client:54 - 
	 client token: N/A
	 diagnostics: N/A
	 ApplicationMaster host: cluster8-worker8-3.novalocal
	 ApplicationMaster RPC port: 37485
	 queue: default
	 start time: 1557022824498
	 final status: SUCCEEDED
	 tracking URL: http://master:8088/proxy/application_1556894988144_0031/
	 user: ubuntu
The result is in yarn.nodemanager.log-dirs/ application_appId/container_appId_01_000001/stdout of ApplicationMaster host (e.g., $HADOOP_HOME/yarn-log/ application_1556894988144_0031/container_1556894988144_0031_01_000001/stdout in cluster8-worker8-3.novalocal) 

B.	The steps to run Hadoop BDCA systems (i.e., BDCA_Hadoop_KDD99.jar and BDCA_Hadoop_CIDDS.jar) are as follows.
a)	Start Hadoop and Yarn:
  $HADOOP_HOME/sbin/start-dfs.sh
  $HADOOP_HOME/sbin/start-yarn.sh
b)	Run cmd on Master node: hadoop jar path_of_Jar_file
  path_of_Jar_file: path in master node
Example: hadoop jar /home/ubuntu/Cloud/OpenStack/nb_hadoop/ BDCA_Hadoop_CIDDS.jar
Note: The path of train and test files are set in the source code for BDCA on Hadoop. If the paths need to be changed, then please modify them in the source code (i.e., HadoopNB.java).

C.	The steps to run Adaptation Implementation (i.e., AdaptionBDCA.sh) are as follows.
a)	Run cmd on local machine: ./AdaptionBDCA.sh path_of_Jar_file path_of_train_file path_of_test_file dataset_identifier responseTimeType
  path_of_Jar_file: path in master node
  path_of_train_file, path_of_test_file: path in HDFS
  dataset_identifier: KDD99 (1), DARPA (2), CIDDS (3), CICIDS2017(4)
  responseTimeType: train or test
Example: ./AdaptionBDCA.sh BDCA_Spark_NaiveBayes.jar KDDTrain.txt KDDTest.txt 1 train
Note: The information of nodes is set in the Shell Script. If it needs to be changed (e.g., IPs of nodes), then please modify it in the Shell Script (i.e., AdaptionBDCA.sh).
