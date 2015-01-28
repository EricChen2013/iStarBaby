package cn.leature.istarbaby.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import cn.leature.istarbaby.utils.ConstantDef;

public class RegistrationAuthActivity extends Activity implements
		OnClickListener {

	private Bundle mBundle;

	private String mRegUserId;
	private String mRegAuthCode;

	private EditText mInputAuthCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registration_auth);

		// title bar设置
		initTitlebar();

		// 取得参数
		mBundle = this.getIntent().getExtras();
		if (mBundle == null) {
			mBundle = new Bundle();
			mBundle.putString(ConstantDef.cUserId, "");
			mBundle.putString(ConstantDef.clAuthCode, "");
		}

		mRegUserId = mBundle.getString(ConstantDef.cUserId);
		mRegAuthCode = mBundle.getString(ConstantDef.clAuthCode);

		// 显示输入手机号码
		TextView labelCode = (TextView) this
				.findViewById(R.id.registratin_code_label);
		labelCode.setText("我们已给你的手机号码" + mRegUserId + "，发送了一条验证短信。");

		// 为了测试方便
		mInputAuthCode = (EditText) this
				.findViewById(R.id.registratin_authcode);
		if (ConstantDef.IS_DEBUG_MODE) {
			// 为测试方便，显示验证码
			mInputAuthCode.setText(mRegAuthCode);
		}

		Button authBtn = (Button) this.findViewById(R.id.registratin_next_btn);
		authBtn.setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doBackAction();
		}
		return true;
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
		titleBarTitle.setText("填写验证码");

		ImageButton backBtn = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		backBtn.setBackgroundResource(R.drawable.selector_hd_bk);
		backBtn.setOnClickListener(this);

		ImageButton titleBarPost = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		titleBarPost.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ConstantDef.REQUEST_CODE_REGISTRATION_AUTH) {
				this.setResult(Activity.RESULT_OK, data);
				this.finish();
			}
		}
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
		case R.id.registratin_next_btn:
			doCheckAuthcode();
			break;
		default:
			break;
		}
	}

	/**
	 * @Title: doCheckAuthcode
	 * @Description: TODO
	 * @return: void
	 */
	private void doCheckAuthcode() {
		// 检查填写手机验证码
		String inputCode = mInputAuthCode.getText().toString().trim();

		if (inputCode.length() == 0) {
			// 未输入验证码
			Toast.makeText(this, "未输入有效验证码。", Toast.LENGTH_LONG).show();
			return;
		}

		if (!inputCode.equals(mRegAuthCode)) {
			// 验证码输入错误
			Toast.makeText(this, "验证码输入不对啊，亲。", Toast.LENGTH_LONG).show();
			return;
		}

		// 跳转到下一个页面
		Intent intent = new Intent();
		intent.setClass(this, RegistrationSubmitActivity.class);

		// 传递参数
		intent.putExtras(mBundle);

		startActivityForResult(intent,
				ConstantDef.REQUEST_CODE_REGISTRATION_AUTH);

		// 设定启动动画
		this.overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);
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
}
