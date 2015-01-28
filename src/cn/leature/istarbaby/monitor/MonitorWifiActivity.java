package cn.leature.istarbaby.monitor;

import java.util.ArrayList;
import java.util.Map;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.ListHeightUtils;
import cn.leature.istarbaby.utils.LzViewHolder;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorWifiActivity extends Activity implements
		OnItemClickListener, OnClickListener {

	protected static final int SHOW_TOAST_MESSAGE = 99;

	private ScrollView mScrollView;
	private ListView mListView;
	private String[] mWifiNames, mWifiSignals;
	byte[] mWifistatus;
	private AlertDialog mMonitorSearchDialog;
	private EditText mWifiDialog_Password;
	private Bundle mBundle;
	private ArrayList<Map<String, Object>> mData;
	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	private Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_wifi);

		mIntent = this.getIntent();

		// 设置title
		setCustomTitleBar();

		initUI();
	}

	private void initUI() {
		mListView = (ListView) findViewById(R.id.wifiListview);

		mBundle = getIntent().getExtras();
		ArrayList list = mBundle.getParcelableArrayList("list");

		mData = (ArrayList<Map<String, Object>>) list.get(0);
		if (mData.size() < 0) {
			return;
		}
		int size = mData.size();
		mWifiNames = new String[size];
		mWifiSignals = new String[size];
		mWifistatus = new byte[size];
		for (int i = 0; i < mData.size(); i++) {

			Map<String, Object> map = mData.get(i);
			mWifiNames[i] = (String) map.get("title");
			mWifiSignals[i] = (String) map.get("text");
			mWifistatus[i] = (Byte) map.get("status");
		}
		MyAdapter adapter = new MyAdapter(mWifiNames, mWifiSignals, mWifistatus);
		mListView.setAdapter(adapter);
		ListHeightUtils.setListViewHeightBasedOnChildren(mListView,
				MonitorWifiActivity.this);
		mListView.setOnItemClickListener(this);

		mScrollView = (ScrollView) findViewById(R.id.scrollView1);
		mScrollView.smoothScrollTo(0, 0);

	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("设置Wi-Fi网络");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_ccl);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);

	}

	class MyAdapter extends BaseAdapter {
		private String[] mWifiName;
		private String[] mWifiSignal;
		private byte[] mWifistatu;

		public MyAdapter(String[] mWifiNames, String[] mWifiSignals,
				byte[] mWifistatus) {
			this.mWifiName = mWifiNames;
			this.mWifiSignal = mWifiSignals;
			this.mWifistatu = mWifistatus;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = getLayoutInflater().inflate(
					R.layout.wifi_listview_item, null);
			TextView mTextName = LzViewHolder.get(convertView,
					R.id.wifi_textname);
			TextView mTextSignal = LzViewHolder.get(convertView,
					R.id.wifi_signal);
			ImageView mWifi_resize = LzViewHolder.get(convertView,
					R.id.wifi_resize);

			if (mWifistatu[position] == 1) {
				mWifi_resize.setVisibility(View.VISIBLE);
			}
			mTextName.setText(mWifiName[position]);
			mTextSignal.setText(mWifiSignal[position]);

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {

		if (mWifistatus[position] == 1) {
			// Wifi已经连接
			Toast.makeText(this, "Wi-Fi网络已连接。", Toast.LENGTH_LONG).show();
			return;
		}

		LayoutInflater inflater = getLayoutInflater();
		View inflate = inflater.inflate(R.layout.wifi_dailog_layout,
				(ViewGroup) findViewById(R.id.wifi_dialog));

		TextView mWifidialog_Name = (TextView) inflate
				.findViewById(R.id.wifidialog_name);
		Button btn_ok = (Button) inflate.findViewById(R.id.wifidialog_btn_ok);

		Button btn_close = (Button) inflate
				.findViewById(R.id.wifidialog_btn_close);
		mWifiDialog_Password = (EditText) inflate
				.findViewById(R.id.wifidialog_pwd);

		mWifidialog_Name.setText(mWifiNames[position]);
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doJoinWifi(position);
			}
		});
		btn_close.setOnClickListener(MonitorWifiActivity.this);

		// dialog
		mMonitorSearchDialog = new AlertDialog.Builder(this).setView(inflate)
				.show();
	}

	public void doJoinWifi(int position) {
		String pwd = mWifiDialog_Password.getText().toString().trim();
		// 检查项目是否有输入
		if ("".equals(pwd)) {
			Toast.makeText(this, "[密码]未输入", Toast.LENGTH_LONG).show();
			return;
		}

		mMonitorSearchDialog.dismiss();

		Intent data = new Intent();
		mBundle.putString("password", pwd);
		mBundle.putInt("newPosition", position);
		data.putExtras(mBundle);
		setResult(Activity.RESULT_OK, data);

		this.finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			cancelWiFiSetting();

			break;
		case R.id.wifidialog_btn_close:
			mMonitorSearchDialog.dismiss();
			break;
		}
	}

	private void cancelWiFiSetting() {
		this.setResult(Activity.RESULT_CANCELED, mIntent);
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			cancelWiFiSetting();
		}

		return true;
	}
}
