package cn.leature.istarbaby.setting;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.SettingUserInfo;
import cn.leature.istarbaby.domain.UserInfo;
import cn.leature.istarbaby.info.CropImageActivity;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.network.UploadInputStreamUtil;
import cn.leature.istarbaby.network.UploadInputStreamUtil.OnUploadProcessListener;
import cn.leature.istarbaby.network.UploadUtil;
import cn.leature.istarbaby.selecttime.DateTimePicker;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DateUtilsDef;
import cn.leature.istarbaby.utils.ResizeImage;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SettingProfileActivity extends Activity implements
		OnClickListener, OnUploadProcessListener, OnPostProcessListener {

	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	private ImageView mUserIcon;
	private EditText mBirthday, mSettingname, mSettinggender, mMobile, mEmail,
			mAddress;
	private RelativeLayout mGenderLayout;
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private LzProgressDialog mProgressDialog;
	private HttpPostUtil mHttpUtil;
	private Intent mIntent;

	private boolean isInfoEdited = false;
	private boolean isEditMode = false;
	private ImageView mManImage, mWomanImage;
	private TextView mManText, mWomanText;
	// 用户登录信息
	private UserInfo mLoginUserInfo;
	// 1：男孩， 2：女孩， 其他：不明
	private String mBabyGender = "0";
	private int mCropImageWH = 400;
	private static final int RESULT_CAPTURE_IMAGE = 2;//
	// 选择的头像
	private Bitmap mIconBitmap = null;
	// 图片上传成功（Message用）
	protected static final int UPLOAD_ICON_DONE = 3;
	// 图片上传出错（Message用）
	protected static final int UPLOAD_ICON_ERROR = -1;
	// 修改成功（Message用）
	protected static final int PROFILE_EDIT_DONE = 4;
	// 修改出错（Message用）
	protected static final int PROFILE_EDIT_ERROR = -2;
	private AlertDialog mMyDialog;
	private String mChildImagePath = "";
	private int screenWidth, screenHeight;
	private static final int FLAG_CHOOSE_IMG = 5;
	private static final int FLAG_CHOOSE_PHONE = 6;
	private static final int FLAG_MODIFY_FINISH = 7;
	private static String localTempImageFileName = "";
	public static final File FILE_SDCARD = Environment
			.getExternalStorageDirectory();
	public static final String IMAGE_PATH = "My_weixin";
	public static final File FILE_LOCAL = new File(FILE_SDCARD, IMAGE_PATH);
	public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL,
			"images/screenshots");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_profile);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		mHttpUtil = HttpPostUtil.getInstance();
		mIntent = this.getIntent();

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.noimage)
				.showImageForEmptyUri(R.drawable.noimage)
				.showImageOnFail(R.drawable.noimage).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

		setCustomTitleBar();
		inItUi();

		// 检查用户登录状况
		checkUserLogin();

		getDetail();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	private void checkUserLogin() {
		// 取得用户登录信息
		mLoginUserInfo = LoginInfo.loadLoginUserInfo(this);

		// 用户名或密码为空，需要重新登录
		if (mLoginUserInfo.getUserSId().equals("")
				|| mLoginUserInfo.getPassword().equals("")) {

		}
	}

	private void getDetail() {
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", mLoginUserInfo.getUserSId());

		mProgressDialog.set_Text("请稍等...", true);
		mProgressDialog.show();
		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();
				if ((result == null) || (result.equals("0"))) {
					// 处理出错
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					SettingUserInfo info = new SettingUserInfo(jsonObject);

					mBirthday.setText(DateUtilsDef.dateFormatString(
							info.getBirthday(), "yyyy/MM/dd"));
					mSettingname.setText(info.getName());

					mMobile.setText(info.getMobile());
					mEmail.setText(info.getEmail());
					mAddress.setText(info.getAddress());
					// 图片加载
					imageLoader.displayImage(
							HttpClientUtil.SERVER_PATH + info.getIcon(),
							mUserIcon, options);

					mBabyGender = info.getGender();
					// 性别
					if ("1".equals(info.getGender())) {
						mSettinggender.setText("男性");
					} else if ("2".equals(info.getGender())) {
						mSettinggender.setText("女性");
					} else {
						mSettinggender.setText("未知");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		httpGetUtil.execute(ConstantDef.cUrlUserInfoDetail, param);
	}

	private void inItUi() {
		mUserIcon = (ImageView) findViewById(R.id.settingchild_icon);
		mUserIcon.setOnClickListener(this);

		mBirthday = (EditText) findViewById(R.id.settingbirthday);
		mBirthday.setOnClickListener(this);

		mSettingname = (EditText) findViewById(R.id.Settingname);
		mSettinggender = (EditText) findViewById(R.id.settinggender);
		mMobile = (EditText) findViewById(R.id.mobile);
		mEmail = (EditText) findViewById(R.id.email_addr);
		mAddress = (EditText) findViewById(R.id.Address);
		mAddress = (EditText) findViewById(R.id.Address);
		mGenderLayout = (RelativeLayout) findViewById(R.id.gender_layout);

		mManImage = (ImageView) findViewById(R.id.setting_boy);
		mWomanImage = (ImageView) findViewById(R.id.setting_girl);
		mManText = (TextView) findViewById(R.id.setting_textboy);
		mWomanText = (TextView) findViewById(R.id.setting_textgirl);

		mManImage.setOnClickListener(this);
		mWomanImage.setOnClickListener(this);
		mManText.setOnClickListener(this);
		mWomanText.setOnClickListener(this);

		mMobile.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {

				String userId = s.toString().trim();

				if (userId.length() != 11) {
					// 非正常UserId长度，返回空
					return;
				}
				if (s.length() == 11) {
					if (getPhone(s.toString()) == false) {

						Toast.makeText(SettingProfileActivity.this,
								"号码有误，请重新输入", Toast.LENGTH_LONG).show();
						return;
					}
				}
			}
		});
	}

	private boolean getPhone(String mobile) {
		Pattern p = Pattern
				.compile("^((14[5,7])|(13[0-9])|(15[^4,\\D])|(18[0-3,5-9]))\\d{8}$");
		Matcher matcher = p.matcher(mobile);
		return matcher.matches();
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("个人信息");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener(this);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_edt);
	}

	private static String strImgPath = "";// 照片文件绝对路径

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		case R.id.titlebar_rightbtn1:
			profileEdit();
			break;
		case R.id.settingchild_icon:
			// SelectCropImageUtil.getCropRectImage(this, mCropImageWH);
			customDialog();

			break;
		case R.id.settingbirthday:
			if (isEditMode) {
				DateTimePicker.settime().getdate(mBirthday,
						SettingProfileActivity.this);
			}
			break;
		case R.id.setting_textboy:

			genderImageClick(R.id.setting_textboy);
			break;
		case R.id.setting_textgirl:

			genderImageClick(R.id.setting_textgirl);
			break;

		case R.id.image_capture:

			cameraMethod();

			break;

		case R.id.image_album:
			mMyDialog.dismiss();
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);
			intent.setType("image/*");
			startActivityForResult(intent, FLAG_CHOOSE_IMG);
			break;

		case R.id.image_quxiao:

			mMyDialog.dismiss();

			break;

		default:
			break;
		}
	}

	private void customDialog() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View myLoginView = layoutInflater.inflate(R.layout.dialog_pick_photo,
				null);
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

	public void cameraMethod() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				localTempImageFileName = "";
				localTempImageFileName = String.valueOf((new Date()).getTime())
						+ ".png";
				File filePath = FILE_PIC_SCREENSHOT;
				if (!filePath.exists()) {
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
			} catch (ActivityNotFoundException e) {
				//
			}
		}

	}

	private void genderImageClick(int id) {
		switch (id) {
		case R.id.setting_textboy:
			// 选择男孩
			mBabyGender = "1";
			mManImage.setImageResource(R.drawable.radio2_btn);
			mWomanImage.setImageResource(R.drawable.radio1_btn);
			break;
		case R.id.setting_textgirl:
			// 选择女孩
			mBabyGender = "2";
			mManImage.setImageResource(R.drawable.radio1_btn);
			mWomanImage.setImageResource(R.drawable.radio2_btn);
			break;
		}
	}

	/**
	 * @Title: profileEdit
	 * @Description: TODO
	 * @return: void
	 */
	private void profileEdit() {
		if (isEditMode) {
			// 编辑状态，保存信息
			startSaveChildDetail();
		} else {
			// 非编辑状态，显示可输入编辑状态
			isEditMode = true;
			// 菜单按钮变成 保存

			LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp2.setMargins(0, 4, 0, 0);

			titleBarEdit.setBackgroundResource(R.drawable.child_edit_cont);
			mSettingname.setBackgroundResource(R.drawable.text_field);
			mSettingname.setEnabled(true);
			mSettingname.setFocusable(true);
			mSettingname.setFocusableInTouchMode(true);
			// mSettingname.setGravity(Gravity.LEFT);
			mSettingname.setHint(R.string.input_hint);

			mBirthday.setBackgroundResource(R.drawable.text_field);
			// mBirthday.setGravity(Gravity.LEFT);
			mBirthday.setHint(R.string.input_hint);

			mMobile.setBackgroundResource(R.drawable.text_field);
			mMobile.setEnabled(true);
			mMobile.setFocusable(true);
			mMobile.setFocusableInTouchMode(true);
			mMobile.setHint(R.string.input_hint);
			mMobile.setLayoutParams(lp2);

			mEmail.setBackgroundResource(R.drawable.text_field);
			mEmail.setEnabled(true);
			mEmail.setFocusable(true);
			mEmail.setFocusableInTouchMode(true);
			mEmail.setHint(R.string.input_hint);
			mEmail.setLayoutParams(lp2);

			mAddress.setBackgroundResource(R.drawable.text_field);
			mAddress.setEnabled(true);
			mAddress.setFocusable(true);
			mAddress.setFocusableInTouchMode(true);
			mAddress.setHint(R.string.input_hint);
			mAddress.setLayoutParams(lp2);

			mGenderLayout.setVisibility(View.VISIBLE);
			mSettinggender.setVisibility(View.INVISIBLE);

			// 性别
			if ("1".equals(mBabyGender)) {
				// 男性
				mManImage.setImageResource(R.drawable.radio2_btn);
				mWomanImage.setImageResource(R.drawable.radio1_btn);
			} else if ("2".equals(mBabyGender)) {
				// 女性
				mManImage.setImageResource(R.drawable.radio1_btn);
				mWomanImage.setImageResource(R.drawable.radio2_btn);
			}
		}
	}

	private void backPrePage() {
		if (isInfoEdited) {
			// 内容被编辑
			this.setResult(Activity.RESULT_OK, mIntent);
		} else {
			// 未编辑
			this.setResult(Activity.RESULT_CANCELED, mIntent);
		}
		this.finish();
		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backPrePage();
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK) {
			return;
		}

		if (mMyDialog.isShowing()) {
			mMyDialog.dismiss();
		}

		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == FLAG_CHOOSE_IMG && resultCode == RESULT_OK) {
			if (data != null) {
				Uri uri = data.getData();
				if (!TextUtils.isEmpty(uri.getAuthority())) {
					Cursor cursor = getContentResolver().query(uri,
							new String[] { MediaStore.Images.Media.DATA },
							null, null, null);
					if (null == cursor) {
						Toast.makeText(SettingProfileActivity.this, "图片没找到", 0)
								.show();
						return;
					}
					cursor.moveToFirst();
					String path = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					cursor.close();
					cursor.close();
					Intent intent = new Intent(this, CropImageActivity.class);
					intent.putExtra("path", path);
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				} else {
					Intent intent = new Intent(this, CropImageActivity.class);
					intent.putExtra("path", uri.getPath());
					startActivityForResult(intent, FLAG_MODIFY_FINISH);
				}
			}
		} else if (requestCode == FLAG_CHOOSE_PHONE && resultCode == RESULT_OK) {
			File f = new File(FILE_PIC_SCREENSHOT, localTempImageFileName);
			Intent intent = new Intent(this, CropImageActivity.class);
			intent.putExtra("path", f.getAbsolutePath());
			startActivityForResult(intent, FLAG_MODIFY_FINISH);
		} else if (requestCode == FLAG_MODIFY_FINISH && resultCode == RESULT_OK) {
			if (data != null) {

				final String path = data.getStringExtra("path");
				Bitmap bm = BitmapFactory.decodeFile(path);
				// 裁剪成正方形图片
				mIconBitmap = ResizeImage.resizeBitmapWithRadio(bm, 1, 1);
				uploadIconBitmap();

			}
		}
		// switch (requestCode)
		// {
		//
		// case SelectCropImageUtil.SELECT_CROP_IMAGE:
		// Bitmap bm = intent.getExtras().getParcelable("data");
		//
		// // 裁剪成正方形图片
		// mIconBitmap = ResizeImage.resizeBitmapWithRadio(bm, 1, 1);
		//
		// // 保存头像
		// uploadIconBitmap();
		// break;
		//
		// case RESULT_CAPTURE_IMAGE:// 拍照
		// if (resultCode == RESULT_OK)
		// {
		//
		// Bitmap bitmap = ResizeImage.getBitmapFromFileName(strImgPath,
		// screenWidth, screenHeight);
		// mIconBitmap = ResizeImage.resizeBitmapWithRadio(bitmap, 1, 1);
		//
		// }
		// // 保存头像
		// uploadIconBitmap();
		// break;
		// default:
		// break;
		// }
	}

	private void uploadIconBitmap() {
		mProgressDialog.set_Text("保存中...", true);
		mProgressDialog.show();

		UploadInputStreamUtil streamUtil = UploadInputStreamUtil.getInstance();
		streamUtil.setOnUploadProcessListener(this); // 设置监听器监听上传状态

		Map<String, String> params = new HashMap<String, String>();
		params.put("UserID", LoginInfo.getLoginUserId(this));
		streamUtil.uploadInputStream(mIconBitmap, "pic",
				ConstantDef.cUrlUploadUserIcon, params);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case UPLOAD_ICON_DONE:
				uploadImageDone(msg.obj.toString());
				break;
			case PROFILE_EDIT_DONE: // 保存
				saveDetailDone(msg.obj.toString());
				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};

	protected void toErrorMessage(String msgString) {
		// List取得出错，显示提示信息
		mProgressDialog.dismiss();
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	private void uploadImageDone(String result)

	{
		mProgressDialog.dismiss();

		if ((result == null) || "0".equals(result)) {
			Toast.makeText(this, "图片上传失败。", Toast.LENGTH_LONG).show();

		} else {
			// 显示头像
			mUserIcon.setImageBitmap(mIconBitmap);

			// 保存用户信息
			saveLoginUserInfo(result);
		}
	}

	/**
	 * @Title: onUploadDone
	 * @Description: TODO
	 * @param responseCode
	 * @param responseMessage
	 * @see cn.leature.istarbaby.network.UploadInputStreamUtil.OnUploadProcessListener#onUploadDone(int,
	 *      java.lang.String)
	 */
	@Override
	public void onUploadDone(int responseCode, String responseMessage) {
		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == UploadUtil.UPLOAD_SUCCESS_CODE) {
			msg.what = UPLOAD_ICON_DONE;
		} else {
			msg.what = UPLOAD_ICON_ERROR;
		}
		handler.sendMessage(msg);
	}

	/**
	 * @Title: onUploadProcess
	 * @Description: TODO
	 * @param uploadSize
	 * @see cn.leature.istarbaby.network.UploadInputStreamUtil.OnUploadProcessListener#onUploadProcess(int)
	 */
	@Override
	public void onUploadProcess(int uploadSize) {
		// TODO Auto-generated method stub

	}

	/**
	 * @Title: initUpload
	 * @Description: TODO
	 * @param fileSize
	 * @see cn.leature.istarbaby.network.UploadInputStreamUtil.OnUploadProcessListener#initUpload(int)
	 */
	@Override
	public void initUpload(int fileSize) {
		// TODO Auto-generated method stub

	}

	protected void startSaveChildDetail() {

		if (!checkInputs()) {
			// 输入检查错误
			return;
		}

		String Birthday = mBirthday.getText().toString().trim();
		String Settingname = mSettingname.getText().toString().trim();
		String Mobile = mMobile.getText().toString().trim();
		String Email = mEmail.getText().toString().trim();
		String Address = mAddress.getText().toString().trim();

		if (Mobile.length() > 0) {
			if (getPhone(Mobile) == false) {
				Toast.makeText(SettingProfileActivity.this, "号码有误，请重新输入",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		mProgressDialog.set_Text("保存中...", true);
		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();

		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("Gender", "" + mBabyGender);
		param.put("Name", Settingname);
		param.put("Birthday", Birthday.replace("/", ""));
		param.put("Mobile", Mobile);
		param.put("Email", Email);
		param.put("Address", Address);

		mHttpUtil.setOnPostProcessListener(this);
		mHttpUtil.sendPostMessage(ConstantDef.cUrlUserInfoEdit, param);
	}

	/**
	 * @Title: checkInputs
	 * @Description: TODO
	 * @return
	 * @return: boolean
	 */
	private boolean checkInputs() {
		String userName = mSettingname.getText().toString().trim();

		if (userName.length() == 0) {
			Toast.makeText(this, "名字必须输入", Toast.LENGTH_LONG).show();
			return false;
		}

		String email = mEmail.getText().toString().trim();
		if (email.length() > 0) {
			if (getEmail(email) == false) {
				Toast.makeText(this, "邮箱输入有误，请重新输入", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
	}

	public static boolean getEmail(String email) {

		Pattern pattern = Pattern
				.compile("\\w+\\@\\w+\\.(com|cn|com.cn|net|org|gov|gov.cn|edu|edu.cn)");
		Matcher matcher = pattern.matcher(email);

		return matcher.matches();
	}

	/**
	 * @Title: onPostDone
	 * @Description: TODO
	 * @param responseCode
	 * @param responseMessage
	 * @see cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener#onPostDone(int,
	 *      java.lang.String)
	 */
	@Override
	public void onPostDone(int responseCode, String responseMessage) {
		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE) {
			msg.what = PROFILE_EDIT_DONE;
		} else {
			msg.what = PROFILE_EDIT_ERROR;
		}

		handler.sendMessage(msg);
	}

	protected void saveDetailDone(String listResult) {
		mProgressDialog.dismiss();

		if ((listResult == null) || "0".equals(listResult)) {

			Toast.makeText(this, "保存失败", Toast.LENGTH_LONG).show();
		} else {
			isEditMode = false;

			// 菜单按钮变成 编辑
			titleBarEdit.setBackgroundResource(R.drawable.selector_hd_edt);

			mSettingname.setBackgroundColor(getResources().getColor(
					R.color.istar_z));
			mSettingname.setEnabled(false);
			mSettingname.setFocusable(false);
			mSettingname.setFocusableInTouchMode(false);
			// mSettingname.setGravity(Gravity.CENTER);
			mSettingname.setHint("");

			mBirthday.setBackgroundColor(getResources().getColor(
					R.color.istar_z));
			// mBirthday.setGravity(Gravity.CENTER);

			mMobile.setBackgroundColor(getResources().getColor(R.color.istar_z));
			mMobile.setEnabled(false);
			mMobile.setFocusable(false);
			mMobile.setFocusableInTouchMode(false);
			mMobile.setHint("");

			mEmail.setBackgroundColor(getResources().getColor(R.color.istar_z));
			mEmail.setEnabled(false);
			mEmail.setFocusable(false);
			mEmail.setFocusableInTouchMode(false);
			mEmail.setHint("");

			mAddress.setBackgroundColor(getResources()
					.getColor(R.color.istar_z));
			mAddress.setEnabled(false);
			mAddress.setFocusable(false);
			mAddress.setFocusableInTouchMode(false);
			mAddress.setHint("");

			mGenderLayout.setVisibility(View.INVISIBLE);
			mSettinggender.setVisibility(View.VISIBLE);
			// 性别
			if ("1".equals(mBabyGender)) {
				mSettinggender.setText("男性");
			} else if ("2".equals(mBabyGender)) {
				mSettinggender.setText("女性");
			} else {
				mSettinggender.setText("未知");
			}

			// 保存修改后信息
			saveLoginUserInfo(listResult);
		}
	}

	private void saveLoginUserInfo(String result) {
		isInfoEdited = true;
		// 修改成功，保存登录用户
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", mLoginUserInfo.getUserSId());
		param.put("UserString", result);
		LoginInfo.saveLoginUser(this, param);
	}
}
