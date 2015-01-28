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
import android.view.Window;
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

public class RegistrationSubmitActivity extends Activity implements
		OnClickListener, OnPostProcessListener {

	// 用户注册URL
	private String registrationUrl = "Registration.aspx";

	private Bundle mBundle;

	private HttpPostUtil httpUtil;
	private LzProgressDialog progressDialog;
	// 用户注册成功（Message用）
	protected static final int USER_REGISTRATION_DONE = 1;
	// 用户注册出错（Message用）
	protected static final int USER_REGISTRATION_ERROR = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registration_submit);

		// title bar设置
		initTitlebar();

		// 取得参数
		mBundle = this.getIntent().getExtras();
		if (mBundle == null) {
			mBundle = new Bundle();
			mBundle.putString(ConstantDef.cUserId, "");
			mBundle.putString(ConstantDef.clAuthCode, "");
		}

		Button authBtn = (Button) this.findViewById(R.id.registratin_submit);
		authBtn.setOnClickListener(this);
		mEdit = (EditText) this.findViewById(R.id.registratin_username);

		progressDialog = new LzProgressDialog(this);

		httpUtil = HttpPostUtil.getInstance();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doBackAction();
		}
		return super.onKeyDown(keyCode, event);
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
		titleBarTitle.setText("设置资料");

		ImageButton backBtn = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		backBtn.setBackgroundResource(R.drawable.selector_hd_bk);
		backBtn.setOnClickListener(this);

		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);
	}

	/**
	 * @Title: onClick
	 * @Description: TODO
	 * @param arg0
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			doBackAction();
			break;
		case R.id.registratin_submit:
			startRegistration();
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: doBackAction
	 * @Description: TODO
	 * @return: void
	 */
	private void doBackAction() {

		this.setResult(Activity.RESULT_CANCELED);
		this.finish();
		// 结束当前页面
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	/**
	 * @Title: doRegistration
	 * @Description: TODO
	 * @return: void
	 */
	private void startRegistration() {
		String strName = mEdit.getText().toString().trim();
		if ("".equals(strName)) {
			Toast.makeText(this, "请输入昵称。", Toast.LENGTH_LONG).show();
			return;
		}

		progressDialog.setMessage("请稍候...");
		progressDialog.setCancelable(true);
		progressDialog.show();

		// 发送登录请求
		Map<String, String> param = new HashMap<String, String>();
		// 手机号作为用户ID
		param.put("UserId", mBundle.getString(ConstantDef.cUserId));
		// 将验证码作为初始密码
		param.put("Password", ConstantDef.cDefaultPassword);

		// 用户名
		param.put("UserName", strName);

		httpUtil.setOnPostProcessListener(this);
		httpUtil.sendPostMessage(registrationUrl, param);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case USER_REGISTRATION_DONE:

				toRegistrationDone(msg.obj.toString());

				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}
	};

	private EditText mEdit;

	/**
	 * @Title: toErrorMessage
	 * @Description: TODO
	 * @param string
	 * @return: void
	 */
	protected void toErrorMessage(String msgString) {
		progressDialog.dismiss();

		// 取得出错，显示提示信息
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
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
			msg.what = USER_REGISTRATION_DONE;
		} else {
			msg.what = USER_REGISTRATION_ERROR;
		}
		handler.sendMessage(msg);
	}

	/**
	 * @Title: toRegistrationDone
	 * @Description: TODO
	 * @param string
	 * @return: void
	 */
	protected void toRegistrationDone(String result) {
		// 关闭进度条
		progressDialog.dismiss();

		if ((result == null) || ("".equals(result))) {
			// 注册失败，提示信息
			Toast.makeText(this, "注册失败，请稍候再试。", Toast.LENGTH_LONG).show();
		} else {

			// 启动注册成功页面
			Intent data = new Intent();
			mBundle.putString(ConstantDef.cUserPassword,
					ConstantDef.cDefaultPassword);
			data.putExtras(mBundle);
			data.setClass(this, RegistrationActivity.class);
			startActivityForResult(data,
					ConstantDef.REQUEST_CODE_REGISTRATION_SUBMIT);

			// 设定启动动画
			this.overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ConstantDef.REQUEST_CODE_REGISTRATION_SUBMIT) {
				this.setResult(Activity.RESULT_OK, data);
				this.finish();
			}
		}
	}
}
