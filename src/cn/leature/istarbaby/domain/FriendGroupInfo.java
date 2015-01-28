package cn.leature.istarbaby.domain;

import java.io.Serializable;


public class FriendGroupInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3519285314992926361L;
	String Group_name;
	String Group_id;

	public FriendGroupInfo() {
		this.Group_name = "";
		this.Group_id = "";
	}

	public FriendGroupInfo(FriendListInfo friendListInfo) {
		this.Group_name = friendListInfo.getGroup_name();
		this.Group_id = friendListInfo.getGroup_id();
	}

	public String getGroup_name() {
		return Group_name;
	}

	public void setGroup_name(String group_name) {
		Group_name = group_name;
	}

	public String getGroup_id() {
		return Group_id;
	}

	public void setGroup_id(String group_id) {
		Group_id = group_id;
	}
}
