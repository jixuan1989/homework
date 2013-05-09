package cn.edu.thu.hxd.homework;

import java.io.IOException;
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
	 */
	public static void main(String[] args) throws IOException {
		//prepare
		double radius=10;
		String hashFamilyType="cos";//cos l1 l2
		int numberOfHashes=4;
		int numberOfHashTables=4;
		int numberOfNeighbours=10;
		List<Vector> dataset=SequenceFileReaderUtils.readFromHDFS("hdfs://pc0:9000/features/frames/part-00000");
		LSH lsh=createIndex(radius, hashFamilyType, numberOfHashes, numberOfHashTables, numberOfNeighbours, dataset);
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
	public static LSH createIndex(double radius,String hashFamilyType,int numberOfHashes,int numberOfHashTables,int numberOfNeighbours,List<Vector> dataset){
		int dimensions=dataset.get(0).getDimensions();
		HashFamily family = getHashFamily(radius,hashFamilyType,dimensions);
		//build
		LSH lsh = new LSH(dataset, family);
		lsh.buildIndex(numberOfHashes,numberOfHashTables);
		return lsh;
	}
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
