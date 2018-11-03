package com.tcd.waggon.service;

import com.tcd.waggon.data.Line;
import com.tcd.waggon.data.Tower;
import com.tcd.waggon.data.WorkList;

public class WorkListAdapter
	implements IWorkListListener {
	private WorkList list;
	
	public WorkListAdapter() {
		list = new WorkList();
	}
	
	public void onLineAdded(Line l) {
		list.addLine(l);
	}
	
	public void onTowerAdded(Line l, Tower t) {
		int idx = list.getLines().indexOf(l);
		list.getLines().get(idx).addTower(t);
	}
	
	public void clear() {
		list = null;
	}
	
	public WorkList getWorkList() {
		return list;
	}
}
