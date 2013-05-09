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

import be.hogent.tarsos.lsh.Vector;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class SequenceFileReaderUtils {

		@SuppressWarnings("resource")
		public static List<Vector> readFromHDFS(String uri) throws IOException{
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
	            double[] features=transferBytes(b); 
	            Vector vector=new Vector(key.toString(),features);
	            valuesList.add(vector);
	        }  
	        fs.close();
	        return valuesList;
		}
	   public List<double[]> readImage() throws IOException{  
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
	            double[] features=transferBytes(b); 
	            Vector vector=new Vector(key.toString(),features);
	            valuesList.add(vector);
	            values.add(features);
	        }  
	       return values;
	        

	    }  
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
	    	SequenceFileReaderUtils demo=new SequenceFileReaderUtils();  
	    	List<double[]> values=demo.readImage();
	    	demo.PCA(values);
	        
//	    	double[][]data=new double[][]{{1,2,3},{1,5,6},{1, 8,9},{1,10,11}};
//	    	Matrix matrix=new Matrix(data);
//	    	matrix.eig().getD().print(4, 2);
//	    	matrix.eig().getV().print(4, 2);
//	    	matrix.times(matrix.eig().getV()).print(4, 2);
	    }
	    private static double[] transferBytes(byte[] bytes) throws IOException{
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
