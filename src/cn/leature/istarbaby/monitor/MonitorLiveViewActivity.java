package cn.leature.istarbaby.monitor;

import java.io.File;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.utils.DensityUtil;
import cn.leature.istarbaby.utils.LcLog;
import cn.leature.istarbaby.utils.SaveBitmapFile;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorLiveViewActivity extends Activity implements
		IRegisterMonitorListener, OnClickListener {
	private final String TAG = "MonitorLiveViewActivity";

	protected static final int SHOW_TOAST_MESSAGE = 99;
	protected static final int GET_VIDEO_PIXEL_DONE = 1001;
	protected static final int GET_VIDEO_ONLINE_DONE = 1002;

	protected static final int FUNCTION_BUTTON_HEIGHT = 40;

	protected static final String LastFrameFileName = "iSCamLastFrame_";

	// TitleBar的控件
	private LinearLayout mLayoutTitlebar;
	private TextView mTitleBarTitle;
	private ImageButton mTitleBarCancel, mTitleBarDone;

	private TextView mTextMonitorStatus, mTextMonitorPixels,
			mTextMonitorOnline;
	private LinearLayout mLayoutMonitorPixels;
	private int mVideoWidth = 0;
	private int mVideoHeight = 0;

	private LinearLayout mLayoutFunction;
	private ImageView mBtnMonitorSnapShot;

	private boolean mIsListening = false;
	private boolean mIsSpeaking = false;

	private int mScreenWidth, mScreenHeight;

	private MonitorSurfaceView mMonitorView;

	private MyMonitor mMonitorClient = null;

	private int mAVChannel = 0;
	private MonitorShareModel mShareMonitor = null;

	private Intent mIntent;
	private Bundle mBundle;
	private MonitorInfo mMonitorInfo;
	private ImageView mBtnMonitorModle;
	private TextView mMonitor_MuTextView;

	private AlertDialog mColokDialog;
	private ImageView speak_img;
	private LinearLayout mSpeakingLayout1, mSpeakingLayout2;
	private TextView mTxtSpeaking;
	private int mPreVolumn = 0;
	private int mVolumnLevel = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_monitor_live_view);

		mIntent = this.getIntent();
		mBundle = mIntent.getExtras();
		mMonitorInfo = (MonitorInfo) mBundle
				.getSerializable("monitor_selected");

		// 自定义title bar
		setCustomTitleBar();
		initUIView();

		int i = ((WindowManager) getSystemService("window"))
				.getDefaultDisplay().getOrientation();
		if (i == 1) {
			// 横屏
			setupViewInLandscapeLayout();
		} else {
			// 竖屏
			setupViewInPortraitLayout();
		}

		mShareMonitor = MonitorShareModel.getInstance();
		mMonitorClient = mShareMonitor.getCurrentMonitor();
		mAVChannel = mMonitorClient.getChannel();
	}

	public void setCustomTitleBar() {
		mLayoutTitlebar = (LinearLayout) this
				.findViewById(R.id.monitor_titlebar);

		mTitleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		mTitleBarTitle.setText("实时影像：" + mMonitorInfo.NickName);

		mTitleBarCancel = (ImageButton) this
				.findViewById(R.id.titlebar_leftbtn);
		mTitleBarCancel.setBackgroundResource(R.drawable.selector_hd_close);
		mTitleBarCancel.setOnClickListener(this);

		mTitleBarDone = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		mTitleBarDone.setVisibility(View.INVISIBLE);
	}

	public void initUIView() {
		mMonitorView = (MonitorSurfaceView) this.findViewById(R.id.monitor);

		mLayoutMonitorPixels = (LinearLayout) this
				.findViewById(R.id.monitor_pixels_layout);
		mTextMonitorStatus = (TextView) this.findViewById(R.id.monitor_status);
		mTextMonitorPixels = (TextView) this.findViewById(R.id.monitor_pixels);
		mTextMonitorOnline = (TextView) this.findViewById(R.id.monitor_online);

		mLayoutFunction = (LinearLayout) this
				.findViewById(R.id.monitor_function_layout);

		// 相册 、拍照、静音
		mBtnMonitorSnapShot = (ImageView) this
				.findViewById(R.id.monitor_snapshot);
		mBtnMonitorSnapShot.setOnClickListener(this);

		mBtnMonitorModle = (ImageView) this
				.findViewById(R.id.monitor_snapshot_modle);
		mBtnMonitorModle.setOnClickListener(this);

		mMonitor_MuTextView = (TextView) this
				.findViewById(R.id.monitor_mutextview);

		mTxtSpeaking = (TextView) findViewById(R.id.selector_speak);
		mTxtSpeaking.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				try {
					mTxtSpeaking.setText("松开 结束");
					// 固定竖屏
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

					// 显示框
					speakingDialogStart();

					// 启动通话
					monitorStartSpeaking();
				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

				return false;
			}
		});

		mTxtSpeaking.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:

					mColokDialog.dismiss();
					mTxtSpeaking.setText("按住 说话");
					// 恢复 屏幕可翻转
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

					monitorStopSpeaking();
					break;
				}

				return false;
			}
		});
	}

	private void speakingDialogStart() {

		View layout = getLayoutInflater().inflate(
				R.layout.layout_selectorspeak_dialog, null);

		speak_img = (ImageView) layout.findViewById(R.id.speak_audio_img);
		mSpeakingLayout1 = (LinearLayout) layout
				.findViewById(R.id.speaking_layout1);
		mSpeakingLayout2 = (LinearLayout) layout
				.findViewById(R.id.speaking_layout2);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		mColokDialog = builder.create();
		mColokDialog.show();
		mColokDialog.setContentView(layout);
	}

	private void speakingDialogVisible() {

		// 如果监听中，先暂停
		if (mIsListening) {
			// 暂停监听
			mMonitorClient.pauseListening(mAVChannel);
		}

		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// 显示话筒及音量
				mSpeakingLayout1.setVisibility(View.INVISIBLE);
				mSpeakingLayout2.setVisibility(View.VISIBLE);
			}
		}, 1000);
	}

	private void speakingDialogProcess(int length, byte[] outBuf) {
		int total = 0;
		for (int i = 0; i < length; i++) {
			total += outBuf[i] * outBuf[i];
		}
		// 平方和除以数据总长度，得到音量大小。可以获取白噪声值，然后对实际采样进行标准化。
		int volumn = total / length / 200; // 除以200，是为了降低敏感度
		Log.e("speakingDialogProcess", String.valueOf(volumn));

		if (volumn > mPreVolumn) {
			mVolumnLevel += 1;
		} else {
			mVolumnLevel -= 1;
		}

		if (mVolumnLevel < 1) {
			mVolumnLevel = 1;
		} else if (mVolumnLevel > 7) {
			mVolumnLevel = 7;
		}
		mPreVolumn = volumn;

		switch (mVolumnLevel) {
		case 1:
			speak_img.setImageResource(R.drawable.ms);
			break;
		case 2:
			speak_img.setImageResource(R.drawable.mt);
			break;
		case 3:
			speak_img.setImageResource(R.drawable.mu);
			break;
		case 4:
			speak_img.setImageResource(R.drawable.mv);
			break;
		case 5:
			speak_img.setImageResource(R.drawable.mw);
			break;
		case 6:
			speak_img.setImageResource(R.drawable.mx);
			break;
		case 7:
			speak_img.setImageResource(R.drawable.my);
			break;

		}
	}

	public void onConfigurationChanged(Configuration configuration) {
		super.onConfigurationChanged(configuration);

		LcLog.i(TAG, "-->onConfigurationChanged. configuration:"
				+ configuration);
		Configuration configuration1 = getResources().getConfiguration();
		if (configuration1.orientation == 2) {

			setupViewInLandscapeLayout();

		} else if (configuration1.orientation == 1) {

			setupViewInPortraitLayout();

		}
	}

	private void setupViewInLandscapeLayout() {
		// 横屏时，显示
		LcLog.i(TAG, "-->setupViewInLandscapeLayout.");

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		LcLog.i(TAG, "LandscapeLayout: widthPixels:" + dm.widthPixels
				+ ",heightPixels:" + dm.heightPixels);

		LayoutParams lParams = (LayoutParams) mLayoutFunction.getLayoutParams();
		lParams.topMargin = mScreenHeight
				- DensityUtil.dip2px(this, FUNCTION_BUTTON_HEIGHT) - 20;
		mLayoutFunction.setLayoutParams(lParams);

		mLayoutMonitorPixels.setVisibility(View.INVISIBLE);
		mLayoutTitlebar.setVisibility(View.INVISIBLE);
		mLayoutFunction.setVisibility(View.INVISIBLE);
	}

	private void setupViewInPortraitLayout() {
		// 竖屏时，显示
		LcLog.i(TAG, "-->setupViewInPortraitLayout.");

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		LcLog.i(TAG, "PortraitLayout: widthPixels:" + dm.widthPixels
				+ ",heightPixels:" + dm.heightPixels);

		LayoutParams lParams = (LayoutParams) mLayoutFunction.getLayoutParams();
		lParams.topMargin = mScreenHeight
				- DensityUtil.dip2px(this, FUNCTION_BUTTON_HEIGHT) - 20;
		mLayoutFunction.setLayoutParams(lParams);

		mLayoutMonitorPixels.setVisibility(View.VISIBLE);
		mLayoutTitlebar.setVisibility(View.VISIBLE);
		mLayoutFunction.setVisibility(View.VISIBLE);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_VIDEO_PIXEL_DONE:
				// 显示图片尺寸
				mTextMonitorPixels.setText(mVideoWidth + " x " + mVideoHeight);
				break;
			case GET_VIDEO_ONLINE_DONE:
				// 显示图片尺寸
				mTextMonitorOnline.setText("在线人数: " + msg.obj);
				break;
			case SHOW_TOAST_MESSAGE:
				// 显示
				showToastMessage(msg.arg1, msg.obj.toString());
				break;
			case MonitorClient.SHOW_SPEAKING_DIALOG_START:
				// 显示
				speakingDialogVisible();
				break;
			case MonitorClient.SHOW_SPEAKING_DIALOG_PROCESS:
				// 音量变动
				speakingDialogProcess(msg.arg1, (byte[]) msg.obj);
				break;
			}

			super.handleMessage(msg);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		LcLog.i(TAG, "[onResume] mMonitorClient:" + mMonitorClient
				+ ",mMonitorView:" + mMonitorView + ",channel:" + mAVChannel);

		mMonitorClient.registerIOTCListener(this);
		if (mMonitorInfo.Status.equals("已联机")) {
			mTextMonitorStatus.setText("已联机");
		} else {
			// 重新连接
			mMonitorClient.stop(mAVChannel);
			mMonitorClient.disconnect();

			// 连接
			mMonitorClient.connect(mMonitorClient.getUID());
			mMonitorClient.start(mAVChannel, mMonitorInfo.DeviceName,
					mMonitorInfo.DevicePassword);
		}

		if (mMonitorView != null) {
			mMonitorView.attachCamera(mMonitorClient, mAVChannel);
		}

		if (mMonitorClient != null) {
			mMonitorClient.startShow(mAVChannel, true);
			if (mIsListening)
				mMonitorClient.startListening(mAVChannel);
			if (mIsSpeaking)
				mMonitorClient.startSpeaking(mAVChannel, mHandler);
		}
	}

	protected void showToastMessage(int arg, String msg) {
		if (arg == 0) {
			// 连接成功，启动视频连接
			mLayoutFunction.setEnabled(true);

			mTextMonitorStatus.setText("已联机");

			return;
		}

		mTextMonitorStatus.setText("连接失败");
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mMonitorClient != null) {
			if (mIsListening) {
				mIsListening = false;
				mMonitorClient.stopListening(mAVChannel);
			}

			if (mIsSpeaking) {
				mIsSpeaking = false;
				mMonitorClient.stopSpeaking(mAVChannel);
			}

			mMonitorClient.stopShow(mAVChannel);

			mMonitorClient.unregisterIOTCListener(this);
		}

		if (mMonitorView != null) {
			// Deattach this camera.
			mMonitorView.deattachCamera();
		}
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

		// 连接成功，启动视频连接
		sendMessageForToast(0, "");
	}

	@Override
	public void receiveFrameData(MonitorClient paramCamera, int paramInt,
			Bitmap bitmap) {
		if ((bitmap.getWidth() != mVideoWidth || bitmap.getHeight() != mVideoHeight)) {
			mVideoWidth = bitmap.getWidth();
			mVideoHeight = bitmap.getHeight();

			mHandler.sendEmptyMessage(GET_VIDEO_PIXEL_DONE);
		}
	}

	@Override
	public void receiveFrameInfo(MonitorClient paramCamera, int paramInt1,
			long paramLong, int paramInt2, int paramInt3, int paramInt4,
			int paramInt5) {
		// show frame info
		if (mAVChannel == paramInt1) {
			Message msg = Message.obtain();
			msg.what = GET_VIDEO_ONLINE_DONE;
			msg.arg1 = paramInt1;
			msg.obj = paramInt3 + "";

			mHandler.sendMessage(msg);
		}
	}

	@Override
	public void receiveIOCtrlData(MonitorClient paramCamera, int paramInt1,
			int paramInt2, byte[] paramArrayOfByte) {

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
			sendMessageForToast(paramInt, "设备无法识别。");
			return;
		} else if (paramInt != 2) {
			// 其他错误
			sendMessageForToast(paramInt, "设备连接错误。");
			return;
		}
	}

	private void sendMessageForToast(int paramInt, String string) {
		Message msg = Message.obtain();
		msg.what = SHOW_TOAST_MESSAGE;
		msg.arg1 = paramInt;
		msg.obj = string;

		mHandler.sendMessage(msg);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			closeLiveView();
			break;
		case R.id.monitor_snapshot:
			monitorSnapshot();
			break;
		case R.id.monitor_snapshot_modle:
			monitorListen();
			break;
		}
	}

	private void monitorStartSpeaking() {
		mIsSpeaking = true;
		mMonitorClient.startSpeaking(mAVChannel, mHandler);
	}

	private void monitorStopSpeaking() {
		// 已经通话中
		if (mIsSpeaking) {
			// 停止
			mIsSpeaking = false;
			mMonitorClient.stopSpeaking(mAVChannel);
		}

		// 如果监听中，先取消暂停
		if (mIsListening) {
			// 继续监听
			mMonitorClient.pauseListening(mAVChannel);
		}
	}

	private void monitorListen() {
		// 监听
		if (mIsListening) {
			// 停止监听
			mIsListening = false;

			mMonitor_MuTextView.setText("静音");
			mBtnMonitorModle.setImageResource(R.drawable.button_silence);

			mMonitorClient.stopListening(mAVChannel);
		} else {
			// 监听
			mIsListening = true;

			mMonitor_MuTextView.setText("监听中");
			mBtnMonitorModle.setImageResource(R.drawable.button_listen);

			mMonitorClient.startListening(mAVChannel);
		}
	}

	private void monitorSnapshot() {

		// 拍照
		Bitmap bitmap = mMonitorClient.snapshot(mAVChannel);
		// 保存到文件
		if (SaveBitmapFile.saveSnapshot(bitmap) == 0) {
			Toast.makeText(this, "成功拍照", Toast.LENGTH_LONG).show();
		} else if (SaveBitmapFile.saveSnapshot(bitmap) == -1) {
			Toast.makeText(this, "拍照失败，没有安装SD卡。", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "拍照失败，请稍后再试。", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			closeLiveView();
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {

			return false;
		}

		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {

			return false;
		}

		return true;
	}

	private void closeLiveView() {
		// 离开时，保存最后一张图片
		Bitmap bitmap = mMonitorClient.snapshot(mAVChannel);
		if (bitmap != null) {
			String path = this.getCacheDir().getAbsolutePath();
			String filename = LastFrameFileName + mMonitorInfo.UID + ".png";
			LcLog.i(TAG, "[closeLiveView]path:" + path + "," + filename);

			SaveBitmapFile.saveBitmapToFile(path, filename, bitmap);

			mMonitorInfo.Snapshot = path + File.separator + filename;
			MonitorDBManager.updateMonitorSnapshot(this, mMonitorInfo);
		}

		// 关闭时候，直接返回前一页
		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();
	}
}
