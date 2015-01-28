/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: SelectPhotosAdapter.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.daily
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午4:06:50
 * @version: V1.0  
 */
package cn.leature.istarbaby.daily;

/**
 * @ClassName: SelectPhotosAdapter
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午4:06:50
 */
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.utils.LzViewHolder;
import cn.leature.istarbaby.utils.SelectPhotosCallBack;
import cn.leature.istarbaby.utils.SelelctPhotosUtils;

public class SelectPhotosAdapter extends BaseAdapter {

	private Context mContext;

	private List<String> mFileContentList;

	private OnItemClickPhotosClass onItemClickPhotosClass;

	private SelelctPhotosUtils mPhotosUtils;

	private Bitmap mBitmaps[];

	// 图片的宽高
	private int mPhotoWidthHeight;

	public SelectPhotosAdapter(Context context, int wh,
			List<String> fileContent,
			OnItemClickPhotosClass onItemClickPhotosClass) {

		this.mContext = context;
		this.mPhotoWidthHeight = wh;
		this.mFileContentList = fileContent;
		this.onItemClickPhotosClass = onItemClickPhotosClass;

		mPhotosUtils = new SelelctPhotosUtils(context);

		mBitmaps = new Bitmap[mFileContentList.size()];
	}

	@Override
	public int getCount() {
		return mFileContentList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFileContentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.select_photos_item, null);

		ImageView sel_photes_imageView = LzViewHolder.get(convertView,
				R.id.sel_photos_imgview);

		CheckBox sel_photes_checkBox = LzViewHolder.get(convertView,
				R.id.sel_photes_checkbox);
		onItemClickPhotosClass.checkItemSelected(position, sel_photes_checkBox);

		LayoutParams lParams = sel_photes_imageView.getLayoutParams();
		lParams.height = mPhotoWidthHeight;
		lParams.width = mPhotoWidthHeight;
		sel_photes_imageView.setLayoutParams(lParams);

		if (mBitmaps[position] == null) {
			mPhotosUtils.imgExcute(sel_photes_imageView,
					new SelectPhotosCallBackListener(position),
					mFileContentList.get(position));
		} else {
			sel_photes_imageView.setImageBitmap(mBitmaps[position]);
		}

		convertView.setOnClickListener(new OnPhotoClick(position,
				sel_photes_checkBox));

		return convertView;
	}

	public class SelectPhotosCallBackListener implements SelectPhotosCallBack {

		private int num;

		public SelectPhotosCallBackListener(int num) {
			this.num = num;
		}

		@Override
		public void resultImgCall(ImageView imageView, Bitmap bitmap) {
			mBitmaps[num] = bitmap;
			imageView.setImageBitmap(bitmap);
		}
	}

	public interface OnItemClickPhotosClass {
		public void OnItemClick(View v, int position, CheckBox checkBox);

		public void checkItemSelected(int position, CheckBox checkBox);
	}

	class OnPhotoClick implements OnClickListener {
		private int position;
		private CheckBox checkBox;

		public OnPhotoClick(int position, CheckBox checkBox) {
			this.position = position;
			this.checkBox = checkBox;
		}

		@Override
		public void onClick(View v) {
			if (mFileContentList != null && onItemClickPhotosClass != null) {
				onItemClickPhotosClass.OnItemClick(v, position, checkBox);
			}
		}
	}
}
