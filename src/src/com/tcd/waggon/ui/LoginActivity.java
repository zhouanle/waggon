package com.tcd.waggon.ui;

import com.tcd.waggon.Constants;
import com.tcd.waggon.R;
import com.tcd.waggon.WaggonApplication;
import com.tcd.waggon.data.Session;
import com.tcd.waggon.service.Connector;
import com.tcd.waggon.util.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private Context mContext = null;
	private EditText txt_user = null;
	private EditText txt_pwd = null;
	//private String mSessionID = "";
	private Session s = null;
	
	private ProgressDialog dlg = null;
	
	private static final int MSG_BEGIN_LOGIN = 1;
	private static final int MSG_LOGIN_SUCCEED = 2;
	private static final int MSG_LOGIN_FAIL = 3;
	private static final int MSG_LOGIN_TIMEOUT = 4;
	
	private Handler handler = new Handler() {  
        public void handleMessage(Message msg) {
        	if (Constants.IS_DEBUG) Log.d(Constants.LOG_TAG, "Get Message " + msg.what);

			switch (msg.what){  
				case MSG_BEGIN_LOGIN:
					dlg = new ProgressDialog(mContext);
			    	
			    	//dlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			    	dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			    	dlg.setMessage(mContext.getResources().getString(R.string.txt_login));
			    	dlg.setIndeterminate(false);
			    	dlg.setCancelable(false);
			    	dlg.setProgress(100);
			    	/*dlg.setButton(mContext.getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Message message = Message.obtain();
					        message.what = MSG_CANCEL_DOWNLOAD;
					        handler.sendMessage(message);
						}
			    	});*/
			    	
			    	dlg.show();
					break;
				case MSG_LOGIN_SUCCEED:
					if (dlg != null) dlg.dismiss();
					
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, MainActivity.class);
					mContext.startActivity(intent);
					LoginActivity.this.finish();

					break;
				case MSG_LOGIN_FAIL:
					if (dlg != null) dlg.dismiss();
					Toast.makeText(mContext, mContext.getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
					break;
				case MSG_LOGIN_TIMEOUT:
					if (dlg != null) dlg.dismiss();
					Toast.makeText(mContext, mContext.getResources().getString(R.string.login_timeout), Toast.LENGTH_SHORT).show();
					break;
			}
        }
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        
        mContext = this;
        Utils.initWaggonDir();
        Button btn_ok = (Button) this.findViewById(R.id.login_btn);
        Button btn_forget_pwd = (Button) this.findViewById(R.id.login_forget_pwd); 
        txt_user = (EditText) this.findViewById(R.id.login_user_edit);
        txt_pwd = (EditText) this.findViewById(R.id.login_pwd_edit);
        
        btn_ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (txt_user.getText().toString().equals("") || txt_pwd.getText().toString().equals("")) {
					Toast.makeText(mContext, mContext.getResources().getString(R.string.login_empty), Toast.LENGTH_SHORT).show();
				} else {
					login_verify();
					/*if () {
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, MainActivity.class);
						mContext.startActivity(intent);
						LoginActivity.this.finish();
					} else {
						Toast.makeText(mContext, mContext.getResources().getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
					}*/
				}
			}
        });
        
        btn_forget_pwd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.login_forget_pwd), Toast.LENGTH_SHORT).show();
			}
        });
    }
    
    private void login_verify() {
    	if (Utils.isConnected(mContext)) {
    		Log.i(Constants.LOG_TAG, "Connection found, will do online check.");
    		Message message = Message.obtain();
            message.what = MSG_BEGIN_LOGIN;
            handler.sendMessage(message);
            
            new Thread() {
            	public void run() {
            		String mSessionID = Connector.getInstance().login(txt_user.getText().toString().trim(), txt_pwd.getText().toString().trim());
            		Message message;
            		
            		if (Constants.LOGIN_FAILED.equals(mSessionID)) {
            			message = Message.obtain();
	                    message.what = MSG_LOGIN_FAIL;
	                    handler.sendMessage(message);
	        		} else if (Constants.LOGIN_TIMEOUT.equals(mSessionID)) {
	        			message = Message.obtain();
	                    message.what = MSG_LOGIN_TIMEOUT;
	                    handler.sendMessage(message);
	        		} else {
	    	    		initData(mSessionID);
	    	    		// save to local
	    	    		if (Utils.saveToLocal(s)) {
		            		message = Message.obtain();
		                    message.what = MSG_LOGIN_SUCCEED;
		                    handler.sendMessage(message);
	    	    		} else {
	    	    			message = Message.obtain();
		                    message.what = MSG_LOGIN_FAIL;
		                    handler.sendMessage(message);
	    	    		}
	        		}
            	}
            }.start();
            
    		//mSessionID = Connector.getInstance().loginFake(txt_user.getText().toString(), txt_pwd.getText().toString());
    		/*if (Constants.LOGIN_FAILED.equals(mSessionID)) {
    			return false;
    		} else {
	    		WaggonApplication app = WaggonApplication.getInstance();
	    		app.putData(Constants.SESSION_ID, mSessionID);
	    		app.putData(Constants.CHECK_FLAG, "NO");
	    		app.putData(Constants.DOWNLOAD_FLAG, "NO");
	    		app.putData(Constants.UPLOAD_FLAG, "NO");
	    		
	    		return true;
    		}*/
    	} else {
    		Log.i(Constants.LOG_TAG, "Connection not found, will do local check.");
    		Message message;
    		
    		// Do local check
    		if (Utils.localCheck(txt_user.getText().toString().trim(), txt_pwd.getText().toString().trim())) {
	    		initData(Constants.SESSION_LOCAL);
	    		
	    		message = Message.obtain();
	            message.what = MSG_LOGIN_SUCCEED;
	            handler.sendMessage(message);
    		} else {
    			message = Message.obtain();
	            message.what = MSG_LOGIN_FAIL;
	            handler.sendMessage(message);
    		}
    	}
    }
    
    private void initData(String sid) {
    	WaggonApplication app = WaggonApplication.getInstance();
    	s = new Session();
    	s.setSessionID(sid);
    	s.setUser(txt_user.getText().toString().trim());
    	s.setPasswd(txt_pwd.getText().toString().trim());
    	app.putData(Constants.SESSION, s);
    	Utils.initUserDir(s.getUser());

		/*app.putData(Constants.SESSION_ID, sid);
		app.putData(Constants.CHECK_FLAG, "NO");
		app.putData(Constants.DOWNLOAD_FLAG, "NO");
		app.putData(Constants.UPLOAD_FLAG, "NO");*/
    }
}