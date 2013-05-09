package cn.edu.thu.hxd.homework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;

public class WavFeatureTest {

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
	public static void main(String[] args) throws Exception {
		String line = "/mutli_movie_wav/mutli/中国1_Part_1_.mp4.wav";
		System.out.println("[map debug] "+ line);
		Path file =new Path(line);
		Path local=new Path("/tmp/wavfeature/"+line);
		try {
			 hdfs.copyToLocalFile(file,local );
			 File file2=new File("/tmp/wavfeature/"+line);
			 List<double[]> windowList=new ArrayList<double[]>();
			 List<double[]> resultList=new ArrayList<double[]>();
			 JAudioUtils.getFeature(file2, windowList, resultList);
			 file2.delete();
			 for(double[] dd:resultList){
				 System.out.println(Arrays.toString(dd));
			 }
		} finally {
		}
	}

}
