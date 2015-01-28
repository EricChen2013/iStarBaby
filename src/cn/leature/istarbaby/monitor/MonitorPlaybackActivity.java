package cn.leature.istarbaby.monitor;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.utils.DensityUtil;
import cn.leature.istarbaby.utils.LcLog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.STimeDay;
import com.tutk.IOTC.Packet;

public class MonitorPlaybackActivity extends Activity implements
		OnClickListener, IRegisterMonitorListener {
	private final String TAG = "MonitorPlaybackActivity";

	protected static final int SHOW_TOAST_MESSAGE = 99;
	protected static final int GET_VIDEO_PIXEL_DONE = 1001;
	protected static final int GET_VIDEO_ONLINE_DONE = 1002;
	protected static final int CONNECT_FOR_PLAYBACK = 1003;

	protected static final int FUNCTION_BUTTON_HEIGHT = 40;

	// TitleBar的控件
	private LinearLayout mLayoutTitlebar;
	private TextView mTitleBarTitle;
	private ImageButton mTitleBarCancel, mTitleBarDone;

	private int mScreenWidth, mScreenHeight;

	private TextView mTextMonitorStatus, mTextMonitorPixels,
			mTextMonitorOnline;
	private LinearLayout mLayoutMonitorPixels;
	private MonitorSurfaceView mMonitorView;

	private LinearLayout mLayoutFunction;
	private ImageView mBtnMonitorPause;
	private TextView mMonitor_MuTextView;

	private Intent mIntent;
	private Bundle mBundle;
	private MonitorInfo mMonitorInfo;
	private byte[] mSTimeDayByte = new byte[8];
	private AVIOCTRLDEFs.STimeDay mStTimeDay;
	private String mEventTypeString;

	private boolean mIsPlaying = false;

	private int mAVChannel = -1;
	private int mChannelPlayback = -1;

	private MonitorShareModel mShareMonitor = null;
	private MyMonitor mMonitorClient = null;

	private int mVideoWidth = 0;
	private int mVideoHeight = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_monitor_playback);

		mIntent = this.getIntent();
		mBundle = mIntent.getExtras();
		mMonitorInfo = (MonitorInfo) mBundle
				.getSerializable("monitor_selected");
		int[] mSTimeDayBuf = mBundle.getIntArray("event_stime_selected");
		LcLog.i(TAG, "-->onCreate. mSAvEvent:" + mSTimeDayBuf[0] + "/"
				+ mSTimeDayBuf[1] + "/" + mSTimeDayBuf[2] + " "
				+ mSTimeDayBuf[3] + " " + mSTimeDayBuf[4] + ":"
				+ mSTimeDayBuf[5] + ":" + mSTimeDayBuf[6]);

		mSTimeDayByte = STimeDay.parseContent(mSTimeDayBuf[0], mSTimeDayBuf[1],
				mSTimeDayBuf[2], mSTimeDayBuf[3], mSTimeDayBuf[4],
				mSTimeDayBuf[5], mSTimeDayBuf[6]);
		mStTimeDay = new AVIOCTRLDEFs.STimeDay(mSTimeDayByte);
		mEventTypeString = mBundle.getString("event_type_selected", "");

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
		mTitleBarTitle.setText("影像回放：" + mMonitorInfo.NickName);

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

		// 暂停
		mBtnMonitorPause = (ImageView) this.findViewById(R.id.monitor_pause);
		mBtnMonitorPause.setOnClickListener(this);
		mMonitor_MuTextView = (TextView) this
				.findViewById(R.id.monitor_mutextview);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			closeLiveView();
			break;
		case R.id.monitor_pause:
			monitorPause();
			break;
		}
	}

	private void closeLiveView() {
		// 关闭时候，直接返回前一页
		this.setResult(Activity.RESULT_OK, mIntent);
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			closeLiveView();
		}

		return true;
	}

	private void monitorPause() {
		if (mChannelPlayback < 0) {
			return;
		}

		if (mIsPlaying) {
			// 播放中
			mIsPlaying = false;

			mBtnMonitorPause.setImageResource(R.drawable.button_pause);
			mMonitor_MuTextView.setText("播放中");
		} else {
			// 暂停中
			mIsPlaying = true;

			mBtnMonitorPause.setImageResource(R.drawable.button_play);
			mMonitor_MuTextView.setText("已暂停");
		}

		requestRecordPlay(AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_PAUSE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		LcLog.i(TAG, "onResume: status:" + mMonitorInfo.Status + ",uid:"
				+ mMonitorInfo.UID);
		mTextMonitorStatus.setText(mEventTypeString + " ("
				+ mStTimeDay.getLocalTime() + ")");

		mMonitorClient.registerIOTCListener(this);
		if (mMonitorInfo.Status.equals("已联机")) {
			// mTextMonitorStatus.setText("已联机");

			mHandler.sendEmptyMessage(CONNECT_FOR_PLAYBACK);
		} else {
			// 重新连接
			mMonitorClient.stop(mAVChannel);
			mMonitorClient.disconnect();

			// 连接
			mMonitorClient.connect(mMonitorClient.getUID());
			mMonitorClient.start(mAVChannel, mMonitorInfo.DeviceName,
					mMonitorInfo.DevicePassword);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (mMonitorClient != null) {

			if (mChannelPlayback >= 0) {
				endPlayback();
			}

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
		if ((paramCamera == mMonitorClient) && (paramInt1 == mAVChannel)) {
			// 回放频道
			sendMessageForToast(0, "");
		}
	}

	@Override
	public void receiveFrameData(MonitorClient paramCamera, int paramInt,
			Bitmap bitmap) {
		LcLog.i(TAG, "[receiveFrameData] start channel:" + paramInt);
		if ((mMonitorClient == paramCamera && paramInt == mChannelPlayback && bitmap != null)
				&& (bitmap.getWidth() != mVideoWidth || bitmap.getHeight() != mVideoHeight)) {
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
		if (mChannelPlayback == paramInt1) {
			Message msg = Message.obtain();
			msg.what = GET_VIDEO_ONLINE_DONE;
			msg.arg1 = paramInt1;
			msg.obj = paramInt3 + "";

			mHandler.sendMessage(msg);
		}
	}

	@Override
	public void receiveIOCtrlData(MonitorClient paramCamera, int channel,
			int ioCtrlType, byte[] paramArrayOfByte) {
		if (mMonitorClient == paramCamera) {
			switch (ioCtrlType) {
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP:
				int command = Packet.byteArrayToInt_Little(paramArrayOfByte, 0);
				mChannelPlayback = Packet.byteArrayToInt_Little(
						paramArrayOfByte, 4);
				LcLog.i(TAG, "[receiveIOCtrlData] start command:" + command
						+ ",avChannel:" + mChannelPlayback);
				if (mChannelPlayback < 0) {
					mChannelPlayback = -1;
					break;
				}

				if (command == AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START) {
					startPlayback();
				} else if ((command == AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_STOP)
						|| (command == AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_END)) {
					sendMessageForToast(99, "影视播放结束。");

					endPlayback();
				}

				break;
			}
		}
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

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_VIDEO_PIXEL_DONE:
				// 显示图片尺寸
				mTextMonitorPixels.setText(mVideoWidth + " x " + mVideoHeight);
				mMonitor_MuTextView.setText("播放中");
				break;
			case GET_VIDEO_ONLINE_DONE:
				// 显示在线人数
				mTextMonitorOnline.setText("在线人数: " + msg.obj);
				break;
			case SHOW_TOAST_MESSAGE:
				// 显示
				showToastMessage(msg.arg1, msg.obj.toString());
				break;
			case CONNECT_FOR_PLAYBACK:
				// 准备回放
				requestRecordPlay(AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START);
				break;
			}

			super.handleMessage(msg);
		}
	};

	protected void showToastMessage(int arg, String msg) {
		if (arg == 0) {
			// 连接成功，启动视频连接
			mLayoutFunction.setEnabled(true);

			// mTextMonitorStatus.setText("已联机");

			mHandler.sendEmptyMessage(CONNECT_FOR_PLAYBACK);

			return;
		}

		if (arg == 99) {
			// 播放结束
			mMonitor_MuTextView.setText("已结束");
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

			return;
		}

		// mTextMonitorStatus.setText("连接失败");
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	private void requestRecordPlay(int command) {
		LcLog.i(TAG, "[requestRecordPlay] start channel:" + mAVChannel
				+ ",command:" + command);
		// 影视回放
		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
				AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord.parseContent(mAVChannel,
						command, 0, mSTimeDayByte));
	}

	private void startPlayback() {
		if (mMonitorView != null) {
			mMonitorView.attachCamera(mMonitorClient, mChannelPlayback);
		}

		mMonitorClient.startPlayback(mChannelPlayback, mMonitorInfo.DeviceName,
				mMonitorInfo.DevicePassword);

		mMonitorClient.startShow(mChannelPlayback, false);
		mMonitorClient.startListening(mChannelPlayback);
	}

	private void endPlayback() {
		mMonitorClient.stopShow(mChannelPlayback);
		mMonitorClient.stopListening(mChannelPlayback);

		mMonitorClient.stop(mChannelPlayback);

		// 结束回放
		requestRecordPlay(AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_END);

		mChannelPlayback = -1;
	}
}
