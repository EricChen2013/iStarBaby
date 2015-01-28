package cn.leature.istarbaby.login;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.utils.ConstantDef;

public class RegistrationUserActivity extends Activity implements
		OnClickListener, OnPostProcessListener, OnCheckedChangeListener {

	private EditText mUserIdEditText;

	private Button authBtn;
	private TextView mTextView;
	private CheckBox mCheckBox;
	private HttpPostUtil httpUtil;

	// 用户注册成功（Message用）
	protected static final int USER_REGISTRATION_CHECK = 1;
	// 用户注册出错（Message用）
	protected static final int USER_REGISTRATION_ERROR = -1;

	private LzProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_registration_user);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		httpUtil = HttpPostUtil.getInstance();

		// title bar设置
		initTitlebar();

		mUserIdEditText = (EditText) this.findViewById(R.id.registratin_userid);

		authBtn = (Button) this.findViewById(R.id.registratin_auth_btn);
		authBtn.setOnClickListener(this);

		mCheckBox = (CheckBox) this.findViewById(R.id.registratin_auth_cbox);
		mCheckBox.setOnCheckedChangeListener(this);

		mTextView = (TextView) this.findViewById(R.id.registratin_auth_tx);
		// 设置服务条款的下划线
		mTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		mTextView.setOnClickListener(this);

		// 初始化时，注册按钮默认不可点击
		mCheckBox.setChecked(true);

		Listener();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			doBackAction();
		}
		return true;
	}

	private void Listener() {
		mUserIdEditText.addTextChangedListener(new TextWatcher() {

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
						Toast.makeText(RegistrationUserActivity.this,
								"你输入有误，请重新输入", Toast.LENGTH_LONG).show();
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

	/**
	 * @Title: initTitlebar
	 * @Description: TODO
	 * @return: void
	 */
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
		case R.id.registratin_auth_btn:
			doCheckUserid();
			break;
		case R.id.registratin_auth_tx:
			doServiceUser();
			break;

		default:

			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK)

		{
			if (requestCode == ConstantDef.REQUEST_CODE_REGISTRATION_USER) {

				this.setResult(Activity.RESULT_OK, data);
				this.finish();
			}
		}
	}

	private void doServiceUser() {
		// 跳转到下一个页面
		Intent intent = new Intent();
		intent.setClass(this, TermOfService.class);
		startActivityForResult(intent, ConstantDef.REQUEST_CODE_TERM_SERVICE);

		// 设定启动动画
		this.overridePendingTransition(R.anim.activity_in_from_bottom,
				R.anim.activity_nothing_in_out);
	}

	/**
	 * @Title: doCheckUserid
	 * @Description: TODO
	 * @return: void
	 */
	private void doCheckUserid() {

		// 输入手机号码是否正确
		String strPhone = mUserIdEditText.getText().toString().trim();

		if (!getPhone(strPhone)) {
			// 电话号码输入有误，返回重新输入
			Toast.makeText(this, "手机号码输入有误啊，亲。", Toast.LENGTH_LONG).show();
			return;
		}

		mProgressDialog.show();

		// 该手机号码是否已经登录过
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", strPhone);
		param.put("Mode", "0"); // 新用户注册
		httpUtil.setOnPostProcessListener(this);
		httpUtil.sendPostMessage(ConstantDef.cUrlCheckUserId, param);
	}

	/**
	 * @Title: doSendAuthcode
	 * @Description: TODO
	 * @return: void
	 */
	private void doSendAuthcode(String string) {
		mProgressDialog.dismiss();

		if ("".equals(string) || "0".equals(string)) {
			Toast.makeText(this, "处理出错，请稍候再试。", Toast.LENGTH_LONG).show();
			return;
		}

		String[] result = string.split(",");
		if ("1".equals(result[0])) {
			// 该用户已经登录过
			Toast.makeText(this, "该号码已经注册过，请输入直接登录。", Toast.LENGTH_LONG).show();
			return;
		}

		if ("0".equals(result[0])) {
			// 该用户已经登录过
			Toast.makeText(this, "" + result[1], Toast.LENGTH_LONG).show();
			return;
		}

		String strAuthcode = result[1];

		// 跳转到下一个页面
		Intent intent = new Intent();
		intent.setClass(this, RegistrationAuthActivity.class);

		// 传递参数
		Bundle bundle = new Bundle();
		bundle.putString(ConstantDef.cUserId, mUserIdEditText.getText()
				.toString());
		bundle.putString(ConstantDef.clAuthCode, strAuthcode);
		intent.putExtras(bundle);

		startActivityForResult(intent,
				ConstantDef.REQUEST_CODE_REGISTRATION_USER);

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

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case USER_REGISTRATION_CHECK:
				doSendAuthcode(msg.obj.toString());
				break;
			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}
	};

	/**
	 * @Title: toErrorMessage
	 * @Description: TODO
	 * @param string
	 * @return: void
	 */
	protected void toErrorMessage(String msgString) {
		mProgressDialog.dismiss();

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
			msg.what = USER_REGISTRATION_CHECK;
		} else {
			msg.what = USER_REGISTRATION_ERROR;
		}
		handler.sendMessage(msg);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub

		if (!isChecked) // 根据用户是否打钩 ，同意了条款就可以注册，否则无法点击按钮
		{
			authBtn.setEnabled(false);
			authBtn.setTextColor(getResources().getColor(R.color.istar_x));
		} else {
			authBtn.setEnabled(true);
			authBtn.setTextColor(getResources().getColor(R.color.istar_link));
		}
	}
}
