package com.tcd.waggon.service;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tcd.waggon.Constants;
import com.tcd.waggon.WaggonApplication;
import com.tcd.waggon.data.Session;

import android.util.Log;

public class Uploader {
	private static Uploader mInstance = null;
	private static String user;
	
	private Uploader() {
		WaggonApplication app = WaggonApplication.getInstance();
    	Session session = (Session) app.getData(Constants.SESSION);
    	user = session.getUser();
	}
	
	public static final Uploader getInstance() {
		if (mInstance == null)
			mInstance = new Uploader();
		
		return mInstance;
	}
	
	public boolean uploadResult(String sid) {
		boolean flag = true;
		String file = Constants.LOCAL_PATH + user + "/" + Constants.WORKLIST_RESULT_XML;
		URL url;
		HttpURLConnection conn;
		
		try {
			url = new URL(Constants.UPLOAD_URL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setUseCaches(false);
			
			//OutputStreamWriter out=new OutputStreamWriter(conn.getOutputStream(), "iso-8859-1");
			OutputStreamWriter out=new OutputStreamWriter(conn.getOutputStream());
			//StringBuffer req = new StringBuffer(OnlineRequest.CHANGE_NICK_PARAM + new String(newName.getBytes("iso-8859-1"),"utf-8"));
			//StringBuffer req = new StringBuffer(OnlineRequest.CHANGE_NICK_PARAM + new String(newName.getBytes("utf-8"),"iso-8859-1"));
			StringBuffer req = new StringBuffer("sessionid=");
			req.append(sid);
			req.append("&xmlFile=");
			
			DataInputStream xml = new DataInputStream(new FileInputStream(file));
			//String req = String.format(Locale.CHINA, OnlineRequest.CHANGE_NICK_PARAM + "%s" + OnlineRequest.LOGDIN_SESSION + "%s", newName, sId);

			byte[] buff = new byte[1024];
		    int n;
		    while ((n = xml.read(buff)) != -1) {
		    	if (Constants.IS_DEBUG)  Log.d(Constants.LOG_TAG, "Read " + n + " bytes");
		    	req.append(new String(buff, 0, n));
		    }
			if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "REQ = " + req.toString());
			
			out.write(req.toString());  
			out.flush();  
			out.close();
			
			InputStream in = conn.getInputStream();
	        //byte[] buff = new byte[1024];
	        //int n = in.read(buff);
			n = in.read(buff);
	        if (Constants.IS_DEBUG)  Log.d(Constants.LOG_TAG, "Read " + n + " bytes");
	        sid = new String(buff, 0, n);
            if (Constants.IS_DEBUG)  Log.d(Constants.LOG_TAG, "Upload result = " + sid);
            in.close();
		} catch (MalformedURLException ex) {
			Log.e(Constants.LOG_TAG, ex.toString());
			flag = false;
		} catch (IOException ex) {
			Log.e(Constants.LOG_TAG, ex.toString());
			flag = false;
		}
		
		return flag;
	}
	
	public boolean uploadAttachment(String sid) {
		boolean flag = true;
		
		URL url;
		HttpURLConnection conn;
		
		String lineEnd = "\r\n";
		//String twoHyphens = "--";
		String boundary =  "---------------------------7db222351b1b3a";
		DataOutputStream outputStream = null;
		
		String pathToOurFile = "/sdcard/.waggon/1.txt";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;

		try {
			FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile));
			
			url = new URL(Constants.ATTACH_URL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			
			outputStream = new DataOutputStream(conn.getOutputStream());
			//FileOutputStream out = new FileOutputStream("/sdcard/.waggon/tmp");
			//outputStream.writeBytes("sessionid=" + sid);
			outputStream.writeBytes(boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"upload\";filename=\"" + pathToOurFile +"\"" + lineEnd);
			outputStream.writeBytes(lineEnd);
			
			bytesAvailable = fileInputStream.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];

