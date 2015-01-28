package cn.leature.istarbaby.domain;

public class FamilyInfo {
	String family_id;
	String userName;
	String user_id;
	String child_id;
	String child_no;
	String relative;
	String usericon;
	String childicon;
	String child_name;



	@Override
	public String toString()
	{
		return "FamilyInfo [family_id=" + family_id + ", userName=" + userName
				+ ", user_id=" + user_id + ", child_id=" + child_id
				+ ", child_no=" + child_no + ", relative=" + relative
				+ ", usericon=" + usericon + ", childicon=" + childicon
				+ ", child_name=" + child_name + "]";
	}



	public String getFamily_id()
	{
		return family_id;
	}



	public void setFamily_id(String family_id)
	{
		this.family_id = family_id;
	}



	public String getUserName()
	{
		return userName;
	}



	public void setUserName(String userName)
	{
		this.userName = userName;
	}



	public String getUser_id()
	{
		return user_id;
	}



	public void setUser_id(String user_id)
	{
		this.user_id = user_id;
	}



	public String getChild_id()
	{
		return child_id;
	}



	public void setChild_id(String child_id)
	{
		this.child_id = child_id;
	}



	public String getChild_no()
	{
		return child_no;
	}



	public void setChild_no(String child_no)
	{
		this.child_no = child_no;
	}



	public String getRelative()
	{
		return relative;
	}



	public void setRelative(String relative)
	{
		this.relative = relative;
	}



	public String getUsericon()
	{
		return usericon;
	}



	public void setUsericon(String usericon)
	{
		this.usericon = usericon;
	}



	public String getChildicon()
	{
		return childicon;
	}



	public void setChildicon(String childicon)
	{
		this.childicon = childicon;
	}



	public String getChild_name()
	{
		return child_name;
	}



	public FamilyInfo(String family_id, String userName, String user_id,
			String child_id, String child_no, String relative, String usericon,
			String childicon, String child_name)
	{
		super();
		this.family_id = family_id;
		this.userName = userName;
		this.user_id = user_id;
		this.child_id = child_id;
		this.child_no = child_no;
		this.relative = relative;
		this.usericon = usericon;
		this.childicon = childicon;
		this.child_name = child_name;
	}



	public void setChild_name(String child_name)
	{
		this.child_name = child_name;
	}

	

}
