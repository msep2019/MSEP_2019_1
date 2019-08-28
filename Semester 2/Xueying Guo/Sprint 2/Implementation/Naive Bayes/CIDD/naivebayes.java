package naivebayes.hadoop_nb;

// Naive bayes for CIDDS dataset

import java.io.BufferedReader;

public class naivebayes {


	public static void main(String[] args) throws Exception
	{
		Configuration conf = new Configuration();
/*		FileSystem fs = FileSystem.getLocal(conf);		// Run on Local Mode
		String sequenceFile = "/home/hadoop/Desktop/hadoop/nb/train/seq";
		String outputDirectory = "/home/hadoop/Desktop/hadoop/nb/output";
		String tempDirectory = "/home/hadoop/Desktop/hadoop/nb/temp";
		String csvTrainPath = "/home/hadoop/Desktop/hadoop/nb/train/CIDDTrain.csv";
		String csvTestPath = "/home/hadoop/Desktop/hadoop/nb/test/CIDDTest.csv";
*/
		String uri = "hdfs://localhost:9000";			// Run on pseudo mode
//		String uri = "hdfs://master:9000";			// Run on cluster mode
		
		FileSystem fs = FileSystem.get(new URI(uri), conf);
/*		String sequenceFile = "/user/hadoop/nb/CIDD/train/seq";
		String outputDirectory = "/user/hadoop/nb/CIDD/output";
		String tempDirectory = "/user/hadoop/nb/CIDD/temp";
		String csvTrainPath = "/user/hadoop/nb/CIDD/train/CIDDTrain.csv";
		String csvTestPath = "/user/hadoop/nb/CIDD/test/CIDDTest.csv";
*/
		String sequenceFile = args[0];
		String outputDirectory = args[1];
		String tempDirectory = args[2];
		String csvTrainPath = args[3];
		String csvTestPath = args[4];
		
		CsvtoSequence(conf,fs,csvTrainPath,sequenceFile);				// transfer csv file to sequence file
		
		// Train data
		TrainNaiveBayesJob trainNaiveBayes = new TrainNaiveBayesJob();
		trainNaiveBayes.setConf(conf);
		
		fs.delete(new Path(outputDirectory),true);			// has to be deleted before run 
		fs.delete(new Path(tempDirectory),true);			// has to be deleted before run 

		Date trainstart = new Date();
		System.out.println("Start Training " + trainstart);
		
		
		sequenceFile = uri + sequenceFile;					// absolute path
		outputDirectory = uri + outputDirectory;
		tempDirectory = uri + tempDirectory;

		trainNaiveBayes.run(new String[] { "--input", sequenceFile, "--output", outputDirectory, "--overwrite", "--tempDir", tempDirectory });	
		
		NaiveBayesModel naiveBayesModel = NaiveBayesModel.materialize(new Path(outputDirectory), conf);

		Date trainend = new Date();
		System.out.println("End Training " + trainend);
		
		Predict(conf, fs, csvTestPath, naiveBayesModel );	// predict
		
		
		System.out.println("Training Time = " + (trainend.getTime() - trainstart.getTime() ) + " milliseconds" );
		
		System.out.println("**********************************************" );
		System.out.println("Job Finished!" );
	}
	
	
	// To transfer csv file to sequence file for training set
	public static void CsvtoSequence(Configuration conf, FileSystem fs,String csvPath,String sequenceFile) throws IOException, Exception
	{				
		String line[];
		int feature_in ;		//index of features
		int label_in = 11;
		int i = 0;					//index of vector
		
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
				
				Vector vector = new RandomAccessSparseVector(line.length-1, line.length-1);	// number of features
				
				for ( feature_in = 0; feature_in<label_in ; feature_in++)						// handling features
				{
//					System.out.println("feature_in is " + feature_in);
//					System.out.println("value is " + line[feature_in]);
					if (isNumeric(line[feature_in])){
						vector.set(feature_in, processNumeric(line[feature_in]));
						
					}
					else{
						vector.set(feature_in, processString(line[feature_in]));
					}
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
	
	public static void Predict(Configuration conf, FileSystem fs, String csvTestPath, NaiveBayesModel naiveBayesModel) throws IOException
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
		
		Path cp= new Path(csvTestPath);		
		
		InputStreamReader isreader = new InputStreamReader(fs.open(cp), "UTF-8");
		BufferedReader br = new BufferedReader(isreader);
		CSVReader reader = new CSVReader(br);
		
		// Start test
		Date teststart = new Date();
		System.out.println("Start Predict " + teststart);
		
		try
		{
			for(line = reader.readNext(); line != null ; line = reader.readNext())
			//while ((line = reader.readNext()) != null)
			{
				feature_in = 0 ;
				
				Vector vector = new RandomAccessSparseVector(line.length-1, line.length-1);	// number of features
				
				for ( feature_in = 0; feature_in<label_in ; feature_in++)						// handling features
				{
					if (isNumeric(line[feature_in])){
						vector.set(feature_in, processNumeric(line[feature_in]));
						
					}
					else{
						vector.set(feature_in, processString(line[feature_in]));
					}
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
		System.out.println("End predict " + testend);
		
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
