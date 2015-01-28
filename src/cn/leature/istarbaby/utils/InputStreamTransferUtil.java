/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: InputStreamUtil.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.utils
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-7-1 下午5:22:38
 * @version: V1.0  
 */
package cn.leature.istarbaby.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * @ClassName: InputStreamUtil
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-7-1 下午5:22:38
 */
public class InputStreamTransferUtil {
	/*
	 * 将byte[]转换成InputStream
	 */
	public static InputStream byte2InputStream(byte[] b) {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		return bais;
	}

	/*
	 * 将InputStream转换成byte[]
	 */
	public static byte[] inputStream2Bytes(InputStream inStream) {

		ByteArrayOutputStream swapStream = changeInputStream(inStream);

		return swapStream.toByteArray();
	}

	/**
	 * @Title: changeInputStream
	 * @Description: TODO
	 * @param inputStream
	 * @return
	 * @return: String
	 */
	public static String inputStream2String(InputStream inputStream) {

		ByteArrayOutputStream outputStream = changeInputStream(inputStream);

		return (new String(outputStream.toByteArray()));
	}

	private static ByteArrayOutputStream changeInputStream(InputStream inStream) {

		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();

		byte[] buff = new byte[1024];
		int rc = 0;
		try {
			while ((rc = inStream.read(buff, 0, buff.length)) > 0) {
				swapStream.write(buff, 0, rc);
			}

			if (inStream != null) {
				inStream.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return swapStream;
	}

	/*
	 * 将Bitmap转换成InputStream 【参数】 quality : 压缩率 例子：30 是压缩率，表示压缩70%;
	 * 如果不压缩是100，表示压缩率为0
	 */
	public static InputStream bitmap2InputStream(Bitmap bm, int quality) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, quality, baos);

		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		return is;
	}

	/*
	 * 将InputStream转换成Bitmap
	 */
	public Bitmap inputStream2Bitmap(InputStream is) {
		return BitmapFactory.decodeStream(is);
	}

	/*
	 * Drawable转换成InputStream
	 */
	public static InputStream drawable2InputStream(Drawable d, int quality) {
		Bitmap bitmap = drawable2Bitmap(d);

		return bitmap2InputStream(bitmap, quality);
	}

	/*
	 * InputStream转换成Drawable
	 */
	public Drawable inputStream2Drawable(InputStream is) {
		Bitmap bitmap = this.inputStream2Bitmap(is);

		return this.bitmap2Drawable(bitmap);
	}

	/*
	 * Drawable转换成byte[]
	 */
	public byte[] drawable2Bytes(Drawable d) {
		Bitmap bitmap = drawable2Bitmap(d);

		return this.bitmap2Bytes(bitmap);
	}

	/*
	 * byte[]转换成Drawable
	 */
	public Drawable bytes2Drawable(byte[] b) {
		Bitmap bitmap = this.bytes2Bitmap(b);

		return this.bitmap2Drawable(bitmap);
	}

	/*
	 * Bitmap转换成byte[]
	 */
	public byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

		return baos.toByteArray();
	}

	/*
	 * byte[]转换成Bitmap
	 */
	public Bitmap bytes2Bitmap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		}

		return null;
	}

	/*
	 * Drawable转换成Bitmap
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	/*
	 * Bitmap转换成Drawable
	 */
	public Drawable bitmap2Drawable(Bitmap bitmap) {
		BitmapDrawable bd = new BitmapDrawable(bitmap);

		return (Drawable) bd;
	}
}
