/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: DailyListViewAdapter.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.daily
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 上午11:26:13
 * @version: V1.0  
 */
package cn.leature.istarbaby.daily;

/**
 * @ClassName: DailyListViewAdapter
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 上午11:26:13
 */

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.domain.DailyDetailInfo;
import cn.leature.istarbaby.domain.DailyDetailInfo.ChildDataInfo;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.slidingmenu.SlidingMenu;
import cn.leature.istarbaby.utils.DateUtilsDef;
import cn.leature.istarbaby.utils.DensityUtil;
import cn.leature.istarbaby.utils.LzViewHolder;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class DailyListViewAdapter extends ArrayAdapter<Object> implements
		OnTouchListener
{
	private Context mContext;
	// 屏幕
	private int mWidth;
	// 数据源
	private ArrayList<Object> mListData;
	private SlidingMenu mSlidingMenu;
	// 图片显示大小
	private int picWidth1, picHeight1;
	private int picWidth2, picHeight2;
	private int picWidth3, picHeight3, picHeight4;

	private HorizontalScrollView mHorizontalScrollView;
	private HorizontalScrollView mHorizontalScrollView1;
	private DisplayImageOptions options;
	DisplayImageOptions optionsForUserIcon;

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public DailyListViewAdapter(Context context, int resource,
			ArrayList<Object> list, int w, int h, SlidingMenu mSlidingMenu)
	{
		super(context, resource);
		this.mSlidingMenu = mSlidingMenu;
		this.mContext = context;
		this.mListData = list;
		this.mWidth = w;

		// 计算图片显示大小
		this.picWidth1 = mWidth - 80 * 2;
		this.picHeight1 = (int) (picWidth1 * 0.75);

		this.picWidth2 = (int) ((mWidth - DensityUtil.dip2px(context, 8) * 4) * 0.4);
		this.picHeight2 = (int) ((mWidth - DensityUtil.dip2px(context, 8) * 4) * 0.6);

		this.picWidth3 = (int) ((mWidth - DensityUtil.dip2px(context, 8) * 4) * 0.6);
		this.picHeight3 = (int) ((mWidth - DensityUtil.dip2px(context, 8) * 4) * 0.4);
		this.picHeight4 = (int) (picWidth3 - DensityUtil.dip2px(context, 8));

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.nophoto)
				.showImageForEmptyUri(R.drawable.nophoto)
				.showImageOnFail(R.drawable.nophoto).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

		optionsForUserIcon = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.noimage)
				.showImageOnFail(R.drawable.noimage)
				.resetViewBeforeLoading(false).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();
	}

	@Override
	public int getCount()
	{
		return mListData.size();
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public boolean isEnabled(int position)
	{
		if (mListData.get(position) instanceof String)
		{
			// 标题栏，不可点击
			return false;
		}

		return super.isEnabled(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		Object obj = mListData.get(position);

		if (obj instanceof String)
		{
			// 标题
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.daily_listview_header, null);

			TextView mTextDate = LzViewHolder.get(convertView,
					R.id.lview_date_header);

			// 标题设置
			mTextDate.setText(DateUtilsDef.dateFormatString(obj.toString(),
					"yyyy/MM/dd"));
		}
		else
		{
			ImageView mUserIcon;
			TextView mTextName;
			TextView mTextTimeDiff;
			TextView mTextDetail;
			ImageView mImageView1;
			ImageView mImageView2;
			ImageView mImageView3;
			TextView textPhotoCount;
			LayoutParams lParams;

			DailyDetailInfo detailInfo = (DailyDetailInfo) obj;
			// 根据图片张数，显示相应布局
			String[] imgFiles = detailInfo.getImage();
			if ("99".equals(detailInfo.getTagName()))
			{
				// 新生宝宝
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.daily_listview_detail4, null);

				// 重新设置图片大小
				mImageView1 = LzViewHolder.get(convertView, R.id.lview_img1);
				lParams = mImageView1.getLayoutParams();
				lParams.width = picWidth1 + 6;
				lParams.height = (int) (picWidth1);
				mImageView1.setLayoutParams(lParams);
				// 显示照片
				if (imgFiles.length > 0)
				{
					imageLoader.displayImage(HttpClientUtil.SERVER_PATH
							+ imgFiles[0], mImageView1, options);
				}
			}
			else if ("96".equals(detailInfo.getTagName()))
			{
				// 接种疫苗
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.daily_listview_detail_premun, null);
				mTextName = LzViewHolder.get(convertView, R.id.lview_name);
				ImageView userIcon = LzViewHolder.get(convertView,
						R.id.premun_userIcon);
				// 头像
				String userIconpath = detailInfo.getUserIcon();
				showImageWithPath(userIcon, userIconpath);

				mTextTimeDiff = LzViewHolder.get(convertView,
						R.id.lview_timediff);
				TextView childName = LzViewHolder.get(convertView,
						R.id.premun_childname);
				TextView childAge = LzViewHolder.get(convertView,
						R.id.premun_childage);
				LinearLayout premunLayout = LzViewHolder.get(convertView,
						R.id.child_premun_layout);

				mHorizontalScrollView1 = LzViewHolder.get(convertView,
						R.id.listview_horizontalscrollview1);
				mHorizontalScrollView1.setOnTouchListener(this);
				ArrayList<String> mData = new ArrayList<String>();
				ArrayList<String> mDataName = new ArrayList<String>();

				// 接种的类型（任意的还是定期）、接种名字
				String detail = detailInfo.getDetail();
				if (!"".equals(detail))
				{

					String[] DetailSplit = detail.split("\\|");
					for (int i = 0; i < DetailSplit.length; i++)
					{
						String Inoculate_times = DetailSplit[i];
						String type = Inoculate_times.substring(
								0,
								Inoculate_times.length()
										- (Inoculate_times.length() - 1));
						mData.add(type);
						mDataName.add(DetailSplit[i = i + 1]);
					}
					for (int i = 0; i < mData.size(); i++)
					{
						TextView premuntype = new TextView(mContext);
						String typeitem = mData.get(i);
						if ("0".equals(typeitem))
						{
							premuntype
									.setBackgroundResource(R.drawable.lab_teiki);

						}
						else
						{

							premuntype
									.setBackgroundResource(R.drawable.lab_nini);

						}
						TextView divimage = new TextView(mContext);
						divimage.setBackgroundResource(R.drawable.timeline_data_div);
						TextView preName = new TextView(mContext);
						preName.setText(mDataName.get(i));
						preName.setTextColor(mContext.getResources().getColor(
								R.color.istar_h));
						preName.setTextSize(17);
						LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);
						lp.setMargins(0, 0,4, 0);
						premuntype.setLayoutParams(lp);
						preName.setLayoutParams(lp);
						premunLayout.addView(premuntype);
						premunLayout.addView(preName);
						if (i < mData.size() - 1)
						{
							divimage.setLayoutParams(lp);
							premunLayout.addView(divimage);

						}

					}
					// 小孩姓名、年龄
					String strDate = detailInfo.getEventDate();
					mTextName.setText(detailInfo.getUserName());
					mTextTimeDiff.setText(DateUtilsDef
							.getDateBeforeNow(strDate));

					// 显示内容
					ChildDataInfo childInfo = detailInfo.getChildDataInfo();
					childName.setText(childInfo.getChidlName());
					childAge.setText(DateUtilsDef.getAgeWithBirthday(childInfo
							.getBirthDay()));
				}
			}
			else
			{
				if ((imgFiles == null) || (imgFiles.length == 0))
				{

					// 没有图片lview_detail
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.daily_listview_detail, null);

					mUserIcon = LzViewHolder.get(convertView,
							R.id.lview_userIcon);
					mTextName = LzViewHolder.get(convertView, R.id.lview_name);
					mTextTimeDiff = LzViewHolder.get(convertView,
							R.id.lview_timediff);
					mTextDetail = LzViewHolder.get(convertView,
							R.id.lview_detail);
				}
				else if (imgFiles.length == 1)
				{
					// 1张图片
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.daily_listview_detail1, null);

					mUserIcon = LzViewHolder.get(convertView,
							R.id.lview_userIcon);
					mTextName = LzViewHolder.get(convertView, R.id.lview_name);
					mTextTimeDiff = LzViewHolder.get(convertView,
							R.id.lview_timediff);
					mTextDetail = LzViewHolder.get(convertView,
							R.id.lview_detail);
					mImageView1 = LzViewHolder
							.get(convertView, R.id.lview_img1);

					// 重新设置图片大小
					lParams = mImageView1.getLayoutParams();
					lParams.width = picWidth1;
					lParams.height = picHeight1;
					mImageView1.setLayoutParams(lParams);

					// 显示照片
					imageLoader.displayImage(HttpClientUtil.SERVER_PATH
							+ imgFiles[0], mImageView1, options);
				}
				else if (imgFiles.length == 2)
				{
					// 2张图片
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.daily_listview_detail2, null);

					mUserIcon = LzViewHolder.get(convertView,
							R.id.lview_userIcon);
					mTextName = LzViewHolder.get(convertView, R.id.lview_name);
					mTextTimeDiff = LzViewHolder.get(convertView,
							R.id.lview_timediff);
					mTextDetail = LzViewHolder.get(convertView,
							R.id.lview_detail);
					mImageView1 = LzViewHolder
							.get(convertView, R.id.lview_img1);
					mImageView2 = LzViewHolder
							.get(convertView, R.id.lview_img2);
					textPhotoCount = LzViewHolder.get(convertView,
							R.id.lview_photo_count);

					// 重新设置图片大小
					lParams = mImageView1.getLayoutParams();
					lParams.width = picWidth2;
					lParams.height = picHeight2;
					mImageView1.setLayoutParams(lParams);

					lParams = mImageView2.getLayoutParams();
					lParams.width = picHeight2;
					lParams.height = picHeight2;
					mImageView2.setLayoutParams(lParams);

					// 照片数量
					textPhotoCount.setText(imgFiles.length + "  Photos");

					// 显示照片
					imageLoader.displayImage(HttpClientUtil.SERVER_PATH
							+ imgFiles[0], mImageView1, options);
					imageLoader.displayImage(HttpClientUtil.SERVER_PATH
							+ imgFiles[1], mImageView2, options);
				}
				else
				{
					// 3张以上图片
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.daily_listview_detail3, null);

					mUserIcon = LzViewHolder.get(convertView,
							R.id.lview_userIcon);
					mTextName = LzViewHolder.get(convertView, R.id.lview_name);
					mTextTimeDiff = LzViewHolder.get(convertView,
							R.id.lview_timediff);
					mTextDetail = LzViewHolder.get(convertView,
							R.id.lview_detail);
					mImageView1 = LzViewHolder
							.get(convertView, R.id.lview_img1);
					mImageView2 = LzViewHolder
							.get(convertView, R.id.lview_img2);
					mImageView3 = LzViewHolder
							.get(convertView, R.id.lview_img3);
					textPhotoCount = LzViewHolder.get(convertView,
							R.id.lview_photo_count);

					// 重新设置图片大小
					lParams = mImageView1.getLayoutParams();
					lParams.width = picWidth3;
					lParams.height = picWidth3;
					mImageView1.setLayoutParams(lParams);

					lParams = mImageView2.getLayoutParams();
					lParams.width = picHeight3;
					lParams.height = (int) (picHeight4 * 0.4);
					mImageView2.setLayoutParams(lParams);

					lParams = mImageView3.getLayoutParams();
					lParams.width = picHeight3;
					lParams.height = (int) (picHeight4 * 0.6);
					mImageView3.setLayoutParams(lParams);

					// 照片数量
					textPhotoCount.setText(imgFiles.length + "  Photos");

					// 显示照片
					imageLoader.displayImage(HttpClientUtil.SERVER_PATH
							+ imgFiles[0], mImageView1, options);
					imageLoader.displayImage(HttpClientUtil.SERVER_PATH
							+ imgFiles[1], mImageView2, options);
					imageLoader.displayImage(HttpClientUtil.SERVER_PATH
							+ imgFiles[2], mImageView3, options);

				}

				// 显示内容
				String strDate = detailInfo.getEventDate();
				mTextName.setText(detailInfo.getUserName());
				mTextTimeDiff.setText(DateUtilsDef.getDateBeforeNow(strDate));
				mTextDetail.setText(detailInfo.getDetail());

				// 头像
				String userIcon = detailInfo.getUserIcon();
				showImageWithPath(mUserIcon, userIcon);
				mHorizontalScrollView = LzViewHolder.get(convertView,
						R.id.listview_horizontalscrollview);
				mHorizontalScrollView.setOnTouchListener(this);
				// 宝宝成长记录
				LinearLayout childLinearLayout = LzViewHolder.get(convertView,
						R.id.lview_childdata_layout);

				if ("98".equals(detailInfo.getTagName()))
				{
					childLinearLayout.setVisibility(View.VISIBLE);
					TextView childHeight = LzViewHolder.get(convertView,
							R.id.lview_childheight);
					TextView childWeight = LzViewHolder.get(convertView,
							R.id.lview_childw);
					TextView childTouwei = LzViewHolder.get(convertView,
							R.id.lview_childhe);
					TextView childXiongwei = LzViewHolder.get(convertView,
							R.id.lview_childc);
					TextView childAge = LzViewHolder.get(convertView,
							R.id.lview_childage);
					TextView childName = LzViewHolder.get(convertView,
							R.id.lview_childname);

					ChildDataInfo childInfo = detailInfo.getChildDataInfo();
					childName.setText(childInfo.getChidlName());
					childAge.setText(DateUtilsDef.getAgeWithBirthday(childInfo
							.getBirthDay()));
					childHeight.setText(childInfo.getChildHeight() + "cm");
					childWeight.setText(childInfo.getChildWeight() + "kg");
					childTouwei.setText(childInfo.getChildTouwei() + "cm");
					childXiongwei.setText(childInfo.getChildXiongwei() + "cm");

				}
				else
				{
					childLinearLayout.setVisibility(View.GONE);
				}
			}
		}

		return convertView;
	}

	/**
	 * @Title: showImageWithPath
	 * @Description: TODO
	 * @param mUserIcon
	 * @param userIcon
	 * @return: void
	 */
	private void showImageWithPath(ImageView imageView, String imgPath)
	{

		if ((null == imageView) || (null == imgPath) || (imgPath.length() == 0))
		{
			return;
		}

		// 显示图片
		imageLoader.displayImage(HttpClientUtil.SERVER_PATH + imgPath,
				imageView, optionsForUserIcon);
	}

	//
	@Override
	public boolean areAllItemsEnabled()
	{
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{

		if (v.getId() == R.id.listview_horizontalscrollview
				&& v.getId() == R.id.listview_horizontalscrollview1
				&& event.getAction() == MotionEvent.ACTION_DOWN)
		{
			mSlidingMenu.getSlidView().setmCtrue();
			return true;

		}
		if (v.getId() == R.id.listview_horizontalscrollview
				&& v.getId() == R.id.listview_horizontalscrollview1
				&& event.getAction() == MotionEvent.ACTION_UP)
		{
			mSlidingMenu.getSlidView().setmCfalse();
		}

		return false;

	}

}
