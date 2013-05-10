package cn.edu.thu.hxd.homework.image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.imageanalysis.CEDD;
import net.semanticmetadata.lire.imageanalysis.ColorLayout;
import net.semanticmetadata.lire.imageanalysis.EdgeHistogram;
import net.semanticmetadata.lire.imageanalysis.FCTH;
import net.semanticmetadata.lire.imageanalysis.sift.Extractor;
import net.semanticmetadata.lire.imageanalysis.sift.Feature;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.mapred.lib.NullOutputFormat;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class FramesFeature2 extends Configured
implements Tool {

	public static class Map extends MapReduceBase implements
	Mapper<LongWritable, Text, Text, BytesWritable>
	{
		static Configuration conf=new Configuration();
		
		static FileSystem hdfs;
		static{
			conf.set("fs.default.name", "hdfs://192.168.137.130:9000");
			try {
				hdfs=FileSystem.get(conf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void close() throws IOException {
			// TODO Auto-generated method stub
			super.close();
//			hdfs.close();
		}



		/**
		 * Mapper接口中的map方法：
		 * void map(K1 key, V1 value, OutputCollector<K2,V2> output, Reporter reporter)
		 * 映射一个单个的输入k/v对到一个中间的k/v对
		 * 输出对不需要和输入对是相同的类型，输入对可以映射到0个或多个输出对。
		 * OutputCollector接口：收集Mapper和Reducer输出的<k,v>对。
		 * OutputCollector接口的collect(k, v)方法:增加一个(k,v)对到output
		 */
		public void map(LongWritable key, Text value, OutputCollector<Text, BytesWritable> output, Reporter reporter) throws IOException
		{
			String line = value.toString();
			System.out.println("[map debug] "+ line);

			Path file =new Path(line);
			ByteArrayOutputStream outputStream=new ByteArrayOutputStream(100000);
//			System.out.println(ByteBuffer.wrap(bytes).getInt());
			FSDataInputStream in = null;
			try {
				in = hdfs.open(file);
				BufferedImage image = ImageIO.read(in);
				 ColorLayout p1 = new ColorLayout();
		         p1.extract(image);
		         outputStream.write(ByteBuffer.allocate(4).putInt(p1.getByteArrayRepresentation().length).array());
		     
		         FCTH f1 = new FCTH();
		         f1.extract(image);
		         outputStream.write(ByteBuffer.allocate(4).putInt(f1.getByteArrayRepresentation().length).array());
		         
		         EdgeHistogram eh1 = new EdgeHistogram();
		         eh1.extract(image);
		         outputStream.write(ByteBuffer.allocate(4).putInt(eh1.getByteArrayRepresentation().length).array());
			      
		         CEDD cedd = new CEDD();
		         cedd.extract(image);
		         outputStream.write(ByteBuffer.allocate(4).putInt(cedd.getByteArrayRepresentation().length).array());
			     
		         Extractor ex = new Extractor();
			 	 List<Feature> features = ex.computeSiftFeatures(image);// 提取特征，
			 	 outputStream.write(ByteBuffer.allocate(4).putInt(features.size()).array());
			 	 outputStream.write(ByteBuffer.allocate(4).putInt(528).array());
			 	 
			 	 outputStream.write(p1.getByteArrayRepresentation());
			 	 outputStream.write(f1.getByteArrayRepresentation());
			 	 outputStream.write(eh1.getByteArrayRepresentation());
			 	 outputStream.write(cedd.getByteArrayRepresentation());
			 	 for(Feature f:features){
			 		 outputStream.write(f.getByteArrayRepresentation());
			 	 }
			 	 
			} finally {
				IOUtils.closeStream(in);
			}
			output.collect(value, new BytesWritable(outputStream.toByteArray()));

		}
	}

//	public static class Reduce extends MapReduceBase implements
//	Reducer<Text, Text, Text, Text>
//	{
//		public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException
//		{
//			System.out.println("[reduce debug] "+ key);
//			output.collect(key, key);
//		}
//	}

	@Override
	public int run(String[] args) throws IOException {
		JobConf jobConf = new JobConf(FramesFeature2.class);
		jobConf.setJobName("test2");
		jobConf.set("mapred.tasktracker.map.tasks.maximum", "5");
		FileInputFormat.addInputPaths(jobConf, args[0]);
		//		FileInputFormat.setInputPaths(jobConf, new Path(args[0]));
		FileOutputFormat.setOutputPath(jobConf, new Path(args[1]));

		jobConf.setInputFormat(TextInputFormat.class);    //为map-reduce任务设置InputFormat实现类
		jobConf.setOutputFormat(SequenceFileOutputFormat.class);
//	     jobConf.setOutputFormat(TextOutputFormat.class);
		
		//    SequenceFileOutputFormat.setOutputCompressionType(jobConf, CompressionType.BLOCK);
		//    SequenceFileOutputFormat.setCompressOutput(jobConf, true); 
		//    SequenceFileOutputFormat.setOutputCompressorClass(jobConf, GzipCodec.class); 

		//    jobConf.setOutputKeyClass(Text.class);
		//    jobConf.setOutputValueClass(BytesWritable.class);
		jobConf.setOutputKeyClass(Text.class);
		jobConf.setOutputValueClass(BytesWritable.class);

		jobConf.setMapperClass(Map.class);
		//jobConf.setReducerClass(IdentityReducer.class);
//		jobConf.setReducerClass(Reduce.class);
		jobConf.setBoolean("mapred.output.compress", true);
		jobConf.setClass("mapred.output.compression.codec", GzipCodec.class, CompressionCodec.class);
		jobConf.set("mapred.output.compress.type", "BLOCK");
//		jobConf.setBoolean("mapred.compress.map.output", true);
//		jobConf.setClass("mapred.map.output.compression.codec", GzipCodec.class, CompressionCodec.class);
		JobClient.runJob(jobConf);
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new FramesFeature2(), args);
		System.exit(exitCode);
	}
}