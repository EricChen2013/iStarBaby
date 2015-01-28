package com.tutk.IOTC;

public class Packet {
	public static final short byteArrayToShort_Little(byte[] byt, int nBeginPos) {
		return (short) (0xFF & byt[nBeginPos] | (0xFF & byt[(nBeginPos + 1)]) << 8);
	}

	public static final int byteArrayToInt_Little(byte[] byt, int nBeginPos) {
		return 0xFF & byt[nBeginPos] | (0xFF & byt[(nBeginPos + 1)]) << 8
				| (0xFF & byt[(nBeginPos + 2)]) << 16
				| (0xFF & byt[(nBeginPos + 3)]) << 24;
	}

	public static final int byteArrayToInt_Little(byte[] byt) {
		if (byt.length == 1)
			return 0xFF & byt[0];
		if (byt.length == 2)
			return 0xFF & byt[0] | (0xFF & byt[1]) << 8;
		if (byt.length == 4) {
			return 0xFF & byt[0] | (0xFF & byt[1]) << 8 | (0xFF & byt[2]) << 16
					| (0xFF & byt[3]) << 24;
		}
		return 0;
	}

	public static final long byteArrayToLong_Little(byte[] byt, int nBeginPos) {
		return 0xFF & byt[nBeginPos] | (0xFF & byt[(nBeginPos + 1)]) << 8
				| (0xFF & byt[(nBeginPos + 2)]) << 16
				| (0xFF & byt[(nBeginPos + 3)]) << 24
				| (0xFF & byt[(nBeginPos + 1)]) << 32
				| (0xFF & byt[(nBeginPos + 1)]) << 40
				| (0xFF & byt[(nBeginPos + 1)]) << 48
				| (0xFF & byt[(nBeginPos + 1)]) << 56;
	}

	public static final int byteArrayToInt_Big(byte[] byt) {
		if (byt.length == 1)
			return 0xFF & byt[0];
		if (byt.length == 2)
			return (0xFF & byt[0]) << 8 | 0xFF & byt[1];
		if (byt.length == 4) {
			return (0xFF & byt[0]) << 24 | (0xFF & byt[1]) << 16
					| (0xFF & byt[2]) << 8 | 0xFF & byt[3];
		}
		return 0;
	}

	public static final byte[] longToByteArray_Little(long value) {
		return new byte[] { (byte) (int) value, (byte) (int) (value >>> 8),
				(byte) (int) (value >>> 16), (byte) (int) (value >>> 24),
				(byte) (int) (value >>> 32), (byte) (int) (value >>> 40),
				(byte) (int) (value >>> 48), (byte) (int) (value >>> 56) };
	}

	public static final byte[] intToByteArray_Little(int value) {
		return new byte[] { (byte) value, (byte) (value >>> 8),
				(byte) (value >>> 16), (byte) (value >>> 24) };
	}

	public static final byte[] intToByteArray_Big(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}

	public static final byte[] shortToByteArray_Little(short value) {
		return new byte[] { (byte) value, (byte) (value >>> 8) };
	}

	public static final byte[] shortToByteArray_Big(short value) {
		return new byte[] { (byte) (value >>> 8), (byte) value };
	}
}
