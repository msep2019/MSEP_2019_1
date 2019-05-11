//This is SVM for DARPA Dataset, not for CrossValidation
package SVM_CrossValidation.SVM_CV

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.{Vectors,Vector}
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import java.io.PrintWriter;

object SVM_CrossV {
  
    def main(args: Array[String]): Unit = {
    //val conf = new SparkConf().setAppName("SVMWithSGDExample").setMaster("local")
    //val conf = new SparkConf().setAppName("TestSVM").setMaster("local")
    val conf = new SparkConf().setAppName("TestSVM").setMaster("local")
    val sc = new SparkContext(conf)

    println("------------------------Read File-------------------------")
    val traindata = sc.textFile("/home/hadoop/Desktop/svm/Darpa/20DarpaTrain.txt")   
    val testdata = sc.textFile("/home/hadoop/Desktop/svm/Darpa/20DarpaTest.txt")
    //val traindata = sc.textFile("hdfs://localhost:9000/user/hadoop/spark/kdd/20KDDTrain.txt")   
    //val testdata = sc.textFile("hdfs://localhost:9000/user/hadoop/spark/kdd/20KDDTest.txt")


    println("-------------------Read file Complete -> Set LabelPoint----------------------")
    val training = traindata.map { line =>
      val parts = line.split(',')
        LabeledPoint(
            parts(6).toDouble,
            Vectors.dense(parts(0).toDouble,parts(1).toDouble,parts(2).toDouble,parts(3).toDouble,parts(4).toDouble,parts(5).toDouble)
        )
       }.cache()

    // Run training algorithm to build the model
    println("--------------------SetLable Complete -> TrainModel----------------------")
    val numIterations = 1000
    
    val trainstarttime = System.currentTimeMillis()
    //new SimpleDateFormat("yyyy-MM-dd HH:ss:SSS").format(new Date())
    
    val model = SVMWithSGD.train(training, numIterations)
    
    val trainendtime = System.currentTimeMillis()
    
    val trainingtime = trainendtime - trainstarttime
    
    println("Training Time = " + trainingtime + " millisecond")

    println("-------------------Trainning Complete -> Testing Label----------------------")
    val testing = testdata.map { line =>
      val parts = line.split(',')
        LabeledPoint(
            parts(6).toDouble,
            Vectors.dense(parts(0).toDouble,parts(1).toDouble,parts(2).toDouble,parts(3).toDouble,parts(4).toDouble,parts(5).toDouble)
        )
       }

    // Clear the default threshold.
    model.clearThreshold()
    model.setThreshold(0.0)
    
    println("--------------Test Label Complete -> Predict----------------------")
    
    val teststarttime = System.currentTimeMillis()
    
    // Compute raw scores on the test set.
    val scoreAndLabels = testing.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }
    
    val testendtime = System.currentTimeMillis()
    
    val testtime = testendtime - teststarttime
    
    println("prediction Time = " + testtime + " millisecond")
    
    //normal is 0 and abnormal is 1, so true positive(tp) is when predict = 1 and correct = 1
    val tp = testing.filter { e =>
      val predict = model.predict(e.features)
      val correct = e.label
      predict == 1.toDouble && correct == 1.toDouble
    }.count

    //normal is 0 and abnormal is 1, so false positive(fp) is when predict = 1 and correct = 0
    val fp = testing.filter { e =>
      val predict = model.predict(e.features)
      val correct = e.label
      predict == 1.toDouble && correct == 0.toDouble
    }.count
    
    //normal is 0 and abnormal is 1, so true negative(tn) is when predict = 0 and correct = 0
    val tn = testing.filter { e =>
      val predict = model.predict(e.features)
      val correct = e.label
      predict == 0.toDouble && correct == 0.toDouble
    }.count
    
    //normal is 0 and abnormal is 1, so false negative(fn) is when predict = 0 and correct = 1
    val fn = testing.filter { e =>
      val predict = model.predict(e.features)
      val correct = e.label
      predict == 0.toDouble && correct == 1.toDouble
    }.count
    
    val accuratecount = tn + tp
    val accuracy=accuratecount.toDouble/(tn+tp+fn+fp).toDouble
    val precision = tp.toDouble/(tp+fp).toDouble
    val POS = tp + fn
    val NEG = tn + fp
    val FPR = fp.toDouble/NEG.toDouble //false positive rate
    val TPR = tp.toDouble/POS.toDouble //true positive rate
    val TNR = tn.toDouble/NEG.toDouble //true negative rate
    val FNR = fn.toDouble/POS.toDouble //false negative rate
    val R = tp.toDouble/(tp+fn).toDouble // recall rate
    val f1 = 2.toDouble/(1.toDouble/precision.toDouble + 1.toDouble/R.toDouble).toDouble
    val dr = tp.toDouble / (tp+fp).toDouble
    //scoreAndLabels.foreach(println)
    
    println("accurate count = " + accuratecount )
    println("total count =" +testdata.count )
    println("true positive (tp)= " + tp )
    println("false positive (fp)= " + fp )
    println("true negative (tn)= " + tn )
    println("false negative (fn)= " + fn )
    println("accuracy = " + accuracy.toDouble )
    println("precision = " + precision.toDouble)
    println("POS = " + POS)
    println("NEG = " + NEG)
    println("FPR = " + FPR.toDouble)
    println("TPR = " + TPR.toDouble)
    println("TNR = " + TNR.toDouble)
    println("FNR = " + FNR.toDouble)
    println("Detection Rate = " + dr.toDouble)
    println("F1 Score = " + f1.toDouble)
    
    println("----------------------------Complete----------------------")

    // Get evaluation metrics.
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    //val metrics = new MulticlassMetrics(scoreAndLabels)
    val auROC = metrics.areaUnderROC()

    println("Area under ROC = " + auROC)
    
    }
}
