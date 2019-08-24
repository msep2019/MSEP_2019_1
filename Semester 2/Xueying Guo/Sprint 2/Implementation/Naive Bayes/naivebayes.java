package naivebayes.hadoop_nb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;  
import java.io.OutputStreamWriter;  
import java.io.PrintWriter;
import java.net.URI;
import java.util.List;  
import java.util.Locale;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.mahout.classifier.AbstractVectorClassifier;
import org.apache.mahout.classifier.naivebayes.ComplementaryNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.VectorWritable;

import com.opencsv.CSVReader;

public class naivebayes {


	public static void main(String[] args) throws Exception
	{
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.getLocal(conf);		// may change when run on cluster
		String sequenceFile = "/home/hadoop/Desktop/hadoop/nb/train/seq";
		String outputDirectory = "/home/hadoop/Desktop/hadoop/nb/output";
		String tempDirectory = "/home/hadoop/Desktop/hadoop/nb/temp";
		Path seqFilePath = new Path(sequenceFile);	// will change when run on cluster
		String csvTrainPath = "/home/hadoop/Desktop/hadoop/nb/train/CIDDTrain.csv";
		String csvTestPath = "/home/hadoop/Desktop/hadoop/nb/test/CIDDTest.csv";
		
		
		fs.delete(seqFilePath,false);
		SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, seqFilePath, Text.class, VectorWritable.class);

		CsvToVector Train_csvToVectors = new CsvToVector(csvTrainPath);
		List<MahoutVector> Train_vectors = Train_csvToVectors.vectorize();
		
		for (MahoutVector Train_vector : Train_vectors)
		{
			VectorWritable vectorWritable = new VectorWritable();
			vectorWritable.set(Train_vector.vector);
			writer.append(new Text("/" + Train_vector.label + "/"), vectorWritable);
			System.out.println(Train_vector.label);
			System.out.println(Train_vector.vector);
		}
		
		System.out.println("finish trainsfer to sequence file");
		
		writer.close();
		
		// Train data
		TrainNaiveBayesJob trainNaiveBayes = new TrainNaiveBayesJob();
		trainNaiveBayes.setConf(conf);
		
		fs.delete(new Path(outputDirectory),true);
		fs.delete(new Path(tempDirectory),true);
		
		trainNaiveBayes.run(new String[] { "--input", sequenceFile, "--output", outputDirectory, "--overwrite", "--tempDir", tempDirectory });
//		trainNaiveBayes.run(new String[] { "-i",sequenceFile + "/tfidf-vectors", "-o", outputDirectory, "-el", "-c", "-ow" });
		
		
		NaiveBayesModel naiveBayesModel = NaiveBayesModel.materialize(new Path(outputDirectory), conf);

		System.out.println("features: " + naiveBayesModel.numFeatures());
		System.out.println("labels: " + naiveBayesModel.numLabels());
		
		StandardNaiveBayesClassifier classifier = new StandardNaiveBayesClassifier(naiveBayesModel);
		
//		AbstractVectorClassifier classifier = new ComplementaryNaiveBayesClassifier(naiveBayesModel);
		
		CsvToVector Test_csvToVectors = new CsvToVector(csvTestPath);
		
		List<MahoutVector> Test_vectors = Test_csvToVectors.vectorize();
		
		//test 
		int tp = 0;
		int fp = 0;
		int fn = 0;
		int tn = 0;
		int total_test = 0;
		
		for (MahoutVector Test_mahoutVector : Test_vectors) {
			Vector prediction = classifier.classifyFull(Test_mahoutVector.vector);
			// 0 - normal , 1 - suspicious
			double bestScore = -Double.MAX_VALUE;
			int predictlabel = -1;
			for (Element element : prediction.all())
			{
				// label with the higher score will be the predicted label
				double score = element.get();
				if (score > bestScore )
				{
					predictlabel = element.index();
					bestScore = score;
				}
				
				System.out.println("Test_mahoutVector.label is:" + Test_mahoutVector.label);
				System.out.println("predictlabel is:"+predictlabel);
				
				if (Test_mahoutVector.label == "suspicious" &&  predictlabel == 1){ // true positive
					tp++;
					total_test++;
				}
				else if (Test_mahoutVector.label == "normal" &&  predictlabel == 1){ // false positive
					fp++;
					total_test++;
				}
				else if (Test_mahoutVector.label == "suspicious" &&  predictlabel == 0){ // false negative
					fn++;
					total_test++;
				}
				else if (Test_mahoutVector.label == "normal" &&  predictlabel == 0){ // true negative
					tn++;
					total_test++;
				}
				else {
					total_test++;
				}

			}
			
			
			System.out.println("total test data:" + total_test);
			System.out.println("tp:" +tp + "  fp:" +fp +"  fn:" +fn + "  tn:" +tn);
			System.out.println("predict label is " +predictlabel + "  score:"+bestScore);
	
			System.out.println(prediction.get(0));
			System.out.println(prediction.get(1));
			
			
		}
		// calculate accuracy
	}

}
