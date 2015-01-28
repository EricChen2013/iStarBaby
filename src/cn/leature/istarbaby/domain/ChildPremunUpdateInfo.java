package cn.leature.istarbaby.domain;

import org.json.JSONException;
import org.json.JSONObject;

public class ChildPremunUpdateInfo {

	String event_date;
	String detail;
	
	public String getEvent_date()
	{
		return event_date;
	}

	public void setEvent_date(String event_date)
	{
		this.event_date = event_date;
	}

	public String getDetail()
	{
		return detail;
	}

	public void setDetail(String detail)
	{
		this.detail = detail;
	}


	public ChildPremunUpdateInfo(String event_date, String detail)
	{
		super();
		this.event_date = event_date;
		this.detail = detail;
	}

	public ChildPremunUpdateInfo(JSONObject jsonObject) {
		try {
			this.event_date = jsonObject.getString("event_date");
			this.detail = jsonObject.getString("detail");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
