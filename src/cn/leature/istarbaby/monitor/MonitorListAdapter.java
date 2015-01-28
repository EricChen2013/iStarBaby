/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: MonitorListAdapter.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.monitor
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-9-15 上午10:47:49
 * @version: V1.0  
 */
package cn.leature.istarbaby.monitor;

import java.util.List;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.utils.LzViewHolder;
import cn.leature.istarbaby.utils.SaveBitmapFile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @ClassName: MonitorListAdapter
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-9-15 上午10:47:49
 */
public class MonitorListAdapter extends ArrayAdapter<String> {

	private Context mContext;
	private List<MonitorInfo> mListItems;

	private boolean isEditMode = false;

	/**
	 * @Title:MonitorListAdapter
	 * @Description:TODO
	 * @param context
	 * @param resource
	 * @param objects
	 */
	public MonitorListAdapter(Context context, int resource,
			List<MonitorInfo> list) {
		super(context, resource);

		this.mContext = context;
		this.mListItems = list;
	}

	public void setEditMode(boolean isEditMode) {
		this.isEditMode = isEditMode;
	}

	@Override
	public int getCount() {
		return mListItems.size();
	}

	@Override
	public String getItem(int position) {
		return mListItems.get(position).UID;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		TextView textName, textUid, textStatus;
		ImageView imageLastFrame;
		ImageView imageMore;

		final MonitorInfo monitorInfo = mListItems.get(position);

		if (monitorInfo.UID.length() > 0) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.monitor_list_item, null);

			imageLastFrame = LzViewHolder.get(convertView,
					R.id.monitor_list_snapshot);
			if (monitorInfo.Snapshot.length() > 0) {
				imageLastFrame.setImageBitmap(SaveBitmapFile
						.loadBitmapFromFile(monitorInfo.Snapshot));
			}

			textName = LzViewHolder.get(convertView, R.id.monitor_list_name);
			textName.setText(monitorInfo.NickName);

			textStatus = LzViewHolder
					.get(convertView, R.id.monitor_list_status);
			textStatus.setText(monitorInfo.Status);

			textUid = LzViewHolder.get(convertView, R.id.monitor_list_uid);
			textUid.setText(monitorInfo.UID);

			imageMore = LzViewHolder.get(convertView, R.id.monitor_list_more);
			imageMore.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isEditMode) {
						// 执行删除
						onMonitorDeleteListener
								.onMonitorDeleteDone(monitorInfo);
					} else {
						// 执行设置
						onMonitorDeleteListener
								.onMonitorSettingDone(monitorInfo);
					}

				}
			});

			if (isEditMode) {
				imageMore.setImageResource(R.drawable.monitor_delete);
			} else {
				imageMore.setImageResource(R.drawable.monitor_settings);
			}
		} else {
			// UID为空的时候， 表示 新增摄像机
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.monitor_list_item_add, null);

			textName = LzViewHolder.get(convertView, R.id.monitor_list_additem);
			textName.setText(monitorInfo.NickName);
		}

		return convertView;
	}

	public static interface OnMonitorDeleteListener {
		void onMonitorDeleteDone(MonitorInfo monitorInfo);

		void onMonitorSettingDone(MonitorInfo monitorInfo);
	}

	private OnMonitorDeleteListener onMonitorDeleteListener;

	public void setOnMonitorDeleteListener(
			OnMonitorDeleteListener OnMonitorDeleteListener) {
		this.onMonitorDeleteListener = OnMonitorDeleteListener;
	}
}
