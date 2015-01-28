package cn.leature.istarbaby.setting;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.Version_Update;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.login.TermOfService;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.utils.ConstantDef;

public class SettingAboutAppActivity extends Activity implements
		OnClickListener {
	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;
	// 服务器的版本信息
	private String mSversion_name;
	// 软件当前版本 信息
	private PackageInfo mPackageInfo;
	// 版本检测出错（Message用）
	protected static final int CHECK_VERSION_ERROR = -1002;
	protected static final int CHECK_VERSION_NEW = 2000;
	protected static final int FOUND_VERSION_NEW = 2001;

	private LinearLayout mLayout;
	private TextView mVersionNow;
	private TextView mVersionChecked;
	private LzProgressDialog mProgressDialog;
	private TextView mProposal;
	// 是否从上级菜单打开
	private int mPageOpened = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_aboutapp);

		initUI();
		setCustomTitleBar();
		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		// 检查版本
		checkAppVersion();
	}

	private void initUI() {
		mVersionNow = (TextView) findViewById(R.id.version_now);

		PackageManager pManager = getPackageManager();
		try {
			mPackageInfo = pManager.getPackageInfo(getPackageName(), 0);
			mVersionNow.setText("版本 " + mPackageInfo.versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TextView terms = (TextView) findViewById(R.id.terms);
		terms.setOnClickListener(this);

		mProposal = (TextView) findViewById(R.id.proposal);
		mProposal.setOnClickListener(this);

		mVersionChecked = (TextView) findViewById(R.id.version_checked);

		// 版本检查
		mLayout = (LinearLayout) findViewById(R.id.setting_version);
		mLayout.setOnClickListener(this);
	}

	// 当前版本与服务器版本对比
	private void checkAppVersion() {

		Map<String, String> param = new HashMap<String, String>();
		param.put("App_Type", "android");

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				if ((result == null) || (result.equals("0"))) {
					// 版本检测出错
					handler.sendEmptyMessage(CHECK_VERSION_ERROR);
				} else {
					try {

						JSONObject jsonObject = new JSONObject(result);

						mSversion_name = jsonObject.getString("version_name");

						if (mSversion_name.equals(mPackageInfo.versionName)) {
							handler.sendEmptyMessage(CHECK_VERSION_NEW);
						} else {
							handler.sendEmptyMessage(FOUND_VERSION_NEW);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlCheckVersion, param);
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("关于 育儿宝");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.terms:

			intent.setClass(this, TermOfService.class);
			startActivityForResult(intent, 0);
			// 设定启动动画
			this.overridePendingTransition(R.anim.activity_in_from_bottom,
					R.anim.activity_nothing_in_out);
			break;
		case R.id.proposal:
			intent.setClass(this, SettingFeedbackActivity.class);
			startActivityForResult(intent, 0);
			overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
			break;

		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		case R.id.setting_version:
			// 检查版本信息
			mPageOpened = 1;
			mProgressDialog.show();
			checkAppVersion();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backPrePage();
		}
		return false;
	}

	private void backPrePage() {
		Intent intent = new Intent();
		this.setResult(Activity.RESULT_CANCELED, intent);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case CHECK_VERSION_ERROR:
				doCheckVersionError();
				break;
			case CHECK_VERSION_NEW:
				doCheckVersion();
				break;
			case FOUND_VERSION_NEW:
				foundNewVersion();
				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};


	private void doCheckVersion() {
		// 目前最新版本
		if (mPageOpened == 0) {
			// 从上级菜单打开
			mVersionChecked.setText("已是最新版本");
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"目前已是最新版本，请安心使用。", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	/**
	 * @Title: foundNewVersion
	 * @Description: TODO
	 * @return: void
	 */
	protected void foundNewVersion() {
		// 发现新版本
		if (mPageOpened == 0) {
			// 从上级菜单打开
			mVersionChecked.setText("版本 " + mSversion_name + "    ");

			Drawable nav_up = getResources().getDrawable(R.drawable.ab_forward);
			nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
					nav_up.getMinimumHeight());
			mVersionChecked.setCompoundDrawables(null, null, nav_up, null);
		} else {

			Version_Update mVersion_Update = new Version_Update(
					SettingAboutAppActivity.this);
			mVersion_Update.checkUpdateInfo();
		}
	}

	protected void doCheckVersionError() {
		if (mPageOpened != 0) {
			Toast.makeText(this, "网络出现问题，无法确认最新版本。", Toast.LENGTH_LONG).show();
		}
	}

	protected void toErrorMessage(String msgString) {
		// 取得出错，显示提示信息
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}
}
