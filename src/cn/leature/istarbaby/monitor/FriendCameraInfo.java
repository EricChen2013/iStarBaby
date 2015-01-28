package cn.leature.istarbaby.monitor;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendCameraInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3801150051838392658L;
	String user_id = "";
	String user_name = "";
	String user_icon = "";
	String contact_id = "";
	String contact_name = "";
	String contact_icon = "";
	String device_id = "";
	String device_name = "";
	String device_account = "";
	String device_password = "";
	String permission = "";
	String contact_memo = "";

	public FriendCameraInfo(String user_id, String user_name, String user_icon,
			String contact_id, String contact_name, String contact_icon,
			String device_id, String device_name, String device_account,
			String device_password, String permission) {
		super();
		this.user_id = user_id;
		this.user_name = user_name;
		this.user_icon = user_icon;
		this.contact_id = contact_id;
		this.contact_name = contact_name;
		this.contact_icon = contact_icon;
		this.device_id = device_id;
		this.device_name = device_name;
		this.device_account = device_account;
		this.device_password = device_password;
		this.permission = permission;
		this.contact_memo = "";
	}

	public FriendCameraInfo(JSONObject jsonObject) {
		try {
			this.user_id = jsonObject.getString("user_id");
			this.user_name = jsonObject.getString("user_name");
			this.user_icon = jsonObject.getString("user_icon");
			this.contact_id = jsonObject.getString("contact_id");
			this.contact_name = jsonObject.getString("contact_name");
			this.contact_icon = jsonObject.getString("contact_icon");
			this.device_id = jsonObject.getString("device_id");
			this.device_name = jsonObject.getString("device_name");
			this.permission = jsonObject.getString("permission");
			this.device_account = jsonObject.getString("device_account");
			this.device_password = jsonObject.getString("device_password");
			this.contact_memo = jsonObject.getString("contact_memo");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getDevice_account() {
		return device_account;
	}

	public void setDevice_account(String device_account) {
		this.device_account = device_account;
	}

	public String getDevice_password() {
		return device_password;
	}

	public void setDevice_password(String device_password) {
		this.device_password = device_password;
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

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getContact_memo() {
		return contact_memo;
	}

	public void setContact_memo(String contact_memo) {
		this.contact_memo = contact_memo;
	}

}
