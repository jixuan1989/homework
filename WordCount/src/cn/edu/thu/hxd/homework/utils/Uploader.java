package cn.edu.thu.hxd.homework.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Uploader {
	public static void main(String[] args) throws IOException{
//		uploadmp4();
		uploadlist();
	}
	public static void uploadlist() throws IOException{
		Uploader uploader=new Uploader();
		uploader.upload("e:\\tmp\\mutli_frame_list.txt","/list/frames.txt");
		uploader.getFiles("/mutli_movie_frames","e:\\tmp\\mutli_frame_list.txt");
		uploader.getFiles("/mutli_movie_wav","e:\\tmp\\mutli_wav_list.txt");
		uploader.upload("e:\\tmp\\mutli_wav_list.txt","/list/wav.txt");
	}
	public static void uploadmp4() throws IOException{
		Uploader uploader=new Uploader();
		uploader.cleanLocalFile("e:\\tmp\\dataset");
		uploader.upload("e:\\tmp\\dataset","/mutli");
		uploader.getFiles("/mutli","e:\\tmp\\mutlilist");
		uploader.upload("e:\\tmp\\mutlilist","/mutli/list.txt");
	}
	//苦逼的shell会把路径中的[]当做正则表达式之类的东西....极端坑，因此替换掉
	public void cleanLocalFile(String folderpath){
		File folder=new File(folderpath);
		for (File file:folder.listFiles()){
			String string=file.getName();
			string=string.replace('[', '_').replace(']', '_').replace(' ', '_');
			file.renameTo(new File(file.getParent()+"/"+string));
		}
	}
	public void  upload(String local,String remote) throws IOException{
		Configuration conf=new Configuration();
		conf.set("fs.default.name", "hdfs://192.168.137.130:9000");
		FileSystem hdfs=FileSystem.get(conf);
		//本地文件
		Path src =new Path(local);
		//HDFS为止
		Path dst =new Path(remote);
		hdfs.copyFromLocalFile(src, dst);
		
		System.out.println("Upload to"+conf.get("fs.default.name"));
		FileStatus files[]=hdfs.listStatus(dst);
		for(FileStatus file:files){
			System.out.println(file.getPath());
		}
		//也可以使用流的方式：http://supercharles888.blog.51cto.com/609344/878921
	}
	
	public  void getFiles(String remote,String filepath) throws IOException{
		FileWriter writer=new FileWriter(new File(filepath));
		Configuration conf=new Configuration();
		conf.set("fs.default.name", "hdfs://192.168.137.130:9000");
		FileSystem hdfs=FileSystem.get(conf);
		Path dst =new Path(remote);
		FileStatus files[]=hdfs.listStatus(dst);
		for(FileStatus file:files){
			if(file.isDir()){
				getFiles(remote+"/"+file.getPath().getName(), writer,hdfs);
			}else{
				writer.write(remote+"/"+file.getPath().getName()+"\n");
//				System.out.println(remote+"/"+file.getPath().getName()+"\n");
			}
		}
		writer.close();
	}
	private  void getFiles(String remote,FileWriter writer,FileSystem hdfs) throws IOException{
		Path dst =new Path(remote);
		FileStatus files[]=hdfs.listStatus(dst);
		for(FileStatus file:files){
			if(file.isDir()){
				getFiles(remote+"/"+file.getPath().getName(), writer,hdfs);
			}else{
				writer.write(remote+"/"+file.getPath().getName()+"\n");
//				System.out.println(remote+"/"+file.getPath().getName()+"\n");
			}
		}
	}
	
	
}
