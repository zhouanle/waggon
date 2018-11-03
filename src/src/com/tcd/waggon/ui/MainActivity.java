package com.tcd.waggon.ui;

import com.tcd.waggon.Constants;
import com.tcd.waggon.R;
import com.tcd.waggon.WaggonApplication;
import com.tcd.waggon.data.Session;
import com.tcd.waggon.data.WorkList;
import com.tcd.waggon.service.Downloader;
import com.tcd.waggon.service.Uploader;
import com.tcd.waggon.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnMenuItemClickListener {
	private Context mContext = null;
	private Button btn_download = null;
	private Button btn_check = null;
	private Button btn_upload = null;
	private ProgressDialog dlg_down = null;
	private ProgressDialog dlg_up = null;
	private String mSessionID = "";
	private WorkList workList = null;
	private Session session = null;
	
	private static final String KEY_PROGRESS = "PROGRESS";
	
	private static final int MSG_BEGIN_DOWNLOAD = 1;
	private static final int MSG_CANCEL_DOWNLOAD = 2;
	private static final int MSG_DOWNLOAD_COMPLETE = 3;
	private static final int MSG_DOWNLOAD_PROGRESS = 4;
	private static final int MSG_BEGIN_UPLOAD = 5;
	private static final int MSG_CANCEL_UPLOAD = 6;
	private static final int MSG_UPLOAD_COMPLETE = 7;
	private static final int MSG_UPLOAD_PROGRESS = 8;
	private static final int MSG_NO_WORKLIST = 9;
	
	private static final int MENU_SETTING = 1;
	private static final int MENU_EXIT = 2;
	
	private Handler handler = new Handler() {  
        public void handleMessage(Message msg) {
        	Bundle bundle = null;
        	if (Constants.IS_DEBUG) {
        		Log.d(Constants.LOG_TAG, "Get Message " + msg.what);
        	}
        	
			switch (msg.what){  
				case MSG_BEGIN_DOWNLOAD:
					dlg_down = new ProgressDialog(mContext);
			    	
			    	//dlg_down.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			    	dlg_down.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			    	dlg_down.setMessage(mContext.getResources().getString(R.string.txt_download));
			    	dlg_down.setIndeterminate(false);
			    	dlg_down.setCancelable(false);
			    	//dlg_down.setProgress(100);
			    	/*dlg_down.setButton(mContext.getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Message message = Message.obtain();
					        message.what = MSG_CANCEL_DOWNLOAD;
					        handler.sendMessage(message);
						}
			    	});*/
			    	
			    	dlg_down.show();
					break;
				case MSG_CANCEL_DOWNLOAD:
					// no need now
					dlg_down.cancel();
					break;
				case MSG_DOWNLOAD_COMPLETE:
					dlg_down.dismiss();
					
					new AlertDialog.Builder(MainActivity.this)
		        		//.setTitle(R.string.txt_download)
		        		.setMessage(R.string.txt_downloaded)
		        		.setPositiveButton(R.string.btn_complete, new DialogInterface.OnClickListener() {
		        			public void onClick(DialogInterface dialog, int whichButton) {
		        				if (workList != null) {
			        				btn_check.setEnabled(true);
			        				btn_check.setBackgroundResource(R.drawable.check);
			        				WaggonApplication app = WaggonApplication.getInstance();
			        				session.setDownloaded(true);
			        				//app.putData(Constants.DOWNLOAD_FLAG, "YES");
			        				app.putData(Constants.SESSION, session);
		        				}
		        			}
		        		})
		            .show();
 
					break;
				case MSG_DOWNLOAD_PROGRESS:
	        		bundle = msg.getData();
					dlg_down.setProgress(bundle.getInt(KEY_PROGRESS));
					break;
				case MSG_BEGIN_UPLOAD:
					dlg_up = new ProgressDialog(mContext);
			    	
					//dlg_up.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			    	dlg_up.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					dlg_up.setMessage(mContext.getResources().getString(R.string.txt_upload));
					dlg_up.setIndeterminate(false);
					dlg_up.setCancelable(false);
					//dlg_up.setProgress(100);
					/*dlg_up.setButton(mContext.getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Message message = Message.obtain();
					        message.what = MSG_CANCEL_UPLOAD;
					        handler.sendMessage(message);
						}
			    	});*/
			    	
					dlg_up.show();
					break;
				case MSG_CANCEL_UPLOAD:
					// no need now
					dlg_up.cancel();
					break;
				case MSG_UPLOAD_COMPLETE:
					dlg_up.dismiss();
					
					new AlertDialog.Builder(MainActivity.this)
		        		//.setTitle(R.string.txt_download)
		        		.setMessage(R.string.txt_uploaded)
		        		.setPositiveButton(R.string.btn_complete, new DialogInterface.OnClickListener() {
		        			public void onClick(DialogInterface dialog, int whichButton) {
		        				WaggonApplication app = WaggonApplication.getInstance();
		        				session.setUploaded(true);
		        				//app.putData(Constants.UPLOAD_FLAG, "YES");
		        				app.putData(Constants.SESSION, session);
		        			}
		        		})
		            .show();
					break;
				case MSG_UPLOAD_PROGRESS:
	        		bundle = msg.getData();
					dlg_up.setProgress(bundle.getInt(KEY_PROGRESS));
					break;
				case MSG_NO_WORKLIST:
					dlg_up.dismiss();
					
					new AlertDialog.Builder(MainActivity.this)
		        		//.setTitle(R.string.txt_download)
		        		.setMessage(R.string.txt_no_worklist)
		        		.setPositiveButton(R.string.btn_complete, new DialogInterface.OnClickListener() {
		        			public void onClick(DialogInterface dialog, int whichButton) {
		        			}
		        		})
		            .show();
					break;
			}
		}
	};
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        mContext = this;
        
        WaggonApplication app = WaggonApplication.getInstance();
        session = (Session) app.getData(Constants.SESSION);
        if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Session [" + session + "]");
		//mSessionID = (String) app.getData(Constants.SESSION_ID);
        mSessionID = session.getSessionID();
		//if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Session ID = " + mSessionID);
		
        btn_download = (Button) this.findViewById(R.id.btn_download);
        btn_check = (Button) this.findViewById(R.id.btn_check);
        btn_upload = (Button) this.findViewById(R.id.btn_upload);

		if (Constants.SESSION_LOCAL.equals(mSessionID)) {
			btn_download.setEnabled(false);
			btn_download.setBackgroundResource(R.drawable.un_down);
			String xml = Constants.LOCAL_PATH + session.getUser() + "/" + Constants.WORKLIST_XML;
			if (Utils.hasLocalWorkList(xml)) {
				btn_check.setEnabled(true);
				btn_check.setBackgroundResource(R.drawable.check);
			}
		}
		
        btn_download.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				download_worklist();
			}
        });
        
        btn_check.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, CheckListActivity.class);
				mContext.startActivity(intent);
			}
        });
        
        btn_upload.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				upload_worklist();
			}
        });
    }
    
    public void onResume() {
    	super.onResume();
    	//Log.d(Constants.LOG_TAG, "MainActivity onResume");
    	/*WaggonApplication app = WaggonApplication.getInsltance();
		String checked = (String) app.getData(Constants.CHECK_FLAG);
		String downloaded = (String) app.getData(Constants.DOWNLOAD_FLAG);*/
    	
		if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Check Flag = " + session.isChecked() + ", Download Flag = " + session.isUploaded());
		
		//if (session.isChecked()) {
			btn_upload.setEnabled(true);
			btn_upload.setBackgroundResource(R.drawable.up);
		//}
		
		/*if (session.isUploaded())
			btn_check.setEnabled(true);*/
    }
    
    public void onPause() {
    	//Log.d(Constants.LOG_TAG, "MainActivity onPause");
    	super.onPause();
    }
    
    private void download_worklist() {
    	Message message = Message.obtain();
        message.what = MSG_BEGIN_DOWNLOAD;
        handler.sendMessage(message);
        
        new Thread() {  
			@Override  
			public void run() {
				/*				try {
					Message message = Message.obtain();
				    message.what = MSG_DOWNLOAD_PROGRESS;
				    Bundle data = new Bundle();
				    data.putInt(KEY_PROGRESS, 50);
				    message.setData(data);
				    handler.sendMessage(message);*/
				Message message;
				
				    WaggonApplication app = WaggonApplication.getInstance();
				    workList = Downloader.getInstance().downloadWorkList(mSessionID);
				    if (workList != null) {
					    if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "WorkList = " + workList);
					    app.putData(Constants.WORK_LIST, workList);
					    		
					    message = Message.obtain();
						message.what = MSG_DOWNLOAD_COMPLETE;
						handler.sendMessage(message);
				    } else {
				    	Log.e(Constants.LOG_TAG, "No worklist found");

				    	message = Message.obtain();
						message.what = MSG_NO_WORKLIST;
						handler.sendMessage(message);
				    }
					
/*					Thread.sleep(1000);
					
					message = Message.obtain();
				    message.what = MSG_DOWNLOAD_PROGRESS;
				    data = new Bundle();
				    data.putInt(KEY_PROGRESS, 100);
				    message.setData(data);
				    handler.sendMessage(message);
				    
				    Thread.sleep(1000);*/
				    
				   
				/*    
				} catch (InterruptedException ex) {
					
				}*/
			}
		}.start();
    }
    
    private void upload_worklist() {
    	Message message = Message.obtain();
        message.what = MSG_BEGIN_UPLOAD;
        handler.sendMessage(message);
        
        new Thread() {  
			@Override  
			public void run() {
				//try {
					/*Message message = Message.obtain();
				    message.what = MSG_UPLOAD_PROGRESS;
				    Bundle data = new Bundle();
				    data.putInt(KEY_PROGRESS, 50);
				    message.setData(data);
				    handler.sendMessage(message);
				    
					Thread.sleep(1000);
					
					message = Message.obtain();
				    message.what = MSG_UPLOAD_PROGRESS;
				    data = new Bundle();
				    data.putInt(KEY_PROGRESS, 100);
				    message.setData(data);
				    handler.sendMessage(message);
				    
				    Thread.sleep(1000);*/
				Uploader.getInstance().uploadResult(mSessionID);
				Uploader.getInstance().uploadAttachment2(mSessionID);
				
				Message message = Message.obtain();
				message.what = MSG_UPLOAD_COMPLETE;
				handler.sendMessage(message);
				    
				/*} catch (InterruptedException ex) {
					
				}*/
			}
		}.start();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		///MenuItem item = menu.add(0, MENU_SETTING, Menu.NONE, mContext.getResources().getString(R.string.menu_setting));
		MenuItem item = menu.add(0, MENU_SETTING, Menu.NONE, "");
		item.setOnMenuItemClickListener(this);
		item.setIcon(R.drawable.menu_setting);
		
		//item = menu.add(0, MENU_EXIT, Menu.NONE, mContext.getResources().getString(R.string.menu_exit));
		item = menu.add(0, MENU_EXIT, Menu.NONE, "");
		item.setOnMenuItemClickListener(this);
		item.setIcon(R.drawable.menu_exit);
		
		return true;
	}

    public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
			case MENU_SETTING:
				return true;
			case MENU_EXIT:
				this.finish();
				return true;
			default:
				return true;
		}
	}
}
