/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: ChildrenInfo.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.domain
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午12:12:11
 * @version: V1.0  
 */
package cn.leature.istarbaby.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @ClassName: ChildrenInfo
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午12:12:11
 */

public class ChildrenInfo {

	private String child_id;
	private String user_id;
	private String child_no;
	private String daily_id;
	private String birthday;
	private String gender;
	private String child_name;
	private String birthday_time;
	private String nickname;
	private String icon;
	private String height;
	private String weight;
	private String xiongwei;
	private String touwei;
	private String fenmian_yuding;
	private String fenmian_difang;
	private String fenmian_shijian;
	private String huaiyun_qijian;
	private String image1;

	public ChildrenInfo() {
		//
	}

	public ChildrenInfo(JSONObject jsonObject) {
		try {

			this.child_id = jsonObject.getString("child_id");
			this.user_id = jsonObject.getString("user_id");
			this.child_no = jsonObject.getString("child_no");
			this.daily_id = jsonObject.getString("daily_id");
			this.birthday = jsonObject.getString("birthday");
			this.gender = jsonObject.getString("gender");
			this.child_name = jsonObject.getString("child_name");
			this.birthday_time = jsonObject.getString("birthday_time");
			this.nickname = jsonObject.getString("nickname");
			this.icon = jsonObject.getString("icon");
			this.height = jsonObject.getString("height");
			this.weight = jsonObject.getString("weight");
			this.xiongwei = jsonObject.getString("xiongwei");
			this.touwei = jsonObject.getString("touwei");
			this.fenmian_yuding = jsonObject.getString("fenmian_yuding");
			this.fenmian_difang = jsonObject.getString("fenmian_difang");
			this.fenmian_shijian = jsonObject.getString("fenmian_shijian");
			this.huaiyun_qijian = jsonObject.getString("huaiyun_qijian");
			this.image1 = jsonObject.getString("image1");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getChild_id() {
		return child_id;
	}

	public void setChildId(String child_id) {
		this.child_id = child_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getChild_no() {
		return child_no;
	}

	public void setChild_no(String child_no) {
		this.child_no = child_no;
	}

	public String getDaily_id() {
		return daily_id;
	}

	public void setDaily_id(String daily_id) {
		this.daily_id = daily_id;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getChildName() {
		return child_name;
	}

	public void setChildName(String child_name) {
		this.child_name = child_name;
	}

	public String getBirthday_time() {
		return birthday_time;
	}

	public void setBirthday_time(String birthday_time) {
		this.birthday_time = birthday_time;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getIcon() {
		return icon;
	}

	public void setChildIcon(String icon) {
		this.icon = icon;
	}

	public String getHeight() {
		if ("0".equals(height)) {
			return "";
		}

		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		if ("0".equals(weight)) {
			return "";
		}

		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getXiongwei() {
		if ("0".equals(xiongwei)) {
			return "";
		}

		return xiongwei;
	}

	public void setXiongwei(String xiongwei) {
		this.xiongwei = xiongwei;
	}

	public String getTouwei() {
		if ("0".equals(touwei)) {
			return "";
		}

		return touwei;
	}

	public void setTouwei(String touwei) {
		this.touwei = touwei;
	}

	public String getFenmian_yuding() {
		return fenmian_yuding;
	}

	public void setFenmian_yuding(String fenmian_yuding) {
		this.fenmian_yuding = fenmian_yuding;
	}

	public String getFenmian_difang() {
		return fenmian_difang;
	}

	public void setFenmian_difang(String fenmian_difang) {
		this.fenmian_difang = fenmian_difang;
	}

	public String getFenmian_shijian() {
		return fenmian_shijian;
	}

	public void setFenmian_shijian(String fenmian_shijian) {
		this.fenmian_shijian = fenmian_shijian;
	}

	public String getHuaiyun_qijian() {
		return huaiyun_qijian;
	}

	public void setHuaiyun_qijian(String huaiyun_qijian) {
		this.huaiyun_qijian = huaiyun_qijian;
	}

	public String getImage1() {
		return image1;
	}

	public void setImage1(String image1) {
		this.image1 = image1;
	}
}
