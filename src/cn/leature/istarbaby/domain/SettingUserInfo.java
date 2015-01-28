package cn.leature.istarbaby.domain;

import org.json.JSONException;
import org.json.JSONObject;
public class SettingUserInfo
{
	String user_id;
	String password;
	String gender;
	String name;
	String icon;
	String birthday;
	String mobile;
	String tel;
	String email;
	String address;
	

	public String getUser_id()
	{
		return user_id;
	}


	public void setUser_id(String user_id)
	{
		this.user_id = user_id;
	}


	public String getPassword()
	{
		return password;
	}


	public void setPassword(String password)
	{
		this.password = password;
	}


	public String getGender()
	{
		return gender;
	}


	public void setGender(String gender)
	{
		this.gender = gender;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public String getIcon()
	{
		return icon;
	}


	public void setIcon(String icon)
	{
		this.icon = icon;
	}


	public String getBirthday()
	{
		return birthday;
	}


	public void setBirthday(String birthday)
	{
		this.birthday = birthday;
	}


	public String getMobile()
	{
		return mobile;
	}


	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}


	public String getTel()
	{
		return tel;
	}


	public void setTel(String tel)
	{
		this.tel = tel;
	}


	public String getEmail()
	{
		return email;
	}


	public void setEmail(String email)
	{
		this.email = email;
	}


	public String getAddress()
	{
		return address;
	}


	public void setAddress(String address)
	{
		this.address = address;
	}


	public SettingUserInfo(JSONObject jsonObject)
	{
		try
		{

			this.user_id = jsonObject.getString("user_id");
			this.password = jsonObject.getString("password");
			this.gender = jsonObject.getString("gender");
			this.name = jsonObject.getString("name");
			this.icon = jsonObject.getString("icon");
			this.birthday = jsonObject.getString("birthday");
			this.mobile = jsonObject.getString("mobile");
			this.tel = jsonObject.getString("tel");
			this.email = jsonObject.getString("email");
			this.address = jsonObject.getString("address");

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
