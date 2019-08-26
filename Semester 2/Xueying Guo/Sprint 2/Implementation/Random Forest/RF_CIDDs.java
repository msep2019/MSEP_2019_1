package randomforest.rf;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.mahout.classifier.df.DecisionForest;
import org.apache.mahout.classifier.df.builder.DefaultTreeBuilder;
import org.apache.mahout.classifier.df.data.Data;
import org.apache.mahout.classifier.df.data.DataLoader;
import org.apache.mahout.classifier.df.data.Instance;
import org.apache.mahout.classifier.df.ref.SequentialBuilder;
import org.apache.mahout.common.RandomUtils;
import org.uncommons.maths.Maths;

public class RF_CIDDs {
	public static void main(String[] args) throws Exception {
		
		// Environment Setting
		Configuration conf = new Configuration();
		
//		String uri = "hdfs://localhost:9000";	
//		FileSystem fs = FileSystem.get(new URI(uri), conf);
		FileSystem fs = FileSystem.getLocal(conf);
		String csvTrainfile = "/home/hadoop/Desktop/hadoop/rf/training/tr1.csv";
		String csvTestfile = "/home/hadoop/Desktop/hadoop/rf/testing/te1.csv";
		
//		String csvTrainfile = "/user/hadoop/rf/CIDD/train/CIDDTrain.csv";
//		String csvTestfile = "/user/hadoop/rf/CIDD/test/CIDDTest.csv";
		
		Path csvTrainPath = new Path(csvTrainfile);
		Path csvTestPath = new Path(csvTestfile);
		
		String descriptor = "N N N N N N N N N N N L";
		
		//variables setting
		int numberOfTrees = 100;
		int labelin = 11;
		int tp = 0;
		int fp = 0;
		int fn = 0;
		int tn = 0;
		int total_test = 0;
		
		Data traindata = DataLoader.loadData(DataLoader.generateDataset(descriptor, false, fs, csvTrainPath), fs, csvTrainPath);
		
		Date trainstart = new Date();
		System.out.println("Start Training " + trainstart);
		
		DecisionForest forest = buildForest(numberOfTrees, traindata);
		
		Date trainend = new Date();
		System.out.println("End Training " + trainend);
		
		Data testdata = DataLoader.loadData(traindata.getDataset(), fs, csvTestPath);
		Random rng = RandomUtils.getRandom();
		
		Date teststart = new Date();
		System.out.println("Start Predict " + teststart);
		
		  for (int i = 0; i < testdata.size(); i++) {
			    Instance oneSample = testdata.get(i);
			    System.out.println("new");
			    double actualIndex = oneSample.get(labelin);	// index of label
			 //   int actuallabel = testdata.getDataset().valueOf(11, String.valueOf((int) actualIndex));
			    double predictlable = forest.classify(traindata.getDataset(), rng, oneSample);
			    
			 //   int predictlable = traindata.getDataset().valueOf(11, String.valueOf((int) classify));
	    
			    System.out.println("Actrual Label: " + actualIndex);
			    System.out.println("Predict Label: " + predictlable);
			    
			    if (actualIndex == 0.0 && predictlable == 0.0) {
			    	tn ++;
			    	total_test++;
			    }
			    else if (actualIndex == 0.0 && predictlable != 0.0) {
			    	fp ++;
			    	total_test++;
			    }
			    else if (actualIndex != 0.0 && predictlable == 0.0 ) {
			    	fn ++;
			    	total_test ++;
			    }
			    else if (actualIndex != 0.0 && predictlable != 0.0 ) {
			    	tp ++;
			    	total_test++;
			    }
			    
			    else { total_test ++; }
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
						
			System.out.println("Training Time = " + (trainend.getTime() - trainstart.getTime() ) + " milliseconds" );
			System.out.println("Predict Time = " + (testend.getTime() - teststart.getTime() ) + " milliseconds" );
			System.out.println("total test data = " + total_test);
			System.out.println("tp:" +tp + "  fp:" +fp +"  fn:" +fn + "  tn:" +tn);
			System.out.println("accuracy = " + accuracy);
			System.out.println("Detection Rate = " + DR);
			System.out.println("False positive rate = " + FPR);
			System.out.println("Precision = " + Precision);
			System.out.println("Recall = " + Recall);
			System.out.println("F1 score = " + F1);
				
			
			System.out.println("**********************************************" );
			System.out.println("Job Finished!" );
		
	}

	private static DecisionForest buildForest(int numberofTrees, Data data) {
		int m = (int) Math.floor(Maths.log(2, data.getDataset().nbAttributes()) + 1);
		
		DefaultTreeBuilder treeBuilder = new DefaultTreeBuilder();
		SequentialBuilder forestBuilder = new SequentialBuilder(RandomUtils.getRandom(),treeBuilder, data.clone());
		treeBuilder.setM(m);
		
		return forestBuilder.build(numberofTrees);
	}
	

}
