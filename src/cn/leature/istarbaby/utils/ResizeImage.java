/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: ResizeImage.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.utils
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 上午11:08:13
 * @version: V1.0  
 */
package cn.leature.istarbaby.utils;

/**
 * @ClassName: ResizeImage
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 上午11:08:13
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ResizeImage
{

	public ResizeImage()
	{
		// TODO Auto-generated constructor stub
	}

	public static Bitmap getBitmapFromByteArray(byte[] data, int width,
			int height)
	{
		// data或宽高为0时，返回null
		if ((data == null) || (data.length == 0) || (width * height == 0))
		{
			return null;
		}

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, opts);

		// 计算图片缩放比例
		final int minSideLength = Math.min(width, height);
		int scale = computeSampleSize(opts, minSideLength, width * height);

		opts.inSampleSize = scale;
		opts.inJustDecodeBounds = false;
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inInputShareable = true;
		opts.inPurgeable = true;

		try
		{
			Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length,
					opts);

			if (scale < 2)
			{
				// 图片真实大小比指定小，无需拉伸
				return bm;
			}

			float scaleWidht = (float) width / bm.getWidth();
			float scaleHeight = (float) height / bm.getHeight();
			float scaleWH = Math.min(scaleWidht, scaleHeight);
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWH, scaleWH);
			Bitmap bm2 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);

			return bm2;
		}
		catch (OutOfMemoryError e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels)
	{
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8)
		{
			roundedSize = 1;
			while (roundedSize < initialSize)
			{
				roundedSize <<= 1;
			}
		}
		else
		{
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels)
	{
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound)
		{
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1))
		{
			return 1;
		}
		else if (minSideLength == -1)
		{
			return lowerBound;
		}
		else
		{
			return upperBound;
		}
	}

	/**
	 * 按宽高比例裁剪图片
	 */
	public static Bitmap resizeBitmapWithRadio(Bitmap data, int widthRadio,
			int heightRadio)
	{
		// data或宽高为0时，返回null
		if ((data == null) || (widthRadio * heightRadio == 0))
		{
			return null;
		}

		int width = data.getWidth();
		int height = data.getHeight();

		if (width < widthRadio || height < heightRadio)
		{
			// 原图片太小，无法裁剪
			return data;
		}

		// 裁剪后图片宽高
		int outWidth = 0;
		int outHeight = 0;
		// 图片裁剪位置
		int outX = 0;
		int outY = 0;

		if (widthRadio == heightRadio)
		{
			// 裁剪出正方形图片
			if (width >= height)
			{
				// 原图宽 ≥ 高，横向裁剪
				outWidth = height;
				outHeight = height;

				outX = (width - outWidth) / 2;
				outY = 0;
			}
			else
			{
				// 原图宽 ＜ 高，纵向裁剪
				outWidth = width;
				outHeight = width;

				outX = 0;
				outY = (height - outHeight) / 2;
			}
		}
		else if (widthRadio > heightRadio)
		{
			// 裁剪出长方形图片（宽＞高）
			outWidth = height * widthRadio / heightRadio;
			if (outWidth > width)
			{
				outWidth = width;
				outHeight = width * heightRadio / widthRadio;

				outX = 0;
				outY = (height - outHeight) / 2;
			}
			else
			{
				outHeight = height;

				outX = (width - outWidth) / 2;
				outY = 0;
			}
		}
		else
		{
			// 裁剪出长方形图片（宽＜高）
			outWidth = height * widthRadio / heightRadio;
			if (outWidth > width)
			{
				outWidth = width;
				outHeight = width * heightRadio / widthRadio;

				outX = 0;
				outY = (height - outHeight) / 2;
			}
			else
			{
				outHeight = height;

				outX = (width - outWidth) / 2;
				outY = 0;
			}
		}

		return Bitmap.createBitmap(data, outX, outY, outWidth, outHeight, null,
				true);
	}

	public static Bitmap getBitmapFromFileName(String fileName, int width,
			int height)
	{
		if ((fileName == null) || fileName.equals(""))
		{
			return null;
		}

		try
		{
			File file = new File(fileName);
			if (file == null || (!file.exists()))
			{
				return null;
			}

			FileInputStream fileInputStream = new FileInputStream(file);

			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fileInputStream.read(buffer)) > 0)
			{
				byteArray.write(buffer, 0, len);
			}
			fileInputStream.close();

			return getBitmapFromByteArray(byteArray.toByteArray(), width,
					height);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
