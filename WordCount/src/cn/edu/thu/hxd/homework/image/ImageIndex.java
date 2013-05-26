package cn.edu.thu.hxd.homework.image;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import be.hogent.tarsos.lsh.LSH;
import be.hogent.tarsos.lsh.Vector;
import cn.edu.thu.hxd.homework.CreateLSHIndex;
import cn.edu.thu.hxd.homework.utils.ImageUtils;
import cn.edu.thu.hxd.homework.utils.SequenceFileReaderUtils;

public class ImageIndex {

	LSH lsh=null;
	double radius=10;
	String hashFamilyType="cos";//cos l1 l2
	int numberOfHashes=8;
	int numberOfHashTables=8;
	int numberOfNeighbours=5;
	String dataFileString="imagedata";
	String indexFileString="imageIndex";
	String hdfsUrl;
	public ImageIndex(String hdfsurl){
		hdfsUrl=hdfsurl;
	}

	public void createIndex() throws IOException, ClassNotFoundException{
		List<Vector> vectors=null;
		File file1=new File(dataFileString);
		if(!file1.exists()){
			vectors=SequenceFileReaderUtils.readImageFeaturesFromHDFS(hdfsUrl);
			System.out.println("save hdfs data into local file..");
			CreateLSHIndex.writeObjectIntoFile(file1.getAbsolutePath(), vectors);
		}else {
			vectors=(List<Vector>) CreateLSHIndex.readFromFile(dataFileString);
		}
		file1=new File(indexFileString);

		if(!file1.exists()){
			lsh=CreateLSHIndex.createIndex(radius, hashFamilyType, numberOfHashes, numberOfHashTables, numberOfNeighbours, vectors);
			System.out.println("save index into local file..");
			CreateLSHIndex.writeObjectIntoFile(file1.getAbsolutePath(), lsh);
		}else {
			lsh=(LSH) CreateLSHIndex.readFromFile(indexFileString);
		}


	}
	public  List<String> query(File file) throws Exception{
		List<Vector> queries=new ArrayList<Vector>();
		List<String> results=new ArrayList<String>();
		Vector vector=new Vector("test",ImageUtils.easyGetFeatures(file));
		queries.add(vector);
		//query
		for(Vector query:queries){
			List<Vector> neighbours = lsh.query(query, numberOfNeighbours);
			System.out.println(query.getKey()+";");
			for(Vector neighbour:neighbours){
				results.add(neighbour.getKey());
			}
		}
		return results;
	}
	public static void main(String[] args) throws Exception {
		ImageIndex wavIndex=new ImageIndex("hdfs://pc0:9000/features/frames/part-00000");
		wavIndex.createIndex();
		System.out.println("begin to query...");
		List<String> results=wavIndex.query(new File("e:\\tmp\\hdfsImgs\\mutli\\海贼王3_Part_3_.mp4\\000000018.jpg"));
		System.out.println(results);
	}


}
