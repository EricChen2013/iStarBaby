/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: AsyncImageLoader.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.network
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 上午11:32:26
 * @version: V1.0  
 */
package cn.leature.istarbaby.network;

/**
 * @ClassName: AsyncImageLoader
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 上午11:32:26
 */

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {

	private int resizeImageWidth, resizeImageHeight;

	public AsyncImageLoader() {

	}
	public int getResizeImageWidth() {
		return resizeImageWidth;
	}

	public void setResizeImageWidth(int resizeImageWidth) {
		this.resizeImageWidth = resizeImageWidth;
	}

	public int getResizeImageHeight() {
		return resizeImageHeight;
	}

	public void setResizeImageHeight(int resizeImageHeight) {
		this.resizeImageHeight = resizeImageHeight;
	}

	public void loadImageBitmap(final String imageUrl,
			final ImageCallback imageCallback) {

		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
			}
		};
		// 建立新一个新的线程下载图片
		new Thread() {
			@Override
			public void run() {
				Bitmap data = loadImageFromUrl(imageUrl);
				Message message = handler.obtainMessage(0, data);
				handler.sendMessage(message);
			}
		}.start();

	}

	private Bitmap loadImageFromUrl(String urlPath) {
		if ((urlPath == null) || urlPath.equals("")) {
			return null;
		}

		return HttpPostUtil.getBitmapWithPath(urlPath, resizeImageWidth,
				resizeImageHeight);
	}

	// 回调接口
	public interface ImageCallback {
		public void imageLoaded(Bitmap data, String imageUrl);
	}
}
