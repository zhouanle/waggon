package com.tcd.waggon.data;

public class CheckItem {
	private int type; // tower type, different tower type has different check items
	private int id;
	private String item;
	private String criteria;
	private boolean result; // true means pass, false means failed
	private boolean checked; 
	private String comments;
	private String attachment;
	
	/*public CheckItem(int type, int id, String item, String criteria,
			boolean result, String comments, String attachment) {
		super();
		this.type = type;
		this.id = id;
		this.item = item;
		this.criteria = criteria;
		this.result = result;
		this.comments = comments;
		this.attachment = attachment;
	}*/
	
	public CheckItem(int type) {
		this.type = type;
		this.result = false;
		this.checked = false;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getCriteria() {
		return criteria;
	}

	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		return "CheckItem [type=" + type + ", id=" + id + ", item=" + item
				+ ", criteria=" + criteria + ", result=" + result
				+ ", checked=" + checked + ", comments=" + comments
				+ ", attachment=" + attachment + "]";
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
