package com.tcd.waggon.service;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.SAXException;

import android.util.Log;

import com.tcd.waggon.Constants;
import com.tcd.waggon.WaggonApplication;
import com.tcd.waggon.data.Session;
import com.tcd.waggon.data.WorkList;
import com.tcd.waggon.util.Utils;

public class Downloader {
	private static Downloader mInstance = null;
	private SAXParserFactory saxParserFactory;
	private static String user;
	
	public static final Downloader getInstance() {
		if (mInstance == null) {
			mInstance = new Downloader();
		}
    	
		return mInstance;
	}
	
	private Downloader() {
		saxParserFactory = SAXParserFactory.newInstance();
		
		WaggonApplication app = WaggonApplication.getInstance();
    	Session session = (Session) app.getData(Constants.SESSION);
    	user = session.getUser();
	}
	
	public WorkList downloadWorkList(String sid) {
		String url = Constants.DOWNLOAD_URL + sid;
		if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, url);
		String xml = Constants.LOCAL_PATH + user + "/" + Constants.WORKLIST_XML;
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		WorkListAdapter listener;
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
	        FileOutputStream out = new FileOutputStream(xml);
	        byte[] buff = new byte[1024];
	        int n = 0;
	        while ((n = in.read(buff)) != -1) {
	            out.write(buff, 0, n);
	        }
		    out.close();
	        in.close();
	        in = null;
	        
		    InputStream in2 = new DataInputStream(new FileInputStream(xml));
	        listener = new WorkListAdapter();
		    parseSearchResult(in2, listener);
	        in2.close();
	        in2 = null;

			return listener.getWorkList();
		} catch (ClientProtocolException ex) {
			Log.e(Constants.LOG_TAG, ex.toString());
			return null;
		} catch (IOException ex) {
			Log.e(Constants.LOG_TAG, ex.toString());
			return null;
		} finally {
			if ( in != null ){
				try{
					in.close();
				}catch(Exception e){
				}
			}
	        httpClient.getConnectionManager().shutdown();
		}
	}
	
	// For test only
	public WorkList downloadWorkListFake(String sid) {
		FileInputStream in;
		String xml = Constants.LOCAL_PATH + user + "/" + Constants.WORKLIST_XML;
		
		if (!Utils.hasLocalWorkList(xml))
			return null;
		
		try {
			in = new FileInputStream(xml);
                
			WorkListAdapter listener = new WorkListAdapter();
		    parseSearchResult(in, listener);
		    if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "WorkList = " + listener.getWorkList().toString());
		    
	        in.close();
	        in = null;
	        
	        return listener.getWorkList();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private void parseSearchResult(InputStream in, WorkListAdapter listener) {
		WorkListHandler handler = new WorkListHandler(listener);
        try {
            SAXParser parser = saxParserFactory.newSAXParser();
            parser.parse(in, handler);
        }
        catch (SAXException e) {
        	Log.e(Constants.LOG_TAG, "SAXException." + e);
            listener.clear();
        }
        catch (IOException e) {
        	Log.e(Constants.LOG_TAG, "IOException." + e);
        	listener.clear();
        }
        catch (ParserConfigurationException e) {
        	Log.e(Constants.LOG_TAG, "ParserConfigurationException." + e);
        	listener.clear();
        }
	}
}
