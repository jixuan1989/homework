package cn.edu.thu.hxd.homework;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.imageanalysis.CEDD;
import net.semanticmetadata.lire.imageanalysis.ColorLayout;
import net.semanticmetadata.lire.imageanalysis.EdgeHistogram;
import net.semanticmetadata.lire.imageanalysis.FCTH;
import net.semanticmetadata.lire.imageanalysis.sift.Extractor;
import net.semanticmetadata.lire.imageanalysis.sift.Feature;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.mapred.JobConf;



public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
//		// TODO Auto-generated method stub
//		String line ="/mutli_movie_frames/mutli/中国1_Part_1_.mp4/000000003.jpg";
//		System.out.println("[map debug] "+ line);
//		JobConf conf=new JobConf();
//		conf.set("fs.default.name", "hdfs://192.168.137.130:9000");
//		FileSystem hdfs=null;
//		try {
//			hdfs=FileSystem.get(conf);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		Path file =new Path(line);
//		FileStatus fileStatus = hdfs.getFileStatus(file);
//		byte[] contents = new byte[(int) fileStatus.getLen()];
//		FSDataInputStream in = null;
//		try {
//			 in = hdfs.open(file);
//			 //IOUtils.readFully(in, contents, 0, contents.length);
//			 BufferedImage image = ImageIO.read(in);
//			 //FETCH特征
//			 FCTH f1 = new FCTH();
//	         f1.extract(image);
//	         System.out.println(f1.getByteArrayRepresentation().length+":"+Arrays.toString(f1.getByteArrayRepresentation()));//192
////	         FCTH f2= new FCTH();
////	         f2.setByteArrayRepresentation(f1.getByteArrayRepresentation());
////	         System.out.println(Arrays.toString(f2.getDoubleHistogram()));//192
////	         System.out.println(Arrays.toString(f1.getDoubleHistogram()));//192
//	         //ColorLayout
//	         ColorLayout p1 = new ColorLayout();
//	         p1.extract(image);
//	         System.out.println(p1.getByteArrayRepresentation().length+":"+Arrays.toString(p1.getByteArrayRepresentation()));//120
//	         //Edge
//	         EdgeHistogram eh1 = new EdgeHistogram();
//	         eh1.extract(image);
////	         System.out.println(eh1.getStringRepresentation());
//	         System.out.println(eh1.getByteArrayRepresentation().length+":"+Arrays.toString(eh1.getByteArrayRepresentation()));//double[] 80
//	         //CEDD
//	         CEDD cedd = new CEDD();
//	         cedd.extract(image);
////	         System.out.println("cedd = " + cedd.getStringRepresentation());
//	         System.out.println(cedd.getByteArrayRepresentation().length+":" + Arrays.toString(cedd.getByteArrayRepresentation()));//double[] 144  
//	         //SIFT
//	         Extractor ex = new Extractor();
//	 		List<Feature> features = ex.computeSiftFeatures(image);// 提取特征，
//	 		System.out.println("featuresize: " + features.get(2).getByteArrayRepresentation().length);
//		} finally {
//			IOUtils.closeStream(in);
//		}
//		hdfs.close();
//		byte[]test=new byte[]{20, 0, 0, 0, 0, 0, 0, 64};
//		System.out.println(ByteBuffer.wrap(test).getDouble());
//		System.out.println(new Double(5.1));
//		ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream(1);
//		DataOutputStream outputStream=new DataOutputStream(byteArrayOutputStream);
//		outputStream.writeDouble(2.0);
//		outputStream.writeDouble(5.0);
//		System.out.println(Arrays.toString(byteArrayOutputStream.toByteArray()));
//		ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
//		DataInputStream inputStream=new DataInputStream(byteArrayInputStream);
//		System.err.println(inputStream.available());
//		System.err.println(inputStream.readDouble());
//		System.err.println(inputStream.readDouble());
//		System.err.println(inputStream.available());
		
		Map<String,Double> scoresMap=new HashMap<String,Double>();
		scoresMap.put("1", 0.11);
		scoresMap.put("2", 0.01);
		scoresMap.put("3", 1.11);
		scoresMap.put("4", 0.81);
//		Collections.sort
		List<Entry<String, Double>> sort=new ArrayList<Entry<String,Double>>(scoresMap.entrySet());
		Collections.sort(sort,new Comparator<Entry<String, Double> >() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
//				System.out.println((-)+"=="+((int) (o1.getValue()-o2.getValue())));
				return Double.compare(o1.getValue(), o2.getValue());
//				return (int) (o1.getValue()-o2.getValue());
			}
		});
		for(int i=0;i<sort.size();i++){
			System.out.println((sort.get(i).getKey())+":"+sort.get(i).getValue());
		}
	}

}
