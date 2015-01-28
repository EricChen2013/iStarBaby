package cn.leature.istarbaby.monitor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Packet;
import com.tutk.IOTC.AVIOCTRLDEFs.SWifiAp;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LcLog;
import cn.leature.istarbaby.utils.LzViewHolder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorManageActivity extends Activity implements OnClickListener,
		OnSeekBarChangeListener, IRegisterMonitorListener {
	private final String TAG = "MonitorManageActivity";

	protected static final int SHOW_TOAST_MESSAGE = 99;
	// 用户登录信息
	private String mLoginUserId;

	// TitleBar的控件
	private TextView mTitleBarTitle;
	private ImageButton mTitleBarCancel, mTitleBarDone;

	private Button mBtnModifyDevPwd;
	private EditText mEditOldDevPwd, mEditDevPwd, mEditDevPwdConfirm;

	private Button mBtnMotionDetect;
	private SeekBar mSeekBarAction;

	private int mActionLevelValue = 0;

	private Button mBtnSelectWifi;
	private String mDevPassword;

	private TextView mTextWifiSSid, mTextWifiConn;

	private ArrayList<SWifiAp> mDataWifiAp = new ArrayList<SWifiAp>();
	private ArrayList<Map<String, Object>> mData;

	private int mAVChannel = 0;
	private MonitorShareModel mShareMonitor = null;
	// 修改前 动作侦测等级
	private int mMotionDetectLevel = 0;
	private int mMotionVideoLevel = 0;
	private int mMotionVideoMode = 0;
	private int mMotionVideoEnviroment = 0;

	private Intent mIntent;
	private Bundle mBundle;
	private MonitorInfo mMonitorInfo;

	private MyMonitor mMonitorClient;
	private TextView mTextActionLevel;
	private Button mBtnVideoSetting;

	private Button mBtnSDCard;
	private TextView mTextSDCardTotal, mTextSDCardFree;
	private AlertDialog mDialog;
	private LzProgressDialog mProgressDialog;

	// 录像模式
	private Button mBtnRecordMode;
	private int mRecordModeLevel = 0;

	CharSequence[] mVideoLevel = { "最高", "高", "适中", "低", "最低" };
	CharSequence[] mVideoMode = { "正常", "垂直翻转", "水平翻转(镜像)", "垂直及水平翻转" };
	CharSequence[] mVideoEnviroment = { "室内模式(50hz)", "室内模式(60hz)", "室外模式",
			"夜间模式" };
	CharSequence[] mRecordMode = { "关闭", "全天候录像", "报警录像" };
	private int mLevelIndex, mModeIndex, mEventIndex, mRecordIndex;

	private Spinner mSpinner1, mSpinner2, mSpinner3, mSpinner4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_monitor_manage);

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
		// 注册
		mMonitorClient.registerIOTCListener(this);

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
		// 取得当前 动作警告等级
		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMOTIONDETECT_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlGetMotionDetectReq
						.parseContent(mAVChannel));

		// 取得当前 视频质量等级
		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSTREAMCTRL_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlGetStreamCtrlReq
						.parseContent(mAVChannel));

		// 取得当前 视频模式
		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VIDEOMODE_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlGetVideoModeReq
						.parseContent(mAVChannel));

		// 取得当前 视频环境
		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ENVIRONMENT_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlGetEnvironmentReq
						.parseContent(mAVChannel));

		// 取得当前 录像模式
		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETRECORD_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlGetRecordReq.parseContent(mAVChannel));

		// 取得当前 SD卡信息
		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());

		// wifi取得
		researchWifi();
	}

	public void setCustomTitleBar() {
		mTitleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		mTitleBarTitle.setText("监护器管理");

		mTitleBarCancel = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		mTitleBarCancel.setBackgroundResource(R.drawable.selector_hd_close);
		mTitleBarCancel.setOnClickListener(this);

		mTitleBarDone = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		mTitleBarDone.setVisibility(View.INVISIBLE);
	}

	private void inItUi() {
		mBtnModifyDevPwd = (Button) this
				.findViewById(R.id.monitor_devpwd_modify);
		mBtnModifyDevPwd.setOnClickListener(this);
		mBtnModifyDevPwd.setEnabled(true);
		mBtnModifyDevPwd.setTextColor(getResources().getColor(
				R.color.istar_link));

		mEditOldDevPwd = (EditText) this.findViewById(R.id.monitor_olddevpwd);
		mEditDevPwd = (EditText) this.findViewById(R.id.monitor_devpwd);
		mEditDevPwdConfirm = (EditText) this
				.findViewById(R.id.monitor_devpwd_confirm);

		mBtnMotionDetect = (Button) this
				.findViewById(R.id.monitor_motion_detect);
		mBtnMotionDetect.setOnClickListener(this);
		mBtnMotionDetect.setEnabled(false);

		mSeekBarAction = (SeekBar) this
				.findViewById(R.id.monitor_action_seekbar);
		mSeekBarAction.setOnSeekBarChangeListener(this);
		// 初始
		mSeekBarAction.setEnabled(false);

		mTextActionLevel = (TextView) this
				.findViewById(R.id.monitor_action_level);

		mBtnSelectWifi = (Button) this.findViewById(R.id.monitor_wifi_select);
		mBtnSelectWifi.setOnClickListener(this);

		mTextWifiSSid = (TextView) this.findViewById(R.id.monitor_wifi_text);
		mTextWifiConn = (TextView) this
				.findViewById(R.id.monitor_wificonn_text);

		mBtnVideoSetting = (Button) this
				.findViewById(R.id.monitor_video_modeset);
		mBtnVideoSetting.setOnClickListener(this);

		mSpinner1 = (Spinner) findViewById(R.id.spinner1);
		MyAdapter adapter1 = new MyAdapter(this, mVideoLevel);
		mSpinner1.setEnabled(false);
		mSpinner1.setAdapter(adapter1);

		mSpinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mLevelIndex = position;
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		mSpinner2 = (Spinner) findViewById(R.id.spinner2);
		MyAdapter adapter2 = new MyAdapter(this, mVideoMode);
		mSpinner2.setEnabled(false);
		mSpinner2.setAdapter(adapter2);
		mSpinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mModeIndex = position;

			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		mSpinner3 = (Spinner) findViewById(R.id.spinner3);
		MyAdapter adapter3 = new MyAdapter(this, mVideoEnviroment);
		mSpinner3.setEnabled(false);
		mSpinner3.setAdapter(adapter3);
		mSpinner3.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mEventIndex = position;
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		mSpinner4 = (Spinner) findViewById(R.id.spinner4);
		MyAdapter adapter4 = new MyAdapter(this, mRecordMode);
		mSpinner4.setEnabled(false);
		mSpinner4.setAdapter(adapter4);
		mSpinner4.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mRecordIndex = position;
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		mBtnRecordMode = (Button) findViewById(R.id.btn_record_mode);
		mBtnRecordMode.setOnClickListener(this);

		mBtnSDCard = (Button) findViewById(R.id.btn_sdcard);
		mBtnSDCard.setOnClickListener(this);
		mTextSDCardTotal = (TextView) this
				.findViewById(R.id.monitor_record_total);
		mTextSDCardFree = (TextView) this
				.findViewById(R.id.monitor_record_free);
	}

	private void dialog() {

		View layout = getLayoutInflater().inflate(R.layout.delete_dialog, null);

		Button mQueding = (Button) layout.findViewById(R.id.delete_queding);
		mQueding.setText("确定");

		Button mQuxiao = (Button) layout.findViewById(R.id.delete_quxiao);

		// 提示信息
		TextView title = (TextView) layout.findViewById(R.id.alert_text);
		title.setText("确定要格式化SD卡吗？");

		mQuxiao.setOnClickListener(this);
		mQueding.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		mDialog = builder.create();
		mDialog.show();
		mDialog.setContentView(layout);
	}

	public class MyAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		private CharSequence[] data;

		public MyAdapter(Context context, CharSequence[] data) {
			this.mInflater = LayoutInflater.from(context);
			this.data = data;
		}

		public int getCount() {
			return data.length;
		}

		public Object getItem(int arg0) {
			return data.length;
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			convertView = mInflater.inflate(R.layout.spinner_textview, null);
			TextView titleText = LzViewHolder.get(convertView,
					R.id.spinner_text1);

			titleText.setText(data[position]);

			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			closeMonitorSettings();
			break;
		case R.id.monitor_devpwd_modify:
			modifyMonitorPassword();
			break;
		case R.id.monitor_wifi_select:
			// 修改wifi
			selectWifi();
			break;
		case R.id.monitor_motion_detect:
			setMotionDetect();
			break;
		case R.id.monitor_video_modeset:
			setVideoModeLevel();
			break;
		case R.id.btn_record_mode:
			setRecordModeLevel();
			break;
		case R.id.btn_sdcard:
			// 弹出对话框看是否要格式化SD卡
			dialog();
			break;
		case R.id.delete_queding:
			// 格式化SD卡
			doFormatSDCard();
			break;
		case R.id.delete_quxiao:
			// 取消格式化
			mDialog.dismiss();
			break;
		}
	}

	private void doFormatSDCard() {
		mDialog.dismiss();

		// 格式化SD卡
		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlFormatExtStorageReq.parseContent(0));

		mProgressDialog.show();
	}

	private void setRecordModeLevel() {
		// 录像模式
		int level = mRecordIndex;

		if (level != mRecordModeLevel) {
			// 改变录像模式
			mRecordModeLevel = level;

			mMonitorClient.sendIOCtrl(mAVChannel,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETRECORD_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlSetRecordReq.parseContent(
							this.mAVChannel, level));

			Toast.makeText(this, "摄像机设置成功。", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "你没有修改设置。", Toast.LENGTH_LONG).show();
		}
	}

	private void setVideoModeLevel() {
		// 视频质量
		int level = mLevelIndex + 1;
		int mode = mModeIndex;
		int videoEnv = mEventIndex;
		boolean hadChanged = false;

		if (level != mMotionVideoLevel) {
			// 改变视频质量
			hadChanged = true;
			mMotionVideoLevel = level;

			mMonitorClient.sendIOCtrl(mAVChannel,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(
							this.mMonitorInfo.ChannelIndex, (byte) level));
		}

		if (mode != mMotionVideoMode) {
			// 改变视频模式
			hadChanged = true;
			mMotionVideoMode = mode;

			mMonitorClient.sendIOCtrl(mAVChannel,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq.parseContent(
							this.mMonitorInfo.ChannelIndex, (byte) mode));
		}

		if (videoEnv != mMotionVideoEnviroment) {
			// 改变视频环境
			hadChanged = true;
			mMotionVideoEnviroment = videoEnv;

			mMonitorClient.sendIOCtrl(mAVChannel,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlSetEnvironmentReq.parseContent(
							this.mMonitorInfo.ChannelIndex, (byte) videoEnv));
		}

		if (hadChanged) {
			Toast.makeText(this, "摄像机设置成功。", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "你没有修改设置。", Toast.LENGTH_LONG).show();
		}
	}

	private void setMotionDetect() {
		// 修改动作侦测等级
		int level = mSeekBarAction.getProgress();
		if (level > 0 && level < 100) {
			level = level / 25 + 1;
			level *= 25;
		}

		if (level == mMotionDetectLevel) {
			// 没有改变等级
			Toast.makeText(this, "你没有改变侦测等级。", Toast.LENGTH_LONG).show();
			return;
		}

		mMotionDetectLevel = level;
		mSeekBarAction.setEnabled(false);
		mBtnMotionDetect.setEnabled(false);
		mBtnMotionDetect.setTextColor(getResources().getColor(R.color.istar_x));

		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlSetMotionDetectReq.parseContent(
						this.mMonitorInfo.ChannelIndex, level));
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
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMOTIONDETECT_RESP:
				getMotionDetectDone((byte[]) msg.obj);
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP:
				setMotionDetectDone((byte[]) msg.obj);
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSTREAMCTRL_RESP:
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ENVIRONMENT_RESP:
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VIDEOMODE_RESP:
				displayVideoMode(msg.what, (byte[]) msg.obj);
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETRECORD_RESP:
				displayRecordMode((byte[]) msg.obj);
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP:
				displayDeviceInfo((byte[]) msg.obj);
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_RESP:
				processFormatSDCardResult((byte[]) msg.obj);
				break;
			case SHOW_TOAST_MESSAGE:
				// 显示
				showToastMessage(msg.arg1, msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}
	};

	protected void processFormatSDCardResult(byte[] result) {
		// 关闭进度条
		mProgressDialog.dismiss();

		int ret = result[5];
		if (ret == 0) {
			// 保存设备信息
			Toast.makeText(this, "格式化SD卡成功。", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "格式化SD卡失败。", Toast.LENGTH_LONG).show();
		}
	}

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

	protected void displayVideoMode(int what, byte[] obj) {
		byte result = obj[4];

		switch (what) {
		case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSTREAMCTRL_RESP:
			if (result >= 1) {
				mMotionVideoLevel = result;

				mSpinner1.setSelection(result - 1);
				mSpinner1.setEnabled(true);
				break;

			}
		case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ENVIRONMENT_RESP:
			if (result >= 0) {
				mMotionVideoEnviroment = result;

				mSpinner3.setSelection(mMotionVideoEnviroment);
				mSpinner3.setEnabled(true);

			}
			break;
		case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VIDEOMODE_RESP:
			if (result >= 0) {
				mMotionVideoMode = result;
				mSpinner2.setSelection(mMotionVideoMode);

				mSpinner2.setEnabled(true);
			}

			break;
		}

		mBtnVideoSetting.setEnabled(true);
		mBtnVideoSetting.setTextColor(getResources().getColor(
				R.color.istar_link));
	}

	protected void displayRecordMode(byte[] obj) {

		byte result = obj[4];

		mRecordModeLevel = result;

		mSpinner4.setSelection(mRecordModeLevel);
		mSpinner4.setEnabled(true);

		mBtnRecordMode.setEnabled(true);
		mBtnRecordMode
				.setTextColor(getResources().getColor(R.color.istar_link));
	}

	protected void displayDeviceInfo(byte[] abyte0) {

		int totalSize = Packet.byteArrayToInt_Little(abyte0, 40);
		int freeSize = Packet.byteArrayToInt_Little(abyte0, 44);

		DecimalFormat decimalFormat = new DecimalFormat("###,###");

		mTextSDCardTotal.setText(decimalFormat.format(totalSize) + " MB");
		mTextSDCardFree.setText(decimalFormat.format(freeSize) + " MB");

		if (totalSize == 0) {
			// 无SD卡
			return;
		}

		mBtnSDCard.setEnabled(true);
		mBtnSDCard.setTextColor(getResources().getColor(R.color.istar_link));
	}

	protected void setMotionDetectDone(byte[] obj) {
		mSeekBarAction.setEnabled(true);
		mBtnMotionDetect.setEnabled(true);
		mBtnMotionDetect.setTextColor(getResources().getColor(
				R.color.istar_link));

		Toast.makeText(this, "摄像机设置成功。", Toast.LENGTH_LONG).show();
	}

	protected void getMotionDetectDone(byte[] obj) {
		mSeekBarAction.setEnabled(true);
		mBtnMotionDetect.setEnabled(true);
		mBtnMotionDetect.setTextColor(getResources().getColor(
				R.color.istar_link));

		mMotionDetectLevel = Packet.byteArrayToInt_Little(obj, 4);

		mTextActionLevel.setText(actionLevelForShow(mMotionDetectLevel));

		mSeekBarAction.setProgress(mMotionDetectLevel);
	}

	private void modifyMonitorPassword() {
		String oldpwd = mEditOldDevPwd.getText().toString().trim();
		mDevPassword = mEditDevPwd.getText().toString().trim();
		String conpwd = mEditDevPwdConfirm.getText().toString().trim();

		// 检查项目是否有输入
		if ("".equals(oldpwd) || "".equals(mDevPassword) || "".equals(conpwd)) {
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
				AVIOCTRLDEFs.SMsgAVIoctrlSetPasswdReq.parseContent(oldpwd,
						mDevPassword));
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
			MonitorDBManager.saveMonitorInfoTable(this, mMonitorInfo);

			Toast.makeText(this, "密码修改成功。稍后摄像机会自动重新连接。", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(this, "密码修改失败。", Toast.LENGTH_LONG).show();
		}
	}

	private void closeMonitorSettings() {
		// 关闭时候，直接返回前一页
		this.setResult(Activity.RESULT_CANCELED, mIntent);
		this.finish();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

		if (progress == 0) {
			mActionLevelValue = 0;
		} else if (progress <= 25) {
			// 1-25: 级别1
			mActionLevelValue = 1;
		} else if (progress <= 50) {
			// 26-50: 级别2
			mActionLevelValue = 2;
		} else if (progress <= 75) {
			// 51-75: 级别3
			mActionLevelValue = 3;
		} else {
			// 76-100: 级别4
			mActionLevelValue = 4;
		}

		mTextActionLevel.setText("" + actionLevelForShow(mActionLevelValue));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		LcLog.i(TAG, "[onStartTrackingTouch] start done");
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		LcLog.i(TAG, "[onStopTrackingTouch] stop done");
	}

	private String actionLevelForShow(int level) {
		String result = "";

		switch (level) {
		case 0:
			result = "关闭";
			break;
		case 1:
			result = "低";
			break;
		case 2:
			result = "中";
			break;
		case 3:
			result = "高";
			break;
		case 4:
			result = "最高";
			break;
		}

		return result;
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
