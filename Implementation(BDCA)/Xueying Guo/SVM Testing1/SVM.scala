package SVM_Testing.SVM

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.regression.LinearRegressionModel
import org.apache.spark.mllib.regression.LinearRegressionWithSGD
import java.io.PrintWriter;

object SVM {
     def main(args: Array[String]): Unit = {
    //val conf = new SparkConf().setAppName("SVMWithSGDExample").setMaster("local")
    val conf = new SparkConf().setAppName("TestSVM").setMaster("local")
    val sc = new SparkContext(conf)

    // $example on$
    // Load training data in LIBSVM format.
    //val data = MLUtils.loadLibSVMFile(sc, "/home/hadoop/Desktop/svm/train.txt")
    val data = sc.textFile("/home/hadoop/Desktop/svm/train.txt")    
    //val data = MLUtils.loadLibSVMFile(sc, "/home/cloudera/Desktop/SVMTesting/svm.txt")
    
    val parsedData = data.map { line =>
      val parts = line.split(',')
      val row1 = parts(0).toDouble
      val row2 = parts.drop(1).map(_.toDouble)
        println("row1=="+row1)
        LabeledPoint(row1, Vectors.dense(row2))
       }
    //print(parsedData)
    
    // Split data into training (60%) and test (40%).
    val splits = parsedData.randomSplit(Array(0.9, 0.1), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)

    // Run training algorithm to build the model
    val numIterations = 100
    val model = SVMWithSGD.train(training, numIterations)

    // Clear the default threshold.
    model.clearThreshold()

    // Compute raw scores on the test set.
    val scoreAndLabels = test.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }
    
    //model.setThreshold(0.0)

    // Get evaluation metrics.
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    val auROC = metrics.areaUnderROC()

    println("Area under ROC = " + auROC)
    
    val forecastdata = sc.textFile("/home/hadoop/Desktop/svm/test.txt")
    
    val forecastresult = forecastdata.map{line=>
      val parts=line.split(",")
      Vectors.dense(parts.map(_.toDouble))
    }
    
    val forecastresult1 = model.predict(forecastresult)
    println("----------------Output-----------------------")
    forecastresult1.foreach(println)

     
  }
}