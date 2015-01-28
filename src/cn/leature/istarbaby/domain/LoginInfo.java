/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: UserLogin.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.login
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 上午11:11:32
 * @version: V1.0  
 */
package cn.leature.istarbaby.domain;

/**
 * @ClassName: UserLogin
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 上午11:11:32
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import cn.leature.istarbaby.sqlLite.LzDBHelper;

public class LoginInfo {

	// 登录的所有用户信息
	private static final String mUserInfoTbl = "LoginData";

	// 当前登录的用户信息写入文件： LoginInfo.xml
	private static String loginUserFileName = "LoginInfo";

	public LoginInfo() {
		// TODO Auto-generated constructor stub
	}

	public static LzDBHelper initUserDB(Context context) {
		LzDBHelper dbHelper = new LzDBHelper(context);
		dbHelper.open();

		if (!dbHelper.isTableExist(mUserInfoTbl)) {
			// 表不存在，创建新表
			String sql = "CREATE TABLE " + mUserInfoTbl
					+ " (user_id CHAR(11) primary key, "
					+ "password VARCHAR(30) not null, "
					+ "user_data VARCHAR(1000) not null);";

			dbHelper.execSQL(sql);
		}

		return dbHelper;
	}

	public static void saveLoginUser(Context context, Map<String, String> param) {
		if ((param == null) || (param.size() == 0)) {
			// 没有登录信息
			return;
		}

		// 保存当前登录用户的信息
		savePreferencesFile(context, param);

		// 往数据库里面保存当前登录用户的信息
		saveUserInfoTable(context, param);
	}

	/**
	 * @Title: saveUserInfoTable
	 * @Description: TODO
	 * @param context
	 * @param param
	 * @return: void
	 */
	private static void saveUserInfoTable(Context context,
			Map<String, String> param) {

		LzDBHelper dbHelper = initUserDB(context);

		String userId = param.get("UserId").toString();

		ContentValues values = new ContentValues();

		if (dbHelper.isRecordExist(mUserInfoTbl, new String[] { "user_id",
				"user_data" }, "user_id=?", new String[] { userId })) {
			// 更新记录
			values.put("user_id", param.get("UserId"));
			if (param.containsKey("Password")) {
				values.put("password", param.get("Password"));
			}

			if (param.containsKey("UserString")) {
				values.put("user_data", param.get("UserString"));
			}

			dbHelper.update(mUserInfoTbl, values, "user_id=?",
					new String[] { userId });
		} else {
			// 新生成记录
			values.put("user_id", userId);
			values.put("password", param.get("Password"));
			values.put("user_data", param.get("UserString"));

			dbHelper.insert(mUserInfoTbl, values);
		}

		dbHelper.close();
	}

	private static void savePreferencesFile(Context context,
			Map<String, String> param) {
		SharedPreferences preferences = context.getSharedPreferences(
				loginUserFileName, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		Iterator<String> it = param.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = param.get(key);

			editor.putString(key, value);
		}

		editor.commit();
	}

	public static UserInfo loadLoginUserInfo(Context context) {

		SharedPreferences preferences = context.getSharedPreferences(
				loginUserFileName, Context.MODE_PRIVATE);

		String userString = preferences.getString("UserString", "");

		UserInfo userInfo = loadUserInfoWithString(context, userString);
		if (userInfo == null) {
			return null;
		}
		// password从本地文件中取得
		userInfo.setPassword(preferences.getString("Password", ""));

		return userInfo;
	}

	private static UserInfo loadUserInfoWithString(Context context,
			String dataString) {

		UserInfo userInfo = new UserInfo("", "");

		if (dataString == null || "".equals(dataString)) {
			return userInfo;
		}

		try {
			JSONObject jsonObject = new JSONObject(dataString);

			userInfo = new UserInfo(jsonObject);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return userInfo;
	}

	public static UserInfo loadLoginUserInfo(Context context, String userId) {

		UserInfo userInfo = new UserInfo(userId, "");

		// 初始化UserDB
		LzDBHelper dbHelper = initUserDB(context);
		Cursor returnCursor = null;
		if (userId == null) {
			// userId为空，取出第一条记录
			returnCursor = dbHelper.findInfo(mUserInfoTbl,
					new String[] { "user_data" }, null, null, null, null, null,
					null, false);

		} else {
			// 取出该userId的记录
			returnCursor = dbHelper.findInfo(mUserInfoTbl, new String[] {
					"user_id", "user_data" }, "user_id=?",
					new String[] { userId }, null, null, null, null, false);
		}

		if (returnCursor == null) {
			dbHelper.close();

			return userInfo;
		}
		if (returnCursor.getCount() > 0) {
			String userData = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("user_data"));

			userInfo = loadUserInfoWithString(context, userData);
		}

		returnCursor.close();
		dbHelper.close();

		return userInfo;
	}

	public static List<UserInfo> loadLoginUserInfoList(Context context) {
		List<UserInfo> list = null;

		// 初始化UserDB
		LzDBHelper dbHelper = initUserDB(context);
		Cursor returnCursor = null;

		// userId为空，取出第一条记录
		returnCursor = dbHelper.findInfo(mUserInfoTbl, new String[] {
				"password", "user_data" }, null, null, null, null, null, null,
				false);

		if (returnCursor == null) {
			dbHelper.close();

			return list;
		}

		if (returnCursor.getCount() > 0) {
			list = new ArrayList<UserInfo>();

			String userData = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("user_data"));
			String password = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("password"));

			UserInfo userInfo = loadUserInfoWithString(context, userData);
			userInfo.setPassword(password);
			if (userInfo.getUserSId().length() > 0) {
				list.add(userInfo);
			}

			while (returnCursor.moveToNext()) {

				String nextData = returnCursor.getString(returnCursor
						.getColumnIndexOrThrow("user_data"));
				String nextPass = returnCursor.getString(returnCursor
						.getColumnIndexOrThrow("password"));
				UserInfo nextUser = loadUserInfoWithString(context, nextData);

				nextUser.setPassword(nextPass);
				if (nextUser.getUserSId().length() > 0) {
					list.add(nextUser);
				}
			}
		}

		returnCursor.close();
		dbHelper.close();

		return list;
	}

	/**
	 * @Title: getLoginUserId
	 * @Description: TODO
	 * @return
	 * @return: String
	 */
	public static String getLoginUserId(Context context) {
		UserInfo userInfo = LoginInfo.loadLoginUserInfo(context);
		if (null == userInfo) {
			return "";
		}

		return userInfo.getUserSId();
	}

	/**
	 * @Title: saveVersionInfo
	 * @Description: TODO
	 * @param requestGetDoneCallback
	 * @param param
	 * @return: void
	 */
	public static void saveVersionInfo(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				loginUserFileName, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String today = df.format(new Date());

		editor.putString("VersionCheckDate", today);

		editor.commit();
	}

	/**
	 * @Title: checkVersionUpgraded
	 * @Description: TODO
	 * @param loginActivity
	 * @return
	 * @return: boolean
	 */
	public static boolean isVersionUpgradeChecked(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				loginUserFileName, Context.MODE_PRIVATE);

		// 上回检查时间
		String checkDate = preferences.getString("VersionCheckDate", "");

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String today = df.format(new Date());

		if (checkDate.compareTo(today) < 0) {
			// 今天第一次检查
			return true;
		}

		return false;
	}

	/**
	 * @Title: isFirstStartApp
	 * @Description: TODO
	 * @param splashActivity
	 * @return
	 * @return: boolean
	 */
	public static boolean isFirstStartApp(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				loginUserFileName, Context.MODE_PRIVATE);

		// 是否初次启动
		String first = preferences.getString("FirstStartApp", "");

		if (first.length() == 0) {
			// 初次登录
			Editor editor = preferences.edit();

			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			String today = df.format(new Date());

			editor.putString("FirstStartApp", today);
			editor.commit();

			return true;
		}

		return false;
	}
}
