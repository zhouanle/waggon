package com.tcd.waggon.data;

public class Session {
	private String sessionID;
	private boolean checked;
	private boolean downloaded;
	private boolean uploaded;
	private String user;
	private String passwd;
	
	public Session() {
		this.sessionID = "";
		this.checked = false;
		this.downloaded = false;
		this.uploaded = false;
		this.user = "";
		this.passwd = "";
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isDownloaded() {
		return downloaded;
	}

	public void setDownloaded(boolean downloaded) {
		this.downloaded = downloaded;
	}

	public boolean isUploaded() {
		return uploaded;
	}

	public void setUploaded(boolean uploaded) {
		this.uploaded = uploaded;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	@Override
	public String toString() {
		return "Session [sessionID=" + sessionID + ", checked=" + checked
				+ ", downloaded=" + downloaded + ", uploaded=" + uploaded
				+ ", user=" + user + ", passwd=" + passwd + "]";
	}
}
