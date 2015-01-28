/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: ConstantDef.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.utils
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午2:47:36
 * @version: V1.0  
 */
package cn.leature.istarbaby.utils;

/**
 * @ClassName: ConstantDef
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午2:47:36
 */
public class ConstantDef {
	public static final boolean IS_DEBUG_MODE = true;

	public static final String WXAPP_ID = "wx0f79bb92672fde26";
	// public static final String WXAPP_ID = "wx24cfa5a56f0c725e";//demo
	public static final String QQAPP_ID = "1103489184";// QQ>appkey
	public static final String WBAPP_ID = "715947659";
	// public static final String WBAPP_ID = "3929830678";// demo
	// 各个画面的URL
	public static final String cUrlCheckVersion = "VersionInfo.aspx"; // 版本更新检测
	public static final String cUrlLogin = "Login.aspx"; // 用户登录
	public static final String cUrlAgreement = "Agreement.aspx"; // 用户条款
	public static final String cUrlAlbumList = "AlbumList.aspx";
	public static final String cUrlDailyList = "DailyList.aspx";

	public static final String cUrlUploadChildIcon = "UploadChildIcon.aspx";
	public static final String cUrlUploadUserIcon = "UploadUserIcon.aspx";
	public static final String cUrlChildrenDetail = "ChildrenDetail.aspx";
	public static final String cUrlChildrenAdd = "ChildrenAdd.aspx";

	public static final String cUrlChildrenEdit = "ChildrenEdit.aspx";
	public static final String cUrlDailyDetail = "DailyDetail.aspx";
	public static final String cUrlDailyDelete = "DailyDelete.aspx";
	public static final String cUrlDailyEdit = "DailyEdit.aspx";

	public static final String cUrlFamilyDetail = "FamilyDetail.aspx";
	public static final String cUrlChildGrowData = "ChildGrowData.aspx";
	public static final String cUrlChildrenDelete = "ChildrenDelete.aspx";
	public static final String cUrlPasswordEdit = "PasswordEdit.aspx";

	public static final String cUrlQuestionList = "QuestionList.aspx";
	public static final String cUrlContactGroupEdit = "ContactGroupEdit.aspx";// 组列表

	// 用户ID检查URL
	public static final String cUrlCheckUserId = "CheckUserId.aspx";
	// 发表文章URL
	public static final String cUrlPostDaily = "PostDaily.aspx";
	// 上传图片URL
	public static final String cUrlUploadPhoto = "UploadPhone.aspx";

	// 设置编辑内容
	public static final String cUrlUserInfoDetail = "UserInfoDetail.aspx";
	public static final String cUrlUserInfoEdit = "UserInfoEdit.aspx";
	public static final String cUrlFeedbackAdd = "FeedbackAdd.aspx";
	public static final String cUrlVaccineInfo = "VaccineInfo.aspx";
	public static final String cUrlInoculateVaccine = "InoculateVaccine.aspx";
	public static final String cUrlContactList = "ContactList.aspx";
	public static final String cUrlContactSearch = "ContactSearch.aspx";// 查找
	public static final String cUrlContactAdd = "ContactAdd.aspx";
	public static final String cUrlContactEdit = "ContactEdit.aspx";
	public static final String cUrlContactMemoEdit = "ContactMemoEdit.aspx";
	public static final String cUrlContactDelete = "ContactDelete.aspx";// 删除好友
	public static final String cUrlDeviceList = "DeviceList.aspx";// 设备分享
	public static final String cUrlDeviceInfoAdd = "DeviceInfoAdd.aspx";// 设备分享添加
	public static final String cUrlContactGroupPositionChange = "ContactGroupPositionChange.aspx";// 移动公组
	public static final String cUrlDeviceContactDelete = "DeviceContactDelete.aspx";

	// 注册后新用户的初始密码
	public static final String cDefaultPassword = "9999";
	// 列表图片下载或显示时，缩放大小
	public static final int cgImageResizeWidth = 200;
	public static final int cgImageResizeHeight = 200;
	// activity间传递参数(Bundle用)
	public static final String cUserId = "Bundle_UserId";
	public static final String cUserPassword = "Bundle_Password";
	public static final String clAuthCode = "Bundle_AuthCode";
	public static final String ccChildNo = "Bundle_ChildNo";
	public static final String ccChildId = "Bundle_ChildId";
	public static final String ccChildName = "Bundle_ChildName";
	public static final String ccGroup_id = "Bundle_group_id";
	public static final String ccEventId = "Bundle_event_id";

	public static final String ccDaily_Id = "Bundle_Daily_Id";
	public static final String ccEventName = "Bundle_event_name";
	public static final String cdImageFolderSelected = "Bundle_ImageFolderSelected";
	public static final String cdImagesSelected = "Bundle_ImagesSelected";

