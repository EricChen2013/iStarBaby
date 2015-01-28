package cn.leature.istarbaby.common;

import cn.leature.istarbaby.utils.DensityUtil;
import android.content.Context;

import android.view.ViewGroup;

import android.widget.ListAdapter;

import android.widget.ListView;

public class ListHeightUtils {

	public static void setListViewHeightBasedOnChildren(ListView listView,
			Context context) {

		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {

			// pre-condition

			return;

		}
		int totalHeight = 0;

		for (int i = 0; i < listAdapter.getCount(); i++) {

			totalHeight += DensityUtil.dip2px(context, 40);

		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();

		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		listView.setLayoutParams(params);

	}

}