			// Read file
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			Log.d("XXX", "Write " + bytesRead + " bytes");
			while (bytesRead > 0)
			{
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
				Log.d("XXXXX", "Write " + bytesRead + " bytes");
			}

			outputStream.writeBytes(lineEnd);
			outputStream.writeBytes(boundary + lineEnd);

			// Responses from the server (code and message)
			int serverResponseCode = conn.getResponseCode();
			String serverResponseMessage = conn.getResponseMessage();

			fileInputStream.close();
			outputStream.flush();
			outputStream.close();
			Log.i(Constants.LOG_TAG, "Upload attachment " + pathToOurFile + ", rsp code = " + serverResponseCode + ", rsp msg = " + serverResponseMessage);
		} catch (MalformedURLException ex) {
			Log.e(Constants.LOG_TAG, ex.toString());
			flag = false;
		} catch (IOException ex) {
			Log.e(Constants.LOG_TAG, ex.toString());
			flag = false;
		}
		
		return flag;
	}
	
	//public static HttpData post(String sUrl, Hashtable<String, String> params, ArrayList<File> files) {
	public boolean uploadAttachment2(String sid) {
        //HttpData ret = new HttpData();
		String ret = "";
        try {
                String boundary = "*****************************************";
                String newLine = "\r\n";
                int bytesAvailable;
                int bufferSize;
                int maxBufferSize = 4096;
                int bytesRead;

                URL url = new URL(Constants.ATTACH_URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setUseCaches(false);
                con.setRequestMethod("POST");
                con.setRequestProperty("Connection", "Keep-Alive");
                con.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
                DataOutputStream dos = new DataOutputStream(con.getOutputStream());

                //dos.writeChars(params);

                //upload files
                //for (int i=0; i<files.size(); i++) {
                        //Log.i("HREQ", i+"");
                String file = "/sdcard/.waggon/1.txt";
                        FileInputStream fis = new FileInputStream(file);
                        dos.writeBytes("--" + boundary + newLine);
                        dos.writeBytes("Content-Disposition: form-data; "
                        + "name=\"upload\";filename=\""
                        + file +"\"" + newLine + newLine);
                        bytesAvailable = fis.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        byte[] buffer = new byte[bufferSize];
                        bytesRead = fis.read(buffer, 0, bufferSize);
                        while (bytesRead > 0) {
                                dos.write(buffer, 0, bufferSize);
                                bytesAvailable = fis.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = fis.read(buffer, 0, bufferSize);
                        }
                        dos.writeBytes(newLine);
                        dos.writeBytes("--" + boundary + "--" + newLine);
                        fis.close();
                //}
                // Now write the data

                /*Enumeration keys = params.keys();
                String key, val;
                while (keys.hasMoreElements()) {
                        key = keys.nextElement().toString();
                        val = params.get(key);
                        dos.writeBytes("--" + boundary + newLine);
                        dos.writeBytes("Content-Disposition: form-data;name=""
                        + key+""" + newLine + newLine + val);
                        dos.writeBytes(newLine);
                        dos.writeBytes("--" + boundary + "--" + newLine);

                }*/
                dos.flush();

                BufferedReader rd = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                        ret += line + "\r\n";
                }
                Log.d("Waggon", ret);
                //get headers
                Map<String, List<String>> headers = con.getHeaderFields();
                Set<Entry<String, List<String>>> hKeys = headers.entrySet();
                for (Iterator<Entry<String, List<String>>> i = hKeys.iterator(); i.hasNext();) {
                        Entry<String, List<String>> m = i.next();

                        Log.w("Waggon", m.getKey() + "");
                        /*ret.headers.put(m.getKey(), m.getValue().toString());
                        if (m.getKey().equals("set-cookie"))
                        ret.cookies.put(m.getKey(), m.getValue().toString());*/
                }
                dos.close();
                rd.close();
        } catch (MalformedURLException me) {
        	Log.e("HREQ", "Exception: "+ me.toString());
        } catch (IOException ie) {
        	Log.e("HREQ", "Exception: "+ ie.toString());
        }
        //return ret;
        return true;
	}
}
