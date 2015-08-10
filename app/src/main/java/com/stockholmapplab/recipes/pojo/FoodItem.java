package com.stockholmapplab.recipes.pojo;

public class FoodItem {

	private String name;
	private int cal;
	private String date;

	public FoodItem(String name, int cal) {
		this.name = name;
		this.cal = cal;
	}

	public FoodItem(String name, int cal, String date) {
		this.name = name;
		this.cal = cal;
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCal() {
		return cal;
	}

	public void setCal(int cal) {
		this.cal = cal;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

}
