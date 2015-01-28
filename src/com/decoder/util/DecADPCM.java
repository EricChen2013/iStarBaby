/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: DecADPCM.java
 * @Prject: iStarBaby
 * @Package: com.leature.ipcam.util
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-9-15 下午3:02:06
 * @version: V1.0  
 */
package com.decoder.util;

/**
 * @ClassName: DecADPCM
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-9-15 下午3:02:06
 */

public class DecADPCM {
	static {
		try {
			System.loadLibrary("ADPCMAndroid");
		} catch (UnsatisfiedLinkError ule) {
			System.out.println("loadLibrary(ADPCMAndroid)," + ule.getMessage());
		}
	}

	public static native int ResetDecoder();

	public static native int Decode(byte[] paramArrayOfByte1, int paramInt,
			byte[] paramArrayOfByte2);
}
