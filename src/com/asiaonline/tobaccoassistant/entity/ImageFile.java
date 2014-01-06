package com.asiaonline.tobaccoassistant.entity;

public class ImageFile {
	private long id;
	private String name;  //图片名称
	private String uploadTime; //图片上传时间
	public ImageFile(){
		
	}
	public ImageFile(long id, String name, String uploadTime) {
		super();
		this.id = id;
		this.name = name;
		this.uploadTime = uploadTime;
	}
	public ImageFile(String name, String uploadTime) {
		super();
		this.name = name;
		this.uploadTime = uploadTime;
	}
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}
	
}
