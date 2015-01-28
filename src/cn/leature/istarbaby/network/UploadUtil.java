/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: UploadUtil.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.network
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午2:46:19
 * @version: V1.0  
 */
package cn.leature.istarbaby.network;

/**
 * @ClassName: UploadUtil
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午2:46:19
 */

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import android.util.Log;
import cn.leature.istarbaby.utils.InputStreamTransferUtil;

public class UploadUtil {

	private static UploadUtil uploadUtil;

	private static final String CHARSET = "utf-8"; // 设置编码
	private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
	private static final String LINE_END = "\r\n";
	private static final String PREFIX = "--";
	private static final String BOUNDARY = UUID.randomUUID().toString(); // 边界标识，随机生成

	private int readTimeout = 10 * 1000; // 读取超时时间
	private int connectTimeout = 10 * 1000; // 连接超时时间

	/***
	 * 上传成功
	 */
	public static final int UPLOAD_SUCCESS_CODE = 1;

	public static final int UPLOAD_FAILED_CODE = -1;

	// 请求使用多长时间
	private static int requestTime = 0;

	public UploadUtil() {
		// TODO Auto-generated constructor stub
	}

	public static UploadUtil getInstance() {
		if (uploadUtil == null) {
			uploadUtil = new UploadUtil();
		}
		return uploadUtil;
	}

	public void uploadFile(String filePath, String fileKey, String RequestURL,
			Map<String, String> param) {

		if ((filePath == null) || filePath.equals("")) {
			sendMessage(UPLOAD_FAILED_CODE, "文件路径不存在。");
			return;
		}

		try {
			File file = new File(filePath);
			if (file == null || (!file.exists())) {
				sendMessage(UPLOAD_FAILED_CODE, "文件不存在。");
				return;
			}

			uploadFile(file, fileKey, RequestURL, param);
		} catch (Exception e) {
			sendMessage(UPLOAD_FAILED_CODE, "文件处理出错。");
			e.printStackTrace();
		}

		return;
	}

	private void uploadFile(final File file, final String fileKey,
			final String requestURL, final Map<String, String> param) {

		new Thread(new Runnable() { // 开启线程上传文件

					@Override
					public void run() {
						toUploadFile(file, fileKey, requestURL, param);
					}
				}).start();
	}

	private void toUploadFile(File file, String fileKey, String requestUrl,
			Map<String, String> param) {

		int responseCode = 0;
		String responseResult = "";

		long reqTime = System.currentTimeMillis();
		long resTime = 0;

		try {
			// 拼接服务器URL，创建URL对象
			URL url = new URL(HttpClientUtil.SERVER_PATH + requestUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setReadTimeout(readTimeout);
			httpURLConnection.setConnectTimeout(connectTimeout);
			httpURLConnection.setDoInput(true); // 允许输入流
			httpURLConnection.setDoOutput(true); // 允许输出流
			httpURLConnection.setUseCaches(false); // 不允许使用缓存
			httpURLConnection.setRequestMethod("POST"); // 请求方式：POST

			httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			httpURLConnection.setRequestProperty("contentType", "utf-8");

			httpURLConnection.setRequestProperty("Charset", CHARSET);
			httpURLConnection.setRequestProperty("connection", "keep-alive");
			httpURLConnection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			httpURLConnection.setRequestProperty("Content-Type", CONTENT_TYPE
					+ ";boundary=" + BOUNDARY);

			DataOutputStream dos = new DataOutputStream(
					httpURLConnection.getOutputStream());

			StringBuffer strBuffer = new StringBuffer();

			strBuffer.append(PREFIX).append(BOUNDARY).append(PREFIX)
					.append(LINE_END).append(LINE_END);
			strBuffer.append("Content-Disposition: form-data; params={");

			// 拼接参数
			if ((param != null) && (param.size() > 0)) {
				Iterator<String> it = param.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					String value = param.get(key);

					strBuffer.append("\"").append(key).append("\":\"");
					strBuffer.append(value).append("\",");
				}
			}

			/**
			 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件 filename是文件的名字，包含后缀名的
			 * 比如:abc.png
			 */
			// 只需要文件的扩展名
			String uploadName = "";
			String[] nameArray = file.getName().toString().split("\\."); // split中[.]是转义字符
			if (nameArray.length <= 1) {
				uploadName = "upload.png"; // 默认文件名
			} else {
				uploadName = "upload." + nameArray[nameArray.length - 1];
			}

			strBuffer.append("\"filetype\":\"" + fileKey + "\",\"filename\":\""
					+ uploadName + "\"}" + LINE_END);
			strBuffer.append(LINE_END);
			strBuffer.append("Content-Type: image/pjpeg" + LINE_END); // 这里配置的Content-type很重要的，用于服务器端辨别文件的类型的
			strBuffer.append(LINE_END);

			dos.write(strBuffer.toString().getBytes());
			strBuffer = null;

			// 上传文件
			InputStream is = new FileInputStream(file);
			onUploadProcessListener.initUpload((int) file.length());

			byte[] bytes = new byte[1024];
			int len = 0;
			long curLen = 0;
			while ((len = is.read(bytes)) != -1) {
				curLen += len;
				dos.write(bytes, 0, len);
				onUploadProcessListener.onUploadProcess(curLen);
			}
			is.close();

			dos.write(LINE_END.getBytes());
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
					.getBytes();
			dos.write(end_data);
			dos.flush();
			dos.close();

			// 取得服务器端响应结果
			int res = httpURLConnection.getResponseCode();
			resTime = System.currentTimeMillis();
			requestTime = (int) ((resTime - reqTime) / 1000);

			if (res == 200) {
				InputStream input = httpURLConnection.getInputStream();

				String[] strArray = InputStreamTransferUtil.inputStream2String(
						input).split(",");
				// 上传字节数
				if (Long.parseLong(strArray[0]) == curLen) {
					// 上传完整文件
					responseCode = UPLOAD_SUCCESS_CODE;
					responseResult = strArray[1];
				} else {
					// 上传不完整
					responseResult = "文件上传失败！";
				}

			} else {
				responseResult = "文件上传处理失败！";
			}
		} catch (Exception e) {
			responseResult = "文件上传失败：error=" + e.getMessage();
			e.printStackTrace();
		}

		sendMessage(responseCode, responseResult);
	}

	private void sendMessage(int responseCode, String responseMessage) {
		Log.i("UploadUtil", "[sendMessage]code:" + responseCode + ", result:"
				+ responseMessage);

		onUploadProcessListener.onUploadDone(responseCode, responseMessage);
	}

	public static interface OnUploadProcessListener {
		void onUploadDone(int responseCode, String responseMessage);

		void onUploadProcess(long uploadSize);

		void initUpload(int fileSize);
	}

	private OnUploadProcessListener onUploadProcessListener;

	public void setOnUploadProcessListener(
			OnUploadProcessListener onUploadProcessListener) {
		this.onUploadProcessListener = onUploadProcessListener;
	}

	public static int getRequestTime() {
		return requestTime;
	}
}
