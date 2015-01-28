package cn.leature.istarbaby.monitor;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cn.leature.istarbaby.sqlLite.LzDBHelper;

public class MonitorDBManager {
	// 登录的所有摄像机信息
	private static final String mMonitorInfoTbl = "MonitorData";

	public MonitorDBManager() {
		// TODO Auto-generated constructor stub
	}

	public static LzDBHelper initUserDB(Context context) {
		LzDBHelper dbHelper = new LzDBHelper(context);
		dbHelper.open();

		if (!dbHelper.isTableExist(mMonitorInfoTbl)) {
			// 表不存在，创建新表
			String sql = "CREATE TABLE " + mMonitorInfoTbl
					+ " (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
					+ "dev_nickname VARCHAR(30) not null, "
					+ "user_id CHAR(11) not null, "
					+ "dev_uid VARCHAR(20) not null, "
					+ "dev_name VARCHAR(30) not null, "
					+ "dev_pwd VARCHAR(30) not null, "
					+ "view_acc VARCHAR(30) not null default (''), "
					+ "view_pwd VARCHAR(30) not null default (''), "
					+ "event_notification INTEGER not null default (0), "
					+ "ask_format_sdcard INTEGER not null default (0), "
					+ "camera_channel INTEGER not null default (0), "
					+ "snapshot VARCHAR(200) not null default (''));";

			dbHelper.execSQL(sql);
		}

		return dbHelper;
	}

	public static boolean isMonitorExist(Context context,
			MonitorInfo monitorInfo) {

		boolean result = false;

		LzDBHelper dbHelper = initUserDB(context);

		String uid = monitorInfo.UID;
		String user_id = monitorInfo.UserId;

		if (dbHelper.isRecordExist(mMonitorInfoTbl, new String[] { "_id" },
				"dev_uid=? and user_id=?", new String[] { uid, user_id })) {
			// 该设备已经存在
			result = true;
		}

		dbHelper.close();
		return result;
	}

	public static boolean isMonitorExist(LzDBHelper dbHelper,
			MonitorInfo monitorInfo) {

		boolean result = false;

		String uid = monitorInfo.UID;
		String user_id = monitorInfo.UserId;

		if (dbHelper.isRecordExist(mMonitorInfoTbl, new String[] { "_id" },
				"dev_uid=? and user_id=?", new String[] { uid, user_id })) {
			// 该设备已经存在
			result = true;
		}

		return result;
	}

	public static void saveMonitorInfoTable(Context context,
			MonitorInfo monitorInfo) {

		String uid = monitorInfo.UID.trim();
		String user_id = monitorInfo.UserId.trim();

		if ((uid.length() == 0) || (user_id.length() == 0)) {
			// 无效信息
			return;
		}

		LzDBHelper dbHelper = initUserDB(context);

		ContentValues values = new ContentValues();

		if (isMonitorExist(dbHelper, monitorInfo)) {

			// 更新记录
			if (monitorInfo.NickName.length() > 0) {
				values.put("dev_nickname", monitorInfo.NickName);
			}
			if (monitorInfo.DeviceName.length() > 0) {
				values.put("dev_name", monitorInfo.DeviceName);
			}
			if (monitorInfo.DevicePassword.length() > 0) {
				values.put("dev_pwd", monitorInfo.DevicePassword);
			}
			if (monitorInfo.ViewAccount.length() > 0) {
				values.put("view_acc", monitorInfo.ViewAccount);
			}
			if (monitorInfo.ViewPassword.length() > 0) {
				values.put("view_pwd", monitorInfo.ViewPassword);
			}

			dbHelper.update(mMonitorInfoTbl, values, "dev_uid=? and user_id=?",
					new String[] { uid, user_id });
		} else {

			// 新生成记录
			values.put("user_id", user_id);
			values.put("dev_uid", uid);
			values.put("dev_nickname", monitorInfo.NickName);
			values.put("dev_name", monitorInfo.DeviceName);
			values.put("dev_pwd", monitorInfo.DevicePassword);
			values.put("view_acc", monitorInfo.ViewAccount);
			values.put("view_pwd", monitorInfo.ViewPassword);

			dbHelper.insert(mMonitorInfoTbl, values);
		}

		dbHelper.close();
	}

