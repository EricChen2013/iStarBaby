package cn.leature.istarbaby.domain;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendDetailInfo {

	String userId = "";
	String password = "";
	String gender = "";
	String name = "";
	String icon = "";
	String birthday = "";
	String mobile = "";
	String tel = "";
	String email = "";
	String address = "";

	String group_id = "";
	String group_name = "";
	String contact_memo = "";

	public String getUserId() {
		return userId;
	}

	public void setUserId(String user_id) {
		this.userId = user_id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getGroupId() {
		return group_id;
	}

	public void setGroupId(String group_id) {
		this.group_id = group_id;
	}

	public String getGroupName() {
		return group_name;
	}

	public void setGroupName(String group_name) {
		this.group_name = group_name;
	}

	public String getContactMemo() {
		return contact_memo;
	}

	public void setContactMemo(String contact_memo) {
		this.contact_memo = contact_memo;
	}

	public FriendDetailInfo(JSONObject jsonObject) {
		try {
			this.userId = jsonObject.getString("user_id");
			this.password = jsonObject.getString("password");
			this.gender = jsonObject.getString("gender");
			this.name = jsonObject.getString("name");
			this.icon = jsonObject.getString("icon");
			this.birthday = jsonObject.getString("birthday");
			this.mobile = jsonObject.getString("mobile");
			this.tel = jsonObject.getString("tel");
			this.email = jsonObject.getString("email");
			this.address = jsonObject.getString("address");

			this.group_id = jsonObject.getString("group_id");
			this.group_name = jsonObject.getString("group_name");
			this.contact_memo = jsonObject.getString("contact_memo");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
