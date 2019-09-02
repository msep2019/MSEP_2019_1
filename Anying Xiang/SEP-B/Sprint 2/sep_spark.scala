//import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.ml.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
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
 
import org.apache.spark.ml.feature.StandardScaler
import org.apache.spark.ml.feature.Normalizer

object SEPSpark {
  def main(args: Array[String]) {
    val appStart = System.currentTimeMillis()
    
//    val conf = new SparkConf().setAppName("NaiveBayes").setMaster("local").set("spark.executor.memory","1g");
    val conf = new SparkConf().setAppName("SparkBDCA") // 1/2
    val sc = new SparkContext(conf)

    val sqlContext = new SQLContext(sc)
    
    var selectedOption = "BotRL"
    
    
    var ml = args(0)
    var ds = args(1)
    selectedOption = ds + ml  // 2/2
    
    
    val spark = SparkSession
      .builder()
      .appName("Input Reader")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()
      
   
    
    if (selectedOption == "BotRF") {
      val featureArr = Array("_c1", "_c2", "_c3", "_c4", "_c5", "_c6", "_c7", "_c8", "_c9", "_c10", "_c11", "_c12", "_c13", "_c14", "_c15", "_c16", "_c17", "_c18", "_c19", "_c20", "_c21", "_c22", "_c23", "_c24", "_c25")

      var df = spark.read.format("csv").option("header", "false").load("/input/Bot/training/*.csv") // *.csv
      var df2 = spark.read.format("csv").option("header", "false").load("/input/Bot/testing/*.csv")
  
      df.show()
      
      val assembler = new VectorAssembler()
        .setInputCols(featureArr)
        .setOutputCol("features")
        .setHandleInvalid("keep")
  
      val indexedLabeledDf = df
      val indexedLabeledDf2 = df2
        
      val cols = indexedLabeledDf.columns.map(f => col(f).cast(FloatType))
      val cols2 = indexedLabeledDf2.columns.map(f => col(f).cast(FloatType))
      var featureDropDf = assembler.transform(indexedLabeledDf.select(cols: _*))
      var featureDropDf2 = assembler.transform(indexedLabeledDf2.select(cols2: _*))  
      
      featureDropDf.show()
  
      
      for (i <- 0 to featureArr.length - 1) {
        featureDropDf = featureDropDf.drop(featureArr(i))
        featureDropDf2 = featureDropDf2.drop(featureArr(i))
      }
      featureDropDf = featureDropDf.withColumnRenamed("_c26", "label")
      
      featureDropDf2 = featureDropDf2.withColumnRenamed("_c26", "label")
  
      featureDropDf.show()
  
      val randomforest = new RandomForestClassifier()
        .setLabelCol("label")
        .setFeaturesCol("features")
        .setNumTrees(10)
      
      var training = featureDropDf
      var testing = featureDropDf2
  
      // cross validation
      val paramGrid = new ParamGridBuilder().build()
  
      val evaluator1 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("accuracy")
      val evaluator2 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("f1")
      val evaluator3 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("weightedPrecision")
      val evaluator4 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("weightedRecall")
        
      
      val pipeline = new Pipeline().setStages(Array(randomforest))
      
      val cv = new CrossValidator()
        .setEstimator(pipeline)
        .setEvaluator(evaluator1)
        .setEvaluator(evaluator2)
        .setEvaluator(evaluator3)
        .setEvaluator(evaluator4)
        .setEstimatorParamMaps(paramGrid)
        .setNumFolds(2)
      
      val trainStart = System.currentTimeMillis()
      val modelCV = cv.fit(training)
      val trainEnd = System.currentTimeMillis()
      val testStart = System.currentTimeMillis()
      val predictionCV = modelCV.transform(testing)
      val testEnd = System.currentTimeMillis()
      
      println("predictionCV: ")
      predictionCV.show()
      
      val acc = evaluator1.evaluate(predictionCV)
      val f1 = evaluator2.evaluate(predictionCV)
      val weightedPrecision = evaluator3.evaluate(predictionCV)
      val weightedRecall = evaluator4.evaluate(predictionCV)
      
      println("evaluator accuracy: " + acc)
      println("evaluator f1: " + f1)
      println("evaluator weightedPrecision: " + weightedPrecision)
      println("evaluator weightedRecall: " + weightedRecall)
      
      println("Train Time: " + (trainEnd - trainStart))
      println("Test Time: " + (testEnd - testStart))
      
      val appEnd = System.currentTimeMillis()
      println("Whole Time: " + (appEnd - appStart))
      return
      
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
    }  else if (selectedOption == "BotRL") {
      val featureArr = Array("_c1", "_c2", "_c3", "_c4", "_c5", "_c6", "_c7", "_c8", "_c9", "_c10", "_c11", "_c12", "_c13", "_c14", "_c15", "_c16", "_c17", "_c18", "_c19", "_c20", "_c21", "_c22", "_c23", "_c24", "_c25")

      var df = spark.read.format("csv").option("header", "false").load("/input/Bot/training/*.csv") // *.csv
      var df2 = spark.read.format("csv").option("header", "false").load("/input/Bot/testing/*.csv")
      
//      var df = spark.read.format("csv").option("header", "false").load("/home/jzd/SEP-B/ori-dataset/IoT/pre-UNSW_2018_IoT_Botnet_Dataset_2.csv") // *.csv
//      var df2 = spark.read.format("csv").option("header", "false").load("/home/jzd/SEP-B/ori-dataset/IoT/pre-UNSW_2018_IoT_Botnet_Dataset_3.csv")
      
  
      df.show()
      
      val assembler = new VectorAssembler()
        .setInputCols(featureArr)
        .setOutputCol("features")
        .setHandleInvalid("skip") // special, not 'keep'
  
      val indexedLabeledDf = df
      val indexedLabeledDf2 = df2
        
      val cols = indexedLabeledDf.columns.map(f => col(f).cast(FloatType))
      val cols2 = indexedLabeledDf2.columns.map(f => col(f).cast(FloatType))
      var featureDropDf = assembler.transform(indexedLabeledDf.select(cols: _*))
      var featureDropDf2 = assembler.transform(indexedLabeledDf2.select(cols2: _*))  
      
      featureDropDf.show()
  
      
      for (i <- 0 to featureArr.length - 1) {
        featureDropDf = featureDropDf.drop(featureArr(i))
        featureDropDf2 = featureDropDf2.drop(featureArr(i))
      }
      featureDropDf = featureDropDf.withColumnRenamed("_c26", "label")
      
      featureDropDf2 = featureDropDf2.withColumnRenamed("_c26", "label")
  
      featureDropDf.show()
  
      val linearlr = new LogisticRegression().setFeaturesCol("features").setLabelCol("label").setAggregationDepth(2).setMaxIter(1)

      
      var training = featureDropDf
      var testing = featureDropDf2
  
      // cross validation
      val paramGrid = new ParamGridBuilder().build()
  
      val evaluator1 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("accuracy")
      val evaluator2 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("f1")
      val evaluator3 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("weightedPrecision")
      val evaluator4 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("weightedRecall")
        
      
      val pipeline = new Pipeline().setStages(Array(linearlr))
      
      val cv = new CrossValidator()
        .setEstimator(pipeline)
        .setEvaluator(evaluator1)
        .setEvaluator(evaluator2)
        .setEvaluator(evaluator3)
        .setEvaluator(evaluator4)
        .setEstimatorParamMaps(paramGrid)
        .setNumFolds(2)
      
      val trainStart = System.currentTimeMillis()
      val modelCV = cv.fit(training)
      val trainEnd = System.currentTimeMillis()
      val testStart = System.currentTimeMillis()
      val predictionCV = modelCV.transform(testing)
      val testEnd = System.currentTimeMillis()
      
      println("predictionCV: ")
      predictionCV.show()
      
      val acc = evaluator1.evaluate(predictionCV)
      val f1 = evaluator2.evaluate(predictionCV)
      val weightedPrecision = evaluator3.evaluate(predictionCV)
      val weightedRecall = evaluator4.evaluate(predictionCV)
      
      println("evaluator accuracy: " + acc)
      println("evaluator f1: " + f1)
      println("evaluator weightedPrecision: " + weightedPrecision)
      println("evaluator weightedRecall: " + weightedRecall)
      
      println("Train Time: " + (trainEnd - trainStart))
      println("Test Time: " + (testEnd - testStart))
      
      val appEnd = System.currentTimeMillis()
      println("Whole Time: " + (appEnd - appStart))
      return
      
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
    } else if (selectedOption == "CIDDSRF") {   
      val featureArr = Array("_c0", "_c1", "_c2", "_c3", "_c4", "_c5", "_c6", "_c7", "_c8", "_c9", "_c10")
      var df = spark.read.format("csv").option("header", "false").load("/input/CIDDS/training/*.csv")
      var df2 = spark.read.format("csv").option("header", "false").load("/input/CIDDS/testing/*.csv")
  
      df.show()
      
      
      val assembler = new VectorAssembler()
        .setInputCols(featureArr)
        .setOutputCol("features")
        .setHandleInvalid("keep")
      
        // 0.0 is normal
      val stringIndexer = new StringIndexer()
        .setInputCol("_c11")
        .setOutputCol("label")
        .setHandleInvalid("keep")
      val indexer = stringIndexer.fit(df)
      val indexer2 = stringIndexer.fit(df2)
      
      val indexedLabeledDf = indexer.transform(df).drop("_c11")
      val indexedLabeledDf2 = indexer.transform(df2).drop("_c11")
      
        
      val cols = indexedLabeledDf.columns.map(f => col(f).cast(FloatType))
      val cols2 = indexedLabeledDf2.columns.map(f => col(f).cast(FloatType))      
      
      var featureDropDf = assembler.transform(indexedLabeledDf.select(cols: _*))
      var featureDropDf2 = assembler.transform(indexedLabeledDf2.select(cols2: _*))
      
      featureDropDf.show()
  
      
      for (i <- 0 to featureArr.length - 1) {
        featureDropDf = featureDropDf.drop(featureArr(i))
        featureDropDf2 = featureDropDf2.drop(featureArr(i))
      }
      
      featureDropDf.show()
  
      val randomforest = new RandomForestClassifier()
        .setLabelCol("label")
        .setFeaturesCol("features")
        .setNumTrees(10)
      
      var training = featureDropDf
      var testing = featureDropDf2
  
      // cross validation
      val paramGrid = new ParamGridBuilder().build()
  
      val evaluator1 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("accuracy")
      val evaluator2 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("f1")
      val evaluator3 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("weightedPrecision")
      val evaluator4 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("weightedRecall")
        
      
      val pipeline = new Pipeline().setStages(Array(randomforest))
      
      val cv = new CrossValidator()
        .setEstimator(pipeline)
        .setEvaluator(evaluator1)
        .setEvaluator(evaluator2)
        .setEvaluator(evaluator3)
        .setEvaluator(evaluator4)
        .setEstimatorParamMaps(paramGrid)
        .setNumFolds(2)
      
      val trainStart = System.currentTimeMillis()
      val modelCV = cv.fit(training)
      val trainEnd = System.currentTimeMillis()
      val testStart = System.currentTimeMillis()
      val predictionCV = modelCV.transform(testing)
      val testEnd = System.currentTimeMillis()
      
      println("predictionCV: ")
      predictionCV.show()
      
      val acc = evaluator1.evaluate(predictionCV)
      val f1 = evaluator2.evaluate(predictionCV)
      val weightedPrecision = evaluator3.evaluate(predictionCV)
      val weightedRecall = evaluator4.evaluate(predictionCV)
      
      println("evaluator accuracy: " + acc)
      println("evaluator f1: " + f1)
      println("evaluator weightedPrecision: " + weightedPrecision)
      println("evaluator weightedRecall: " + weightedRecall)
      
      println("Train Time: " + (trainEnd - trainStart))
      println("Test Time: " + (testEnd - testStart))
      
      val appEnd = System.currentTimeMillis()
      println("Whole Time: " + (appEnd - appStart))
      return
      
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
    } else if (selectedOption == "CIDDSLR") {

      val featureArr = Array("_c0", "_c1", "_c2", "_c3", "_c4", "_c5", "_c6", "_c7", "_c8", "_c9", "_c10")
      var df = spark.read.format("csv").option("header", "false").load("/input/CIDDS/training/*.csv")
      var df2 = spark.read.format("csv").option("header", "false").load("/input/CIDDS/testing/*.csv")
  
      df.show()
      
      
      val assembler = new VectorAssembler()
        .setInputCols(featureArr)
        .setOutputCol("features")
        .setHandleInvalid("keep")
      
        // 0.0 is normal
      val stringIndexer = new StringIndexer()
        .setInputCol("_c11")
        .setOutputCol("label")
        .setHandleInvalid("keep")
      val indexer = stringIndexer.fit(df)
      val indexer2 = stringIndexer.fit(df2)
      
      val indexedLabeledDf = indexer.transform(df).drop("_c11")
      val indexedLabeledDf2 = indexer.transform(df2).drop("_c11")
      
        
      val cols = indexedLabeledDf.columns.map(f => col(f).cast(FloatType))
      val cols2 = indexedLabeledDf2.columns.map(f => col(f).cast(FloatType))      
      
      var featureDropDf = assembler.transform(indexedLabeledDf.select(cols: _*))
      var featureDropDf2 = assembler.transform(indexedLabeledDf2.select(cols2: _*))
      
      featureDropDf.show()
  
      
      for (i <- 0 to featureArr.length - 1) {
        featureDropDf = featureDropDf.drop(featureArr(i))
        featureDropDf2 = featureDropDf2.drop(featureArr(i))
      }
      
      featureDropDf.show()
      
      val linearlr = new LogisticRegression().setFeaturesCol("features").setLabelCol("label").setAggregationDepth(2).setMaxIter(1)

      
      var training = featureDropDf
      var testing = featureDropDf2
  
      // cross validation
      val paramGrid = new ParamGridBuilder().build()
  
      val evaluator1 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("accuracy")
      val evaluator2 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("f1")
      val evaluator3 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("weightedPrecision")
      val evaluator4 = new MulticlassClassificationEvaluator()
        .setLabelCol("label")
        .setPredictionCol("prediction")
        .setMetricName("weightedRecall")
        
      
      val pipeline = new Pipeline().setStages(Array(linearlr))
      
      val cv = new CrossValidator()
        .setEstimator(pipeline)
        .setEvaluator(evaluator1)
        .setEvaluator(evaluator2)
        .setEvaluator(evaluator3)
        .setEvaluator(evaluator4)
        .setEstimatorParamMaps(paramGrid)
        .setNumFolds(2)
      
      val trainStart = System.currentTimeMillis()
      val modelCV = cv.fit(training)
      val trainEnd = System.currentTimeMillis()
      val testStart = System.currentTimeMillis()
      val predictionCV = modelCV.transform(testing)
      val testEnd = System.currentTimeMillis()
      
      println("predictionCV: ")
      predictionCV.show()
      
      val acc = evaluator1.evaluate(predictionCV)
      val f1 = evaluator2.evaluate(predictionCV)
      val weightedPrecision = evaluator3.evaluate(predictionCV)
      val weightedRecall = evaluator4.evaluate(predictionCV)
      
      println("evaluator accuracy: " + acc)
      println("evaluator f1: " + f1)
      println("evaluator weightedPrecision: " + weightedPrecision)
      println("evaluator weightedRecall: " + weightedRecall)
      
      println("Train Time: " + (trainEnd - trainStart))
      println("Test Time: " + (testEnd - testStart))
      
      val appEnd = System.currentTimeMillis()
      println("Whole Time: " + (appEnd - appStart))
      return
      
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

  }
}
