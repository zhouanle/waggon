package com.tcd.waggon.data;

import java.util.ArrayList;

public class WorkList {
	private String id;
	private String operator;
	private String execute_date;
	private String desc;
	private ArrayList<Line> lines;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public String getExecute_date() {
		return execute_date;
	}
	
	public void setExecute_date(String execute_date) {
		this.execute_date = execute_date;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public void addLine(Line l) {
		lines.add(l);
	}
	
	public ArrayList<Line> getLines() {
		return lines;
	}

	@Override
	public String toString() {
		return "WorkList [id=" + id + ", operator=" + operator
				+ ", execute_date=" + execute_date + ", desc=" + desc
				+ ", lines=" + lines + "]";
	}

	public WorkList(String id, String operator, String execute_date,
			String desc) {
		this.id = id;
		this.operator = operator;
		this.execute_date = execute_date;
		this.desc = desc;
		this.lines = new ArrayList<Line>(1);
	}
	
	public WorkList() {
		this.id = "";
		this.operator = "";
		this.execute_date = "";
		this.desc = "";
		this.lines = new ArrayList<Line>(1);
	}
}
