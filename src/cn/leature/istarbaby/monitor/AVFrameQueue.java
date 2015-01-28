package cn.leature.istarbaby.monitor;

import java.util.LinkedList;

class AVFrameQueue {
	private volatile LinkedList<AVFrame> listData = new LinkedList<AVFrame>();
	private volatile int mSize = 0;

	public synchronized int getCount() {
		return this.mSize;
	}

	public synchronized void addLast(AVFrame node) {
		if (this.mSize > 1500) {
			boolean bFirst = true;

			while (!this.listData.isEmpty()) {
				AVFrame frame = (AVFrame) this.listData.get(0);

				if (bFirst) {
					if (frame.isIFrame())
						System.out.println("drop I frame");
					else {
						System.out.println("drop p frame");
					}
					this.listData.removeFirst();
					this.mSize -= 1;
				} else {
					if (frame.isIFrame()) {
						break;
					}
					System.out.println("drop p frame");
					this.listData.removeFirst();
					this.mSize -= 1;
				}

				bFirst = false;
			}
		}

		this.listData.addLast(node);
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

	public synchronized boolean isFirstIFrame() {
		return (this.listData != null) && (!this.listData.isEmpty())
				&& (((AVFrame) this.listData.get(0)).isIFrame());
	}
}
