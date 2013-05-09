package cn.edu.thu.hxd.homework;

import jAudioFeatureExtractor.DataModel;
import jAudioFeatureExtractor.ACE.DataTypes.Batch;
import jAudioFeatureExtractor.Aggregators.Aggregator;
import jAudioFeatureExtractor.AudioFeatures.MFCC;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class JAudioUtils {

	public static void getFeature(File file,List<double[]>windows,List<double[]>results) throws Exception{
		getFeature( file,windows,results,"feature.xml");
	}
	public static void getFeature(File file,List<double[]>windows,List<double[]>results,String featureConfigxml) throws Exception{
		OutputStream destinationFK = null;
		OutputStream destinationFV = null;
		destinationFK = (new ByteArrayOutputStream());
		destinationFV = (new ByteArrayOutputStream());;
		System.out.println("[debug] feature:"+featureConfigxml);
		String string="<?xml version=\"1.0\"?>\n" +
				"<featureList>\n" +
				" <pluginFolder>file:///private/Network/Servers/borges.mt.lan/Volumes/home/mcennis/Documents/workspace/ScratchSpace/</pluginFolder>\n"+
				"<feature><class>jAudioFeatureExtractor.AudioFeatures.MagnitudeSpectrum</class></feature>"+	
				"<feature>\n" +
				"<class>jAudioFeatureExtractor.AudioFeatures.MFCC</class>\n" +
				"<on/>\n" +
				"</feature>\n" +
				"<aggregator>jAudioFeatureExtractor.Aggregators.MFCC</aggregator>\n" +
				"<aggregator>jAudioFeatureExtractor.Aggregators.Mean</aggregator>\n" +
				"<aggregator>jAudioFeatureExtractor.Aggregators.StandardDeviation</aggregator>\n" +
				"<aggregator>jAudioFeatureExtractor.Aggregators.AreaMoments</aggregator>\n" +
				"</featureList>\n";
		//		ByteArrayOutputStream out=new ByteArrayOutputStream(200);
		//		BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(out));
		//		writer.write("<?xml version=\"1.0\"?>\n<featureList>\n<feature>\n<class>jAudioFeatureExtractor.AudioFeatures.MFCC</class>\n</feature><aggregator>jAudioFeatureExtractor.Aggregators.MFCC</aggregator>\n</featureList>\n");
		//		Reader reader=new InputStreamReader (new ByteArrayInputStream(out.toByteArray()),"utf-8");
		DataModel dm = new DataModel(new ByteArrayInputStream(string.getBytes("utf-8")),null);
		dm.featureKey = destinationFK;
		dm.featureValue = destinationFV;
		HashMap<String,Boolean> active = new HashMap<String,Boolean>();
		HashMap<String,String[]> attributes = new HashMap<String,String[]>();
		LinkedList<String> tmpAttributes = new LinkedList<String>();
		// set feature attribute list
		for (int i = 0; i < dm.features.length; ++i) {
			if(dm.features[i] instanceof MFCC){
				dm.defaults[i]=true;
				String name = dm.features[i].getFeatureDefinition().name;
				active.put(name,true);
				int count = dm.features[i].getFeatureDefinition().attributes.length;
				for (int j = 0; j < count; ++j) {
					try {
						tmpAttributes.add(dm.features[i].getElement(j));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				attributes.put(name,tmpAttributes.toArray(new String[] {}));
				tmpAttributes.clear();
			}else {
				dm.defaults[i]=false;
			}
		}
		if((dm.aggregators==null)||(dm.aggregators.length==0)){
			dm.aggregators = new Aggregator[2];
			dm.aggregators[0]=new jAudioFeatureExtractor.Aggregators.Mean();
			dm.aggregators[1]=new jAudioFeatureExtractor.Aggregators.StandardDeviation();
			//		dm.aggregators[2]=new jAudioFeatureExtractor.Aggregators.AreaMoments();
			//		dm.aggregators[2].setParameters(new String[]{"Area Method of Moments of MFCCs"},new String[]{""});
		}
		Aggregator[] aggs = dm.aggregators;
		String[] names = new String[aggs.length];
		String[][] features = new String[aggs.length][];
		String[][] parameters = new String[aggs.length][];
		for(int i=0;i<aggs.length;++i){
			names[i] = aggs[i].getAggregatorDefinition().name;
			features[i] = aggs[i].getFeaturesToApply();
			parameters[i] = aggs[i].getParamaters();
		}


		Batch batch=new Batch();
		batch.setDataModel(dm);
		int windowLength = 512;//100-200 ms
		batch.setWindowSize(windowLength);
		double offset = 0.0;//0.2-0.5
		batch.setWindowOverlap(offset);
		batch.setSamplingRate(16000);
		batch.setNormalise(false);
		batch.setPerWindow(true);
		batch.setOverall(true);
		batch.setOutputType(0);
		batch.setRecordings(new File[]{file});
		batch.setFeatures(active, attributes);
		batch.setAggregators(names, features, parameters);
		batch.execute();

		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc;
			InputStream in=new ByteArrayInputStream(((ByteArrayOutputStream)destinationFV).toByteArray());
			doc = builder.build(new InputStreamReader(in,"utf-8"));
			//doc=builder.build(new File(file));
			Element root = doc.getRootElement();
			List<Element> list=root.getChildren("data_set");
			List<double[]> tmpResultList=new ArrayList<double[]>();
			String filename=null;
			for(Element element:list){//循环每一个文件，事实上只有一个文件
				filename= element.getChildText("data_set_id");
				List<Element> xmlfeatures=element.getChildren("section");//
				for(Element e:xmlfeatures){		//循环每一段
					windows.add(new double[]{Double.parseDouble(e.getAttributeValue("start")),Double.parseDouble(e.getAttributeValue("stop"))});
					//					System.out.println(e.getAttributeValue("start")+","+e.getAttributeValue("stop")+":");
					List<Element> values=e.getChild("feature").getChildren("v");
					double[] tmp=new double[values.size()-1];//不要第一个值
					for(int i=0;i<values.size()-1;i++){//该段的特征数组
						tmp[i]=Double.parseDouble(values.get(i+1).getText());
						//						System.out.print(Double.parseDouble(values.get(i).getText())+",");
					}
					tmpResultList.add(tmp);
					//					System.out.println();
				}
				//				//平滑MFCC'(t) = 2*MFCC(t+2)+MFCC(t+1)-MFCC(t-1)-2*MFCC(t-2)
				//				for(double[] dd:tmpResultList){
				//					System.out.println(Arrays.toString(dd));
				//				}
				//				System.out.println("----------");

				for(int i=2;i<tmpResultList.size()-2;i++){
					if(i==tmpResultList.size()-3){
						System.out.println("be cal");
					}
					double[] tmp=new double[tmpResultList.get(i).length];
					for (int j = 0; j < tmpResultList.get(i).length; j++) {
						tmp[j]=((int)((2*tmpResultList.get(i-2)[j]+tmpResultList.get(i-1)[j]-2*tmpResultList.get(i+2)[j]-tmpResultList.get(i+1)[j])*10000))/10000.0;
						if(Double.isInfinite(tmp[j])){
							System.out.println("be cal");
						}
					}
					results.add(tmp);
				}
				//由于平滑后前2段 后两段都去掉了，因此windows中也要去掉
				windows.remove(0);
				windows.remove(0);
				windows.remove(windows.size()-1);
				windows.remove(windows.size()-1);
				//				for(double[] dd:finalResultList){
				//					System.out.println(Arrays.toString(dd));
				//				}
				//				for(int i=0;i<results.size();i++){
				//					System.out.println(Arrays.toString(results.get(i)));
				//					System.out.println(Arrays.toString(tmpResultList.get(i+2)));
				//					
				//				}
				//				System.out.println("----------");
				//				System.out.println("---------------");
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void test() throws Exception{
		File file2=new File("e:\\tmp\\dataset\\huoying3_1.wav");
		List<double[]> windowList=new ArrayList<double[]>();
		List<double[]> resultList=new ArrayList<double[]>();
		JAudioUtils.getFeature(file2, windowList, resultList,"features.xml");//这里写死了配置文件的本地路径。。无奈啊。。
//		for(int i=0;i<windowList.size();i++){
//			System.out.println(Arrays.toString(windowList.get(i)));
//			System.out.println(Arrays.toString(resultList.get(i)));
//
//		}
	}
	public  static void main(String[] args) throws Exception{
		test();
	}
}
