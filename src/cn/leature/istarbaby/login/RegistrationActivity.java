package cn.leature.istarbaby.login;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.monitor.MonitorFragmentActivity;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.utils.ConstantDef;

public class RegistrationActivity extends Activity implements OnClickListener,
		OnPostProcessListener {

	private TextView mAccout;
	private Bundle mBundle;
	private HttpPostUtil httpUtil;
	// 用户登录成功（Message用）
	protected static final int USER_LOGIN_DONE = 1;
	// 用户登录出错（Message用）
	protected static final int USER_LOGIN_ERROR = -1;
	private LzProgressDialog mProgressDialog;
	private boolean ispress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);

		// title bar设置
		initTitlebar();
		initUI();

		mProgressDialog = new LzProgressDialog(this);

		httpUtil = HttpPostUtil.getInstance();
		httpUtil.setOnPostProcessListener(this);
	}

	private void initUI() {
		mBundle = getIntent().getExtras();
		mAccout = (TextView) findViewById(R.id.user_account);
		mAccout.setText(mBundle.getString(ConstantDef.cUserId));
		TextView registratin_btn = (TextView) findViewById(R.id.registratin_btn);
		registratin_btn.setOnClickListener(this);

	}

	/**
	 * @Title: initTitlebar
	 * @Description: TODO
	 * @return: void
	 */
	private void initTitlebar() {
		// 设置顶部导航
		TextView titleBarTitle = (TextView) this
				.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("注册成功");

		ImageButton backBtn = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		backBtn.setVisibility(View.INVISIBLE);
		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.registratin_btn:

			// 发送登录请求
			mProgressDialog.setMessage("登录中...");
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();

			Map<String, String> param = new HashMap<String, String>();
			param.put("UserId", mBundle.getString(ConstantDef.cUserId));
			param.put("Password", mBundle.getString(ConstantDef.cUserPassword));
			httpUtil.sendPostMessage(ConstantDef.cUrlLogin, param);
			break;

		default:
			break;
		}
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doBackAction(keyCode);
		}

		return true;
	}

	private void doBackAction(int keyCode) {
		this.setResult(Activity.RESULT_OK);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (ispress) {

				finish();
			} else {
				Toast.makeText(RegistrationActivity.this, "请再按一次退出",
						Toast.LENGTH_LONG).show();
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						ispress = false;
					}
				}, 5000);

				ispress = true;
			}
		}

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case USER_LOGIN_DONE:
				toLoginDone(msg.obj.toString());
				break;

			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};

	private void toLoginDone(String result) {
		mProgressDialog.dismiss();

		// 注册成功，保存登录用户
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", mBundle.getString(ConstantDef.cUserId));
		param.put("Password", mBundle.getString(ConstantDef.cUserPassword));
		param.put("UserString", result);
		LoginInfo.saveLoginUser(this, param);

		// 启动List页面
		Intent intent = new Intent();
		this.setResult(Activity.RESULT_OK, intent);
		this.finish();

		intent.putExtras(mBundle);
		intent.setClass(this, MonitorFragmentActivity.class);
		startActivity(intent);

		// 设定启动动画
		this.overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);

	}

	protected void toErrorMessage(String msgString) {
		// 取得出错，显示提示信息
		mProgressDialog.dismiss();

		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

}
