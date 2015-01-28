/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: ChildAlbumInfo.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.domain
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-12 下午2:25:04
 * @version: V1.0  
 */
package cn.leature.istarbaby.domain;

/**
 * @ClassName: ChildAlbumInfo
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-12 下午2:25:04
 */

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public class ChildAlbumInfo implements Serializable {

	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 * @Description: TODO
	 */
	private static final long serialVersionUID = 1L;
	public String group_id;
	public String photo_url;
	public String event_name;
	public String event_id;
	public String daily_Id;

	public ChildAlbumInfo(String group_id, String photo_url, String event_name,
			String event_id, String daily_Id) {
		super();
		this.group_id = group_id;
		this.photo_url = photo_url;
		this.event_name = event_name;
		this.event_id = event_id;
		this.daily_Id = daily_Id;
	}

	public String getDaily_Id() {
		return daily_Id;
	}

	public void setDaily_Id(String daily_Id) {
		this.daily_Id = daily_Id;
	}

	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	public String getEvent_name() {
		return event_name;
	}

	public void setEvent_name(String event_name) {
		this.event_name = event_name;
	}

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	public ChildAlbumInfo(JSONObject jsonObject) {
		super();

		try {
			this.group_id = jsonObject.getString("group_id");
			this.event_name = jsonObject.getString("event_name");
			this.photo_url = jsonObject.getString("photo_url");
			this.event_id = jsonObject.getString("event_id");
			this.daily_Id = jsonObject.getString("daily_id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
