package cn.leature.istarbaby.login;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.utils.ConstantDef;

public class PasswordFindActivity extends Activity implements OnClickListener,
		OnPostProcessListener {

	private EditText mUserId;
	private HttpPostUtil httpUtil;
	private LzProgressDialog mProgressDialog;
	// 用户注册成功（Message用）
	protected static final int USER_REGISTRATION_CHECK = 1;
	// 用户注册出错（Message用）
	protected static final int USER_REGISTRATION_ERROR = -1;
	// 时间（Message用）
	protected static final int TIME_TRIGGER_PROCCESS = 100;
	// 时间结束（Message用）

	private EditText mLabCode;
	private Button mNext_Btn;
	private String strAuthcode;
	private Button mMessage;
	private TextView mAuthCodeHint;

	// 60秒倒计时器
	private int mCount;
	Timer mTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_find);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		httpUtil = HttpPostUtil.getInstance();

		// title bar设置
		initTitlebar();
		initUi();
		// 监听输入手机号码是否有误
		Listener();
	}

	private void initUi() {
		mNext_Btn = (Button) findViewById(R.id.found_next_btn);
		mNext_Btn.setOnClickListener(this);

		mUserId = (EditText) findViewById(R.id.found_userid);
		mLabCode = (EditText) findViewById(R.id.foundcode);

		mMessage = (Button) findViewById(R.id.found_message);
		mMessage.setOnClickListener(this);

		mAuthCodeHint = (TextView) this.findViewById(R.id.authcode_hint);
	}

	private void initTitlebar() {
		// 设置顶部导航
		TextView titleBarTitle = (TextView) this
				.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("验证手机号码");

		ImageButton backBtn = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		backBtn.setBackgroundResource(R.drawable.selector_hd_bk);
		backBtn.setOnClickListener(this);

		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.fround_password, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			doBackAction();
			break;
		case R.id.found_message:
			doCheckAuthcode();
			break;

		case R.id.found_next_btn:
			doFindPasswordNext();
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: doFindPasswordNext
	 * @Description: TODO
	 * @return: void
	 */
	private void doFindPasswordNext() {
		String inputCode = mLabCode.getText().toString().trim();
		if (inputCode.length() == 0) {
			// 未输入验证码
			Toast.makeText(this, "未输入有效验证码。", Toast.LENGTH_LONG).show();
			return;
		}

		if (!inputCode.equals(strAuthcode)) {
			// 验证码输入错误
			Toast.makeText(this, "验证码输入不对啊，亲。", Toast.LENGTH_LONG).show();
			return;
		}

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString(ConstantDef.cUserId, mUserId.getText().toString());
		intent.putExtras(bundle);
		intent.setClass(this, PasswordSubmitActivity.class);
		startActivityForResult(intent, ConstantDef.REQUEST_CODE_PASSWORDSUBMIT);

		// 设定启动动画
		this.overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);
	}

	private void doBackAction() {
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();
		// 结束当前页面
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	private void doCheckAuthcode() {
		// 检查填写手机验证码
		String strPhone = mUserId.getText().toString().trim();

		if (!getPhone(strPhone)) {
			// 电话号码输入有误，返回重新输入
			Toast.makeText(this, "手机号码输入有误啊，亲。", Toast.LENGTH_LONG).show();
			return;
		}

		mProgressDialog.show();

		// 发送登录请求
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", strPhone);
		param.put("Mode", "1"); // 忘记密码
		httpUtil.setOnPostProcessListener(this);
		httpUtil.sendPostMessage(ConstantDef.cUrlCheckUserId, param);
	}

	private void Listener() {
		mUserId.addTextChangedListener(new TextWatcher() {

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

				if (s.length() == 11) {

					if (getPhone(s.toString()) == false) {

						Toast.makeText(PasswordFindActivity.this,
								"手机号码输入有误啊，亲。", Toast.LENGTH_LONG).show();
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

	@Override
	public void onPostDone(int responseCode, String responseMessage) {
		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE) {
			msg.what = USER_REGISTRATION_CHECK;
		} else {
			msg.what = USER_REGISTRATION_ERROR;
		}
		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case USER_REGISTRATION_CHECK:
				sendAuthcodeDone(msg.obj.toString());
				break;
			// 时间（Message用）
			case TIME_TRIGGER_PROCCESS:
				triggerTimeStart();
				break;

			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};

	private void triggerTimeStart() {
		if (mCount >= 10) {
			mMessage.setText("重新发送(" + mCount + ")");
		} else if (mCount >= 0) {
			mMessage.setText("重新发送(" + "0" + mCount + ")");
		} else {
			mMessage.setText("重新发送");
			mMessage.setEnabled(true);
			mMessage.setTextColor(getResources().getColor(R.color.istar_link));
			mTimer.cancel();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ConstantDef.REQUEST_CODE_PASSWORDSUBMIT) {

				setResult(Activity.RESULT_OK, data);
				this.finish();
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doBackAction();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void toErrorMessage(String msgString) {
		mProgressDialog.dismiss();
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	private void sendAuthcodeDone(String string) {
		mProgressDialog.dismiss();

		if ("".equals(string) || "0".equals(string)) {
			Toast.makeText(this, "处理出错，请稍候再试。", Toast.LENGTH_LONG).show();
			return;
		}

		String[] Reslut = string.split(",");
		if ("2".equals(Reslut[0])) {
			Toast.makeText(this, "您输入的账号未注册，请重新输入", Toast.LENGTH_LONG).show();
			return;
		}

		if ("0".equals(Reslut[0])) {
			Toast.makeText(this, "" + Reslut[1], Toast.LENGTH_LONG).show();
			return;
		}
		strAuthcode = Reslut[1];

		mMessage.setEnabled(false);
		mMessage.setTextColor(getResources().getColor(R.color.istar_x));

		mAuthCodeHint.setText("我们已给你的手机号码"
				+ mUserId.getText().toString().trim() + "，发送了一条验证短信。");

		if (ConstantDef.IS_DEBUG_MODE) {
			// 为测试方便，显示验证码
			mLabCode.setText(Reslut[1]);
		}

		// 允许再发送 倒计时
		trigger();
	}

	private void trigger() {
		mTimer = new Timer();
		mCount = 180;
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				msg.what = TIME_TRIGGER_PROCCESS;
				mCount--;
				handler.sendMessage(msg);
			}

		};
		mTimer.schedule(task, 0, 1000);
	}
}
