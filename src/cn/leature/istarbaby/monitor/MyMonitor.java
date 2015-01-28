/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: LeatureCamera.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.monitor
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-9-15 上午11:47:43
 * @version: V1.0  
 */
package cn.leature.istarbaby.monitor;

/**
 * @ClassName: LeatureCamera
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-9-15 上午11:47:43
 */
public class MyMonitor extends MonitorClient {

	private String mUID = "";
	private int mChannel = 0;
	private String mViewName = "";
	private String mViewPwd = "";

	public MyMonitor(String uid) {
		super();

		this.mUID = uid;
	}

	public MyMonitor(String uid, String account, String pwd) {
		super();

		this.mUID = uid;
		this.mViewName = account;
		this.mViewPwd = pwd;
	}

	@Override
	public void connect(String uid) {
		this.mUID = uid;
		super.connect(uid);
	}

	@Override
	public void start(int avChannel, String viewAccount, String viewPasswd) {
		this.mChannel = avChannel;
		this.mViewName = viewAccount;
		this.mViewPwd = viewPasswd;

		super.start(avChannel, viewAccount, viewPasswd);
	}

	public void startPlayback(int avChannel, String viewAccount,
			String viewPasswd) {

		super.start(avChannel, viewAccount, viewPasswd);
	}

	public String getName() {
		return mViewName;
	}

	public void setName(String name) {
		this.mViewName = name;
	}

	public String getUID() {
		return mUID;
	}

	public int getChannel() {
		return mChannel;
	}

	public String getPassword() {
		return mViewPwd;
	}

}
