package cn.edu.thu.hxd.homework.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.edu.thu.hxd.homework.audio.WavIndex;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * <code>Set welcome message.</code>
 */
public class VedioAction extends ActionSupport {
	private File vedio; //上传的文件
	private String vedioFileName; //文件名称
	private String vedioContentType; //文件类型
	private List<Map.Entry<String,Double>> results=new ArrayList<Map.Entry<String,Double>>();
	
	static WavIndex wavIndex=new WavIndex("hdfs://pc0:9000/features/wavs_smooth2/part-00000");
	static{
		try {
			wavIndex.createIndex();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String execute() throws Exception {
		System.out.println("hello~~~~");
//		String realpath = ServletActionContext.getServletContext().getRealPath("/vedios");
		//D:\apache-tomcat-6.0.18\webapps\struts2_upload\images
//		System.out.println("realpath: "+realpath);
		if (vedio != null) {
//			File savefile = new File(new File("e:\\tmp\\uploader"), vedioFileName);
//			if (!savefile.getParentFile().exists())
//				savefile.getParentFile().mkdirs();
//			FileUtils.copyFile(vedio, savefile);
			ActionContext.getContext().put("message", "文件上传成功");
			
			System.out.println("begin to query...");
			results=wavIndex.query(vedio);
//			for(String string:results){
//				System.out.println(string);
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

	public String getVedioFileName() {
		return vedioFileName;
	}

	public void setVedioFileName(String vedioFileName) {
		this.vedioFileName = vedioFileName;
	}

	public String getVedioContentType() {
		return vedioContentType;
	}

	public void setVedioContentType(String vedioContentType) {
		this.vedioContentType = vedioContentType;
	}

	public File getVedio() {
		return vedio;
	}

	public void setVedio(File vedio) {
		this.vedio = vedio;
	}

	public List<Map.Entry<String,Double>> getResults() {
		return results;
	}

	public void setResults(List<Map.Entry<String,Double>> results) {
		this.results = results;
	}



}
