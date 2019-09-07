//import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.ml.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.sql.types.{StructType, StructField, StringType, DoubleType}
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.sql.{DataFrame, Row, SQLContext, SparkSession, Column}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.types._
import org.apache.spark.sql.functions._
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}

import org.apache.spark.ml.feature.Normalizer
import org.apache.spark.ml.feature.StandardScaler
import org.apache.spark.ml.feature.PolynomialExpansion
 
object NaiveBayesSpark {
  def main(args: Array[String]) {
    val sconf = new SparkConf().setAppName("NaiveBayes").setMaster("yarn");
    val sc = new SparkContext(sconf)

    val sqlContext = new SQLContext(sc)
    
    if (args.length != 3) {
      println("params error!")
      return
    }
    
    val path = args(0)
    val path2 = args(1)
    val dataset = args(2)
    
    val spark = SparkSession
      .builder()
      .appName("Data Reader")
      .getOrCreate()
    import spark.implicits._

    if (dataset == "4") { //cici2017
      val header = "cici1 cici2 cici3 cici4 cici5 cici6 cici7 cici8 cici9 cici10 cici11 cici12 cici13 cici14 cici15 cici16 cici17 cici18 cici19 cici20 cici21 cici22 cici23 cici24 cici25 cici26 cici27 cici28 cici29 cici30 cici31 cici32 cici33 cici34 cici35 cici36 cici37 cici38 cici39 cici40 cici41 cici42 cici43 cici44 cici45 cici46 cici47 cici48 cici49 cici50 cici51 cici52 cici53 cici54 cici55 cici56 cici57 cici58 cici59 cici60 cici61 cici62 cici63 cici64 cici65 cici66 cici67 cici68 cici69 cici70 cici71 cici72 cici73 cici74 cici75 cici76 cici77 cici78 cici79 cici80 cici81 cici82 label"
      val schema = StructType(header.split(" ").map(fieldName => StructField(fieldName, StringType, true)))
      val rdd = spark.sparkContext.textFile(path).map(_.split(",")).map(x => Row(x(0), x(1), x(2), x(3), x(4), x(5), x(6), x(7), x(8), x(9), x(10), x(11), x(12), x(13), x(14), x(15), x(16), x(17), x(18), x(19), x(20), x(21), x(22), x(23), x(24), x(25), x(26), x(27), x(28), x(29), x(30), x(31), x(32), x(33), x(34), x(35), x(36), x(37), x(38), x(39), x(40), x(41), x(42), x(43), x(44), x(45), x(46), x(47), x(48), x(49), x(50), x(51), x(52), x(53), x(54), x(55), x(56), x(57), x(58), x(59), x(60), x(61), x(62), x(63), x(64), x(65), x(66), x(67), x(68), x(69), x(70), x(71), x(72), x(73), x(74), x(75), x(76), x(77), x(78), x(79), x(80), x(81), x(82)))
      val df = sqlContext.createDataFrame(rdd, schema)
  
      val assembler = new VectorAssembler()
        .setInputCols(Array("cici1", "cici2", "cici3", "cici4", "cici5", "cici6", "cici7", "cici8", "cici9", "cici10", "cici11", "cici12", "cici13", "cici14", "cici15", "cici16", "cici17", "cici18", "cici19", "cici20", "cici21", "cici22", "cici23", "cici24", "cici25", "cici26", "cici27", "cici28", "cici29", "cici30", "cici31", "cici32", "cici33", "cici34", "cici35", "cici36", "cici37", "cici38", "cici39", "cici40", "cici41", "cici42", "cici43", "cici44", "cici45", "cici46", "cici47", "cici48", "cici49", "cici50", "cici51", "cici52", "cici53", "cici54", "cici55", "cici56", "cici57", "cici58", "cici59", "cici60", "cici61", "cici62", "cici63", "cici64", "cici65", "cici66", "cici67", "cici68", "cici69", "cici70", "cici71", "cici72", "cici73", "cici74", "cici75", "cici76", "cici77", "cici78", "cici79", "cici80", "cici81", "cici82"))
        .setOutputCol("features")
        .setHandleInvalid("skip")
  
      val index = new StringIndexer().setInputCol("cici72").setOutputCol("cici72Index")
      val indexedDf = index.fit(df).transform(df).drop("cici72").withColumnRenamed("cici72Index", "cici72")
      val index71 = new StringIndexer().setInputCol("cici71").setOutputCol("cici71Index")
      val indexedDf71 = index71.fit(indexedDf).transform(indexedDf).drop("cici71").withColumnRenamed("cici71Index", "cici71")

      indexedDf71.show()
      
      val cols = indexedDf71.columns.map(d => col(d).cast(DoubleType))
      val doubleDf = indexedDf71.select(cols: _*)
      val positiveAllDf = doubleDf.filter($"cici1" >= 0 && $"cici2" >= 0 && $"cici3" >= 0 && $"cici4" >= 0 && $"cici5" >= 0 && $"cici6" >= 0 && $"cici7" >= 0 && $"cici8" >= 0 && $"cici9" >= 0 && $"cici10" >= 0 && $"cici11" >= 0 && $"cici12" >= 0 && $"cici13" >= 0 && $"cici14" >= 0 && $"cici15" >= 0 && $"cici16" >= 0 && $"cici17" >= 0 && $"cici18" >= 0 && $"cici19" >= 0 && $"cici20" >= 0 && $"cici20" < Double.MaxValue && $"cici22" >= 0 && $"cici23" >= 0 && $"cici24" >= 0 && $"cici25" >= 0 && $"cici26" >= 0 && $"cici27" >= 0 && $"cici28" >= 0 && $"cici29" >= 0 && $"cici30" >= 0 && $"cici31" >= 0 && $"cici32" >= 0 && $"cici33" >= 0 && $"cici34" >= 0 && $"cici35" >= 0 && $"cici36" >= 0 && $"cici37" >= 0 && $"cici38" >= 0 && $"cici39" >= 0 && $"cici40" >= 0 && $"cici41" >= 0 && $"cici42" >= 0 && $"cici43" >= 0 && $"cici44" >= 0 && $"cici45" >= 0 && $"cici46" >= 0 && $"cici47" >= 0 && $"cici48" >= 0 && $"cici49" >= 0 && $"cici50" >= 0 && $"cici51" >= 0 && $"cici52" >= 0 && $"cici53" >= 0 && $"cici54" >= 0 && $"cici55" >= 0 && $"cici56" >= 0 && $"cici57" >= 0 && $"cici58" >= 0 && $"cici59" >= 0 && $"cici60" >= 0 && $"cici61" >= 0 && $"cici62" >= 0 && $"cici63" >= 0 && $"cici64" >= 0 && $"cici65" >= 0 && $"cici66" >= 0 && $"cici67" >= 0 && $"cici68" >= 0 && $"cici69" >= 0 && $"cici70" >= 0 && $"cici71" >= 0 && $"cici72" >= 0 && $"cici73" >= 0 && $"cici74" >= 0 && $"cici75" >= 0 && $"cici76" >= 0 && $"cici77" >= 0 && $"cici78" >= 0 && $"cici79" >= 0 && $"cici80" >= 0 && $"cici81" >= 0 && $"cici82" >= 0)
      var featureDropDf: DataFrame = assembler.transform(positiveAllDf)
    
      featureDropDf = featureDropDf.drop("cici1", "cici2", "cici3", "cici4", "cici5", "cici6", "cici7", "cici8", "cici9", "cici10", "cici11", "cici12", "cici13", "cici14", "cici15", "cici16", "cici17", "cici18", "cici19", "cici20", "cici21", "cici22", "cici23", "cici24", "cici25", "cici26", "cici27", "cici28", "cici29", "cici30", "cici31", "cici32", "cici33", "cici34", "cici35", "cici36", "cici37", "cici38", "cici39", "cici40", "cici41", "cici42 cici43", "cici44", "cici45", "cici46", "cici47", "cici48", "cici49", "cici50", "cici51", "cici52", "cici53", "cici54", "cici55", "cici56", "cici57", "cici58", "cici59", "cici60", "cici61", "cici62", "cici63", "cici64", "cici65", "cici66", "cici67", "cici68", "cici69", "cici70", "cici71", "cici72", "cici73", "cici74", "cici75", "cici76", "cici77", "cici78", "cici79", "cici80", "cici81", "cici82")


      val std = new StandardScaler()
        .setInputCol("features")
        .setOutputCol("stdFeatures")
        .setWithStd(true)
        .setWithMean(false)
    
      val stdModel = std.fit(featureDropDf)
    
      val stdDF = stdModel.transform(featureDropDf)
    
      featureDropDf = stdDF.drop("features").withColumnRenamed("stdFeatures", "features")


      val naivebayes = new NaiveBayes().setFeaturesCol("features").setLabelCol("label").setModelType("multinomial")
      
      // split training set
      val Array(training, testing) = featureDropDf.randomSplit(Array(0.66, 0.34))
      
      // cross validation
      val paramGrid = (new ParamGridBuilder()
                 .addGrid(naivebayes.smoothing, Array(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0))
                 .build())
  
      val evaluator = new MulticlassClassificationEvaluator()
      
      val pipeline = new Pipeline().setStages(Array(naivebayes))
      
      val cv = new CrossValidator()
      .setEstimator(pipeline)
      .setEvaluator(evaluator)
      .setEstimatorParamMaps(paramGrid)
      .setNumFolds(10)
      
      val trainStart = System.currentTimeMillis()
      val modelCV = cv.fit(training)
      val trainEnd = System.currentTimeMillis()
      val testStart = System.currentTimeMillis()
      val predictionCV = modelCV.transform(testing)
      val testEnd = System.currentTimeMillis()
      
      println("predictionCV: ")
      predictionCV.show()
      
      val (pred, label, count) = (predictionCV.select("prediction").collect,
      testing.select("label").collect(),
      testing.count().toInt)
      println("pred: " + pred(0).toString())
      println("label: " + label(0).toString())
      var correct = 0
      var tp = 0
      var fn = 0
      var tn = 0
      var fp = 0
      for (i <- 0 to count - 1) {
        if (pred(i) == label(i)) {
          correct += 1
          if (pred(i).toString().equals("[0.0]")) { // normal -> normal
            tn += 1
          } else { // anomalous -> anomalous
            tp += 1
          }
        } else {
          if (pred(i).toString().equals("[0.0]")) { // anomalous -> normal
            fn += 1
          } else { // normal -> anomalous
            fp += 1
          }
        }
        
      }
      println("correct: " + correct)
      println("total count: " + count)
      println("accuracy rate: " + 1.0 * correct / count)
      
      println("tp: " + tp)
      println("fn: " + fn)
      println("tn: " + tn)
      println("fp: " + fp)
      var pos = tp + fn
      var neg = fp + tn
      var accuracy = 1.0 * (tp + tn) / (pos + neg)
      var fpr = 1.0 * fp / neg
      var precision = 1.0 * tp / (tp + fp)
      var recall = 1.0 * tp / pos
      var fmeasure = 1.0 * 2 / (1 / precision + 1 / recall)
      println("POS = " + pos)
      println("NEG = " + neg)
      println("Accuracy = " + accuracy)
      println("FPR = " + fpr)
      println("Precision = " + precision)
      println("TPR = " + recall)
      println("F-Measure = " + fmeasure)
      println("TNR = " + (1 - fpr))
      println("FNR = " + (1 - recall))
      var derate = 1.0 * correct / count
      println("Detection Rate: " + derate)
      
      println("Train Time: " + (trainEnd - trainStart))
      println("Test Time: " + (testEnd - testStart))
      
    } 
    if (dataset == "1") { //kdd
  //    feature (header) from "https://blog.csdn.net/com_stu_zhang/article/details/6987632"
      var featureArr = Array("duration", "protocol_type", "service", "flag", "src_bytes", "dst_bytes", "land",
      "wrong_fragment", "urgent", "hot", "num_failed_logins", "logged_in", "num_compromised", "root_shell",
      "su_attempted", "num_root", "num_file_creations", "num_shells", "num_access_files", "num_outbound_cmds",
      "is_host_login", "is_guest_login", "count", "srv_count", "serror_rate", "srv_serror_rate", "rerror_rate",
      "srv_rerror_rate", "same_srv_rate", "diff_srv_rate", "srv_diff_host_rate", "dst_host_count",
      "dst_host_srv_count", "dst_host_same_srv_rate", "dst_host_diff_srv_rate", "dst_host_same_src_port_rate",
      "dst_host_srv_diff_host_rate", "dst_host_serror_rate", "dst_host_srv_serror_rate", "dst_host_rerror_rate",
      "dst_host_srv_rerror_rate")
      var headerArr = featureArr :+ "label"
      
      var rowRDD = spark.sparkContext.textFile(path).map(_.split(",")).map(x => Row(x(0), x(1), x(2), x(3), x(4), x(5) , x(6) , x(7) , x(8), x(9), x(10), x(11), x(12), x(13), x(14), x(15) , x(16) , x(17) , x(18), x(19), x(20), x(21), x(22), x(23), x(24), x(25) , x(26) , x(27) , x(28), x(29), x(30), x(31), x(32), x(33), x(34), x(35) , x(36) , x(37) , x(38), x(39), x(40), x(41)))
      var rowRDD2 = spark.sparkContext.textFile(path2).map(_.split(",")).map(x => Row(x(0), x(1), x(2), x(3), x(4), x(5) , x(6) , x(7) , x(8), x(9), x(10), x(11), x(12), x(13), x(14), x(15) , x(16) , x(17) , x(18), x(19), x(20), x(21), x(22), x(23), x(24), x(25) , x(26) , x(27) , x(28), x(29), x(30), x(31), x(32), x(33), x(34), x(35) , x(36) , x(37) , x(38), x(39), x(40), x(41)))

      var schema = StructType(headerArr.map(fieldName => StructField(fieldName, StringType, true)))
      var df = sqlContext.createDataFrame(rowRDD, schema)
      var df2 = sqlContext.createDataFrame(rowRDD2, schema)
  
      df.show()
      
      var assembler = new VectorAssembler()
        .setInputCols(featureArr)
        .setOutputCol("features")
        .setHandleInvalid("skip")
  
      
      
      
        
      var indexedLabeledDf = df
      var indexedLabeledDf2 = df2
      
      val stringIndexer = new StringIndexer()
        .setInputCol("label")
        .setOutputCol("indexed_label")
        .setHandleInvalid("skip")
      val indexer = stringIndexer.fit(df)
      
      indexedLabeledDf = indexer.transform(df).drop("label").withColumnRenamed("indexed_label", "label")
      indexedLabeledDf2 = indexer.transform(df2).drop("label").withColumnRenamed("indexed_label", "label")

      
      indexedLabeledDf.show()
      indexedLabeledDf2.show()
        
      val cols = indexedLabeledDf.columns.map(d => col(d).cast(DoubleType))
      val cols2 = indexedLabeledDf2.columns.map(d => col(d).cast(DoubleType))
      var featureDropDf: DataFrame = assembler.transform(indexedLabeledDf.select(cols: _*))
      var featureDropDf2: DataFrame = assembler.transform(indexedLabeledDf2.select(cols2: _*))
      
      for (i <- 0 to featureArr.length - 1) {
        featureDropDf = featureDropDf.drop(featureArr(i))
        featureDropDf2 = featureDropDf2.drop(featureArr(i))
      }
      
      featureDropDf.show()


      val naivebayes = new NaiveBayes().setFeaturesCol("features").setLabelCol("label").setModelType("multinomial")
      
      // split training set
  //    val Array(training, testing) = featureDropDf.randomSplit(Array(0.8, 0.2))
      
      val training = featureDropDf
      val testing = featureDropDf2
      
      // cross validation
      val paramGrid = (new ParamGridBuilder()
        .addGrid(naivebayes.smoothing, Array(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0))
        .build())
  
      val evaluator = new BinaryClassificationEvaluator()
      
      val pipeline = new Pipeline().setStages(Array(naivebayes))
      
      val cv = new CrossValidator()
        .setEstimator(pipeline)
        .setEvaluator(evaluator)
        .setEstimatorParamMaps(paramGrid)
        .setNumFolds(10)
        
        
      val trainStart = System.currentTimeMillis()
      val modelCV = cv.fit(training)
      val trainEnd = System.currentTimeMillis()
      val testStart = System.currentTimeMillis()
      val predictionCV = modelCV.transform(testing)
      val testEnd = System.currentTimeMillis()

      
      println("predictionCV: finish")
  //    predictionCV.show()
      
      val (pred, label, count) = (predictionCV.select("prediction").collect,
        testing.select("label").collect(),
        testing.count().toInt)
      println("pred: " + pred(0).toString())
      println("label: " + label(0).toString())
      var correct = 0
      var tp = 0
      var fn = 0
      var tn = 0
      var fp = 0
      for (i <- 0 to count - 1) {
        if (pred(i) == label(i)) {
          correct += 1
          if (pred(i).toString().equals("[2.0]")) { // normal -> normal
            tn += 1
          } else { // anomalous -> anomalous
            tp += 1
          }
        } else {
          if (pred(i).toString().equals("[2.0]")) { // anomalous -> normal
            fn += 1
          } else { // normal -> anomalous
            fp += 1
          }
        }
        
      }
      println("correct: " + correct)
      println("total count: " + count)
      println("accuracy rate: " + 1.0 * correct / count)
      
      println("tp: " + tp)
      println("fn: " + fn)
      println("tn: " + tn)
      println("fp: " + fp)
      val pos = tp + fn
      val neg = fp + tn
      val accuracy = 1.0 * (tp + tn) / (pos + neg)
      val fpr = 1.0 * fp / neg
      val precision = 1.0 * tp / (tp + fp)
      val recall = 1.0 * tp / pos
      val fmeasure = 1.0 * 2 / (1 / precision + 1 / recall)
      println("POS = " + pos)
      println("NEG = " + neg)
      println("Accuracy = " + accuracy)
      println("FPR = " + fpr)
      println("Precision = " + precision)
      println("TPR = " + recall)
      println("F-Measure = " + fmeasure)
      println("TNR = " + (1 - fpr))
      println("FNR = " + (1 - recall))
      val derate = 1.0 * correct / count
      println("Detection Rate: " + derate)
      
      println("Train Time: " + (trainEnd - trainStart))
      println("Test Time: " + (testEnd - testStart))
    }
    
    if (dataset == "2") { // darpa
        var featureArr = Array("darpa1", "darpa2", "darpa3", "darpa4", "darpa5", "darpa6")
        var headerArr = featureArr :+ "label"
        var rowRDD = spark.sparkContext.textFile(path).map(_.split(",")).map(x => Row(x(0), x(1), x(2), x(3), x(4), x(5) , x(6)))
        var rowRDD2 = spark.sparkContext.textFile(path2).map(_.split(",")).map(x => Row(x(0), x(1), x(2), x(3), x(4), x(5) , x(6)))

        var schema = StructType(headerArr.map(fieldName => StructField(fieldName, StringType, true)))
        var df = sqlContext.createDataFrame(rowRDD, schema)
        var df2 = sqlContext.createDataFrame(rowRDD2, schema)
  
        df.show()
      
        var assembler = new VectorAssembler()
          .setInputCols(featureArr)
          .setOutputCol("features")
          .setHandleInvalid("skip")
          
          
      var indexedLabeledDf = df
      var indexedLabeledDf2 = df2
      
      indexedLabeledDf.show()
      indexedLabeledDf2.show()
        
      val cols = indexedLabeledDf.columns.map(d => col(d).cast(DoubleType))
      val cols2 = indexedLabeledDf2.columns.map(d => col(d).cast(DoubleType))
      var featureDropDf: DataFrame = assembler.transform(indexedLabeledDf.select(cols: _*))
      var featureDropDf2: DataFrame = assembler.transform(indexedLabeledDf2.select(cols2: _*))
      
      for (i <- 0 to featureArr.length - 1) {
        featureDropDf = featureDropDf.drop(featureArr(i))
        featureDropDf2 = featureDropDf2.drop(featureArr(i))
      }
      
      featureDropDf.show()

      if (dataset == "2") { // darpa
    	val expansionFeature = new PolynomialExpansion()
      	  .setInputCol("features")
      	  .setOutputCol("expFeatures")
      	  .setDegree(3)
    
    	val expDF = expansionFeature.transform(featureDropDf)
    	featureDropDf = expDF.drop("features").withColumnRenamed("expFeatures", "features")
    	val expDF2 = expansionFeature.transform(featureDropDf2)
    	featureDropDf2 = expDF2.drop("features").withColumnRenamed("expFeatures", "features")
    
    	featureDropDf.show()
    
    	val normalizer = new Normalizer()
          .setInputCol("features")
          .setOutputCol("normFeatures")
          .setP(1.0)
    
    	val normDF = normalizer.transform(featureDropDf)
    	featureDropDf = normDF.drop("features").withColumnRenamed("normFeatures", "features")
    	val normDF2 = normalizer.transform(featureDropDf2)
    	featureDropDf2 = normDF2.drop("features").withColumnRenamed("normFeatures", "features")
    
    
    
    	val std = new StandardScaler()
          .setInputCol("features")
          .setOutputCol("stdFeatures")
          .setWithStd(true)
          .setWithMean(false)
    
    	val stdModel = std.fit(featureDropDf)
    
    	val stdDF = stdModel.transform(featureDropDf)
    	val stdDF2 = stdModel.transform(featureDropDf2)
    
    	featureDropDf = stdDF.drop("features").withColumnRenamed("stdFeatures", "features")
    	featureDropDf2 = stdDF2.drop("features").withColumnRenamed("stdFeatures", "features")
    	featureDropDf.show()
      }
      
      val naivebayes = new NaiveBayes().setFeaturesCol("features").setLabelCol("label").setModelType("multinomial")
      
      // split training set
  //    val Array(training, testing) = featureDropDf.randomSplit(Array(0.8, 0.2))
      
      val training = featureDropDf
      val testing = featureDropDf2
      
      // cross validation
      val paramGrid = (new ParamGridBuilder()
        .addGrid(naivebayes.smoothing, Array(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0))
        .build())
  
      val evaluator = new BinaryClassificationEvaluator()
      
      val pipeline = new Pipeline().setStages(Array(naivebayes))
      
      val cv = new CrossValidator()
        .setEstimator(pipeline)
        .setEvaluator(evaluator)
        .setEstimatorParamMaps(paramGrid)
        .setNumFolds(10)
        
        
      val trainStart = System.currentTimeMillis()
      val modelCV = cv.fit(training)
      val trainEnd = System.currentTimeMillis()
      val testStart = System.currentTimeMillis()
      val predictionCV = modelCV.transform(testing)
      val testEnd = System.currentTimeMillis()

      
      println("predictionCV: finish")
  //    predictionCV.show()
      
      val (pred, label, count) = (predictionCV.select("prediction").collect,
        testing.select("label").collect(),
        testing.count().toInt)
      println("pred: " + pred(0).toString())
      println("label: " + label(0).toString())
      var correct = 0
      var tp = 0
      var fn = 0
      var tn = 0
      var fp = 0
      for (i <- 0 to count - 1) {
        if (pred(i) == label(i)) {
          correct += 1
          if (pred(i).toString().equals("[0.0]")) { // normal -> normal
            tn += 1
          } else { // anomalous -> anomalous
            tp += 1
          }
        } else {
          if (pred(i).toString().equals("[0.0]")) { // anomalous -> normal
            fn += 1
          } else { // normal -> anomalous
            fp += 1
          }
        }
        
      }
      println("correct: " + correct)
      println("total count: " + count)
      println("accuracy rate: " + 1.0 * correct / count)
      
      println("tp: " + tp)
      println("fn: " + fn)
      println("tn: " + tn)
      println("fp: " + fp)
      val pos = tp + fn
      val neg = fp + tn
      val accuracy = 1.0 * (tp + tn) / (pos + neg)
      val fpr = 1.0 * fp / neg
      val precision = 1.0 * tp / (tp + fp)
      val recall = 1.0 * tp / pos
      val fmeasure = 1.0 * 2 / (1 / precision + 1 / recall)
      println("POS = " + pos)
      println("NEG = " + neg)
      println("Accuracy = " + accuracy)
      println("FPR = " + fpr)
      println("Precision = " + precision)
      println("TPR = " + recall)
      println("F-Measure = " + fmeasure)
      println("TNR = " + (1 - fpr))
      println("FNR = " + (1 - recall))
      val derate = 1.0 * correct / count
      println("Detection Rate: " + derate)
      
      println("Train Time: " + (trainEnd - trainStart))
      println("Test Time: " + (testEnd - testStart))
    }
    
    if (dataset == "3") { // cidi
      var featureArr = Array("Duration", "Proto", "Src_IP_Addr", "Src_Pt", "Dst_IP_Addr", "Dst_Pt", "Packets", "Bytes", "Flows", "Flags", "Tos")
      var headerArr = featureArr :+ "label"
      var rowRDD = spark.sparkContext.textFile(path).map(_.split(",")).map(x => Row(x(0), x(1), x(2), x(3), x(4), x(5), x(6), x(7), x(8), x(9), x(10), x(11)))
      var rowRDD2 = spark.sparkContext.textFile(path2).map(_.split(",")).map(x => Row(x(0), x(1), x(2), x(3), x(4), x(5), x(6), x(7), x(8), x(9), x(10), x(11)))

      var schema = StructType(headerArr.map(fieldName => StructField(fieldName, StringType, true)))
      var df = sqlContext.createDataFrame(rowRDD, schema)
      var df2 = sqlContext.createDataFrame(rowRDD2, schema)

      df.show()
    
      var assembler = new VectorAssembler()
        .setInputCols(featureArr)
        .setOutputCol("features")
        .setHandleInvalid("skip")
        
      var indexedLabeledDf = df
      var indexedLabeledDf2 = df2
      
      
      indexedLabeledDf.show()
      indexedLabeledDf2.show()
        
      val cols = indexedLabeledDf.columns.map(d => col(d).cast(DoubleType))
      val cols2 = indexedLabeledDf2.columns.map(d => col(d).cast(DoubleType))
      var featureDropDf: DataFrame = assembler.transform(indexedLabeledDf.select(cols: _*))
      var featureDropDf2: DataFrame = assembler.transform(indexedLabeledDf2.select(cols2: _*))
      
      for (i <- 0 to featureArr.length - 1) {
        featureDropDf = featureDropDf.drop(featureArr(i))
        featureDropDf2 = featureDropDf2.drop(featureArr(i))
      }
      
      featureDropDf.show()

      
      val naivebayes = new NaiveBayes().setFeaturesCol("features").setLabelCol("label").setModelType("multinomial")
      
      // split training set
  //    val Array(training, testing) = featureDropDf.randomSplit(Array(0.8, 0.2))
      
      val training = featureDropDf
      val testing = featureDropDf2
      
      // cross validation
      val paramGrid = (new ParamGridBuilder()
        .addGrid(naivebayes.smoothing, Array(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0))
        .build())
  
      val evaluator = new BinaryClassificationEvaluator()
      
      val pipeline = new Pipeline().setStages(Array(naivebayes))
      
      val cv = new CrossValidator()
        .setEstimator(pipeline)
        .setEvaluator(evaluator)
        .setEstimatorParamMaps(paramGrid)
        .setNumFolds(10)
        
        
      val trainStart = System.currentTimeMillis()
      val modelCV = cv.fit(training)
      val trainEnd = System.currentTimeMillis()
      val testStart = System.currentTimeMillis()
      val predictionCV = modelCV.transform(testing)
      val testEnd = System.currentTimeMillis()

      
      println("predictionCV: finish")
  //    predictionCV.show()
      
      val (pred, label, count) = (predictionCV.select("prediction").collect,
        testing.select("label").collect(),
        testing.count().toInt)
      println("pred: " + pred(0).toString())
      println("label: " + label(0).toString())
      var correct = 0
      var tp = 0
      var fn = 0
      var tn = 0
      var fp = 0
      for (i <- 0 to count - 1) {
        if (pred(i) == label(i)) {
          correct += 1
          if (pred(i).toString().equals("[0.0]")) { // normal -> normal
            tn += 1
          } else { // anomalous -> anomalous
            tp += 1
          }
        } else {
          if (pred(i).toString().equals("[0.0]")) { // anomalous -> normal
            fn += 1
          } else { // normal -> anomalous
            fp += 1
          }
        }
        
      }
      println("correct: " + correct)
      println("total count: " + count)
      println("accuracy rate: " + 1.0 * correct / count)
      
      println("tp: " + tp)
      println("fn: " + fn)
      println("tn: " + tn)
      println("fp: " + fp)
      val pos = tp + fn
      val neg = fp + tn
      val accuracy = 1.0 * (tp + tn) / (pos + neg)
      val fpr = 1.0 * fp / neg
      val precision = 1.0 * tp / (tp + fp)
      val recall = 1.0 * tp / pos
      val fmeasure = 1.0 * 2 / (1 / precision + 1 / recall)
      println("POS = " + pos)
      println("NEG = " + neg)
      println("Accuracy = " + accuracy)
      println("FPR = " + fpr)
      println("Precision = " + precision)
      println("TPR = " + recall)
      println("F-Measure = " + fmeasure)
      println("TNR = " + (1 - fpr))
      println("FNR = " + (1 - recall))
      val derate = 1.0 * correct / count
      println("Detection Rate: " + derate)
      
      println("Train Time: " + (trainEnd - trainStart))
      println("Test Time: " + (testEnd - testStart))
    }
    
    
    sc.stop()
  }
}
