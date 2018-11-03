package com.tcd.waggon.service;

import com.tcd.waggon.data.Line;
import com.tcd.waggon.data.Tower;
import com.tcd.waggon.data.WorkList;

public interface IWorkListListener {
	public void onLineAdded(Line l);
	
	public void onTowerAdded(Line l, Tower t);
}