/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: HttpGetUtil.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.network
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-7-2 下午2:58:13
 * @version: V1.0  
 */
package cn.leature.istarbaby.network;

import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.leature.istarbaby.utils.InputStreamTransferUtil;

/**
 * @ClassName: HttpGetUtil
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-7-2 下午2:58:13
 */
public class HttpGetUtil {

	private RequestGetDoneCallback mRequestWithGetDone;

	public static final int HTTP_GET_DONE = 99;
	public static final int HTTP_GET_ERROR = -99;

	public HttpGetUtil(RequestGetDoneCallback requestWithGetDone) {
		mRequestWithGetDone = requestWithGetDone;
	}

	private void requestWithHttpGet(String requestUrl,
			Map<String, String> params) {
		int resultCode = 0;
		String result = "";

		try {

			HttpResponse response = HttpClientUtil.executeWithGet(requestUrl,
					params);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				resultCode = HTTP_GET_DONE;
				result = InputStreamTransferUtil.inputStream2String(response
						.getEntity().getContent());
			} else {
				resultCode = HTTP_GET_ERROR;
			}

		} catch (Exception e) {
			e.printStackTrace();

			resultCode = HTTP_GET_ERROR;
		} finally {
			// 返回结果
			sendMessage(resultCode, result);
		}
	}


	private void doCallBack(String result) {

		Log.i("HttpGetUtil", "[doCallBack]result:" + result);

		// 通过回调函数，返回结果
		mRequestWithGetDone.requestWithGetDone(result);

	}

	public void execute(final String requestUrl,
			final Map<String, String> params) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				requestWithHttpGet(requestUrl, params);
			}
		}).start();

	}

	private void sendMessage(int responseCode, String responseMessage) {
		Message msg = Message.obtain();

		msg.arg1 = responseCode;
		msg.obj = responseMessage;
		msg.what = responseCode;

		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HTTP_GET_DONE:
				doCallBack(msg.obj.toString());
				break;
			default:
				// 错误处理
				doCallBack(null);
				break;
			}

			super.handleMessage(msg);
		}
	};

	// 回调接口
	public interface RequestGetDoneCallback {
		public void requestWithGetDone(String result);
	}
}
