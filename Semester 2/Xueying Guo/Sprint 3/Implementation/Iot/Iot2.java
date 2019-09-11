// this is sequential prediction, not map reduce prediction

package randomforest.random;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
import org.apache.mahout.classifier.df.mapreduce.partial.PartialBuilder;
import org.apache.mahout.common.RandomUtils;
import org.uncommons.maths.Maths;

import com.google.common.io.Closeables;

public class Iot2 {
	
	 public static void main(String[] args) throws Exception {
		 
		 System.out.println("Job Start " + new Date() );

			Configuration conf = new Configuration();
//			String uri = "hdfs://localhost:9000";		// Run on pseudo mode
			String uri = "hdfs://master:9000";			// Run on cluster mode
			FileSystem fs = FileSystem.get(new URI(uri), conf);
		    conf.set("fs.defaultFS", uri);

/*			String train_dataName = "/user/hadoop/rf/Iot/train/train1.csv";
			String test_dataName = "/user/hadoop/rf/Iot/test/test1.csv";
			String train_datasetName = "/user/hadoop/rf/Iot/train/descriptor.info";
			String modelName = "/user/hadoop/rf/Iot/output";
			String prediction_file = "/user/hadoop/rf/Iot/prediction";
*/		    String workpath = fs.getWorkingDirectory().toString();
			
		    String train_dataName = args[0];
			String test_dataName = args[1];
			String train_datasetName = args[2];
			String modelName = args[3];
			String prediction_file = args[4];
		    
		
//			String descriptor = "N N N N N N N N N N N L";
			String descriptor = "5 N 1 I 1 N 1 I 18 N 1 L";
			
			int feature_num = 26;
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
		    
		    
//		    System.out.println("Sequential classification...");
//		    long time2 = System.currentTimeMillis();
		    
		    Random rng = RandomUtils.getRandom();
		    
		    
		    fs.delete(predictionPath,true);
		    
		    testFile(fs, test_dataPath, predictionPath, converter, forest, dataset, rng);
		    
//		    time2 = System.currentTimeMillis() - time2;
//		    System.out.println("Classification Time: "+ DFUtils.elapsedTime(time2));
		    
			System.out.println("Training Time = " + (trainend.getTime() - trainstart.getTime() ) + " milliseconds" );
			
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
			
			
			  private static void testFile(FileSystem fs, Path inPath, Path outPath, DataConverter converter, DecisionForest forest,
					    Dataset dataset, Random rng) throws IOException {
					    // create the predictions file
				  
					int tp = 0;
					int fp = 0;
					int fn = 0;
					int tn = 0;
					int total_test = 0;
				  
				  FSDataOutputStream ofile = null;
				  
				    if (outPath != null) {
				        ofile = fs.create(outPath);
				      }

				    FSDataInputStream input = fs.open(inPath);
				    
					// Start test
					Date teststart = new Date();
					System.out.println("Start Predict " + teststart);
				  
				  try {
					  System.out.println("begin scan");
			Scanner scanner = new Scanner(input);
					  
				      while (scanner.hasNextLine()) {
				          String line = scanner.nextLine();
//				          System.out.println(line);
				          if (line.isEmpty()) {
				            continue; // skip empty lines
				          }
				          
				          Instance instance = converter.convert(line);
				          double prediction = forest.classify(dataset, rng, instance);
				          double truelable = dataset.getLabel(instance);
//				          System.out.println("truelable " + truelable);
//				          System.out.println("prediction " + prediction);
				          
//				          if (ofile != null) {
//				              ofile.writeChars(Double.toString(prediction)); // write the prediction
//				              ofile.writeChar('\n');
//				            }
				          
//				          results.add(new double[] {dataset.getLabel(instance), prediction});
				          
				          if ( truelable == 0.0 && prediction == 0.0) {
				        	  tn ++; total_test++;
				          }else if(truelable != 0.0 && prediction == 0.0) {
				        	  fn ++; total_test++;
				          }else if (truelable == 0.0 && prediction != 0.0) {
				        	  fp ++; total_test++;
				          }else if (truelable != 0.0 && prediction != 0.0) {
				        	  tp ++ ;total_test++;
				          }else {total_test++;}

				          
				      }
				      
						// Test End
						Date testend = new Date();
						System.out.println("End prediction " + testend);
				      
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
		    			
		    			System.out.println("Predict Time = " + (testend.getTime() - teststart.getTime() ) + " milliseconds" );

				      
				      scanner.close(); 
				  }
				  finally {
					  Closeables.closeQuietly(input);
				  }
				  
			  }

}
