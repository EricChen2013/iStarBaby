/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: HttpUtil.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.network
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 上午11:06:09
 * @version: V1.0  
 */
package cn.leature.istarbaby.network;

/**
 * @ClassName: HttpUtil
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 上午11:06:09
 */

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.utils.ResizeImage;

public class HttpPostUtil
{

	private static HttpPostUtil httpUtil;

	public static final int POST_SUCCESS_CODE = 1;

	public HttpPostUtil()
	{
		super();
	}

	public static HttpPostUtil getInstance()
	{
		if (httpUtil == null)
		{
			httpUtil = new HttpPostUtil();
		}

		return httpUtil;
	}

	public void sendPostMessage(final String requestUrl,
			final Map<String, String> param)
	{
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				toSendPostMessage(requestUrl, param);
			}
		}).start();
	}

	private void toSendPostMessage(String requestUrl, Map<String, String> param)
	{
		String responseResult = "";
		int responseCode = 0;

		try
		{
			HttpResponse httpResponse = HttpClientUtil.executeWithPost(
					requestUrl, param);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				// 使用getEntity方法获得返回结果
				responseResult = EntityUtils.toString(httpResponse.getEntity());
				responseCode = POST_SUCCESS_CODE;
			}
			else
			{
				responseResult = "网络异常，请确定您当前网络。";
			}
		}
		catch (ConnectTimeoutException e)
		{
			e.printStackTrace();

			responseResult = "连接超时，请检查网络连接状况。";
		}
		catch (Exception e)
		{
			e.printStackTrace();

			responseResult = "网络异常，请确定您当前网络。";
		}

		// 返回结果
		sendMessage(responseCode, responseResult);
	}

	private void sendMessage(int responseCode, String responseMessage)
	{
		Log.i("HttpPostUtil", "[sendMessage]code:" + responseCode + ", result:"
				+ responseMessage);

		onPostProcessListener.onPostDone(responseCode, responseMessage);
	}

	public static interface OnPostProcessListener
	{
		void onPostDone(int responseCode, String responseMessage);

	}

	private OnPostProcessListener onPostProcessListener;

	public void setOnPostProcessListener(
			OnPostProcessListener onPostProcessListener)
	{
		this.onPostProcessListener = onPostProcessListener;
	}

	public static Bitmap getBitmapWithPath(String imagePath, int width,
			int height)
	{
		Bitmap bitmap = null;

		InputStream inputStream = null;

		try
		{

			HttpResponse response = HttpClientUtil.executeWithGet(imagePath,
					null);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				inputStream = response.getEntity().getContent();

				byte[] buffer = new byte[1024];
				int len = -1;
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				while ((len = inputStream.read(buffer)) != -1)
				{
					outputStream.write(buffer, 0, len);
				}
				byte[] result = outputStream.toByteArray();

				if (width * height == 0)
				{
					// 原始尺寸
					bitmap = BitmapFactory.decodeByteArray(result, 0,
							result.length);
				}
				else
				{
					// 图片缩放
					bitmap = ResizeImage.getBitmapFromByteArray(result, width,
							height);
				}
			}

			if (inputStream != null)
			{
				inputStream.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		catch (OutOfMemoryError error)
		{
			error.printStackTrace();
		}

		return bitmap;
	}

	public void toErrorMessage(Context context, String msgString,
			LzProgressDialog progressDialog)
	{
		progressDialog.dismiss();

		Toast.makeText(context, msgString, Toast.LENGTH_LONG).show();
	}
}
