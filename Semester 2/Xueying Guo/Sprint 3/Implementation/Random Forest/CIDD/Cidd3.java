// map reduce for training and prediction

package randomforest.random;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.mahout.classifier.df.DFUtils;
import org.apache.mahout.classifier.df.DecisionForest;
import org.apache.mahout.classifier.df.builder.DecisionTreeBuilder;
import org.apache.mahout.classifier.df.data.Data;
import org.apache.mahout.classifier.df.data.DataConverter;
import org.apache.mahout.classifier.df.data.DataLoader;
import org.apache.mahout.classifier.df.data.Dataset;
import org.apache.mahout.classifier.df.data.DescriptorException;
import org.apache.mahout.classifier.df.data.DescriptorUtils;
import org.apache.mahout.classifier.df.data.Instance;
import org.apache.mahout.classifier.df.mapreduce.Builder;
import org.apache.mahout.classifier.df.mapreduce.Classifier;
import org.apache.mahout.classifier.df.mapreduce.Classifier.CMapper;
import org.apache.mahout.classifier.df.mapreduce.partial.PartialBuilder;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable;
import org.uncommons.maths.Maths;

import com.google.common.io.Closeables;

public class Cidd3 {

	
	 public static void main(String[] args) throws Exception {
		 
		 System.out.println("Job Start " + new Date() );

			Configuration conf = new Configuration();
			String uri = "hdfs://localhost:9000";		// Run on pseudo mode
//			String uri = "hdfs://master:9000";			// Run on cluster mode
			FileSystem fs = FileSystem.get(new URI(uri), conf);
		    conf.set("fs.defaultFS", uri);

			String train_dataName = "/user/hadoop/rf/CIDD/train/tr1.csv";
			String test_dataName = "/user/hadoop/rf/CIDD/test/te1.csv";
			String train_datasetName = "/user/hadoop/rf/CIDD/train/descriptor.info";
			String modelName = "/user/hadoop/rf/CIDD/output";
			String prediction_file = "/user/hadoop/rf/CIDD/prediction";
		    String workpath = fs.getWorkingDirectory().toString();
/*			
		    String train_dataName = args[0];
			String test_dataName = args[1];
			String train_datasetName = args[2];
			String modelName = args[3];
			String prediction_file = args[4];
		  
	*/	
//			String descriptor = "N N N N N N N N N N N L";
			String descriptor = "11 N L";
			
			int feature_num = 11;
			int nbTrees = 5;							 // Number of trees to grow
		    int m = (int) Math.floor(Maths.log(2, feature_num+1) + 1);
//		    boolean complemented = false;
//		    Integer minSplitNum = 3;					// minimum number for split
//		    Double minVarianceProportion = 2.0;		// minimum proportion of the total variance for split
		    Long seed = (long) 350;

		    Path train_dataPath = new Path(uri + train_dataName);
		    Path train_datasetPath = new Path(train_datasetName);
		    Path modelPath = new Path(uri + modelName);
		    Path test_dataPath = new Path(uri + test_dataName);
		    Path predictionPath = new Path(uri + prediction_file);
		    
		    // Generate descriptor file
		    runTool(conf, uri + train_dataName, descriptor, uri + train_datasetName, false);
		  	
		    // Build Tree
		    DecisionTreeBuilder treeBuilder = new DecisionTreeBuilder();
		    treeBuilder.setM(m);
//		    treeBuilder.setComplemented(complemented);
//		    treeBuilder.setMinSplitNum(minSplitNum);
//		    treeBuilder.setMinVarianceProportion(minVarianceProportion);
		    
		    // Build Forest
		    System.out.println("Partial Mapred implementation" );
		    Builder forestBuilder = new PartialBuilder(treeBuilder, train_dataPath, train_datasetPath, seed, conf);

		    fs.delete(modelPath,true);					// delete before create
		    fs.delete(new Path(workpath + "/output"),true);	// delete before create
		    
		    
		    Date trainstart = new Date();
			System.out.println("Start Training (Building the forest...)" + trainstart);
		    long time = System.currentTimeMillis();

		    DecisionForest forest = forestBuilder.build(nbTrees);		//forest is the model

			Date trainend = new Date();
			System.out.println("End Training " + trainend);
		    
		    time = System.currentTimeMillis() - time;
		    
		    System.out.println("Build Time: " + DFUtils.elapsedTime(time));
		    System.out.println("Forest num Nodes: " + forest.nbNodes());
		    System.out.println("Forest mean num Nodes: " + forest.meanNbNodes());
		    System.out.println("Forest mean max Depth: " + forest.meanMaxDepth());

		    // store the decision forest in the output path
		    Path forestPath = new Path(modelPath, "forest.seq");
		    System.out.println("Storing the forest in: " + forestPath);
		    
		    DFUtils.storeWritable(conf, forestPath, forest);		
		    
		    System.out.println("finish building forest model");

		    
		    Dataset dataset = Dataset.load(conf, train_datasetPath);
		    DataConverter converter = new DataConverter(dataset);
		    		    
		    Random rng = RandomUtils.getRandom();

		    	    
			System.out.println("Adding the dataset to the DistributedCache");
		    DistributedCache.addCacheFile(train_datasetPath.toUri(), conf);

		    System.out.println("Adding the decision forest to the DistributedCache");
		    DistributedCache.addCacheFile(forestPath.toUri(), conf);
		    
			Date teststart = new Date();
			System.out.println("Start Testing " + teststart);

		    Job job = new Job(conf, "decision forest classifier");
		    fs.delete(predictionPath,true);

		    job.setJarByClass(Cidd3.class);
		    
		    FileInputFormat.setInputPaths(job, test_dataPath);
		    FileOutputFormat.setOutputPath(job, predictionPath);
		    
		    job.setOutputKeyClass(DoubleWritable.class);
		    job.setOutputValueClass(Text.class);
		    
		    job.setMapperClass(CMapper.class);
		    job.setNumReduceTasks(0); // no reducers

		    job.setInputFormatClass(CTextInputFormat.class);
		    job.setOutputFormatClass(SequenceFileOutputFormat.class);

		    System.out.println("Running the mapper job..." + new Date());
		    if (!job.waitForCompletion(true)) {
		      throw new IllegalStateException("Job failed!");
		    }

		    
			Date testend = new Date();
			System.out.println("End Testing " + testend);
		    
		    //Calculate Accuracy
			int tp = 0;
			int fp = 0;
			int fn = 0;
			int tn = 0;
			int total_test = 0;
		    
		    Path[] outfiles = DFUtils.listOutputFiles(fs, predictionPath);
		    for (Path path : outfiles) {
		    	FSDataOutputStream ofile = null;
		    	try {
		            for (Pair<DoubleWritable,Text> record : new SequenceFileIterable<DoubleWritable,Text>(path, true, conf)) {
		              
		            	
		              double key = record.getFirst().get();				//true label
		              String value = record.getSecond().toString();		//predict label
		              
		              
		              if (value.contains("csv")) {			// this is the first value, it contains the name of the input file
		                  continue;
		                }
		              else {
		            	  double prediction = Double.valueOf(value);			//predict label
//			              System.out.println("key " + key );
//			              System.out.println("prediction " + prediction );
		            	  if ( key == 0.0 && prediction == 0.0) {
				        	  tn ++; total_test++;
				          }else if(key != 0.0 && prediction == 0.0) {
				        	  fn ++; total_test++;
				          }else if (key == 0.0 && prediction != 0.0) {
				        	  fp ++; total_test++;
				          }else if (key != 0.0 && prediction != 0.0) {
				        	  tp ++ ;total_test++;
				          }else {total_test++;}
		              }
		            }
		          } finally {
		            Closeables.close(ofile, false);
		          }
		    	
		    }
		    
		    HadoopUtil.delete(conf, predictionPath);
		    	      
			// calculate accuracy
			
			double accuracy, DR, FPR, Precision, Recall,F1;
			
			accuracy = (double)((double)(tp+tn) / (double)(tp+fp+fn+tn));
			DR = (double)(tp/(double)(tp+fp));
			FPR = (double)fp / (double)(fp+tn);
			Precision = (double)tp / (double)(tp+fp);
			Recall = (double)tp /(double) (tp+fn);
			F1 =(double)( 2.0 / (double)( ( 1/Precision ) + ( 1 / Recall) ));
	      
			System.out.println("********************Metrics********************" );
			
			System.out.println("total test data = " + total_test);
			System.out.println("tp:" +tp + "  fp:" +fp +"  fn:" +fn + "  tn:" +tn);
			System.out.println("accuracy = " + accuracy);
			System.out.println("Detection Rate = " + DR);
			System.out.println("False positive rate = " + FPR);
			System.out.println("Precision = " + Precision);
			System.out.println("Recall = " + Recall);
			System.out.println("F1 score = " + F1);
			
			System.out.println("Training Time = " + (trainend.getTime() - trainstart.getTime() ) + " milliseconds" );
			System.out.println("Predict Time = " + (testend.getTime() - teststart.getTime() ) + " milliseconds" );
			
			System.out.println("**********************************************" );
			System.out.println("Job Finished!" + new Date() );
		    
		  }
		  
