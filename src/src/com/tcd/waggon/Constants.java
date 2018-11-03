package com.tcd.waggon;

import android.location.LocationManager;

public class Constants {
	public static final boolean IS_DEBUG = true;
	public static final String LOG_TAG = "Waggon";
	
	//public static final String SRV_ADDR = "http://10.10.10.102";
	public static final String SRV_ADDR = "http://www.tcd-tech.com";
	//public static final String SRV_ADDR = "http://119.57.70.22";
	public static final String SRV_PORT = "8080";
	
	public static final String SESSION = "SESSION";
	/*public static final String SESSION_ID = "SID";
	public static final String CHECK_FLAG = "CHECK_FLAG";
	public static final String DOWNLOAD_FLAG = "DOWNLOAD_FLAG";
	public static final String UPLOAD_FLAG = "DOWNLOAD_FLAG";*/
	public static final String WORK_LIST = "WORK_LIST";
	public static final String TOWER_NAME = "TOWER_NAME";
	
	public static final int CHECK_TOWER_ACTION = 1;
	public static final int CHECK_TOWER_COMPLETE = 100;
	public static final int CHECK_TOWER_CANCEL = 101;
	
	public static final String PROVIDER = LocationManager.GPS_PROVIDER;
	//public static final String PROVIDER = LocationManager.NETWORK_PROVIDER;
	public static final float RADIUS = 100.00f; // radius = 100 meters
	public static final long INTERVAL = 1000 * 10; // 10 seconds
	/* Login URL */
	public static final String LOGIN_URL = SRV_ADDR + ":" + SRV_PORT + "/xs/pad/login.action?loginname=";
	
	/* Download worklist URL */
	public static final String DOWNLOAD_URL = SRV_ADDR + ":" + SRV_PORT + "/xs/pad/getWorkList.action?sessionid=";
	
	/* Upload worklist URL */
	//public static final String UPLOAD_URL = SRV_ADDR + ":" + SRV_PORT + "/xs/pad/uploadWorkList.action?sessionid=";
	public static final String UPLOAD_URL = SRV_ADDR + ":" + SRV_PORT + "/xs/pad/uploadWorkList.action";
	
	/* Upload Attachment URL */
	public static final String ATTACH_URL = SRV_ADDR + ":" + SRV_PORT + "/xs/pad/uploadFile.action";
	//public static final String ATTACH_URL = "http://127.0.0.1:10000";
	
	public static final String LOGIN_FAILED = "fail";
	public static final String LOGIN_TIMEOUT = "timeout";
	public static final String SESSION_LOCAL = "LOCAL_SESSION";
	
	public static final String LOCAL_PATH = "/sdcard/.waggon/";
	public static final String LOCAL_USER_FILE = ".profile";
	public static final String WORKLIST_XML = "worklist.xml";
	public static final String WORKLIST_RESULT_XML = "worklistresult.xml";
}
