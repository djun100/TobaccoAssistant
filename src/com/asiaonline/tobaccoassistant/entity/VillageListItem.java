package com.asiaonline.tobaccoassistant.entity;

public class VillageListItem {
	private String VillageName;
	private int count;
	private double totalPlantArea;
	public VillageListItem(String villageName,int count,double totalPlantArea) {
		super();
		VillageName = villageName;
		this.count = count;
		this.totalPlantArea = totalPlantArea;
	}
	public String getVillageName() {
		return VillageName;
	}
	public void setVillageName(String villageName) {
		VillageName = villageName;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getTotalPlantArea() {
		return totalPlantArea;
	}
	public void setTotalPlantArea(double totalPlantArea) {
		this.totalPlantArea = totalPlantArea;
	}
}
