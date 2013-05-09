package cn.edu.thu.hxd.homework;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
/**
 * 有问题
 * @author hxd
 *
 */
public class DownloadFeatures2 extends Configured
implements Tool {

	public static class Map extends MapReduceBase implements
	Mapper<Text, BytesWritable, Text, Text>
	{
		/**
		 * Mapper接口中的map方法：
		 * void map(K1 key, V1 value, OutputCollector<K2,V2> output, Reporter reporter)
		 * 映射一个单个的输入k/v对到一个中间的k/v对
		 * 输出对不需要和输入对是相同的类型，输入对可以映射到0个或多个输出对。
		 * OutputCollector接口：收集Mapper和Reducer输出的<k,v>对。
		 * OutputCollector接口的collect(k, v)方法:增加一个(k,v)对到output
		 */
		public void map(Text key, BytesWritable value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
		{
			String line = key.toString();
			System.out.println("[map debug] "+ line);
			byte[] bytes=value.getBytes();
			byte[] lengthbyte=new byte[4];
			ByteArrayOutputStream outputStream=new ByteArrayOutputStream(100000);

			int color;
			int fcth;
			int edge;
			int cedd;
			int siftnumber;
			int feature;
			byte[] features;
			int begin=0;
				//colorlayout size:
 				 System.arraycopy(bytes, 0, lengthbyte, 0, 4);
 				 color=ByteBuffer.wrap(lengthbyte).getInt();
		         outputStream.write(ByteBuffer.allocate(4).putInt(color).array());
		     
//		         FCTH f1 = new FCTH();
		         System.arraycopy(bytes, 4, lengthbyte, 0, 4);
		         fcth=ByteBuffer.wrap(lengthbyte).getInt();
		         outputStream.write(ByteBuffer.allocate(4).putInt(fcth).array());
		         
//		         EdgeHistogram eh1 = new EdgeHistogram();
		         System.arraycopy(bytes, 8, lengthbyte, 0, 4);
		         edge=ByteBuffer.wrap(lengthbyte).getInt();
		         outputStream.write(ByteBuffer.allocate(4).putInt(edge).array());
		         
//		         CEDD cedd = new CEDD();
		         System.arraycopy(bytes, 12, lengthbyte, 0, 4);
		         cedd=ByteBuffer.wrap(lengthbyte).getInt();
		         outputStream.write(ByteBuffer.allocate(4).putInt(cedd).array());
		         	     
//		         Extractor ex = new Extractor();
//			 	 List<Feature> features = ex.computeSiftFeatures(image);// 提取特征，
		         System.arraycopy(bytes, 16, lengthbyte, 0, 4);
		         siftnumber=ByteBuffer.wrap(lengthbyte).getInt();
		         outputStream.write(ByteBuffer.allocate(4).putInt(siftnumber).array());
		        
		         System.arraycopy(bytes, 20, lengthbyte, 0, 4);
		         feature=ByteBuffer.wrap(lengthbyte).getInt();
		         outputStream.write(ByteBuffer.allocate(4).putInt(feature).array());  
		         
		        
		         //从24开始是特征
		         begin=24;
		         features=new byte[color];
		         System.arraycopy(bytes, begin, features, 0, color);
		         outputStream.write(features);
		         begin+=color;
		         
		         features=new byte[fcth];
		         System.arraycopy(bytes, begin, features, 0, fcth);
		         outputStream.write(features);
		         begin+=fcth;
		         
		         features=new byte[edge];
		         System.arraycopy(bytes, begin, features, 0, edge);
		         outputStream.write(features);
		         begin+=edge;
		         
		         features=new byte[cedd];
		         System.arraycopy(bytes, begin, features, 0, cedd);
		         outputStream.write(features);
		         begin+=cedd;

		         for(int i=0;i<siftnumber;i++){
		        	 features=new byte[feature];
		        	 System.arraycopy(bytes, begin+feature*i, features, 0, feature);
			         outputStream.write(features);
		         }     
			output.collect(key,new Text(siftnumber+""));
		}
	}

	public static class Reduce extends MapReduceBase implements
	Reducer<Text, Text, Text, Text>
	{
		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
		{
			System.out.println("[reduce debug] "+ key);
			output.collect(key, values.next());
		}
	}

	@Override
	public int run(String[] args) throws IOException {
		JobConf jobConf = new JobConf(DownloadFeatures2.class);
		jobConf.setJobName("test2");
		jobConf.set("mapred.tasktracker.map.tasks.maximum", "5");
		FileInputFormat.addInputPaths(jobConf, args[0]);
		//		FileInputFormat.setInputPaths(jobConf, new Path(args[0]));
		FileOutputFormat.setOutputPath(jobConf, new Path(args[1]));

		jobConf.setInputFormat(SequenceFileInputFormat.class);    //为map-reduce任务设置InputFormat实现类
//		jobConf.setOutputFormat(SequenceFileOutputFormat.class);
	     jobConf.setOutputFormat(TextOutputFormat.class);
		
		//    SequenceFileOutputFormat.setOutputCompressionType(jobConf, CompressionType.BLOCK);
		//    SequenceFileOutputFormat.setCompressOutput(jobConf, true); 
		//    SequenceFileOutputFormat.setOutputCompressorClass(jobConf, GzipCodec.class); 

		//    jobConf.setOutputKeyClass(Text.class);
		//    jobConf.setOutputValueClass(BytesWritable.class);
//		jobConf.setNumReduceTasks(0);
		jobConf.setMapOutputKeyClass(Text.class);
		jobConf.setMapOutputValueClass(Text.class);
		jobConf.setOutputKeyClass(Text.class);
		jobConf.setOutputValueClass(Text.class);

		jobConf.setMapperClass(Map.class);
//		jobConf.setReducerClass(IdentityReducer.class);
		jobConf.setReducerClass(Reduce.class);

		JobClient.runJob(jobConf);
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new DownloadFeatures2(), args);
		System.exit(exitCode);
	}
}