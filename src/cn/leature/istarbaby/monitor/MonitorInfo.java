package cn.leature.istarbaby.monitor;

import java.io.Serializable;

public class MonitorInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean ChangePassword = false;
	public int ChannelIndex = 0;
	public long DBID = 0;
	public int EventNotification = 0;
	public int Mode = 0;
	public boolean Online = false;
	public boolean ShowTipsForFormatSDCard = false;
	public String Snapshot = "";
	public String Status = "";

	public String UserId;
	public String UID;
	public String NickName;
	public String DeviceName;
	public String DevicePassword;
	public String ViewAccount;
	public String ViewPassword;

	public int connect_count = 0;
	public int n_gcm_count = 0;

	public MonitorInfo(String userid, String uid, String name, String dev_name,
			String dev_pwd, String account, String pwd) {
		this.UserId = userid;
		this.UID = uid;
		this.NickName = name;

		if (dev_name.trim().length() == 0) {
			// 默认
			this.DeviceName = "admin";
		} else {
			this.DeviceName = account;
		}
		if (dev_pwd.trim().length() == 0) {
			// 默认
			this.DevicePassword = "admin";
		} else {
			this.DevicePassword = dev_pwd;
		}

		if (account.trim().length() == 0) {
			// 默认
			this.ViewAccount = "admin";
		} else {
			this.ViewAccount = account;
		}
		if (pwd.trim().length() == 0) {
			// 默认
			this.ViewPassword = "admin";
		} else {
			this.ViewPassword = pwd;
		}
	}
}
