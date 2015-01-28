package com.tutk.IOTC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class AVIOCTRLDEFs {
	public static final int AVIOCTRL_AUTO_PAN_LIMIT = 28;
	public static final int AVIOCTRL_AUTO_PAN_SPEED = 27;
	public static final int AVIOCTRL_AUTO_PAN_START = 29;
	public static final int AVIOCTRL_CLEAR_AUX = 34;
	public static final int AVIOCTRL_ENVIRONMENT_INDOOR_50HZ = 0;
	public static final int AVIOCTRL_ENVIRONMENT_INDOOR_60HZ = 1;
	public static final int AVIOCTRL_ENVIRONMENT_NIGHT = 3;
	public static final int AVIOCTRL_ENVIRONMENT_OUTDOOR = 2;
	public static final int AVIOCTRL_EVENT_ALL = 0;
	public static final int AVIOCTRL_EVENT_EXPT_REBOOT = 16;
	public static final int AVIOCTRL_EVENT_IOALARM = 3;
	public static final int AVIOCTRL_EVENT_IOALARMPASS = 6;
	public static final int AVIOCTRL_EVENT_MOTIONDECT = 1;
	public static final int AVIOCTRL_EVENT_MOTIONPASS = 4;
	public static final int AVIOCTRL_EVENT_SDFAULT = 17;
	public static final int AVIOCTRL_EVENT_VIDEOLOST = 2;
	public static final int AVIOCTRL_EVENT_VIDEORESUME = 5;
	public static final int AVIOCTRL_LENS_APERTURE_CLOSE = 22;
	public static final int AVIOCTRL_LENS_APERTURE_OPEN = 21;
	public static final int AVIOCTRL_LENS_FOCAL_FAR = 26;
	public static final int AVIOCTRL_LENS_FOCAL_NEAR = 25;
	public static final int AVIOCTRL_LENS_ZOOM_IN = 23;
	public static final int AVIOCTRL_LENS_ZOOM_OUT = 24;
	public static final int AVIOCTRL_MOTOR_RESET_POSITION = 35;
	public static final int AVIOCTRL_PATTERN_RUN = 32;
	public static final int AVIOCTRL_PATTERN_START = 30;
	public static final int AVIOCTRL_PATTERN_STOP = 31;
	public static final int AVIOCTRL_PTZ_AUTO = 9;
	public static final int AVIOCTRL_PTZ_CLEAR_POINT = 11;
	public static final int AVIOCTRL_PTZ_DOWN = 2;
	public static final int AVIOCTRL_PTZ_FLIP = 19;
	public static final int AVIOCTRL_PTZ_GOTO_POINT = 12;
	public static final int AVIOCTRL_PTZ_LEFT = 3;
	public static final int AVIOCTRL_PTZ_LEFT_DOWN = 5;
	public static final int AVIOCTRL_PTZ_LEFT_UP = 4;
	public static final int AVIOCTRL_PTZ_MENU_ENTER = 18;
	public static final int AVIOCTRL_PTZ_MENU_EXIT = 17;
	public static final int AVIOCTRL_PTZ_MENU_OPEN = 16;
	public static final int AVIOCTRL_PTZ_MODE_RUN = 15;
	public static final int AVIOCTRL_PTZ_RIGHT = 6;
	public static final int AVIOCTRL_PTZ_RIGHT_DOWN = 8;
	public static final int AVIOCTRL_PTZ_RIGHT_UP = 7;
	public static final int AVIOCTRL_PTZ_SET_MODE_START = 13;
	public static final int AVIOCTRL_PTZ_SET_MODE_STOP = 14;
	public static final int AVIOCTRL_PTZ_SET_POINT = 10;
	public static final int AVIOCTRL_PTZ_START = 20;
	public static final int AVIOCTRL_PTZ_STOP = 0;
	public static final int AVIOCTRL_PTZ_UP = 1;
	public static final int AVIOCTRL_QUALITY_HIGH = 2;
	public static final int AVIOCTRL_QUALITY_LOW = 4;
	public static final int AVIOCTRL_QUALITY_MAX = 1;
	public static final int AVIOCTRL_QUALITY_MIDDLE = 3;
	public static final int AVIOCTRL_QUALITY_MIN = 5;
	public static final int AVIOCTRL_QUALITY_UNKNOWN = 0;
	public static final int AVIOCTRL_RECORD_PLAY_BACKWARD = 5;
	public static final int AVIOCTRL_RECORD_PLAY_END = 7;
	public static final int AVIOCTRL_RECORD_PLAY_FORWARD = 4;
	public static final int AVIOCTRL_RECORD_PLAY_PAUSE = 0;
	public static final int AVIOCTRL_RECORD_PLAY_SEEKTIME = 6;
	public static final int AVIOCTRL_RECORD_PLAY_START = 16;
	public static final int AVIOCTRL_RECORD_PLAY_STEPBACKWARD = 3;
	public static final int AVIOCTRL_RECORD_PLAY_STEPFORWARD = 2;
	public static final int AVIOCTRL_RECORD_PLAY_STOP = 1;
	public static final int AVIOCTRL_SET_AUX = 33;
	public static final int AVIOCTRL_VIDEOMODE_FLIP = 1;
	public static final int AVIOCTRL_VIDEOMODE_FLIP_MIRROR = 3;
	public static final int AVIOCTRL_VIDEOMODE_MIRROR = 2;
	public static final int AVIOCTRL_VIDEOMODE_NORMAL = 0;
	public static final int AVIOTC_RECORDTYPE_ALAM = 2;
	public static final int AVIOTC_RECORDTYPE_FULLTIME = 1;
	public static final int AVIOTC_RECORDTYPE_MANUAL = 3;
	public static final int AVIOTC_RECORDTYPE_OFF = 0;
	public static final int AVIOTC_WIFIAPENC_INVALID = 0;
	public static final int AVIOTC_WIFIAPENC_NONE = 1;
	public static final int AVIOTC_WIFIAPENC_WEP = 2;
	public static final int AVIOTC_WIFIAPENC_WPA2_AES = 6;
	public static final int AVIOTC_WIFIAPENC_WPA2_PSK_AES = 10;
	public static final int AVIOTC_WIFIAPENC_WPA2_PSK_TKIP = 9;
	public static final int AVIOTC_WIFIAPENC_WPA2_TKIP = 5;
	public static final int AVIOTC_WIFIAPENC_WPA_AES = 4;
	public static final int AVIOTC_WIFIAPENC_WPA_PSK_AES = 8;
	public static final int AVIOTC_WIFIAPENC_WPA_PSK_TKIP = 7;
	public static final int AVIOTC_WIFIAPENC_WPA_TKIP = 3;
	public static final int AVIOTC_WIFIAPMODE_ADHOC = 0;
	public static final int AVIOTC_WIFIAPMODE_MANAGED = 1;
	public static final int IOTYPE_USER_IPCAM_AUDIOSTART = 768;
	public static final int IOTYPE_USER_IPCAM_AUDIOSTOP = 769;
	public static final int IOTYPE_USER_IPCAM_CURRENT_FLOWINFO = 914;
	public static final int IOTYPE_USER_IPCAM_DEVINFO_REQ = 816;
	public static final int IOTYPE_USER_IPCAM_DEVINFO_RESP = 817;
	public static final int IOTYPE_USER_IPCAM_EVENT_REPORT = 8191;
	public static final int IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_REQ = 896;
	public static final int IOTYPE_USER_IPCAM_FORMATEXTSTORAGE_RESP = 897;
	public static final int IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ = 810;
	public static final int IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_RESP = 811;
	public static final int IOTYPE_USER_IPCAM_GETMOTIONDETECT_REQ = 806;
	public static final int IOTYPE_USER_IPCAM_GETMOTIONDETECT_RESP = 807;
	public static final int IOTYPE_USER_IPCAM_GETRCD_DURATION_REQ = 790;
	public static final int IOTYPE_USER_IPCAM_GETRCD_DURATION_RESP = 791;
	public static final int IOTYPE_USER_IPCAM_GETRECORD_REQ = 786;
	public static final int IOTYPE_USER_IPCAM_GETRECORD_RESP = 787;
	public static final int IOTYPE_USER_IPCAM_GETSTREAMCTRL_REQ = 802;
	public static final int IOTYPE_USER_IPCAM_GETSTREAMCTRL_RESP = 803;
	public static final int IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ = 808;
	public static final int IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP = 809;
	public static final int IOTYPE_USER_IPCAM_GETWIFI_REQ = 836;
	public static final int IOTYPE_USER_IPCAM_GETWIFI_RESP = 837;
	public static final int IOTYPE_USER_IPCAM_GETWIFI_RESP_2 = 839;
	public static final int IOTYPE_USER_IPCAM_GET_ENVIRONMENT_REQ = 866;
	public static final int IOTYPE_USER_IPCAM_GET_ENVIRONMENT_RESP = 867;
	public static final int IOTYPE_USER_IPCAM_GET_EVENTCONFIG_REQ = 1024;
	public static final int IOTYPE_USER_IPCAM_GET_EVENTCONFIG_RESP = 1025;
	public static final int IOTYPE_USER_IPCAM_GET_FLOWINFO_REQ = 912;
	public static final int IOTYPE_USER_IPCAM_GET_FLOWINFO_RESP = 913;
	public static final int IOTYPE_USER_IPCAM_GET_SAVE_DROPBOX_REQ = 1280;
	public static final int IOTYPE_USER_IPCAM_GET_SAVE_DROPBOX_RESP = 1281;
	public static final int IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ = 928;
	public static final int IOTYPE_USER_IPCAM_GET_TIMEZONE_RESP = 929;
	public static final int IOTYPE_USER_IPCAM_GET_VIDEOMODE_REQ = 882;
	public static final int IOTYPE_USER_IPCAM_GET_VIDEOMODE_RESP = 883;
	public static final int IOTYPE_USER_IPCAM_LISTEVENT_REQ = 792;
	public static final int IOTYPE_USER_IPCAM_LISTEVENT_RESP = 793;
	public static final int IOTYPE_USER_IPCAM_LISTWIFIAP_REQ = 832;
	public static final int IOTYPE_USER_IPCAM_LISTWIFIAP_RESP = 833;
	public static final int IOTYPE_USER_IPCAM_PTZ_COMMAND = 4097;
	public static final int IOTYPE_USER_IPCAM_RECEIVE_FIRST_IFRAME = 4098;
	public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL = 794;
	public static final int IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL_RESP = 795;
	public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT_REQ = 804;
	public static final int IOTYPE_USER_IPCAM_SETMOTIONDETECT_RESP = 805;
	public static final int IOTYPE_USER_IPCAM_SETPASSWORD_REQ = 818;
	public static final int IOTYPE_USER_IPCAM_SETPASSWORD_RESP = 819;
	public static final int IOTYPE_USER_IPCAM_SETRCD_DURATION_REQ = 788;
	public static final int IOTYPE_USER_IPCAM_SETRCD_DURATION_RESP = 789;
	public static final int IOTYPE_USER_IPCAM_SETRECORD_REQ = 784;
	public static final int IOTYPE_USER_IPCAM_SETRECORD_RESP = 785;
	public static final int IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ = 800;
	public static final int IOTYPE_USER_IPCAM_SETSTREAMCTRL_RESP = 801;
	public static final int IOTYPE_USER_IPCAM_SETWIFI_REQ = 834;
	public static final int IOTYPE_USER_IPCAM_SETWIFI_REQ_2 = 838;
	public static final int IOTYPE_USER_IPCAM_SETWIFI_RESP = 835;
	public static final int IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ = 864;
	public static final int IOTYPE_USER_IPCAM_SET_ENVIRONMENT_RESP = 865;
	public static final int IOTYPE_USER_IPCAM_SET_EVENTCONFIG_REQ = 1026;
	public static final int IOTYPE_USER_IPCAM_SET_EVENTCONFIG_RESP = 1027;
	public static final int IOTYPE_USER_IPCAM_SET_SAVE_DROPBOX_REQ = 1282;
	public static final int IOTYPE_USER_IPCAM_SET_SAVE_DROPBOX_RESP = 1283;
	public static final int IOTYPE_USER_IPCAM_SET_TIMEZONE_REQ = 944;
	public static final int IOTYPE_USER_IPCAM_SET_TIMEZONE_RESP = 945;
	public static final int IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ = 880;
	public static final int IOTYPE_USER_IPCAM_SET_VIDEOMODE_RESP = 881;
	public static final int IOTYPE_USER_IPCAM_SPEAKERSTART = 848;
	public static final int IOTYPE_USER_IPCAM_SPEAKERSTOP = 849;
	public static final int IOTYPE_USER_IPCAM_START = 511;
	public static final int IOTYPE_USER_IPCAM_STOP = 767;

	public static class SAvEvent {
		public byte event;
		public byte status;
		public AVIOCTRLDEFs.STimeDay sTimeDay;
		byte[] utctime = new byte[8];
		byte[] reserved = new byte[2];

		public static int getTotalSize() {
			return 12;
		}
	}

	public static class SFrameInfo {
		byte cam_index;
		short codec_id;
		byte flags;
		byte onlineNum;
		byte[] reserved = new byte[3];
		int reserved2;
		int timestamp;

		public static byte[] parseContent(short paramShort, byte paramByte1,
				byte paramByte2, byte paramByte3, int paramInt) {
			byte[] arrayOfByte = new byte[16];
			System.arraycopy(Packet.shortToByteArray_Little(paramShort), 0,
					arrayOfByte, 0, 2);
			arrayOfByte[2] = paramByte1;
			arrayOfByte[3] = paramByte2;
			arrayOfByte[4] = paramByte3;
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 12, 4);
			return arrayOfByte;
		}
	}

	public static class SMsgAVIoctrlAVStream {
		int channel = 0;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int paramInt) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 0, 4);
			return arrayOfByte;
		}
	}

	public static class SMsgAVIoctrlCurrentFlowInfo {
		public int channel;
		public int elapse_time_ms;
		public int lost_incomplete_frame_count;
		public int total_actual_frame_size;
		public int total_expected_frame_size;
		public int total_frame_count;

		public static byte[] parseContent(int paramInt1, int paramInt2,
				int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
			byte[] arrayOfByte = new byte[32];
			System.arraycopy(Packet.intToByteArray_Little(paramInt1), 0,
					arrayOfByte, 0, 4);
			System.arraycopy(Packet.intToByteArray_Little(paramInt2), 0,
					arrayOfByte, 4, 4);
			System.arraycopy(Packet.intToByteArray_Little(paramInt3), 0,
					arrayOfByte, 8, 4);
			System.arraycopy(Packet.intToByteArray_Little(paramInt4), 0,
					arrayOfByte, 12, 4);
			System.arraycopy(Packet.intToByteArray_Little(paramInt5), 0,
					arrayOfByte, 16, 4);
			System.arraycopy(Packet.intToByteArray_Little(paramInt6), 0,
					arrayOfByte, 20, 4);
			return arrayOfByte;
		}
	}

	public static class SMsgAVIoctrlDeviceInfoReq {
		static byte[] reserved = new byte[4];

		public static byte[] parseContent() {
			return reserved;
		}
	}

	public class SMsgAVIoctrlDeviceInfoResp {
		int channel;
		int free;
		byte[] model = new byte[16];
		byte[] reserved = new byte[8];
		int total;
		byte[] vendor = new byte[16];
		int version;

		public SMsgAVIoctrlDeviceInfoResp() {
		}
	}

	public static class SMsgAVIoctrlEvent {
		public AVIOCTRLDEFs.STimeDay stTime;
		long time;
		public int channel;
		public int event;
		byte[] reserved = new byte[4];

		public static int getTotalSize() {
			return 24;
		}

		public SMsgAVIoctrlEvent(byte[] arrayOfByte) {
			byte abyte[] = new byte[8];
			System.arraycopy(arrayOfByte, 0, abyte, 0, 8);
			this.stTime = new AVIOCTRLDEFs.STimeDay(abyte);

			this.channel = Packet.byteArrayToInt_Little(arrayOfByte, 12);
			this.event = arrayOfByte[16];
		}
	}

	public class SMsgAVIoctrlEventConfig {
		long channel;
		byte ftp;
		byte localIO;
		byte mail;
		byte p2pPushMsg;

		public SMsgAVIoctrlEventConfig() {
		}
	}

	public static class SMsgAVIoctrlFormatExtStorageReq {
		byte[] reserved = new byte[4];
		int storage;

		public static byte[] parseContent(int paramInt) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 0, 4);
			return arrayOfByte;
		}
	}

	public class SMsgAVIoctrlFormatExtStorageResp {
		byte[] reserved = new byte[3];
		byte result;
		int storage;

		public SMsgAVIoctrlFormatExtStorageResp() {
		}
	}

	public static class SMsgAVIoctrlGetAudioOutFormatReq {
		public static byte[] parseContent() {
			return new byte[8];
		}
	}

	public class SMsgAVIoctrlGetAudioOutFormatResp {
		public int channel;
		public int format;

		public SMsgAVIoctrlGetAudioOutFormatResp() {
		}
	}

	public static class SMsgAVIoctrlGetDropbox {
		public short nLinked;
		public short nSupportDropbox;
		public String szLinkUDID;

		public SMsgAVIoctrlGetDropbox(byte[] paramArrayOfByte) {
			this.nSupportDropbox = Packet.byteArrayToShort_Little(
					paramArrayOfByte, 0);
			this.nLinked = Packet.byteArrayToShort_Little(paramArrayOfByte, 2);
			this.szLinkUDID = new String(paramArrayOfByte, 4, -4
					+ paramArrayOfByte.length).replace("", "");
		}
	}

	public static class SMsgAVIoctrlGetEnvironmentReq {
		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int paramInt) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 0, 4);
			return arrayOfByte;
		}
	}

	public class SMsgAVIoctrlGetEnvironmentResp {
		int channel;
		byte mode;
		byte[] reserved = new byte[3];

		public SMsgAVIoctrlGetEnvironmentResp() {
		}
	}

	public static class SMsgAVIoctrlGetFlowInfoReq {
		public int channel;
		public int collect_interval;
	}

	public static class SMsgAVIoctrlGetFlowInfoResp {
		public int channel;
		public int collect_interval;

		public static byte[] parseContent(int paramInt1, int paramInt2) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(paramInt1), 0,
					arrayOfByte, 0, 4);
			System.arraycopy(Packet.intToByteArray_Little(paramInt2), 0,
					arrayOfByte, 4, 4);
			return arrayOfByte;
		}
	}

	public static class SMsgAVIoctrlGetMotionDetectReq {
		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int paramInt) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 0, 4);
			return arrayOfByte;
		}
	}

	public class SMsgAVIoctrlGetMotionDetectResp {
		int channel;
		int sensitivity;

		public SMsgAVIoctrlGetMotionDetectResp() {
		}
	}

	public class SMsgAVIoctrlGetRcdDurationReq {
		int channel;
		byte[] reserved = new byte[4];

		public SMsgAVIoctrlGetRcdDurationReq() {
		}
	}

	public class SMsgAVIoctrlGetRcdDurationResp {
		int channel;
		int durasecond;
		int presecond;

		public SMsgAVIoctrlGetRcdDurationResp() {
		}
	}

	public static class SMsgAVIoctrlGetRecordReq {
		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int paramInt) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 0, 4);
			return arrayOfByte;
		}
	}

	public class SMsgAVIoctrlGetRecordResp {
		int channel;
		int recordType;
		byte[] reserved = new byte[4];

		public SMsgAVIoctrlGetRecordResp() {
		}
	}

	public static class SMsgAVIoctrlGetStreamCtrlReq {
		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int paramInt) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 0, 4);
			return arrayOfByte;
		}
	}

	public class SMsgAVIoctrlGetStreamCtrlResp {
		int channel;
		byte quality;
		byte[] reserved = new byte[3];

		public SMsgAVIoctrlGetStreamCtrlResp() {
		}
	}

	public static class SMsgAVIoctrlGetSupportStreamReq {
		public static int getContentSize() {
			return 4;
		}

		public static byte[] parseContent() {
			return new byte[4];
		}
	}

	public class SMsgAVIoctrlGetSupportStreamResp {
		public AVIOCTRLDEFs.SStreamDef[] mStreamDef;
		public long number;

		public SMsgAVIoctrlGetSupportStreamResp() {
		}
	}

	public static class SMsgAVIoctrlGetVideoModeReq {
		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int paramInt) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 0, 4);
			return arrayOfByte;
		}
	}

	public class SMsgAVIoctrlGetVideoModeResp {
		int channel;
		byte mode;
		byte[] reserved = new byte[3];

		public SMsgAVIoctrlGetVideoModeResp() {
		}
	}

	public static class SMsgAVIoctrlGetWifiReq {
		static byte[] reserved = new byte[4];

		public static byte[] parseContent() {
			return reserved;
		}
	}

	public class SMsgAVIoctrlGetWifiResp {
		byte enctype;
		byte mode;
		byte[] password = new byte[32];
		byte signal;
		byte[] ssid = new byte[32];
		byte status;

		public SMsgAVIoctrlGetWifiResp() {
		}
	}

	public static class SMsgAVIoctrlListEventReq {
		int channel;
		byte[] endutctime = new byte[8];
		byte event;
		byte[] reversed = new byte[2];
		byte[] startutctime = new byte[8];
		byte status;

		public static byte[] parseConent(int paramInt, long paramLong1,
				long paramLong2, byte paramByte1, byte paramByte2) {
			Calendar localCalendar1 = Calendar.getInstance(TimeZone
					.getTimeZone("gmt"));
			Calendar localCalendar2 = Calendar.getInstance(TimeZone
					.getTimeZone("gmt"));
			localCalendar1.setTimeInMillis(paramLong1);
			localCalendar2.setTimeInMillis(paramLong2);
			System.out.println("search from " + localCalendar1.get(1) + "/"
					+ localCalendar1.get(2) + "/" + localCalendar1.get(5) + " "
					+ localCalendar1.get(11) + ":" + localCalendar1.get(12)
					+ ":" + localCalendar1.get(13));
			System.out.println("       to   " + localCalendar2.get(1) + "/"
					+ localCalendar2.get(2) + "/" + localCalendar2.get(5) + " "
					+ localCalendar2.get(11) + ":" + localCalendar2.get(12)
					+ ":" + localCalendar2.get(13));
			byte[] arrayOfByte = new byte[24];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 0, 4);
			System.arraycopy(AVIOCTRLDEFs.STimeDay.parseContent(
					localCalendar1.get(1), 1 + localCalendar1.get(2),
					localCalendar1.get(5), localCalendar1.get(7),
					localCalendar1.get(11), localCalendar1.get(12), 0), 0,
					arrayOfByte, 4, 8);
			System.arraycopy(AVIOCTRLDEFs.STimeDay.parseContent(
					localCalendar2.get(1), 1 + localCalendar2.get(2),
					localCalendar2.get(5), localCalendar2.get(7),
					localCalendar2.get(11), localCalendar2.get(12), 0), 0,
					arrayOfByte, 12, 8);
			arrayOfByte[20] = paramByte1;
			arrayOfByte[21] = paramByte2;
			return arrayOfByte;
		}
	}

	public static class SMsgAVIoctrlListEventResp {
		public int channel;
		public int total;
		public byte count;
		public byte endflag;
		public byte index;
		public List<AVIOCTRLDEFs.SAvEvent> eventList;
		byte reserved;
		AVIOCTRLDEFs.SAvEvent stEvent;

		public SMsgAVIoctrlListEventResp(byte[] arrayOfByte) {
			this.channel = Packet.byteArrayToInt_Little(arrayOfByte);
			this.total = Packet.byteArrayToInt_Little(arrayOfByte, 4);
			this.index = arrayOfByte[8];
			this.endflag = arrayOfByte[9];
			this.count = arrayOfByte[10];

			int eventSize = AVIOCTRLDEFs.SAvEvent.getTotalSize();
			eventList = new ArrayList<AVIOCTRLDEFs.SAvEvent>();
			for (int i = 0; i < count; i++) {
				byte abyte[] = new byte[eventSize];
				System.arraycopy(arrayOfByte, 12 + i * eventSize, abyte, 0,
						eventSize);
				stEvent = new AVIOCTRLDEFs.SAvEvent();
				stEvent.sTimeDay = new AVIOCTRLDEFs.STimeDay(abyte);
				stEvent.event = abyte[8];
				stEvent.status = abyte[9];
				eventList.add(stEvent);
			}
		}
	}

	public static class SMsgAVIoctrlListWifiApReq {
		static byte[] reserved = new byte[4];

		public static byte[] parseContent() {
			return reserved;
		}
	}

	public class SMsgAVIoctrlListWifiApResp {
		int number;
		AVIOCTRLDEFs.SWifiAp stWifiAp;

		public SMsgAVIoctrlListWifiApResp() {
		}
	}

	public static class SMsgAVIoctrlPlayRecord {
		int Param;
		int channel;
		int command;
		byte[] reserved = new byte[4];
		byte[] stTimeDay = new byte[8];

		public static byte[] parseContent(int paramInt1, int paramInt2,
				int paramInt3, long paramLong) {
			byte[] arrayOfByte = new byte[24];
			System.arraycopy(Packet.intToByteArray_Little(paramInt1), 0,
					arrayOfByte, 0, 4);
			System.arraycopy(Packet.intToByteArray_Little(paramInt2), 0,
					arrayOfByte, 4, 4);
			System.arraycopy(Packet.intToByteArray_Little(paramInt3), 0,
					arrayOfByte, 8, 4);
			Calendar localCalendar = Calendar.getInstance(TimeZone
					.getTimeZone("gmt"));
			localCalendar.setTimeInMillis(paramLong);
			localCalendar.add(5, -1);
			localCalendar.add(5, 1);
			System.arraycopy(AVIOCTRLDEFs.STimeDay.parseContent(
					localCalendar.get(1), localCalendar.get(2),
					localCalendar.get(5), localCalendar.get(7),
					localCalendar.get(11), localCalendar.get(12),
					localCalendar.get(13)), 0, arrayOfByte, 12, 8);
			return arrayOfByte;
		}

		public static byte[] parseContent(int paramInt1, int paramInt2,
				int paramInt3, byte[] paramArrayOfByte) {
			byte[] arrayOfByte = new byte[24];
			System.arraycopy(Packet.intToByteArray_Little(paramInt1), 0,
					arrayOfByte, 0, 4);
			System.arraycopy(Packet.intToByteArray_Little(paramInt2), 0,
					arrayOfByte, 4, 4);
			System.arraycopy(Packet.intToByteArray_Little(paramInt3), 0,
					arrayOfByte, 8, 4);
			System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 12, 8);
			return arrayOfByte;
		}
	}

	public class SMsgAVIoctrlPlayRecordResp {
		int channel;
		byte[] reserved = new byte[4];
		int result;

		public SMsgAVIoctrlPlayRecordResp() {
		}
	}

	public static class SMsgAVIoctrlPtzCmd {
		byte aux;
		byte channel;
		byte control;
		byte limit;
		byte point;
		byte[] reserved = new byte[2];
		byte speed;

		public static byte[] parseContent(byte paramByte1, byte paramByte2,
				byte paramByte3, byte paramByte4, byte paramByte5,
				byte paramByte6) {
			byte[] arrayOfByte = new byte[8];
			arrayOfByte[0] = paramByte1;
			arrayOfByte[1] = paramByte2;
			arrayOfByte[2] = paramByte3;
			arrayOfByte[3] = paramByte4;
			arrayOfByte[4] = paramByte5;
			arrayOfByte[5] = paramByte6;
			return arrayOfByte;
		}
	}

	public static class SMsgAVIoctrlReceiveFirstIFrame {
		int channel;
		int recordType;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int paramInt1, int paramInt2) {
			byte[] arrayOfByte1 = new byte[12];
			byte[] arrayOfByte2 = Packet.intToByteArray_Little(paramInt1);
			byte[] arrayOfByte3 = Packet.intToByteArray_Little(paramInt2);
			System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, 4);
			System.arraycopy(arrayOfByte3, 0, arrayOfByte1, 4, 4);
			return arrayOfByte1;
		}
	}

	public static class SMsgAVIoctrlSetDropbox {
		public int nLinked;
		byte[] reserved = new byte[4];
		byte[] szAccessToken = new byte[32];
		byte[] szAccessTokenSecret = new byte[32];
		byte[] szAppKey = new byte[32];
		byte[] szLinkUDID = new byte[64];
		byte[] szSecret = new byte[32];

		public static byte[] parseContent(int paramInt, String paramString1,
				String paramString2, String paramString3, String paramString4,
				String paramString5, String paramString6) {
			byte[] arrayOfByte1 = new byte['Æ'];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte1, 0, 2);
			byte[] arrayOfByte2 = paramString1.getBytes();
			System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 2,
					arrayOfByte2.length);
			byte[] arrayOfByte3 = paramString2.getBytes();
			System.arraycopy(arrayOfByte3, 0, arrayOfByte1, 66,
					arrayOfByte3.length);
			byte[] arrayOfByte4 = paramString3.getBytes();
			System.arraycopy(arrayOfByte4, 0, arrayOfByte1, 98,
					arrayOfByte4.length);
			byte[] arrayOfByte5 = paramString4.getBytes();
			System.arraycopy(arrayOfByte5, 0, arrayOfByte1, 130,
					arrayOfByte5.length);
			byte[] arrayOfByte6 = paramString5.getBytes();
			System.arraycopy(arrayOfByte6, 0, arrayOfByte1, 162,
					arrayOfByte6.length);
			byte[] arrayOfByte7 = paramString6.getBytes();
			System.arraycopy(arrayOfByte7, 0, arrayOfByte1, 194,
					arrayOfByte7.length);
			return arrayOfByte1;
		}
	}

	public static class SMsgAVIoctrlSetEnvironmentReq {
		int channel;
		byte mode;
		byte[] reserved = new byte[3];

		public static byte[] parseContent(int paramInt, byte paramByte) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 0, 4);
			arrayOfByte[4] = paramByte;
			return arrayOfByte;
		}
	}

	public class SMsgAVIoctrlSetEnvironmentResp {
		int channel;
		byte[] reserved = new byte[3];
		byte result;

		public SMsgAVIoctrlSetEnvironmentResp() {
		}
	}

	public static class SMsgAVIoctrlSetMotionDetectReq {
		int channel;
		int sensitivity;

		public static byte[] parseContent(int paramInt1, int paramInt2) {
			byte[] arrayOfByte1 = new byte[8];
			byte[] arrayOfByte2 = Packet.intToByteArray_Little(paramInt1);
			byte[] arrayOfByte3 = Packet.intToByteArray_Little(paramInt2);
			System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, 4);
			System.arraycopy(arrayOfByte3, 0, arrayOfByte1, 4, 4);
			return arrayOfByte1;
		}
	}

	public class SMsgAVIoctrlSetMotionDetectResp {
		byte[] reserved = new byte[3];
		byte result;

		public SMsgAVIoctrlSetMotionDetectResp() {
		}
	}

	public static class SMsgAVIoctrlGetDevInfoReq {
		int channel;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int paramInt) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 0, 4);
			return arrayOfByte;
		}
	}

	public class SMsgAVIoctrlGetDevInfoResp {
		byte[] model = new byte[16];
		byte[] vendor = new byte[16];
		int version;
		int channel;
		int total;
		int free;
		byte[] reserved = new byte[8];

		public SMsgAVIoctrlGetDevInfoResp() {
		}
	}

	public static class SMsgAVIoctrlSetPasswdReq {
		byte[] newPasswd = new byte[32];
		byte[] oldPasswd = new byte[32];

		public static byte[] parseContent(String paramString1,
				String paramString2) {
			byte[] arrayOfByte1 = paramString1.getBytes();
			byte[] arrayOfByte2 = paramString2.getBytes();
			byte[] arrayOfByte3 = new byte[64];
			System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0,
					arrayOfByte1.length);
			System.arraycopy(arrayOfByte2, 0, arrayOfByte3, 32,
					arrayOfByte2.length);
			return arrayOfByte3;
		}
	}

	public class SMsgAVIoctrlSetPasswdResp {
		byte[] reserved = new byte[3];
		byte result;

		public SMsgAVIoctrlSetPasswdResp() {
		}
	}

	public class SMsgAVIoctrlSetRcdDurationReq {
		int channel;
		int durasecond;
		int presecond;

		public SMsgAVIoctrlSetRcdDurationReq() {
		}
	}

	public class SMsgAVIoctrlSetRcdDurationResp {
		byte[] reserved = new byte[3];
		byte result;

		public SMsgAVIoctrlSetRcdDurationResp() {
		}
	}

	public static class SMsgAVIoctrlSetRecordReq {
		int channel;
		int recordType;
		byte[] reserved = new byte[4];

		public static byte[] parseContent(int paramInt1, int paramInt2) {
			byte[] arrayOfByte1 = new byte[12];
			byte[] arrayOfByte2 = Packet.intToByteArray_Little(paramInt1);
			byte[] arrayOfByte3 = Packet.intToByteArray_Little(paramInt2);
			System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, 4);
			System.arraycopy(arrayOfByte3, 0, arrayOfByte1, 4, 4);
			return arrayOfByte1;
		}
	}

	public class SMsgAVIoctrlSetRecordResp {
		byte[] reserved = new byte[3];
		byte result;

		public SMsgAVIoctrlSetRecordResp() {
		}
	}

	public static class SMsgAVIoctrlSetStreamCtrlReq {
		int channel;
		byte quality;
		byte[] reserved = new byte[3];

		public static byte[] parseContent(int paramInt, byte paramByte) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 0, 4);
			arrayOfByte[4] = paramByte;
			return arrayOfByte;
		}
	}

	public class SMsgAVIoctrlSetStreamCtrlResp {
		byte[] reserved = new byte[4];
		int result;

		public SMsgAVIoctrlSetStreamCtrlResp() {
		}
	}

	public static class SMsgAVIoctrlSetVideoModeReq {
		int channel;
		byte mode;
		byte[] reserved = new byte[3];

		public static byte[] parseContent(int paramInt, byte paramByte) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.intToByteArray_Little(paramInt), 0,
					arrayOfByte, 0, 4);
			arrayOfByte[4] = paramByte;
			return arrayOfByte;
		}
	}

	public class SMsgAVIoctrlSetVideoModeResp {
		int channel;
		byte[] reserved = new byte[3];
		byte result;

		public SMsgAVIoctrlSetVideoModeResp() {
		}
	}

	public static class SMsgAVIoctrlSetWifiReq {
		byte enctype;
		byte mode;
		byte[] password = new byte[32];
		byte[] reserved = new byte[10];
		byte[] ssid = new byte[32];

		public static byte[] parseContent(byte[] paramArrayOfByte1,
				byte[] paramArrayOfByte2, byte paramByte1, byte paramByte2) {
			byte[] arrayOfByte = new byte[76];
			System.arraycopy(paramArrayOfByte1, 0, arrayOfByte, 0,
					paramArrayOfByte1.length);
			System.arraycopy(paramArrayOfByte2, 0, arrayOfByte, 32,
					paramArrayOfByte2.length);
			arrayOfByte[64] = paramByte1;
			arrayOfByte[65] = paramByte2;
			return arrayOfByte;
		}
	}

	public class SMsgAVIoctrlSetWifiResp {
		byte[] reserved = new byte[3];
		byte result;

		public SMsgAVIoctrlSetWifiResp() {
		}
	}

	public static class SMsgAVIoctrlTimeZone {
		public int cbSize;
		public int nGMTDiff;
		public int nIsSupportTimeZone;
		public byte[] szTimeZoneString = new byte[256];

		public static byte[] parseContent() {
			return new byte[268];
		}

		public static byte[] parseContent(int paramInt1, int paramInt2,
				int paramInt3, byte[] paramArrayOfByte) {
			byte[] arrayOfByte = new byte[268];
			System.arraycopy(Packet.intToByteArray_Little(paramInt1), 0,
					arrayOfByte, 0, 4);
			System.arraycopy(Packet.intToByteArray_Little(paramInt2), 0,
					arrayOfByte, 4, 4);
			System.arraycopy(Packet.intToByteArray_Little(paramInt3), 0,
					arrayOfByte, 8, 4);
			System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 12,
					paramArrayOfByte.length);
			return arrayOfByte;
		}
	}

	public static class SStreamDef {
		public int channel;
		public int index;

		public SStreamDef(byte[] paramArrayOfByte) {
			this.index = Packet.byteArrayToShort_Little(paramArrayOfByte, 0);
			this.channel = Packet.byteArrayToShort_Little(paramArrayOfByte, 2);
		}

		public String toString() {
			return "CH" + String.valueOf(1 + this.index);
		}
	}

	public static class STimeDay {
		public byte day;
		public byte hour;
		private byte[] mBuf = new byte[8];
		public byte minute;
		public byte month;
		public byte second;
		public byte wday;
		public short year;

		public STimeDay(byte[] paramArrayOfByte) {
			System.arraycopy(paramArrayOfByte, 0, this.mBuf, 0, 8);
			this.year = Packet.byteArrayToShort_Little(paramArrayOfByte, 0);
			this.month = paramArrayOfByte[2];
			this.day = paramArrayOfByte[3];
			this.wday = paramArrayOfByte[4];
			this.hour = paramArrayOfByte[5];
			this.minute = paramArrayOfByte[6];
			this.second = paramArrayOfByte[7];
		}

		public static byte[] parseContent(int paramInt1, int paramInt2,
				int paramInt3, int paramInt4, int paramInt5, int paramInt6,
				int paramInt7) {
			byte[] arrayOfByte = new byte[8];
			System.arraycopy(Packet.shortToByteArray_Little((short) paramInt1),
					0, arrayOfByte, 0, 2);
			arrayOfByte[2] = (byte) paramInt2;
			arrayOfByte[3] = (byte) paramInt3;
			arrayOfByte[4] = (byte) paramInt4;
			arrayOfByte[5] = (byte) paramInt5;
			arrayOfByte[6] = (byte) paramInt6;
			arrayOfByte[7] = (byte) paramInt7;
			return arrayOfByte;
		}

		public String getLocalTime() {
			Calendar localCalendar = Calendar.getInstance(TimeZone
					.getTimeZone("gmt"));
			localCalendar.setTimeInMillis(getTimeInMillis());
			SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			return localSimpleDateFormat.format(localCalendar.getTime());
		}

		public String getLocalTimeDefault() {
			Calendar localCalendar = Calendar
					.getInstance(TimeZone.getDefault());
			localCalendar.setTimeInMillis(getDefaultTimeInMillis());
			SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			// localSimpleDateFormat.setTimeZone(TimeZone.getDefault());
			return localSimpleDateFormat.format(localCalendar.getTime());
		}

		public long getTimeInMillis() {
			Calendar localCalendar = Calendar.getInstance(TimeZone
					.getTimeZone("gmt"));
			localCalendar.set(this.year, -1 + this.month, this.day, this.hour,
					this.minute, this.second);
			return localCalendar.getTimeInMillis();
		}

		public long getDefaultTimeInMillis() {
			Calendar localCalendar = Calendar
					.getInstance(TimeZone.getDefault());
			localCalendar.set(this.year, -1 + this.month, this.day, this.hour,
					this.minute, this.second);
			return localCalendar.getTimeInMillis();
		}

		public byte[] toByteArray() {
			return this.mBuf;
		}
	}

	public static class SWifiAp {
		public byte enctype;
		public byte mode;
		public byte signal;
		public byte[] ssid = new byte[32];
		public byte status;

		public SWifiAp(byte[] paramArrayOfByte) {
			System.arraycopy(paramArrayOfByte, 0, this.ssid, 0,
					this.ssid.length);
			this.mode = paramArrayOfByte[32];
			this.enctype = paramArrayOfByte[33];
			this.signal = paramArrayOfByte[34];
			this.status = paramArrayOfByte[35];
		}

		public SWifiAp(byte[] paramArrayOfByte, byte paramByte1,
				byte paramByte2, byte paramByte3, byte paramByte4) {
			System.arraycopy(paramArrayOfByte, 0, this.ssid, 0,
					paramArrayOfByte.length);
			this.mode = paramByte1;
			this.enctype = paramByte2;
			this.signal = paramByte3;
			this.status = paramByte4;
		}

		public static int getTotalSize() {
			return 36;
		}
	}
}
