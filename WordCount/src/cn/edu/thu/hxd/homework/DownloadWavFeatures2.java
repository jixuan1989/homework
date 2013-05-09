package cn.edu.thu.hxd.homework;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import cern.colt.Arrays;
/**
 * 有问题
 * @author hxd
 *
 */
public class DownloadWavFeatures2 extends Configured
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
			byte[] lengthbyte=new byte[Double.SIZE];
			 List<double[]> windowList=new ArrayList<double[]>();
			 List<double[]> resultList=new ArrayList<double[]>();
//			 JAudioUtils.getFeature(file2, windowList, resultList,this.getClass().getResource("/features.xml").toString());//这里写死了配置文件的本地路径。。无奈啊。。
//			 System.out.println(paths.length);
			 //输出特征为bytes
			 int begin=0;
			 for(int i=0;i<windowList.size();i++){
				 System.arraycopy(bytes, begin, lengthbyte, 0, Double.SIZE);
				 windowList.get(i)[0]=ByteBuffer.wrap(lengthbyte).getDouble();//start
				 begin+=Double.SIZE;
				 System.arraycopy(bytes,begin , lengthbyte, 0, Double.SIZE);
				 windowList.get(i)[1]=ByteBuffer.wrap(lengthbyte).getDouble();//end
				 begin+=Double.SIZE;
				 begin+=Integer.SIZE;//length=12
				 for(int j=0;j<12;j++){
					 System.arraycopy(bytes,begin , lengthbyte, 0, Double.SIZE);
					 resultList.get(i)[j]=ByteBuffer.wrap(lengthbyte).getDouble();
					 begin+=Double.SIZE;
				 }
			 }
			output.collect(key,new Text(Arrays.toString(resultList.get(0))));
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
		JobConf jobConf = new JobConf(DownloadWavFeatures2.class);
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
		int exitCode = ToolRunner.run(new DownloadWavFeatures2(), args);
		System.exit(exitCode);
	}
}