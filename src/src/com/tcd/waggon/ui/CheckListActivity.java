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
import com.tcd.waggon.service.Downloader;
import com.tcd.waggon.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CheckListActivity extends Activity implements LocationListener {
	private Context mContext = null;
	private Button btn_complete = null;
	private ListView check_list = null;
	private List<Map<String, Object>> towerList = null;
	private WorkList workList = null;
	// Next check point
	//private Location nextPoint = null;
	//private Location checkedPoint = null;
	private WaggonApplication app = null; 
	private LocationManager locMgr = null;
	private Session session = null;
	//private PendingIntent mPending = null;
	
	// if near the tower, can check, otherwise can't
	//private boolean canCheck = false;
	//private Tower t = null; // current tower
	private Tower mTower = null;
	
	// if add proximity alter, need remove when quit
	//private boolean hasProximity = false;
	
	private static final int MSG_LIST_LOADED = 1;
	private static final int MSG_CANNOT_CHECK = 2;
	
	private Handler handler = new Handler() {  
        public void handleMessage(Message msg) {
        	if (Constants.IS_DEBUG) {
        		Log.d(Constants.LOG_TAG, "Get Message " + msg.what);
        	}
        	
			switch (msg.what){  
				case MSG_LIST_LOADED:
			    	Map<String, Object> map;
					towerList.clear();
					
					for (Line line : workList.getLines()) {
						for (Tower tower : line.getTowers()) {
							map = new HashMap<String, Object>();
						    map.put("TOWER", tower.getId() + " - " + tower.getAddr() + " (" + 
						    		(tower.isChecked() ? mContext.getResources().getString(R.string.check_status_done) : mContext.getResources().getString(R.string.check_status_none))
						    		+ ", " + (tower.isNear() ? mContext.getResources().getString(R.string.near_yes) : mContext.getResources().getString(R.string.near_no))
						    		+ ", " + tower.getDist() + "m)");
						    towerList.add(map);
						    //moveToNext(tower);
						}
					}
					
					/*if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Add ProximityAlert for [" + nextPoint + "]");
    				locMgr.addProximityAlert(nextPoint.getLatitude(), nextPoint.getLongitude(), Constants.RADIUS, -1, mPending);
					hasProximity = true;*/
					
					SimpleAdapter adapter = new SimpleAdapter(mContext, towerList,
							android.R.layout.simple_list_item_1, 
							new String[] { "TOWER" },   
							new int[] { android.R.id.text1 });
					check_list.setAdapter(adapter);
				    check_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){ 
			    		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
			    		{
			    			//if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "arg0 = " + arg0 + ", arg1 = " + arg1 + ", arg2 = " + arg2 + ", arg3 = " + arg3);
			    		
			    			//if (canCheck) {
				    			TextView tmp = (TextView) arg1;
				    			if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Check Tower [" + tmp.getText().toString() + "]");
				    			//Tower t = getTower(tmp.getText().toString());
				    			Tower t = getTower(arg2);
				    			
				    			if (t.isNear()) {
				    				mTower = t;
				    				/*Intent intent = new Intent();
									intent.setClass(CheckListActivity.this, CheckItemActivity.class);
									intent.putExtra(Constants.TOWER_NAME, t.getAddr());
									//app.putData(Constants.TOWER_NAME, t.getAddr());
									mContext.startActivity(intent);*/
				    				launchCheck(t);
				    			} else {
				    				Message message = Message.obtain();
				    		        message.what = MSG_CANNOT_CHECK;
				    		        handler.sendMessage(message);
				    		        return;
				    			}
				    			/*if (t.getLat() != nextPoint.getLatitude() || t.getLon() != nextPoint.getLongitude()) {
				    				// Not near current tower
				    				Message message = Message.obtain();
				    		        message.what = MSG_CANNOT_CHECK;
				    		        handler.sendMessage(message);
				    		        return;
				    			}
				    			
				    			Intent intent = new Intent();
								intent.setClass(CheckListActivity.this, CheckItemActivity.class);
								intent.putExtra(Constants.TOWER_NAME, t.getAddr());
								//app.putData(Constants.TOWER_NAME, t.getAddr());
								mContext.startActivity(intent);*/
			    			/*} else {
			    				Message message = Message.obtain();
			    		        message.what = MSG_CANNOT_CHECK;
			    		        handler.sendMessage(message);
			    			}*/
			    		}
				    });
					break;
				case MSG_CANNOT_CHECK:
					//Log.d("Waggon", "Can't check, because " + mContext.getResources().getString(R.string.txt_cannot_check));
    				//Toast.makeText(mContext, mContext.getResources().getString(R.string.txt_cannot_check), Toast.LENGTH_SHORT);
    				new AlertDialog.Builder(CheckListActivity.this)
    				//.setTitle(R.string.txt_download)
    				.setMessage(R.string.txt_cannot_check)
    				.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
    					public void onClick(DialogInterface dialog, int whichButton) {
    					}
    				})
    				.show();
					break;
			}
        }
	};
	
	private void launchCheck(Tower t) {
		Intent intent = new Intent();
		intent.setClass(CheckListActivity.this, CheckItemActivity.class);
		intent.putExtra(Constants.TOWER_NAME, t.getId());
		//intent.putExtra(Constants.TOWER_NAME, t.getAddr());
		//app.putData(Constants.TOWER_NAME, t.getAddr());
		//mContext.startActivity(intent);
		startActivityForResult(intent, Constants.CHECK_TOWER_ACTION);
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.check);
	    
	    mContext = this;

	    btn_complete = (Button) this.findViewById(R.id.btn_complete);
	    btn_complete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		    	save2xml();		        
				//WaggonApplication app = WaggonApplication.getInstance();
				session.setChecked(true);
				//app.putData(Constants.CHECK_FLAG, "YES");
				app.putData(Constants.SESSION, session);
				CheckListActivity.this.finish();
			}
        });
	    
	    check_list = (ListView)findViewById(R.id.check_listview);
	    check_list.setFocusableInTouchMode(true);
	    
	    towerList = new ArrayList<Map<String,Object>>(3);
	    
	    locMgr = (LocationManager) this.getSystemService(LOCATION_SERVICE);
	    //mPending = PendingIntent.getBroadcast(mContext, 0, new Intent(this, ProximityReceiver.class), PendingIntent.FLAG_CANCEL_CURRENT);
	    //mPending = PendingIntent.getBroadcast(mContext, -1, new Intent("com.tcd.waggon.ProximityAlter"), 0);
		//filter = new IntentFilter("com.tcd.waggon.ProximityAlter");
    }
    
    private void save2xml() {
    	String user = session.getUser();
    	
    	String xml = Constants.LOCAL_PATH + user + "/" + Constants.WORKLIST_RESULT_XML;
    	Log.i(Constants.LOG_TAG, "Save result to " + xml);
    	
    	XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        
        try {
        	serializer.setOutput(writer);
        	serializer.startDocument("UTF-8",true);
        	serializer.startTag("","WorkList");
        	serializer.attribute("", "id", workList.getId());

        	for (Line line : workList.getLines()) {
        		serializer.startTag("","Line");
        		serializer.attribute("", "id", line.getId());
        		
    			for (Tower tower : line.getTowers()) {
    				serializer.startTag("","Tower");
    				serializer.attribute("", "id", tower.getId()); 
    				
    				for (CheckItem item : tower.getItems()) {
    					serializer.startTag("","Item");
    					serializer.attribute("", "id", item.getId() + "");
    					serializer.attribute("", "result", item.isResult() ? "pass" : "fail");
    					if (item.getComments() != null)
    						serializer.attribute("", "reason", item.getComments());
    					if (item.getAttachment() != null)
    						serializer.attribute("", "attachment", item.getAttachment());
    					serializer.endTag("","Item");
    				}
    				
    				serializer.endTag("","Tower");
    			}
    			
    			serializer.endTag("","Line");
        	}
        	
        	serializer.endTag("","WorkList");	
        	serializer.endDocument();
        	
        	BufferedWriter fWriter = null;
    		FileOutputStream writerStream = null;
    		try {
    			writerStream = new FileOutputStream(xml);
    			fWriter = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
    			fWriter.write(writer.toString());
    			fWriter.close();
    			writerStream.close();
    		} catch (FileNotFoundException e2) {
    			Log.e(Constants.LOG_TAG, e2.toString());
    		} catch (UnsupportedEncodingException e1) {
    			Log.e(Constants.LOG_TAG, e1.toString());
    		} catch (IOException e) {
    			Log.e(Constants.LOG_TAG, e.toString());
    		}
        } catch (IOException e) {
        	Log.e(Constants.LOG_TAG, e.toString());
        }
    }
    
    public void onResume() {
    	super.onResume();
	    
    	locMgr.requestLocationUpdates(Constants.PROVIDER, Constants.INTERVAL, 0, this);

    	app = WaggonApplication.getInstance();
    	session = (Session) app.getData(Constants.SESSION);
		//String checked = (String) app.getData(Constants.CHECK_FLAG);
		if (session.isChecked())
			btn_complete.setEnabled(true);
		
    	new Thread() {
	    	@Override
	    	public void run() {
	    	    loadWorkList();
	    	}
	    }.start();
		
		//mContext.registerReceiver(pr, filter);
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "onActivityResult requestCode = " + requestCode + ", resultCode = " + resultCode);
    	if (requestCode == Constants.CHECK_TOWER_ACTION) {
    		if (resultCode == Constants.CHECK_TOWER_COMPLETE) {
    			if (mTower != null) {
    				mTower.setChecked(true);
    				/*checkedPoint = nextPoint;
    				nextPoint = null;
    				canCheck = false;*/
    				/*if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Remove ProximityAlert for [" + checkedPoint + "]");
    				locMgr.removeProximityAlert(mPending);
    				nextPoint = null;
    				hasProximity = false;*/
    				
    				if (isAllChecked())
    					btn_complete.setEnabled(true);
    				/*else {
    					if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Add ProximityAlert for [" + nextPoint + "]");
    					locMgr.addProximityAlert(nextPoint.getLatitude(), nextPoint.getLongitude(), Constants.RADIUS, -1, mPending);
    					hasProximity = true;
    				}*/
    			} else {
    				Log.e(Constants.LOG_TAG, "No current tower.");
    			}
    		} else {
    			Log.i(Constants.LOG_TAG, "Check tower canceled");
    		}
    	}
    }
    
    public void onPause() {
    	locMgr.removeUpdates(this);
    	
    	/*if (hasProximity) {
    		locMgr.removeProximityAlert(mPending);
    		if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Remove ProximityAlert for [" + nextPoint + "]");
    	}*/
    	
    	//mContext.unregisterReceiver(pr);
    	
    	super.onPause();
    }
    
