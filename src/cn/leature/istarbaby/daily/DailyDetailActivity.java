package cn.leature.istarbaby.daily;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.DailyDetailInfo;
import cn.leature.istarbaby.domain.DailyDetailInfo.ChildDataInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.gallery.GalleyShowActivity;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DateUtilsDef;
import cn.leature.istarbaby.utils.DensityUtil;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class DailyDetailActivity extends Activity implements OnClickListener,
		OnPageChangeListener, OnPostProcessListener
{

	private TextView mTextUserName;
	private TextView mTextDetail;
	private TextView mPhotoNo, mPhotoCounts;
	private ImageView mUserIcon;
	private ViewPager mImagePager;
	private LinearLayout mImageLayout;
	private RelativeLayout mTitleLayout1, mTitleLayout2;
	private Button mQueding;
	private Button mQuxiao;
	private TextView mChildName, mChildHeight, mChildAge, mChildDw, mChildHe,
			mChildDc;
	protected ImageLoader imageLoader = ImageLoader.getInstance(); // 图片下载

	// 进度条控件
	private LzProgressDialog mProgressDialog;
	private static final int ALBUM_LIST_DONE = 1;
	private static final int ALBUM_LIST_ERROR = -1;

	// TitleBar的控件
	private TextView titleBarTitle;
	private ImageButton titleBarBack, titleBarEdit;

	private HttpPostUtil httpUtil;
	private Intent mIntent;
	private String mEventDate;
	private AlertDialog dialog;
	private Bundle mBundle;
	boolean openWindow = false;
	private boolean isRefreshData = false;
	// 菜单窗口控件
	private PopupWindow mPopupWindow;

	DisplayImageOptions options;
	DisplayImageOptions optionsForUserIcon;

	private int screenWidth, screenHeight;

	private static final int Pic_Margin_Vertical = 120;

	private String[] mImageFiles;
	private View mInculd_layout;
	private FrameLayout mDaily_detail;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_daily_detail);
		httpUtil = HttpPostUtil.getInstance();

		// 创建进度条
		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		// 取出详细信息
		mIntent = this.getIntent();
		mBundle = mIntent.getExtras();

		// 取得窗口属性
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 窗口的宽度
		screenWidth = dm.widthPixels;
		// 窗口高度
		screenHeight = dm.heightPixels;

		inItUi();

		// 设置view pager的宽高
		LayoutParams lParams = mImagePager.getLayoutParams();
		lParams.width = screenWidth;
		lParams.height = screenHeight
				- DensityUtil.dip2px(this, Pic_Margin_Vertical);
		mImagePager.setLayoutParams(lParams);

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.nophoto)
				.showImageOnFail(R.drawable.nophoto)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		optionsForUserIcon = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.noimage)
				.showImageOnFail(R.drawable.noimage)
				.resetViewBeforeLoading(false).cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		getDefaultData();
	}

	private void getDefaultData()
	{
		httpUtil.setOnPostProcessListener(this);

		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		// 登录用户的ID
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("Daily_Id", mBundle.getString(ConstantDef.ccDaily_Id));
		httpUtil.sendPostMessage(ConstantDef.cUrlDailyDetail, param);
	}

	/**
	 * @param imageView
	 * @Title: showImageWithPath
	 * @Description: TODO
	 * @param string
	 * @return: void
	 */

	private void showIconWithPath(final ImageView imageView, String imgPath)
	{
		if ((null == imageView) || (null == imgPath) || (imgPath.length() == 0))
		{
			return;
		}

		// 显示图片
		imageLoader.displayImage(HttpClientUtil.SERVER_PATH + imgPath,
				imageView, optionsForUserIcon);
	}

	private void dialog()
	{

		View layout = getLayoutInflater().inflate(R.layout.delete_dialog, null);
		mQueding = (Button) layout.findViewById(R.id.delete_queding);
		mQuxiao = (Button) layout.findViewById(R.id.delete_quxiao);
		mQuxiao.setOnClickListener(this);
		mQueding.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(layout);
	}

	private void inItUi()
	{

		mTextDetail = (TextView) this.findViewById(R.id.dailyDetailText);
		mPhotoNo = (TextView) this.findViewById(R.id.detail_photo_no);
		mPhotoCounts = (TextView) this.findViewById(R.id.detail_photo_counts);
		mImageLayout = (LinearLayout) this
				.findViewById(R.id.detail_image_layout);
		mImagePager = (ViewPager) findViewById(R.id.daily_detail_pager);
		mImagePager.setOnPageChangeListener(this);

		mTitleLayout1 = (RelativeLayout) this
				.findViewById(R.id.detail_title_layout1);
		mTitleLayout2 = (RelativeLayout) this
				.findViewById(R.id.detail_title_layout2);

		// vapage数据
		mChildName = (TextView) findViewById(R.id.lview_childname);
		mChildAge = (TextView) findViewById(R.id.lview_childage);
		mChildHeight = (TextView) findViewById(R.id.lview_childheight);
		mChildDw = (TextView) findViewById(R.id.lview_childw);
		mChildHe = (TextView) findViewById(R.id.lview_childhe);
		mChildDc = (TextView) findViewById(R.id.lview_childc);
		mInculd_layout = findViewById(R.id.include_layout);
		// 自定义title bar
		mDaily_detail = (FrameLayout) findViewById(R.id.daily_detail_layout);
		mDaily_detail.setOnClickListener(this);
		setCustomTitleBar();
	}

	private void setCustomTitleBar()
	{

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);
		titleBarBack.setOnClickListener(this);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_edt);
		titleBarEdit.setOnClickListener(this);
		mInculd_layout = findViewById(R.id.include_layout);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			backPrePage();
		}
		return true;

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.daily_detail_layout:
			if (mPopupWindow != null)
			{
				openWindow = false;
				mPopupWindow.dismiss();
			}
			break;
		case R.id.titlebar_leftbtn:
			// 返回主页面
			backPrePage();
			break;
		case R.id.daily_detail_images:
			showImage();
			break;
		case R.id.titlebar_rightbtn1:

			if (!openWindow)
			{
				openWindow = !openWindow;
				getpopunwindow();

			}
			else
			{
				openWindow = !openWindow;
				mPopupWindow.dismiss();
			}
			break;
		case R.id.delete_queding:
			// 删除日志
			Map<String, String> param = new HashMap<String, String>();
			param.put("UserId",
					LoginInfo.getLoginUserId(DailyDetailActivity.this));
			param.put("Daily_Id", mBundle.getString(ConstantDef.ccDaily_Id));

			HttpGetUtil httpGetUtil = new HttpGetUtil(
					new RequestGetDoneCallback()
					{

						@Override
						public void requestWithGetDone(String result)
						{
							mProgressDialog.dismiss();
							mPopupWindow.dismiss();
							dialog.dismiss();
							if ((result == null) || (result.equals("0")))
							{
								// 处理出错
								return;
							}
							// 从发表新文章页面返回时，重新加载
							reloadDailyList();
						}
					});
			httpGetUtil.execute(ConstantDef.cUrlDailyDelete, param);

			break;
		case R.id.delete_quxiao:

			mProgressDialog.dismiss();
			mPopupWindow.dismiss();
			dialog.dismiss();

			break;

		default:
			break;
		}
	}

	private void showImage()
	{
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("SD", "DetilyDetail");
		bundle.putInt("StartIndex",
				Integer.parseInt(mPhotoNo.getText().toString().trim()) - 1);
		bundle.putStringArray("Dailyimage", mImageFiles);
		intent.putExtras(bundle);
		intent.setClass(DailyDetailActivity.this, GalleyShowActivity.class);
		startActivityForResult(intent, ConstantDef.REQUEST_CODE_DAILY_PHOTO);
	}

	protected void reloadDailyList()
	{

		// 从发表新文章页面返回时，重新加载
		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();

		// 设定退出动画
		overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	private void backPrePage()
	{
		// 取消时候，直接返回主页面
		if (mPopupWindow != null)
		{

			mPopupWindow.dismiss();
		}
		if (isRefreshData)
		{
			// 信息被修改过，需要返回更新数据
			this.setResult(ConstantDef.RESULT_CODE_DAILY_EDIT, mIntent);
		}
		else
		{
			this.setResult(Activity.RESULT_CANCELED, mIntent);
		}
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	private void getpopunwindow()
	{
		// 编辑文章
		LayoutInflater LayoutInflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popunwindwow = LayoutInflater.inflate(
				R.layout.dailydetail_edit_item, null);
		int Height = mInculd_layout.getHeight();

		mPopupWindow = new PopupWindow(popunwindwow, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);

		mPopupWindow.showAtLocation(findViewById(R.id.daily_detail_layout),
				Gravity.RIGHT | Gravity.TOP, 0,
				Height + DensityUtil.dip2px(this, 20));

		popunwindwow.findViewById(R.id.item1).setOnClickListener(
				new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						openWindow = !openWindow;
						mPopupWindow.dismiss();

						Intent intent = new Intent();
						intent.putExtras(mBundle);
						intent.setClass(DailyDetailActivity.this,
								DailyEditActivity.class);

						startActivityForResult(intent,
								ConstantDef.REQUEST_CODE_DAILY_EDIT);

						DailyDetailActivity.this.overridePendingTransition(
								R.anim.activity_in_from_bottom,
								R.anim.activity_nothing_in_out);

					}
				});

		popunwindwow.findViewById(R.id.item2).setOnClickListener(
				new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						// 显示删除进度
						openWindow = !openWindow;
						mProgressDialog.show();
						dialog();

					}
				});

		popunwindwow.findViewById(R.id.item3).setOnClickListener(
				new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						openWindow = !openWindow;
						mPopupWindow.dismiss();
					}
				});
	}

	public class ImagePagerAdapter extends PagerAdapter
	{

		private List<View> listViews;

		public ImagePagerAdapter(List<View> listViews)
		{
			this.listViews = listViews;
		}

		/**
		 * @Title: getCount
		 * @Description: TODO
		 * @return
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public int getCount()
		{
			return listViews.size();
		}

		/**
		 * @Title: isViewFromObject
		 * @Description: TODO
		 * @param arg0
		 * @param arg1
		 * @return
		 * @see android.support.v4.view.PagerAdapter#isViewFromObject(android.view.View,
		 *      java.lang.Object)
		 */
		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object)
		{
			((ViewPager) container).removeView(listViews.get(position));

		}

		@Override
		public Object instantiateItem(View container, int position)
		{

			((ViewPager) container).addView(listViews.get(position));

			return listViews.get(position);
		}
	}

	/**
	 * @Title: onPageScrollStateChanged
	 * @Description: TODO
	 * @param arg0
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
	 */
	@Override
	public void onPageScrollStateChanged(int arg0)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @Title: onPageScrolled
	 * @Description: TODO
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int,
	 *      float, int)
	 */
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @Title: onPageSelected
	 * @Description: TODO
	 * @param arg0
	 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected(int)
	 */
	@Override
	public void onPageSelected(int curPage)
	{
		mPhotoNo.setText((curPage + 1) + "");
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage)
	{
		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE)
		{
			msg.what = ALBUM_LIST_DONE;
		}
		else
		{
			msg.what = ALBUM_LIST_ERROR;
		}

		handler.sendMessage(msg);
	}

	private Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case ALBUM_LIST_DONE:
				toListDailyDone(msg.obj.toString());
				break;

			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}
	};

	protected void toErrorMessage(String msgString)
	{
		// List取得出错，显示提示信息
		mProgressDialog.dismiss();
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	private void toListDailyDone(String listResult)
	{
		// 没有图片的情况下
		mProgressDialog.dismiss();

		JSONObject jsonObject;
		try
		{
			jsonObject = new JSONObject(listResult);
			DailyDetailInfo detailInfo = new DailyDetailInfo(jsonObject);

			// title信息
			mEventDate = detailInfo.getEventDate();
			titleBarTitle.setText(DateUtilsDef.dateFormatString(mEventDate,
					"yyyy/MM/dd"));

			// 显示详细信息
			mTextDetail.setText(detailInfo.getDetail());
			ChildDataInfo childDataInfo = detailInfo.getChildDataInfo();

			mChildName.setText(childDataInfo.getChidlName());
			mChildAge.setText(DateUtilsDef.calAgeBetweenString(
					childDataInfo.getBirthDay(), mEventDate.substring(0, 8)));
			mChildHeight.setText(childDataInfo.getChildHeight() + "cm");
			mChildDw.setText(childDataInfo.getChildWeight() + "kg");
			mChildHe.setText(childDataInfo.getChildTouwei() + "cm");
			mChildDc.setText(childDataInfo.getChildXiongwei() + "cm");

			/*
			 * --------------------------------------------------------------
			 */
			// 图片URL
			mImageFiles = detailInfo.getImage();
			Log.e("图片url", "mImageFiles" + mImageFiles);
			if (mImageFiles.length == 0)
			{

				mImageLayout.setVisibility(View.GONE);
				mTitleLayout1.setVisibility(View.GONE);
				mTitleLayout2.setVisibility(View.VISIBLE);

				mUserIcon = (ImageView) this
						.findViewById(R.id.detail_user_icon2);
				mTextUserName = (TextView) this
						.findViewById(R.id.daily_detail_user2);
			}
			else
			{

				mUserIcon = (ImageView) this.findViewById(R.id.detail_userIcon);
				mTextUserName = (TextView) this
						.findViewById(R.id.dailyDetailUser);

				final ArrayList<View> views = new ArrayList<View>();
				for (int i = 0; i < mImageFiles.length; i++)
				{
					View view = LayoutInflater.from(this).inflate(
							R.layout.daily_detail_image, null);

					ImageView imageView = (ImageView) view
							.findViewById(R.id.daily_detail_images);
					imageView.setOnClickListener(this);

					// 显示图片
					imageLoader.displayImage(HttpClientUtil.SERVER_PATH
							+ mImageFiles[i], imageView, options);

					views.add(view);
				}

				ImagePagerAdapter pagerAdapter = new ImagePagerAdapter(views);
				mImagePager.setAdapter(pagerAdapter);

				// 显示图片编号：初始显示第一张
				mPhotoNo.setText("1");
				mPhotoCounts.setText(" / " + mImageFiles.length + " Photos");
			}

			// 显示头像及用户名
			String userIcon = detailInfo.getUserIcon();
			showIconWithPath(mUserIcon, userIcon);
			mTextUserName.setText(detailInfo.getUserName());

			// 宝宝成长记录
			LinearLayout childLinearLayout = (LinearLayout) this
					.findViewById(R.id.lview_childdata_layout);

			if ("98".equals(detailInfo.getTagName()))
			{
				childLinearLayout.setVisibility(View.VISIBLE);
			}

			else
			{
				childLinearLayout.setVisibility(View.GONE);
			}

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ConstantDef.REQUEST_CODE_DAILY_PHOTO)
		{
			return;
		}

		if (resultCode == Activity.RESULT_OK)
		{
			// 重新加载
			isRefreshData = true;
			getDefaultData();
		}
	}

}
