package SVM_Testing.SVM

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.{Vectors,Vector}
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import java.io.PrintWriter;

object SVM {
     def main(args: Array[String]): Unit = {
    //val conf = new SparkConf().setAppName("SVMWithSGDExample").setMaster("local")
    val conf = new SparkConf().setAppName("TestSVM").setMaster("local")
    val sc = new SparkContext(conf)

    // $example on$
    // Load training data in LIBSVM format.
    //val data = MLUtils.loadLibSVMFile(sc, "/home/hadoop/Desktop/svm/train.txt")
    val data = sc.textFile("/home/hadoop/Desktop/svm/iris.txt")    
    //val data = MLUtils.loadLibSVMFile(sc, "/home/cloudera/Desktop/SVMTesting/svm.txt")
    
    val parsedData = data.map { line =>
      val parts = line.split(',')
        LabeledPoint(
            if (parts(4)=="Iris-setosa") 0.toDouble
            else if (parts(4)=="Iris-versicolor") 1.toDouble
            else 2.toDouble, 
            Vectors.dense(parts(0).toDouble,parts(1).toDouble,parts(2).toDouble,parts(3).toDouble)
        )
       }
    //print(parsedData)
    
    // Split data into training (60%) and test (40%).
    val splits = parsedData.filter { point => point.label != 2 }.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)

    // Run training algorithm to build the model
    val numIterations = 1000
    val model = SVMWithSGD.train(training, numIterations)

    // Clear the default threshold.
    model.clearThreshold()

    // Compute raw scores on the test set.
    val scoreAndLabels = test.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }
    
    scoreAndLabels.foreach(println)
    
    model.setThreshold(0.0)
    
    scoreAndLabels.foreach(println)

    // Get evaluation metrics.
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    val auROC = metrics.areaUnderROC()

    println("Area under ROC = " + auROC)

     
  }
}