/*    public void onBackPressed() {
    	// Do nothing, prevent press backkey, must click finish button
    }*/
    
    private void loadWorkList() {
    	/*app = WaggonApplication.getInstance();
    	session = (Session) app.getData(Constants.SESSION);*/
		//String mSessionID = (String) app.getData(Constants.SESSION_ID);
		if (Constants.SESSION_LOCAL.equals(session.getSessionID())) {
			// Do locally
	    	workList = Downloader.getInstance().downloadWorkListFake(session.getSessionID());
	    	app.putData(Constants.WORK_LIST, workList);
		} else
			workList = (WorkList) app.getData(Constants.WORK_LIST);
    	
    	Message message = Message.obtain();
        message.what = MSG_LIST_LOADED;
        handler.sendMessage(message);
    }
    
/*    private Tower getTower(String addr) {
    	for (Line line : workList.getLines()) {
			for (Tower tower : line.getTowers()) {
				if (tower.getAddr().equals(addr))
					return tower;
			}
    	}
    	
    	return null;
    }*/
    
    private Tower getTower(int pos) {
    	return workList.getLines().get(0).getTowers().get(pos);
    }
    
    private boolean isAllChecked() {
    	for (Line line : workList.getLines()) {
			for (Tower tower : line.getTowers()) {
				if (tower.isChecked() == false) {
					//moveToNext(tower);
					return false;
				}
			}
    	}
    	
    	return true;
    }

