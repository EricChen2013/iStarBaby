package cn.leature.istarbaby.friend;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.utils.ConstantDef;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FriendSearchActivity extends Activity implements OnClickListener {
	private ImageButton titleBarBack, titleBarEdit;
	private TextView titleBarTitle;

	private EditText mSeekName;
	private TextView mSeek_Delete;
	private LzProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_search);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		setCustomTitleBar();
		ititUI();
	}

	private void ititUI() {

		Button btn_seek = (Button) findViewById(R.id.btn_search);
		btn_seek.setOnClickListener(this);

		mSeek_Delete = (TextView) findViewById(R.id.seek_delete);
		mSeek_Delete.setVisibility(View.INVISIBLE);
		mSeek_Delete.setOnClickListener(this);

		mSeekName = (EditText) findViewById(R.id.seek_friendname);
		mSeekName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				String userId = s.toString().trim();

				if (userId.length() == 0) {
					// 输入空的时候，隐藏消除图标
					mSeek_Delete.setVisibility(View.INVISIBLE);
				} else {
					mSeek_Delete.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void setCustomTitleBar() {
		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("添加");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		case R.id.btn_search:
			seekDetail();
			break;
		case R.id.seek_delete:
			mSeekName.setText("");
			break;
		}
	}

	private void backPrePage() {
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			backPrePage();
		}

		return super.onKeyDown(keyCode, event);
	}

	private void seekDetail() {
		// 日志List取得的参数设定
		String contactid = mSeekName.getText().toString().trim();

		if (contactid.equals("")) {
			Toast.makeText(this, "[账号/手机号码]未输入", Toast.LENGTH_LONG).show();
			return;
		}
		if (contactid.length() != 11) {
			Toast.makeText(this, "[账号/手机号码]输入有误", Toast.LENGTH_LONG).show();
			return;
		}
		if (getPhone(contactid) == false) {
			Toast.makeText(FriendSearchActivity.this, "[账号/手机号码]输入有误",
					Toast.LENGTH_LONG).show();
			return;
		}

		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("ContactId", contactid);

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				if ((result == null) || ("0".equals(result))
						|| ("9".equals(result))) {
					Toast.makeText(FriendSearchActivity.this, "没有找到符合搜索条件的用户",
							Toast.LENGTH_LONG).show();
					return;
				}

				String[] resultiInfo = result.split("\\|");
				if (resultiInfo.length < 2) {
					Toast.makeText(FriendSearchActivity.this, "没有找到符合搜索条件的用户",
							Toast.LENGTH_LONG).show();
					return;
				}

				Intent intent = new Intent();
				Bundle mBundle = new Bundle();
				mBundle.putString("Detail", result);
				mBundle.putString("ContactId", mSeekName.getText().toString()
						.trim());

				intent.putExtras(mBundle);

				if ("8".equals(resultiInfo[0])) {
					// 已经是好友
					intent.setClass(FriendSearchActivity.this,
							FriendDetailActivity.class);
				} else if ("6".equals(resultiInfo[0])) {
					// 自己
					intent.setClass(FriendSearchActivity.this,
							FriendDetailSelfActivity.class);
				} else {
					intent.setClass(FriendSearchActivity.this,
							FriendDetailAddActivity.class);
				}

				startActivityForResult(intent,
						ConstantDef.REQUEST_CODE_FRIENDSEARCH);

				// 设定启动动画
				FriendSearchActivity.this.overridePendingTransition(
						R.anim.activity_in_from_right,
						R.anim.activity_nothing_in_out);
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlContactSearch, param);
	}

	private boolean getPhone(String mobile) {
		Pattern p = Pattern
				.compile("^((14[5,7])|(13[0-9])|(15[^4,\\D])|(18[0-3,5-9]))\\d{8}$");
		Matcher matcher = p.matcher(mobile);

		return matcher.matches();
	}
}
