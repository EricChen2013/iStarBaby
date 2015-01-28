package cn.leature.istarbaby.child;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.ChildrenInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.UserInfo;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.network.ImageDownloadTask;
import cn.leature.istarbaby.network.ImageDownloadTask.ImageDoneCallback;
import cn.leature.istarbaby.network.UploadInputStreamUtil;
import cn.leature.istarbaby.network.UploadInputStreamUtil.OnUploadProcessListener;
import cn.leature.istarbaby.selecttime.DateTimePicker;
import cn.leature.istarbaby.selecttime.TimeCycleUtil;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DateUtilsDef;
import cn.leature.istarbaby.utils.ResizeImage;

public class ChildEditActivity extends Activity implements OnClickListener,
		OnPostProcessListener, OnUploadProcessListener
{

	// 文件上传初始化（Message用）
	private static final int UPLOAD_INIT_PROCESS = 1;
	// 文件上传中（Message用）
	private static final int UPLOAD_IN_PROCESS = 2;
	// 图片上传成功（Message用）
	protected static final int UPLOAD_FILE_DONE = 3;
	// 图片上传出错（Message用）
	protected static final int UPLOAD_FILE_ERROR = -1;
	private static final int CHILD_HTTPGET_ERROR = -9;
	// 文章发表成功（Message用）o
	protected static final int CHILD_EDIT_DONE = 4;
	// 文章发表出错（Message用）
	protected static final int CHILD_EDIT_ERROR = -2;

	// 出生时图片的宽高比例
	private static final int CHILD_IMAGE_WIDTH = 4;
	private static final int CHILD_IMAGE_HEIGHT = 3;
	private String strImgPath = "";// 照片文件绝对路径
	private static final int RESULT_CAPTURE_IMAGE = 2;//
	private LzProgressDialog mProgressDialog;
	private HttpPostUtil mHttpUtil;
	// 用户登录信息
	private UserInfo mLoginUserInfo;

	private TextView titleBarTitle;

	private ImageButton titleBarBack, titleBarEdit;

	private EditText mDanshengshijian, mFenmianShijian, mBirthdayTime,
			mFenmianYuding;
	private EditText mHeight, mXiongwei, mTouwei, mWeight, mChuchanshunxu;
	private EditText mNickname, mFenmianDifang, mHuanyunQijian;

	private ImageView mChildIcon, mBoy, mGirl;
	private String mChildImagePath = "";
	private String mUploadImageName = "";

	private String child_id;

	// 1：男孩， 2：女孩， 其他：未定
	private int mBabyGender = 0;

	private UploadInputStreamUtil mUploadUtil;

	private Intent mIntent;
	private Bundle mBundle;
	private int screenWidth, screenHeight;
	private AlertDialog mMyDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_child_edit);

		// 检查用户登录状况
		checkUserLogin();
		// 屏蔽聚焦弹出对话框
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mHttpUtil = HttpPostUtil.getInstance();

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();

		mIntent = this.getIntent();
		mBundle = this.getIntent().getExtras();

		// 控件初始化
		inItUi();
		setCustomTitleBar();

		// 显示默认图片
		Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(
				R.drawable.noimage_child)).getBitmap();
		mChildIcon.setImageBitmap(ResizeImage.resizeBitmapWithRadio(bitmap,
				CHILD_IMAGE_WIDTH, CHILD_IMAGE_HEIGHT));

		// 取得详细信息
		getChildDetail();

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	/**
	 * @Title: getChildDetail
	 * @Description: TODO
	 * @return: void
	 */
	private void getChildDetail()
	{

		// 日志List取得的参数设定
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", mLoginUserInfo.getUserSId());
		// ChildNo
		param.put("ChildNo", mBundle.getString(ConstantDef.ccChildNo));

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback()
		{

			@Override
			public void requestWithGetDone(String result)
			{

				mProgressDialog.dismiss();

				if ((result == null) || (result.equals("0")))
				{
					// 处理出错
					Message msg = Message.obtain();
					msg.arg1 = -1;
					msg.obj = result;

					msg.what = CHILD_HTTPGET_ERROR;

					handler.sendMessage(msg);

					return;
				}

				JSONObject jsonObject;
				try
				{
					jsonObject = new JSONObject(result)
							.getJSONObject("children_detail");

					ChildrenInfo childInfo = new ChildrenInfo(jsonObject);

					// 显示信息
					mNickname.setText(childInfo.getChildName());

					// 转换时间显示yyyy/mm/dd hh:ss
					if (!"".equals(childInfo.getBirthday()))
					{

						mBirthdayTime.setText(DateUtilsDef.dateFormatString(
								childInfo.getBirthday(), "yyyy/MM/dd"));
					}
					TimeCycleUtil.showtime().showHourTime(
							childInfo.getBirthday_time(), mDanshengshijian);

					child_id = childInfo.getChild_id();
					mChuchanshunxu.setText(childInfo.getChild_no());
					if (childInfo.getGender().equals("1"))
					{
						mBabyGender = 1;
						mBoy.setImageResource(R.drawable.baby_sex_boy_on);
						mGirl.setImageResource(R.drawable.baby_sex_girl);
					}
					else if (childInfo.getGender().equals("2"))
					{
						mBabyGender = 2;
						mBoy.setImageResource(R.drawable.baby_sex_boy);
						mGirl.setImageResource(R.drawable.baby_sex_girl_on);
					}

					mHeight.setText(childInfo.getHeight());
					mTouwei.setText(childInfo.getTouwei());
					mXiongwei.setText(childInfo.getXiongwei());
					mWeight.setText(childInfo.getWeight());

					TimeCycleUtil.showtime().showHourTime(
							childInfo.getFenmian_shijian(), mFenmianShijian);
					if (!"".equals(childInfo.getFenmian_yuding()))
					{

						mFenmianYuding.setText(DateUtilsDef.dateFormatString(
								childInfo.getFenmian_yuding(), "yyyy/MM/dd"));
					}

					mFenmianDifang.setText(childInfo.getFenmian_difang());
					mHuanyunQijian.setText(childInfo.getHuaiyun_qijian());

					// 显示登录图片
					String imgPath = childInfo.getImage1().trim();
					if (imgPath.length() > 0)
					{
						// 有图片
						ImageDownloadTask imageTask = new ImageDownloadTask(
								new ImageDoneCallback()
								{

									@Override
									public void imageLoaded(Bitmap result)
									{
										if (null == result)
										{
											return;
										}

										mChildIcon.setImageBitmap(ResizeImage
												.resizeBitmapWithRadio(result,
														CHILD_IMAGE_WIDTH,
														CHILD_IMAGE_HEIGHT));
									}
								});
						imageTask.execute(imgPath);
					}
				}
				catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlChildrenDetail, param);
	}

	/**
	 * @Title: checkUserLogin
	 * @Description: TODO
	 * @return: void
	 */
	private void checkUserLogin()
	{
		// 取得用户登录信息
		mLoginUserInfo = LoginInfo.loadLoginUserInfo(this);

		// 用户名或密码为空，需要重新登录
		if (mLoginUserInfo.getUserSId().equals("")
				|| mLoginUserInfo.getPassword().equals(""))
		{

		}
	}

	private void inItUi()
	{

		mHeight = (EditText) findViewById(R.id.compile_height);
		mWeight = (EditText) findViewById(R.id.compile_weitht);
		mXiongwei = (EditText) findViewById(R.id.compile_xiongwei);
		mTouwei = (EditText) findViewById(R.id.compile_touwei);
		// 限制小数点一位
		EditTextListener.addTextListener().Listener(mHeight, this);
		EditTextListener.addTextListener().Listener(mWeight, this);
		EditTextListener.addTextListener().Listener(mXiongwei, this);
		EditTextListener.addTextListener().Listener(mTouwei, this);

		mNickname = (EditText) findViewById(R.id.Nickname);
		mBirthdayTime = (EditText) findViewById(R.id.BirthdayTime);
		mDanshengshijian = (EditText) findViewById(R.id.child_edit_danshengshijian);
		mBirthdayTime.setOnClickListener(this);
		mDanshengshijian.setOnClickListener(this);
		mFenmianYuding = (EditText) findViewById(R.id.FenmianYuding);
		mFenmianShijian = (EditText) findViewById(R.id.FenmianShijian);
		mFenmianShijian.setOnClickListener(this);
		mFenmianYuding.setOnClickListener(this);

		mChuchanshunxu = (EditText) findViewById(R.id.chuchanshun);
		mFenmianDifang = (EditText) findViewById(R.id.FenmianDifang);
		mHuanyunQijian = (EditText) findViewById(R.id.HuanyunQijian);
		mChildIcon = (ImageView) findViewById(R.id.child_icon);
		mChildIcon.setOnClickListener(this);
		mChildIcon.requestFocus();
		mBoy = (ImageView) findViewById(R.id.child_edit_boy);
		mGirl = (ImageView) findViewById(R.id.child_edit_girl);
		mBoy.setOnClickListener(this);
		mGirl.setOnClickListener(this);

		ForEdittextMode(mNickname);
		ForEdittextMode(mFenmianDifang);

	}

	public void ForEdittextMode(final EditText editText)
	{
		editText.addTextChangedListener(new TextWatcher()
		{
			String tmp = "";
			String digits = "`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& *（）——+|{}【】‘；：”“’。，、？]";

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
				editText.setSelection(s.length());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
				tmp = s.toString();
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				String str = s.toString();
				if (str.equals(tmp))
				{
					return;
				}
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < str.length(); i++)
				{
					if (digits.indexOf(str.charAt(i)) < 0)
					{
						sb.append(str.charAt(i));
					}
				}
				tmp = sb.toString();
				editText.setText(tmp);
			}
		});
	}

	private void setCustomTitleBar()
	{

		// 设置主菜单
		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("编辑");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_ccl);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener(this);
		titleBarEdit.setBackgroundResource(R.drawable.child_edit_cont);
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage)
	{

		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE)
		{
			msg.what = CHILD_EDIT_DONE;
		}
		else
		{
			msg.what = CHILD_EDIT_ERROR;
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
			case UPLOAD_INIT_PROCESS:
			case UPLOAD_IN_PROCESS:
				// 文件上传中
				break;
			case UPLOAD_FILE_DONE:
				uploadImageDone(msg.obj.toString());
				break;
			case CHILD_EDIT_DONE:// 保存
				saveDetailDone(msg.obj.toString());
				break;
			case CHILD_HTTPGET_ERROR:
				ChildEditActivity.this.finish();
				Toast.makeText(ChildEditActivity.this, "处理出错请稍后再试",
						Toast.LENGTH_LONG).show();
				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};

	protected void saveDetailDone(String listResult)
	{

		if (mProgressDialog.isShowing())
		{
			mProgressDialog.dismiss();
		}

		if ((listResult == null) || "0".equals(listResult))
		{

			Toast.makeText(this, "保存失败", Toast.LENGTH_LONG).show();
		}
		else
		{

			// 登录成功，保存登录用户
			Map<String, String> param = new HashMap<String, String>();
			param.put("UserId", mLoginUserInfo.getUserSId());
			param.put("UserString", listResult);
			LoginInfo.saveLoginUser(this, param);

			mBundle.putString(ConstantDef.ccChildNo, mChuchanshunxu.getText()
					.toString().trim());

			mIntent.putExtras(mBundle);

			this.setResult(Activity.RESULT_OK, mIntent);
			this.finish();

			// 设定退出动画
			overridePendingTransition(R.anim.activity_nothing_in_out,
					R.anim.activity_out_to_bottom);
		}

	}

	/**
	 * @Title: uploadImageDone
	 * @Description: TODO
	 * @param string
	 * @return: void
	 */
	protected void uploadImageDone(String result)
	{
		mProgressDialog.dismiss();
		if ((result == null) || "0".equals(result))
		{
			Toast.makeText(this, "上传失败，请稍候再试。", Toast.LENGTH_LONG).show();

			return;
		}

		String[] newFileName = result.split(",");
		// 保存上传后的文件名
		if (newFileName.length <= 1)
		{
			mUploadImageName = newFileName[0];
		}
		else
		{
			mUploadImageName = newFileName[1];
		}

		// 保存修改信息
		startSaveChildDetail();

	}

	/**
	 * @Title: toErrorMessage
	 * @Description: TODO
	 * @param string
	 * @return: void
	 */
	protected void toErrorMessage(String msgString)
	{
		// 取得出错，显示提示信息
		mProgressDialog.dismiss();

		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	/**
	 * @param imageName
	 * @Title: toSaveChildDetail
	 * @Description: TODO
	 * @return: void
	 */
	protected void startSaveChildDetail()
	{
		String height = mHeight.getText().toString().trim();
		String weight = mWeight.getText().toString().trim();
		String xiongwei = mXiongwei.getText().toString().trim();
		String touwei = mTouwei.getText().toString().trim();
		String nickname = mNickname.getText().toString().trim();
		String birthdayTime = mBirthdayTime.getText().toString().trim();
		String danshengshijian = mDanshengshijian.getText().toString().trim();

		String fenmianYuding = mFenmianYuding.getText().toString().trim();
		String fenmianShijian = mFenmianShijian.getText().toString().trim();
		String huanyunQijian = mHuanyunQijian.getText().toString().trim();
		String fenmianDifang = mFenmianDifang.getText().toString().trim();
		String chuchanshunxu = mChuchanshunxu.getText().toString().trim();

		Map<String, String> param = new HashMap<String, String>();
		param.put("Gender", mBabyGender + "");

		param.put("UserId", mLoginUserInfo.getUserSId());
		param.put("ChildId", child_id);

		param.put("ChildNo", chuchanshunxu);
		param.put("Name", nickname);
		if (mUploadImageName.length() > 0)
		{
			param.put("Image1", mUploadImageName);
		}

		param.put("Height", height);
		param.put("Weight", weight);
		param.put("Xiongwei", xiongwei);
		param.put("Touwei", touwei);

		param.put("Birthday", birthdayTime.replace("/", ""));
		param.put("birthdayTime", danshengshijian.replace(":", ""));

		param.put("FenmianYuding", fenmianYuding.replace("/", ""));
		param.put("FenmianShijian", fenmianShijian.replace(":", ""));
		param.put("FenmianDifang", fenmianDifang);
		param.put("HuaiyunQijian", huanyunQijian);

		mHttpUtil.setOnPostProcessListener(this);
		mHttpUtil.sendPostMessage(ConstantDef.cUrlChildrenEdit, param);

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.child_edit_boy:

			genderImageClick(v.getId());
			break;

		case R.id.child_edit_girl:
			genderImageClick(v.getId());
			break;

		case R.id.titlebar_rightbtn1:
			// 保存信息
			if (checkInputs())
			{
				startChildEdit();
			}
			break;

		case R.id.titlebar_leftbtn:
			backPrePage();
			break;

		case R.id.child_icon:

			customDialog();

			break;

		case R.id.FenmianYuding:
			DateTimePicker.settime().getdate(mFenmianYuding,
					ChildEditActivity.this);
			break;

		case R.id.BirthdayTime:
			DateTimePicker.settime().getdate(mBirthdayTime,
					ChildEditActivity.this);

			break;

		case R.id.child_edit_danshengshijian:
			DateTimePicker.settime().gethourtime(mDanshengshijian,
					ChildEditActivity.this);
			break;

		case R.id.image_capture:

			cameraMethod();

			break;

		case R.id.image_album:

			ChildEditSelectImageUtils.openLocalImage(ChildEditActivity.this);

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
		mMyDialog.show();
		image_capture.setOnClickListener(this);
		image_album.setOnClickListener(this);
		image_quxiao.setOnClickListener(this);
	}

	public void cameraMethod()
	{
		Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		strImgPath = Environment.getExternalStorageDirectory().toString()
				+ "/CONSDCGMPIC/";// 存放照片的文件夹
		String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date()) + ".jpg";// 照片命名
		File out = new File(strImgPath);
		if (!out.exists())
		{
			out.mkdirs();
		}
		out = new File(strImgPath, fileName);
		strImgPath = strImgPath + fileName;// 该照片的绝对路径
		Uri uri = Uri.fromFile(out);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		imageCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		startActivityForResult(imageCaptureIntent, RESULT_CAPTURE_IMAGE);

	}

	private void backPrePage()
	{
		// 取消时候，直接返回主页面
		this.setResult(Activity.RESULT_CANCELED, mIntent);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_bottom);
	}

	private boolean checkInputs()
	{

		// 检查项目是否有输入
		if ("".equals(mBirthdayTime.getText().toString().trim()))
		{
			Toast.makeText(this, "[生日]未输入", Toast.LENGTH_LONG).show();
			return false;
		}

		if (mBabyGender == 0)
		{
			Toast.makeText(this, "[性别]未输入", Toast.LENGTH_LONG).show();
			return false;
		}

		if ("".equals(mNickname.getText().toString().trim()))
		{
			Toast.makeText(this, "[名字]未输入", Toast.LENGTH_LONG).show();
			return false;
		}

		return checkChuchanshunxu();
	}

	// 宝宝排行是否重复
	private boolean checkChuchanshunxu()
	{
		String num = mChuchanshunxu.getText().toString().trim();

		if ("".equals(num))
		{
			Toast.makeText(this, "[宝宝排行]未输入", Toast.LENGTH_LONG).show();
			return false;
		}

		if (Integer.parseInt(num) < 1)
		{
			Toast.makeText(this, "[宝宝排行]输入有误", Toast.LENGTH_LONG).show();
			return false;
		}

		// 宝宝排行没修改
		if (num.equals(mBundle.getString(ConstantDef.ccChildNo)))
		{
			return true;
		}

		// 如果存在不能更改
		List<ChildrenInfo> childrenInfo = mLoginUserInfo.getChildList();
		for (int i = 0; i < childrenInfo.size(); i++)
		{

			if (num.equals(childrenInfo.get(i).getChild_no()))
			{
				Toast.makeText(ChildEditActivity.this, "[宝宝排行]已经存在",
						Toast.LENGTH_LONG).show();
				return false;
			}
		}

		return true;
	}

	/**
	 * @Title: startChildEdit
	 * @Description: TODO
	 * @return: void
	 */
	private void startChildEdit()
	{

		mProgressDialog = new LzProgressDialog(ChildEditActivity.this);
		mProgressDialog.setCancelable(true);
		mProgressDialog.set_Text("正在保存", true);
		mProgressDialog.show();

		if ("".equals(mChildImagePath))
		{
			// 没有更改图片
			startSaveChildDetail();
		}
		else
		{
			// 更改图片时，先上传图片
			startUploadImage();
		}
	}

	/**
	 * @Title: startUploadImage
	 * @Description: TODO
	 * @return: void
	 */
	private void startUploadImage()
	{
		mUploadUtil = UploadInputStreamUtil.getInstance();
		mUploadUtil.setOnUploadProcessListener(this); // 设置监听器监听上传状态

		Map<String, String> params = new HashMap<String, String>();
		params.put("UserID", mLoginUserInfo.getUserSId());
		mUploadUtil.uploadBitmapFile(mChildImagePath, "pic",
				ConstantDef.cUrlUploadPhoto, params, this);

	}

	private void genderImageClick(int id)
	{
		switch (id)
		{
		case R.id.child_edit_boy:

			// 选择男孩
			if (mBabyGender != 1)
			{
				mBabyGender = 1;
				mBoy.setImageResource(R.drawable.baby_sex_boy_on);
				mGirl.setImageResource(R.drawable.baby_sex_girl);
			}
			break;
		case R.id.child_edit_girl:
			// 选择女孩
			if (mBabyGender != 2)
			{
				mBabyGender = 2;
				mBoy.setImageResource(R.drawable.baby_sex_boy);
				mGirl.setImageResource(R.drawable.baby_sex_girl_on);
			}
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent)
	{
		super.onActivityResult(requestCode, resultCode, intent);

		if (mMyDialog.isShowing())
		{
			mMyDialog.dismiss();
		}

		if (resultCode != RESULT_OK)
		{
			return;
		}
		switch (requestCode)
		{

		// 手机相册获取图片
		case ChildEditSelectImageUtils.GET_IMAGE_FROM_PHONE:

			// 可以得到图片在Content：//。。。中的地址，把它转化成绝对地址如下
			Uri uri = intent.getData();
			String[] proj =
			{ MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(uri, proj, null, null,
					null);
			if (cursor != null)
			{
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				if (cursor.getCount() > 0 && cursor.moveToFirst())
				{
					mChildImagePath = cursor.getString(column_index);
				}
			}

			if (intent != null && intent.getData() != null)
			{

				Bitmap bitmap = ChildEditSelectImageUtils.decodeBitmapByUri(
						this, intent.getData());
				mChildIcon.setImageBitmap(ResizeImage.resizeBitmapWithRadio(
						bitmap, CHILD_IMAGE_WIDTH, CHILD_IMAGE_HEIGHT));
			}
			break;
		case RESULT_CAPTURE_IMAGE:// 拍照
			if (resultCode == RESULT_OK)
			{
				mChildImagePath = strImgPath;

				Bitmap bitmap = ResizeImage.getBitmapFromFileName(strImgPath,
						screenWidth, screenHeight);
				mChildIcon.setImageBitmap(ResizeImage.resizeBitmapWithRadio(
						bitmap, CHILD_IMAGE_WIDTH, CHILD_IMAGE_HEIGHT));

			}
			break;
		default:
			break;
		}

	}

	/**
	 * @Title: onUploadDone
	 * @Description: TODO
	 * @param responseCode
	 * @param responseMessage
	 * @see cn.leature.istarbaby.network.UploadUtil.OnUploadProcessListener#onUploadDone(int,
	 *      java.lang.String)
	 */
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

	/**
	 * @Title: onUploadProcess
	 * @Description: TODO
	 * @param uploadSize
	 * @see cn.leature.istarbaby.network.UploadUtil.OnUploadProcessListener#onUploadProcess(int)
	 */
	@Override
	public void onUploadProcess(int uploadSize)
	{

		Message msg = Message.obtain();
		msg.what = UPLOAD_INIT_PROCESS;
		msg.arg1 = 0;
		handler.sendMessage(msg);

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

	/**
	 * @Title: initUpload
	 * @Description: TODO
	 * @param fileSize
	 * @see cn.leature.istarbaby.network.UploadUtil.OnUploadProcessListener#initUpload(int)
	 */
	@Override
	public void initUpload(int fileSize)
	{

		Message msg = Message.obtain();
		msg.what = UPLOAD_INIT_PROCESS;
		msg.arg1 = fileSize;
		handler.sendMessage(msg);
	}

}
