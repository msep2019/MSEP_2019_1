import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class EachLetter {

	public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, LongWritable>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			String[] words = value.toString().split(" ");
			for (int i = 0; i < words.length; i++) {
				if (words[i].length() > 0) {
					char letter = words[i].charAt(0);
					
					if (Character.isLetter(letter)) { // ignore non-alphabetic characters
						letter = Character.toLowerCase(letter); // ignore letter case
						context.write(new Text(letter + " "), new LongWritable(1));
					}
				}				
			}
		}
	}
	
	public static class MyReducer extends Reducer<Text,LongWritable,Text,LongWritable> {

		@Override
		protected void reduce(Text arg0, Iterable<LongWritable> arg1,
				Reducer<Text, LongWritable, Text, LongWritable>.Context arg2) throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			long cnt = 0;
			for (LongWritable val : arg1) {
				cnt += val.get();
			}
			arg2.write(arg0, new LongWritable(cnt));
		}
		
		
	}
	
	public static void main(String[] args) throws Exception {  
		Job job = Job.getInstance(new Configuration());
	    job.setJarByClass(EachLetter.class);
	    
	    job.setMapperClass(MyMapper.class);
	    job.setReducerClass(MyReducer.class);
	    
	    // output key & value type
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(LongWritable.class);
	    
	    // input & output file path
//	    FileInputFormat.setInputPaths(job, new Path("hdfs://localhost:9000/input_0401_e2/"));
//	    FileOutputFormat.setOutputPath(job, new Path("hdfs://localhost:9000/output_0401_e2/"));
	    FileInputFormat.setInputPaths(job, new Path("/home/jzd/Cloud/hadoop-2.9.2/input/0401_e2/"));
		FileOutputFormat.setOutputPath(job, new Path("/home/jzd/Cloud/hadoop-2.9.2/output/0401_e2/"));
//	    FileInputFormat.setInputPaths(job, new Path("/home/jzd/Cloud/hadoop-2.9.2/input/test/"));
//		FileOutputFormat.setOutputPath(job, new Path("/home/jzd/Cloud/hadoop-2.9.2/output/test/"));
	    
		job.waitForCompletion(true); 
	}
	
}
