package cn.leature.istarbaby.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceInfo
{

	// 是否开启后台服务：
	public static final String PreferenceFileName = "Preinfo";

	public PreferenceInfo()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * 获取配置
	 */

	public static boolean get(Context context, String name, boolean defaultValue)
	{
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean value = prefs.getBoolean(name, defaultValue);
		return value;
	}

	/**
	 * 保存用户配置
	 */

	public static boolean set(Context context, String name, boolean value)
	{
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean(name, value);
		return editor.commit(); // 提交
	}

//	/**
//	 * 获取配置
//	 */
//	public static boolean getisStarServer(Context context, boolean devalue)
//	{
//		SharedPreferences preferences = context.getSharedPreferences(
//				PreferenceFileName, Context.MODE_PRIVATE);
//
//		boolean value = preferences.getBoolean("isServer", devalue);
//		return value;
//	}
//
//	/**
//	 * 保存用户配置
//	 */
//	public static boolean setisStarServer(Context context, boolean value)
//	{
//		SharedPreferences preferences = context.getSharedPreferences(
//				PreferenceFileName, Context.MODE_PRIVATE);
//		Editor editor = preferences.edit();
//		editor.putBoolean("isServer", value);
//
//		return editor.commit();
//	}

}
