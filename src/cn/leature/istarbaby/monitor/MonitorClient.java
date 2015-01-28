package cn.leature.istarbaby.monitor;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.decoder.util.DecADPCM;
import com.decoder.util.DecH264;
import com.encoder.util.EncADPCM;
import com.tutk.IOTC.AVAPIs;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.Packet;
import com.tutk.IOTC.St_SInfo;
import com.tutk.IOTC.st_LanSearchInfo;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import cn.leature.istarbaby.utils.LcLog;

public class MonitorClient {
	private final String TAG = "MonitorClient";

	public static final int SHOW_SPEAKING_DIALOG_START = 10000;
	public static final int SHOW_SPEAKING_DIALOG_PROCESS = 10001;

	private static volatile int mCameraCount = 0;
	private static int mDefaultMaxCameraLimit = 4;
	private static long mAVClientStartTimeOut = 30L;

	private final Object mWaitObjectForConnected = new Object();

	private ThreadConnectDev mThreadConnectDev = null;
	private ThreadCheckDevStatus mThreadChkDevStatus = null;
	private ThreadSendAudio mThreadSendAudio = null;

	private volatile int nGet_SID = -1;
	private volatile int mSID = -1;
	private volatile int mSessionMode = -1;
	private volatile int[] bResend = new int[1];
	private volatile int nRecvFrmPreSec;
	private volatile int nDispFrmPreSec;

	private boolean mInitAudio = false;
	private AudioTrack mAudioTrack = null;
	private int mCamIndex = 0;

	public boolean mEnableDither = true;
	private String mDevUID;
	private String mDevPwd;
	public static int nFlow_total_FPS_count = 0;
	public static int nFlow_total_FPS_count_noClear = 0;

	private List<IRegisterMonitorListener> mIOTCListeners = Collections
			.synchronizedList(new Vector());
	protected List<AVChannel> mAVChannels = Collections
			.synchronizedList(new Vector());

	public MonitorClient() {
		super();
	}

	static String getHex(byte[] raw, int size) {
		if (raw == null) {
			return null;
		}

		StringBuilder hex = new StringBuilder(2 * raw.length);

		int len = 0;

		byte[] arrayOfByte = raw;
		int j = raw.length;
		for (int i = 0; i < j; i++) {
			byte b = arrayOfByte[i];
			hex.append("0123456789ABCDEF".charAt((b & 0xF0) >> 4))
					.append("0123456789ABCDEF".charAt(b & 0xF)).append(" ");

			len++;
			if (len >= size) {
				break;
			}
		}
		return hex.toString();
	}

	public Bitmap snapshot(int avChannel) {
		Bitmap result = null;
		synchronized (mAVChannels) {
			for (int i = 0; i < mAVChannels.size(); i++) {
				AVChannel ch = (AVChannel) mAVChannels.get(i);
				if (avChannel != ch.getChannel())
					continue;
				result = ch.LastFrame;
				break;
			}

		}

		return result;
	}

	public boolean isSessionConnected() {
		return mSID >= 0;
	}

	public boolean isChannelConnected(int avChannel) {
		boolean result = false;
		synchronized (mAVChannels) {
			for (Iterator iterator = mAVChannels.iterator(); iterator.hasNext();) {
				AVChannel ch = (AVChannel) iterator.next();
				if (avChannel == ch.getChannel()) {
					result = mSID >= 0 && ch.getAVIndex() >= 0;
					break;
				}
			}

		}
		return result;
	}

	public void sendIOCtrl(int avChannel, int type, byte[] data) {
		synchronized (this.mAVChannels) {
			for (AVChannel ch : this.mAVChannels)
				if (avChannel == ch.getChannel())
					ch.IOCtrlQueue.Enqueue(type, data);
		}
	}

	private synchronized boolean audioDev_init(int sampleRateInHz, int channel,
			int dataBit, int codec_id) {
		if (!this.mInitAudio) {
			int channelConfig = 2;
			int audioFormat = 2;
			int mMinBufSize = 0;

			channelConfig = channel == 1 ? 3 : 2;
			audioFormat = dataBit == 1 ? 2 : 3;
			mMinBufSize = AudioTrack.getMinBufferSize(sampleRateInHz,
					channelConfig, audioFormat);

			if ((mMinBufSize == -2) || (mMinBufSize == -1)) {
				return false;
			}
			try {
				this.mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
						sampleRateInHz, channelConfig, audioFormat,
						mMinBufSize, AudioTrack.MODE_STREAM);
				Log.i("IOTCamera",
						"init AudioTrack with SampleRate:"
								+ sampleRateInHz
								+ " "
								+ (dataBit == 1 ? String.valueOf(16) : String
										.valueOf(8)) + "bit "
								+ (channel == 1 ? "Stereo" : "Mono"));
			} catch (IllegalArgumentException iae) {
				iae.printStackTrace();
				return false;
			}

			if ((codec_id == 139) || (codec_id == 140)) {
				DecADPCM.ResetDecoder();
			}

			// this.mAudioTrack.setStereoVolume(1.0F, 1.0F);
			this.mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(),
					AudioTrack.getMaxVolume());

			this.mAudioTrack.play();
			this.mInitAudio = true;

