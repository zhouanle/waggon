package com.tcd.waggon.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

import com.tcd.waggon.Constants;

public class Connector {
	private static Connector mInstance = null;
	
	private Connector() {
		
	}
	
	public static final Connector getInstance() {
		if (mInstance == null)
			mInstance = new Connector();
		
		return mInstance;
	}
	
	/*
	 * Login to server
	 * 
	 * @return session ID, "fail" means fail
	 * 
	 */
	public String login(String name, String pwd) {
		String sid = Constants.LOGIN_FAILED;
		
		String url = Constants.LOGIN_URL + name + "&password=" + pwd;
		if (Constants.IS_DEBUG)  Log.d(Constants.LOG_TAG, url);
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		//WorkListAdapter listener;
		InputStream in = null;
		
		try {
			HttpParams params = new BasicHttpParams();
			params.setIntParameter(AllClientPNames.CONNECTION_TIMEOUT, 20000);
			params.setIntParameter(AllClientPNames.SO_TIMEOUT, 20000);
			httpClient.setParams(params);
			
			HttpResponse response =httpClient.execute(request);
			/*Header[] header = response.getAllHeaders();
	        
	        for (int i = 0; i < header.length; i++) {;
	          Log.d(TAG, "Header[" + header[i].getName() + "] = " + header[i].getValue());
	        }*/
	        
	        HttpEntity entity = response.getEntity();
	        /*Header contentType = entity.getContentType();
	        Header contentEncoding = entity.getContentEncoding();
	        long contentLength = entity.getContentLength();
	        
	        Log.d(TAG, "contentType = " + contentType.getName() + ", " + contentType.getValue());
	        if (contentEncoding != null)
	        	Log.d(TAG, "contentEncoding = " + contentEncoding.getName() + ", " + contentEncoding.getValue());
	        else
	        	Log.d(TAG, "empty contentEncoding!");
	        Log.d(TAG, "contentLength = " + contentLength);
	        Log.d(TAG, "isStreaming = " + entity.isStreaming());*/
	        
	        in = entity.getContent();
	        byte[] buff = new byte[1024];
	        int n = in.read(buff);
	        if (Constants.IS_DEBUG)  Log.d(Constants.LOG_TAG, "Read " + n + " bytes");
	        sid = new String(buff, 0, n);
            if (Constants.IS_DEBUG)  Log.d(Constants.LOG_TAG, "Session ID = " + sid);
	        /*if ((n = in.read(buff)) != -1) {
	            sid = new String(buff);
	            if (Constants.IS_DEBUG)  Log.d(Constants.LOG_TAG, "SessionID = " + sid);
	        }*/
	        /*FileOutputStream out = new FileOutputStream(Constants.WORKLIST_XML);
	        byte[] buff = new byte[1024];
	        int n = 0;
	        while ((n = in.read(buff)) != -1) {
	            out.write(buff, 0, n);
	        }
		    out.close();*/
		} catch (ClientProtocolException ex) {
			Log.e(Constants.LOG_TAG, ex.toString());
			sid = Constants.LOGIN_TIMEOUT;
		} catch (IOException ex) {
			Log.e(Constants.LOG_TAG, ex.toString());
			sid = Constants.LOGIN_TIMEOUT;
		} finally {
			if ( in != null ){
				try{
					in.close();
				}catch(Exception e){
				}
			}
	        httpClient.getConnectionManager().shutdown();
		}
		
		return sid;
	}
	
	public String loginFake(String name, String pwd) {
		String sid = "abcd1234";
		
		String url = Constants.LOGIN_URL + name + "&password=" + pwd;
		if (Constants.IS_DEBUG)  Log.d(Constants.LOG_TAG, url);
		
		return sid;
	}
}
