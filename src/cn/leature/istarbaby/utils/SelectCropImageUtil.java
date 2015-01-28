/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: SelectCropImageUtil.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.utils
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-7-1 下午4:48:58
 * @version: V1.0  
 */
package cn.leature.istarbaby.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;

/**
 * @ClassName: SelectCropImageUtil
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-7-1 下午4:48:58
 */
public class SelectCropImageUtil
{
	public final static int SELECT_CROP_IMAGE = 9;

	public static void getCropImage(Activity activity, int aspectX,
			int aspectY, int outputX, int outputY)
	{

		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", aspectX);
		intent.putExtra("aspectY", aspectY);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
		intent.putExtra("noFaceDetection", false); // no face detection

		activity.startActivityForResult(intent, SELECT_CROP_IMAGE);
	}

	public static void getCropRectImage(Activity activity, int outputWH)
	{

		// 截取正方形图片
		SelectCropImageUtil.getCropImage(activity, 1, 1, outputWH, outputWH);
	}
}
