package cn.leature.istarbaby.daily;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.child.EditTextListener;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.UserInfo;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.UploadInputStreamUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.network.ImageDownloadTask;
import cn.leature.istarbaby.network.ImageDownloadTask.ImageDoneCallback;
import cn.leature.istarbaby.network.UploadInputStreamUtil.OnUploadProcessListener;
import cn.leature.istarbaby.selecttime.DateTimePicker;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DensityUtil;
import cn.leature.istarbaby.utils.SelectPhotosCallBack;
import cn.leature.istarbaby.utils.SelelctPhotosUtils;

public class DailyPostActivity extends Activity implements OnClickListener,
		OnPostProcessListener, OnUploadProcessListener
{
	// 用户登录信息
	private UserInfo loginUserInfo;

	// 使用相册中的图片（Activity跳转用）
	public static final int SELECT_PIC_BY_PICK_PHOTO = 1;

	// 文件上传初始化（Message用）
	private static final int UPLOAD_INIT_PROCESS = 1;
	// 文件上传中（Message用）
	private static final int UPLOAD_IN_PROCESS = 2;
	// 图片上传成功（Message用）
	protected static final int UPLOAD_FILE_DONE = 3;
	// 图片上传出错（Message用）
	protected static final int UPLOAD_FILE_ERROR = -1;

	// 文章发表成功（Message用）
	protected static final int POST_DAILY_DONE = 4;
	// 文章发表出错（Message用）
	protected static final int POST_DAILY_ERROR = -2;

	// 上传图片大小
	private final int mUploadPhotoSize = 54;

	// 年，月，日
	private int mYear, mMonth, mDay;

	// TitleBar的控件
	private TextView mTitleBarTitle, mShowText;
	private ImageButton mTitleBarCancel, mTitleBarPost;

	// 主画面的控件
	private EditText mDetailEditText; // 内容
	private ImageButton mUploadImageBtn;
	private EditText mPostDateTextView;
	private LzProgressDialog mProgressDialog;

	private Intent mIntent;

	private UploadInputStreamUtil mUploadUtil;

	private EditText mHeight, mWeight, mTouWei, mXiongWei;

	private View mLayout;
	// 表示选择的图片列表（本地的文件名）
	private ArrayList<String> mListData;
	// 用来保持图片缓存
	private HashMap<String, Bitmap> mPhotosHashMap;

	// 表示上传的图片列表（上传后的文件名）
	private ArrayList<String> mUploadListData;

	private ImageView mShow_Image;
	private Bundle mBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_daily_post);

		mIntent = this.getIntent();

		// 检查用户登录状况
		checkUserLogin();

		inItUi();

		mBundle = mIntent.getExtras();
		mShowText = (TextView) findViewById(R.id.post_show_text);
		mShow_Image = (ImageView) findViewById(R.id.post_show_image);
		mLabel = (TextView) findViewById(R.id.post_label1);
		mLayout = findViewById(R.id.layouts_hwtx);
		mHeight = (EditText) findViewById(R.id.shengao_edit);
		mWeight = (EditText) findViewById(R.id.tizhong_edit);
		mTouWei = (EditText) findViewById(R.id.touwei_edit);
		mXiongWei = (EditText) findViewById(R.id.xiongwei_edit);

		// 限制小数点只有一位
		EditTextListener.addTextListener().Listener(mHeight, this);
		EditTextListener.addTextListener().Listener(mWeight, this);
		EditTextListener.addTextListener().Listener(mXiongWei, this);
		EditTextListener.addTextListener().Listener(mTouWei, this);

		if (mBundle != null)
		{
			if ("tab".equals(mBundle.getString("Channel")))
			{

				mShowText.setVisibility(View.VISIBLE);
				String eventName = mBundle.getString(ConstantDef.ccEventName);
				mShowText.setText(eventName);
				mLabel.setText(eventName);
				mShow_Image.setVisibility(View.VISIBLE);
				mLayout.setVisibility(View.GONE);

			}
			else if ("hw".equals(mBundle.getString("Channel"))
					|| "hx".equals(mBundle.getString("Channel")))
			{
				mLabel.setText("成长记录");
				mLayout.setVisibility(View.VISIBLE);
				mShow_Image.setVisibility(View.GONE);
				mShowText.setVisibility(View.GONE);

			}
			else if ("list".equals(mBundle.getString("Channel")))
			{
				mLayout.setVisibility(View.GONE);
				mShow_Image.setVisibility(View.GONE);
				mShowText.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{

		if (keyCode == KeyEvent.KEYCODE_BACK)
		{

			cancelPostDaily();
		}

		return true;
	}

	private void checkUserLogin()
	{
		// 取得用户登录信息
		loginUserInfo = LoginInfo.loadLoginUserInfo(this);

		// 用户名或密码为空，需要重新登录
		if (loginUserInfo.getUserSId().equals("")
				|| loginUserInfo.getPassword().equals(""))
		{

		}
	}

	private void inItUi()
	{
		mPostDateTextView = (EditText) this.findViewById(R.id.post_datetext);
		mPostDateTextView.setOnClickListener(this);

		mDetailEditText = (EditText) this.findViewById(R.id.detailEditText);
		mDetailEditText.setFilters(new InputFilter[]
		{ new InputFilter.LengthFilter(500) });
		mUploadImageBtn = (ImageButton) this
				.findViewById(R.id.daily_upload_image);

		mUploadImageBtn.setOnClickListener(this);
		mUploadImageBtn.setBackgroundResource(R.drawable.selector_post_add);
		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);
		// 显示头像
		final ImageView userIcon = (ImageView) this
				.findViewById(R.id.post_user_icon);
		ImageDownloadTask imageTask = new ImageDownloadTask(
				new ImageDoneCallback()
				{

					@Override
					public void imageLoaded(Bitmap result)
					{
						if (result != null)
						{
							userIcon.setImageBitmap(result);
						}
					}
				});
		imageTask.execute(loginUserInfo.getUserIcon());

		// 选择上传照片
		mPhotosHashMap = new HashMap<String, Bitmap>();

		// 自定义title bar
		setCustomTitleBar();

		// 取得当前日期
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());

		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH);
		mDay = cal.get(Calendar.DAY_OF_MONTH);
		mPostDateTextView.setText(String.format("%d/%02d/%02d", mYear,
				mMonth + 1, mDay));
	}

	public void setCustomTitleBar()
	{
		mTitleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		mTitleBarTitle.setText("新事件");

		mTitleBarCancel = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		mTitleBarCancel.setBackgroundResource(R.drawable.selector_hd_ccl);
		mTitleBarCancel.setOnClickListener(this);

		mTitleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		mTitleBarPost.setBackgroundResource(R.drawable.selector_hd_cont);
		mTitleBarPost.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.daily_upload_image:
			// 选择上传图片
			pickUploadImage();
			break;
		case R.id.titlebar_leftbtn:
			cancelPostDaily();
			break;
		case R.id.titlebar_rightbtn1:
			// 发表文章
			postNewDaily();
			break;
		case R.id.post_datetext:
			// 选择日期
			DateTimePicker.settime().getdate(mPostDateTextView,
					DailyPostActivity.this);
			break;
		default:
			break;
		}
	}

	private Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case UPLOAD_INIT_PROCESS:
			case UPLOAD_IN_PROCESS:
				uploadProcess(msg);
				break;
			case UPLOAD_FILE_DONE:
				// 上传下一张图片
				toUploadNextPhoto(msg.obj.toString());
				break;
			case POST_DAILY_DONE:
				reloadDailyList();
				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};

	private TextView mLabel;

	protected void toErrorMessage(String msgString)
	{
		// 取得出错，显示提示信息
		mProgressDialog.dismiss();
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	protected void toUploadNextPhoto(String fileName)
	{
		if ("".equals(fileName) || "0".equals(fileName))
		{
			
			Toast.makeText(this, "上传失败，请稍候再试。", Toast.LENGTH_LONG).show();

			return;
		}

		String[] newFileName = fileName.split(",");
		// 保存上传后的文件名
		if (newFileName.length <= 1)
		{
			mUploadListData.add(newFileName[0]);
		}
		else
		{
			mUploadListData.add(newFileName[1]);
		}

		if (mUploadListData.size() == mListData.size())
		{
			// 表示已经全部上传完成，开始发表文章
			doPostNewDaily();
			return;
		}

		// 上传下一张图片
		uploadNextPhoto(mUploadListData.size());
	}

	protected void uploadProcess(Message msg)
	{
		// 文件上传过程，状态监听处理
		return;
	}

	protected void reloadDailyList()
	{
		mProgressDialog.dismiss();

		// 从发表新文章页面返回时，重新加载

		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();

		// 设定退出动画
		overridePendingTransition(R.anim.activity_out_to_bottom,
				R.anim.activity_nothing_in_out);
	}

	private void pickUploadImage()
	{
		Intent intent = new Intent();

		intent.setClass(DailyPostActivity.this, SelectPhotoListActivity.class);

		startActivityForResult(intent, SELECT_PIC_BY_PICK_PHOTO);
	}

	private void cancelPostDaily()
	{
		// 取消时候，直接返回主页面
		this.setResult(Activity.RESULT_CANCELED, mIntent);
		this.finish();

		// 设定退出动画
		overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_bottom);
	}

	private void postNewDaily()
	{
		// 没有发表内容时候，提示输入发表内容
		String detail = mDetailEditText.getText().toString().trim();
		if (detail.equals(""))
		{
			Toast.makeText(this, "请说几句话吧，亲。", Toast.LENGTH_LONG).show();
			return;
		}

		if ("tab".equals(mBundle.getString("Channel")))
		{
			if ((mListData == null) || (mListData.size() == 0))
			{
				Toast.makeText(this, "宝宝相册至少登一张照片。", Toast.LENGTH_LONG).show();
				return;
			}
		}

		if ((mListData == null) || (mListData.size() == 0))
		{
			// 没有添付图片，直接发表新信息
			doPostNewDaily();
		}
		else
		{
			// 有添付图片，先上传图片
			toUploadFile();
		}
	}

	protected void toUploadFile()
	{

		mProgressDialog.set_Text("开始上传文件...", true);
		mProgressDialog.show();

		mUploadUtil = UploadInputStreamUtil.getInstance();
		mUploadUtil.setOnUploadProcessListener(this); // 设置监听器监听上传状态

		mUploadListData = new ArrayList<String>();

		// 开始上传第一张图片
		uploadNextPhoto(0);
	}

	protected void uploadNextPhoto(int idx)
	{

		mProgressDialog.set_Text("正在上传文件" + (idx + 1) + "...", true);
		String fileKey = "pic";

		Map<String, String> params = new HashMap<String, String>();
		params.put("UserID", loginUserInfo.getUserSId());

		mUploadUtil.uploadBitmapFile(mListData.get(idx), fileKey,
				ConstantDef.cUrlUploadPhoto, params, this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK)
		{
			if (requestCode == SELECT_PIC_BY_PICK_PHOTO)
			{
				// 显示要上传的图片
				getUploadImageInfo(data);
			}
		}
	}

	private void getUploadImageInfo(Intent data)
	{
		// 显示选择的图片
		Bundle bundle = data.getExtras();
		mListData = bundle.getStringArrayList(ConstantDef.cdImagesSelected);

		LinearLayout layout = (LinearLayout) this
				.findViewById(R.id.daily_post_layout);
		// 清除上次选择的照片
		layout.removeAllViews();

		// 显示本次选择的照片
		SelelctPhotosUtils photosUtils = new SelelctPhotosUtils(this);
		// 图片大小
		int photoSize = DensityUtil.dip2px(this, mUploadPhotoSize);
		for (int i = 0; i < mListData.size(); i++)
		{
			ImageView image = new ImageView(this);
			image.setLayoutParams(new LayoutParams(photoSize, photoSize));
			image.setScaleType(ImageView.ScaleType.FIT_XY);
			image.setPadding(0, 0, 0, 0);

			// 图片间距
			LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(
					photoSize, photoSize);
			lParams.setMargins(DensityUtil.dip2px(this, 4), 0, 0, 0);
			image.setLayoutParams(lParams);

			final String imgPath = mListData.get(i);
			if (mPhotosHashMap.get(imgPath) == null)
			{
				photosUtils.imgExcute(image, new SelectPhotosCallBack()
				{

					@Override
					public void resultImgCall(ImageView imageView, Bitmap bitmap)
					{
						// 加入缓存
						mPhotosHashMap.put(imgPath, bitmap);
						// 显示图片
						imageView.setImageBitmap(bitmap);
					}
				}, imgPath);
			}
			else
			{
				image.setImageBitmap(mPhotosHashMap.get(imgPath));
			}

			layout.addView(image);
		}
	}

	protected void doPostNewDaily()
	{

		mProgressDialog.set_Text("正在更新列表...", true);
		if (!mProgressDialog.isShowing())
		{
			mProgressDialog.show();
		}

		// （发表文章）后台处理参数设定
		Map<String, String> params = new HashMap<String, String>();
		params.put("UserId", loginUserInfo.getUserSId());

		// 发表日期编辑 YYYY/MM/DD -> YYYYMMDD
		String postDate = mPostDateTextView.getText().toString();
		params.put("EventDate", postDate.replaceAll("/", ""));

		params.put("Detail", mDetailEditText.getText().toString());
		// 身高体重头围胸围
		params.put("Height", mHeight.getText().toString());
		params.put("Weight", mWeight.getText().toString());
		params.put("Touwei", mTouWei.getText().toString());
		params.put("Xiongwei", mXiongWei.getText().toString());

		// 添附图片参数设定
		if (null != mUploadListData)
		{
			for (int i = 1; i < mUploadListData.size() + 1; i++)
			{
				// 这里从1开始循环，因为参数是Image1到Imaage9
				params.put("Image" + i, mUploadListData.get(i - 1));
			}
		}
		// 添加标签
		if (mBundle != null)
		{

			if ("tab".equals(mBundle.getString("Channel")))
			{

				String groupId = mBundle.getString(ConstantDef.ccGroup_id);
				String eventId = mBundle.getString(ConstantDef.ccEventId);
				String childId = mBundle.getString(ConstantDef.ccChildId);

				params.put("ChildId", childId);
				params.put("Group_Id", groupId);
				params.put("Event_Id", eventId);
				params.put("Channel", "2");

			}
			else if ("hw".equals(mBundle.getString("Channel")))
			{
				String childId = mBundle.getString(ConstantDef.ccChildId);
				params.put("ChildId", childId);

				params.put("Channel", "1");

			}
			else if ("list".equals(mBundle.getString("Channel")))
			{

				params.put("Channel", "3");
			}

		}
		HttpPostUtil httpUtil = HttpPostUtil.getInstance();
		httpUtil.setOnPostProcessListener(this);
		httpUtil.sendPostMessage(ConstantDef.cUrlPostDaily, params);
	}

	@Override
	public void onUploadDone(int responseCode, String responseMessage)
	{

		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == UploadInputStreamUtil.UPLOAD_SUCCESS_CODE)
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

		Message msg = Message.obtain();
		msg.what = UPLOAD_IN_PROCESS;
		msg.arg1 = 0;
		handler.sendMessage(msg);
	}

	@Override
	public void initUpload(int fileSize)
	{

		Message msg = Message.obtain();
		msg.what = UPLOAD_INIT_PROCESS;
		msg.arg1 = fileSize;
		handler.sendMessage(msg);
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage)
	{
		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE)
		{
			msg.what = POST_DAILY_DONE;
		}
		else
		{
			msg.what = POST_DAILY_ERROR;
		}
		handler.sendMessage(msg);
	}

}
