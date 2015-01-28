/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: SelectPhotosCallBack.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.utils
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午2:50:21
 * @version: V1.0  
 */
package cn.leature.istarbaby.utils;

/**
 * @ClassName: SelectPhotosCallBack
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午2:50:21
 */

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface SelectPhotosCallBack
{
	public void resultImgCall(ImageView imageView, Bitmap bitmap);
}
