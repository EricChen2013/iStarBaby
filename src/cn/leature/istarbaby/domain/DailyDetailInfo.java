/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: DailyDetailInfo.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.domain
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 上午11:24:30
 * @version: V1.0  
 */
package cn.leature.istarbaby.domain;

/**
 * @ClassName: DailyDetailInfo
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 上午11:24:30
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DailyDetailInfo
{

	private String dailyId;
	private String userId;
	private String userName;
	private String userIcon;
	private String eventDate;
	private String detail;
	private String tagName;
	private String[] image;


	// 宝宝成长记录
	private ChildDataInfo childDataInfo;

	private String JSON_DailyId = "daily_id";
	private String JSON_UserId = "user_id";
	private String JSON_UserName = "user_name";
	private String JSON_UserIcon = "user_icon";
	private String JSON_EventDate = "event_date";
	private String JSON_Detail = "detail";
	private String JSON_TagName = "tagname";
	private String JSON_Image = "image";
	// 宝宝成长记录
	private String JSON_ChildId = "child_id";
	private String JSON_ChildH = "height";
	private String JSON_ChildW = "weight";
	private String JSON_ChildT = "touwei";
	private String JSON_ChildX = "xiongwei";
	private String JSON_ChildName = "child_name";
	private String JSON_ChildBirthDay = "birthday";


	public DailyDetailInfo()
	{
		//
	}

	public String getDailyId()
	{
		return dailyId;
	}

	public void setDailyId(String dailyId)
	{
		this.dailyId = dailyId;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getUserIcon()
	{
		return userIcon;
	}

	public void setUserIcon(String userIcon)
	{
		this.userIcon = userIcon;
	}

	public String getEventDate()
	{
		return eventDate;
	}

	public void setEventDate(String eventDate)
	{
		this.eventDate = eventDate;
	}

	public String getDetail()
	{
		return detail;
	}

	public void setDetail(String detail)
	{
		this.detail = detail;
	}

	public String getTagName()
	{
		return tagName;
	}

	public void setTagName(String tagName)
	{
		this.tagName = tagName;
	}

	public String[] getImage()
	{
		return image;
	}

	public void setImage(String[] image)
	{
		this.image = image;
	}

	public ChildDataInfo getChildDataInfo()
	{
		return childDataInfo;
	}

	public void setChildDataInfo(ChildDataInfo childDataInfo)
	{
		this.childDataInfo = childDataInfo;
	}

	public DailyDetailInfo(JSONObject jsonObject)
	{
		super();

		try
		{
			this.dailyId = jsonObject.getString(JSON_DailyId);
			this.userId = jsonObject.getString(JSON_UserId);
			this.userName = jsonObject.getString(JSON_UserName);
			this.userIcon = jsonObject.getString(JSON_UserIcon);
			this.eventDate = jsonObject.getString(JSON_EventDate);
			this.detail = jsonObject.getString(JSON_Detail);
			this.tagName = jsonObject.getString(JSON_TagName);
			
			JSONArray jsonArray = jsonObject.getJSONArray(JSON_Image);

			this.image = new String[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++)
			{
				this.image[i] = jsonArray.getString(i);
			}

			// 宝宝记录
			this.childDataInfo = new ChildDataInfo(
					jsonObject.getJSONObject("childdata"));
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class ChildDataInfo
	{
		private String childId;
		private String childHeight;
		private String childWeight;
		private String childTouwei;
		private String childXiongwei;
		private String chidlName;
		private String birthDay;

		public String getChidlName()
		{
			return chidlName;
		}

		public void setChidlName(String chidlName)
		{
			this.chidlName = chidlName;
		}

		public String getBirthDay()
		{
			return birthDay;
		}

		public void setBirthDay(String birthDay)
		{
			this.birthDay = birthDay;
		}

		public String getChildId()
		{
			return childId;
		}

		public void setChildId(String childId)
		{
			this.childId = childId;
		}

		public String getChildHeight()
		{
			return childHeight;
		}

		public void setChildHeight(String childHeight)
		{
			this.childHeight = childHeight;
		}

		public String getChildWeight()
		{
			return childWeight;
		}

		public void setChildWeight(String childWeight)
		{
			this.childWeight = childWeight;
		}

		public String getChildTouwei()
		{
			return childTouwei;
		}

		public void setChildTouwei(String childTouwei)
		{
			this.childTouwei = childTouwei;
		}

		public String getChildXiongwei()
		{
			return childXiongwei;
		}

		public void setChildXiongwei(String childXiongwei)
		{
			this.childXiongwei = childXiongwei;
		}

		public ChildDataInfo(JSONObject jsonObject)
		{
			try
			{
				this.childId = jsonObject.getString(JSON_ChildId);
				this.childHeight = jsonObject.getString(JSON_ChildH);
				this.childWeight = jsonObject.getString(JSON_ChildW);
				this.childTouwei = jsonObject.getString(JSON_ChildT);
				this.childXiongwei = jsonObject.getString(JSON_ChildX);
				this.chidlName = jsonObject.getString(JSON_ChildName);
				this.birthDay = jsonObject.getString(JSON_ChildBirthDay);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
	}
}
