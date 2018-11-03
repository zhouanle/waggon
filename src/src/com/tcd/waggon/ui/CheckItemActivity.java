package com.tcd.waggon.ui;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlSerializer;

import com.tcd.waggon.Constants;
import com.tcd.waggon.R;
import com.tcd.waggon.WaggonApplication;
import com.tcd.waggon.data.CheckItem;
import com.tcd.waggon.data.Line;
import com.tcd.waggon.data.Session;
import com.tcd.waggon.data.Tower;
import com.tcd.waggon.data.WorkList;
import com.tcd.waggon.util.CheckItemBuilder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CheckItemActivity extends Activity {
	private Context mContext = null;
	private Button btn_complete = null;
	private Button btn_cancel = null;
	private ListView check_list = null;
	private WorkList workList = null;
	private Tower mTower = null;
	private List<Map<String, Object>> items = null;
	private boolean result = false;
	private CheckItem mItem = null;
	private String tower_id;
	private int lastPos = 0;
	
	private static final int MSG_ITEM_LOADED = 1;
	
	private Handler handler = new Handler() {  
        public void handleMessage(Message msg) {
        	if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Get Message " + msg.what);
        	
			switch (msg.what){  
				case MSG_ITEM_LOADED:
				    TextView title = (TextView) findViewById(R.id.top);
				    title.setText(mTower.getId() + " " + mTower.getAddr());
				    
					Map<String, Object> map;
					items.clear();
					
					/*for (int i = 0; i < type1.length; i++) {
						map = new HashMap<String, Object>();
						map.put("ITEM", type1[i]);
						items.add(map); 
					}*/
					//mTower.clearItems();
					items.clear();
					for (CheckItem item : mTower.getItems()) {
						map = new HashMap<String, Object>();
						map.put("ITEM", item.getId() + " - " + item.getItem() + " - " + item.getCriteria() + " (" + (item.isChecked() ? "Checked" : "Uncheck") + ")");
						items.add(map);
					}
					
					SimpleAdapter adapter = new SimpleAdapter(mContext, items,
							android.R.layout.simple_list_item_1, 
							new String[] { "ITEM" },   
							new int[] { android.R.id.text1 });
					check_list.setAdapter(adapter);
					check_list.setSelection(lastPos);
				    check_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				    	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			    		{
				    		//if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "arg0 = " + arg0 + ", arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3);
				    		
				    		mItem = getItem(arg2);
				    		lastPos = arg2;
				    		if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Select CheckItem [" + mItem + "]");

				    		LayoutInflater factory = LayoutInflater.from(mContext);
				            final View textEntryView = factory.inflate(R.layout.check_comments, null);
				    		new AlertDialog.Builder(CheckItemActivity.this)
				                .setSingleChoiceItems(R.array.choices, -1, new DialogInterface.OnClickListener() {
				                    public void onClick(DialogInterface dialog, int whichButton) {
				                    	if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Your choice [" + whichButton + "]");
				                    	//item_result.add(arg2, "CHOICE" + whichButton);
				                    	result = (whichButton == 0) ? true : false;
				                    }
				                })
				                .setView(textEntryView)
				                .setPositiveButton(R.string.btn_complete, new DialogInterface.OnClickListener() {
				                    public void onClick(DialogInterface dialog, int whichButton) {
							    		mItem.setResult(result);
							    		mItem.setChecked(true);
							    		
							    		Message message = Message.obtain();
							            message.what = MSG_ITEM_LOADED;
							            handler.sendMessage(message);
							            
							    		EditText comments = (EditText) textEntryView.findViewById(R.id.comments);
							    		mItem.setComments(comments.getText().toString().trim());
							    		if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Checked [" + mItem + "]");
							    		if (isAllChecked())
							    			btn_complete.setEnabled(true);
				                    }
				                })
				                .setNegativeButton(R.string.btn_takephoto, new DialogInterface.OnClickListener() {
				                    public void onClick(DialogInterface dialog, int whichButton) {
				                    	// TODO to take a photo
				                    }
				                })
				                .show();
			    		}
				    });
					break;
			}
        }
	};
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.check_tower);
	    
	    mContext = this;
	    
	    btn_complete = (Button) this.findViewById(R.id.btn_complete);
	    btn_complete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				save_result();
			}
        });
	    //btn_complete.setEnabled(true);
	    btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
	    btn_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				canceled();
			}
        });
	    check_list = (ListView) findViewById(R.id.check_listview);
	    check_list.setFocusableInTouchMode(true);
	    
	    items = new ArrayList<Map<String,Object>>(3);
	    
	    /*String tower_id = savedInstanceState.getString("com.tcd.waggon.TOWER");*/
	    //WaggonApplication app = WaggonApplication.getInstance();
	    tower_id = this.getIntent().getStringExtra(Constants.TOWER_NAME);
	    Log.d(Constants.LOG_TAG, "tower_id = " + tower_id);
	    
	    new Thread() {
	    	public void run() {
	    	    loadItems(tower_id);
	    	}
	    }.start();
    }
    
    public void onResume() {
    	super.onResume();
    }
    
    public void onPause() {  	
    	super.onPause();
    }
    
    private void canceled() {
    	this.setResult(Constants.CHECK_TOWER_CANCEL);
    	this.finish();
    }
    
    private void save_result() {
    	this.setResult(Constants.CHECK_TOWER_COMPLETE);
    	this.finish();
    }
    
    private CheckItem getItem(int i) {
    	return mTower.getItems().get(i);
    }
    
    private void loadItems(String tower_id) {
    	WaggonApplication app = WaggonApplication.getInstance();
    	workList = (WorkList) app.getData(Constants.WORK_LIST);
    	
    	for (Line line : workList.getLines()) {
			for (Tower tower : line.getTowers()) {
				if (tower.getId().equals(tower_id)) {
					mTower = tower;
					break;
				}
			}
    	}
    	
    	CheckItemBuilder.build(mContext, mTower.getType(), mTower.getItems());
    	if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Tower [" + mTower.toString() + "]");
    	
    	Message message = Message.obtain();
        message.what = MSG_ITEM_LOADED;
        handler.sendMessage(message);
    }
    
    private boolean isAllChecked() {
    	for (CheckItem item : mTower.getItems()) {
    		//if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "CheckItem [" + item + "]");
    		if (item.isChecked() == false)
    			return false;
    	}
    	
    	return true;
    }
}
