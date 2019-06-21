package bdca.kdd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.classifier.AbstractVectorClassifier;
import org.apache.mahout.classifier.naivebayes.NaiveBayesModel;
import org.apache.mahout.classifier.naivebayes.StandardNaiveBayesClassifier;
import org.apache.mahout.classifier.naivebayes.test.TestNaiveBayesDriver;
import org.apache.mahout.classifier.naivebayes.training.TrainNaiveBayesJob;

public class HadoopNB {
	private String inputTrainPath;
	private Path sequenceTrainPath;
	private String inputTestPath;
	private Path sequenceTestPath;
	private String inputPath;
	private Path sequencePath;
	private String outputDir;
	private String modelDir;
	private SequenceFile.Writer sequenceWriter;
	private Configuration conf = new Configuration();
	private List<String> labels = new ArrayList<>();
	private FileSystem fs;
	private NaiveBayesModel naiveBayesModel;
	
	private Long trainStart;
	private Long trainEnd;
	private Long testStart;
	private Long testEnd;
	
	HadoopNB(String inputTrainPath, String sequenceTrainPath, String inputTestPath, String sequenceTestPath, String modelDir, String outputDir) {
		this.inputTrainPath = inputTrainPath;
		this.sequenceTrainPath = new Path(sequenceTrainPath);
		this.inputTestPath = inputTestPath;
		this.sequenceTestPath = new Path(sequenceTestPath);
		this.modelDir = modelDir;
		this.outputDir = outputDir;
	}
	
	public List<String> getAllLabels() {
		return labels;
	}	
	
	public static boolean isNumeric(String feature) {
		Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
		Matcher matcher = pattern.matcher(feature);
		if (matcher.matches() == false) {
			return false;
		}
		return true;
	}
	
	public void parseInputFile(int labelInd, String input) {
		try {
			if (input.equals("train")) {
				this.sequencePath = this.sequenceTrainPath;
				this.inputPath = this.inputTrainPath;
			} else if (input.equals("test")) {
				this.sequencePath = this.sequenceTestPath;
				this.inputPath = this.inputTestPath;
			}
			
//			BufferedReader bufferedReader = new BufferedReader(new FileReader(this.inputPath));
//			fs = FileSystem.getLocal(conf);
			fs = FileSystem.get(new URI("hdfs://master:9000"),conf);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fs.open(new Path(this.inputPath))));
			
			
			
//			fs = FileSystem.getLocal(conf);
			if(fs.exists(this.sequencePath)) {
				fs.delete(this.sequencePath, true);
			}
			
			
			sequenceWriter = SequenceFile.createWriter(fs, conf, this.sequencePath, Text.class, VectorWritable.class);
			VectorWritable vectorWritable = new VectorWritable();
			
			String line = null;
			String[] featureAndLabel = null;
			String[] featureArr = null;
			String label = null;
			Vector featureVector = null;
			
			Long identifier = 0L;
			
