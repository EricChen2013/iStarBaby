package cn.leature.istarbaby.domain;

import org.json.JSONException;
import org.json.JSONObject;

public class ChildAlbumUpdateInfo {
	String event_name;
	String photo_url;
	String event_date;

	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	public String getEvent_date() {
		return event_date;
	}

	public void setEvent_date(String event_date) {
		this.event_date = event_date;
	}

	public ChildAlbumUpdateInfo(String event_name, String photo_url,
			String event_date) {
		super();
		this.event_name = event_name;
		this.photo_url = photo_url;
		this.event_date = event_date;
	}

	public ChildAlbumUpdateInfo(JSONObject jsonObject) {
		try {
			this.event_name = jsonObject.getString("event_name");
			this.event_date = jsonObject.getString("event_date");
			this.photo_url = jsonObject.getString("photo_url");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
