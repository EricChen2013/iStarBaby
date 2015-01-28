package cn.leature.istarbaby.child;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzBaseActivity;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.daily.DailyListActivity;
import cn.leature.istarbaby.domain.ChildAlbumUpdateInfo;
import cn.leature.istarbaby.domain.ChildGrowInfo;
import cn.leature.istarbaby.domain.ChildPremunUpdateInfo;
import cn.leature.istarbaby.domain.ChildrenInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.info.CropImageActivity;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.network.UploadInputStreamUtil;
import cn.leature.istarbaby.network.UploadInputStreamUtil.OnUploadProcessListener;
import cn.leature.istarbaby.network.UploadUtil;
import cn.leature.istarbaby.slidingmenu.SlidingMenu;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DateUtilsDef;
import cn.leature.istarbaby.utils.ResizeImage;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/*
 * 
 * @author Administrator
 * @date 2014 06-23 下午14:39
 */

public class ChildrenActivity extends LzBaseActivity implements
		OnClickListener, OnPostProcessListener, OnUploadProcessListener
{

	private TextView titleBarTitle;
	private HttpPostUtil httpUtil;
	private static final int ALBUM_LIST_DONE = 1;
	private static final int ALBUM_LIST_ERROR = -1;

	// 图片上传成功（Message用）
	protected static final int UPLOAD_FILE_DONE = 3;

	// 图片上传出错（Message用）
	protected static final int UPLOAD_FILE_ERROR = -1;

	// 选择的头像
	private Bitmap mIconBitmap = null;

	private boolean isFlowerDisplay = true;
	private SlidingMenu mSlidingMenu;

	private FrameLayout mFlowerLayout;
	private LzProgressDialog mProgressDialog;
	private int mCropImageWH = 400;
	private Bundle mBundle;
	private ImageButton mBTN;
	private View mHxtx_layout, mMovelayout1, mMovelayout2, mIamge_hw,
			mIamge_tx;
	private ImageView mChildImage, mImage;
	private TextView mGender, mChildNo, mhuanyunqijian, mFenwan_shijian,
			mBirthday, mHeight, mTouwei, mXiongwei, mWeight, mEvent_date,
			mEvent_name;
	private TextView mName, mAge;
	// private int mCropImageWH = 400;
	private TextView mText_Height, mText_XiongWei, mText_Weight, mText_Touwei;
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private int mDataWidth;
	private AlertDialog mMyDialog;
	private static final int RESULT_CAPTURE_IMAGE = 2;
	private String strImgPath = "";// 照片文件绝对路径
	private int screenWidth, screenHeight;
	private String mChildImagePath = "";
	private TextView mPremun_Type;
	private TextView mPremun_Name;
	// 选择头像
	private Context mCon;
	private String TAG = "InformationActivity";
	public static final String IMAGE_PATH = "My_weixin";
	private static String localTempImageFileName = "";
	private static final int FLAG_CHOOSE_IMG = 5;
	private static final int FLAG_CHOOSE_PHONE = 6;
	private static final int FLAG_MODIFY_FINISH = 7;
	public static final File FILE_SDCARD = Environment
			.getExternalStorageDirectory();
	public static final File FILE_LOCAL = new File(FILE_SDCARD, IMAGE_PATH);
	public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL,
			"images/screenshots");
	// private LinearLayout mPremun_Layout;
	private TextView mPremun_EventDay;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sliding_main);
		mCon = ChildrenActivity.this;
		// 设置主菜单及导航
		setMenu_CustomTitleBar();

		inItUi();

		// 重新排列控件布局
		resetPosition();

		httpUtil = HttpPostUtil.getInstance();

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);
		mProgressDialog.set_Text("正在加载数据...", true);

		mBundle = this.getIntent().getExtras();

		getDefaultData();

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.noimage_child)
				.showImageForEmptyUri(R.drawable.noimage_child)
				.showImageOnFail(R.drawable.noimage_child).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	private void getDefaultData()
	{
		mProgressDialog.show();
		httpUtil.setOnPostProcessListener(this);

		Map<String, String> param = new HashMap<String, String>();

		// 登录用户的ID
		param.put("UserId", LoginInfo.getLoginUserId(this));
		// 宝宝排行childNo ChildId
		param.put("ChildNo", mBundle.getString(ConstantDef.ccChildNo));
		param.put("ChildId", mBundle.getString(ConstantDef.ccChildId));

		httpUtil.sendPostMessage(ConstantDef.cUrlChildrenDetail, param);
	}

	/**
	 * @Title: resetPosition
	 * @Description: TODO
	 * @return: void
	 */
	private void resetPosition()
	{

		// 成长记录
		LayoutParams param1 = (LayoutParams) mText_Height.getLayoutParams();
		param1.width = mDataWidth;
		mText_Height.setLayoutParams(param1);

		LayoutParams param2 = (LayoutParams) mText_Weight.getLayoutParams();
		param2.width = mDataWidth;
		mText_Weight.setLayoutParams(param2);

		LayoutParams param3 = (LayoutParams) mText_Touwei.getLayoutParams();
		param3.width = mDataWidth;
		mText_Touwei.setLayoutParams(param3);

		LayoutParams param4 = (LayoutParams) mText_XiongWei.getLayoutParams();
		param4.width = mDataWidth;
		mText_XiongWei.setLayoutParams(param4);
	}

	private void inItUi()
	{

		/**************
		 * 冒泡UI
		 * ************************************************************************/

		mChildImage = (ImageView) this.findViewById(R.id.child_detail_album);
		mHeight = (TextView) this.findViewById(R.id.child_detail_height);
		mTouwei = (TextView) this.findViewById(R.id.child_detail_touwei);
		mXiongwei = (TextView) this.findViewById(R.id.child_detail_xiongwei);
		mWeight = (TextView) this.findViewById(R.id.child_detail_weight);
		mChildNo = (TextView) this.findViewById(R.id.child_no);
		mChildNo.setOnClickListener(this);

		mFenwan_shijian = (TextView) this
				.findViewById(R.id.child_detail_fenwanshijian);
		mGender = (TextView) this.findViewById(R.id.child_detail_sex);
		mGender.setOnClickListener(this);

		mImage = (ImageView) this.findViewById(R.id.child_image);
		mImage.setOnClickListener(this);

		mhuanyunqijian = (TextView) this
				.findViewById(R.id.child_detail_mhuanyunqijian);
		mBirthday = (TextView) this.findViewById(R.id.child_detail_birthday);
		mBirthday.setOnClickListener(this);

		mBTN = (ImageButton) this.findViewById(R.id.child_btn);
		mBTN.setOnClickListener(this);

		mHxtx_layout = (LinearLayout) this
				.findViewById(R.id.child_nature_hwtx_layout);
		mMovelayout1 = (RelativeLayout) this.findViewById(R.id.movelayout1);
		mMovelayout1.setOnClickListener(this);

		mMovelayout2 = (LinearLayout) this.findViewById(R.id.movelayout2);
		mHxtx_layout.setOnClickListener(this);
		mMovelayout2.setOnClickListener(this);
		mText_Height = (TextView) findViewById(R.id.text_height);
		mText_XiongWei = (TextView) findViewById(R.id.text_xiongwei);
		mText_Weight = (TextView) findViewById(R.id.text_weight);
		mText_Touwei = (TextView) findViewById(R.id.text_touwei);

		// 设置selector
		mIamge_hw = (FrameLayout) this.findViewById(R.id.child_detail_hw);
		mIamge_tx = (FrameLayout) this.findViewById(R.id.child_detail_tx);
		mIamge_hw.setOnClickListener(this);
		mIamge_tx.setOnClickListener(this);
		mEvent_date = (TextView) this
				.findViewById(R.id.child_detail_event_date);
		mEvent_name = (TextView) this
				.findViewById(R.id.child_detail_event_name);

		mName = (TextView) this.findViewById(R.id.child_detail_name);
		mName.setOnClickListener(this);

		mAge = (TextView) this.findViewById(R.id.child_detail_age);
		/***********************************************************/
		mFlowerLayout = (FrameLayout) this
				.findViewById(R.id.child_flower_layout);
		// mPremun_Layout = (LinearLayout)
		// findViewById(R.id.children_premunlayout);
		mPremun_EventDay = (TextView) findViewById(R.id.children_prenmuneventday);
		mPremun_Type = (TextView) findViewById(R.id.child_premun_type);
		mPremun_Name = (TextView) findViewById(R.id.child_premun_name);
	}

	private void setMenu_CustomTitleBar()
	{
		// 设置主菜单
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMain);

		View leftView = getLayoutInflater()
				.inflate(R.layout.sliding_menu, null);
		View centerView = getLayoutInflater().inflate(
				R.layout.activity_children, null);
		mSlidingMenu.setLeftView(leftView);
		mSlidingMenu.setCenterView(centerView);

		// 设置顶部导航
		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("母子手册");
		ImageButton showLeftMenu = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);

		showLeftMenu.setBackgroundResource(R.drawable.selector_hd_menu);
		showLeftMenu.setOnClickListener(this);
		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);

		// 成长记录宽度
		mDataWidth = 100 * dm.widthPixels / 640;
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
			case UPLOAD_FILE_DONE:
				// 图片更新
				toUploadIconDone(msg.obj.toString());
				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};

	private void toUploadIconDone(String string)
	{
		if (mProgressDialog.isShowing())
		{
			mProgressDialog.dismiss();
		}
		// 登录成功，保存登录用户
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("UserString", string);
		LoginInfo.saveLoginUser(this, param);

		// 通知菜单数据更新
		mSlidingMenu.reloadAdapterData();
	}

	protected void toListDailyDone(String result)
	{

		if (mProgressDialog.isShowing())
		{
			mProgressDialog.dismiss();
		}

		if ((result == null) || (result.equals("0")))
		{
			// 处理出错，提示信息
			Toast.makeText(this, "数据加载出错。", Toast.LENGTH_LONG).show();

			return;
		}

		try
		{
			JSONObject jsonObject = new JSONObject(result);
			JSONObject json_detail = jsonObject
					.getJSONObject("children_detail");
			ChildrenInfo manageinfo = new ChildrenInfo(json_detail);

			mHeight.setText(manageinfo.getHeight());
			mTouwei.setText(manageinfo.getTouwei());
			mXiongwei.setText(manageinfo.getXiongwei());
			mWeight.setText(manageinfo.getWeight());

			mChildNo.setText("第" + manageinfo.getChild_no() + "子");
			mFenwan_shijian.setText(manageinfo.getFenmian_shijian());

			// 性别
			if ("1".equals(manageinfo.getGender()))
			{
				mGender.setText("男");
			}
			else if ("2".equals(manageinfo.getGender()))
			{
				mGender.setText("女");
			}
			else
			{
				mGender.setText("未知");
			}

			mhuanyunqijian.setText(manageinfo.getHuaiyun_qijian());

			// 年龄
			mAge.setText(DateUtilsDef.getAgeWithBirthday(manageinfo
					.getBirthday()));

			mBirthday.setText(DateUtilsDef.dateFormatString(
					manageinfo.getBirthday(), "yyyy\nMM/dd"));

			// 图片加载
			imageLoader.displayImage(
					HttpClientUtil.SERVER_PATH + manageinfo.getIcon(), mImage,
					options);

			// 有名字显示名字没名字显示小名
			if (manageinfo.getNickname().equals(""))
			{
				mName.setText(manageinfo.getChildName());
			}
			else
			{

				mName.setText(manageinfo.getNickname());
			}

			JSONObject json_update = jsonObject.getJSONObject("last_update");
			ChildAlbumUpdateInfo jsonUpdateInfo = new ChildAlbumUpdateInfo(
					json_update);
			if (!"".equals(jsonUpdateInfo.getEvent_date()))
			{
				mEvent_date.setText(DateUtilsDef.dateFormatString(
						jsonUpdateInfo.getEvent_date(), "yyyy/MM/dd") + " 更新");
				mEvent_name.setText(jsonUpdateInfo.getEvent_name());
				imageLoader.displayImage(HttpClientUtil.SERVER_PATH
						+ jsonUpdateInfo.getPhoto_url(), mChildImage, options);
				mChildImage.setBackgroundResource(R.drawable.bg_detailpic);
			}
			else
			{
				mEvent_date.setText("----/--/--");
				mEvent_name.setText("保留宝宝的\n精彩瞬间");
				mChildImage.setBackgroundResource(R.drawable.child_icon);
			}

			JSONObject json_growdata = jsonObject
					.getJSONObject("child_growdata");
			ChildGrowInfo growdatainfo = new ChildGrowInfo(json_growdata);
			if ("".equals(growdatainfo.getHeight()))
			{
				mText_Height.setText("-");
			}
			else
			{

				mText_Height.setText(growdatainfo.getHeight());
			}
			if ("".equals(growdatainfo.getWeight()))
			{
				mText_Weight.setText("-");
			}
			else
			{

				mText_Weight.setText(growdatainfo.getWeight());
			}
			if ("".equals(growdatainfo.getTouwei()))
			{
				mText_Touwei.setText("-");
			}
			else
			{

				mText_Touwei.setText(growdatainfo.getTouwei());
			}

			if ("".equals(growdatainfo.getXiongwei()))
			{
				mText_XiongWei.setText("-");
			}
			else
			{

				mText_XiongWei.setText(growdatainfo.getXiongwei());
			}
			JSONObject json_Premun = jsonObject
					.getJSONObject("vaccine_inoculate");
			ChildPremunUpdateInfo json_PremunInfo = new ChildPremunUpdateInfo(
					json_Premun);
			String detail = json_PremunInfo.getDetail();
			if (!"".equals(json_PremunInfo.getEvent_date()))
			{
				mPremun_EventDay.setText(DateUtilsDef.dateFormatString(
						json_PremunInfo.getEvent_date(), "yyyy/MM/dd") + " 接种");
				mPremun_Type.setVisibility(View.VISIBLE);
			}
			else
			{
				mPremun_EventDay.setText("登录预防记录");
			}

			// 接种的类型（任意的还是定期）、接种名字
			ArrayList<String> mData = new ArrayList<String>();
			ArrayList<String> mDataName = new ArrayList<String>();
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

				String typeitem = mData.get(0);
				String premunName = mDataName.get(0);
				if ("0".equals(typeitem))
				{
					mPremun_Type.setBackgroundResource(R.drawable.lab_teiki);

				}
				else
				{

					mPremun_Type.setBackgroundResource(R.drawable.lab_nini);

				}
				if (!"".equals(premunName))
				{
					mPremun_Name.setText(premunName);
				}
				else
				{
					mPremun_Name.setText("-");

				}
			}

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @Title: toErrorMessage
	 * @Description: TODO
	 * @param string
	 * @return: void
	 */
	protected void toErrorMessage(String msgString)
	{
		// 出错，显示提示信息
		if (mProgressDialog.isShowing())
		{
			mProgressDialog.dismiss();
		}
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onClick(View v)
	{

		Intent intent = new Intent();
		switch (v.getId())
		{
		case R.id.titlebar_leftbtn:
			// 打开主菜单（从左边划出）
			mSlidingMenu.showLeftView();

			break;

		case R.id.child_image:

			customDialog();

			break;

		case R.id.child_btn:
			if (isFlowerDisplay)
			{

				mFlowerLayout.startAnimation(AnimationUtils.loadAnimation(this,
						R.anim.child_detail_flower_invisible));
			}
			else
			{
				mFlowerLayout.startAnimation(AnimationUtils.loadAnimation(this,
						R.anim.child_detail_flower_visible));
			}
			isFlowerDisplay = !isFlowerDisplay;
			break;

		case R.id.movelayout2:

			intent.setClass(ChildrenActivity.this, PremunitionActivity.class);
			mBundle.putString(ConstantDef.ccChildName, mName.getText()
					.toString());
			intent.putExtras(mBundle);
			startActivityForResult(intent, ConstantDef.REQUEST_CODE_PREMUNITION);
			overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);

			break;
		case R.id.movelayout1:

			intent.setClass(this, ChildAlbumActivity.class);
			intent.putExtras(mBundle);
			startActivityForResult(intent, ConstantDef.REQUEST_CODE_CHILD_ALBUM);
			overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
			break;

		case R.id.child_detail_tx:

			intent.setClass(ChildrenActivity.this, ChildTxActivity.class);
			mBundle.putString(ConstantDef.ccChildName, mName.getText()
					.toString());
			intent.putExtras(mBundle);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_CHILD_TXDATA);
			overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
			break;

		case R.id.child_detail_hw:

			intent.setClass(ChildrenActivity.this, ChildHwActivity.class);
			mBundle.putString(ConstantDef.ccChildName, mName.getText()
					.toString());
			intent.putExtras(mBundle);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_CHILD_HWDATA);
			overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
			break;

		// 编辑头围~胸围~身高~ 体重 详情
		case R.id.child_nature_hwtx_layout:
		case R.id.child_detail_name:
		case R.id.child_no:
		case R.id.child_detail_birthday:
		case R.id.child_detail_sex:
			intent.setClass(this, ChildDetailActivity.class);
			intent.putExtras(mBundle);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_CHILD_DETAIL);
			overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
			break;
		case R.id.image_capture:

			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED))
			{
				try
				{
					localTempImageFileName = "";
					localTempImageFileName = String.valueOf((new Date())
							.getTime()) + ".png";
					File filePath = FILE_PIC_SCREENSHOT;
					if (!filePath.exists())
					{
						filePath.mkdirs();
					}
					Intent intent1 = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					File f = new File(filePath, localTempImageFileName);
					// localTempImgDir和localTempImageFileName是自己定义的名字
					Uri u = Uri.fromFile(f);
					intent1.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
					intent1.putExtra(MediaStore.EXTRA_OUTPUT, u);
					startActivityForResult(intent1, FLAG_CHOOSE_PHONE);
				}
				catch (ActivityNotFoundException e)
				{
					//
				}
			}

			break;

		case R.id.image_album:
			mMyDialog.dismiss();
			intent.setAction(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, FLAG_CHOOSE_IMG);
			// SelectCropImageUtil.getCropRectImage(this, mCropImageWH);
			break;

		case R.id.image_quxiao:

			mMyDialog.dismiss();

			break;
		default:
			break;
		}
	}

	private void customDialog()
	{
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View myLoginView = layoutInflater.inflate(R.layout.dialog_pick_photo, null);
		Window dialogWindow = this.getWindow();
		Button image_capture = (Button) myLoginView
				.findViewById(R.id.image_capture);
		Button image_album = (Button) myLoginView
				.findViewById(R.id.image_album);
		Button image_quxiao = (Button) myLoginView
				.findViewById(R.id.image_quxiao);

		mMyDialog = new AlertDialog.Builder(this)

		.setView(myLoginView).create();

		dialogWindow = mMyDialog.getWindow();
		// WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.BOTTOM);
		AlignmentSpan span = new AlignmentSpan.Standard(
				Layout.Alignment.ALIGN_CENTER);
		AbsoluteSizeSpan span_size = new AbsoluteSizeSpan(25, true);
		SpannableStringBuilder spannable = new SpannableStringBuilder();
		String dTitle = getString(R.string.gl_choose_title);
		spannable.append(dTitle);
		spannable.setSpan(span, 0, dTitle.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(span_size, 0, dTitle.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		mMyDialog.show();
		image_capture.setOnClickListener(this);
		image_album.setOnClickListener(this);
		image_quxiao.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		if (mMyDialog != null)
		{
			mMyDialog.dismiss();
		}

		if (requestCode == FLAG_CHOOSE_IMG && resultCode == RESULT_OK)
		{
			if (data != null)
			{
				Uri uri = data.getData();
				if (!TextUtils.isEmpty(uri.getAuthority()))
				{
					Cursor cursor = getContentResolver().query(uri,
							new String[]
							{ MediaStore.Images.Media.DATA }, null, null, null);
					if (null == cursor)
					{
						Toast.makeText(mCon, "图片没找到", 0).show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					cursor.close();
					Log.i(TAG, "path=" + path);
					Intent intent = new Intent(this, CropImageActivity.class);
					intent.putExtra("path", path);
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				}
				else
				{
					Log.i(TAG, "path=" + uri.getPath());
					Intent intent = new Intent(this, CropImageActivity.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				}
			}
		}
		else if (requestCode == FLAG_CHOOSE_PHONE && resultCode == RESULT_OK)
		{
			File f = new File(FILE_PIC_SCREENSHOT, localTempImageFileName);
			Intent intent = new Intent(this, CropImageActivity.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, FLAG_MODIFY_FINISH);
		}
		else if (requestCode == FLAG_MODIFY_FINISH && resultCode == RESULT_OK)
		{
			if (data != null)
			{

				final String path = data.getStringExtra("path");
				Bitmap bm = BitmapFactory.decodeFile(path);
				// 裁剪成正方形图片
				mIconBitmap = ResizeImage.resizeBitmapWithRadio(bm, 1, 1);

				mImage.setImageBitmap(mIconBitmap);
				uploadIconBitmap();

			}
		}

		else if (resultCode == RESULT_OK)
		{
			switch (requestCode)
			{
			case ConstantDef.REQUEST_CODE_CHILD_DETAIL:
				// 宝宝删除，跳转都首页
				Intent topPage = new Intent();
				topPage.setClass(this, DailyListActivity.class);
				startActivity(topPage);
				finish();

				// 设定启动动画
				overridePendingTransition(R.anim.activity_in_from_right,
						R.anim.activity_nothing_in_out);

				break;

			default:
				break;
			}
		}
		else if (resultCode == ConstantDef.RESULT_CODE_ALBUM_EDIT
				|| resultCode == ConstantDef.RESULT_CODE_CHILD_HWDATA
				|| resultCode == ConstantDef.RESULT_CODE_CHILD_TXDATA
				|| resultCode == ConstantDef.RESULT_CODE_PREMUNITION)
		{
			// 重新加载
			getDefaultData();

		}
		else if (resultCode == ConstantDef.RESULT_CODE_CHILD_EDIT)
		{
			// 宝宝信息编辑
			// 刷新菜单
			mSlidingMenu.reloadAdapterData();
			mBundle = data.getExtras();
			// 重新加载
			getDefaultData();
		}
	}

	private void uploadIconBitmap()
	{
		UploadInputStreamUtil streamUtil = UploadInputStreamUtil.getInstance();
		streamUtil.setOnUploadProcessListener(this); // 设置监听器监听上传状态

		Map<String, String> params = new HashMap<String, String>();
		params.put("UserID", LoginInfo.getLoginUserId(this));
		params.put("ChildNo", mBundle.getString(ConstantDef.ccChildNo));

		streamUtil.uploadInputStream(mIconBitmap, "pic",
				ConstantDef.cUrlUploadChildIcon, params);
	}

	@Override
	public void onUploadDone(int responseCode, String responseMessage)
	{

		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == UploadUtil.UPLOAD_SUCCESS_CODE)
		{
			msg.what = UPLOAD_FILE_DONE;
		}
		else
		{
			msg.what = UPLOAD_FILE_ERROR;
		}
		handler.sendMessage(msg);
	}

	@Override
	public void onUploadProcess(int uploadSize)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void initUpload(int fileSize)
	{
		// TODO Auto-generated method stub

	}

}
