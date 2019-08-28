// this is the original class for transfering csv file to sequence file



package naivebayes.hadoop_nb;

import com.google.common.collect.Lists;

public class CsvToVector {
	private static long wordCount = 1;
	private static final Map<String, Long> words = Maps.newHashMap();
	private final String csvPath;	
//	private final FileSystem fs;

	public CsvToVector(String csvPath)
	{
		this.csvPath = csvPath;
		//this.fs = fs;
	}
	
	
	public List<MahoutVector> vectorize() throws IOException, Exception
	{		
		Configuration conf = new Configuration();
		String uri = "hdfs://localhost:9000";
		FileSystem fs = FileSystem.get(new URI(uri), conf);
		
		Path cp= new Path(csvPath);
		System.out.println("after cp");
		
		InputStreamReader isreader = new InputStreamReader(fs.open(cp), "UTF-8");
		System.out.println("after isreader");
		
		BufferedReader br = new BufferedReader(isreader);
		System.out.println("after br");
		
		CSVReader reader = new CSVReader(br);
		System.out.println("after reader");
				
		List<MahoutVector> vectors = Lists.newArrayList();	// new vector
		System.out.println("after new vector");
		
//		CSVReader reader = new CSVReader(new FileReader(csv_path));
		String line[];
		int feature_in ;		//index of features
		int label_in = 11;
		int i = 0;					//index of vector
		
		try
		{
			System.out.println("start loop");
			//for(line = reader.readNext(); line != null ; line = reader.readNext())
			while ((line = reader.readNext()) != null)
			{
				feature_in = 0 ;
				System.out.println("new line" + i);
				i++;
				
				Vector vector = new RandomAccessSparseVector(line.length-1, line.length-1);	// number of features
				
				for ( feature_in = 0; feature_in<label_in ; feature_in++)						// handling features
				{
//					System.out.println("feature_in is " + feature_in);
//					System.out.println("value is " + line[feature_in]);
					if (isNumeric(line[feature_in])){
//						System.out.println("go to number way");
						vector.set(feature_in, processNumeric(line[feature_in]));
						
					}
					else{
//						System.out.println("go to String way");
						vector.set(feature_in, processString(line[feature_in]));
					}
/*					if ( feature_in == 0 || feature_in == 2 || feature_in == 3 || feature_in == 5 || feature_in == 9 || feature_in == 11 || feature_in ==12 || feature_in == 13  ) {			// String
					vector.set(i, processString(line[feature_in]));
					}
				
					else if ( i == label_in ){
						i=i-1;
						continue;
					}
					else {
						vector.set(i, processNumeric(line[feature_in]));
					}*/
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
				vectors.add(mahoutVector);

			}
			
			System.out.println("close");
			reader.close();
			isreader.close();
			br.close();
			fs.close();
			System.out.println("return value");
		
			return vectors;
		}
		finally {
			System.out.println("return finished");

		}
		
		
	}
	
	protected double processString(String features)
	{
		
		Long a = words.get(features);
		if (a == null)
		{
			a = wordCount++;
			words.put(features, a);
		}
		
		return a;
	}

	protected double processNumeric(String features)
	{
		Double d ;
//		if (isNumeric(features))
//		{
//			d = Double.valueOf(features.toString());
			d = Double.parseDouble(features);
//		}
		
		return d;
	}

	public static boolean isNumeric(String features)
	{
/*        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
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
		*/
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition parsePosition = new ParsePosition(0);
		formatter.parse(features, parsePosition);
		return features.length() == parsePosition.getIndex();

	}
	
	
}
