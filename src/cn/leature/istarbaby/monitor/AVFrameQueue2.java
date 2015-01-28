package cn.leature.istarbaby.monitor;

import java.util.LinkedList;

class AVFrameQueue2 {
	private volatile LinkedList<AVFrame> listData = new LinkedList<AVFrame>();
	private volatile int mSize = 0;

	public synchronized int getCount() {
		return this.mSize;
	}

	public synchronized void addLast(AVFrame node) {

		byte[] data = node.frmData;
		int len = node.getFrmSize();

		int position = findFrameHeaderPosition(data, len, 0);

		if (position < 0) {
			// 没有帧头， 拼接到前一帧
			if (this.listData.isEmpty()) {
				// 找不到前一帧，扔掉
				return;
			}

			// 直接加到前一帧
			joinLastFrame(node);

			return;
		} else if (position == 0) {
			// 完整的帧开头
			this.listData.addLast(node);
			this.mSize += 1;

			return;
		}

		// 取出前面的数据
		len = position;
		byte[] dataBuff = new byte[len];
		System.arraycopy(data, 0, dataBuff, 0, len);

		// 直接加到前一帧
		joinLastFrame(new AVFrame(dataBuff, len));

		// 剩余的生成新的一帧
		len = node.getFrmSize() - position;
		byte[] dataRemain = new byte[len];
		System.arraycopy(data, position, dataRemain, 0, len);

		this.listData.addLast(new AVFrame(dataRemain, len));
		this.mSize += 1;
	}

	public synchronized AVFrame removeHead() {
		if (this.mSize == 0) {
			return null;
		}
		AVFrame frame = (AVFrame) this.listData.removeFirst();
		this.mSize -= 1;
		return frame;
	}

	public synchronized void removeAll() {
		if (!this.listData.isEmpty()) {
			this.listData.clear();
		}
		this.mSize = 0;
	}

	/*
	 * 查找 头标识（0x00 00 00 01 67）的位置 参数：data 对象数据流 len 数据长度 start 搜索开始位置 返回：位置（-1：
	 * 未找到） 从0开始 同时也表示， 头标识之前的长度
	 */
	private int findFrameHeaderPosition(byte[] buffer, int len, int start) {

		if (len - start < 5) {
			// 剩余位数不足5位
			return -1;
		}

		int i;
		boolean isMatch = false;
		for (i = start; i < len - 4; i++) {
			if (buffer[i] == 0x00 && buffer[i + 1] == 0x00
					&& buffer[i + 2] == 0x00 && buffer[i + 3] == 0x01
					&& buffer[i + 4] == 0x67) {
				isMatch = true;
				break;
			}
		}

		return isMatch ? i : -1;
	}

	private void joinLastFrame(AVFrame node) {
		// 取出前一桢
		AVFrame lastFrame = this.listData.getLast();
		int len = lastFrame.getFrmSize() + node.getFrmSize();

		byte[] dataBuff = new byte[len];
		System.arraycopy(lastFrame.frmData, 0, dataBuff, 0,
				lastFrame.getFrmSize());

		// 接到前一桢后面
		System.arraycopy(node.frmData, 0, dataBuff, lastFrame.getFrmSize(),
				node.getFrmSize());
		lastFrame.frmData = dataBuff;
		lastFrame.setFrmSize(len);

		return;
	}
}
