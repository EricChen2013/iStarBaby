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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.utils.ConstantDef;

public class PasswordSubmitActivity extends Activity implements
		OnClickListener, OnPostProcessListener {

	private EditText mSumitCode;
	private Button mNext_btn;
	private HttpPostUtil httpUtil;
	// 文章发表成功（Message用）
	protected static final int CHILD_EDIT_DONE = 4;
	protected static final int REDIRECT_LOGIN = 9;
	// 文章发表出错（Message用）
	protected static final int CHILD_EDIT_ERROR = -2;
	private Bundle mBundle;
	private Intent mIntent;
	private LzProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_submit);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		httpUtil = HttpPostUtil.getInstance();

		mIntent = getIntent();
		mBundle = mIntent.getExtras();

		initTitlebar();
		initUI();
	}

	private void initUI() {
		mSumitCode = (EditText) findViewById(R.id.pass_word_submitcode);
		mNext_btn = (Button) findViewById(R.id.pass_word_submit_next_btn);
		mNext_btn.setOnClickListener(this);
	}

	private void initTitlebar() {
		// 设置顶部导航
		TextView titleBarTitle = (TextView) this
				.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("设置密码");

		ImageButton backBtn = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		backBtn.setBackgroundResource(R.drawable.selector_hd_bk);
		backBtn.setOnClickListener(this);

		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doBackAction();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pass_word_submit_next_btn:
			getDefaultData();
			break;
		case R.id.titlebar_leftbtn:
			doBackAction();
			break;
		default:
			break;
		}
	}

	private void doBackAction() {
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();

		// 结束当前页面
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	private void getDefaultData() {

		String password = mSumitCode.getText().toString().trim();

		if (password.length() == 0) {
			Toast.makeText(this, "密码不能为空。", Toast.LENGTH_LONG).show();
			return;
		}

		if (password.length() < 4 || password.length() > 16) {
			Toast.makeText(this, "密码必须为4-16个字符(数字、字母、下划线)。", Toast.LENGTH_LONG)
					.show();
			return;
		}

		if (password.equals(ConstantDef.cDefaultPassword)) {
			Toast.makeText(this, "密码过于简单，请重新设定。", Toast.LENGTH_LONG).show();
			return;
		}

		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		// 登录用户的ID
		param.put("UserId", mBundle.getString(ConstantDef.cUserId));
		param.put("Password", password);

		httpUtil.setOnPostProcessListener(this);
		httpUtil.sendPostMessage(ConstantDef.cUrlPasswordEdit, param);
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage) {

		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;
		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE) {
			msg.what = CHILD_EDIT_DONE;
		} else {
			msg.what = CHILD_EDIT_ERROR;
		}

		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case CHILD_EDIT_DONE:// 保存密码
				saveDetailDone(msg.obj.toString());
				break;
			case REDIRECT_LOGIN: // 跳转登录页面
				redirectLogin(msg.obj.toString());
				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}
	};

	private void toErrorMessage(String msgString) {
		mProgressDialog.dismiss();

		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	/**
	 * @Title: redirectLogin
	 * @Description: TODO
	 * @param string
	 * @return: void
	 */
	protected void redirectLogin(String result) {
		// 登录成功，保存登录用户
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", mBundle.getString(ConstantDef.cUserId));
		param.put("Password", "");
		param.put("UserString", result);
		LoginInfo.saveLoginUser(this, param);

		// 保存成功
		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();

		Intent intent = new Intent();
		Bundle bundle=new Bundle();
		bundle.putString("modle", "passwordsubbmit");
		intent.putExtras(bundle);
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
	}

	private void saveDetailDone(String result) {
		mProgressDialog.showSubmitSuccess("密码设置成功！");

		final String resuleMessage = result;

		(new Handler()).postDelayed(new Runnable() {

			@Override
			public void run() {
				//
				mProgressDialog.dismiss();

				Message msg = Message.obtain();
				msg.obj = resuleMessage;
				msg.what = REDIRECT_LOGIN;

				handler.sendMessage(msg);
			}
		}, 2000);
	}
}
