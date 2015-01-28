package cn.leature.istarbaby.friend;

import java.util.HashMap;
import java.util.Map;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.utils.ConstantDef;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FriendMemoNameActivity extends Activity implements OnClickListener {

	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	private Intent mIntent;
	private Bundle mBundle;
	private LzProgressDialog mProgressDialog;
	private EditText mEditFriendMemoName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_memo_name);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		mIntent = getIntent();
		mBundle = mIntent.getExtras();

		initUI();
		// 头部导航
		setCustomTitleBar();
	}

	private void initUI() {
		mEditFriendMemoName = (EditText) this.findViewById(R.id.edit_memo_name);

		mEditFriendMemoName.setText(mBundle.getString("ContactMemo", ""));
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("修改备注");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener((this));
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_cmp);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		case R.id.titlebar_rightbtn1:
			saveMemoName();
			break;
		}
	}

	private void backPrePage() {
		setResult(Activity.RESULT_CANCELED, mIntent);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	private void backPrePageForSave() {
		mBundle.putString("friend_memo_name", mEditFriendMemoName.getText()
				.toString().trim());
		mIntent.putExtras(mBundle);
		setResult(Activity.RESULT_OK, mIntent);
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

	private void saveMemoName() {
		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("ContactId", mBundle.getString("ContactId"));
		param.put("ContactMemo", mEditFriendMemoName.getText().toString()
				.trim());
		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				if ((result == null) || ("0".equals(result))) {
					Toast.makeText(FriendMemoNameActivity.this, "修改备注失败",
							Toast.LENGTH_LONG).show();
				} else {
					backPrePageForSave();
				}
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlContactMemoEdit, param);
	}
}
