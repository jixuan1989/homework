package utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XMLParser {

	public static void main(String[]s){
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc;
			doc = builder.build(new File("e:\\Users\\hxd\\Desktop\\Petri net 1.xml"));
			//doc=builder.build(new File(file));
			Element root = doc.getRootElement().getChild("net");
			
			List places=root.getChildren("place");
			Map<String, Integer> placeMap=new HashMap<String, Integer>();
			for(int i=0;i<places.size();i++){
				placeMap.put(((Element)places.get(i)).getAttributeValue("id"), i);
			}
			List transitions=root.getChildren("transition");
			Map<String, Integer> transitionMap=new HashMap<String, Integer>();
			for(int i=0;i<transitions.size();i++){
				transitionMap.put(((Element)transitions.get(i)).getAttributeValue("id"), i);
			}
			int[][] matrix=new int[places.size()][transitions.size()];
			List arcs=root.getChildren("arc");
			for(Object arc:arcs){
				String source=((Element)arc).getAttributeValue("source");
				String target=((Element)arc).getAttributeValue("target");
				if(placeMap.containsKey(source)){
					matrix[placeMap.get(source)][transitionMap.get(target)]=1;
				}else{
					matrix[placeMap.get(target)][transitionMap.get(source)]=-1;
				}
			}
			for(int i=0;i<transitions.size();i++){
				System.out.print("\t"+((Element)transitions.get(i)).getAttributeValue("id"));
			}
			System.out.println();
			for(int i=0;i<matrix.length;i++){
//				System.out.print(((Element)places.get(i)).getAttributeValue("id")+"\t");
				for(int j=0;j<matrix[i].length;j++){
					System.out.print(matrix[i][j]+"\t");
				}
				System.out.println();
			}
			int[][] matrix2=new int[places.size()+transitions.size()][places.size()+transitions.size()];
			for(Object arc:arcs){
				String source=((Element)arc).getAttributeValue("source");
				String target=((Element)arc).getAttributeValue("target");
				if(placeMap.containsKey(source)){
					matrix2[placeMap.get(source)][transitionMap.get(target)+places.size()]=1;
				}else{
					matrix2[transitionMap.get(source)+places.size()][placeMap.get(target)]=1;
				}
			}
//			System.out.print("\t");
			for(int i=0;i<matrix2.length;i++){
				if(i<places.size()){
					System.out.print(((Element)places.get(i)).getAttributeValue("id")+"\t");
				}else{
					System.out.print(((Element)transitions.get(i-places.size())).getAttributeValue("id")+"\t");
				}
			}
			for(int i=0;i<matrix2.length;i++){
				
				for(int j=0;j<matrix2[i].length;j++){
					System.out.print(matrix2[i][j]+"\t");
				}
				System.out.println();
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
