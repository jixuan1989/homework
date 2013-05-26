import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;



public  class Node {
	private static int total=0;
	public boolean isPlace=false;
	private Set<Node> parents=new HashSet<Node>();
	private Set<Node> children=new HashSet<Node>();
	public Node(String tag,boolean isPlace){
		this.tag=tag;
		this.isPlace=isPlace;
		this.no=total++;
	}
	private String tag;
	private int no;
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	
	public Node getFirstParent(){
		Iterator<Node> iterator=parents.iterator();
		if(iterator.hasNext()){
			return iterator.next();
		}else{
			System.err.println(tag);
		}
		return  null;
	}
	public Node getFirstChild(){
		Iterator<Node> iterator=children.iterator();
		if(iterator.hasNext()){
			return iterator.next();
		}else{
			System.err.println(tag);
		}
		return  null;
	}
	/**
	 * 增加孩子，同时通知那些孩子也增加自己
	 * @param subSet
	 */
	public void addChildren(Set<Node> subSet){
		for(Node node:subSet){
			this.children.add(node);
			node.parents.add(this);
		}
	}
	
	public void addChild(Node node){
		
			this.children.add(node);
	
	}
	
	/**
	 * 增加父母，同时通知那些父母也增加自己
	 * @param subSet
	 */
	public void addParents(Set<Node> subSet){
		for(Node node:subSet){
			this.parents.add(node);
			node.children.add(this);
		}
	}
	
	public void addParent(Node node){
		
			this.parents.add(node);			
		
	}
	
	/**
	 * 通知parent忘记自己
	 * 自己也忘记parent
	 * @param parent
	 */
	public void removeParent(Node parent){
		parents.remove(parent);
		parent.children.remove(this);
	}
	
	/**
	 * 通知孩子忘记自己
	 * 自己也忘记child
	 * @param child
	 */
	public void removeChild(Node child){
			children.remove(child);
			child.parents.remove(this);
	}
	
	/**
	 * 脱离,该方法会通知父母孩子 自己的消失消息。
	 */
	public void disengage(){
		for(Node node:parents){
			node.children.remove(this);
		}
		for(Node node:children){
			node.parents.remove(this);
		}
		this.parents.clear();
		this.children.clear();
	}
	
	
	
	public Set<Node> getParents() {
		return parents;
	}

	public void setParents(Set<Node> parents) {
		this.parents = parents;
	}

	public Set<Node> getChildren() {
		return children;
	}

	public void setChildren(Set<Node> children) {
		this.children = children;
	}
	public String toString(){
		String string="["+this.tag+",Children(";
		for(Node node:children){
			string+=node.tag+",";
		}
		string+="),Parents(";
		for(Node node:parents){
			string+=node.tag+",";
		}
		string+=").]";
		return string;
	}
		
	
}

