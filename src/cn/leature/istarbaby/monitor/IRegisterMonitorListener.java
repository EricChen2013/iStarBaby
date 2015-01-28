package cn.leature.istarbaby.monitor;

import android.graphics.Bitmap;

public abstract interface IRegisterMonitorListener {
	public abstract void receiveChannelInfo(MonitorClient paramCamera,
			int paramInt1, int paramInt2);

	public abstract void receiveFrameData(MonitorClient paramCamera,
			int paramInt, Bitmap paramBitmap);

	public abstract void receiveFrameInfo(MonitorClient paramCamera,
			int paramInt1, long paramLong, int paramInt2, int paramInt3,
			int paramInt4, int paramInt5);

	public abstract void receiveIOCtrlData(MonitorClient paramCamera,
			int channel, int ioCtrlType, byte[] paramArrayOfByte);

	public abstract void receiveSessionInfo(MonitorClient paramCamera,
			int paramInt);
}
