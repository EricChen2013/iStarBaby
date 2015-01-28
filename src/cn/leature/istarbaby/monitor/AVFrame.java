package cn.leature.istarbaby.monitor;

import com.tutk.IOTC.Packet;

public class AVFrame {

	public static final int FRAMEINFO_SIZE = 24;
	public static final int MEDIA_CODEC_UNKNOWN = 0;
	public static final int MEDIA_CODEC_VIDEO_MPEG4 = 76;
	public static final int MEDIA_CODEC_VIDEO_H263 = 77;
	public static final int MEDIA_CODEC_VIDEO_H264 = 78;
	public static final int MEDIA_CODEC_VIDEO_MJPEG = 79;
	public static final int MEDIA_CODEC_AUDIO_ADPCM = 139;
	public static final int MEDIA_CODEC_AUDIO_PCM = 140;
	public static final int MEDIA_CODEC_AUDIO_SPEEX = 141;
	public static final int MEDIA_CODEC_AUDIO_MP3 = 142;
	public static final int MEDIA_CODEC_AUDIO_G726 = 143;
	public static final int MEDIA_CODEC_AUDIO_G711U = 137;
	public static final int MEDIA_CODEC_AUDIO_G711A = 138;
	public static final int IPC_FRAME_FLAG_PBFRAME = 0;
	public static final int IPC_FRAME_FLAG_IFRAME = 1;
	public static final int IPC_FRAME_FLAG_MD = 2;
	public static final int IPC_FRAME_FLAG_IO = 3;
	public static final int AUDIO_SAMPLE_8K = 0;
	public static final int AUDIO_SAMPLE_11K = 1;
	public static final int AUDIO_SAMPLE_12K = 2;
	public static final int AUDIO_SAMPLE_16K = 3;
	public static final int AUDIO_SAMPLE_22K = 4;
	public static final int AUDIO_SAMPLE_24K = 5;
	public static final int AUDIO_SAMPLE_32K = 6;
	public static final int AUDIO_SAMPLE_44K = 7;
	public static final int AUDIO_SAMPLE_48K = 8;
	public static final int AUDIO_DATABITS_8 = 0;
	public static final int AUDIO_DATABITS_16 = 1;
	public static final int AUDIO_CHANNEL_MONO = 0;
	public static final int AUDIO_CHANNEL_STERO = 1;
	public static final byte FRM_STATE_UNKOWN = -1;
	public static final byte FRM_STATE_COMPLETE = 0;
	public static final byte FRM_STATE_INCOMPLETE = 1;
	public static final byte FRM_STATE_LOSED = 2;
	private short codec_id = 0;

	private byte flags = -1;
	private byte onlineNum = 0;
	private int timestamp = 0;
	private int videoWidth = 0;
	private int videoHeight = 0;

	private long frmNo = -1L;
	private byte frmState = 0;
	private int frmSize = 0;
	public byte[] frmData = null;

	public AVFrame(byte[] frameData, int frameDataSize) {
		this.frmData = frameData;
		this.frmSize = frameDataSize;
	}

	public AVFrame(long frameNo, byte frameState, byte[] frameHead,
			byte[] frameData, int frameDataSize) {
		this.codec_id = Packet.byteArrayToShort_Little(frameHead, 0);
		this.frmState = frameState;
		this.flags = frameHead[2];

		this.onlineNum = frameHead[4];
		this.timestamp = Packet.byteArrayToInt_Little(frameHead, 12);
		this.videoWidth = (frameHead.length > 16 ? Packet
				.byteArrayToInt_Little(frameHead, 16) : 0);
		this.videoHeight = (frameHead.length > 16 ? Packet
				.byteArrayToInt_Little(frameHead, 20) : 0);
		this.frmSize = frameDataSize;
		this.frmData = frameData;
		this.frmNo = frameNo;
	}

	public static int getSamplerate(byte flags) {
		int nSamplerate = 0;

		switch (flags >>> 2) {
		case 0:
			nSamplerate = 8000;
			break;
		case 1:
			nSamplerate = 11025;
			break;
		case 2:
			nSamplerate = 12000;
			break;
		case 3:
			nSamplerate = 16000;
			break;
		case 4:
			nSamplerate = 22050;
			break;
		case 5:
			nSamplerate = 24000;
			break;
		case 6:
			nSamplerate = 32000;
			break;
		case 7:
			nSamplerate = 44100;
			break;
		case 8:
			nSamplerate = 48000;
			break;
		default:
			nSamplerate = 8000;
		}
		return nSamplerate;
	}

	public boolean isIFrame() {
		return (this.flags & 0x1) == 1;
	}

	public byte getFlags() {
		return this.flags;
	}

	public byte getFrmState() {
		return this.frmState;
	}

	public long getFrmNo() {
		return this.frmNo;
	}

	public int getFrmSize() {
		return this.frmSize;
	}

	public void setFrmSize(int frmSize) {
		this.frmSize = frmSize;
	}

	public byte getOnlineNum() {
		return this.onlineNum;
	}

	public short getCodecId() {
		return this.codec_id;
	}

	public int getTimeStamp() {
		return this.timestamp;
	}

	public int getVideoWidth() {
		return this.videoWidth;
	}

	public int getVideoHeight() {
		return this.videoHeight;
	}
}