/*    private void moveToNext(Tower t) {
    	// Move to next check point
		if (nextPoint == null) {
	    	nextPoint = new Location(Constants.PROVIDER);
	    	nextPoint.setLatitude(t.getLat());
	    	nextPoint.setLongitude(t.getLon());
	    	
	    	if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Next check point is [" + nextPoint + "]");
	    }
    }*/
    
	public void onLocationChanged(Location location) {
		/*if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Location is [" + location + "], nextPoint is [" + nextPoint + "]");
		//locMgr.addProximityAlert(location.getLatitude(), location.getLongitude(), Constants.RADIUS, -1, mPending);
		
		double dist = Utils.calcDistance(nextPoint, location);
		if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Distance = " + dist);
		
		if (dist <= Constants.RADIUS) {
			Log.i(Constants.LOG_TAG, "Near the tower at " + nextPoint);
			canCheck = true;
		} else {
			canCheck = false;
		}*/
		
		if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Location is [" + location + "]");
		double dist;
		Location l;
		
		for (Line line : workList.getLines()) {
			for (Tower tower : line.getTowers()) {
				l = new Location(Constants.PROVIDER);
				l.setLatitude(tower.getLat());
				l.setLongitude(tower.getLon());
				
				dist = Utils.calcDistance(l, location);
				if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, dist + " meters to [= " + l + "] is " + dist);
				tower.setDist(dist);
				if (dist <= Constants.RADIUS) {
					tower.setNear(true);
				} else
					tower.setNear(false);
				
				
				Message message = Message.obtain();
		        message.what = MSG_LIST_LOADED;
		        handler.sendMessage(message);
			}
    	}
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onProviderEnabled(String provider) {
		if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "GPS on");
	}

	public void onProviderDisabled(String provider) {
		new AlertDialog.Builder(CheckListActivity.this)
			//.setTitle(R.string.txt_download)
			.setMessage(R.string.txt_gps_disabled)
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Intent fireAlarm = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    fireAlarm.addCategory(Intent.CATEGORY_DEFAULT);
                    startActivity(fireAlarm);
				}
			})
			.show();
	}
	
	/*private class ProximityReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("ProximityReceiver", "Action = " + intent.getAction());
			//Bundle bundle = intent.getExtras().getBoolean(KEY_PROXIMITY_ENTERING);
			if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "KEY_PROXIMITY_ENTERING = " + 
					intent.getExtras().getBoolean(LocationManager.KEY_PROXIMITY_ENTERING));
		}
	}
	
	private ProximityReceiver pr = new ProximityReceiver();
	private IntentFilter filter;*/
}