		  protected static Data loadData(Configuration conf, Path dataPath, Dataset dataset) throws IOException {
			  	System.out.println("Loading the data...");
			    FileSystem fs = dataPath.getFileSystem(conf);
			    Data data = DataLoader.loadData(dataset, fs, dataPath);
			    System.out.println("Data Loaded");
			    
			    return data;
			  }

		  private static void runTool(Configuration conf, String dataPath, String description, String filePath, boolean regression)
				    throws DescriptorException, IOException {
				    
			  FileSystem fs = FileSystem.get(conf);
			  
			  Path fpath = new Path(filePath);
			  
			  System.out.println("Generating the descriptor..." + new Date());
			  String descriptor = DescriptorUtils.generateDescriptor(description);


			  System.out.println("generating the dataset..." + new Date());
			  Dataset dataset = generateDataset(descriptor, dataPath, regression);
			  
//			  System.out.println(dataset);
			  
			  System.out.println("storing the dataset description" + new Date());
			  String json = dataset.toJSON();

			  DFUtils.storeString(conf, fpath, json);

				  }

			private static Dataset generateDataset(String descriptor, String dataPath, boolean regression) throws IOException,
			DescriptorException {
			  Path path = new Path(dataPath);
			  FileSystem fs = path.getFileSystem(new Configuration());

			  return DataLoader.generateDataset(descriptor, regression, fs, path);
		}
			  	  
			  
			  private static class CTextInputFormat extends TextInputFormat {
				    @Override
				    protected boolean isSplitable(JobContext jobContext, Path path) {
				      return false;
				    }
				  }

			  
			  public static class CMapper extends Mapper<LongWritable, Text, DoubleWritable, Text> {

