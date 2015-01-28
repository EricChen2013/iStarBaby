package cn.leature.istarbaby.domain;

import org.json.JSONException;
import org.json.JSONObject;

public class FriendGroupListInfo {

	String group_id;
	String group_name;
	String position;

	public FriendGroupListInfo(JSONObject jsonObject) {
		try {
			this.position = jsonObject.getString("position");
			this.group_id = jsonObject.getString("group_id");
			this.group_name = jsonObject.getString("group_name");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
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

	public FriendGroupListInfo(String group_id, String group_name, String position) {
		super();

		this.group_id = group_id;
		this.group_name = group_name;
		this.position = position;
	}

}
