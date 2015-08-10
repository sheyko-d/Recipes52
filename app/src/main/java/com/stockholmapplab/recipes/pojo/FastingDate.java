package com.stockholmapplab.recipes.pojo;

public class FastingDate {

	private long time;
	private boolean isCheck;

	public FastingDate(long time, boolean isCheck) {
		this.time = time;
		this.isCheck = isCheck;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

}
