package cn.leature.istarbaby.login;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.Version_Update;
import cn.leature.istarbaby.Version_Update_Notification;
import cn.leature.istarbaby.common.LzBaseActivity;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.UserInfo;
import cn.leature.istarbaby.monitor.MonitorFragmentActivity;
import cn.leature.istarbaby.network.AsyncImageLoader;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DensityUtil;

public class LoginActivity extends LzBaseActivity implements OnClickListener,
		OnPostProcessListener, OnFocusChangeListener, OnItemClickListener {

	// 用户登录信息
	private UserInfo mLoginUserInfo;
	// 登录用户名及密码
	private String UserSId, password;

	private EditText mUserName, mPassword;
	private ImageButton mButtonLogin;
	// 服务器的版本信息
	private String mSversion_name;
	private String mSversion_flag;
	// 软件当前版本 信息
	private String mCversion_name;
	private HttpPostUtil httpUtil;
	private LzProgressDialog mProgressDialog;
	// 用户登录成功（Message用）
	protected static final int USER_LOGIN_DONE = 1;
	// 用户登录出错（Message用）
	protected static final int USER_LOGIN_ERROR = -1;
	// 版本检测出错（Message用）
	protected static final int CHECK_VERSION_ERROR = -1002;
	private int screenWidth, screenHeight;
	private AsyncImageLoader asyncImageLoader;
	private ImageView mUserIcon;

	private RadioButton mListview_name;
	private TextView mDeleteName;
	private TextView mDelete_password;
	boolean isShowIcon = false;

	private ListView mListview;
	private List<UserInfo> mData;
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private ListApdate mApdate;
	private RelativeLayout mLogin_layout;
	private RelativeLayout mNewUser_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		mData = LoginInfo.loadLoginUserInfoList(this);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		httpUtil = HttpPostUtil.getInstance();
		httpUtil.setOnPostProcessListener(this);

		// 取得窗口属性
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 窗口的宽度
		screenWidth = dm.widthPixels;
		// 窗口高度
		screenHeight = dm.heightPixels;

		asyncImageLoader = new AsyncImageLoader();
		asyncImageLoader.setResizeImageWidth(screenWidth);
		asyncImageLoader.setResizeImageHeight(screenHeight);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.noimage_child)
				.showImageForEmptyUri(R.drawable.noimage_child)
				.showImageOnFail(R.drawable.noimage_child).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

		initTitlebar();
		initView();

		// 版本检测
		checkAppVersion();

		// 用户信息检测
		checkUserLogin();
	}

	private void initTitlebar() {
		// 设置顶部导航
		// TextView titleBarTitle = (TextView) this
		// .findViewById(R.id.titlebar_title);
		ImageView titleimage = (ImageView) this
				.findViewById(R.id.titlebar_front_image);
		titleimage.setVisibility(View.VISIBLE);
		ImageButton backBtn = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		backBtn.setVisibility(View.INVISIBLE);

		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case USER_LOGIN_DONE:
				toLoginDone(msg.obj.toString());
				break;
			case CHECK_VERSION_ERROR:
				doCheckVersionError();
				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}
	};

	protected void toLoginDone(String result) {
		// 关闭进度条
		mProgressDialog.dismiss();

		if (result == null) {
			Toast.makeText(this, "网络出现问题，请稍候再试。", Toast.LENGTH_LONG).show();

		} else if (result.equals("9")) {
			Toast.makeText(this, "用户名或密码输入错误。", Toast.LENGTH_LONG).show();

		} else {
			// 登录成功，保存登录用户
			Map<String, String> param = new HashMap<String, String>();
			param.put("UserId", UserSId);
			param.put("Password", password);
			param.put("UserString", result);
		
			LoginInfo.saveLoginUser(this, param);
			// sp.edit().putBoolean("type", true).commit();
			// 启动List页面
			Intent intent = new Intent();
			//intent.setClass(LoginActivity.this, DailyListActivity.class);
			intent.setClass(LoginActivity.this, MonitorFragmentActivity.class);
			startActivity(intent);
			this.finish();

			// 设定启动动画
			this.overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
		}
	}

	/**
	 * @Title: doCheckVersionError
	 * @Description: TODO
	 * @return: void
	 */
	protected void doCheckVersionError() {
		Toast.makeText(this, "网络出现问题，请稍候再试。", Toast.LENGTH_LONG).show();
		finish();
	}

	protected void toErrorMessage(String msgString) {
		// 取得出错，显示提示信息
		mProgressDialog.dismiss();
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	private boolean getPhone(String mobile) {
		Pattern p = Pattern
				.compile("^((14[5,7])|(13[0-9])|(15[^4,\\D])|(18[0-3,5-9]))\\d{8}$");
		Matcher matcher = p.matcher(mobile);
		return matcher.matches();
	}

	private void initView() {
		mProgressDialog = new LzProgressDialog(this);

		mUserName = (EditText) this.findViewById(R.id.editUserName);
		mPassword = (EditText) this.findViewById(R.id.editPassword);
		mButtonLogin = (ImageButton) this.findViewById(R.id.splash_login);
		mUserIcon = (ImageView) this.findViewById(R.id.login_usericon);

		mListview = (ListView) findViewById(R.id.listview);
		mDeleteName = (TextView) findViewById(R.id.upname_delete);
		mListview_name = (RadioButton) findViewById(R.id.listview_name);
		mDelete_password = (TextView) findViewById(R.id.password_delete);
		mLogin_layout = (RelativeLayout) findViewById(R.id.login_layout);
		mNewUser_layout = (RelativeLayout) findViewById(R.id.layout_newuser);

		mLogin_layout.setOnClickListener(this);
		mListview.setOnItemClickListener(this);
		mApdate = new ListApdate();
		if (null == mData) {
			mData = new ArrayList<UserInfo>();
		}
		mDelete_password.setOnClickListener(this);
		mButtonLogin.setOnClickListener(this);

		mDeleteName.setOnClickListener(this);
		mListview_name.setOnClickListener(this);
		mPassword.setOnFocusChangeListener(this);
		mUserName.setOnFocusChangeListener(this);

		// 注册新用户按钮事件
		ImageButton btnRegistor = (ImageButton) this
				.findViewById(R.id.splash_registor);
		btnRegistor.setOnClickListener(this);

		// 找回密码按钮事件
		ImageButton splah_password = (ImageButton) this
				.findViewById(R.id.splash_password);
		splah_password.setOnClickListener(this);

		mUserName.addTextChangedListener(new TextWatcher() {

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
					mUserIcon.setImageResource(R.drawable.noimage);
					return;
				}
				if (s.length() == 11) {
					if (getPhone(s.toString()) == false) {
						Toast.makeText(LoginActivity.this, "号码有误，请重新输入",
								Toast.LENGTH_LONG).show();
						return;
					}
				}

				// 查询数据库
				UserInfo userInfo = LoginInfo.loadLoginUserInfo(
						LoginActivity.this, userId);
				if (userInfo != null && userInfo.getUserIcon().length() > 0) {

					imageLoader.displayImage(HttpClientUtil.SERVER_PATH
							+ userInfo.getUserIcon(), mUserIcon, options);
					mUserIcon.startAnimation(AnimationUtils.loadAnimation(
							LoginActivity.this,
							R.anim.child_detail_flower_visible));
				} else {
					mUserIcon.setImageResource(R.drawable.noimage);
					mUserIcon.startAnimation(AnimationUtils.loadAnimation(
							LoginActivity.this,
							R.anim.child_detail_flower_visible));
				}
			}
		});
	}

	private void checkUserLogin() {

		// 取得用户登录信息
		mLoginUserInfo = LoginInfo.loadLoginUserInfo(this);

		// 用户名或密码为空，需要重新登录
		if (mLoginUserInfo == null || mLoginUserInfo.getUserSId().equals("")) {
			return;
		}

		// 初始化Id
		mUserName.setText(mLoginUserInfo.getUserSId());
		mPassword.setText(mLoginUserInfo.getPassword());

		// 图片加载
		if (mLoginUserInfo.getUserIcon().length() > 0) {

			imageLoader.displayImage(HttpClientUtil.SERVER_PATH
					+ mLoginUserInfo.getUserIcon(), mUserIcon, options);

		}
		Bundle bundle = getIntent().getExtras();

		if (bundle.getString("modle").equals("start")) {
			// 自动登录
			toDoLogin();
		}

	}

	class ListApdate extends BaseAdapter {

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View inflate = getLayoutInflater().inflate(
					R.layout.login_listview_item, null);

			// ImageView item_Delete = (ImageView) inflate
			// .findViewById(R.id.item_delete);
			final ImageView item_Usericon = (ImageView) inflate
					.findViewById(R.id.item_usericon);
			TextView item_Userid = (TextView) inflate
					.findViewById(R.id.item_userid);
			TextView item_Username = (TextView) inflate
					.findViewById(R.id.item_username);

			UserInfo userInfo = mData.get(position);

			item_Userid.setText(userInfo.getUserSId());
			item_Username.setText(userInfo.getUserName());
			imageLoader.displayImage(
					HttpClientUtil.SERVER_PATH + userInfo.getUserIcon(),
					item_Usericon, options);

			if (convertView != null) {

				int height = 57;
				if (mData.size() <= 1) {
					setListViewHeightBasedOnChildren(mListview, height);
				} else if (mData.size() == 2) {
					setListViewHeightBasedOnChildren(mListview, height * 2);
				} else if (mData.size() >= 3) {
					setListViewHeightBasedOnChildren(mListview, height * 3);
				}

			}
			return inflate;
		}

	}

	public void setListViewHeightBasedOnChildren(ListView listView, int height) {
		int itemHeight = DensityUtil.dip2px(this, height);
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = itemHeight;
		listView.setLayoutParams(params);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.splash_login:
			toDoLogin();
			break;
		case R.id.splash_registor:
			toDoRegistration();
			break;
		case R.id.login_layout:
			isShowIcon = false;
			mListview.setVisibility(View.GONE);
			mListview_name
					.setBackgroundResource(R.drawable.login_textfield_more);

			break;
		case R.id.splash_password:
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, PasswordFindActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_FROUNDPASSWORD);

			// 设定启动动画
			this.overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
			break;

		case R.id.upname_delete:

			mUserName.setText("");

			break;
		case R.id.password_delete:

			mPassword.setText("");

			break;
		case R.id.listview_name:
			checkSoftInputStatus();

			mListview.setAdapter(mApdate);
			if (!isShowIcon) {
				isShowIcon = true;
				mListview_name
						.setBackgroundResource(R.drawable.login_textfield_more_flip);
				mListview.setVisibility(View.VISIBLE);

			} else {
				isShowIcon = false;
				mListview_name
						.setBackgroundResource(R.drawable.login_textfield_more);
				mListview.setVisibility(View.GONE);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * @Title: toDoRegistration
	 * @Description: TODO
	 * @return: void
	 */

	private void toDoRegistration() {
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, RegistrationUserActivity.class);
		startActivityForResult(intent, ConstantDef.REQUEST_CODE_LOGIN);

		// 设定启动动画
		this.overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);
	}

	private void toDoLogin() {

		UserSId = mUserName.getText().toString().trim();
		password = mPassword.getText().toString().trim();

		if (UserSId.equals("") || password.equals("")) {
			Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_LONG).show();
			return;
		}

		if (UserSId.length() != 11) {
			Toast.makeText(this, "用户名输入错误", Toast.LENGTH_LONG).show();
			return;
		}

		httpUtil.setOnPostProcessListener(this);
		mProgressDialog.show();

		// 发送登录请求
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", UserSId);
		param.put("Password", password);
		httpUtil.sendPostMessage(ConstantDef.cUrlLogin, param);
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage) {

		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE) {
			msg.what = USER_LOGIN_DONE;
		} else {
			msg.what = USER_LOGIN_ERROR;
		}
		handler.sendMessage(msg);
	}

	// 当前版本与服务器版本对比
	private void checkAppVersion() {

		if (!LoginInfo.isVersionUpgradeChecked(this)) {
			// 每天只需一次检查版本更新
			return;
		}

		PackageManager pManager = getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = pManager.getPackageInfo(getPackageName(), 0);
			mCversion_name = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, String> param = new HashMap<String, String>();
		param.put("App_Type", "android");

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {
			public void requestWithGetDone(String result) {

				if ((result == null) || (result.equals("0"))) {
					// 版本检测出错
					handler.sendEmptyMessage(CHECK_VERSION_ERROR);
				} else {
					try {

						JSONObject jsonObject = new JSONObject(result);

						mSversion_name = jsonObject.getString("version_name");
						mSversion_flag = jsonObject.getString("upgradeflag");

						if (!mSversion_name.equals(mCversion_name)) {
							if (mSversion_flag.equals("1")) // 强制更新 ，出现对话框
							{
								Version_Update mVersion_Update = new Version_Update(
										LoginActivity.this);
								mVersion_Update.checkUpdateInfo();

								return;
							} else {
								// 非强制更新，只是给予通知
								NotificationManager msg = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
								Notification notification = new Notification(
										R.drawable.ic_menu_notifications,
										"更新提示", System.currentTimeMillis());
								Intent intent = new Intent(LoginActivity.this,
										Version_Update_Notification.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										| Intent.FLAG_ACTIVITY_NEW_TASK);
								PendingIntent ic = PendingIntent.getActivity(
										LoginActivity.this, 0, intent, 0);
								notification.setLatestEventInfo(
										LoginActivity.this, "[育儿宝] 新版本发布",
										"点击进入更新", ic);
								notification.flags |= Notification.FLAG_AUTO_CANCEL;// 点击消失

								msg.notify(123, notification);
							}
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

					// 保存检查时间
					LoginInfo.saveVersionInfo(LoginActivity.this);

				}
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlCheckVersion, param);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ConstantDef.REQUEST_CODE_LOGIN
					|| requestCode == ConstantDef.REQUEST_CODE_FROUNDPASSWORD) {

				this.finish();
			}
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.editUserName:

			if (hasFocus) {
				mDeleteName.setVisibility(View.VISIBLE);
				mListview.setVisibility(View.GONE);
				isShowIcon = false;
				mListview_name
						.setBackgroundResource(R.drawable.login_textfield_more);
			} else {
				mDeleteName.setVisibility(View.GONE);
			}
			break;

		case R.id.editPassword:

			if (hasFocus)

			{
				mDelete_password.setVisibility(View.VISIBLE);
			} else {
				mDelete_password.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		UserInfo userInfo = mData.get(arg2);

		mUserName.setText(userInfo.getUserSId());
		mListview.setVisibility(View.GONE);
		isShowIcon = false;
		mListview_name.setBackgroundResource(R.drawable.login_textfield_more);
		mPassword.setText(userInfo.getPassword());
	}

	private void checkSoftInputStatus() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
