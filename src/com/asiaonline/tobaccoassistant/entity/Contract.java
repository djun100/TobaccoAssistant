package com.asiaonline.tobaccoassistant.entity;

import java.io.Serializable;

public class Contract implements Serializable{

	private static final long serialVersionUID = 1L;
	private int id;
	private String name; //名字
	private String gender; //性别
	private String image;  //身份证照片保存地址
	private String indentityCard;//身份证号
	private String birthday; //生日
	private String national; //民族
	private String address; //地址
	private String villageName;  //村委会名称
	private int familyMembers  ; //家庭人口数
	private int workers;	//劳动人口
	private String education;//文化程度
	private String prevPlantType;//前作品种
	private double plantTYArea;//田烟面积
	private double plantDYArea;//地烟面积
	private String bankCard;//银行卡照片名称
	private String bankCardNo; //银行卡账号
	private String signTime; //录入时间 yyyy-MM-dd
	private String longtitude; //经度
	private String latitude;//纬度
	private int status;	//状态
	public static final int YES = 1; //种植
	public static final int NO = 2;	//不种植
	public static final int GTASKS= 3; //未处理
	public Contract(){
		
	}
	public Contract(String name, String gender, String image,
			String indentityCard, String birthday, String national,
			String address, String villageName, int familyMembers, int workers,
			String education, String prevPlantType, double plantTYArea,
			double plantDYArea, String bankCard, String bankCardNo,
			String signTime, String longtitude, String latitude, int status) {
		super();
		this.name = name;
		this.gender = gender;
		this.image = image;
		this.indentityCard = indentityCard;
		this.birthday = birthday;
		this.national = national;
		this.address = address;
		this.villageName = villageName;
		this.familyMembers = familyMembers;
		this.workers = workers;
		this.education = education;
		this.prevPlantType = prevPlantType;
		this.plantTYArea = plantTYArea;
		this.plantDYArea = plantDYArea;
		this.bankCard = bankCard;
		this.bankCardNo = bankCardNo;
		this.signTime = signTime;
		this.longtitude = longtitude;
		this.latitude = latitude;
		this.status = status;
	}
	public Contract(int id, String name, String gender, String image,
			String indentityCard, String birthday, String national,
			String address, String villageName, int familyMembers, int workers,
			String education, String prevPlantType, double plantTYArea,
			double plantDYArea, String bankCard, String bankCardNo,
			String signTime, String longtitude, String latitude, int status) {
		super();
		this.id = id;
		this.name = name;
		this.gender = gender;
		this.image = image;
		this.indentityCard = indentityCard;
		this.birthday = birthday;
		this.national = national;
		this.address = address;
		this.villageName = villageName;
		this.familyMembers = familyMembers;
		this.workers = workers;
		this.education = education;
		this.prevPlantType = prevPlantType;
		this.plantTYArea = plantTYArea;
		this.plantDYArea = plantDYArea;
		this.bankCard = bankCard;
		this.bankCardNo = bankCardNo;
		this.signTime = signTime;
		this.longtitude = longtitude;
		this.latitude = latitude;
		this.status = status;
	}
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getIndentityCard() {
		return indentityCard;
	}
	public void setIndentityCard(String indentityCard) {
		this.indentityCard = indentityCard;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getNational() {
		return national;
	}
	public void setNational(String national) {
		this.national = national;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getVillageName() {
		return villageName;
	}
	public void setVillageName(String villageName) {
		this.villageName = villageName;
	}
	public int getFamilyMembers() {
		return familyMembers;
	}
	public void setFamilyMembers(int familyMembers) {
		this.familyMembers = familyMembers;
	}
	public int getWorkers() {
		return workers;
	}
	public void setWorkers(int workers) {
		this.workers = workers;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getPrevPlantType() {
		return prevPlantType;
	}
	public void setPrevPlantType(String prevPlantType) {
		this.prevPlantType = prevPlantType;
	}
	public double getPlantTYArea() {
		return plantTYArea;
	}
	public void setPlantTYArea(double plantTYArea) {
		this.plantTYArea = plantTYArea;
	}
	public double getPlantDYArea() {
		return plantDYArea;
	}
	public void setPlantDYArea(double plantDYArea) {
		this.plantDYArea = plantDYArea;
	}
	public String getBankCard() {
		return bankCard;
	}
	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}
	public String getBankCardNo() {
		return bankCardNo;
	}
	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}
	public String getSignTime() {
		return signTime;
	}
	public void setSignTime(String signTime) {
		this.signTime = signTime;
	}
	public String getLongtitude() {
		return longtitude;
	}
	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
	
}
