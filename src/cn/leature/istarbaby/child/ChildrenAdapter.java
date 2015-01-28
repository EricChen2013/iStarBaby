/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: ChildDetailAdapter.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.child
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-15 下午4:02:19
 * @version: V1.0  
 */
package cn.leature.istarbaby.child;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import cn.leature.istarbaby.R;

/**
 * @ClassName: ChildDetailAdapter
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-15 下午4:02:19
 */
public class ChildrenAdapter extends ArrayAdapter<Object> {

	private Context mContext;

	/**
	 * @Title:ChildDetailAdapter
	 * @Description:TODO
	 * @param context
	 * @param resource
	 * @param objects
	 */

	public ChildrenAdapter(Context context, int resource) {
		super(context, resource);
		mContext = context;
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.child_detail_album, null);
		}

		return convertView;
	}

}
