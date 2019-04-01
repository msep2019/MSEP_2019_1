import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Friend {

	public static class MyMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
		
		@Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, IntWritable, IntWritable>.Context context)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			String[] uAF = value.toString().split("\t");
			if (uAF.length != 2) {
				return;
			}
			String userID = uAF[0];
			String[] friendsID = uAF[1].split(",");
			
			for (int i = 0; i < friendsID.length; i++) { // userID & friendsID[i] are friends already
				int user = Integer.valueOf(userID);
				int friend = Integer.valueOf(friendsID[i]);
				if (user < friend) { // friends are mutual, so set key < value to easy counting in reduce
					context.write(new IntWritable(user), new IntWritable(-friend - 1)); // minus int ID means already friends, which will never be recommended
				} else {
					context.write(new IntWritable(friend), new IntWritable(-user - 1));
				}
				
			}
			for (int i = 0; i < friendsID.length - 1; i++) { // each pair in friendsID[] have mutual friend userID
				for (int j = i + 1; j < friendsID.length; j++) {
					int friendi = Integer.valueOf(friendsID[i]);
					int friendj = Integer.valueOf(friendsID[j]);
					if (friendi < friendj) {
						context.write(new IntWritable(friendi), new IntWritable(friendj));
					} else {
						context.write(new IntWritable(friendj), new IntWritable(friendi));
					}
				}
			}
		}
	}
	
	public static class MyReducer extends Reducer<IntWritable,IntWritable,IntWritable,Text> {
		
		
		@Override
		protected void reduce(IntWritable arg0, Iterable<IntWritable> arg1,
				Reducer<IntWritable, IntWritable, IntWritable, Text>.Context arg2)
				throws IOException, InterruptedException {
			// TODO Auto-generated method stub
			Map<Integer, Integer> cnt = new TreeMap<Integer, Integer>();
			for (IntWritable ID : arg1) {
				int friend = ID.get();
				
				if (friend < 0) { // ignore friends already
					friend = -friend - 1;
					if (!cnt.containsKey(friend)) {
						cnt.put(friend, -1);
					}
				} else {
					if (!cnt.containsKey(friend)) { // first mutual friend
						cnt.put(friend, 1);
					} else if (cnt.get(friend) != -1) { // add mutual friend number
						int cntCurr = cnt.get(friend);
						cnt.put(friend, cntCurr + 1);
					}
				}
			}
			List<Entry<Integer, Integer>> cntList = new ArrayList<Entry<Integer, Integer>>(cnt.entrySet()); // transform to list for sorting value
			Collections.sort(cntList, new Comparator<Map.Entry<Integer,Integer>>() {

				@Override
				public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) { // descending
					// TODO Auto-generated method stub
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			
			String outputFriends = "";
			for (int i = 0; i < 10; i++) {
				if (cntList.size() > i && cntList.get(i).getValue() != -1) { // if the number is smaller than ten and not already friends
					outputFriends += cntList.get(i).getKey() + ",";
				} else {
					break;
				}
			}
			if (outputFriends.length() > 0) {
				outputFriends = outputFriends.substring(0, outputFriends.length() - 1);
			}

			List<Integer> inputUsersList = Arrays.asList(924, 8941, 8942, 9019, 9020, 9021, 9022, 9990, 9992, 9993);
			if (inputUsersList.contains(arg0.get())) {
				arg2.write(arg0, new Text(outputFriends));
			}
		}
	}
	
	public static void main(String[] args) throws Exception {  
		Job job = Job.getInstance(new Configuration());
	    job.setJarByClass(Friend.class);
	    
	    job.setMapperClass(MyMapper.class);
	    job.setReducerClass(MyReducer.class);
	    
	    // output key & value type
	    job.setMapOutputKeyClass(IntWritable.class);
	    job.setMapOutputValueClass(IntWritable.class);
	    
	    // input & output file path
//	    FileInputFormat.setInputPaths(job, new Path("hdfs://localhost:9000/input_0401_e3/"));
//	    FileOutputFormat.setOutputPath(job, new Path("hdfs://localhost:9000/output_0401_e3/"));
	    FileInputFormat.setInputPaths(job, new Path("/home/jzd/Cloud/hadoop-2.9.2/input/0401_e3/"));
		FileOutputFormat.setOutputPath(job, new Path("/home/jzd/Cloud/hadoop-2.9.2/output/0401_e3/"));
//	    FileInputFormat.setInputPaths(job, new Path("/home/jzd/Cloud/hadoop-2.9.2/input/test/"));
//		FileOutputFormat.setOutputPath(job, new Path("/home/jzd/Cloud/hadoop-2.9.2/output/test/"));
	    
		job.waitForCompletion(true); 
	}
	
}
