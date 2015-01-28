package cn.leature.istarbaby.domain;

import org.json.JSONException;
import org.json.JSONObject;

public class ChildGrowInfo {
	String daily_id;
	String user_id;
	String child_id;
	String birthday;
	String child_name;
	String nickname;
	String event_date;
	String height;
	String weight;
	String xiongwei;
	String touwei;

	public String getDaily_id() {
		return daily_id;
	}

	public void setDaily_id(String daily_id) {
		this.daily_id = daily_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getChild_id() {
		return child_id;
	}

	public void setChild_id(String child_id) {
		this.child_id = child_id;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getChild_name() {
		return child_name;
	}

	public void setChild_name(String child_name) {
		this.child_name = child_name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEvent_date() {
		return event_date;
	}

	public void setEvent_date(String event_date) {
		this.event_date = event_date;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getXiongwei() {
		return xiongwei;
	}

	public void setXiongwei(String xiongwei) {
		this.xiongwei = xiongwei;
	}

	public String getTouwei() {
		return touwei;
	}

	public void setTouwei(String touwei) {
		this.touwei = touwei;
	}

	public ChildGrowInfo(String daily_id, String user_id, String child_id,
			String birthday, String child_name, String nickname,
			String event_date, String height, String weight, String xiongwei,
			String touwei) {
		super();
		this.daily_id = daily_id;
		this.user_id = user_id;
		this.child_id = child_id;
		this.birthday = birthday;
		this.child_name = child_name;
		this.nickname = nickname;
		this.event_date = event_date;
		this.height = height;
		this.weight = weight;
		this.xiongwei = xiongwei;
		this.touwei = touwei;
	}

	public ChildGrowInfo(JSONObject jsonObject) {
		try {

			this.child_id = jsonObject.getString("child_id");
			this.user_id = jsonObject.getString("user_id");
			this.daily_id = jsonObject.getString("daily_id");
			this.event_date = jsonObject.getString("event_date");
			this.nickname = jsonObject.getString("nickname");
			this.birthday = jsonObject.getString("birthday");
			this.child_name = jsonObject.getString("child_name");
			this.height = jsonObject.getString("height");
			this.weight = jsonObject.getString("weight");
			this.xiongwei = jsonObject.getString("xiongwei");
			this.touwei = jsonObject.getString("touwei");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
