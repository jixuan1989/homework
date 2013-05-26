package cn.edu.thu.hxd.homework.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.GzipCodec;

import cn.edu.thu.hxd.homework.audio.LinkedVector;

import be.hogent.tarsos.lsh.Vector;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class SequenceFileReaderUtils {

		@SuppressWarnings("resource")
		public static List<Vector> readImageFeaturesFromHDFS(String uri) throws IOException{
			//String uri="hdfs://pc0:9000/features/frames/part-00000";  
	        Configuration con=new Configuration();  
	        FileSystem fs=FileSystem.get(URI.create(uri), con);  
	        Path path=new Path(uri);  
	        SequenceFile.Reader reader=null;  
	        reader=new SequenceFile.Reader(fs, path, con);  
	        Text key=new Text();  
	        BytesWritable value=new BytesWritable();  
	        List<Vector> valuesList=new ArrayList<Vector>();
	        while(reader.next(key, value)){  
	            System.out.println("key:"+key);  
	            byte[] b=value.getBytes();  
	            System.out.println("b.length:"+b.length);  
	            double[] features=transferFrameFeature2Bytes(b); 
	            Vector vector=new Vector(key.toString(),features);
	            valuesList.add(vector);
	        }  
	        fs.close();
	        return valuesList;
		}
	   public List<double[]> readImageFeaturesFromHDFS() throws IOException{  
//	      String uri="hdfs://192.168.50.28:8020/user/root/jyl/testByteImageSequenceFile";  
	          
	        String uri="hdfs://pc0:9000/features/frames/part-00000";  
	        Configuration con=new Configuration();  
	        FileSystem fs=FileSystem.get(URI.create(uri), con);  
	        Path path=new Path(uri);  
	        SequenceFile.Reader reader=null;  
	        reader=new SequenceFile.Reader(fs, path, con);  
	        Text key=new Text();  
	        BytesWritable value=new BytesWritable();  
//	        long position=reader.getPosition(); 
	        List<Vector> valuesList=new ArrayList<Vector>();
	        List<double[]> values=new ArrayList<double[]>();
	        List<String> keyList=new ArrayList<String>();
	        while(reader.next(key, value)){  
	            System.out.println("key:"+key);  
	            keyList.add(key.toString());
	            byte[] b=value.getBytes();  
	            System.out.println("b.length:"+b.length);  
	            double[] features=transferFrameFeature2Bytes(b); 
	            Vector vector=new Vector(key.toString(),features);
	            valuesList.add(vector);
	            values.add(features);
	        }  
	       return values;
	    } 
	   
		@SuppressWarnings("resource")
		public static List<Vector> readWavFeaturesFromHDFS(String uri) throws IOException{
			//String uri="hdfs://pc0:9000/features/frames/part-00000";  
	        Configuration con=new Configuration();  
	        FileSystem fs=FileSystem.get(URI.create(uri), con);  
	        Path path=new Path(uri);  
	        SequenceFile.Reader reader=null;  
	        reader=new SequenceFile.Reader(fs, path, con);  
	        Text key=new Text();  
	        BytesWritable value=new BytesWritable();  
	        List<Vector> valuesList=new ArrayList<Vector>();
	        while(reader.next(key, value)){  
//	            System.out.println("key:"+key);  
	            byte[] b=value.getBytes();  
//	            System.out.println("b.length:"+b.length); 
	            double[][][] features=transferWavFeature2Bytes(b);
	            for(int i=0;i<features.length;i++){
	            	LinkedVector vector=new LinkedVector(key.toString()+"|"+features[i][0][0]+"|"+features[i][0][1],features[i][1]);
		            valuesList.add(vector);
	            }
	        }  
	        fs.close();
	        return valuesList;
		}
	   
	   /**
	    * 保存到文件
	    * @param keys 特征值
	    * @param values 特征向量
	    * @throws IOException
	    */
	   private static void saveEigenvalue(double[] keys,double[][] values) throws IOException{
		   File file=new File("eigenvalue.bin");
		   DataOutputStream outputStream=new DataOutputStream(new FileOutputStream(file));
		   outputStream.writeInt(keys.length);
		   for(int i=0;i<keys.length;i++){
			   outputStream.writeDouble(keys[i]);
		   }
		   outputStream.writeInt(values.length);
		   outputStream.writeInt(values[0].length);
		   for(int i=0;i<values.length;i++){
			   for(int j=0;j<values[i].length;j++){
				   outputStream.writeDouble(values[i][j]);
			   }
		   }
		   outputStream.close();
	   }
	   /**
	    * 将结果写入hdfs中，有问题。
	    * @param keys
	    * @param values
	    * @throws IOException
	    */
	   public static void writeIntoHDFS(List<String> keys,double[][] values) throws IOException{
		   String uri="hdfs://pc0:9000/features/framesPCA";  
	        Configuration con=new Configuration();  
	        FileSystem fs=FileSystem.get(URI.create(uri), con);  
	        Path path=new Path(uri);  
	        FSDataOutputStream out=fs.create(path);
	        SequenceFile.Writer writer=SequenceFile.createWriter(con, out, Text.class, BytesWritable.class, CompressionType.BLOCK, new GzipCodec());
	        for(int i=0;i<keys.size();i++){
	        	ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
		        DataOutputStream dataout=new DataOutputStream(outputStream);
		        dataout.writeInt(values[i].length);
		        for(int j=0;j<values[i].length;j++){
		        	dataout.writeDouble(values[i][j]);
		        }
	        	writer.append(new Text(keys.get(i)), new BytesWritable(outputStream.toByteArray()));
	        	dataout.close();
	        	outputStream.close();
	        }
	        fs.close();
	   }
	    public static void main(String[] args) throws IOException {  
//	    	SequenceFileReaderUtils demo=new SequenceFileReaderUtils();  
//	    	List<double[]> values=demo.readImageFeaturesFromHDFS();
//	    	demo.PCA(values);
	        
//	    	double[][]data=new double[][]{{1,2,3},{1,5,6},{1, 8,9},{1,10,11}};
//	    	Matrix matrix=new Matrix(data);
//	    	matrix.eig().getD().print(4, 2);
//	    	matrix.eig().getV().print(4, 2);
//	    	matrix.times(matrix.eig().getV()).print(4, 2);
	    	
//	    	List<Vector> vectors=readImageFeaturesFromHDFS("hdfs://pc0:9000/features/frames/part-00000");
	    	//String uri="hdfs://pc0:9000/features/frames/part-00000";  
//	        Configuration con=new Configuration();  
//	        FileSystem fs=FileSystem.get(URI.create("hdfs://pc0:9000/"), con);  
//	        Path path=new Path("hdfs://pc0:9000/mutli_movie_wav");  
//	       Path dest=new Path("e:\\tmp\\hdfsWavs");
//	        fs.copyToLocalFile(path, dest);
	    	rmCRCFile(new File("e:\\tmp\\hdfsWavs"));
	    }
	    public static void rmCRCFile(File folder){
	    	if(folder.isDirectory()){
	    		for(File file:folder.listFiles()){
	    			if(file.isDirectory()){
	    				rmCRCFile(file);
	    			}else{
	    				if(file.getName().endsWith(".crc")){
	    					file.delete();
	    				}
	    			}
	    		}
	    	}
	    }
	    /**
	     * 将一个bytes数组解析为音频特征数组
	     * @param bytes
	     * @return
	     * @throws IOException
	     */
	    public static double[][][] transferWavFeature2Bytes(byte[] bytes) throws IOException{
	    	List<double[][]> list=new ArrayList<double[][]>();
	    	 DataInputStream inputStream=new DataInputStream(new ByteArrayInputStream(bytes));
	    	 int total=inputStream.readInt();
//	    	 System.out.println("total window size:"+total);
	    	 for(int i=0;i<total;i++){
	    		 double[][] window=new double[2][];
	    		 window[0]=new double[2];
	    		 window[0][0]=inputStream.readDouble();
	    		 window[0][1]=inputStream.readDouble();
//	    		 System.out.println(Arrays.toString(window[0]));
	    		 int size=inputStream.readInt();
	    		 double[] data=new double[size];
	    		 for(int j=0;j<size;j++){
	    			 data[j]=inputStream.readDouble();
				 }
	    		 window[1]=data;
	    		 list.add(window);
	    	 }
	    	 inputStream.close();
	    	 return list.toArray(new double[1][1][]);
	    }
	    /**
	     * 将一个bytes数组解析为图片特征数组
	     * @param bytes
	     * @return
	     * @throws IOException
	     */
	    public static double[] transferFrameFeature2Bytes(byte[] bytes) throws IOException{
			int color;
			int fcth;
			int edge;
			int cedd;
			int siftnumber;
			int feature;
	         DataInputStream inputStream=new DataInputStream(new ByteArrayInputStream(bytes));
			 color=inputStream.readInt();
			 fcth=inputStream.readInt();
			 edge=inputStream.readInt();
			 cedd=inputStream.readInt();
			 siftnumber=inputStream.readInt();
			 feature=inputStream.readInt();
			 double[] results=new double[(color/4+fcth/8+edge/4+cedd)];
			 int loc=0;
	         for(int j=8;j<color;j+=4,loc++){//4个一位的整数,color的前8位不要。
	        	 results[loc]=inputStream.readInt();
	         }
	         for(int j=0;j<fcth;j+=8,loc++){
	        	 results[loc]=inputStream.readDouble();
	         }
	         for(int j=0;j<edge;j+=4,loc++){
	        	 results[loc]=inputStream.readInt();
	         }
	         for(int j=0;j<cedd;j++,loc++){
	        	 results[loc]=inputStream.readByte();
	         } 
		     return results;
	    }
	    
	    /**
	     * 对一个double矩阵进行pca降维
	     * @param values
	     * @throws IOException
	     */
	    public static void PCA(List<double[]> values) throws IOException{
	        //PCA降维
	        double[][] result=new double[values.size()][values.get(0).length];
	        values.toArray(result);
	        System.out.println(result.length);
	        Matrix origionMatrix=Matrix.constructWithCopy(result);
	        decentralization(result);
			Matrix A = new Matrix(result);
			Matrix covarianceMatrix=A.transpose().times(A).times(1.0/(A.getRowDimension()-1));
			
			EigenvalueDecomposition  eigenvalueDecomposition=covarianceMatrix.eig();
			double[] eigenvalue=eigenvalueDecomposition.getRealEigenvalues();
			System.out.println(Arrays.toString(eigenvalue));
			Matrix V=eigenvalueDecomposition.getV();
			System.out.println("save in local File...");
			saveEigenvalue(eigenvalue, V.getArray());
			System.out.println("origion:"+origionMatrix.getRowDimension()+","+origionMatrix.getColumnDimension());
			System.out.println("eigenvalueDecomposition.getV:"+V.getRowDimension()+","+V.getColumnDimension());
			double sum=0;
			for(int i=0;i<eigenvalue.length;i++){
				sum+=eigenvalue[i];
			}
			double tmpsum=0,threod=sum*0.9;
			int dimension=0;
			for(int i=eigenvalue.length-1;i>=0;i--){
				tmpsum+=eigenvalue[i];
				if(tmpsum>=threod){
					dimension=eigenvalue.length-i;
					break;
				}
			}
			System.err.println("pca dimension:"+dimension);
			if(dimension>300){
				dimension=300;
				tmpsum=0;
				for(int i=0;i<dimension;i++){
					tmpsum+=eigenvalue[eigenvalue.length-1-i];
				}
				System.err.println("sum:"+tmpsum/sum);
			}
			
			V=V.getMatrix(0,V.getRowDimension()-1, V.getColumnDimension()-dimension,V.getColumnDimension()-1);		
			Matrix resultMatrix=origionMatrix.times(V);
//			System.out.println("save in HDFS...");
//			writeIntoHDFS(keyList, resultMatrix.getArray());
	    }
	    /**
	     * 矩阵按列中心化
	     * @param data
	     */
	    private static void decentralization(double[][] data){
			int m=data.length;
			int n=data[0].length;
			double sum;
			for(int i=0;i<n;i++){
				sum=0;
				for(int j=0;j<m;j++){
					sum+=data[j][i];
				}
				sum/=m;
				for(int j=0;j<m;j++){
					data[j][i]-=sum;
				}
			}
		}
}
