package com.tcd.waggon.data;

import java.util.ArrayList;

public class Tower {
	private static final int TOWER_TYPE_UNKNOWN = 0;
	// 直线自立塔
	public static final int TOWER_TYPE1 = 1;
	// 砼杆
	public static final int TOWER_TYPE2 = 2;
	// LV塔
	public static final int TOWER_TYPE3 = 3;
	// 耐张塔
	public static final int TOWER_TYPE4 = 4;
	
	private String id;
	private int type;
	// latitude
	private double lat;
	// longitude
	private double lon;
	private String addr;
	private boolean checked;
	private boolean near;
	private double dist;
	private ArrayList<CheckItem> items;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public double getLat() {
		return lat;
	}
	
	public void setLat(double d) {
		this.lat = d;
	}
	
	public double getLon() {
		return lon;
	}
	
	public void setLon(double lon) {
		this.lon = lon;
	}
	
	public String getAddr() {
		return addr;
	}
	
	public void setAddr(String addr) {
		this.addr = addr;
	}
	
	public boolean isChecked() {
		return this.checked;
	}
	
	public void setChecked(boolean c) {
		this.checked = c;
	}

	/*public Tower(String id, int type, float lat, float lon, String addr) {
		this.id = id;
		this.type = type;
		this.lat = lat;
		this.lon = lon;
		this.addr = addr;
	}*/
	
	public Tower() {
		this.id = "";
		this.type = TOWER_TYPE_UNKNOWN;
		this.lat = 0.0f;
		this.lon = 0.0f;
		this.addr = "";
		this.checked = false;
		this.near = false;
		this.dist = -0.0f;
		this.items = new ArrayList<CheckItem>(10);
	}

	public boolean isNear() {
		return near;
	}

	public void setNear(boolean near) {
		this.near = near;
	}

	public double getDist() {
		return this.dist;
	}
	
	public void setDist(double dist) {
		this.dist = dist;
	}
	
	public ArrayList<CheckItem> getItems() {
		return this.items;
	}
	
	public void clearItems() {
		this.items.clear();
	}
	
	@Override
	public String toString() {
		return "Tower [id=" + id + ", type=" + type + ", lat=" + lat + ", lon="
				+ lon + ", addr=" + addr + ", checked=" + checked + ", near="
				+ near + ", dist=" + dist + "]" ;
	}
}