	// Activity跳转用
	public static final int REQUEST_CODE_DAILY_POST = 101;
	public static final int REQUEST_CODE_DAILY_EDIT = 102;
	public static final int REQUEST_CODE_DAILY_DETAIL = 103;
	public static final int REQUEST_CODE_DAILY_PHOTO = 104;
	public static final int REQUEST_CODE_CHILD_DETAIL = 201;
	public static final int REQUEST_CODE_CHILD_HWDATA = 211;
	public static final int REQUEST_CODE_CHILD_TXDATA = 212;
	public static final int REQUEST_CODE_CHILD_ALBUM = 213;
	public static final int REQUEST_CODE_CHILD_EDIT = 221;
	public static final int REQUEST_CODE_CHILD_PHOTO = 222;
	public static final int REQUEST_CODE_BABY_ADD = 301;
	public static final int REQUEST_CODE_PREMUNITION = 302;
	public static final int REQUEST_CODE_QA_CATALOG = 401;
	public static final int REQUEST_CODE_QA_DETAIL = 402;
	public static final int REQUEST_CODE_MONITOR_ADD = 501;
	public static final int REQUEST_CODE_MONITOR_PHOTO = 503;
	public static final int REQUEST_CODE_MONITOR_EVENT = 504;
	public static final int REQUEST_CODE_MONITOR_SHARE = 505;
	public static final int REQUEST_CODE_MONITOR_SETTING = 506;
	public static final int REQUEST_CODE_MONITOR_ADVSETTING = 601;
	public static final int REQUEST_CODE_MONITOR_LIVE = 602;
	public static final int REQUEST_CODE_MONITOR_WIFI = 603;
	public static final int REQUEST_CODE_MONITOR_PLAYBACK = 604;
	public static final int REQUEST_CODE_LOGIN = 901;
	public static final int REQUEST_CODE_REGISTRATION_USER = 902;
	public static final int REQUEST_CODE_REGISTRATION_AUTH = 903;
	public static final int REQUEST_CODE_REGISTRATION_SUBMIT = 904;
	public static final int REQUEST_CODE_TERM_SERVICE = 905;
	public static final int REQUEST_CODE_FROUNDPASSWORD = 911;
	public static final int REQUEST_CODE_PASSWORDSUBMIT = 912;
	public static final int REQUEST_CODE_SETTING_PROFILE = 921;
	public static final int REQUEST_CODE_SETTING_PASSWORD = 922;
	public static final int REQUEST_CODE_SETTING_PREFERENCE = 923;
	public static final int REQUEST_CODE_SETTINGABOUTAPP = 924;
	public static final int REQUEST_CODE_SETTINGRECOMMEND = 925;
	public static final int REQUEST_CODE_SETTINGMYSURFACE = 926;
	public static final int REQUEST_CODE_SETTINGFRIENDS = 927;
	public static final int REQUEST_CODE_FRIENDDETAIL = 931;
	public static final int REQUEST_CODE_FRIENDSEARCH = 932;
	public static final int REQUEST_CODE_FRIENDSDETAIL = 933;
	public static final int REQUEST_CODE_FRIENDMANAGE = 934;
	public static final int REQUEST_CODE_FRIENDGRPMNG = 935;
	public static final int REQUEST_CODE_FRIENDMEMONAME = 936;
	public static final int REQUEST_CODE_FRIEND_ADDSEND = 937;
	public static final int REQUEST_CODE_FRIEND_SELGROUP = 938;
	public static final int REQUEST_CODE_FRIEND_SHARE = 939;
	public static final int REQUEST_CODE_MonitorAddFriendFx = 621;

	public static final int RESULT_CODE_CHILD_HWDATA = 211;
	public static final int RESULT_CODE_CHILD_TXDATA = 212;
	public static final int RESULT_CODE_DAILY_EDIT = 102;
	public static final int RESULT_CODE_ALBUM_EDIT = 213;
	public static final int RESULT_CODE_CHILD_EDIT = 221;
	public static final int RESULT_CODE_PREMUNITION = 302;
	public static final int RESULT_CODE_MONITOR_PHOTO = 503;
	public static final int RESULT_CODE_MONITOR_EVENT = 504;
	public static final int RESULT_CODE_MONITOR_WIFI = 603;
	public static final int RESULT_CODE_PASSWORDSUBMIT = 906;
	public static final int RESULT_CODE_SETTINGNEWSERVER = 928;
	public static final int RESULT_CODE_FRIENDDETAIL = 931;
	public static final int RESULT_CODE_FRIENDSEARCH = 932;
	public static final int RESULT_CODE_FRIENDSDETAIL = 933;
	public static final int RESULT_CODE_FRIENDMANAGE = 934;
	public static final int RESULT_CODE_FRIENDGRPMNG = 935;
	public static final int RESULT_CODE_FRIENDMEMONAME = 936;
	public static final int RESULT_CODE_FRIEND_ADDSEND = 937;
	public static final int RESULT_CODE_FRIEND_SELGROUP = 938;
	public static final int RESULT_CODE_MonitorAddFriendFx = 621;

}