	public static void updateMonitorSnapshot(Context context,
			MonitorInfo monitorInfo) {

		String uid = monitorInfo.UID.trim();
		String user_id = monitorInfo.UserId.trim();

		if ((uid.length() == 0) || (user_id.length() == 0)) {
			// 无效信息
			return;
		}

		LzDBHelper dbHelper = initUserDB(context);
		ContentValues values = new ContentValues();

		if (!isMonitorExist(dbHelper, monitorInfo)) {
			// 没有信息
			return;
		}

		// 更新记录
		if (monitorInfo.Snapshot.length() > 0) {
			values.put("snapshot", monitorInfo.Snapshot);
		}

		dbHelper.update(mMonitorInfoTbl, values, "dev_uid=? and user_id=?",
				new String[] { uid, user_id });

		dbHelper.close();
	}

	public static MonitorInfo loadMonitorInfo(Context context, String uid,
			String userid) {

		MonitorInfo monitorInfo = null;

		// 初始化UserDB
		LzDBHelper dbHelper = initUserDB(context);
		Cursor returnCursor = null;

		// 取出该uid的记录
		returnCursor = dbHelper.findInfo(mMonitorInfoTbl, new String[] { "_id",
				"dev_nickname", "dev_name", "dev_pwd", "view_acc", "view_pwd",
				"snapshot" }, "dev_uid=? and user_id=?", new String[] { uid,
				userid }, null, null, null, null, false);

		if (returnCursor == null) {
			dbHelper.close();

			return monitorInfo;
		}

		if (returnCursor.getCount() > 0) {
			String name = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("dev_nickname"));
			String devname = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("dev_name"));
			String devpwd = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("dev_pwd"));
			String viewacc = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("view_acc"));
			String viewpwd = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("view_pwd"));
			String snapshot = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("snapshot"));

			monitorInfo = new MonitorInfo(userid, uid, name, devname, devpwd,
					viewacc, viewpwd);
			monitorInfo.Snapshot = snapshot;
		}

		returnCursor.close();
		dbHelper.close();

		return monitorInfo;
	}

	public static ArrayList<MonitorInfo> loadMonitorInfoList(Context context,
			String userid) {

		ArrayList<MonitorInfo> list = new ArrayList<MonitorInfo>();

		// 初始化UserDB
		LzDBHelper dbHelper = initUserDB(context);

		Cursor returnCursor = dbHelper.findInfo(mMonitorInfoTbl, new String[] {
				"_id", "dev_nickname", "dev_uid", "dev_name", "dev_pwd",
				"view_acc", "view_pwd", "snapshot" }, "user_id=?",
				new String[] { userid }, null, null, "_id", null, false);

		for (int i = 0; i < returnCursor.getCount(); i++) {
			String uid = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("dev_uid"));
			String name = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("dev_nickname"));
			String devname = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("dev_name"));
			String devpwd = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("dev_pwd"));
			String viewacc = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("view_acc"));
			String viewpwd = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("view_pwd"));
			String snapshot = returnCursor.getString(returnCursor
					.getColumnIndexOrThrow("snapshot"));

			MonitorInfo monitorInfo = new MonitorInfo(userid, uid, name,
					devname, devpwd, viewacc, viewpwd);
			monitorInfo.Snapshot = snapshot;
			list.add(monitorInfo);

			returnCursor.moveToNext();
		}

		returnCursor.close();
		dbHelper.close();

		return list;
	}

	public static boolean deleteMonitorInfoTable(Context context,
			MonitorInfo monitorInfo) {

		LzDBHelper dbHelper = initUserDB(context);

		String uid = monitorInfo.UID;
		String userid = monitorInfo.UserId;

		boolean ret = dbHelper.delete(mMonitorInfoTbl,
				"dev_uid=? and user_id=?", new String[] { uid, userid });

		dbHelper.close();

		return ret;
	}
}
