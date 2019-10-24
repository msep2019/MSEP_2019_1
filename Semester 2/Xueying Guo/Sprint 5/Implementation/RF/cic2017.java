package randomforest.random;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.mahout.classifier.df.DFUtils;
import org.apache.mahout.classifier.df.DecisionForest;
import org.apache.mahout.classifier.df.builder.DecisionTreeBuilder;
import org.apache.mahout.classifier.df.data.Data;
import org.apache.mahout.classifier.df.data.DataConverter;
import org.apache.mahout.classifier.df.data.DataLoader;
import org.apache.mahout.classifier.df.data.Dataset;
import org.apache.mahout.classifier.df.data.DescriptorException;
import org.apache.mahout.classifier.df.data.DescriptorUtils;
import org.apache.mahout.classifier.df.mapreduce.Builder;
import org.apache.mahout.classifier.df.mapreduce.partial.PartialBuilder;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.Pair;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.common.iterator.sequencefile.SequenceFileIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uncommons.maths.Maths;

import com.google.common.io.Closeables;

import randomforest.random.Cidd3.CMapper;

public class cic2017 {
	
	private static final Logger log = LoggerFactory.getLogger(cic2017.class);

	
	 public static void main(String[] args) throws Exception {
		 
		 log.info("Job Start ! ");
		

			Configuration conf = new Configuration();
			String extraArgs[] = new GenericOptionsParser(conf, args).getRemainingArgs(); 
			String uri = "hdfs://localhost:9000";		// Run on pseudo mode
//			String uri = "hdfs://master:9000";			// Run on cluster mode
			FileSystem fs = FileSystem.get(new URI(uri), conf);
		    conf.set("fs.defaultFS", uri);
		    
			String train_dataName = "/user/hadoop/nb/cic2017/train/CIC_train2.csv";
			String test_dataName = "/user/hadoop/nb/cic2017/test/CIC_test2.csv";
			String train_datasetName = "/user/hadoop/rf/cic2017/train/descriptor.info";
			String modelName = "/user/hadoop/rf/cic2017/output";
			String prediction_file = "/user/hadoop/rf/cic2017/prediction";
			/*			String train_dataName = args[0];
			String test_dataName = args[1];
			String train_datasetName = args[2];
			String modelName = args[3];
			String prediction_file = args[4];
*/		    String workpath = fs.getWorkingDirectory().toString();
			
/*			String train_dataName = "/user/ubuntu/Hadoop/nb/CIDD/train/CIDD_train.csv";
			String test_dataName = "/user/ubuntu/Hadoop/nb/CIDD/test/CIDD_test.csv";
			String train_datasetName = "/user/ubuntu/Hadoop/rf/CIDD/train/descriptor.info";
			String modelName = "/user/ubuntu/Hadoop/rf/CIDD/output";
			String prediction_file = "/user/ubuntu/Hadoop/rf/CIDD/prediction";
*/
			int numSplits = 8;
			
			System.out.println("mapred.output.compress = " + conf.get("mapreduce.output.fileoutputformat.compress") );
			System.out.println("mapred.output.compression.type = " + conf.get("mapreduce.output.fileoutputformat.compress.type") );
			System.out.println("mapred.output.compression.codec = " + conf.get("mapreduce.output.fileoutputformat.compress.codec") );
			System.out.println("mapred.compress.map.output = " + conf.get("mapreduce.map.output.compress") );
			System.out.println("mapred.map.output.compression.codec = " + conf.get("mapreduce.map.output.compress.codec") );
			System.out.println("mapred.tasktracker.map.tasks.maximum = " + conf.get("mapred.tasktracker.map.tasks.maximum") );
			System.out.println("mapred.tasktracker.reduce.tasks.maximum = " + conf.get("mapreduce.tasktracker.reduce.tasks.maximum") );
			System.out.println("mapred.child.java.opts = " + conf.get("mapred.child.java.opts") );
			System.out.println("mapred.reduce.tasks = " + conf.get("mapreduce.job.reduces") );
			System.out.println("mapred.min.split.size = " + conf.get("mapreduce.input.fileinputformat.split.minsize") );
			
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

			
			String descriptor = "47 N 7 I 4 N 5 I 16 N L";
			
			int feature_num = 79;
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
		    log.info("Partial Mapred implementation");
		    Builder forestBuilder = new PartialBuilder(treeBuilder, train_dataPath, train_datasetPath, seed, conf);

		    fs.delete(modelPath,true);					// delete before create
		    fs.delete(new Path(workpath + "/output"),true);	// delete before create
		    
		    
		    Date trainstart = new Date();
			log.info("Start Training (Building the forest...)");
			long time = System.currentTimeMillis();

		    DecisionForest forest = forestBuilder.build(nbTrees);		//forest is the model

			Date trainend = new Date();
			log.info("End Training");
		    
		    time = System.currentTimeMillis() - time;
		    
		    log.info("Build Time: {}", DFUtils.elapsedTime(time));
		    log.info("Forest num Nodes: {}", forest.nbNodes());
		    log.info("Forest mean num Nodes: {}", forest.meanNbNodes());
		    log.info("Forest mean max Depth: {}", forest.meanMaxDepth());


		    // store the decision forest in the output path
		    Path forestPath = new Path(modelPath, "forest.seq");
		    log.info("Storing the forest in: {}", forestPath);
		    
		    DFUtils.storeWritable(conf, forestPath, forest);		
		    
		    log.info("finish building forest model");

		    
		    Dataset dataset = Dataset.load(conf, train_datasetPath);
		    DataConverter converter = new DataConverter(dataset);
		    		    
		    Random rng = RandomUtils.getRandom();

			log.info("Adding the dataset to the DistributedCache");
		    DistributedCache.addCacheFile(train_datasetPath.toUri(), conf);

		    log.info("Adding the decision forest to the DistributedCache");
		    DistributedCache.addCacheFile(forestPath.toUri(), conf);
		    
			Date teststart = new Date();
			log.info("Start Testing");


		    Job job = new Job(conf, "decision forest classifier");
		    fs.delete(predictionPath,true);


		    job.setJarByClass(kdd.class);
		    
		    FileInputFormat.setInputPaths(job, test_dataPath);
		    FileOutputFormat.setOutputPath(job, predictionPath);
		    
		    job.setOutputKeyClass(DoubleWritable.class);
		    job.setOutputValueClass(Text.class);
		    
		    job.setMapperClass(CMapper.class);
		    job.setNumReduceTasks(0); // no reducers

		    job.setInputFormatClass(CTextInputFormat.class);
		    job.setOutputFormatClass(SequenceFileOutputFormat.class);
		    

		    log.info("Running the mapper job...");
		    if (!job.waitForCompletion(true)) {
		      throw new IllegalStateException("Job failed!");
		    }

		    
			Date testend = new Date();
			log.info("End testing");
		    
		    //Calculate Accuracy
			int tp = 0;
			int fp = 0;
			int fn = 0;
			int tn = 0;
			int total_test = 0;
		    
		    Path[] outfiles = DFUtils.listOutputFiles(fs, predictionPath);
		    int pp=1;
		    for (Path path : outfiles) {
		    	FSDataOutputStream ofile = null;
		    	try {
		    		
		            for (Pair<DoubleWritable,Text> record : new SequenceFileIterable<DoubleWritable,Text>(path, true, conf)) {
		              
		            	
		              double key = record.getFirst().get();				//true label
		              String value = record.getSecond().toString();		//predict label
		             // String[] format=test_dataName.split(".");
		              String format = test_dataName.substring(test_dataName.indexOf(".")+1,test_dataName.length());
		              
		              if (value.contains(format)) {			// this is the first value, it contains the name of the input file
		                  continue;
		                }
		              else {
		            	  double prediction = Double.valueOf(value);			//predict label
		            	  if(pp == 4) { System.out.println("key " + key ); System.out.println("prediction " + prediction ); pp++;}
		            	  else {pp++;}
//		            	  System.out.println("pp " + pp++ );
//		            	  System.out.println("key " + key );
//			              System.out.println("prediction " + prediction );
		            	  if ( key == 5.0 && prediction == 5.0) {
				        	  tn ++; total_test++;
				          }else if(key != 5.0 && prediction == 5.0) {
				        	  fn ++; total_test++;
				          }else if (key == 5.0 && prediction != 5.0) {
				        	  fp ++; total_test++;
				          }else if (key != 5.0 && prediction != 5.0) {
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
			  	log.info("Loading the data...");
			    FileSystem fs = dataPath.getFileSystem(conf);
			    Data data = DataLoader.loadData(dataset, fs, dataPath);
			    log.info("Data Loaded");
			    
			    return data;
			  }

		  private static void runTool(Configuration conf, String dataPath, String description, String filePath, boolean regression)
				    throws DescriptorException, IOException {
				    
			  FileSystem fs = FileSystem.get(conf);
			  
			  Path fpath = new Path(filePath);
			  
			  log.info("Generating the descriptor...");
			  String descriptor = DescriptorUtils.generateDescriptor(description);

			  log.info("generating the dataset...");
			  Dataset dataset = generateDataset(descriptor, dataPath, regression);
			  
//			  System.out.println(dataset);
			  
			  log.info("storing the dataset description");
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

		

}
