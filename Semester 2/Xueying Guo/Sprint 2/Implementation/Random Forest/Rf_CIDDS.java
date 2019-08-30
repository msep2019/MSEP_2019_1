package randomforest.rf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.common.CommandLineUtil;
import org.apache.mahout.common.RandomUtils;
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
import org.apache.mahout.classifier.df.mapreduce.inmem.InMemBuilder;
import org.apache.mahout.classifier.df.mapreduce.partial.PartialBuilder;
import org.apache.mahout.classifier.df.ref.SequentialBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uncommons.maths.Maths;

/**
 * Tool to builds a Random Forest using any given dataset (in UCI format). Can use either the in-mem mapred or
 * partial mapred implementations. Stores the forest in the given output directory
 */
public class Rf_CIDDS {
  
  private static final Logger log = LoggerFactory.getLogger(Rf_CIDDS.class);
  
  
  public static void main(String[] args) throws Exception {

	Configuration conf = new Configuration();
	String uri = "hdfs://localhost:9000";
	FileSystem fs = FileSystem.get(new URI(uri), conf);
    conf.set("fs.defaultFS", uri);

	String train_dataName = "/user/hadoop/rf/CIDD/train/train1.csv";
	String test_dataName = "/user/hadoop/rf/CIDD/test/test1.csv";
	String train_datasetName = "/user/hadoop/rf/CIDD/train/descriptor.info";
	String modelName = "/user/hadoop/rf/CIDD/output";
	String prediction_file = "/user/hadoop/rf/CIDD/prediction";
	
	String descriptor = "N N N N N N N N N N N L";
	
	boolean isPartial = true ;					 // use partial data implementation
    int nbTrees = 100;							 // Number of trees to grow
    int m = (int) Math.floor(Maths.log(2, 12) + 1);
    boolean complemented = false;
    Integer minSplitNum = 2;					// minimum number for split
    Double minVarianceProportion = 2.0;		// minimum proportion of the total variance for split
    Long seed = (long) 35;

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
    treeBuilder.setComplemented(complemented);
    treeBuilder.setMinSplitNum(minSplitNum);
    treeBuilder.setMinVarianceProportion(minVarianceProportion);
    
    // Build Forest
    System.out.println("Partial Mapred implementation" );
    Builder forestBuilder = new PartialBuilder(treeBuilder, train_dataPath, train_datasetPath, seed, conf);

    fs.delete(modelPath,true);					// delete before create
    fs.delete(new Path("hdfs://localhost:9000/user/hadoop/output"),true);
    
    System.out.println("Building the forest...");
    long time = System.currentTimeMillis();
    

    DecisionForest forest = forestBuilder.build(nbTrees);		//forest is the model

    time = System.currentTimeMillis() - time;
    
    System.out.println("Build Time: {}" + DFUtils.elapsedTime(time));
    System.out.println("Forest num Nodes: {}" + forest.nbNodes());
    System.out.println("Forest mean num Nodes: {}" + forest.meanNbNodes());
    System.out.println("Forest mean max Depth: {}" + forest.meanMaxDepth());

    // store the decision forest in the output path
    Path forestPath = new Path(modelPath, "forest.seq");
    System.out.println("Storing the forest in: " + forestPath);
    
    DFUtils.storeWritable(conf, forestPath, forest);		
    
    System.out.println("finish building forest model");
    
    // Test data
    
    Classifier classifier = new Classifier(modelPath, test_dataPath, train_datasetPath, predictionPath, conf);
  
    classifier.run();
    
    System.out.println("Finish Prediction");
    
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
	  
	  System.out.println(dataset);
	  
	  System.out.println("storing the dataset description");
	  String json = dataset.toJSON();

	  DFUtils.storeString(conf, fpath, json);


//	  DFUtils.storeWritable(conf, fpath, traindata);	
//	  dataset.load(conf, fpath);
//	  return dataset;
		  }

	private static Dataset generateDataset(String descriptor, String dataPath, boolean regression) throws IOException,
	DescriptorException {
	  Path path = new Path(dataPath);
	  FileSystem fs = path.getFileSystem(new Configuration());

	  return DataLoader.generateDataset(descriptor, regression, fs, path);
}

  
}
