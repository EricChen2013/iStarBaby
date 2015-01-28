package cn.leature.istarbaby.domain;


public class FriendInfo {
	String contact_id = "";
	String contact_name = "";
	String contact_icon = "";
	String contact_gender = "";
	String contact_memo = "";

	public FriendInfo() {
		this.contact_id = "";
		this.contact_name = "";
		this.contact_icon = "";
		this.contact_gender = "";
		this.contact_memo = "";
	}

	public FriendInfo(FriendListInfo friendListInfo) {
		this.contact_id = friendListInfo.getContact_id();
		this.contact_name = friendListInfo.getContact_name();
		this.contact_icon = friendListInfo.getContact_icon();
		this.contact_gender = friendListInfo.getContact_gender();
		this.contact_memo = friendListInfo.getContactMemo();
	}

	public String getContactId() {
		return contact_id;
	}

	public void setContactId(String contact_id) {
		this.contact_id = contact_id;
	}

	public String getContact_name() {
		return contact_name;
	}

	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}

	public String getContact_icon() {
		return contact_icon;
	}

	public void setContact_icon(String contact_icon) {
		this.contact_icon = contact_icon;
	}

	public String getContact_gender() {
		return contact_gender;
	}

	public void setContact_gender(String contact_gender) {
		this.contact_gender = contact_gender;
	}

	public String getContactMemo() {
		return contact_memo;
	}

	public void setContactMemo(String contact_memo) {
		this.contact_memo = contact_memo;
	}

}
