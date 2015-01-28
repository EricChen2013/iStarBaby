/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: ImageDownloadTask.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.network
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-28 下午2:31:01
 * @version: V1.0  
 */
package cn.leature.istarbaby.network;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * @ClassName: ImageDownloadTask
 * @Description: 后台执行下载图片（如头像等小图片）
 * @author: Administrator
 * @date: 2014-6-28 下午2:31:01
 */
public class ImageDownloadTask extends AsyncTask<Object, Object, Bitmap> {

	private ImageDoneCallback mImageDoneCallback;

	public ImageDownloadTask(ImageDoneCallback imageDoneCallback) {
		mImageDoneCallback = imageDoneCallback;
	}

	@Override
	protected Bitmap doInBackground(Object... params) {

		Bitmap bmp = loadImage((String) params[0]);

		return bmp;
	}

	@Override
	protected void onPostExecute(Bitmap result) {

		// 通过回调函数，返回结果
		mImageDoneCallback.imageLoaded(result);

		super.onPostExecute(result);
	}

	private Bitmap loadImage(String imagePath) {

		if ((imagePath == null) || imagePath.equals("")) {
			return null;
		}

		return HttpPostUtil.getBitmapWithPath(imagePath, 0, 0);
	}

	// 回调接口
	public interface ImageDoneCallback {
		public void imageLoaded(Bitmap result);
	}
}
