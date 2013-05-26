import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;


public class Graph {
	List<Node> nodes;
	/**
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		XMLParser parser=new XMLParser();
		Graph graph=new Graph();
//		JOptionPane.showInputDialog(parentComponent, message)
		
		graph.nodes=parser.parse("Petri net 2.xml");
		File file=new File("Petri net 2.xml");
		File file2=new File("dest.xml");
		FileUtils.copyFile(file, file2);
		boolean change=true;
		int i=1;
		
		while(change){
//			System.out.println("----------------");
//			for(Node node:graph.nodes){
//				System.out.println(node);
//			}
//			System.out.println("----------------");
			StringBuilder info=new StringBuilder();
			change=graph.reduce(info);
			parser.updateXML(graph.nodes,file2.getAbsolutePath(), i++,info.toString());
		}
		
	}
	
	public boolean reduce(StringBuilder info) {
		boolean change=false;
		Set<Node> oneNodes=new HashSet<Node>();
		//找到所有单入单出的点集合
		for(Node node:nodes){
			if(node.getChildren().size()==1&&node.getParents().size()==1){
//				System.out.println("find candidate:"+node);
				oneNodes.add(node);
			}
		}
		System.out.println();
		for(Node node:oneNodes){
			if(node.getChildren().size()!=1||node.getParents().size()!=1){
				continue;
			}
			if(node.getFirstChild().equals(node.getFirstParent())){//ESP/EST
//				System.out.println("ESP/EST:"+node);
				info.append("ESP/EST:"+node+"\n");
				node.disengage();
				change=true;
				continue;
			}
			if(node.isPlace){
				if(node.getFirstChild().getParents().size()==1){//FSP
//					System.out.println("FSP:"+node);
					info.append("FSP:"+node+"\n");
					node.getFirstParent().addChildren(node.getFirstChild().getChildren());
					node.getFirstChild().disengage();
					node.disengage();
					
					change=true;
					continue;
				}
			}else{
				if(node.getFirstParent().getChildren().size()==1){//FST
//					System.out.println("FST:"+node);
					info.append("FST:"+node+"\n");
					node.getFirstChild().addParents(node.getFirstParent().getParents());
					node.getFirstParent().disengage();
					node.disengage();
					change=true;
					continue;
				}
			}
			//
			for(Node other:oneNodes){
				if(other.equals(node)||(other.getChildren().size()!=1||other.getParents().size()!=1)){
					continue;
				}else{
					if(other.getFirstParent().equals(node.getFirstParent())&& other.getFirstChild().equals(node.getFirstChild())){
//						System.out.println("FPP/FPT:"+other);
						info.append("FPP/FPT:"+other+"\n");
						other.disengage();
						change=true;
					}
				}
			}
		}
		for(Node node:oneNodes){
			if(node.getParents().size()==0&&node.getChildren().size()==0){
				nodes.remove(node);
				info.append("remove:"+node+"\n");
//				System.out.println("remove:"+node);
			}
		}
		return change;
	}
}
