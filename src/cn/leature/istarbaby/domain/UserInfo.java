/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: UserInfo.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.domain
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 上午11:12:29
 * @version: V1.0  
 */
package cn.leature.istarbaby.domain;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName: UserInfo
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 上午11:12:29
 */

public class UserInfo {

	private String userSId = "";
	private String password = "";
	private String userName = "";
	private String userIcon = "";
	private String listString = "";
	private List<ChildrenInfo> childList = new ArrayList<ChildrenInfo>();

	private final String JSON_UserId = "user_id";
	private final String JSON_UserName = "user_name";
	private final String JSON_Icon = "icon";
	private final String JSON_List = "childList";

	// 宝宝信息
	private final String JSON_ChildId = "child_id";
	private final String JSON_ChildNo = "child_no";
	private final String JSON_DailyId = "daily_id";
	private final String JSON_ChildName = "child_name";
	private final String JSON_Nickname = "nickname";
	private final String JSON_ChildIcon = "child_icon";
	private final String JSON_ChildBirthday = "birthday";

	public UserInfo(JSONObject jsonObject) throws JSONException {
		super();

		// 解析json数据
		this.userSId = jsonObject.getString(JSON_UserId);
		this.password = "";
		this.userName = jsonObject.getString(JSON_UserName);
		this.userIcon = jsonObject.getString(JSON_Icon);
		this.listString = jsonObject.getString(JSON_List);

		this.childList = new ArrayList<ChildrenInfo>();
		JSONArray jsonArray = jsonObject.getJSONArray(JSON_List);

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonListModel = jsonArray.getJSONObject(i);

			ChildrenInfo childrenInfo = new ChildrenInfo();
			childrenInfo.setChildId(jsonListModel.getString(JSON_ChildId));
			childrenInfo.setChild_no(jsonListModel.getString(JSON_ChildNo));
			childrenInfo.setDaily_id(jsonListModel.getString(JSON_DailyId));
			childrenInfo.setChildName(jsonListModel.getString(JSON_ChildName));
			childrenInfo.setNickname(jsonListModel.getString(JSON_Nickname));
			childrenInfo.setChildIcon(jsonListModel.getString(JSON_ChildIcon));

			try {
				childrenInfo.setBirthday(jsonListModel
						.getString(JSON_ChildBirthday));
			} catch (Exception e) {
				childrenInfo.setBirthday("");
			}

			this.childList.add(childrenInfo);
		}
	}

	public static UserInfo userInfoWithString(String infoString) {

		UserInfo userInfo = null;
		try {
			JSONObject jsonObject = new JSONObject(infoString);

			userInfo = new UserInfo(jsonObject);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return userInfo;
	}

	public UserInfo(String userSId, String userName) {
		this.userSId = userSId;
		this.userName = userName;
	}

	public String getUserSId() {
		return userSId;
	}

	public void setUserSId(String userId) {
		this.userSId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserIcon() {
		return userIcon;
	}

	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getListString() {
		return listString;
	}

	public void setListString(String listString) {
		this.listString = listString;
	}

	public List<ChildrenInfo> getChildList() {
		return childList;
	}

	public void setChildList(List<ChildrenInfo> childList) {
		this.childList = childList;
	}
}