				    /** used to convert input values to data instances */
				    private DataConverter converter;
				    private DecisionForest forest;
				    private final Random rng = RandomUtils.getRandom();
				    private boolean first = true;
				    private final Text lvalue = new Text();
				    private Dataset dataset;
				    private final DoubleWritable lkey = new DoubleWritable();

				    @Override
				    protected void setup(Context context) throws IOException, InterruptedException {
				      super.setup(context);    //To change body of overridden methods use File | Settings | File Templates.

				      Configuration conf = context.getConfiguration();

				      Path[] files = HadoopUtil.getCachedFiles(conf);

				      if (files.length < 2) {
				        throw new IOException("not enough paths in the DistributedCache");
				      }
				      dataset = Dataset.load(conf, files[0]);
				      converter = new DataConverter(dataset);

				      forest = DecisionForest.load(conf, files[1]);
				      if (forest == null) {
				        throw new InterruptedException("DecisionForest not found!");
				      }
				    }

				    @Override
				    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
				      if (first) {
				        FileSplit split = (FileSplit) context.getInputSplit();

				        Path path = split.getPath(); // current split path
				        lvalue.set(path.getName());
				        lkey.set(key.get());
				        context.write(lkey, lvalue);


				        first = false;
				      }

				      String line = value.toString();
				      if (!line.isEmpty()) {
				        Instance instance = converter.convert(line);
				        double prediction = forest.classify(dataset, rng, instance);
				        lkey.set(dataset.getLabel(instance));
				        lvalue.set(Double.toString(prediction));
				        context.write(lkey, lvalue);
				      }
				    }
				  }
}
