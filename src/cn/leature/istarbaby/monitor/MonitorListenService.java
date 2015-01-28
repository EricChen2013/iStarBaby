package cn.leature.istarbaby.monitor;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlEvent;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.utils.LcLog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;

public class MonitorListenService extends Service implements
		IRegisterMonitorListener {

	public static final int ONGOING_NOTIFICATION = 1;
	public static final int MOTION_DETECT_NOTIFICATION = 2;

	// private int mStartMode = START_STICKY; // 自动重启
	private int mStartMode = START_NOT_STICKY; // 不自动重启

	private MonitorShareModel mShareMonitor;

	NotificationManager mNotiManager;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		LcLog.i("MonitorListenService", "------>Create");

		mShareMonitor = MonitorShareModel.getInstance();
		if (mShareMonitor != null) {
			// 初始化监护器
			int nRet = MonitorClient.init();
			if (nRet < 0) {
				// 初始化失败
				LcLog.e("MonitorListenService",
						"[onCreate] init camera failed.");
			}
		}

		super.onCreate();
	}

	@Override
	public void onDestroy() {
		mShareMonitor.unregisterIOTCListener(this);
		// 断开连接
		mShareMonitor.disconnect();

		// 结束
		MonitorClient.uninit();

		// 清除所有消息
		mNotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotiManager.cancelAll();

		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LcLog.i("MonitorListenService", "------>onStartCommand");

		if (intent != null) {
			Bundle bundle = intent.getExtras();

			if (bundle != null) {
				String channel = bundle.getString("start_channel", "");
				if (channel.equals("list")) {
					// 启动连接
					(new ThreadCamerasConnect()).start();
				}
			}
		}

		return mStartMode;
	}

	class ThreadCamerasConnect extends Thread {

		public void run() {
			LcLog.i("MonitorListenService", "---->ThreadCamerasConnect start");

			Intent notificationIntent = new Intent(Intent.ACTION_MAIN);
			notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			notificationIntent.setClass(MonitorListenService.this,
					MonitorFragmentActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

			PendingIntent pendingIntent = PendingIntent.getActivity(
					MonitorListenService.this, 0, notificationIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			Notification notification = new Notification(
					R.drawable.icon_notification, "宝宝监护执行中...",
					System.currentTimeMillis());
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notification.setLatestEventInfo(MonitorListenService.this, "宝宝监护",
					"联机中 (" + mShareMonitor.getMonitorLists().size() + ")",
					pendingIntent);

			startForeground(ONGOING_NOTIFICATION, notification);

			mShareMonitor.registerIOTCListener(MonitorListenService.this);
			mShareMonitor.connect();
		}

		public ThreadCamerasConnect() {
			//
		}
	}

	@Override
	public void receiveChannelInfo(MonitorClient paramCamera, int paramInt1,
			int paramInt2) {
		LcLog.i("MonitorListenService", "------>receiveChannelInfo");
	}

	@Override
	public void receiveFrameData(MonitorClient paramCamera, int paramInt,
			Bitmap paramBitmap) {
		LcLog.i("MonitorListenService", "------>receiveFrameData");
	}

	@Override
	public void receiveFrameInfo(MonitorClient paramCamera, int paramInt1,
			long paramLong, int paramInt2, int paramInt3, int paramInt4,
			int paramInt5) {
		LcLog.i("MonitorListenService", "------>receiveFrameInfo");
	}

	@Override
	public void receiveIOCtrlData(MonitorClient paramCamera, int channel,
			int ioCtrlType, byte[] paramArrayOfByte) {
		LcLog.i("MonitorListenService", "------>receiveIOCtrlData");

		if (ioCtrlType == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_EVENT_REPORT) {
			String monitorUid = ((MyMonitor) paramCamera).getUID();
			mShareMonitor.saveCurrentMonitor(monitorUid);

			String userId = LoginInfo.getLoginUserId(this);
			MonitorInfo monitorInfo = MonitorDBManager.loadMonitorInfo(this,
					monitorUid, userId);

			SMsgAVIoctrlEvent event = new SMsgAVIoctrlEvent(paramArrayOfByte);
			String notifMessage = "";
			if (1 == event.event) {
				notifMessage += "动作警告";
			} else {
				notifMessage += "其他";
			}
			notifMessage += ": " + event.stTime.getLocalTimeDefault();

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setClass(MonitorListenService.this,
					MonitorFragmentActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
					| Intent.FLAG_ACTIVITY_NEW_TASK);

			intent.putExtra("notification", true);
			intent.putExtra("monitor_uid", monitorUid);

			PendingIntent ic = PendingIntent.getActivity(
					MonitorListenService.this, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			mNotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Notification notification = new Notification(
					R.drawable.icon_notification, "事件警示",
					System.currentTimeMillis());
			notification.setLatestEventInfo(MonitorListenService.this, "从 "
					+ monitorInfo.NickName + " 接收事件警示", notifMessage, ic);
			notification.flags |= Notification.FLAG_AUTO_CANCEL;// 点击消失
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_VIBRATE;

			mNotiManager.notify(MOTION_DETECT_NOTIFICATION, notification);

			// TipHelper.playBeepSoundAndVibrate(
			// mShareMonitor.getCurrentActivity(), R.raw.beep, true);
		}
	}

	@Override
	public void receiveSessionInfo(MonitorClient paramCamera, int paramInt) {
		LcLog.i("MonitorListenService", "------>receiveSessionInfo");
	}

}
