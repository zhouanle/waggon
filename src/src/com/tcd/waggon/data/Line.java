package com.tcd.waggon.data;

import java.util.ArrayList;

public class Line {
	private String id;
	private String name;
	private ArrayList<Tower> towers;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addTower(Tower t) {
		towers.add(t);
	}
	
	public ArrayList<Tower> getTowers() {
		return towers;
	}

	public Line(String id, String name) {
		this.id = id;
		this.name = name;
		this.towers = new ArrayList<Tower>(5);
	}

	public Line() {
		this.id = "";
		this.name = "";
		this.towers = new ArrayList<Tower>(5);
	}
	
	@Override
	public String toString() {
		return "Line [id=" + id + ", name=" + name + ", towers=" + towers + "]";
	}
}
