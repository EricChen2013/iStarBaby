/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: LzViewHolder.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.utils
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-23 下午3:00:10
 * @version: V1.0  
 */
package cn.leature.istarbaby.utils;

import android.util.SparseArray;
import android.view.View;

/**
 * @ClassName: LzViewHolder
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-23 下午3:00:10
 */
public class LzViewHolder
{
	// I added a generic return type to reduce the casting noise in client code
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id)
	{
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null)
		{
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null)
		{
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
