package cn.leature.istarbaby.monitor;

import android.graphics.Bitmap;

public class AVChannel {
	public volatile int mChannel = -1;
	public volatile int mAVIndex = -1;
	private long mServiceType = -1L;
	private String mViewAcc;
	private String mViewPwd;
	private int mAudioCodec;

	public IOCtrlQueue IOCtrlQueue;
	public AVFrameQueue VideoFrameQueue;

	public Bitmap LastFrame;
	public int VideoFPS;
	public int VideoBPS;
	public int AudioBPS;
	public int flowInfoInterval;
	public int mnAVCodecCtxIdx_h264 = -1;

	public MonitorClient.ThreadStartDev threadStartDev = null;
	public MonitorClient.ThreadRecvIOCtrl threadRecvIOCtrl = null;
	public MonitorClient.ThreadSendIOCtrl threadSendIOCtrl = null;

	public MonitorClient.ThreadRecvVideo2 threadRecvVideo = null;
	public MonitorClient.ThreadDecodeVideo2 threadDecVideo = null;

	public MonitorClient.ThreadRecvAudio threadRecvAudio = null;

	public AVChannel(int channel, String view_acc, String view_pwd) {
		this.mChannel = channel;
		this.mViewAcc = view_acc;
		this.mViewPwd = view_pwd;
		this.mServiceType = -1L;

		this.VideoFPS = (this.VideoBPS = this.AudioBPS = this.flowInfoInterval = 0);

		this.LastFrame = null;

		this.IOCtrlQueue = new IOCtrlQueue();
		this.VideoFrameQueue = new AVFrameQueue();
	}

	public int getChannel() {
		return this.mChannel;
	}

	public synchronized int getAVIndex() {
		return this.mAVIndex;
	}

	public synchronized void setAVIndex(int idx) {
		this.mAVIndex = idx;
	}

	public synchronized long getServiceType() {
		return this.mServiceType;
	}

	public synchronized int getAudioCodec() {
		return this.mAudioCodec;
	}

	public synchronized void setAudioCodec(int codec) {
		this.mAudioCodec = codec;
	}

	public synchronized void setServiceType(long serviceType) {
		this.mServiceType = serviceType;
//		this.mAudioCodec = ((serviceType & 0x1000) == 0L ? 141 : 139);
		this.mAudioCodec = 139;
	}

	public String getViewAcc() {
		return this.mViewAcc;
	}

	public String getViewPwd() {
		return this.mViewPwd;
	}
}
