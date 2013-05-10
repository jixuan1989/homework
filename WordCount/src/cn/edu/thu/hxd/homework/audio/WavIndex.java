package cn.edu.thu.hxd.homework.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import be.hogent.tarsos.lsh.LSH;
import be.hogent.tarsos.lsh.Vector;
import be.hogent.tarsos.lsh.families.CosineDistance;
import cn.edu.thu.hxd.homework.CreateLSHIndex;
import cn.edu.thu.hxd.homework.utils.JAudioUtils;
import cn.edu.thu.hxd.homework.utils.SequenceFileReaderUtils;

public class WavIndex {

	LSH lsh=null;
	double radius=3;
	String hashFamilyType="cos";//cos l1 l2
	int numberOfHashes=8;
	int numberOfHashTables=8;
	int numberOfNeighbours=5;
	String dataFileString="wavdata";
	String indexFileString="wavIndex";
	String hdfsUrl;
	Map<String,List<LinkedVector> > oridatas;
	public WavIndex(String hdfsurl){
		hdfsUrl=hdfsurl;
	}


	public void createIndex() throws IOException, ClassNotFoundException{
		List<Vector> vectors=null;
		File file1=new File(dataFileString);
		oridatas=new HashMap<String,List<LinkedVector> >();
		if(!file1.exists()){
			vectors=SequenceFileReaderUtils.readWavFeaturesFromHDFS(hdfsUrl);

			for (Vector vector:vectors){
				String key=vector.getKey().split("\\|")[0];
				if(oridatas.containsKey(key)){
					oridatas.get(key).add((LinkedVector) vector);
				}else{
					oridatas.put(key,new ArrayList<LinkedVector>());
					oridatas.get(key).add((LinkedVector) vector);
				}
			}
			Comparator<Vector> comparator=new Comparator<Vector>() {
				@Override
				public int compare(Vector o1, Vector o2) {
					double v1=Double.parseDouble(o1.getKey().split("\\|")[1]);
					double v2=Double.parseDouble(o2.getKey().split("\\|")[1]);
					return (int) (v1-v2);
				}
			};
			vectors.clear();
			for(Map.Entry<String, List<LinkedVector>> entry:oridatas.entrySet()){
				Collections.sort(entry.getValue(),comparator);
				for(int i=0;i<entry.getValue().size();i++){
					entry.getValue().get(i).setId(i);
					entry.getValue().get(i).setTotal(entry.getValue().size());
				}
				vectors.addAll(entry.getValue());
			}
			System.out.println("save hdfs data into local file..");
			CreateLSHIndex.writeObjectIntoFile(file1.getAbsolutePath(), vectors);
			CreateLSHIndex.writeObjectIntoFile("oriDataIndex", oridatas);
		}else {
			vectors=(List<Vector>) CreateLSHIndex.readFromFile(dataFileString);
			oridatas=(Map<String, List<LinkedVector>>) CreateLSHIndex.readFromFile("oriDataIndex");
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
	public  void query(File file) throws Exception{
		long time=System.currentTimeMillis();
		List<Vector> queries=new ArrayList<Vector>();
		List<double[]> windows=new ArrayList<double[]>();
		List<double[]> results=new ArrayList<double[]>();
		JAudioUtils.essyExtractWavFeature(file, windows, results);
		//查询样本不需要这么频繁的滑动，可以舍弃掉一些
		//TODO
		for(int i=0;i<windows.size();i++){
			Vector vector=new Vector("|"+windows.get(i)[0]+"|"+windows.get(i)[1],results.get(i));
			queries.add(vector);
		}
		int split=queries.size()/50;
		CosineDistance distance=new CosineDistance();
		Map<String,Double> scoresMap=new HashMap<String, Double>();
		for(int t=0;t<queries.size();t+=split){
			for(int i=t;i<t+5&&i<queries.size();i++){
				List<Vector> neighbours = lsh.query(queries.get(i), numberOfNeighbours);
				for(Vector vector:neighbours){	
					String[] keys=vector.getKey().split("\\|");
					String keyString=keys[0];
					List<LinkedVector> vectors=oridatas.get(keyString);//手动对比这个结果的后续对不对
					int j=((LinkedVector)vector).getId();
					if(((LinkedVector)vector).getTotal()-j<queries.size()-i){
						continue;//比样本长度还短，认为不可靠
					}
					double dert=0;
					int length=0;
					for(int k=i;j<vectors.size()&&k<queries.size();j++,k++){
						dert+=distance.distance(queries.get(k),vectors.get(j));
						length++;
					}
					dert/=length;//均一化
					dert/=(1-((double)i)/queries.size());//惩罚项，匹配的长度越短，惩罚越厉害
					Double scoreDouble=scoresMap.get(keyString);
					if(scoreDouble==null||scoreDouble>dert){
						scoresMap.put(keyString, dert);
					}
				}
			}
		}
		//		Collections.sort
		List<Entry<String, Double>> sort=new ArrayList<Entry<String,Double>>(scoresMap.entrySet());
		Collections.sort(sort,new Comparator<Entry<String, Double> >() {
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				return Double.compare(o1.getValue(), o2.getValue());
			}
		});
		for(int i=0;i<sort.size();i++){
			System.out.println((sort.get(i).getKey())+":"+sort.get(i).getValue());
		}
		System.out.println("size:"+sort.size()+",cost time:"+(System.currentTimeMillis()-time));
	}
	public static void main(String[] args) throws Exception {
		WavIndex wavIndex=new WavIndex("hdfs://pc0:9000/features/wavs_smooth2/part-00000");
		wavIndex.createIndex();
		System.out.println("begin to query...");
		wavIndex.query(new File("e:\\tmp\\dataset\\huoying3_1.wav"));
	}


}
