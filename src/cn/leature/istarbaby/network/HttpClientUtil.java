/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: HttpClientUtil.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.network
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-7-25 下午2:41:48
 * @version: V1.0  
 */
package cn.leature.istarbaby.network;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

/**
 * @ClassName: HttpClientUtil
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-7-25 下午2:41:48
 */
public class HttpClientUtil {

	// 服务器URL
	public static final String SERVER_PATH = "http://192.168.0.149/";
//	public static final String SERVER_PATH = "http://www.yuerbao.top/iStarBaby/";
	// 这个是IP地址
//	public static final String SERVER_PATH = "http://219.239.230.236/iStarBaby/";
	// 这个是测试环境
//	public static final String SERVER_PATH = "http://www.leature.cn/iStarBaby/";

	// 应该程序名
	public static final String PACKAGE_NAME = "iStarBaby.apk";
	// 微信分享地址
	public static final String SHARE_PATH = "http://www.leature.cn/m/Download.aspx";
	// APK的服务器地址(二维码地址)
	public static final String PACKAGE_SERVER_URL = "http://www.leature.cn/m/Download.aspx";
	// APK下载短缩地址（发送短信用）
	public static final String PACKAGE_SERVER_URL_MESSAGE = "http://t.cn/RzZIQvc";

	private static final int REQUEST_TIMEOUT = 10 * 1000; // 连接超时
	private static final int SO_TIMEOUT = 10 * 1000; // 请求超时(等待数据超时)
	private static final int POOL_TIMEOUT = 2 * 1000; // 连接池中取连接的超时时间

	public HttpClientUtil() {
		super();
	}

	private static HttpClient getHttpClient() {
		HttpParams httpParams = new BasicHttpParams();
		// 连接超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		// 请求超时时间
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		// 连接池中取连接的超时时间
		ConnManagerParams.setTimeout(httpParams, POOL_TIMEOUT);

		HttpClient client = new DefaultHttpClient(httpParams);

		return client;
	}

	public static HttpResponse executeWithGet(String request,
			Map<String, String> params) throws Exception {

		// 拼接参数
		String paramString = "";
		StringBuffer buffer = new StringBuffer();
		if ((params != null) && (params.size() > 0)) {
			Iterator<String> it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = params.get(key);

				buffer.append(key).append("=").append(value).append("&");
			}

			if (buffer.length() > 0) {
				paramString = "?"
						+ buffer.toString().substring(0, buffer.length() - 1);
			}
		}
		Log.i("HttpClientUtil", "[executeWithGet]URL:" + request + ", params:"
				+ paramString);

		// 拼接服务器URL，创建URL对象
		HttpGet httpGet = new HttpGet(SERVER_PATH + request + paramString);

		HttpClient client = HttpClientUtil.getHttpClient();
		HttpResponse response = client.execute(httpGet);
		return response;
	}

	public static HttpResponse executeWithPost(String request,
			Map<String, String> params) throws Exception {

		// 设置HTTP POST请求参数必须用NameValuePair对象
		// 拼接参数
		List<NameValuePair> reqParams = new ArrayList<NameValuePair>();

		if ((params != null) && (params.size() > 0)) {
			Iterator<String> it = params.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				String value = params.get(key);

				reqParams.add(new BasicNameValuePair(key, value));
			}
		}
		Log.i("HttpClientUtil", "[executeWithPost]URL:" + request + ", params:"
				+ reqParams);

		// 拼接服务器URL，创建HttpPost对象
		HttpPost httpPost = new HttpPost(SERVER_PATH + request);

		// 设置httpPost请求参数
		httpPost.setEntity(new UrlEncodedFormEntity(reqParams, HTTP.UTF_8));

		HttpClient client = HttpClientUtil.getHttpClient();
		HttpResponse response = client.execute(httpPost);

		return response;
	}
}
