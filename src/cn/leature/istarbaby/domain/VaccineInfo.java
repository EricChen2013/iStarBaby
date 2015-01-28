package cn.leature.istarbaby.domain;


import org.json.JSONException;
import org.json.JSONObject;


public class VaccineInfo
{
	String vaccine_id;
	String vaccine_name;
	String inoculate_times;
	String vaccine_type;

	public String getVaccine_id()
	{
		return vaccine_id;
	}

	public void setVaccine_id(String vaccine_id)
	{
		this.vaccine_id = vaccine_id;
	}

	public String getVaccine_name()
	{
		return vaccine_name;
	}

	public void setVaccine_name(String vaccine_name)
	{
		this.vaccine_name = vaccine_name;
	}

	public String getInoculate_times()
	{
		return inoculate_times;
	}

	public void setInoculate_times(String inoculate_times)
	{
		this.inoculate_times = inoculate_times;
	}

	public String getVaccine_type()
	{
		return vaccine_type;
	}

	public void setVaccine_type(String vaccine_type)
	{
		this.vaccine_type = vaccine_type;
	}

	@Override
	public String toString()
	{
		return "vaccineInfo [vaccine_id=" + vaccine_id + ", vaccine_name="
				+ vaccine_name + ", inoculate_times=" + inoculate_times
				+ ", vaccine_type=" + vaccine_type + "]";
	}

	public VaccineInfo(JSONObject jsonObject)
	{
		try
		{

			this.vaccine_id = jsonObject.getString("vaccine_id");
			this.vaccine_name = jsonObject.getString("vaccine_name");
			this.inoculate_times = jsonObject.getString("inoculate_times");
			this.vaccine_type = jsonObject.getString("vaccine_type");

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
