package cn.leature.istarbaby.child;

import java.util.HashMap;
import java.util.List;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.ChildrenInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.UserInfo;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.selecttime.DateTimePicker;
import cn.leature.istarbaby.utils.ConstantDef;

public class ChildAddActivity extends Activity implements OnClickListener,
		OnPostProcessListener {

	// 用户登录信息
	private UserInfo mLoginUserInfo;

	private TextView mTitleBarTitle;
	private ImageButton mTitleBarCancel, mTitleBarRight;

	private ImageView mBoyImageView, mGirlImageView;

	private EditText mName, mBabyNumber, mBirthday;

	private static final int ALBUM_LIST_DONE = 1;
	private static final int ALBUM_LIST_ERROR = -1;

	private HttpPostUtil mHttpUtil;
	private Intent mIntent;
	private Button mManage_login;

	// 性别 （1：男孩， 2：女孩， 其他：未定）
	private int mBabyGender = 0;

	private LzProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_child_add);

		// 检查用户登录状况
		checkUserLogin();

		inItUi();

		mHttpUtil = HttpPostUtil.getInstance();
		mHttpUtil.setOnPostProcessListener(this);
		mIntent = this.getIntent();
		mProgressDialog = new LzProgressDialog(this);
	}

	/**
	 * @Title: checkUserLogin
	 * @Description: TODO
	 * @return: void
	 */
	private void checkUserLogin() {
		// 取得用户登录信息
		mLoginUserInfo = LoginInfo.loadLoginUserInfo(this);

		// 用户名或密码为空，需要重新登录
		if (mLoginUserInfo.getUserSId().equals("")
				|| mLoginUserInfo.getPassword().equals("")) {

		}
	}

	private void inItUi() {
		// 设置title
		setCustomTitleBar();

		mBirthday = (EditText) findViewById(R.id.manage_manage_birthday_edit);
		mBirthday.setOnClickListener(this);
		mName = (EditText) findViewById(R.id.manage_manage_name_edit);
		mBabyNumber = (EditText) findViewById(R.id.manage_baby_no);

		mBoyImageView = (ImageView) this.findViewById(R.id.child_add_boy);
		mBoyImageView.setOnClickListener(this);

		mGirlImageView = (ImageView) this.findViewById(R.id.child_add_girl);
		mGirlImageView.setOnClickListener(this);

		mManage_login = (Button) this.findViewById(R.id.manage_login);
		mManage_login.setOnClickListener(this);
	}

	private void setCustomTitleBar() {

		mTitleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		mTitleBarTitle.setText("宝宝登录");

		mTitleBarCancel = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		mTitleBarCancel.setBackgroundResource(R.drawable.selector_hd_ccl);
		mTitleBarCancel.setOnClickListener(this);

		mTitleBarRight = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		mTitleBarRight.setVisibility(View.INVISIBLE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backPrePage();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:

			backPrePage();
			break;
		case R.id.child_add_boy:
			genderImageClick(v.getId());
			break;
		case R.id.child_add_girl:
			genderImageClick(v.getId());
			break;
		case R.id.manage_login:
			// 登录成功跳到宝宝详情页面
			doChildAddPost();
			break;
		case R.id.manage_manage_birthday_edit:
			DateTimePicker.settime().getdate(mBirthday, ChildAddActivity.this);
			break;

		default:
			break;
		}
	}

	private void backPrePage() {
		// 取消时候，直接返回

		this.setResult(Activity.RESULT_CANCELED, mIntent);
		this.finish();

		// 设定退出动画
		overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_bottom);
	}

	/**
	 * @Title: genderImageClick
	 * @Description: TODO
	 * @param id
	 * @return: void
	 */
	private void genderImageClick(int id) {
		switch (id) {
		case R.id.child_add_boy:
			// 选择男孩
			if (mBabyGender != 1) {
				mBabyGender = 1;
				mBoyImageView.setImageResource(R.drawable.baby_sex_boy_on);
				mGirlImageView.setImageResource(R.drawable.baby_sex_girl);
			}
			break;
		case R.id.child_add_girl:
			// 选择女孩
			if (mBabyGender != 2) {
				mBabyGender = 2;
				mBoyImageView.setImageResource(R.drawable.baby_sex_boy);
				mGirlImageView.setImageResource(R.drawable.baby_sex_girl_on);
			}
			break;
		}
	}

	/**
	 * @Title: doChildAddPost
	 * @Description: TODO
	 * @return: void
	 */
	private void doChildAddPost() {

		String name = mName.getText().toString().trim();
		String childno = mBabyNumber.getText().toString().trim();
		String birthday = mBirthday.getText().toString().trim();

		// 检查项目是否有输入
		if ("".equals(birthday)) {
			Toast.makeText(this, "[生日]未输入", Toast.LENGTH_LONG).show();
			return;
		}

		if (mBabyGender == 0) {
			Toast.makeText(this, "[性别]未输入", Toast.LENGTH_LONG).show();
			return;
		}

		if ("".equals(name)) {
			Toast.makeText(this, "[名字]未输入", Toast.LENGTH_LONG).show();
			return;
		}

		if ("".equals(childno)) {
			Toast.makeText(this, "[宝宝排行]未输入", Toast.LENGTH_LONG).show();
			return;
		}
		// 宝宝排行重复检查
		List<ChildrenInfo> childrenInfo = mLoginUserInfo.getChildList();
		for (int i = 0; i < childrenInfo.size(); i++) {
			if (childno.equals(childrenInfo.get(i).getChild_no())) {
				// 排行登录重复
				Toast.makeText(this, "[宝宝排行]重复登录", Toast.LENGTH_LONG).show();
				return;
			}
		}
		mProgressDialog.setMessage("正在保存...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
		Map<String, String> param = new HashMap<String, String>();

		param.put("UserId", mLoginUserInfo.getUserSId());
		param.put("Name", name);
		param.put("ChildNo", childno);
		param.put("Birthday", birthday.replaceAll("/", ""));
		param.put("Gender", Integer.toString(mBabyGender));

		mHttpUtil.sendPostMessage(ConstantDef.cUrlChildrenAdd, param);
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage) {
		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE) {
			msg.what = ALBUM_LIST_DONE;
		} else {
			msg.what = ALBUM_LIST_ERROR;
		}

		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ALBUM_LIST_DONE:
				toListDailyDone(msg.obj.toString());
				break;

			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};

	protected void toListDailyDone(String result) {

		// 关闭进度条
		mProgressDialog.dismiss();
		if ((result == null) || (result.equals("0"))) {

			Toast.makeText(this, "宝宝添加失败。", Toast.LENGTH_LONG).show();

			return;
		}

		// 登录成功，保存登录用户
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", mLoginUserInfo.getUserSId());
		param.put("UserString", result);
		LoginInfo.saveLoginUser(this, param);

		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();

		// 设定退出动画
		overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_bottom);
	}

	/**
	 * @Title: toErrorMessage
	 * @Description: TODO
	 * @param string
	 * @return: void
	 */
	protected void toErrorMessage(String msgString) {
		// 出错，显示提示信息
		if (mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

}
