package com.stockholmapplab.recipes.pojo;

public class Point {

	private int[] x = new int[5];
	private int y[] = new int[5];

	public int getX(int index) {
		return x[index];
	}

	public void setX(int x, int index) {
		this.x[index] = x;
	}

	public int getY(int index) {
		return y[index];
	}

	public void setY(int y, int index) {
		this.y[index] = y;
	}

}
