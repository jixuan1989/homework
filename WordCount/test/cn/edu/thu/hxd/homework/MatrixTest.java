package cn.edu.thu.hxd.homework;

import java.util.Arrays;

import Jama.Matrix;

public class MatrixTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
    	double[][]data=new double[][]{{1,2,3},{1,5,6},{1, 8,9},{1,10,11}};
    	Matrix matrix=new Matrix(data);
    	matrix.getMatrix(1,2, 0,2).print(4, 2);
	}

}
