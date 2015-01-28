package cn.leature.istarbaby.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.SWifiAp;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LcLog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorAddSettingActivity extends Activity implements
		OnClickListener, IRegisterMonitorListener {
	private final String TAG = "MonitorAddSettingActivity";

	protected static final int SHOW_TOAST_MESSAGE = 99;
	// 用户登录信息
	private String mLoginUserId;

	// TitleBar的控件
	private TextView mTitleBarTitle;
	private ImageButton mTitleBarCancel, mTitleBarDone;

	private Button mBtnModifyDevPwd;
	private EditText mEditDevPwd, mEditDevPwdConfirm;

	private Button mBtnSelectWifi;
	private String mDevPassword = "";

	private TextView mTextWifiSSid, mTextWifiConn;

	private ArrayList<SWifiAp> mDataWifiAp = new ArrayList<SWifiAp>();
	private ArrayList<Map<String, Object>> mData;

	private int mAVChannel = 0;
	private MonitorShareModel mShareMonitor = null;

	private Intent mIntent;
	private Bundle mBundle;
	private MonitorInfo mMonitorInfo;
	private boolean mIsDevPwdChanged = false;

	private MyMonitor mMonitorClient;

	private LzProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_monitor_add_setting);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		mShareMonitor = MonitorShareModel.getInstance();

		// 取得用户登录信息
		mLoginUserId = LoginInfo.getLoginUserId(this);

		mIntent = this.getIntent();
		mBundle = mIntent.getExtras();
		mMonitorInfo = (MonitorInfo) mBundle
				.getSerializable("monitor_settings");

		// 自定义title bar
		setCustomTitleBar();

		inItUi();

		mMonitorClient = mShareMonitor.getCurrentMonitor();

		mAVChannel = mMonitorClient.getChannel();
		mMonitorInfo.ChannelIndex = mAVChannel;

		// 请求数据
		requestCameraInfo();
	}

	@Override
	protected void onResume() {
		// 注册
		mMonitorClient.registerIOTCListener(this);

		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// 注册
		mMonitorClient.unregisterIOTCListener(this);
	}

	private void requestCameraInfo() {

		// wifi取得
		researchWifi();
	}

	public void setCustomTitleBar() {
		mTitleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		mTitleBarTitle.setText("设置密码及Wi-Fi网络");

		mTitleBarCancel = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		mTitleBarCancel.setBackgroundResource(R.drawable.selector_hd_close);
		mTitleBarCancel.setOnClickListener(this);

		mTitleBarDone = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		mTitleBarDone.setBackgroundResource(R.drawable.selector_hd_cmp);
		mTitleBarDone.setOnClickListener(this);
	}

	private void inItUi() {
		mBtnModifyDevPwd = (Button) this
				.findViewById(R.id.monitor_devpwd_modify);
		mBtnModifyDevPwd.setOnClickListener(this);
		mBtnModifyDevPwd.setEnabled(true);
		mBtnModifyDevPwd.setTextColor(getResources().getColor(
				R.color.istar_link));

		mEditDevPwd = (EditText) this.findViewById(R.id.monitor_devpwd);
		mEditDevPwdConfirm = (EditText) this
				.findViewById(R.id.monitor_devpwd_confirm);

		mBtnSelectWifi = (Button) this.findViewById(R.id.monitor_wifi_select);
		mBtnSelectWifi.setOnClickListener(this);

		mTextWifiSSid = (TextView) this.findViewById(R.id.monitor_wifi_text);
		mTextWifiConn = (TextView) this
				.findViewById(R.id.monitor_wificonn_text);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			closeMonitorSettings();
			break;
		case R.id.titlebar_rightbtn1:
			completeMonitorSettings();
			break;
		case R.id.monitor_devpwd_modify:
			modifyMonitorPassword();
			break;
		case R.id.monitor_wifi_select:
			// 修改wifi
			selectWifi();
			break;
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTWIFIAP_RESP:
				displayWifiList((byte[]) msg.obj);
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_RESP:
				modifyWifiDone((byte[]) msg.obj);
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_RESP:
				modifyMonitorPasswordDone((byte[]) msg.obj);
				break;
			case SHOW_TOAST_MESSAGE:
				// 显示
				showToastMessage(msg.arg1, msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}
	};

	protected void showToastMessage(int arg, String msg) {
		if (arg > 0) {
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			return;
		}
	}

	protected void modifyWifiDone(byte[] result) {
		// WiFi修改返回
		mBtnSelectWifi.setEnabled(true);
		mBtnSelectWifi
				.setTextColor(getResources().getColor(R.color.istar_link));

		int ret = result[0];
		if (ret == 0) {
			// 保存设备信息
			mTextWifiConn.setText("已连接");

			Toast.makeText(this, "Wi-Fi设置成功。", Toast.LENGTH_LONG).show();
		} else {
			mTextWifiConn.setText("Wi-Fi设置失败");
		}
	}

	private void selectWifi() {
		LcLog.i(TAG, "[selectWifi] search wifi.");

		// 跳转到wifi设定页面
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		// 传递复杂数据ArrayList<Map<String, Object>>
		ArrayList bundlelist = new ArrayList();
		bundlelist.add(mData);
		bundle.putParcelableArrayList("list", bundlelist);
		intent.putExtras(bundle);

		intent.setClass(this, MonitorWifiActivity.class);
		startActivityForResult(intent, ConstantDef.REQUEST_CODE_MONITOR_WIFI);
	}

	private void modifyMonitorPassword() {
		mDevPassword = mEditDevPwd.getText().toString().trim();
		String conpwd = mEditDevPwdConfirm.getText().toString().trim();

		// 检查项目是否有输入
		if ("".equals(mDevPassword) || "".equals(conpwd)) {
			Toast.makeText(this, "[密码]未输入", Toast.LENGTH_LONG).show();
			return;
		}
		if (!mDevPassword.equals(conpwd)) {
			Toast.makeText(this, "两次输入的新密码不一致。", Toast.LENGTH_LONG).show();
			return;
		}

		mBtnModifyDevPwd.setEnabled(false);
		mBtnModifyDevPwd.setTextColor(getResources().getColor(R.color.istar_x));

		// 修改设备密码， 其实旧密码是无效的（没有检查）
		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlSetPasswdReq.parseContent(
						mDevPassword, mDevPassword));
	}

	private void modifyMonitorPasswordDone(byte[] result) {
		// 密码修改返回
		mBtnModifyDevPwd.setEnabled(true);
		mBtnModifyDevPwd.setTextColor(getResources().getColor(
				R.color.istar_link));

		int ret = result[0];
		if (ret == 0) {
			// 保存设备信息
			mMonitorInfo.ViewPassword = mDevPassword;
			mMonitorInfo.DevicePassword = mDevPassword;
			mIsDevPwdChanged = true;

			Toast.makeText(this, "密码修改成功。", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "密码修改失败。", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeMonitorSettings();
		}
		return true;
	}

	private void closeMonitorSettings() {
		// 关闭时候，直接返回前一页
		if (mIsDevPwdChanged) {
			// 修改过密码
			mIntent.putExtra("camera_changepwd", mDevPassword);
		}

		this.setResult(Activity.RESULT_CANCELED, mIntent);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	private void completeMonitorSettings() {
		// 关闭时候，直接返回前一页
		if (mIsDevPwdChanged) {
			// 修改过密码
			mIntent.putExtra("camera_changepwd", mDevPassword);
		}

		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public void receiveChannelInfo(MonitorClient paramCamera, int paramInt1,
			int paramInt2) {
		// 连接结果返回
		LcLog.i(TAG, "[receiveChannelInfo] start param1:" + paramInt1
				+ ",param2:" + paramInt2);
	}

	@Override
	public void receiveFrameData(MonitorClient paramCamera, int paramInt,
			Bitmap paramBitmap) {

	}

	@Override
	public void receiveFrameInfo(MonitorClient paramCamera, int paramInt1,
			long paramLong, int paramInt2, int paramInt3, int paramInt4,
			int paramInt5) {

	}

	@Override
	public void receiveIOCtrlData(MonitorClient paramCamera, int channel,
			int ioCtrlType, byte[] paramArrayOfByte) {
		LcLog.i(TAG, "[receiveIOCtrlData] start param1:" + channel + ",param2:"
				+ ioCtrlType);

		Message msg = Message.obtain();
		msg.what = ioCtrlType;
		msg.arg1 = channel;
		msg.obj = paramArrayOfByte;

		handler.sendMessage(msg);
	}

	@Override
	public void receiveSessionInfo(MonitorClient paramCamera, int paramInt) {
		// 连接结果返回
		LcLog.i(TAG, "[receiveSessionInfo] start param:" + paramInt);
	}

	protected void displayWifiList(byte[] paramArrayOfByte) {
		LcLog.i(TAG, "[displayWifiList] start .");

		int len = 4;
		byte[] byteCount = new byte[len];
		System.arraycopy(paramArrayOfByte, 0, byteCount, 0, len);

		len = SWifiAp.getTotalSize();
		int wifiCount = paramArrayOfByte[0];
		if (paramArrayOfByte.length < wifiCount * len + 1) {
			// 数据位数不足
			LcLog.e(TAG, "[displayWifiList] wifi parse failed. size:"
					+ paramArrayOfByte.length);
			return;
		}

		mBtnSelectWifi.setEnabled(true);
		mBtnSelectWifi
				.setTextColor(getResources().getColor(R.color.istar_link));

		mData = new ArrayList<Map<String, Object>>();
		boolean isWifiConnected = false;
		int start = 4; // 不知道为什么是 第5位开始
		for (int i = 0; i < wifiCount; i++) {
			byte[] arrayOfByte = new byte[len];
			System.arraycopy(paramArrayOfByte, start + i * len, arrayOfByte, 0,
					len);
			SWifiAp swifiAp = new SWifiAp(arrayOfByte);

			LcLog.i(TAG, "[displayWifiList] wifi :" + i + ",uid :"
					+ getWifiSSidString(swifiAp.ssid) + ",mode:" + swifiAp.mode
					+ ",enctype:" + swifiAp.enctype + ",signal:"
					+ swifiAp.signal + ",status:" + swifiAp.status);
			// 添加到列表
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("title", getWifiSSidString(swifiAp.ssid).trim());
			item.put("text", "信号强度: " + swifiAp.signal + " %");
			item.put("swifiAp", swifiAp.enctype);
			item.put("status", swifiAp.status);

			mData.add(item);
			mDataWifiAp.add(swifiAp);

			if (swifiAp.status == 1) {
				// 连接wifi
				isWifiConnected = true;
				mTextWifiSSid.setText(getWifiSSidString(swifiAp.ssid).trim());
				mTextWifiConn.setVisibility(View.VISIBLE);
				mTextWifiConn.setText("已连接");
			} else if (swifiAp.status == 2) {
				// wifi密码错误
				isWifiConnected = true;
				mTextWifiSSid.setText(getWifiSSidString(swifiAp.ssid).trim());
				mTextWifiConn.setVisibility(View.VISIBLE);
				mTextWifiConn.setText("Wi-Fi密码错误");
			}
		}

		if (!isWifiConnected) {
			mTextWifiSSid.setText("未连接Wi-Fi");
			mTextWifiConn.setVisibility(View.INVISIBLE);
		}
	}

	private String getWifiSSidString(byte[] paramArrayOfByte) {
		StringBuilder localStringBuilder = new StringBuilder();

		for (int i = 0; i < paramArrayOfByte.length; i++) {

			if (paramArrayOfByte[i] != 0) {
				localStringBuilder.append((char) paramArrayOfByte[i]);
			}
		}

		return localStringBuilder.toString();
	}

	private void researchWifi() {
		// 取得wifi
		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTWIFIAP_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlListWifiApReq.parseContent());
	}

	public void doJoinWifi(int mWifiSelected, String psw) {
		LcLog.e("mDataWifiAp", "密码==" + psw + "行号" + mWifiSelected);

		SWifiAp swifiAp = (SWifiAp) mDataWifiAp.get(mWifiSelected);
		mBtnSelectWifi.setEnabled(false);
		mBtnSelectWifi.setTextColor(getResources().getColor(R.color.istar_x));

		mTextWifiSSid.setText(getWifiSSidString(swifiAp.ssid).trim());
		mTextWifiConn.setVisibility(View.VISIBLE);
		mTextWifiConn.setText("正在设置Wi-Fi...");

		// 尝试连接新wifi
		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq.parseContent(swifiAp.ssid,
						psw.getBytes(), swifiAp.mode, swifiAp.enctype));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case ConstantDef.RESULT_CODE_MONITOR_WIFI:
				Bundle bundle = data.getExtras();
				int newPosition = bundle.getInt("newPosition");
				String psw = bundle.getString("password");

				doJoinWifi(newPosition, psw);
				break;
			}
		}
	}
}
