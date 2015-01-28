/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: ChildAlbumTabsFragment.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.child
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-12 下午2:24:24
 * @version: V1.0  
 */
package cn.leature.istarbaby.child;

/**
 * @ClassName: ChildAlbumTabsFragment
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-12 下午2:24:24
 */

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.daily.DailyDetailActivity;
import cn.leature.istarbaby.daily.DailyPostActivity;
import cn.leature.istarbaby.domain.ChildAlbumInfo;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DensityUtil;
import cn.leature.istarbaby.utils.LzViewHolder;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChildAlbumTabsFragment extends Fragment implements
		OnItemClickListener {

	// Activity跳转用
	public static final int REQUEST_CODE_DAILY_POST = 101;

	private ArrayList<ChildAlbumInfo> mData;
	private GridView mAlbumgridview;

	private Context mContext;
	// 图片的宽高
	private int photoWidthHeight;
	// 图片列表的列数
	private final int photoListColNum = 3;
	// 图片列表的图片间隔
	private final int photoListCellSpace = 8;
	// 图片列表和边框的间距
	private final int photoListMargin = 8;
	// 图片列表和边框的间距
	private final int gridViewMarginBottom = 48;

	private Bundle mBundleposition;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	private String tabpage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View inflate = inflater
				.inflate(R.layout.child_album_tab_gridview, null);
		mAlbumgridview = (GridView) inflate.findViewById(R.id.album_gridview);
		mAlbumgridview.setOnItemClickListener(this);
		mAlbumgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mContext = getActivity().getApplicationContext();

		mBundleposition = getActivity().getIntent().getExtras();

		// 计算图片大小
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		photoWidthHeight = dm.widthPixels
				- DensityUtil.dip2px(mContext, photoListMargin) * 2
				- DensityUtil.dip2px(mContext, photoListCellSpace)
				* (photoListColNum - 1);
		photoWidthHeight /= photoListColNum;
		if (mData != null) {
			AlbumImageAdapter imageAdapter = new AlbumImageAdapter();
			mAlbumgridview.setAdapter(imageAdapter);
		}
		// 初始化
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.nophoto)
				.showImageForEmptyUri(R.drawable.nophoto)
				.showImageOnFail(R.drawable.nophoto).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		return inflate;
	}

	public void setlist(ArrayList<ChildAlbumInfo> data) {
		this.mData = data;
	}

	public class AlbumImageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			class Holder {
				private ImageView imageView;
				private TextView textName;

			}
			final Holder holder;
			holder = new Holder();
			convertView = getActivity().getLayoutInflater().inflate(
					R.layout.child_album_tab_fragment, null);

			holder.imageView = LzViewHolder
					.get(convertView, R.id.tab_add_image);
			LayoutParams lParams = holder.imageView.getLayoutParams();
			lParams.width = photoWidthHeight;
			lParams.height = photoWidthHeight;
			holder.imageView.setLayoutParams(lParams);
			holder.textName = LzViewHolder.get(convertView, R.id.tab_add_text);
			if (position < mData.size() - 1) {
				lParams = holder.textName.getLayoutParams();
				lParams.width = photoWidthHeight;
				holder.textName.setLayoutParams(lParams);
			} else {
				LinearLayout.LayoutParams lParams2 = new LinearLayout.LayoutParams(
						photoWidthHeight,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lParams2.setMargins(0, DensityUtil.dip2px(mContext, 4), 0,
						DensityUtil.dip2px(mContext, gridViewMarginBottom));
				holder.textName.setLayoutParams(lParams2);
			}

			final ChildAlbumInfo info = mData.get(position);
			holder.textName.setText(info.event_name);

			if (info.photo_url.length() > 0) {
				String imageUrlPath = HttpClientUtil.SERVER_PATH
						+ info.photo_url;

				// 加载图片
				imageLoader.displayImage(imageUrlPath, holder.imageView,
						options, null);
				holder.imageView
						.setBackgroundResource(R.drawable.bg_detailpic_selector);
			} else {
				holder.imageView.setBackgroundResource(R.drawable.addpic);
				holder.imageView
						.setImageResource(R.drawable.bg_detailpic_selector_no);
			}

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {
		// 得到当前图片的名字 id
		Intent intent = new Intent();
		Bundle mBundle = new Bundle();

		ChildAlbumInfo info = mData.get(position);
		if ("".equals(info.getPhoto_url())) {
			// 新事件
			mBundle.putString(ConstantDef.ccGroup_id, info.group_id);
			mBundle.putString(ConstantDef.ccEventId, info.event_id);
			mBundle.putString(ConstantDef.ccEventName, info.event_name);
			mBundle.putString(ConstantDef.ccChildId,
					(String) mBundleposition.get(ConstantDef.ccChildId));

			intent.setClass(getActivity(), DailyPostActivity.class);

			mBundle.putString("Channel", "tab");
			mBundle.putString("tabpage", tabpage);
			intent.putExtras(mBundle);

			startActivityForResult(intent, ConstantDef.REQUEST_CODE_DAILY_POST);
			getActivity().overridePendingTransition(
					R.anim.activity_in_from_bottom,
					R.anim.activity_nothing_in_out);
		} else {
			// 已登录过事件
			mBundle.putString(ConstantDef.ccDaily_Id, info.daily_Id);

			intent.setClass(getActivity(), DailyDetailActivity.class);

			mBundle.putString("Channel", "tab");
			mBundle.putString("tabpage", tabpage);
			intent.putExtras(mBundle);

			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_DAILY_DETAIL);
			getActivity().overridePendingTransition(
					R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
		}

	}

	public void settab(String CurrentTab) {
		this.tabpage = CurrentTab;
	}
}
