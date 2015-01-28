/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: EncADPCM.java
 * @Prject: iStarBaby
 * @Package: com.leature.ipcam.util
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-9-15 下午3:00:01
 * @version: V1.0  
 */
package com.encoder.util;

/**
 * @ClassName: EncADPCM
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-9-15 下午3:00:01
 */

public class EncADPCM {
	static {
		try {
			System.loadLibrary("ADPCMAndroid");
		} catch (UnsatisfiedLinkError ule) {
			System.out.println("loadLibrary(ADPCMAndroid)," + ule.getMessage());
		}
	}

	public static native int ResetEncoder();

	public static native int Encode(byte[] paramArrayOfByte1, int paramInt,
			byte[] paramArrayOfByte2);
}
