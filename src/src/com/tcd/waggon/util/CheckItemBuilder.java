package com.tcd.waggon.util;

import java.io.DataInputStream;
import com.tcd.waggon.R;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

import com.tcd.waggon.Constants;
import com.tcd.waggon.data.CheckItem;
import com.tcd.waggon.data.Tower;

public class CheckItemBuilder {
	public static final void build(Context c, int type, ArrayList<CheckItem> items) {
		//String file_name = "tower" + type + ".xml";
		//String file_name = "t" + type + ".txt";
		//DataInputStream in;
		String raw = "";
		String[] line;
		String[] s;
		CheckItem item;
		
		switch (type) {
			case Tower.TOWER_TYPE1:
				// load assets
				raw = c.getResources().getString(R.string.tower1);
				break;
			case Tower.TOWER_TYPE2:
				raw = c.getResources().getString(R.string.tower2);
				break;
			case Tower.TOWER_TYPE3:
				raw = c.getResources().getString(R.string.tower3);
				break;
			case Tower.TOWER_TYPE4:
				raw = c.getResources().getString(R.string.tower4);
				break;
		}
		
		//try {
			//in = new DataInputStream(c.getAssets().open(file_name));
			//in = new DataInputStream(new FileInputStream("/sdcard/.waggon/tower1.txt"));

			//while ((line = in.readLine()) != null) {
				//Log.d(Constants.LOG_TAG, "=> " + raw);
				line = raw.split(" ");
				for (int i = 0; i < line.length; i++) {
					s = line[i].split(":");
					item = new CheckItem(type);
					item.setId(Integer.parseInt(s[0]));
					item.setItem(s[1]);
					item.setCriteria(s[2]);
					/*item = new CheckItem(type);
					item.setItem(line);*/
					items.add(item);
					if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Add CheckItem[" + item + "]");
				}
			//}
			//in.close();
		/*} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
}
