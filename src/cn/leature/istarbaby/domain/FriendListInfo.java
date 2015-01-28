package cn.leature.istarbaby.domain;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendListInfo {

	String user_id = "";
	String user_name = "";
	String user_icon = "";
	String contact_id = "";
	String contact_name = "";
	String contact_icon = "";
	String contact_gender = "";
	String group_id = "";
	String group_name = "";
	String contact_memo = "";

	public FriendListInfo() {
	}

	public FriendListInfo(JSONObject jsonObject) {
		try {
			this.user_id = jsonObject.getString("user_id");
			this.user_name = jsonObject.getString("user_name");
			this.user_icon = jsonObject.getString("user_icon");
			this.contact_id = jsonObject.getString("contact_id");
			this.contact_name = jsonObject.getString("contact_name");
			this.contact_icon = jsonObject.getString("contact_icon");
			this.contact_gender = jsonObject.getString("contact_gender");
			this.group_id = jsonObject.getString("group_id");
			this.group_name = jsonObject.getString("group_name");
			this.contact_memo = jsonObject.getString("contact_memo");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_icon() {
		return user_icon;
	}

	public void setUser_icon(String user_icon) {
		this.user_icon = user_icon;
	}

	public String getContact_id() {
		return contact_id;
	}

	public void setContact_id(String contact_id) {
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

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public String getContactMemo() {
		return contact_memo;
	}

	public void setContactMemo(String contact_memo) {
		this.contact_memo = contact_memo;
	}

	public FriendListInfo(String user_id, String user_name, String user_icon,
			String contact_id, String contact_name, String contact_icon,
			String contact_gender, String group_id, String group_name,
			String contact_memo) {
		super();
		this.user_id = user_id;
		this.user_name = user_name;
		this.user_icon = user_icon;
		this.contact_id = contact_id;
		this.contact_name = contact_name;
		this.contact_icon = contact_icon;
		this.contact_gender = contact_gender;
		this.group_id = group_id;
		this.group_name = group_name;
		this.contact_memo = contact_memo;
	}
}
