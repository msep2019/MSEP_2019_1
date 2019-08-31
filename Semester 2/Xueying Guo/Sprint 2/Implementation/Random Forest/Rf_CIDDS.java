package randomforest.random;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.classifier.df.DFUtils;
import org.apache.mahout.classifier.df.DecisionForest;
import org.apache.mahout.classifier.df.builder.DecisionTreeBuilder;
import org.apache.mahout.classifier.df.data.Data;
import org.apache.mahout.classifier.df.data.DataLoader;
import org.apache.mahout.classifier.df.data.Dataset;
import org.apache.mahout.classifier.df.data.DescriptorException;
import org.apache.mahout.classifier.df.data.DescriptorUtils;
import org.apache.mahout.classifier.df.mapreduce.Builder;
import org.apache.mahout.classifier.df.mapreduce.Classifier;
import org.apache.mahout.classifier.df.mapreduce.partial.PartialBuilder;
import org.uncommons.maths.Maths;

public class Cidd {
	
	 public static void main(String[] args) throws Exception {

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
		    
		    // Test data
		    fs.delete(predictionPath,true);
		    
		    
//		    Prediction
			Date teststart = new Date();
			System.out.println("Start Testing " + teststart);
		    
		    Classifier classifier = new Classifier(modelPath, test_dataPath, train_datasetPath, predictionPath, conf);
		  
		    classifier.run();
		    
			Date testend = new Date();
			System.out.println("Start Testing " + testend);
		    
		//  End prediction
		    
		// Calculate Accuracy
		    int feature_in ;		//index of features
			int label_in = 11;
			int i = 0;					//index of vector
			int tp = 0;
			int fp = 0;
			int fn = 0;
			int tn = 0;
			int total_test = 0;
			
		    double[][] results = classifier.getResults();
		    if (results != null) {
		    	Dataset dataset = Dataset.load(conf, train_datasetPath);
		    	if (dataset.isNumerical(dataset.getLabelId())) {
		    		for (int row =0; row<results.length;row++ ) {
		 //   			System.out.println("true label is: "+results[row][0]);	    
		 //   			System.out.println("predict label is: " + results[row][1]);	
		    			if (results[row][0] == 0 && results[row][1] == 0 ){
		    	        	tn ++;
		    	        	total_test++;
		    	        }
		    	        else if(results[row][0] == 0 && results[row][1] != 0) {
		    	        	fp ++;
		    	        	total_test++;
		    	        }
		    	        else if(results[row][0] != 0 && results[row][1] == 0) {
		    	        	fn ++;
		    	        	total_test++;
		    	        }
		    	        else if(results[row][0] != 0 && results[row][1] != 0) {
		    	        	tp ++;
		    	        	total_test++;
		    	        }
		    		}
		    		

		    	}else {
		    		for (double[] res : results) {
//			            System.out.println("true label is: " + dataset.getLabelString(res[0]));
//			            System.out.println("predict label is: " + dataset.getLabelString(res[1]));   		
			        if (dataset.getLabelString(res[0]).equals("normal") && dataset.getLabelString(res[1]).equals("normal") ){
			        	tn ++;
			        	total_test++;
			        }
			        else if(dataset.getLabelString(res[0]).equals("normal") && !dataset.getLabelString(res[1]).equals("normal")) {
			        	fp ++;
			        	total_test++;
			        }
			        else if(!dataset.getLabelString(res[0]).equals("normal") && dataset.getLabelString(res[1]).equals("normal")) {
			        	fn ++;
			        	total_test++;
			        }
			        else if(!dataset.getLabelString(res[0]).equals("normal") && !dataset.getLabelString(res[1]).equals("normal")) {
			        	tp ++;
			        	total_test++;
			        }
		    		}     	
		    		}
		        System.out.println("Finish Prediction");
		    	
				
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
		    			System.out.println("Job Finished!" );

		    
		    }
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
			  
			  System.out.println("Generating the descriptor...");
			  String descriptor = DescriptorUtils.generateDescriptor(description);


			  System.out.println("generating the dataset...");
			  Dataset dataset = generateDataset(descriptor, dataPath, regression);
			  
//			  System.out.println(dataset);
			  
			  System.out.println("storing the dataset description");
			  String json = dataset.toJSON();

			  DFUtils.storeString(conf, fpath, json);

				  }

			private static Dataset generateDataset(String descriptor, String dataPath, boolean regression) throws IOException,
			DescriptorException {
			  Path path = new Path(dataPath);
			  FileSystem fs = path.getFileSystem(new Configuration());

			  return DataLoader.generateDataset(descriptor, regression, fs, path);
		}


}
