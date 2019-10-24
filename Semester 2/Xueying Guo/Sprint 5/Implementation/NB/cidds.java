package naivebayes.hadoop_nb;

// Naive bayes for CIDDS dataset

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;  
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.List;  
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.mahout.classifier.AbstractVectorClassifier;
import org.apache.mahout.classifier.naivebayes.ComplementaryNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.math.Vector.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.opencsv.CSVReader;


public class naivebayes {
	
	private static final Logger log = LoggerFactory.getLogger(naivebayes.class);


	public static void main(String[] args) throws Exception
	{
		
		log.info("Job Start ! ");
		
		Configuration conf = new Configuration();
		String extraArgs[] = new GenericOptionsParser(conf, args).getRemainingArgs(); 
/*		FileSystem fs = FileSystem.getLocal(conf);		// Run on Local Mode
		String sequenceFile = "/home/hadoop/Desktop/hadoop/nb/train/seq";
		String outputDirectory = "/home/hadoop/Desktop/hadoop/nb/output";
		String tempDirectory = "/home/hadoop/Desktop/hadoop/nb/temp";
		String csvTrainPath = "/home/hadoop/Desktop/hadoop/nb/train/CIDDTrain.csv";
		String csvTestPath = "/home/hadoop/Desktop/hadoop/nb/test/CIDDTest.csv";
*/
//		String uri = "hdfs://localhost:9000";			// Run on pseudo mode
		String uri = "hdfs://master:9000";			// Run on cluster mode
		
		FileSystem fs = FileSystem.get(new URI(uri), conf);
		/*		String sequenceFile = "/user/hadoop/nb/CIDD/train/seq";
		String outputDirectory = "/user/hadoop/nb/CIDD/output";
		String tempDirectory = "/user/hadoop/nb/CIDD/temp";
		String csvTrainPath = "/user/hadoop/nb/CIDD/train/CIDDS_train.csv";
		String csvTestPath = "/user/hadoop/nb/CIDD/test/CIDDS_test.csv";
		
		String sequenceFile = args[0];
		String outputDirectory = args[1];
		String tempDirectory = args[2];
		String csvTrainPath = args[3];
		String csvTestPath = args[4];
*/		int[] getfeature = {0,1,2,3,6,7,8,9,10};	// use different features 

		String sequenceFile = "/user/ubuntu/Hadoop/nb/CIDD/train/seq";
		String outputDirectory = "/user/ubuntu/Hadoop/nb/CIDD/output";
		String tempDirectory = "/user/ubuntu/Hadoop/nb/CIDD/temp";
		String csvTrainPath = "/user/ubuntu/Hadoop/nb/CIDD/train/CIDDS_train.csv";
		String csvTestPath = "/user/ubuntu/Hadoop/nb/CIDD/test/CIDDS_test.csv";
		
		System.out.println("mapred.output.compress = " + conf.get("mapreduce.output.fileoutputformat.compress") );
		System.out.println("mapred.output.compression.type = " + conf.get("mapreduce.output.fileoutputformat.compress.type") );
		System.out.println("mapred.output.compression.codec = " + conf.get("mapreduce.output.fileoutputformat.compress.codec") );
		System.out.println("mapred.compress.map.output = " + conf.get("mapreduce.map.output.compress") );
		System.out.println("mapred.map.output.compression.codec = " + conf.get("mapreduce.map.output.compress.codec") );
		System.out.println("mapred.tasktracker.map.tasks.maximum = " + conf.get("mapreduce.tasktracker.map.tasks.maximum") );
		System.out.println("mapred.tasktracker.reduce.tasks.maximum = " + conf.get("mapreduce.tasktracker.reduce.tasks.maximum") );
		System.out.println("mapred.child.java.opts = " + conf.get("mapred.child.java.opts") );
		System.out.println("mapred.reduce.tasks = " + conf.get("mapreduce.job.reduces") );
		System.out.println("mapred.min.split.size = " + conf.get("mapreduce.input.fileinputformat.split.minsize") );
		
		System.out.println("mapred.map.tasks = " + conf.get("mapreduce.job.maps") );
		System.out.println("mapred.map.tasks.speculative.execution = " + conf.get("mapreduce.map.speculative") );
		System.out.println("mapred.reduce.tasks.speculative.execution = " + conf.get("mapreduce.reduce.speculative") );
		System.out.println("mapred.job.reuse.jvm.num.tasks = " + conf.get("mapreduce.job.jvm.numtasks") );
		System.out.println("io.sort.mbs = " + conf.get("mapreduce.task.io.sort.mb") );
		System.out.println("io.sort.factor = " + conf.get("mapreduce.task.io.sort.factor") );
		System.out.println("io.sort.record.percent = " + conf.get("io.sort.record.percent") );
		
		System.out.println("io.sort.spill.percent = " + conf.get("mapreduce.map.sort.spill.percent") );
		System.out.println("min.num.spill.for.combine = " + conf.get("min.num.spill.for.combine") );
		System.out.println("mapred.job.shuffle.input.buffer.percent = " + conf.get("mapreduce.reduce.shuffle.input.buffer.percent") );
		System.out.println("mapred.job.shuffle.merge.percent = " + conf.get("mapreduce.reduce.shuffle.merge.percent") );
		System.out.println("mapred.inmem.merge.threshold = " + conf.get("mapreduce.reduce.merge.inmem.threshold") );
		
		System.out.println("mapred.job.reduce.input.buffer.percent = " + conf.get("mapreduce.reduce.input.buffer.percent") );
		System.out.println("mapred.reduce.slowstart.completed.maps = " + conf.get("mapreduce.job.reduce.slowstart.completedmaps") );

		
		CsvtoSequence(conf,fs,csvTrainPath,sequenceFile,getfeature);				// transfer csv file to sequence file
		
		// Train data
		TrainNaiveBayesJob trainNaiveBayes = new TrainNaiveBayesJob();
		trainNaiveBayes.setConf(conf);
		
		fs.delete(new Path(outputDirectory),true);			// has to be deleted before run 
		fs.delete(new Path(tempDirectory),true);			// has to be deleted before run 

		Date trainstart = new Date();
		log.info("Start Training");
		
		
		sequenceFile = uri + sequenceFile;					// absolute path
		outputDirectory = uri + outputDirectory;
		tempDirectory = uri + tempDirectory;

		trainNaiveBayes.run(new String[] { "--input", sequenceFile, "--output", outputDirectory, "--overwrite", "--tempDir", tempDirectory });	
		
		NaiveBayesModel naiveBayesModel = NaiveBayesModel.materialize(new Path(outputDirectory), conf);

		Date trainend = new Date();
		log.info("End Training");
		
		Predict(conf, fs, csvTestPath, naiveBayesModel , getfeature);	// predict
		
		
		System.out.println("Training Time = " + (trainend.getTime() - trainstart.getTime() ) + " milliseconds" );
		
		System.out.println("**********************************************" );
		log.info("Job Finished");
		
		HadoopUtil.delete(conf, new Path(sequenceFile));
	}
	
	
	// To transfer csv file to sequence file for training set
	public static void CsvtoSequence(Configuration conf, FileSystem fs,String csvPath,String sequenceFile, int[] getfeature) throws IOException, Exception
	{				
		String line[];
		int feature_in ;		//index of features
		int label_in = 11;
		int i = 0;					//index of vector
		
		int ignore = label_in - getfeature.length;	//how many ignored feature
 		
		Path seqFilePath = new Path(sequenceFile);	
		Path cp= new Path(csvPath);		
		
		InputStreamReader isreader = new InputStreamReader(fs.open(cp), "UTF-8");
		BufferedReader br = new BufferedReader(isreader);
		CSVReader reader = new CSVReader(br);
		
		fs.delete(seqFilePath,false);
		
		SequenceFile.Writer writer = SequenceFile.createWriter(fs, conf, seqFilePath, Text.class, VectorWritable.class);

		try
		{
			for(line = reader.readNext(); line != null ; line = reader.readNext())
			//while ((line = reader.readNext()) != null)
			{
				feature_in = 0 ;
				
				Vector vector = new RandomAccessSparseVector(line.length-1-ignore, line.length-1-ignore);	// number of features
				
				
				for ( i =0 ,feature_in = 0; feature_in<label_in; feature_in++)						// handling features
				{
					if (Arrays.binarySearch(getfeature, feature_in) < 0 ) { 		// current feature_in is a ignore feature, skip
						continue;
					}
//					System.out.println("i is " + i );
//					System.out.println("feature_in is " + feature_in);
//					System.out.println("value is " + line[feature_in]);
					if (isNumeric(line[feature_in])){
						vector.set(i, processNumeric(line[feature_in]));
						
					}
					else{
						vector.set(i, processString(line[feature_in]));
					}
					i++;
				}
			
				String label = "";
			
				if (line[label_in].equals("normal")) {
					label = line[label_in];
				}
				else {
					label = "suspicious";
				}
				
				MahoutVector mahoutVector = new MahoutVector();		// format is label, features(vector)
				mahoutVector.label = label;
				mahoutVector.vector = vector;				
				VectorWritable vectorWritable = new VectorWritable();
				vectorWritable.set(mahoutVector.vector);
				writer.append(new Text("/" + mahoutVector.label + "/"), vectorWritable);

			}
		
		}
		finally {
			reader.close();
			isreader.close();
			br.close();
			writer.close();
			System.out.println("return finished");

		}
		
	}
	
