package cn.edu.thu.hxd.homework.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;

import cn.edu.thu.hxd.homework.image.ImageIndex;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <code>Set welcome message.</code>
 */
public class ImageAction extends ActionSupport {
	private File image; //上传的文件
	private String imageFileName; //文件名称
	private String imageContentType; //文件类型
	private List<String> results=new ArrayList<String>();
	
	static ImageIndex imgIndex=new ImageIndex("hdfs://pc0:9000/features/frames/part-00000");
	static{
		try {
			imgIndex.createIndex();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String execute() throws Exception {
		System.out.println("hello~~~~");
//		String realpath = ServletActionContext.getServletContext().getRealPath("/images");
//		//D:\apache-tomcat-6.0.18\webapps\struts2_upload\images
//		System.out.println("realpath: "+realpath);
		if (image != null) {
//			File savefile = new File(new File("e:\\tmp\\uploader"), imageFileName);
//			if (!savefile.getParentFile().exists())
//				savefile.getParentFile().mkdirs();
//			FileUtils.copyFile(image, savefile);
			ActionContext.getContext().put("message", "文件上传成功");
			
			System.out.println("begin to query...");
			
			results=imgIndex.query(image);
			
//			for(String string:tmpresults){
//				results.add("e:/tmp"+string);
//			}
		}
		return "success";
	}

	/**
	 * Provide default valuie for Message property.
	 */
	public static final String MESSAGE = "hello.message";

	/**
	 * Field for Message property.
	 */
	private String message;

	/**
	 * Return Message property.
	 *
	 * @return Message property
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set Message property.
	 *
	 * @param message Text to display on HelloWorld page.
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	public String getImageFileName() {
		return imageFileName;
	}

	public void setImageFileName(String imageFileName) {
		this.imageFileName = imageFileName;
	}

	public String getImageContentType() {
		return imageContentType;
	}

	public void setImageContentType(String imageContentType) {
		this.imageContentType = imageContentType;
	}

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}
}