			return true;
		}

		return false;
	}

	private synchronized void audioDev_stop(int codec_id) {
		if (this.mInitAudio) {
			if (this.mAudioTrack != null) {
				this.mAudioTrack.stop();
				this.mAudioTrack.release();
				this.mAudioTrack = null;
			}

			this.mInitAudio = false;
		}
	}

	public static synchronized st_LanSearchInfo[] SearchLAN() {

		int[] num = new int[1];
		st_LanSearchInfo[] result = null;

		result = IOTCAPIs.IOTC_Lan_Search(num, 2000);
		Log.i("IOTCamera", "IOTC_Lan_Search() returns: " + result);

		return result;
	}

	public static synchronized int init() {
		int nRet = 0;

		if (mCameraCount == 0) {
			int port = (int) (10000L + System.currentTimeMillis() % 10000L);

			// nRet = IOTCAPIs.IOTC_Initialize(port, "m1.iotcplatform.com",
			// "m2.iotcplatform.com", "m4.iotcplatform.com",
			// "m5.iotcplatform.com");
			nRet = IOTCAPIs.IOTC_Initialize2(port);

			Log.i("IOTCamera", "IOTC_Initialize2() returns: " + nRet);

			if (nRet < 0) {
				return nRet;
			}

			nRet = AVAPIs.avInitialize(mDefaultMaxCameraLimit * 16);
			Log.i("IOTCamera", "avInitialize() = " + nRet);

			if (nRet < 0) {
				return nRet;
			}
		}

		mCameraCount += 1;
		return nRet;
	}

	public static synchronized int uninit() {
		int nRet = 0;

		if (mCameraCount > 0) {
			mCameraCount -= 1;

			if (mCameraCount == 0) {
				nRet = AVAPIs.avDeInitialize();
				Log.i("IOTCamera", "avDeInitialize() returns: " + nRet);

				nRet = IOTCAPIs.IOTC_DeInitialize();
				Log.i("IOTCamera", "IOTC_DeInitialize() returns: " + nRet);
			}
		}

		return nRet;
	}

	public boolean registerIOTCListener(IRegisterMonitorListener listener) {
		boolean result = false;

		if (!this.mIOTCListeners.contains(listener)) {
			Log.i("IOTCamera", "register IOTC listener:" + listener);
			this.mIOTCListeners.add(listener);
			result = true;
		}

		return result;
	}

	public boolean unregisterIOTCListener(IRegisterMonitorListener listener) {
		boolean result = false;

		if (this.mIOTCListeners.contains(listener)) {
			Log.i("IOTCamera", "unregister IOTC listener");
			this.mIOTCListeners.remove(listener);
			result = true;
		}

		return result;
	}

	public void unregisterAllListener() {

		for (IRegisterMonitorListener listener : this.mIOTCListeners) {
			this.mIOTCListeners.remove(listener);
		}
	}

	public void connect(String uid) {
		this.mDevUID = uid;

		if (this.mThreadConnectDev == null) {
			this.mThreadConnectDev = new ThreadConnectDev(0);
			this.mThreadConnectDev.start();
		}

		if (this.mThreadChkDevStatus == null) {
			this.mThreadChkDevStatus = new ThreadCheckDevStatus();
			this.mThreadChkDevStatus.start();
		}
	}

	public void connect(String uid, String pwd) {
		this.mDevUID = uid;
		this.mDevPwd = pwd;

		if (this.mThreadConnectDev == null) {
			this.mThreadConnectDev = new ThreadConnectDev(1);
			this.mThreadConnectDev.start();
		}

		if (this.mThreadChkDevStatus == null) {
			this.mThreadChkDevStatus = new ThreadCheckDevStatus();
			this.mThreadChkDevStatus.start();
		}
	}

	public void disconnect() {
		synchronized (this.mAVChannels) {
			for (AVChannel ch : this.mAVChannels) {
				stopSpeaking(ch.getChannel());

				if (ch.threadStartDev != null) {
					ch.threadStartDev.stopThread();
				}
				if (ch.threadDecVideo != null) {
					ch.threadDecVideo.stopThread();
				}
				if (ch.threadRecvAudio != null) {
					ch.threadRecvAudio.stopThread();
				}
				if (ch.threadRecvVideo != null) {
					ch.threadRecvVideo.stopThread();
				}
				if (ch.threadRecvIOCtrl != null) {
					ch.threadRecvIOCtrl.stopThread();
				}
				if (ch.threadSendIOCtrl != null) {
					ch.threadSendIOCtrl.stopThread();
				}
				if (ch.threadRecvVideo != null) {
					try {
						ch.threadRecvVideo.interrupt();
						ch.threadRecvVideo.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadRecvVideo = null;
				}

				if (ch.threadRecvAudio != null) {
					try {
						ch.threadRecvAudio.interrupt();
						ch.threadRecvAudio.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadRecvAudio = null;
				}

				if (ch.threadDecVideo != null) {
					try {
						ch.threadDecVideo.interrupt();
						ch.threadDecVideo.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadDecVideo = null;
				}

				if (ch.threadRecvIOCtrl != null) {
					try {
						ch.threadRecvIOCtrl.interrupt();
						ch.threadRecvIOCtrl.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadRecvIOCtrl = null;
				}

				if (ch.threadSendIOCtrl != null) {
					try {
						ch.threadSendIOCtrl.interrupt();
						ch.threadSendIOCtrl.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadSendIOCtrl = null;
				}

				if ((ch.threadStartDev != null)
						&& (ch.threadStartDev.isAlive())) {
					try {
						ch.threadStartDev.interrupt();
						ch.threadStartDev.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				ch.threadStartDev = null;

				ch.VideoFrameQueue.removeAll();
				ch.VideoFrameQueue = null;

				ch.IOCtrlQueue.removeAll();
				ch.IOCtrlQueue = null;

				if (ch.getAVIndex() < 0)
					continue;
				AVAPIs.avClientStop(ch.getAVIndex());
				Log.i("IOTCamera", "avClientStop(avIndex = " + ch.getAVIndex()
						+ ")");
			}

		}

		this.mAVChannels.clear();

		synchronized (this.mWaitObjectForConnected) {
			this.mWaitObjectForConnected.notify();
		}

		if (this.mThreadChkDevStatus != null) {
			this.mThreadChkDevStatus.stopThread();
		}
		if (this.mThreadConnectDev != null) {
			this.mThreadConnectDev.stopThread();
		}
		if (this.mThreadChkDevStatus != null) {
			try {
				this.mThreadChkDevStatus.interrupt();
				this.mThreadChkDevStatus.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			this.mThreadChkDevStatus = null;
		}

		if ((this.mThreadConnectDev != null)
				&& (this.mThreadConnectDev.isAlive())) {
			try {
				this.mThreadConnectDev.interrupt();
				this.mThreadConnectDev.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.mThreadConnectDev = null;

		if (this.mSID >= 0) {
			IOTCAPIs.IOTC_Session_Close(this.mSID);
			Log.i("IOTCamera", "IOTC_Session_Close(nSID = " + this.mSID + ")");
			this.mSID = -1;
		}

		this.mSessionMode = -1;
	}

	public void start(int avChannel, String viewAccount, String viewPasswd) {
		AVChannel session = null;
		LcLog.i(TAG, "[start] start.");
		synchronized (this.mAVChannels) {
			for (AVChannel ch : this.mAVChannels) {
				if (ch.getChannel() == avChannel) {
					session = ch;
					break;
				}
			}
		}
		LcLog.i(TAG, "[start] start session=" + session);
		if (session == null) {
			AVChannel ch = new AVChannel(avChannel, viewAccount, viewPasswd);
			this.mAVChannels.add(ch);

			ch.threadStartDev = new ThreadStartDev(ch);
			ch.threadStartDev.start();

			ch.threadRecvIOCtrl = new ThreadRecvIOCtrl(ch);
			ch.threadRecvIOCtrl.start();

			ch.threadSendIOCtrl = new ThreadSendIOCtrl(ch);
			ch.threadSendIOCtrl.start();
		} else {
			if (session.threadStartDev == null) {
				session.threadStartDev = new ThreadStartDev(session);
				session.threadStartDev.start();
			}

			if (session.threadRecvIOCtrl == null) {
				session.threadRecvIOCtrl = new ThreadRecvIOCtrl(session);
				session.threadRecvIOCtrl.start();
			}

			if (session.threadSendIOCtrl == null) {
				session.threadSendIOCtrl = new ThreadSendIOCtrl(session);
				session.threadSendIOCtrl.start();
			}
		}
	}

	public void stop(int avChannel) {
		synchronized (this.mAVChannels) {
			int idx = -1;

			for (int i = 0; i < this.mAVChannels.size(); i++) {
				AVChannel ch = (AVChannel) this.mAVChannels.get(i);

				if (ch.getChannel() != avChannel)
					continue;
				idx = i;

				stopSpeaking(ch.getChannel());

				if (ch.threadStartDev != null) {
					ch.threadStartDev.stopThread();
				}
				if (ch.threadDecVideo != null) {
					ch.threadDecVideo.stopThread();
				}
				if (ch.threadRecvAudio != null) {
					ch.threadRecvAudio.stopThread();
				}
				if (ch.threadRecvVideo != null) {
					ch.threadRecvVideo.stopThread();
				}
				if (ch.threadRecvIOCtrl != null) {
					ch.threadRecvIOCtrl.stopThread();
				}
				if (ch.threadSendIOCtrl != null) {
					ch.threadSendIOCtrl.stopThread();
				}

				if (ch.threadRecvVideo != null) {
					try {
						ch.threadRecvVideo.interrupt();
						ch.threadRecvVideo.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadRecvVideo = null;
				}

				if (ch.threadRecvAudio != null) {
					try {
						ch.threadRecvAudio.interrupt();
						ch.threadRecvAudio.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadRecvAudio = null;
				}

				if (ch.threadDecVideo != null) {
					try {
						ch.threadDecVideo.interrupt();
						ch.threadDecVideo.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadDecVideo = null;
				}

				if (ch.threadRecvIOCtrl != null) {
					try {
						ch.threadRecvIOCtrl.interrupt();
						ch.threadRecvIOCtrl.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadRecvIOCtrl = null;
				}

				if (ch.threadSendIOCtrl != null) {
					try {
						ch.threadSendIOCtrl.interrupt();
						ch.threadSendIOCtrl.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadSendIOCtrl = null;
				}

				if ((ch.threadStartDev != null)
						&& (ch.threadStartDev.isAlive())) {
					try {
						ch.threadStartDev.interrupt();
						ch.threadStartDev.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				ch.threadStartDev = null;

				ch.VideoFrameQueue.removeAll();
				ch.VideoFrameQueue = null;

				ch.IOCtrlQueue.removeAll();
				ch.IOCtrlQueue = null;

				if (ch.getAVIndex() < 0)
					break;

				AVAPIs.avClientStop(ch.getAVIndex());
				Log.i("IOTCamera", "avClientStop(avIndex = " + ch.getAVIndex()
						+ ")");

				break;
			}

			if (idx >= 0)
				this.mAVChannels.remove(idx);
		}
	}

	public void startShow(int avChannel, boolean avNoClearBuf) {
		synchronized (this.mAVChannels) {
			for (int i = 0; i < this.mAVChannels.size(); i++) {
				AVChannel ch = (AVChannel) this.mAVChannels.get(i);

				if (ch.getChannel() != avChannel)
					continue;
				ch.VideoFrameQueue.removeAll();

				if (ch.threadRecvVideo == null) {
					ch.threadRecvVideo = new ThreadRecvVideo2(ch, avNoClearBuf);
					ch.threadRecvVideo.start();
				}

				if (ch.threadDecVideo == null) {
					ch.threadDecVideo = new ThreadDecodeVideo2(ch);
					ch.threadDecVideo.start();
				}

				break;
			}
		}
	}

	public void stopShow(int avChannel) {
		synchronized (this.mAVChannels) {
			for (int i = 0; i < this.mAVChannels.size(); i++) {
				AVChannel ch = (AVChannel) this.mAVChannels.get(i);

				if (ch.getChannel() != avChannel)
					continue;
				if (ch.threadRecvVideo != null) {
					ch.threadRecvVideo.stopThread();
					try {
						ch.threadRecvVideo.interrupt();
						ch.threadRecvVideo.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadRecvVideo = null;
				}

				if (ch.threadDecVideo != null) {
					ch.threadDecVideo.stopThread();
					try {
						ch.threadDecVideo.interrupt();
						ch.threadDecVideo.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadDecVideo = null;
				}

				ch.VideoFrameQueue.removeAll();

				break;
			}
		}
	}

	public void startListening(int avChannel) {
		synchronized (this.mAVChannels) {
			for (int i = 0; i < this.mAVChannels.size(); i++) {
				AVChannel ch = (AVChannel) this.mAVChannels.get(i);

				if (avChannel != ch.getChannel())
					continue;

				if (ch.threadRecvAudio != null)
					break;
				ch.threadRecvAudio = new ThreadRecvAudio(ch);
				ch.threadRecvAudio.start();

				break;
			}
		}
	}

	public void stopListening(int avChannel) {
		synchronized (this.mAVChannels) {
			for (int i = 0; i < this.mAVChannels.size(); i++) {
				AVChannel ch = (AVChannel) this.mAVChannels.get(i);

				if (avChannel != ch.getChannel())
					continue;
				if (ch.threadRecvAudio != null) {
					ch.threadRecvAudio.stopThread();
					try {
						ch.threadRecvAudio.interrupt();
						ch.threadRecvAudio.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ch.threadRecvAudio = null;
				}

				break;
			}
		}
	}

	public void pauseListening(int avChannel) {
		synchronized (this.mAVChannels) {
			for (int i = 0; i < this.mAVChannels.size(); i++) {
				AVChannel ch = (AVChannel) this.mAVChannels.get(i);

				if (avChannel != ch.getChannel())
					continue;

				if (ch.threadRecvAudio != null) {
					ch.threadRecvAudio.pauseThread();
				}

				break;
			}
		}
	}

	public void startSpeaking(int avChannel, Handler handler) {
		synchronized (this.mAVChannels) {
			for (int i = 0; i < this.mAVChannels.size(); i++) {
				AVChannel ch = (AVChannel) this.mAVChannels.get(i);
				if (ch.getChannel() != avChannel)
					continue;

				if (this.mThreadSendAudio != null)
					break;
				this.mThreadSendAudio = new ThreadSendAudio(ch, handler);
				this.mThreadSendAudio.start();
				break;
			}
		}
	}

	public void stopSpeaking(int avChannel) {
		if (this.mThreadSendAudio != null) {
			this.mThreadSendAudio.stopThread();
			try {
				this.mThreadSendAudio.interrupt();
				this.mThreadSendAudio.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			this.mThreadSendAudio = null;
		}
	}

	public class ThreadRecvIOCtrl extends Thread {
		private final int TIME_OUT = 0;
		private boolean bIsRunning = false;
		private AVChannel mAVChannel;

		public ThreadRecvIOCtrl(AVChannel channel) {
			this.mAVChannel = channel;
		}

		public void stopThread() {
			this.bIsRunning = false;
		}

		public void run() {
			this.bIsRunning = true;

			while ((this.bIsRunning)
					&& ((MonitorClient.this.mSID < 0) || (this.mAVChannel
							.getAVIndex() < 0))) {
				try {
					synchronized (MonitorClient.this.mWaitObjectForConnected) {
						MonitorClient.this.mWaitObjectForConnected.wait(1000L);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			while (this.bIsRunning) {
				if ((MonitorClient.this.mSID < 0)
						|| (this.mAVChannel.getAVIndex() < 0))
					continue;
				int[] ioCtrlType = new int[1];
				byte[] ioCtrlBuf = new byte[1024];

				int nRet = AVAPIs.avRecvIOCtrl(this.mAVChannel.getAVIndex(),
						ioCtrlType, ioCtrlBuf, ioCtrlBuf.length, TIME_OUT);

				if (nRet >= 0) {
					Log.i("IOTCamera",
							"avRecvIOCtrl(" + this.mAVChannel.getAVIndex()
									+ ", 0x"
									+ Integer.toHexString(ioCtrlType[0]) + ", "
									+ MonitorClient.getHex(ioCtrlBuf, nRet)
									+ ")");

					byte[] data = new byte[nRet];
					System.arraycopy(ioCtrlBuf, 0, data, 0, nRet);

					if (ioCtrlType[0] == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_RESP) {
						int channel = Packet.byteArrayToInt_Little(data, 0);
						int format = Packet.byteArrayToInt_Little(data, 4);

						for (AVChannel ch : MonitorClient.this.mAVChannels) {
							if (ch.getChannel() == channel) {
								ch.setAudioCodec(format);
								break;
							}
						}
					}

					if (ioCtrlType[0] == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_FLOWINFO_REQ) {
						int channel = Packet.byteArrayToInt_Little(data, 0);
						int collect_interval = Packet.byteArrayToInt_Little(
								data, 4);

						for (AVChannel ch : MonitorClient.this.mAVChannels) {
							if (ch.getChannel() == channel) {
								ch.flowInfoInterval = collect_interval;
								MonitorClient.this
										.sendIOCtrl(
												this.mAVChannel.mChannel,
												AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_FLOWINFO_RESP,
												AVIOCTRLDEFs.SMsgAVIoctrlGetFlowInfoResp
														.parseContent(
																channel,
																ch.flowInfoInterval));

								break;
							}
						}
						Log.i("AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_FLOWINFO_REQ","AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_FLOWINFO_REQ ++");
					}

					for (int i = 0; i < MonitorClient.this.mIOTCListeners
							.size(); i++) {
						IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
								.get(i);
						listener.receiveIOCtrlData(MonitorClient.this,
								this.mAVChannel.getChannel(), ioCtrlType[0],
								data);
					}
				} else {
					try {
						Thread.sleep(100L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}

			Log.i("IOTCamera", "===ThreadRecvIOCtrl exit===");
		}
	}

	public class ThreadSendIOCtrl extends Thread {
		private boolean bIsRunning = false;
		private AVChannel mAVChannel;

		public ThreadSendIOCtrl(AVChannel channel) {
			this.mAVChannel = channel;
		}

		public void stopThread() {
			this.bIsRunning = false;

			if (this.mAVChannel.getAVIndex() >= 0) {
				Log.i("IOTCamera",
						"avSendIOCtrlExit(" + this.mAVChannel.getAVIndex()
								+ ")");
				AVAPIs.avSendIOCtrlExit(this.mAVChannel.getAVIndex());
			}
		}

		public void run() {
			// Log.i("IOTCamera",
			// "avSendIOCtrl avIndex " + this.mAVChannel.getAVIndex()
			// + ",sid:" + MonitorClient.this.mSID);

			this.bIsRunning = true;

			while ((this.bIsRunning)
					&& ((MonitorClient.this.mSID < 0) || (this.mAVChannel
							.getAVIndex() < 0))) {
				try {
					synchronized (MonitorClient.this.mWaitObjectForConnected) {
						MonitorClient.this.mWaitObjectForConnected.wait(1000L);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if ((this.bIsRunning) && (MonitorClient.this.mSID >= 0)
					&& (this.mAVChannel.getAVIndex() >= 0)) {
				int nDelayTime_ms = 0;
				AVAPIs.avSendIOCtrl(this.mAVChannel.getAVIndex(),
						AVAPIs.IOTYPE_INNER_SND_DATA_DELAY,
						Packet.intToByteArray_Little(nDelayTime_ms), 4);
				Log.i("IOTCamera",
						"avSendIOCtrl("
								+ this.mAVChannel.getAVIndex()
								+ ", 0x"
								+ Integer
										.toHexString(AVAPIs.IOTYPE_INNER_SND_DATA_DELAY)
								+ ", "
								+ MonitorClient.getHex(Packet
										.intToByteArray_Little(nDelayTime_ms),
										4) + ")");
			}

			while (this.bIsRunning) {
				// Log.i("IOTCamera",
				// "avSendIOCtrl avIndex " + this.mAVChannel.getAVIndex()
				// + ",ioQueue:" + this.mAVChannel.IOCtrlQueue);
				if ((MonitorClient.this.mSID >= 0)
						&& (this.mAVChannel.getAVIndex() >= 0)
						&& (!this.mAVChannel.IOCtrlQueue.isEmpty())) {
					IOCtrlQueue.IOCtrlSet data = this.mAVChannel.IOCtrlQueue
							.Dequeue();

					if ((!this.bIsRunning) || (data == null))
						continue;
					int ret = AVAPIs.avSendIOCtrl(this.mAVChannel.getAVIndex(),
							data.IOCtrlType, data.IOCtrlBuf,
							data.IOCtrlBuf.length);

					if (ret >= 0)
						Log.i("IOTCamera",
								"avSendIOCtrl("
										+ this.mAVChannel.getAVIndex()
										+ ", 0x"
										+ Integer.toHexString(data.IOCtrlType)
										+ ", "
										+ MonitorClient.getHex(data.IOCtrlBuf,
												data.IOCtrlBuf.length) + ")");
					else
						Log.i("IOTCamera", "avSendIOCtrl failed : " + ret);
				} else {
					try {
						Thread.sleep(50L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			Log.i("IOTCamera", "===ThreadSendIOCtrl exit===");
		}
	}

	public class ThreadStartDev extends Thread {
		private boolean mIsRunning = false;
		private AVChannel mAVChannel;
		private Object mWaitObject = new Object();

		public ThreadStartDev(AVChannel channel) {
			this.mAVChannel = channel;
		}

		public void stopThread() {
			this.mIsRunning = false;

			if (MonitorClient.this.mSID >= 0) {
				Log.i("IOTCamera", "avClientExit(" + MonitorClient.this.mSID
						+ ", " + this.mAVChannel.getChannel() + ")");
				AVAPIs.avClientExit(MonitorClient.this.mSID,
						this.mAVChannel.getChannel());
			}

			synchronized (this.mWaitObject) {
				this.mWaitObject.notify();
			}
		}

		public void run() {
			this.mIsRunning = true;

			int avIndex = -1;

			while (this.mIsRunning) {
				LcLog.i(TAG, "[ThreadStartDev(" + this.toString()
						+ ")] thread run. mSid=" + MonitorClient.this.mSID);
				if (MonitorClient.this.mSID < 0) {
					if ((MonitorClient.this.mSID == -15)
							|| (MonitorClient.this.mSID == -10)
							|| (MonitorClient.this.mSID == -19)
							|| (MonitorClient.this.mSID == -13)) {
						// 未知设备或连接超时
						break;
					}

					try {
						synchronized (MonitorClient.this.mWaitObjectForConnected) {
							MonitorClient.this.mWaitObjectForConnected
									.wait(100L);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					for (int i = 0; i < MonitorClient.this.mIOTCListeners
							.size(); i++) {
						IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
								.get(i);
						listener.receiveChannelInfo(MonitorClient.this,
								this.mAVChannel.getChannel(), 1);
					}

					long[] nServType = new long[1];
					nServType[0] = -1L;

					avIndex = AVAPIs.avClientStart2(MonitorClient.this.mSID,
							this.mAVChannel.getViewAcc(),
							this.mAVChannel.getViewPwd(),
							mAVClientStartTimeOut, nServType,
							this.mAVChannel.getChannel(),
							MonitorClient.this.bResend);
					LcLog.e(TAG, "[ThreadStartDev] thread run. avIndex="
							+ avIndex + ",mSID:" + MonitorClient.this.mSID
							+ ",acc:" + this.mAVChannel.getViewAcc() + ",pwd:"
							+ this.mAVChannel.getViewPwd() + ",channel:"
							+ this.mAVChannel.getChannel());

					long servType = nServType[0];

					if (avIndex >= 0) {
						this.mAVChannel.setAVIndex(avIndex);
						this.mAVChannel.setServiceType(servType);

						for (int i = 0; i < MonitorClient.this.mIOTCListeners
								.size(); i++) {
							IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
									.get(i);
							listener.receiveChannelInfo(MonitorClient.this,
									this.mAVChannel.getChannel(), 2);
						}

						break;
					}
					if ((avIndex == -20016) || (avIndex == -20011)) {
						// AV_ER_REMOTE_TIMEOUT_DISCONNECT -20016
						// AV_ER_TIMEOUT -20011
						for (int i = 0; i < MonitorClient.this.mIOTCListeners
								.size(); i++) {
							IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
									.get(i);
							listener.receiveChannelInfo(MonitorClient.this,
									this.mAVChannel.getChannel(), 6);
						}

					} else {
						if (avIndex == -20009) {
							// AV_ER_WRONG_VIEWACCorPWD -20009
							for (int i = 0; i < MonitorClient.this.mIOTCListeners
									.size(); i++) {
								IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
										.get(i);
								listener.receiveChannelInfo(MonitorClient.this,
										this.mAVChannel.getChannel(), 5);
							}

							break;
						}

						try {
							synchronized (this.mWaitObject) {
								this.mWaitObject.wait(1000L);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}

			Log.i("IOTCamera", "===ThreadStartDev exit===");
		}
	}

	private class ThreadConnectDev extends Thread {
		private int mConnType = -1;
		private boolean mIsRunning = false;
		private Object m_waitForStopConnectThread = new Object();

		public ThreadConnectDev(int connType) {
			this.mConnType = connType;
		}

		public void stopThread() {
			this.mIsRunning = false;

			if (MonitorClient.this.nGet_SID >= 0) {
				IOTCAPIs.IOTC_Connect_Stop_BySID(MonitorClient.this.nGet_SID);
			}
			synchronized (this.m_waitForStopConnectThread) {
				this.m_waitForStopConnectThread.notify();
			}
		}

		public void run() {
			int nRetryForIOTC_Conn = 0;

			this.mIsRunning = true;
			LcLog.i(TAG, "[ThreadConnectDev(" + this.toString()
					+ ")] thread run. mSid=" + MonitorClient.this.mSID
					+ ",mConnType=" + this.mConnType);

			while ((this.mIsRunning) && (MonitorClient.this.mSID < 0)) {
				for (int i = 0; i < MonitorClient.this.mIOTCListeners.size(); i++) {
					IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
							.get(i);
					listener.receiveSessionInfo(MonitorClient.this, 1);
				}

				if (this.mConnType == 0) {
					MonitorClient.this.nGet_SID = IOTCAPIs.IOTC_Get_SessionID();
					Log.i("IOTCamera", "IOTC_Get_SessionID nGet_SID = "
							+ MonitorClient.this.nGet_SID);

					if (MonitorClient.this.nGet_SID >= 0) {
						MonitorClient.this.mSID = IOTCAPIs
								.IOTC_Connect_ByUID_Parallel(
										MonitorClient.this.mDevUID,
										MonitorClient.this.nGet_SID);
						// MonitorClient.this.nGet_SID = -1;
					}
				} else if (this.mConnType == 1) {
					MonitorClient.this.nGet_SID = IOTCAPIs.IOTC_Get_SessionID();
					Log.i("IOTCamera", "IOTC_Get_SessionID nGet_SID = "
							+ MonitorClient.this.nGet_SID);

					if (MonitorClient.this.nGet_SID >= 0) {
						MonitorClient.this.mSID = IOTCAPIs
								.IOTC_Connect_ByUID_Parallel(
										MonitorClient.this.mDevUID,
										MonitorClient.this.nGet_SID);
						// MonitorClient.this.nGet_SID = -1;
					}
				} else {
					break;
				}
				Log.e("IOTCamera", "IOTC_Connect_ByUID_Parallel mSID = "
						+ MonitorClient.this.mSID + ",nGet_SID:"
						+ MonitorClient.this.nGet_SID);

				if (MonitorClient.this.mSID >= 0) {

					for (int i = 0; i < MonitorClient.this.mIOTCListeners
							.size(); i++) {
						IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
								.get(i);
						listener.receiveSessionInfo(MonitorClient.this, 2);
					}

					synchronized (MonitorClient.this.mWaitObjectForConnected) {
						MonitorClient.this.mWaitObjectForConnected.notify();
					}
				} else if ((MonitorClient.this.mSID == -20)
						|| (MonitorClient.this.mSID == -14)) {
					try {
						synchronized (this.m_waitForStopConnectThread) {
							this.m_waitForStopConnectThread.wait(1000L);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if ((MonitorClient.this.mSID == -15)
						|| (MonitorClient.this.mSID == -10)
						|| (MonitorClient.this.mSID == -19)
						|| (MonitorClient.this.mSID == -13)) {
					if (MonitorClient.this.mSID != -13) {
						for (int i = 0; i < MonitorClient.this.mIOTCListeners
								.size(); i++) {
							IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
									.get(i);
							listener.receiveSessionInfo(MonitorClient.this, 4);
						}
						break;
					}

					nRetryForIOTC_Conn++;
					try {
						long sleepTime = nRetryForIOTC_Conn > 60 ? 60000
								: nRetryForIOTC_Conn * 1000;
						synchronized (this.m_waitForStopConnectThread) {
							this.m_waitForStopConnectThread.wait(sleepTime);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					if ((MonitorClient.this.mSID == -36)
							|| (MonitorClient.this.mSID == -37)) {
						for (int i = 0; i < MonitorClient.this.mIOTCListeners
								.size(); i++) {
							IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
									.get(i);
							listener.receiveSessionInfo(MonitorClient.this, 7);
						}

						break;
					}

					for (int i = 0; i < MonitorClient.this.mIOTCListeners
							.size(); i++) {
						IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
								.get(i);
						listener.receiveSessionInfo(MonitorClient.this, 8);
					}

					break;
				}
			}

			Log.i("IOTCamera", "===ThreadConnectDev exit===");
		}
	}

	private class ThreadCheckDevStatus extends Thread {
		private boolean m_bIsRunning = false;
		private Object m_waitObjForCheckDevStatus = new Object();

		private ThreadCheckDevStatus() {
		}

		public void stopThread() {
			this.m_bIsRunning = false;

			synchronized (this.m_waitObjForCheckDevStatus) {
				this.m_waitObjForCheckDevStatus.notify();
			}
		}

		public void run() {
			super.run();

			this.m_bIsRunning = true;
			St_SInfo stSInfo = new St_SInfo();
			int ret = 0;
			do {
				try {
					synchronized (MonitorClient.this.mWaitObjectForConnected) {
						MonitorClient.this.mWaitObjectForConnected.wait(1000L);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (!this.m_bIsRunning)
					break;
			} while (MonitorClient.this.mSID < 0);

			while (this.m_bIsRunning) {
				if (MonitorClient.this.mSID >= 0) {
					ret = IOTCAPIs.IOTC_Session_Check(MonitorClient.this.mSID,
							stSInfo);

					if (ret >= 0) {
						if (MonitorClient.this.mSessionMode != stSInfo.Mode) {
							MonitorClient.this.mSessionMode = stSInfo.Mode;
						}

					} else if ((ret == -23) || (ret == -13)) {
						Log.i("IOTCamera", "IOTC_Session_Check("
								+ MonitorClient.this.mSID + ") timeout");

						for (int i = 0; i < MonitorClient.this.mIOTCListeners
								.size(); i++) {
							IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
									.get(i);
							listener.receiveSessionInfo(MonitorClient.this, 6);
						}

					} else {
						Log.i("IOTCamera", "IOTC_Session_Check("
								+ MonitorClient.this.mSID + ") Failed return "
								+ ret);

						for (int i = 0; i < MonitorClient.this.mIOTCListeners
								.size(); i++) {
							IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
									.get(i);
							listener.receiveSessionInfo(MonitorClient.this, 8);
						}
					}

				}

				synchronized (this.m_waitObjForCheckDevStatus) {
					try {
						this.m_waitObjForCheckDevStatus.wait(5000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

			Log.i("IOTCamera", "===ThreadCheckDevStatus exit===");
		}
	}

	public class ThreadRecvVideo2 extends Thread {
		private static final int MAX_BUF_SIZE = 2764800;
		private boolean bIsRunning = false;
		private AVChannel mAVChannel;
		private boolean avNoClearBuf = false;

		public ThreadRecvVideo2(AVChannel channel, boolean noClearBuf) {
			this.mAVChannel = channel;
			this.avNoClearBuf = noClearBuf;
		}

		public void stopThread() {
			this.bIsRunning = false;
		}

		public void run() {
			System.gc();
			// Log.i("IOTCamera", "ThreadRecvVideo2,111 avIndex:"
			// + this.mAVChannel.getAVIndex());
			this.bIsRunning = true;

			while ((this.bIsRunning)
					&& ((MonitorClient.this.mSID < 0) || (this.mAVChannel
							.getAVIndex() < 0))) {
				try {
					synchronized (MonitorClient.this.mWaitObjectForConnected) {
						MonitorClient.this.mWaitObjectForConnected.wait(100L);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// Log.i("IOTCamera", "ThreadRecvVideo2,2222 avIndex:"
			// + this.mAVChannel.getAVIndex());

			this.mAVChannel.VideoBPS = 0;

			byte[] buf = new byte[MAX_BUF_SIZE];
			byte[] pFrmInfoBuf = new byte[24];

			int[] pFrmNo = new int[1];
			int nCodecId = 0;
			int nReadSize = 0;
			int nFrmCount = 0;
			int nIncompleteFrmCount = 0;
			int nOnlineNumber = 0;
			long nPrevFrmNo = 0L;
			long nLastTimeStamp = System.currentTimeMillis();

			int nFlow_total_frame_count = 0;
			int nFlow_lost_incomplete_frame_count = 0;
			int nFlow_total_expected_frame_size = 0;
			int nFlow_total_actual_frame_size = 0;
			long nFlow_timestamp = System.currentTimeMillis();
			MonitorClient.this.nRecvFrmPreSec = 0;

			int[] outBufSize = new int[1];
			int[] outFrmSize = new int[1];
			int[] outFrmInfoBufSize = new int[1];

			if ((this.bIsRunning) && (MonitorClient.this.mSID >= 0)
					&& (this.mAVChannel.getAVIndex() >= 0)
					&& (this.avNoClearBuf)) {
				AVAPIs.avClientCleanVideoBuf(this.mAVChannel.getAVIndex());
			}

			this.mAVChannel.VideoFrameQueue.removeAll();

			// Log.i("IOTCamera", "ThreadRecvVideo2,333 avIndex:"
			// + this.mAVChannel.getAVIndex());

			if ((this.bIsRunning) && (MonitorClient.this.mSID >= 0)
					&& (this.mAVChannel.getAVIndex() >= 0)) {
				this.mAVChannel.IOCtrlQueue.Enqueue(this.mAVChannel
						.getAVIndex(), AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START,
						AVIOCTRLDEFs.SMsgAVIoctrlAVStream
								.parseContent(MonitorClient.this.mCamIndex));
			}

			// Log.i("IOTCamera", "ThreadRecvVideo2,444 avIndex:"
			// + this.mAVChannel.getAVIndex());
			while (this.bIsRunning) {
				if ((MonitorClient.this.mSID < 0)
						|| (this.mAVChannel.getAVIndex() < 0))
					continue;
				if (System.currentTimeMillis() - nLastTimeStamp > 1000L) {
					nLastTimeStamp = System.currentTimeMillis();

					for (int i = 0; i < MonitorClient.this.mIOTCListeners
							.size(); i++) {
						IRegisterMonitorListener listener = (IRegisterMonitorListener) MonitorClient.this.mIOTCListeners
								.get(i);
						listener.receiveFrameInfo(
								MonitorClient.this,
								this.mAVChannel.getAVIndex(),
								(this.mAVChannel.AudioBPS + this.mAVChannel.VideoBPS) * 8 / 1024,
								this.mAVChannel.VideoFPS, nOnlineNumber,
								nFrmCount, nIncompleteFrmCount);
					}

					this.mAVChannel.VideoFPS = (this.mAVChannel.VideoBPS = this.mAVChannel.AudioBPS = 0);
				}

				if ((this.mAVChannel.flowInfoInterval > 0)
						&& (System.currentTimeMillis() - nFlow_timestamp > this.mAVChannel.flowInfoInterval * 1000)) {
					int elapsedTimeMillis = (int) System.currentTimeMillis();
					MonitorClient.this
							.sendIOCtrl(
									this.mAVChannel.getAVIndex(),
									AVIOCTRLDEFs.IOTYPE_USER_IPCAM_CURRENT_FLOWINFO,
									AVIOCTRLDEFs.SMsgAVIoctrlCurrentFlowInfo.parseContent(
											this.mAVChannel.getAVIndex(),
											nFlow_total_frame_count,
											nFlow_total_frame_count
													- MonitorClient.nFlow_total_FPS_count,
											nFlow_total_expected_frame_size,
											nFlow_total_actual_frame_size,
											elapsedTimeMillis));
					nFlow_total_frame_count = 0;
					nFlow_lost_incomplete_frame_count = 0;
					nFlow_total_expected_frame_size = 0;
					nFlow_total_actual_frame_size = 0;
					MonitorClient.nFlow_total_FPS_count = 0;
					nFlow_timestamp = System.currentTimeMillis();
				}

				nReadSize = AVAPIs.avRecvFrameData2(
						this.mAVChannel.getAVIndex(), buf, buf.length,
						outBufSize, outFrmSize, pFrmInfoBuf,
						pFrmInfoBuf.length, outFrmInfoBufSize, pFrmNo);

				if (nReadSize >= 0) {
					this.mAVChannel.VideoBPS += outBufSize[0];
					nFrmCount++;

					byte[] frameData = new byte[nReadSize];
					System.arraycopy(buf, 0, frameData, 0, nReadSize);

					AVFrame frame = new AVFrame((long) pFrmNo[0], (byte) 0,
							pFrmInfoBuf, frameData, nReadSize);

					nCodecId = frame.getCodecId();
					nOnlineNumber = frame.getOnlineNum();

					if (nCodecId == 78) {
						if ((frame.isIFrame())
								|| (pFrmNo[0] == nPrevFrmNo + 1L)) {
							nPrevFrmNo = pFrmNo[0];
							MonitorClient.this.nRecvFrmPreSec += 1;
							this.mAVChannel.VideoFrameQueue.addLast(frame);
						} else {
							Log.i("IOTCamera", "Incorrect frame no("
									+ pFrmNo[0] + "), prev:" + nPrevFrmNo
									+ " -> drop frame");
						}
					}

					nFlow_total_actual_frame_size += outBufSize[0];
					nFlow_total_expected_frame_size += outFrmSize[0];
					nFlow_total_frame_count++;
				} else if (nReadSize == -20015) {
					Log.i("IOTCamera", "AV_ER_SESSION_CLOSE_BY_REMOTE");
				} else if (nReadSize == -20016) {
					Log.i("IOTCamera", "AV_ER_REMOTE_TIMEOUT_DISCONNECT");
				} else if (nReadSize == -20012) {
					try {
						Thread.sleep(32L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} else {
					if (nReadSize == -20001) {
						continue;
					}
					if (nReadSize == -20003) {
						nFrmCount++;
						nIncompleteFrmCount++;
						nFlow_lost_incomplete_frame_count++;
						nFlow_total_frame_count++;
						Log.i("IOTCamera", "AV_ER_MEM_INSUFF");
					} else if (nReadSize == -20014) {
						Log.i("IOTCamera", "AV_ER_LOSED_THIS_FRAME");

						nFrmCount++;
						nIncompleteFrmCount++;
						nFlow_lost_incomplete_frame_count++;
						nFlow_total_frame_count++;
					} else {
						if (nReadSize != -20013)
							continue;
						nFlow_total_actual_frame_size += outBufSize[0];
						nFlow_total_expected_frame_size += outFrmSize[0];
						nFrmCount++;
						nFlow_total_frame_count++;
						this.mAVChannel.VideoBPS += outBufSize[0];

						if ((outFrmInfoBufSize[0] == 0)
								|| (outFrmSize[0] != outBufSize[0])
								|| (pFrmInfoBuf[2] == 0)) {
							nIncompleteFrmCount++;
							nFlow_lost_incomplete_frame_count++;
							Log.i("IOTCamera",
									(pFrmInfoBuf[2] == 0 ? "P" : "I")
											+ " frame, outFrmSize("
											+ outFrmSize[0] + ") = "
											+ outFrmSize[0] + " > outBufSize("
											+ outBufSize[0] + ")");
						} else {
							byte[] frameData = new byte[outFrmSize[0]];
							System.arraycopy(buf, 0, frameData, 0,
									outFrmSize[0]);
							nCodecId = Packet.byteArrayToShort_Little(
									pFrmInfoBuf, 0);

							if ((nCodecId == 79) || (nCodecId == 76)) {
								nIncompleteFrmCount++;
								nFlow_lost_incomplete_frame_count++;
							} else if (nCodecId == 78) {
								if ((outFrmInfoBufSize[0] == 0)
										|| (outFrmSize[0] != outBufSize[0])
										|| (pFrmInfoBuf[2] == 0)) {
									nIncompleteFrmCount++;
									Log.i("IOTCamera",
											(pFrmInfoBuf[2] == 0 ? "P" : "I")
													+ " frame, outFrmSize("
													+ outFrmSize[0] + ") = "
													+ outFrmSize[0]
													+ " > outBufSize("
													+ outBufSize[0] + ")");
								} else {
									AVFrame frame = new AVFrame(
											(long) pFrmNo[0], (byte) 0,
											pFrmInfoBuf, frameData,
											outFrmSize[0]);

									if ((frame.isIFrame())
											|| (pFrmNo[0] == nPrevFrmNo + 1L)) {
										nPrevFrmNo = pFrmNo[0];
										MonitorClient.this.nRecvFrmPreSec += 1;
										this.mAVChannel.VideoFrameQueue
												.addLast(frame);
										nFlow_total_actual_frame_size += outBufSize[0];
										nFlow_total_expected_frame_size += outFrmSize[0];

										Log.i("IOTCamera",
												"AV_ER_INCOMPLETE_FRAME - H264 or MPEG4");
									} else {
										nIncompleteFrmCount++;
										nFlow_lost_incomplete_frame_count++;
										Log.i("IOTCamera",
												"AV_ER_INCOMPLETE_FRAME - H264 or MPEG4 - LOST");
									}
								}
							} else {
								nIncompleteFrmCount++;
								nFlow_lost_incomplete_frame_count++;
							}
						}

					}

				}

			}

			this.mAVChannel.VideoFrameQueue.removeAll();
			if ((MonitorClient.this.mSID >= 0)
					&& (this.mAVChannel.getAVIndex() >= 0)) {
				this.mAVChannel.IOCtrlQueue.Enqueue(this.mAVChannel
						.getAVIndex(), AVIOCTRLDEFs.IOTYPE_USER_IPCAM_STOP,
						AVIOCTRLDEFs.SMsgAVIoctrlAVStream
								.parseContent(MonitorClient.this.mCamIndex));
			}

			buf = null;

			Log.i("IOTCamera", "===ThreadRecvVideo exit===");
		}
	}

	public class ThreadDecodeVideo2 extends Thread {

		static final int MAX_FRAMEBUF = 0x384000;
		private boolean m_bIsRunning;
		private AVChannel mAVChannel;

		public ThreadDecodeVideo2(AVChannel channel) {
			super();
			m_bIsRunning = false;
			mAVChannel = channel;
		}

		public void stopThread() {
			m_bIsRunning = false;
		}

		public void run() {
			System.gc();

			int avFrameSize = 0;
			AVFrame avFrame = null;
			int videoWidth = 0;
			int videoHeight = 0;
			long firstTimeStampFromDevice = 0L;
			long firstTimeStampFromLocal = 0L;
			long sleepTime = 0L;
			long t1 = 0L;
			long t2 = 0L;

			nDispFrmPreSec = 0;
			long lastUpdateDispFrmPreSec = 0L;
			long lastFrameTimeStamp = 0L;
			long delayTime = 0L;
			int framePara[] = new int[4];
			byte bufOut[] = new byte[0x384000];
			ByteBuffer bytBuffer = null;
			Bitmap bmp = null;
			int out_width[] = new int[1];
			int out_height[] = new int[1];
			int out_size[] = new int[1];
			boolean bInitH264 = false;
			mAVChannel.VideoFPS = 0;
			m_bIsRunning = true;
			System.gc();

			boolean bSkipThisRound = false;
			while (m_bIsRunning) {
				avFrame = null;
				if (mAVChannel.VideoFrameQueue.getCount() > 0) {
					avFrame = mAVChannel.VideoFrameQueue.removeHead();
					if (avFrame == null)
						continue;
					avFrameSize = avFrame.getFrmSize();
				} else {
					try {
						Thread.sleep(4L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				if (!avFrame.isIFrame() && delayTime > 2000L) {
					long skipTime = (long) avFrame.getTimeStamp()
							- lastFrameTimeStamp;
					Log.i("IOTCamera",
							(new StringBuilder(
									"case 1. low decode performance, drop "))
									.append(avFrame.isIFrame() ? "I" : "P")
									.append(" frame, skip time: ")
									.append((long) avFrame.getTimeStamp()
											- lastFrameTimeStamp)
									.append(", total skip: ").append(skipTime)
									.toString());
					lastFrameTimeStamp = avFrame.getTimeStamp();
					delayTime -= skipTime;
					bSkipThisRound = true;
				} else if (!avFrame.isIFrame() && bSkipThisRound) {
					long skipTime = (long) avFrame.getTimeStamp()
							- lastFrameTimeStamp;
					Log.i("IOTCamera",
							(new StringBuilder(
									"case 2. low decode performance, drop "))
									.append(avFrame.isIFrame() ? "I" : "P")
									.append(" frame, skip time: ")
									.append((long) avFrame.getTimeStamp()
											- lastFrameTimeStamp)
									.append(", total skip: ").append(skipTime)
									.toString());
					lastFrameTimeStamp = avFrame.getTimeStamp();
					delayTime -= skipTime;
				} else {
					if (avFrameSize > 0) {
						out_size[0] = 0;
						out_width[0] = 0;
						out_height[0] = 0;
						bSkipThisRound = false;
						LcLog.i("IOTCamera",
								(new StringBuilder("decode frame: ")).append(
										avFrame.isIFrame() ? "I" : "P")
										.toString());
						if (avFrame.getCodecId() == 78) {
							if (!bInitH264) {
								int nAVCodecCtxIdx[] = new int[1];
								int nResult = -1;
								try {
									synchronized (mWaitObjectForConnected) {
										nResult = DecH264
												.InitDecoderV2(nAVCodecCtxIdx);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								if (nResult < 0) {
									mAVChannel.mnAVCodecCtxIdx_h264 = -1;
									LcLog.e("IOTCamera", (new StringBuilder(
											"InitDecoderV2 failed! ErrCode:"))
											.append(nResult).append("L")
											.toString());
								} else {
									mAVChannel.mnAVCodecCtxIdx_h264 = nAVCodecCtxIdx[0];
									LcLog.d("MultiView",
											(new StringBuilder("["))
													.append(mAVChannel.mAVIndex)
													.append("] InitDecoderV2 idx:")
													.append(mAVChannel.mnAVCodecCtxIdx_h264)
													.append(" retVal:")
													.append(nResult)
													.append("L").toString());
								}
								bInitH264 = true;
							}
							if (mAVChannel.mnAVCodecCtxIdx_h264 >= 0)
								DecH264.DecoderNalV2(
										mAVChannel.mnAVCodecCtxIdx_h264,
										avFrame.frmData, avFrameSize,
										framePara, bufOut, mEnableDither);
						}

						if (avFrame.getCodecId() == 78) {
							out_width[0] = framePara[2];
							out_height[0] = framePara[3];
							out_size[0] = out_width[0] * out_height[0] * 2;
						}
						if (out_size[0] > 0 && out_width[0] > 0
								&& out_height[0] > 0) {
							videoWidth = out_width[0];
							videoHeight = out_height[0];
							if (!mEnableDither || avFrame.getCodecId() == 76) {
								bmp = Bitmap.createBitmap(videoWidth,
										videoHeight,
										android.graphics.Bitmap.Config.RGB_565);
							} else {
								bmp = Bitmap
										.createBitmap(
												videoWidth,
												videoHeight,
												android.graphics.Bitmap.Config.ARGB_8888);
							}
							bytBuffer = ByteBuffer.wrap(bufOut);
							bmp.copyPixelsFromBuffer(bytBuffer);
							if (avFrame != null
									&& firstTimeStampFromDevice != 0L
									&& firstTimeStampFromLocal != 0L) {
								long t = System.currentTimeMillis();
								t2 = t - t1;
								sleepTime = (firstTimeStampFromLocal + ((long) avFrame
										.getTimeStamp() - firstTimeStampFromDevice))
										- t;
								delayTime = sleepTime * -1L;
								if (sleepTime >= 0L) {
									if ((long) avFrame.getTimeStamp()
											- lastFrameTimeStamp > 1000L) {
										firstTimeStampFromDevice = avFrame
												.getTimeStamp();
										firstTimeStampFromLocal = t;
										Log.i("IOTCamera",
												"RESET base timestamp");
										if (sleepTime > 1000L)
											sleepTime = 33L;
									}
									if (sleepTime > 1000L)
										sleepTime = 1000L;
									try {
										Thread.sleep(sleepTime);
									} catch (Exception exception) {
									}
								}
								lastFrameTimeStamp = avFrame.getTimeStamp();
							}
							if (firstTimeStampFromDevice == 0L
									|| firstTimeStampFromLocal == 0L) {
								firstTimeStampFromDevice = lastFrameTimeStamp = avFrame
										.getTimeStamp();
								firstTimeStampFromLocal = System
										.currentTimeMillis();
							}
							mAVChannel.VideoFPS++;
							MonitorClient.nFlow_total_FPS_count++;
							MonitorClient.nFlow_total_FPS_count_noClear++;
							nDispFrmPreSec++;
							synchronized (mIOTCListeners) {
								for (int i = 0; i < mIOTCListeners.size(); i++) {
									IRegisterMonitorListener listener = (IRegisterMonitorListener) mIOTCListeners
											.get(i);
									listener.receiveFrameData(
											MonitorClient.this,
											mAVChannel.getChannel(), bmp);
								}

							}
							mAVChannel.LastFrame = bmp;
							long now = System.currentTimeMillis();
							if (now - lastUpdateDispFrmPreSec > 60000L) {
								nDispFrmPreSec = 0;
								nRecvFrmPreSec = 0;
								lastUpdateDispFrmPreSec = now;
							}
						}
					}
					if (avFrame != null) {
						avFrame.frmData = null;
						avFrame = null;
					}
				}
			}
			if (bInitH264 && mAVChannel.mnAVCodecCtxIdx_h264 >= 0)
				try {
					synchronized (mWaitObjectForConnected) {
						DecH264.DeinitDecoderV2(mAVChannel.mnAVCodecCtxIdx_h264);
						LcLog.d("MultiView",
								(new StringBuilder("["))
										.append(mAVChannel.mAVIndex)
										.append("] DeinitDecoderV2( ")
										.append(mAVChannel.mnAVCodecCtxIdx_h264)
										.append("L )").toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			bufOut = null;
			if (bmp != null) {
				bmp.recycle();
				bmp = null;
			}
			System.gc();
			Log.i("IOTCamera", "===ThreadDecodeVideo exit===");
		}

	}

	public class ThreadRecvAudio extends Thread {
		private final int MAX_BUF_SIZE = 1280;
		private int nReadSize = 0;
		private boolean bIsRunning = false;
		private boolean bIsPause = false;
		private AVChannel mAVChannel;

		public ThreadRecvAudio(AVChannel channel) {
			this.mAVChannel = channel;
		}

		public void stopThread() {
			this.bIsRunning = false;
		}

		public void pauseThread() {
			this.bIsPause = !this.bIsPause;
		}

		public void run() {
			this.bIsRunning = true;

			while ((this.bIsRunning)
					&& ((MonitorClient.this.mSID < 0) || (this.mAVChannel
							.getAVIndex() < 0))) {
				try {
					synchronized (MonitorClient.this.mWaitObjectForConnected) {
						MonitorClient.this.mWaitObjectForConnected.wait(100L);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			this.mAVChannel.AudioBPS = 0;
			byte[] recvBuf = new byte[MAX_BUF_SIZE];
			byte[] bytAVFrame = new byte[24];
			int[] pFrmNo = new int[1];

			byte[] adpcmOutBuf = new byte[640];

			boolean bFirst = true;
			boolean bInitAudio = false;

			int nSamplerate = 44100;
			int nDatabits = 1;
			int nChannel = 1;
			int nCodecId = 0;
			int nFPS = 0;

			if ((this.bIsRunning) && (MonitorClient.this.mSID >= 0)
					&& (this.mAVChannel.getAVIndex() >= 0)) {
				AVAPIs.avClientCleanAudioBuf(this.mAVChannel.getAVIndex());
			}

			if ((this.bIsRunning) && (MonitorClient.this.mSID >= 0)
					&& (this.mAVChannel.getAVIndex() >= 0)) {
				this.mAVChannel.IOCtrlQueue.Enqueue(this.mAVChannel
						.getAVIndex(),
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_AUDIOSTART,
						AVIOCTRLDEFs.SMsgAVIoctrlAVStream
								.parseContent(MonitorClient.this.mCamIndex));
			}

			while (this.bIsRunning) {

				this.nReadSize = AVAPIs.avRecvAudioData(
						this.mAVChannel.getAVIndex(), recvBuf, recvBuf.length,
						bytAVFrame, 24, pFrmNo);

				if (bIsPause) {
					// 暂停中，停止接收
					continue;
				}

				if (this.nReadSize > 0) {
					this.mAVChannel.AudioBPS += this.nReadSize;

					byte[] frameData = new byte[this.nReadSize];
					System.arraycopy(recvBuf, 0, frameData, 0, this.nReadSize);

					AVFrame frame = new AVFrame((long) pFrmNo[0], (byte) 0,
							bytAVFrame, frameData, this.nReadSize);

					nCodecId = frame.getCodecId();
					if (bFirst) {
						if ((!MonitorClient.this.mInitAudio)
								&& ((nCodecId == 142) || (nCodecId == 141)
										|| (nCodecId == 139)
										|| (nCodecId == 140)
										|| (nCodecId == 143)
										|| (nCodecId == 138) || (nCodecId == 137))) {
							bFirst = false;

							nSamplerate = AVFrame.getSamplerate(frame
									.getFlags());
							nDatabits = frame.getFlags() & 0x2;
							nDatabits = nDatabits == 2 ? 1 : 0;
							nChannel = frame.getFlags() & 0x1;

							if (nCodecId == 141)
								nFPS = nSamplerate * (nChannel == 0 ? 1 : 2)
										* (nDatabits == 0 ? 8 : 16) / 8 / 160;
							else if (nCodecId == 139)
								nFPS = nSamplerate * (nChannel == 0 ? 1 : 2)
										* (nDatabits == 0 ? 8 : 16) / 8 / 640;
							else if (nCodecId == 140) {
								nFPS = nSamplerate * (nChannel == 0 ? 1 : 2)
										* (nDatabits == 0 ? 8 : 16) / 8
										/ frame.getFrmSize();
							}
							bInitAudio = MonitorClient.this.audioDev_init(
									nSamplerate, nChannel, nDatabits, nCodecId);

							if (!bInitAudio) {
								break;
							}
						}
					}

					if (nCodecId == 139) {
						DecADPCM.Decode(recvBuf, this.nReadSize, adpcmOutBuf);
						MonitorClient.this.mAudioTrack.write(adpcmOutBuf, 0,
								640);
					}
				} else if (this.nReadSize == -20012) {
					try {
						Thread.sleep(500L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if (this.nReadSize == -20014) {
					Log.i("IOTCamera",
							"avRecvAudioData returns AV_ER_LOSED_THIS_FRAME");
				} else {
					try {
						Thread.sleep(nFPS == 0 ? 33 : 1000 / nFPS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Log.i("IOTCamera", "avRecvAudioData returns "
							+ this.nReadSize);
				}
			}

			if (bInitAudio) {
				MonitorClient.this.audioDev_stop(nCodecId);
			}

			this.mAVChannel.IOCtrlQueue.Enqueue(this.mAVChannel.getAVIndex(),
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_AUDIOSTOP,
					AVIOCTRLDEFs.SMsgAVIoctrlAVStream
							.parseContent(MonitorClient.this.mCamIndex));

			Log.i("IOTCamera", "===ThreadRecvAudio exit===");
		}
	}

	public class ThreadSendAudio extends Thread {
		private static final int SAMPLE_RATE_IN_HZ = 8000;

		private boolean m_bIsRunning = false;
		private int avIndexForSendAudio = -1;
		private int chIndexForSendAudio = -1;
		private AVChannel mAVChannel = null;
		private Handler mHandler = null;

		public ThreadSendAudio(AVChannel ch, Handler handler) {
			this.mAVChannel = ch;
			this.mHandler = handler;
		}

		public void stopThread() {
			if ((MonitorClient.this.mSID >= 0)
					&& (this.chIndexForSendAudio >= 0)) {
				AVAPIs.avServExit(MonitorClient.this.mSID,
						this.chIndexForSendAudio);
				MonitorClient.this.sendIOCtrl(this.mAVChannel.getAVIndex(),
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SPEAKERSTOP,
						AVIOCTRLDEFs.SMsgAVIoctrlAVStream
								.parseContent(this.chIndexForSendAudio));
			}

			this.m_bIsRunning = false;
		}

		public void run() {
			System.gc();

			this.m_bIsRunning = true;

			while ((this.m_bIsRunning)
					&& ((MonitorClient.this.mSID < 0) || (this.mAVChannel
							.getAVIndex() < 0))) {
				try {
					synchronized (MonitorClient.this.mWaitObjectForConnected) {
						MonitorClient.this.mWaitObjectForConnected.wait(100L);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			boolean bInitADPCM = false;

			int nMinBufSize = 0;
			int nReadBytes = 0;

			this.chIndexForSendAudio = IOTCAPIs
					.IOTC_Session_Get_Free_Channel(MonitorClient.this.mSID);

			if (this.chIndexForSendAudio < 0) {
				Log.i("IOTCamera",
						"=== ThreadSendAudio exit becuase no more channel for connection ===");
				return;
			}

			MonitorClient.this.sendIOCtrl(this.mAVChannel.getAVIndex(),
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SPEAKERSTART,
					AVIOCTRLDEFs.SMsgAVIoctrlAVStream
							.parseContent(this.chIndexForSendAudio));

			Log.i("IOTCamera", "start avServerStart(" + MonitorClient.this.mSID
					+ ", " + this.chIndexForSendAudio + ")");

			while ((this.m_bIsRunning)
					&& ((this.avIndexForSendAudio = AVAPIs.avServStart(
							MonitorClient.this.mSID, null, null, 60L, 0L,
							this.chIndexForSendAudio)) < 0)) {
				Log.i("IOTCamera", "avServerStart(" + MonitorClient.this.mSID
						+ ", " + this.chIndexForSendAudio + ") : "
						+ this.avIndexForSendAudio);
			}
			Log.i("IOTCamera",
					"avServerStart(" + MonitorClient.this.mSID + ", "
							+ this.chIndexForSendAudio + ") : "
							+ this.avIndexForSendAudio + "codeC:"
							+ this.mAVChannel.getAudioCodec());

			if ((this.m_bIsRunning) && (this.mAVChannel.getAudioCodec() == 139)) {
				EncADPCM.ResetEncoder();
				bInitADPCM = true;

				nMinBufSize = AudioRecord.getMinBufferSize(8000, 16, 2);

				Log.i("IOTCamera", "ADPCM encoder init");
			}

			AudioRecord recorder = null;
			if ((this.m_bIsRunning) && (bInitADPCM)) {
				recorder = new AudioRecord(1, SAMPLE_RATE_IN_HZ, 16, 2,
						nMinBufSize);
				recorder.startRecording();

				// 音量显示
				this.mHandler.sendEmptyMessage(SHOW_SPEAKING_DIALOG_START);
			}

			byte[] inADPCMBuf = new byte[640];
			byte[] outADPCMBuf = new byte[160];
			while (this.m_bIsRunning) {
				if (this.mAVChannel.getAudioCodec() == 139) {
					nReadBytes = recorder
							.read(inADPCMBuf, 0, inADPCMBuf.length);

					if (nReadBytes > 0) {
						// 音量处理
						sendMessageForProcessSpeaking(nReadBytes, inADPCMBuf);

						EncADPCM.Encode(inADPCMBuf, nReadBytes, outADPCMBuf);
						byte flag = 2;
						byte[] frameInfo = AVIOCTRLDEFs.SFrameInfo
								.parseContent((short) 139, flag, (byte) 0,
										(byte) 0,
										(int) System.currentTimeMillis());
						int ret = AVAPIs.avSendAudioData(
								this.avIndexForSendAudio, outADPCMBuf,
								nReadBytes / 4, frameInfo, 16);
					}
				}
			}

			if (recorder != null) {
				recorder.stop();
				recorder.release();
				recorder = null;
			}

			if (this.avIndexForSendAudio >= 0) {
				AVAPIs.avServStop(this.avIndexForSendAudio);
			}

			if (this.chIndexForSendAudio >= 0) {
				IOTCAPIs.IOTC_Session_Channel_OFF(MonitorClient.this.mSID,
						this.chIndexForSendAudio);
			}

			this.avIndexForSendAudio = -1;
			this.chIndexForSendAudio = -1;

			Log.i("IOTCamera", "===ThreadSendAudio exit===");
		}

		private void sendMessageForProcessSpeaking(int length, byte[] outBuf) {
			Message msg = Message.obtain();
			msg.what = SHOW_SPEAKING_DIALOG_PROCESS;
			msg.arg1 = length;
			msg.obj = outBuf;

			this.mHandler.sendMessage(msg);
		}
	}
}
