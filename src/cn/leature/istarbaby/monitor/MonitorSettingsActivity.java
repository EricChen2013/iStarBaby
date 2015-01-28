package cn.leature.istarbaby.monitor;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Packet;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LcLog;
import cn.leature.istarbaby.view.SwitchButton;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

public class MonitorSettingsActivity extends Activity implements
		OnClickListener, IRegisterMonitorListener, OnCheckedChangeListener {
	private final String TAG = "MonitorSettingsActivity";

	protected static final int SHOW_TOAST_MESSAGE = 99;
	// TitleBar的控件
	private TextView mTitleBarTitle;
	private ImageButton mTitleBarCancel, mTitleBarDone;

	private EditText mEditDevNickName, mEditViewPwd;

	private Button mBtnNameModify, mBtnAdvanceSetting;
	private String mDevNickName, mViewPassword;

	private Intent mIntent;
	private Bundle mBundle;
	private MonitorInfo mMonitorInfo;
	private MyMonitor mMonitorClient;
	private int mAVChannel = 0;
	private boolean mDetectChanged = true;

	private MonitorShareModel mShareMonitor = null;
	private String mDetectResult = "设置完成。";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_monitor_settings);

		this.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// // 取得用户登录信息
		// mLoginUserId = LoginInfo.getLoginUserId(this);

		mIntent = this.getIntent();
		mBundle = mIntent.getExtras();
		mMonitorInfo = (MonitorInfo) mBundle
				.getSerializable("monitor_settings");

		mShareMonitor = MonitorShareModel.getInstance();

		// 自定义title bar
		setCustomTitleBar();

		inItUi();

		mMonitorClient = mShareMonitor.getCurrentMonitor();
		mAVChannel = mMonitorClient.getChannel();
		// 注册
		mMonitorClient.registerIOTCListener(this);

		if (mMonitorInfo.Status.equals("已联机")) {
			mBtnAdvanceSetting.setEnabled(true);
			mBtnAdvanceSetting.setTextColor(getResources().getColor(
					R.color.istar_link));

			requestCameraInfo();
		} else {

			mMonitorClient.stop(mAVChannel);
			mMonitorClient.disconnect();

			// 连接
			mMonitorClient.connect(mMonitorClient.getUID());
			mMonitorClient.start(mAVChannel, mMonitorInfo.DeviceName,
					mMonitorInfo.DevicePassword);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		// 注册
		mMonitorClient.unregisterIOTCListener(this);
	}

	public void setCustomTitleBar() {
		mTitleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		mTitleBarTitle.setText("监护器设置");

		mTitleBarCancel = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		mTitleBarCancel.setBackgroundResource(R.drawable.selector_hd_close);
		mTitleBarCancel.setOnClickListener(this);

		mTitleBarDone = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		mTitleBarDone.setVisibility(View.INVISIBLE);
	}

	private void inItUi() {
		mEditDevNickName = (EditText) this
				.findViewById(R.id.monitor_devnickname);
		mEditViewPwd = (EditText) this.findViewById(R.id.monitor_viewpwd);

		mBtnNameModify = (Button) this.findViewById(R.id.monitor_name_modify);
		mBtnNameModify.setOnClickListener(this);

		mBtnAdvanceSetting = (Button) this
				.findViewById(R.id.monitor_advance_setting);
		mBtnAdvanceSetting.setOnClickListener(this);
		// 初始显示
		mEditDevNickName.setText(mMonitorInfo.NickName);
		mEditViewPwd.setText(mMonitorInfo.ViewPassword);

		// 动作警告按钮
		mSports = (SwitchButton) findViewById(R.id.set_switchbutton);
		mSports.setOnCheckedChangeListener(this);

		Button btn_friendlist = (Button) findViewById(R.id.btn_friend_list);
		btn_friendlist.setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeMonitorSettings();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			closeMonitorSettings();
			break;
		case R.id.monitor_name_modify:
			modifyMonitorName();
			break;
		case R.id.monitor_advance_setting:
			monitorAdvanceSetting();
			break;
		case R.id.btn_friend_list:
			monitorFriendList();
			break;
		}
	}

	private void monitorFriendList() {
		Intent intent = new Intent();

		intent.setClass(this, MonitorShareListActivity.class);
		intent.putExtras(mBundle);

		this.startActivityForResult(intent,
				ConstantDef.REQUEST_CODE_MONITOR_SHARE);

		// 设定启动动画
		this.overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);
	}

	private void monitorAdvanceSetting() {

		Intent intent = new Intent();

		intent.setClass(this, MonitorManageActivity.class);
		intent.putExtras(mBundle);

		this.startActivityForResult(intent,
				ConstantDef.REQUEST_CODE_MONITOR_ADVSETTING);
	}

	private void modifyMonitorName() {
		mDevNickName = mEditDevNickName.getText().toString().trim();
		mViewPassword = mEditViewPwd.getText().toString().trim();

		// 检查项目是否有输入
		if ("".equals(mDevNickName)) {
			Toast.makeText(this, "[名称]未输入", Toast.LENGTH_LONG).show();
			return;
		}

		if ("".equals(mViewPassword)) {
			Toast.makeText(this, "[密码]未输入", Toast.LENGTH_LONG).show();
			return;
		}

		modifyMonitorNameDone();
	}

	private void modifyMonitorNameDone() {
		// 保存设备信息
		mMonitorInfo.NickName = mDevNickName;
		mMonitorInfo.ViewPassword = mViewPassword;
		mMonitorInfo.DevicePassword = mViewPassword;
		MonitorDBManager.saveMonitorInfoTable(this, mMonitorInfo);

		// 返回前一页
		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();

		// 设定退出动画
		overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_bottom);
	}

	private void closeMonitorSettings() {
		// 关闭时候，直接返回前一页
		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();

		// 设定退出动画
		overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_bottom);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		MonitorInfo monitorInfo = MonitorDBManager.loadMonitorInfo(this,
				mMonitorInfo.UID, mMonitorInfo.UserId);

		LcLog.i(TAG, "[onProgressChanged] change done. DevicePassword:"
				+ monitorInfo.DevicePassword + ",ViewPassword:"
				+ monitorInfo.ViewPassword);
		// 重新显示
		mEditDevNickName.setText(monitorInfo.NickName);
		mEditViewPwd.setText(monitorInfo.ViewPassword);
	}

	@Override
	public void receiveChannelInfo(MonitorClient paramCamera, int paramInt1,
			int paramInt2) {
		// 连接结果返回
		LcLog.i(TAG, "[receiveChannelInfo] start param1:" + paramInt1
				+ ",param2:" + paramInt2);
		if (paramInt2 == 1) {
			// 摄像机个数，这里无需处理
			return;
		} else if (paramInt2 == 5) {
			// 账号或密码错误
			sendMessageForToast(paramInt2, "账号或密码输入错误。");
			return;
		} else if (paramInt2 == 6) {
			// 连接超时
			sendMessageForToast(paramInt2, "网络连接超时，请稍后再试。");
			return;
		}

		// 连接成功
		sendMessageForToast(0, "");
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
		if (paramInt == 1 || paramInt == 8) {
			// 摄像机个数，这里无需处理
			return;
		} else if (paramInt == 4) {
			// 连接超时，或未知设备
			sendMessageForToast(paramInt, "设备无法识别");
			return;
		} else if (paramInt != 2) {
			// 其他错误
			sendMessageForToast(paramInt, "设备连接错误");
			return;
		}
	}

	private void sendMessageForToast(int paramInt, String string) {
		Message msg = Message.obtain();
		msg.what = SHOW_TOAST_MESSAGE;
		msg.arg1 = paramInt;
		msg.obj = string;

		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_TOAST_MESSAGE:
				// 显示
				showToastMessage(msg.arg1, msg.obj.toString());
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMOTIONDETECT_RESP:
				getMotionDetectDone((byte[]) msg.obj);
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP:
				setMotionDetectDone((byte[]) msg.obj);
				break;
			}

			super.handleMessage(msg);
		}
	};

	protected void setMotionDetectDone(byte[] obj) {
		mSports.setEnabled(true);

		Toast.makeText(this, "动作警告" + mDetectResult, Toast.LENGTH_LONG).show();
	}

	protected void getMotionDetectDone(byte[] obj) {
		mSports.setEnabled(true);

		int level = Packet.byteArrayToInt_Little(obj, 4);
		boolean result = false;
		if ((level <= 0) || (level > 100)) {
			// 关闭
			result = true;
		}

		if (result != mSports.isChecked()) {
			mDetectChanged = false;
			mSports.setChecked(result);
		}
	}

	private SwitchButton mSports;

	protected void showToastMessage(int arg, String msg) {
		if (arg > 0) {
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			return;
		}

		mBtnAdvanceSetting.setEnabled(true);
		mBtnAdvanceSetting.setTextColor(getResources().getColor(
				R.color.istar_link));

		requestCameraInfo();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		LcLog.i(TAG, "[onCheckedChanged] start isChecked:" + isChecked
				+ ",changed:" + mDetectChanged);

		if (!mDetectChanged) {
			mDetectChanged = true;
			return;
		}

		if (isChecked) {
			// 关闭
			setMotionDetect(0);

			mDetectResult = "已关闭。";
		} else {
			setMotionDetect(25);

			mDetectResult = "已启动。";
		}
	}

	private void requestCameraInfo() {
		// 取得当前 动作警告等级
		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMOTIONDETECT_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlGetMotionDetectReq
						.parseContent(mAVChannel));
	}

	private void setMotionDetect(int level) {
		// 修改动作侦测等级
		mSports.setEnabled(false);

		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlSetMotionDetectReq.parseContent(
						mAVChannel, level));
	}
}
