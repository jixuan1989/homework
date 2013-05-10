package cn.edu.thu.hxd.homework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.edu.thu.hxd.homework.utils.SequenceFileReaderUtils;

import be.hogent.tarsos.lsh.CommandLineInterface;
import be.hogent.tarsos.lsh.LSH;
import be.hogent.tarsos.lsh.Vector;
import be.hogent.tarsos.lsh.families.CityBlockHashFamily;
import be.hogent.tarsos.lsh.families.CosineHashFamily;
import be.hogent.tarsos.lsh.families.EuclidianHashFamily;
import be.hogent.tarsos.lsh.families.HashFamily;

public class CreateLSHIndex {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		double radius=10;
		String hashFamilyType="cos";//cos l1 l2
		int numberOfHashes=4;
		int numberOfHashTables=4;
		int numberOfNeighbours=10;
		List<Vector> vectors=new ArrayList<Vector>();
		vectors.add(new Vector("1", new double[]{1,1,1,1,1}));
		vectors.add(new Vector("1", new double[]{2,2,2,2,2}));
		vectors.add(new Vector("2", new double[]{1,2,2,2,2}));
		vectors.add(new Vector("2", new double[]{2,1,2,2,2}));
		vectors.add(new Vector("2", new double[]{2,2,1,2,2}));
		LSH lsh=createIndex(radius, hashFamilyType, numberOfHashes, numberOfHashTables, numberOfNeighbours, vectors);
		List<Vector> queries=new ArrayList<Vector>();
		queries.add(new Vector("2", new double[]{2,2,2,1,2}));
		//query
		for(Vector query:queries){
			List<Vector> neighbours = lsh.query(query, numberOfNeighbours);
			System.out.println(query.getKey()+";");
			for(Vector neighbour:neighbours){
				System.out.println(neighbour.getKey() + ";");
			}
		}
	}
	public static void testFromFile() throws FileNotFoundException, IOException, ClassNotFoundException{
		//prepare
				double radius=10;
				String hashFamilyType="cos";//cos l1 l2
				int numberOfHashes=4;
				int numberOfHashTables=4;
				int numberOfNeighbours=10;
//						List<Vector> dataset=SequenceFileReaderUtils.readFromHDFS("hdfs://pc0:9000/features/frames/part-00000");
//				writeObjectIntoFile("data", dataset);
				List<Vector> dataset=(List<Vector>) readFromFile("data");
			
//				LSH lsh=createIndex(radius, hashFamilyType, numberOfHashes, numberOfHashTables, numberOfNeighbours, dataset);
//				writeObjectIntoFile("index", lsh);
				LSH lsh=(LSH) readFromFile("index");
				List<Vector> queries=new ArrayList<Vector>();
				queries.add(dataset.get(10));
				//query
				for(Vector query:queries){
					List<Vector> neighbours = lsh.query(query, numberOfNeighbours);
					System.out.println(query.getKey()+";");
					for(Vector neighbour:neighbours){
						System.out.println(neighbour.getKey() + ";");
					}
				}
	}
	public static void test() throws IOException{
		//prepare
		double radius=10;
		String hashFamilyType="cos";//cos l1 l2
		int numberOfHashes=4;
		int numberOfHashTables=4;
		int numberOfNeighbours=10;
		List<Vector> dataset=SequenceFileReaderUtils.readImageFeaturesFromHDFS("hdfs://pc0:9000/features/frames/part-00000");
		File origionFile=new File("data");
		ObjectOutputStream outputStream1=new ObjectOutputStream(new FileOutputStream(origionFile));
		outputStream1.writeObject(dataset);
		outputStream1.close();
		LSH lsh=createIndex(radius, hashFamilyType, numberOfHashes, numberOfHashTables, numberOfNeighbours, dataset);
		File file=new File("index");
		ObjectOutputStream outputStream=new ObjectOutputStream(new FileOutputStream(file));
		outputStream.writeObject(lsh);
		outputStream.close();
		List<Vector> queries=new ArrayList<Vector>();
		queries.add(dataset.get(10));
		//query
		for(Vector query:queries){
			List<Vector> neighbours = lsh.query(query, numberOfNeighbours);
			System.out.println(query.getKey()+";");
			for(Vector neighbour:neighbours){
				System.out.println(neighbour.getKey() + ";");
			}
		}
	}
	/**
	 * 讲一个Object对象写入到文件中
	 * @param filename
	 * @param object
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void writeObjectIntoFile(String filename,Object object) throws FileNotFoundException, IOException{
		File file=new File(filename);
		ObjectOutputStream outputStream=new ObjectOutputStream(new FileOutputStream(file));
		outputStream.writeObject(object);
		outputStream.close();
	}
	/**
	 * 将一个Object对象从文件中读出
	 * @param finename
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object readFromFile(String finename) throws FileNotFoundException, IOException, ClassNotFoundException{
		File origionFile=new File(finename);
		ObjectInputStream inputStream=new ObjectInputStream(new FileInputStream(origionFile));
		Object object= inputStream.readObject();
		inputStream.close();
		return object;
	}
	/**
	 * 创建LSH索引
	 * @param radius
	 * @param hashFamilyType
	 * @param numberOfHashes
	 * @param numberOfHashTables
	 * @param numberOfNeighbours
	 * @param dataset
	 * @return
	 */
	public static LSH createIndex(double radius,String hashFamilyType,int numberOfHashes,int numberOfHashTables,int numberOfNeighbours,List<Vector> dataset){
		int dimensions=dataset.get(0).getDimensions();
		HashFamily family = getHashFamily(radius,hashFamilyType,dimensions);
		//build
		LSH lsh = new LSH(dataset, family);
		lsh.buildIndex(numberOfHashes,numberOfHashTables);
		return lsh;
	}
	/**
	 * 得到哈希类型
	 * @param radius
	 * @param hashFamilyType
	 * @param dimensions
	 * @return
	 */
	private static HashFamily getHashFamily(double radius,String hashFamilyType,int dimensions){
		HashFamily family = null;
		if(hashFamilyType.equalsIgnoreCase("cos")){
			family = new CosineHashFamily(dimensions);
		}else if(hashFamilyType.equalsIgnoreCase("l1")){
			int w = (int) (10 * radius);
			family = new CityBlockHashFamily(w,dimensions);
		}else if(hashFamilyType.equalsIgnoreCase("l2")){
			int w = (int) (10 * radius);
			family = new EuclidianHashFamily(w,dimensions);
		}else{
			new IllegalArgumentException(hashFamilyType + " is unknown, should be one of cos|l1|l2" );
		}
		return family;
	}

}
