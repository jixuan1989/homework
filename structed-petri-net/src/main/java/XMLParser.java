import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class XMLParser {

	public  List<Node> parse(String filepath){
		try {
			SAXBuilder builder = new SAXBuilder();
			Document doc;
			doc = builder.build(new File(filepath));

			Element root = doc.getRootElement().getChild("net");
			List<Node> nodes=new ArrayList<Node>();
			//add places
			List places=root.getChildren("place");
			Map<String, Integer> placeMap=new HashMap<String, Integer>();
			for(int i=0;i<places.size();i++){
				placeMap.put(((Element)places.get(i)).getAttributeValue("id"), i);
				String tag=((Element)places.get(i)).getAttributeValue("id");
				Node node=new Node(tag, true);
				nodes.add(node);
			}

			//add transitions
			int k=nodes.size();
			List transitions=root.getChildren("transition");
			Map<String, Integer> transitionMap=new HashMap<String, Integer>();
			for(int i=0;i<transitions.size();i++){
				transitionMap.put(((Element)transitions.get(i)).getAttributeValue("id"), i+k);
				String tag=((Element)transitions.get(i)).getAttributeValue("id");
				Node node=new Node(tag, false);
				nodes.add(node);
			}
			//int[][] matrix=new int[places.size()][transitions.size()];
			List arcs=root.getChildren("arc");
			for(Object arc:arcs){
				String source=((Element)arc).getAttributeValue("source");
				String target=((Element)arc).getAttributeValue("target");
				if(placeMap.containsKey(source)){
					//matrix[placeMap.get(source)][transitionMap.get(target)]=1;
					nodes.get(placeMap.get(source)).addChild(nodes.get(transitionMap.get(target)));
					nodes.get(transitionMap.get(target)).addParent(nodes.get(placeMap.get(source)));
				}else{
					//matrix[placeMap.get(target)][transitionMap.get(source)]=-1;
					nodes.get(placeMap.get(target)).addParent(nodes.get(transitionMap.get(source)));
					nodes.get(transitionMap.get(source)).addChild(nodes.get(placeMap.get(target)));
				}
			}
			return nodes;

			/*	for(int i=0;i<transitions.size();i++){
				System.out.print("\t"+((Element)transitions.get(i)).getAttributeValue("id"));
			}
			System.out.println();
			for(int i=0;i<matrix.length;i++){
				System.out.print(((Element)places.get(i)).getAttributeValue("id")+"\t");
				for(int j=0;j<matrix[i].length;j++){
					System.out.print(matrix[i][j]+"\t");
				}
				System.out.println();
			}  */
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public void updateXML(List<Node> nodes,String filepath,int tag,String info)throws Exception{
		float offset=0;
		float width=0;
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		doc = builder.build(new File(filepath));

		Element root = doc.getRootElement().getChild("net");

		Element token=(Element) root.getChild("token").clone();
		//get places.position
		List places=root.getChildren("place");
		Map<String, Float> placeX=new HashMap<String, Float>();
		Map<String, Float> placeY=new HashMap<String, Float>();
		for(int i=0;i<places.size();i++){
			placeX.put(((Element)places.get(i)).getAttributeValue("id"), Float.valueOf(((Element)places.get(i)).getChild("graphics").getChild("position").getAttributeValue("x")));
			placeY.put(((Element)places.get(i)).getAttributeValue("id"), Float.valueOf(((Element)places.get(i)).getChild("graphics").getChild("position").getAttributeValue("y")));
		}
		offset=Collections.max(placeY.values());
		width=Collections.max(placeX.values());
		//get transitions.position	
		List transitions=root.getChildren("transition");
		Map<String, Float> transitionX=new HashMap<String, Float>();
		Map<String, Float> transitionY=new HashMap<String, Float>();
		for(int i=0;i<transitions.size();i++){
			transitionX.put(((Element)transitions.get(i)).getAttributeValue("id"),Float.valueOf(((Element)transitions.get(i)).getChild("graphics").getChild("position").getAttributeValue("x")) );
			transitionY.put(((Element)transitions.get(i)).getAttributeValue("id"),Float.valueOf(((Element)transitions.get(i)).getChild("graphics").getChild("position").getAttributeValue("y")) );
		}
		offset=offset<Collections.max(transitionY.values())?Collections.max(transitionY.values()):offset;
		offset+=50;
		

		Element Root =doc.getRootElement();
//		Element net=new Element("net");
		Element net=root;
		net.setAttribute("id","auto "+tag);
		net.setAttribute("type","P/T net");
//		net.addContent(token);

		List list=doc.getRootElement().getChildren("net");
		Element ee=(Element )list.get(list.size()-1);
		if(ee.getAttributeValue("offsety")!=null){
			offset=Float.valueOf(ee.getAttributeValue("offsety"))+Float.valueOf(ee.getAttributeValue("height"))+50;
			net.setAttribute("offsety",offset+"");
			net.setAttribute("height",ee.getAttributeValue("height"));
		}
		else{
			net.setAttribute("offsety",(offset)+"");
			net.setAttribute("height",(offset-50)+"");
		}

		//add place and transition
		for(Node node:nodes){
			if(node.getChildren().size()>0 || node.getParents().size()>0 ){
				if(node.isPlace){
					Element place=new Element("place");
					place.setAttribute("id", node.getTag()+"_"+tag);
					Element graphics=new Element("graphics");
					Element position =new Element("position");
					position.setAttribute("x", String.valueOf(placeX.get(node.getTag())));
					position.setAttribute("y", String.valueOf(placeY.get(node.getTag())+offset));
					graphics.addContent(position);
					place.addContent(graphics);
					
					Element name=new Element("name");
					Element value=new Element("value");
					value.setText(node.getTag()+"_"+tag);
					Element graphics2=new Element("graphics");
					Element offsetElement=new Element("offset");
					offsetElement.setAttribute("x","0.0");
					offsetElement.setAttribute("y","0.0");
					graphics2.addContent(offsetElement);
					name.addContent(graphics2);
					name.addContent(value);
					place.addContent(name);
					
					Element initialMarking=new Element("initialMarking");
					Element initialMarkingValue=new Element("value");
					initialMarkingValue.setText("Default,0");
					Element initialMarkingGraphics=new Element("graphics");
					Element initialMarkingGraphicsOffset=new Element("offset");
					initialMarkingGraphicsOffset.setAttribute("x","0.0");
					initialMarkingGraphicsOffset.setAttribute("y","0.0");
					initialMarkingGraphics.addContent(initialMarkingGraphicsOffset);
					initialMarking.addContent(initialMarkingValue);
					initialMarking.addContent(initialMarkingGraphics);
					place.addContent(initialMarking);
					
					Element capacity=new Element("capacity");
					Element capacityValue=new Element("value");	
					capacityValue.setText("0");
					capacity.addContent(capacityValue);
					place.addContent(capacity);
					
					net.addContent(place);
				}
				else{
					Element transition=new Element("transition");
					transition.setAttribute("id", node.getTag()+"_"+tag);
					Element graphics=new Element("graphics");
					Element position =new Element("position");
					position.setAttribute("x", String.valueOf(transitionX.get(node.getTag())));
					position.setAttribute("y", String.valueOf(transitionY.get(node.getTag())+offset));
					graphics.addContent(position);
					
					
					Element name=new Element("name");
					Element value=new Element("value");
					value.setText(node.getTag()+"_"+tag);
					Element graphics2=new Element("graphics");
					Element offsetElement=new Element("offset");
					offsetElement.setAttribute("x","-5.0");
					offsetElement.setAttribute("y","35.0");
					graphics2.addContent(offsetElement);
					name.addContent(graphics2);
					name.addContent(value);
					transition.addContent(name);
					
					
					/*
					 * 
<orientation>
	<value>0</value>
</orientation>
<rate>
	<value>1.0</value>
</rate>
<timed>
	<value>false</value>
</timed>
<infiniteServer>
	<value>false</value>
</infiniteServer>
<priority>
	<value>1</value>
</priority>
					 */
					Element element=new Element("orientation");
					Element value1=new Element("value");
					element.addContent(value1);
					value1.setText("0");
					transition.addContent(element);

					element=new Element("rate");
					value1=new Element("value");
					value1.setText("1.0");
					element.addContent(value1);
					transition.addContent(element);
					
					element=new Element("timed");
					value1=new Element("value");
					value1.setText("false");
					element.addContent(value1);
					transition.addContent(element);
					
					element=new Element("infiniteServer");
					value1=new Element("value");
					value1.setText("false");
					element.addContent(value1);
					transition.addContent(element);
					
					element=new Element("priority");
					value1=new Element("value");
					value1.setText("1");
					element.addContent(value1);
					transition.addContent(element);				
					
					
					transition.addContent(graphics);
					net.addContent(transition);
				}
			}
		}
		//add arc
		for(Node node:nodes){
			if(node.getChildren().size()>0){

				Iterator<Node> ite=node.getChildren().iterator();
				while(ite.hasNext()){
					Node child=ite.next();
					Element arc=new Element("arc");
					arc.setAttribute("id", node.getTag()+"_"+tag+" to "+child.getTag()+"_"+tag);
					arc.setAttribute("source", node.getTag()+"_"+tag);
					arc.setAttribute("target", child.getTag()+"_"+tag);
					
					Element element=new Element("tagged");
					Element value1=new Element("value");
					value1.setText("false");
					element.addContent(value1);
					arc.addContent(element);

					element =new Element("type");
					element.setAttribute("value","normal");
					arc.addContent(element);
					
					net.addContent(arc);
				}
			}
		}
		Element label=new Element("labels");
		label.setAttribute("x","0");
		label.setAttribute("y",""+(new Float(offset)).intValue());
		label.setAttribute("width",""+(new Float(width)).intValue());
		label.setAttribute("height",40+"");
		label.setAttribute("border","true");
		Element text=new Element("text");
		text.setText(info);
		label.addContent(text);
		net.addContent(label);
//		Root.addContent(net);
		
		XMLOutputter out = new XMLOutputter();
		out.output(doc, new FileOutputStream(filepath));


	}
}

