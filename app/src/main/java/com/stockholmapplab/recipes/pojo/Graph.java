package com.stockholmapplab.recipes.pojo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Graph implements Serializable {
	private static final long serialVersionUID = 1L;
	private double goal;

	private Map<String, Double> values = new HashMap<String, Double>();

	public Graph(double goal) {
		this.goal = goal;
	}

	public Map<String, Double> getValues() {
		return values;
	}

	public void setValues(String key, double value) {
		this.values.put(key, value);
	}

	public void setValues(Map<String, Double> value) {
		this.values.clear();
		this.values = value;
	}

	public double getGoal() {
		return goal;
	}

	public void setGoal(double goal) {
		this.goal = goal;
	}

}
