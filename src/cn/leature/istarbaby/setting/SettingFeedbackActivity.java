package cn.leature.istarbaby.setting;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
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

public class SettingFeedbackActivity extends Activity implements
		OnClickListener, OnPostProcessListener {
	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;
	private EditText mEditText;
	private HttpPostUtil mHttpUtil;
	private LzProgressDialog mProgressDialog;
	private Intent mIntent;
	// 图片上传成功（Message用）
	protected static final int UPLOAD_FILE_DONE = 3;
	// 文章发表成功（Message用）
	protected static final int CHILD_EDIT_DONE = 4;
	// 文章发表出错（Message用）
	protected static final int CHILD_EDIT_ERROR = -2;

	private Button mQueding;
	private Button mQuxiao;
	private AlertDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_feedback);

		mIntent = this.getIntent();
		mHttpUtil = HttpPostUtil.getInstance();

		mProgressDialog = new LzProgressDialog(SettingFeedbackActivity.this);
		mProgressDialog.setCancelable(true);

		setCustomTitleBar();
		initUI();

	}

	private void initUI() {
		mEditText = (EditText) findViewById(R.id.statusedit);

		Button publish = (Button) findViewById(R.id.publish);
		publish.setOnClickListener(this);
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("建议反馈");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);

	}

	private void backPrePage() {
		String text = mEditText.getText().toString().trim();
		if (text.length() > 0) {
			// 提示框
			dialog();
		} else {
			doBackPrePage();
		}
	}

	private void doBackPrePage() {
		if (mDialog != null) {
			mDialog.dismiss();
		}

		this.setResult(Activity.RESULT_CANCELED, mIntent);
		this.finish();
		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	private void dialog() {

		View layout = getLayoutInflater().inflate(R.layout.delete_dialog, null);
		mQueding = (Button) layout.findViewById(R.id.delete_queding);
		mQueding.setText("确定");

		mQuxiao = (Button) layout.findViewById(R.id.delete_quxiao);

		// 提示信息
		TextView title = (TextView) layout.findViewById(R.id.alert_text);
		title.setText("确定要取消发送建议吗？");

		mQuxiao.setOnClickListener(this);
		mQueding.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		mDialog = builder.create();
		mDialog.show();
		mDialog.setContentView(layout);
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
		case R.id.publish:
			statusPrePage();
			break;
		case R.id.delete_queding:
			doBackPrePage();
			break;
		case R.id.delete_quxiao:
			mDialog.dismiss();
			break;
		default:
			break;
		}
	}

	private void statusPrePage() {
		String publishDetail = mEditText.getText().toString().trim();

		// 发表内容为空的时候
		if ("".equals(publishDetail)) {
			Toast.makeText(this, "您还没有输入内容。", Toast.LENGTH_LONG).show();
			return;
		}

		mProgressDialog.set_Text("提交建议...", false);
		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("Detail", publishDetail);
		mHttpUtil.setOnPostProcessListener(this);
		mHttpUtil.sendPostMessage(ConstantDef.cUrlFeedbackAdd, param);
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

			case CHILD_EDIT_DONE: // 保存
				saveDetailDone(msg.obj.toString());
				break;

			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}
	};

	protected void toErrorMessage(String msgString) {
		// List取得出错，显示提示信息
		mProgressDialog.dismiss();
		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	protected void saveDetailDone(String listResult) {

		if ((listResult == null) || "0".equals(listResult)) {
			mProgressDialog.dismiss();

			Toast.makeText(this, "建议提交失败！", Toast.LENGTH_LONG).show();
			return;
		}

		mProgressDialog.showSubmitSuccess("建议提交成功！");

		(new Handler()).postDelayed(new Runnable() {

			@Override
			public void run() {
				mProgressDialog.dismiss();

				SettingFeedbackActivity.this.setResult(Activity.RESULT_OK,
						mIntent);
				SettingFeedbackActivity.this.finish();

				// 设定退出动画
				SettingFeedbackActivity.this.overridePendingTransition(
						R.anim.activity_nothing_in_out,
						R.anim.activity_out_to_right);
			}
		}, 2000);
	}
}
