package cn.edu.thu.hxd.homework.audio;

import java.io.Serializable;

import be.hogent.tarsos.lsh.Vector;

public class LinkedVector extends Vector implements Serializable{
	
	private int id=-1;
	private int total=-1;
	public LinkedVector(Vector other) {
		super(other);
		// TODO Auto-generated constructor stub
	}
	public LinkedVector(String key, double[] values) {
		super(key, values);
		// TODO Auto-generated constructor stub
	}
	public LinkedVector(int dimensions) {
		super(dimensions);
		// TODO Auto-generated constructor stub
	}
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	

}
