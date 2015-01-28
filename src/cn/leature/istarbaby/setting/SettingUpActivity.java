package cn.leature.istarbaby.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzBaseActivity;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.UserInfo;
import cn.leature.istarbaby.login.LoginActivity;
import cn.leature.istarbaby.login.PasswordSubmitActivity;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.slidingmenu.SlidingMenu;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.wxapi.WXEntryActivity;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SettingUpActivity extends LzBaseActivity implements
		OnClickListener
{
	private SlidingMenu mSlidingMenu;
	private TextView mSettingtext, mExittext, mSettingUserId, mRegardtext,
			mRecommed;
	private ImageView mUpicon;
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private LzProgressDialog mProgressDialog;
	private LinearLayout mLayout;

	private Button mQuxiao, mQueding, msettingQueding, msettingQuxiao;;
	private ImageButton showLeftMenu;
	private AlertDialog dialog;
	// 用户登录信息
	private UserInfo mLoginUserInfo;
	private AlertDialog PassWorddialog;
	private TextView mGenerel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sliding_main);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		initView();
		// 检查用户信息
		checkUserLogin();

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.noimage)
				.showImageForEmptyUri(R.drawable.noimage)
				.showImageOnFail(R.drawable.noimage).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
	}

	private void initView()
	{
		// 设置主菜单
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMain);

		View leftView = getLayoutInflater()
				.inflate(R.layout.sliding_menu, null);
		View centerView = getLayoutInflater().inflate(
				R.layout.activity_setting_up, null);

		mSlidingMenu.setLeftView(leftView);
		mSlidingMenu.setCenterView(centerView);

		// 设置顶部导航
		TextView titleBarTitle = (TextView) this
				.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("设定");

		showLeftMenu = (ImageButton) centerView
				.findViewById(R.id.titlebar_leftbtn);
		showLeftMenu.setBackgroundResource(R.drawable.selector_hd_menu);
		showLeftMenu.setOnClickListener(this);

		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);

		mGenerel = (TextView) findViewById(R.id.general_textview);
		mSettingtext = (TextView) centerView.findViewById(R.id.setting_upname);
		mRegardtext = (TextView) centerView.findViewById(R.id.regardtext);
		mExittext = (TextView) centerView.findViewById(R.id.exittext);
		
		mRecommed = (TextView) findViewById(R.id.recommed_textview);
		mUpicon = (ImageView) findViewById(R.id.setting_upicon);
		mLayout = (LinearLayout) findViewById(R.id.settingtext);

		mLayout.setOnClickListener(this);
		mRegardtext.setOnClickListener(this);
		mExittext.setOnClickListener(this);
		mGenerel.setOnClickListener(this);

		mRecommed.setOnClickListener(this);
		mSettingUserId = (TextView) centerView
				.findViewById(R.id.setting_userid);
	}
	@Override
	public void onClick(View v)
	{
		Intent intent = new Intent();
		switch (v.getId())
		{

		case R.id.titlebar_leftbtn:

			mSlidingMenu.showLeftView();

			break;

		case R.id.settingtext:

			intent.setClass(this, SettingProfileActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_SETTING_PROFILE);

			overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);

			break;

		case R.id.regardtext:

			intent.setClass(this, SettingAboutAppActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_SETTINGABOUTAPP);

			overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);

			break;
		case R.id.exittext:

			dialog();

			break;

		case R.id.delete_queding:

			dialog.dismiss();
			if (mLoginUserInfo.getPassword().equals(
					ConstantDef.cDefaultPassword))
			{
				SettingPassWord();
			}
			else
			{
				Bundle bundle = new Bundle();
				bundle.putString("modle", "setting");
				intent.putExtras(bundle);
				intent.setClass(this, LoginActivity.class);
				startActivity(intent);

				finish();
			}
			break;
		case R.id.general_textview:
			intent.setClass(this, SettingPreferenceActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_SETTING_PREFERENCE);

			overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);

			break;

		case R.id.delete_quxiao:

			dialog.dismiss();

			break;

		case R.id.setting_delete_queding:

			PassWorddialog.dismiss();

			Bundle bundle = new Bundle();
			bundle.putString("defaultPassWord", "default");
			bundle.putString(ConstantDef.cUserId,
					LoginInfo.getLoginUserId(this));
			intent.putExtras(bundle);
			intent.setClass(this, PasswordSubmitActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_SETTING_PASSWORD);

			this.overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);

			break;

		case R.id.setting_delete_quxiao:

			PassWorddialog.dismiss();

			break;
		case R.id.recommed_textview:
			intent.setClass(this, WXEntryActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_SETTINGRECOMMEND);

			overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			if (requestCode == ConstantDef.REQUEST_CODE_SETTING_PROFILE)
			{
				// 重新显示修改后信息
				checkUserLogin();
			}
			else if (requestCode == ConstantDef.REQUEST_CODE_SETTING_PASSWORD)
			{
				// 关闭窗口
				this.finish();
			}
		}
	}

	private void checkUserLogin()
	{

		// 取得用户登录信息
		mLoginUserInfo = LoginInfo.loadLoginUserInfo(this);

		// 用户名或密码为空，需要重新登录
		if (mLoginUserInfo == null || mLoginUserInfo.getUserSId().equals("")
				|| mLoginUserInfo.getPassword().equals(""))
		{
			return;
		}

		mSettingtext.setText(mLoginUserInfo.getUserName());
		mSettingUserId.setText(mLoginUserInfo.getUserSId());

		// 图片加载
		imageLoader.displayImage(
				HttpClientUtil.SERVER_PATH + mLoginUserInfo.getUserIcon(),
				mUpicon, options);
	}

	private void SettingPassWord()
	{

		View layout = getLayoutInflater().inflate(
				R.layout.settingpassdword_dialog, null);
		msettingQueding = (Button) layout
				.findViewById(R.id.setting_delete_queding);
		msettingQueding.setText("设置密码");

		msettingQuxiao = (Button) layout
				.findViewById(R.id.setting_delete_quxiao);

		// 标题
		TextView title = (TextView) layout.findViewById(R.id.setting_title);
		title.setText("设置密码提示");

		// 提示信息
		TextView detail = (TextView) layout
				.findViewById(R.id.setting_alert_text);
		detail.setText("你还没有设置密码，为了能够再次登录，请先设定密码！");

		msettingQuxiao.setOnClickListener(this);
		msettingQueding.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		PassWorddialog = builder.create();
		PassWorddialog.show();
		PassWorddialog.setContentView(layout);
	}

	private void dialog()
	{

		View layout = getLayoutInflater().inflate(R.layout.delete_dialog, null);
		mQueding = (Button) layout.findViewById(R.id.delete_queding);
		mQueding.setText("确定退出");

		mQuxiao = (Button) layout.findViewById(R.id.delete_quxiao);

		// 提示信息
		TextView title = (TextView) layout.findViewById(R.id.alert_text);
		title.setText("确定要退出吗？");

		mQuxiao.setOnClickListener(this);
		mQueding.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(layout);
	}
}
