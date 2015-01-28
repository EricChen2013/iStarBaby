package cn.leature.istarbaby.setting;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.domain.PreferenceInfo;
import cn.leature.istarbaby.view.SwitchButton;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingPreferenceActivity extends Activity implements
		OnCheckedChangeListener, OnClickListener {
	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_preference);

		initUI();
		setCustomTitleBar();
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("通用");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);
	}

	private void initUI() {

		SwitchButton switchbun = (SwitchButton) findViewById(R.id.switchButton1);
		TextView mStart_server = (TextView) findViewById(R.id.start_server);
		mStart_server.setText("后台运行模式");
		switchbun.setOnCheckedChangeListener(this);

		// 获取是否开启后台服务
		boolean isPlayServer = PreferenceInfo.get(this,
				PreferenceInfo.PreferenceFileName, false);
		switchbun.setChecked(!isPlayServer);

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

		isChecked = !isChecked;
		switch (buttonView.getId()) {
		case R.id.switchButton1:

			PreferenceInfo.set(this, PreferenceInfo.PreferenceFileName,
					isChecked);

			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;

		default:
			break;
		}

	}

	private void backPrePage() {
		Intent intent = new Intent();
		this.setResult(Activity.RESULT_CANCELED, intent);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		backPrePage();
		return true;
	}
}
