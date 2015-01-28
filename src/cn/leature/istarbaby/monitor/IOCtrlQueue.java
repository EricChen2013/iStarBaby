package cn.leature.istarbaby.monitor;

import java.util.LinkedList;

public class IOCtrlQueue {
	LinkedList<IOCtrlSet> listData = new LinkedList<IOCtrlSet>();

	public IOCtrlQueue() {
	}

	public synchronized boolean isEmpty() {
		return this.listData.isEmpty();
	}

	public synchronized void Enqueue(int type, byte[] data) {
		this.listData.addLast(new IOCtrlSet(type, data));
	}

	public synchronized void Enqueue(int avIndex, int type, byte[] data) {
		this.listData.addLast(new IOCtrlSet(avIndex, type, data));
	}

	public synchronized IOCtrlSet Dequeue() {
		return this.listData.isEmpty() ? null : (IOCtrlSet) this.listData
				.removeFirst();
	}

	public synchronized void removeAll() {
		if (!this.listData.isEmpty())
			this.listData.clear();
	}

	public class IOCtrlSet {
		public int IOCtrlType;
		public byte[] IOCtrlBuf;

		public IOCtrlSet(int avIndex, int type, byte[] buf) {
			this.IOCtrlType = type;
			this.IOCtrlBuf = buf;
		}

		public IOCtrlSet(int type, byte[] buf) {
			this.IOCtrlType = type;
			this.IOCtrlBuf = buf;
		}
	}
}