	protected static double processNumeric(String features)
	{
		Double d ;
		d = Double.parseDouble(features);

		return d;
	}
	
	static double processString(String features)
	{
		long wordCount = 1;
		final Map<String, Long> words = Maps.newHashMap();
		
		Long a = words.get(features);
		if (a == null)
		{
			a = wordCount++;
			words.put(features, a);
		}
		
		return a;
	}

	public static boolean isNumeric(String features)
	{
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        String bigStr;
        try {
            bigStr = new BigDecimal(features).toString();
        } catch (Exception e) {
            return false;//exception
        }

        Matcher isNum = pattern.matcher(bigStr); // all match
        if (!isNum.matches()) {
            return false;
        }
        return true;
	}
	
	public static void Predict(Configuration conf, FileSystem fs, String csvTestPath, NaiveBayesModel naiveBayesModel, int[] getfeature) throws IOException
	{
		
		StandardNaiveBayesClassifier classifier = new StandardNaiveBayesClassifier(naiveBayesModel);	

		String line[];
		int feature_in ;		//index of features
		int label_in = 11;
		int i = 0;					//index of vector
		int tp = 0;
		int fp = 0;
		int fn = 0;
		int tn = 0;
		int total_test = 0;
		int ignore = label_in - getfeature.length;	//how many ignored feature
		
		
		Path cp= new Path(csvTestPath);		
		
		InputStreamReader isreader = new InputStreamReader(fs.open(cp), "UTF-8");
		BufferedReader br = new BufferedReader(isreader);
		CSVReader reader = new CSVReader(br);
		
		// Start test
		Date teststart = new Date();
		log.info("Start Prediction");
		
		try
		{
			for(line = reader.readNext(); line != null ; line = reader.readNext())
			{
				feature_in = 0 ;
				
				Vector vector = new RandomAccessSparseVector(line.length-1-ignore, line.length-1-ignore);	// number of features
				
				for ( i=0 , feature_in = 0; feature_in<label_in ; feature_in++)						// handling features
				{
					if (Arrays.binarySearch(getfeature, feature_in) < 0 ) { 		// current feature_in is a ignore feature, skip
						continue;
					}
//					System.out.println(" test i is " + i);
//					System.out.println("feature_in is " + feature_in);
//					System.out.println("value is " + line[feature_in]);
					if (isNumeric(line[feature_in])){
						vector.set(i, processNumeric(line[feature_in]));
						
					}
					else{
						vector.set(i, processString(line[feature_in]));
					}
					i++;
				}
			
				String label = "";
			
				if (line[label_in].equals("normal")) {
					label = line[label_in];
				}
				else {
					label = "suspicious";
				}
				
				MahoutVector mahoutVector = new MahoutVector();		// format is label, features(vector)
				mahoutVector.label = label;
				mahoutVector.vector = vector;				

		
				// Start test
				
				Vector prediction = classifier.classifyFull(mahoutVector.vector);
				// 0 - normal , 1 - suspicious
				double normal_score = prediction.get(0);
				double suspicious_score = prediction.get(1);
				int predictlabel;
				double bestscore;
	    	
				if (normal_score>suspicious_score) {			//normal
					predictlabel=0;
					bestscore=normal_score;
				}
				else {
					predictlabel=1;
					bestscore=suspicious_score;
				}

//				System.out.println("True label is:" + Test_mahoutVector.label);
//				System.out.println("predictlabel is:"+predictlabel);
			
				if (mahoutVector.label.equals("suspicious") &&  predictlabel == 1){ // true positive
					tp++;
					total_test++;
				}
				else if (mahoutVector.label.equals("normal") &&  predictlabel == 1){ // false positive
					fp++;
					total_test++;
				}
				else if (mahoutVector.label.equals("suspicious") &&  predictlabel == 0){ // false negative
					fn++;
					total_test++;
				}
				else if (mahoutVector.label.equals("normal") &&  predictlabel == 0){ // true negative
					tn++;
					total_test++;
				}
				else {
					total_test++;
				}
			
//			System.out.println("predict label is " +predictlabel + "  score:"+bestscore);
			}
		
		// Train End
		Date testend = new Date();
		log.info("End Prediction");
		
		// calculate accuracy
		
		double accuracy, DR, FPR, Precision, Recall,F1;
		
		accuracy = (double)((double)(tp+tn) / (double)(tp+fp+fn+tn));
		DR = (double)(tp/(double)(tp+fp));
		FPR = (double)fp / (double)(fp+tn);
		Precision = (double)tp / (double)(tp+fp);
		Recall = (double)tp /(double) (tp+fn);
		F1 =(double)( 2.0 / (double)( ( 1/Precision ) + ( 1 / Recall) ));
		
		System.out.println("********************Metrics********************" );
		
		System.out.println("number of features: " + naiveBayesModel.numFeatures());
		System.out.println("number of labels: " + naiveBayesModel.numLabels());
		
		System.out.println("total test data = " + total_test);
		System.out.println("tp:" +tp + "  fp:" +fp +"  fn:" +fn + "  tn:" +tn);
		System.out.println("accuracy = " + accuracy);
		System.out.println("Detection Rate = " + DR);
		System.out.println("False positive rate = " + FPR);
		System.out.println("Precision = " + Precision);
		System.out.println("Recall = " + Recall);
		System.out.println("F1 score = " + F1);
		
		System.out.println("Predict Time = " + (testend.getTime() - teststart.getTime() ) + " milliseconds" );

	}		
	finally {
		reader.close();
		br.close();
		isreader.close();
		}
		
		
	}
	
	
}
