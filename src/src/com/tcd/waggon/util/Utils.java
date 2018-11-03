package com.tcd.waggon.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import com.tcd.waggon.Constants;
import com.tcd.waggon.data.Session;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utils {
	public static boolean isConnected(Context c) {
		boolean bNetwork = false;
		
		ConnectivityManager mConnMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo[] nis = mConnMgr.getAllNetworkInfo();
		if (nis != null) {
			for (NetworkInfo ni : nis) {
				if (ni != null) {
					if (ni.isConnected() == true) {
						bNetwork = true;
						break;
					}
				}
			}
		}
		return bNetwork;
	}
	
	public static void initWaggonDir() {
		try {
			File rootdir = new File(Constants.LOCAL_PATH);
			if (rootdir.exists() == false)
				rootdir.mkdir();
			else if (rootdir.isDirectory() == false) {
				rootdir.delete();
				rootdir.mkdir();
			}
    	} catch (Exception ex) {
    		Log.e(Constants.LOG_TAG, "fail to init waggon directory. " + ex.toString());
    	}
	}
	
	public static void initUserDir(String user) {
		try {
			File rootdir = new File(Constants.LOCAL_PATH + user);
			if (rootdir.exists() == false)
				rootdir.mkdir();
			else if (rootdir.isDirectory() == false) {
				rootdir.delete();
				rootdir.mkdir();
			}
    	} catch (Exception ex) {
    		Log.e(Constants.LOG_TAG, "fail to init user directory. " + ex.toString());
    	}
	}
	
	/* return distance of 2 point, meters */
	public static double calcDistance(Location l1, Location l2) {	
		double dx = (l1.getLongitude() * 100000)  - (l2.getLongitude() * 100000);

	    double dy = (l1.getLatitude() * 100000)  - (l2.getLatitude() * 100000);

	    long l  = (long) ((l1.getLatitude() * 100000)  + (l2.getLatitude() * 100000)) / 2;

	    double angle = (double) l;

	    angle = angle * (3.141592653589793 / (100000) / (180));

	    dx = dx * Math.cos(angle);

	    dx = dx * dx;

	    dy = dy * dy;
	    
		return Math.sqrt((dx + dy) * 1.1119104);
	}
	
	public static boolean hasLocalWorkList(String xml) {
		//String xml = Constants.LOCAL_PATH + user + "/" + Constants.WORKLIST_XML;
		File worklist = new File(xml);
		
		return worklist.exists();
	}
	
	public static boolean saveToLocal(Session s) {
		boolean rslt = true;
		String profile = Constants.LOCAL_PATH + s.getUser() + "/" + Constants.LOCAL_USER_FILE;
		
		try {
			FileWriter f = new FileWriter(profile);
			f.write(s.getPasswd());
			f.flush();
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
			rslt = false;
		}
		
		return rslt;
	}
	
	/*public static String getLocalPasswd(String user) {
		String passwd = "";
		
		String profile = Constants.LOCAL_PATH + user + "/" + Constants.LOCAL_USER_FILE;
		
		try {
			FileReader f = new FileReader(profile);
			//passwd = f.re

			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return passwd;
	}*/
	
	public static boolean localCheck(String user, String pwd) {
		boolean rslt = true;
		
		String profile = Constants.LOCAL_PATH + user + "/" + Constants.LOCAL_USER_FILE;
		
		/*if (new File(profile).exists() == false)
			return false;*/

		try {
			DataInputStream in = new DataInputStream(new FileInputStream(profile));
			String tmp = in.readLine();
			rslt = pwd.equals(tmp);
			
			if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Local check for user: " + user + ", passwd: " + pwd + ", " + tmp);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			rslt = false;
		} catch (IOException ex) {
			rslt = false;
			Log.e(Constants.LOG_TAG, ex.toString());
		}

		return rslt;
	}
}