			while((line = bufferedReader.readLine()) != null) {
				featureAndLabel = line.split(",");
				
				if (labelInd != featureAndLabel.length) {
					System.out.println("featureAndLabel.length: " + featureAndLabel.length);
					System.out.println("data preprocessing: missing data");
					break;
				}
				
				featureArr = new String[featureAndLabel.length - 1];
				for (int i = 0; i < featureAndLabel.length; i++) {
					if (i != featureAndLabel.length - 1) {
						featureArr[i] = featureAndLabel[i];
					} else {
						label = featureAndLabel[i];
					}
				}
				if(!labels.contains(label)) {
					labels.add(label);
				}
				
				Text key = new Text("/" + label + "/" + identifier);
				identifier ++;
				featureVector = new RandomAccessSparseVector(featureArr.length, featureArr.length);
				
				for (int i = 0; i < featureArr.length; i++) {
					String feature = featureArr[i];
					// preprocess the data, change to double
					if(isNumeric(feature)) {
						featureVector.set(i, Double.parseDouble(feature));
						
						if (Double.parseDouble(feature) > Double.MAX_VALUE) {
							featureVector.set(i, Double.MAX_VALUE);
							System.out.println("Double.MAX_VALUE exception");
						}
					}
					// non-number
					else {
						// String in CIDDS
						if (feature.equals("EXTSERVER")) {
							feature = "1";
							featureVector.set(i, Double.parseDouble(feature));
						}
						else if (feature.indexOf("M") != -1) {
							int ind = feature.indexOf("M");
							String val = feature.substring(0, ind);
							featureVector.set(i, Double.parseDouble(val) * 1024);
						}
						// String in CICIDS2017
						else if (feature.equals("Infinity")) {
							featureVector.set(i, Double.parseDouble("-1"));
						}
						else if (feature.equals("NaN")) {
							featureVector.set(i, Double.parseDouble("0"));
						}
						else if (feature.indexOf("E") != -1) {
							int ind = feature.indexOf("E");
							String val = feature.substring(0, ind);
							String pow = feature.substring(ind + 1, feature.length());
							double dval = Double.parseDouble(val);
							
							if (!pow.equals("")) {
								for (int j = 0; j < Double.parseDouble(pow); j++) {
									dval *= 10;
								}
								featureVector.set(i, dval);
							}
						}
						else {
							System.out.println(feature);
						}
					}
				}
				vectorWritable.set(featureVector);
				sequenceWriter.append(key, vectorWritable);
			}
			bufferedReader.close();
			fs.close();
			sequenceWriter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void train() {
		System.out.println("Start training Mahout Naive Bayes");
		
		try {
			
			
//			fs = FileSystem.getLocal(conf);
			fs = FileSystem.get(new URI("hdfs://master:9000"),conf);
			TrainNaiveBayesJob trainNaiveBayes = new TrainNaiveBayesJob();
			trainNaiveBayes.setConf(conf);
			if(fs.exists(new Path(modelDir))) {
				fs.delete(new Path(modelDir),true);
			}
			
			
			this.trainStart = System.currentTimeMillis();
			
			trainNaiveBayes.run(new String[] { 
				"--input", this.sequenceTrainPath.toString(),
//				"--input", "/home/jzd/eclipse-workspace2/input/part-r-00000",	
				"--output", modelDir,
//				"--labelIndex", "/home/jzd/eclipse-workspace2/input/labelIndex",
				"--labelIndex", "hdfs://master:9000/ubuntu/input/labelIndex",
				"--overwrite",
//				"--tempDir", "/home/jzd/eclipse-workspace2/tmp/"
				"--tempDir", "hdfs://master:9000/ubuntu/output/tmp",
			});
			
			
			naiveBayesModel = NaiveBayesModel.materialize(new Path(modelDir), conf);
			
			this.trainEnd = System.currentTimeMillis();
	
			System.out.println("feature number: " + naiveBayesModel.numFeatures());
			System.out.println("label number: " + naiveBayesModel.numLabels()); 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void test() {
		System.out.println("Start testing Mahout Naive Bayes");
		
		try {
//			fs = FileSystem.getLocal(conf);
			fs = FileSystem.get(new URI("hdfs://master:9000"),conf);
			TestNaiveBayesDriver testNaiveBayes = new TestNaiveBayesDriver();
			testNaiveBayes.setConf(conf);
			if(fs.exists(new Path(outputDir))) {
				fs.delete(new Path(outputDir),true);
			}

			this.testStart = System.currentTimeMillis();
			
			testNaiveBayes.run(new String[] {
				"--input", this.sequenceTrainPath.toString(),
				"--model", modelDir,
//				"--labelIndex", "/home/jzd/eclipse-workspace2/input/labelIndex",
				"--labelIndex", "hdfs://master:9000/ubuntu/input/labelIndex",
				"--overwrite",
				"--output", outputDir
			});
			
			this.testEnd = System.currentTimeMillis();
	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public static void main(String[] args) {
		System.out.println("args: " + args.toString());
		// local
//		HadoopNB hadoopNB = new HadoopNB("/home/jzd/eclipse-workspace2/input/KDDTrain.txt", "/home/jzd/eclipse-workspace2/input/KDDTrainSeq", "/home/jzd/eclipse-workspace2/input/KDDTest.txt", "/home/jzd/eclipse-workspace2/input/KDDTestSeq", "/home/jzd/eclipse-workspace2/output/KDDModel", "/home/jzd/eclipse-workspace2/output/KDDOutput");
//		HadoopNB hadoopNB = new HadoopNB("/home/jzd/eclipse-workspace2/input/trainDARPA-Disc.txt", "/home/jzd/eclipse-workspace2/input/trainDARPA-DiscSeq", "/home/jzd/eclipse-workspace2/input/testDARPA-Disc.txt", "/home/jzd/eclipse-workspace2/input/testDARPA-DiscSeq", "/home/jzd/eclipse-workspace2/output/DARPAModel", "/home/jzd/eclipse-workspace2/output/DARPAOutput");
//		HadoopNB hadoopNB = new HadoopNB("/home/jzd/eclipse-workspace2/input/preprd-train-CIDIDS.txt", "/home/jzd/eclipse-workspace2/input/preprd-train-CIDIDSSeq", "/home/jzd/eclipse-workspace2/input/preprd-test-CIDIDS.txt", "/home/jzd/eclipse-workspace2/input/preprd-test-CIDIDSSeq", "/home/jzd/eclipse-workspace2/output/CIDDSModel", "/home/jzd/eclipse-workspace2/output/CIDDSOutput");
//		HadoopNB hadoopNB = new HadoopNB("/home/jzd/eclipse-workspace2/input/preprd-train-CICIDS2017.txt", "/home/jzd/eclipse-workspace2/input/preprd-train-CICIDS2017Seq", "/home/jzd/eclipse-workspace2/input/preprd-test-CICIDS2017.txt", "/home/jzd/eclipse-workspace2/input/preprd-test-CICIDS2017Seq", "/home/jzd/eclipse-workspace2/output/CICIDS2017Model", "/home/jzd/eclipse-workspace2/output/CICIDS2017Output");
//		HadoopNB hadoopNB = new HadoopNB("/home/jzd/eclipse-workspace2/input/preprd-train-CICIDS20172.txt", "/home/jzd/eclipse-workspace2/input/preprd-train-CICIDS20172Seq", "/home/jzd/eclipse-workspace2/input/preprd-train-CICIDS20172.txt", "/home/jzd/eclipse-workspace2/input/preprd-train-CICIDS2017Seq2.txt", "/home/jzd/eclipse-workspace2/output/CICIDS20172Model", "/home/jzd/eclipse-workspace2/output/CICIDS20172Output");
		
		
		// hdfs
//		HadoopNB hadoopNB = new HadoopNB("hdfs://master:9000//ubuntu/input/KDDTrain.txt", "hdfs://master:9000//ubuntu/input/KDDTrainSeq", "hdfs://master:9000//ubuntu/input/KDDTest.txt", "hdfs://master:9000//ubuntu/input/KDDTestSeq", "hdfs://master:9000//ubuntu/output/KDDModel", "hdfs://master:9000//ubuntu/output/KDDOutput");
//		HadoopNB hadoopNB = new HadoopNB("/jzd/input/trainDARPA-Disc.txt", "/jzd/input/trainDARPA-DiscSeq", "/jzd/input/testDARPA-Disc.txt", "/jzd/input/testDARPA-DiscSeq", "/jzd/output/DARPAModel", "/jzd/output/DARPAOutput");
		HadoopNB hadoopNB = new HadoopNB("hdfs://master:9000//ubuntu/input/preprd-train-CIDIDS.txt", "hdfs://master:9000//ubuntu/input/preprd-train-CIDIDSSeq", "hdfs://master:9000//ubuntu/input/preprd-test-CIDIDS.txt", "hdfs://master:9000//ubuntu/input/preprd-test-CIDIDSSeq", "hdfs://master:9000//ubuntu/output/CIDDSModel", "hdfs://master:9000//ubuntu/output/CIDDSOutput");
		
		
		
		hadoopNB.parseInputFile(12, "train"); // 42, 7, 12, 83
		hadoopNB.parseInputFile(12, "test");
		
		System.out.println(hadoopNB.getAllLabels().toString());
		
		hadoopNB.train();
		hadoopNB.test();
		
		System.out.println("Train Time: " + (hadoopNB.trainEnd - hadoopNB.trainStart));
		System.out.println("Test Time: " + (hadoopNB.testEnd - hadoopNB.testStart));
		
	}
	
} 
