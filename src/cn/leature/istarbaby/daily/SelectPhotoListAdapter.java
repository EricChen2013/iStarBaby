/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: SelectPhotoListAdapter.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.daily
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午3:56:31
 * @version: V1.0  
 */
package cn.leature.istarbaby.daily;

/**
 * @ClassName: SelectPhotoListAdapter
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午3:56:31
 */
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.utils.LzViewHolder;
import cn.leature.istarbaby.utils.SelectPhotosCallBack;
import cn.leature.istarbaby.utils.SelelctPhotosUtils;

public class SelectPhotoListAdapter extends BaseAdapter {

	// 常量
	public static final String cFilePathName = "FilePathName";
	public static final String cFileCount = "FileCounts";
	public static final String cImgFileName = "ImgFileName";

	private Context mContext;
	private List<HashMap<String, String>> mListData;

	private SelelctPhotosUtils mPhotosUtils;

	private Bitmap[] mBitmaps;

	public SelectPhotoListAdapter(Context context,
			List<HashMap<String, String>> listdata) {
		this.mContext = context;
		this.mListData = listdata;

		mBitmaps = new Bitmap[listdata.size()];
		mPhotosUtils = new SelelctPhotosUtils(context);
	}

	@Override
	public int getCount() {
		return mListData.size();
	}

	@Override
	public Object getItem(int position) {
		return mListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.select_photo_list_adapter, null);

		ImageView photo_imgview = LzViewHolder.get(convertView,
				R.id.sel_photolist_imgview);
		TextView filePathName_textView = LzViewHolder.get(convertView,
				R.id.filepathname_textview);
		TextView fileCount_textview = LzViewHolder.get(convertView,
				R.id.filecount_textview);

		filePathName_textView.setText(mListData.get(position)
				.get(cFilePathName));
		fileCount_textview.setText(mListData.get(position).get(cFileCount));
		if (mBitmaps[position] == null) {
			mPhotosUtils.imgExcute(photo_imgview, new SelectPhotosCallBack() {
				@Override
				public void resultImgCall(ImageView imageView, Bitmap bitmap) {
					mBitmaps[position] = bitmap;
					imageView.setImageBitmap(bitmap);
				}
			}, mListData.get(position).get(cImgFileName));
		} else {
			photo_imgview.setImageBitmap(mBitmaps[position]);
		}

		return convertView;

	}
}
