package com.setting.app.friend;

import java.util.ArrayList;
import java.util.List;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.utils.LzViewHolder;

import android.R.raw;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyUserInfoAdapter extends BaseAdapter
{

	private Context context;

	private List<UserInfo> userInfos;

	public MyUserInfoAdapter(Context context, List<UserInfo> userInfos)
	{

		this.context = context;
		this.userInfos = userInfos;

	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return userInfos.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{

		convertView = LayoutInflater.from(context).inflate(
				R.layout.settingfriend_item, null);

		TextView tv_catalog = LzViewHolder.get(convertView, R.id.tv_catalog);
		TextView tv_nick = LzViewHolder.get(convertView, R.id.tv_nick);
		TextView tv_mobile = LzViewHolder.get(convertView, R.id.tv_mobile);
		final ImageView img_select = LzViewHolder.get(convertView,
				R.id.contactitem_avatar_iv);

		String catalog = PinyinUtils.converterToFirstSpell(
				userInfos.get(position).getUserName()).substring(0, 1);

		if (position == 0)
		{
			tv_catalog.setVisibility(View.VISIBLE);
			tv_catalog.setText(catalog);
		}
		else
		{
			String lastCatalog = PinyinUtils.converterToFirstSpell(
					userInfos.get(position - 1).getUserName()).substring(0, 1);

			if (catalog.equals(lastCatalog))
			{
				tv_catalog.setVisibility(View.GONE);

			}
			else
			{
				tv_catalog.setVisibility(View.VISIBLE);
				tv_catalog.setText(catalog);
			}
		}
		boolean isSelect = false;
		img_select.setOnClickListener(new ImgeListener(isSelect, position,
				img_select, userInfos));
		tv_nick.setText(userInfos.get(position).getUserName());
		tv_mobile.setText(userInfos.get(position).getPhoneNum());

		return convertView;
	}

	List<UserInfo> mData = new ArrayList<UserInfo>();

	class ImgeListener implements OnClickListener
	{
		private boolean isSelect;
		private int position;
		private ImageView img_select;
		private List<UserInfo> userInfos;

		ImgeListener(boolean isSelect, int position, ImageView img_select,
				List<UserInfo> userInfos)
		{
			this.isSelect = isSelect;
			this.position = position;
			this.img_select = img_select;
			this.userInfos = userInfos;
		}

		@Override
		public void onClick(View v)
		{
			isSelect = !isSelect;

			if (isSelect)
			{
				img_select.setImageResource(R.drawable.gou_selected);
				UserInfo userInfo = userInfos.get(position);
				mData.add(userInfo);
			}
			else
			{
				img_select.setImageResource(R.drawable.gou_normal);
				UserInfo userInfo = userInfos.get(position);
				mData.remove(userInfo);
			}
		}

	}

	public ArrayList<UserInfo> getselect()
	{
		return (ArrayList<UserInfo>) mData;

	}

}
