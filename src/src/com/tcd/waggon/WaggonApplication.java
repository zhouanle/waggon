package com.tcd.waggon;

import java.util.HashMap;

import android.app.Application;

public class WaggonApplication extends Application {
	private static WaggonApplication mInstance = null;
	private final HashMap myData = new HashMap();
	
	public static final WaggonApplication getInstance() {
		return mInstance;
	}
	
	public WaggonApplication() {
		mInstance = this;
	}
	
	public void onCreate() {
		super.onCreate();
	}
	
	public void onTerminate() {
		super.onTerminate();
	}

	public void putData(Object key, Object value) {
		myData.put(key, value);
	}

	public void removeData(Object key) {
		myData.remove(key);
	}

	public Object getData(Object key) {
		return myData.get(key);
	}
}
