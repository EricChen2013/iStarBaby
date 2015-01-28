/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: Glog.java
 * @Prject: iStarBaby
 * @Package: com.tutk.Logger
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-9-15 下午3:17:28
 * @version: V1.0  
 */
package cn.leature.istarbaby.utils;

/**
 * @ClassName: Glog
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-9-15 下午3:17:28
 */
import android.util.Log;

public class LcLog {

	public static void d(String tag, String message) {
		if (ConstantDef.IS_DEBUG_MODE) {
			Log.d(tag, message);
		}
	}

	public static void d(String tag, String message, Throwable tr) {
		if (ConstantDef.IS_DEBUG_MODE) {
			Log.d(tag, message, tr);
		}
	}

	public static void e(String tag, String message) {
		if (ConstantDef.IS_DEBUG_MODE) {
			Log.e(tag, message);
		}
	}

	public static void e(String tag, String message, Throwable tr) {
		if (ConstantDef.IS_DEBUG_MODE) {
			Log.e(tag, message, tr);
		}
	}

	public static void i(String tag, String message) {
		if (ConstantDef.IS_DEBUG_MODE) {
			Log.i(tag, message);
		}
	}

	public static void i(String tag, String message, Throwable tr) {
		if (ConstantDef.IS_DEBUG_MODE) {
			Log.i(tag, message, tr);
		}
	}

	public static void v(String tag, String message) {
		if (ConstantDef.IS_DEBUG_MODE) {
			Log.v(tag, message);
		}
	}

	public static void v(String tag, String message, Throwable tr) {
		if (ConstantDef.IS_DEBUG_MODE) {
			Log.v(tag, message, tr);
		}
	}

	public static void w(String tag, String message) {
		if (ConstantDef.IS_DEBUG_MODE) {
			Log.w(tag, message);
		}
	}

	public static void w(String tag, String message, Throwable tr) {
		if (ConstantDef.IS_DEBUG_MODE) {
			Log.w(tag, message, tr);
		}
	}
}
