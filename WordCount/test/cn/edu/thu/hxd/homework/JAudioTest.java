package cn.edu.thu.hxd.homework;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class JAudioTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		File file=new File("E:\\tmp\\dataset\\_mutli_movie_wav_mutli_--1_Part_2_.mp4.wav");
		List<double[]> windowList=new ArrayList<double[]>();
		List<double[]> resultList=new ArrayList<double[]>();
		JAudioUtils.getFeature(file, windowList, resultList);
	}
	
}
