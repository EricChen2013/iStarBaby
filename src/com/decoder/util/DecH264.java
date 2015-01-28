package com.decoder.util;

public class DecH264 {
	static {
		try {
			System.loadLibrary("H264Android");
		} catch (UnsatisfiedLinkError localUnsatisfiedLinkError) {
			System.out.println("loadLibrary(H264Android),"
					+ localUnsatisfiedLinkError.getMessage());
		}
	}

	public static native int DecoderNal(byte[] paramArrayOfByte1, int paramInt,
			int[] paramArrayOfInt, byte[] paramArrayOfByte2,
			boolean paramBoolean);

	public static native int DecoderNalV2(int paramInt1,
			byte[] paramArrayOfByte1, int paramInt2, int[] paramArrayOfInt,
			byte[] paramArrayOfByte2, boolean paramBoolean);

	public static native int DeinitDecoderV2(int paramInt);

	public static native int InitDecoder();

	public static native int InitDecoderV2(int[] paramArrayOfInt);

	public static native void SetMaxAVCodecCtxNum(int paramInt);

	public static native int UninitDecoder();
}
