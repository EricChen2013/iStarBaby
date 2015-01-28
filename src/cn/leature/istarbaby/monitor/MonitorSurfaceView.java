package cn.leature.istarbaby.monitor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.utils.LcLog;

import com.tutk.IOTC.AVIOCTRLDEFs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;
import android.view.View;

public class MonitorSurfaceView extends SurfaceView implements Callback,
		IRegisterMonitorListener, OnGestureListener, View.OnTouchListener {
	private final String TAG = "MonitorSurfaceView";
	public boolean mEnableDither = false;
	// private static final float DEFAULT_MAX_ZOOM_SCALE = 2.0F;
	// private static final int PTZ_SPEED = 8;
	// private static final int PTZ_DELAY = 1500;
	// private static final int FLING_MIN_DISTANCE = 100;
	// private static final int FLING_MIN_VELOCITY = 0;
	// private static final int NONE = 0;
	// private static final int DRAG = 1;
	// private static final int ZOOM = 2;
	private int mPinchedMode = 0;
	private PointF mStartPoint = new PointF();
	private PointF mMidPoint = new PointF();
	private PointF mMidPointForCanvas = new PointF();
	private float mOrigDist = 0.0F;
	private long mLastZoomTime;
	private float mCurrentScale = 1.0F;
	private float mCurrentMaxScale = 2.0F;
	private GestureDetector mGestureDetector;
	private SurfaceHolder mSurHolder = null;
	private int vLeft;
	private int vTop;
	private int vRight;
	private int vBottom;
	private Rect mRectCanvas = new Rect();
	private Rect mRectMonitor = new Rect();

	private Paint mPaint = new Paint();
	private Bitmap mLastFrame;
	private Lock mLastFrameLock = new ReentrantLock();
	private MonitorClient mCamera;
	private int mAVChannel = -1;

	private int mCurVideoWidth = 0;
	private int mCurVideoHeight = 0;

	private ThreadRender mThreadRender = null;

	private boolean mbEnablePTZ = true;

	private boolean mbFitXY = false;

	public void setPTZ(boolean bEnable) {
		this.mbEnablePTZ = bEnable;
	}

	public void setFixXY(boolean bEnable) {
		this.mbFitXY = bEnable;
	}

	public MonitorSurfaceView(Context context) {
		super(context);

		this.mSurHolder = getHolder();
		this.mSurHolder.addCallback(this);

		this.mGestureDetector = new GestureDetector(this);
		setOnTouchListener(this);
		setLongClickable(true);
	}

	public MonitorSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.mSurHolder = getHolder();
		this.mSurHolder.addCallback(this);

		this.mGestureDetector = new GestureDetector(this);
		setOnTouchListener(this);
		setLongClickable(true);
	}

	public void setMaxZoom(float value) {
		this.mCurrentMaxScale = value;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		LcLog.i(TAG, "[surfaceChanged]width:" + width + ",height:" + height);

		synchronized (this) {
			this.mRectMonitor.set(0, 0, width, height);
			this.mRectCanvas.set(0, 0, width, height);

			if ((this.mCurVideoWidth == 0) || (this.mCurVideoHeight == 0)) {
				if (height < width) {
					this.mRectCanvas.bottom = (3 * width / 4);
					this.mRectCanvas.offset(0,
							(height - this.mRectCanvas.bottom) / 2);
				} else {
					this.mRectCanvas.right = (4 * height / 3);
					this.mRectCanvas.offset(
							(width - this.mRectCanvas.right) / 2, 0);
				}
			} else if (!this.mbFitXY) {

				if (this.mRectMonitor.bottom - this.mRectMonitor.top < this.mRectMonitor.right
						- this.mRectMonitor.left) {
					LcLog.i(TAG, "Landscape layout");

					double ratio = (double) this.mCurVideoWidth
							/ (double) this.mCurVideoHeight;

					this.mRectCanvas.right = (int) (this.mRectMonitor.bottom * ratio);
					this.mRectCanvas
							.offset((this.mRectMonitor.right - this.mRectCanvas.right) / 2,
									0);
				} else {
					LcLog.i(TAG, "Portrait layout");

					double ratio = (double) this.mCurVideoWidth
							/ (double) this.mCurVideoHeight;

					this.mRectCanvas.bottom = (int) (this.mRectMonitor.right / ratio);

					this.mRectCanvas
							.offset(0,
									(this.mRectMonitor.bottom - this.mRectCanvas.bottom) / 2);
				}
			}

			this.vLeft = this.mRectCanvas.left;
			this.vTop = this.mRectCanvas.top;
			this.vRight = this.mRectCanvas.right;
			this.vBottom = this.mRectCanvas.bottom;

			this.mCurrentScale = 1.0F;

			parseMidPoint(this.mMidPoint, this.vLeft, this.vTop, this.vRight,
					this.vBottom);
			parseMidPoint(this.mMidPointForCanvas, this.vLeft, this.vTop,
					this.vRight, this.vBottom);

			LcLog.d(TAG, "[surfaceChanged] mRectCanvas--left:"
					+ MonitorSurfaceView.this.mRectCanvas.left + ",right:"
					+ MonitorSurfaceView.this.mRectCanvas.right + ",top:"
					+ MonitorSurfaceView.this.mRectCanvas.top + ",bottom:"
					+ MonitorSurfaceView.this.mRectCanvas.bottom);
		}
	}

	public void surfaceCreated(SurfaceHolder holder) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public void attachCamera(MonitorClient camera, int avChannel) {
		this.mCamera = camera;
		this.mCamera.registerIOTCListener(this);
		this.mAVChannel = avChannel;
		this.mEnableDither = camera.mEnableDither;

		if (this.mThreadRender == null) {
			this.mThreadRender = new ThreadRender();
			this.mThreadRender.start();
		}
	}

	public void deattachCamera() {
		this.mAVChannel = -1;

		if (this.mCamera != null) {
			this.mCamera.unregisterIOTCListener(this);
			this.mCamera = null;
		}

		if (this.mThreadRender != null) {
			this.mThreadRender.stopThread();
			try {
				this.mThreadRender.interrupt();
				this.mThreadRender.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			this.mThreadRender = null;
		}
	}

	public void receiveFrameData(MonitorClient camera, int avChannel, Bitmap bmp) {
		LcLog.i(TAG, "receiveFrameData channel:" + avChannel);
		if (this.mAVChannel == avChannel) {
			this.mLastFrame = bmp;

			if ((bmp.getWidth() > 0)
					&& (bmp.getHeight() > 0)
					&& (this.mRectMonitor.right > 0)
					&& (this.mRectMonitor.bottom > 0)
					&& ((bmp.getWidth() != this.mCurVideoWidth) || (bmp
							.getHeight() != this.mCurVideoHeight))) {
				this.mCurVideoWidth = bmp.getWidth();
				this.mCurVideoHeight = bmp.getHeight();

				this.mRectCanvas.set(0, 0, this.mRectMonitor.right,
						this.mRectMonitor.bottom);

				if (!this.mbFitXY) {
					if (this.mRectMonitor.bottom - this.mRectMonitor.top < this.mRectMonitor.right
							- this.mRectMonitor.left) {
						LcLog.i("IOTCamera", "Landscape layout");
						double ratio = (double) this.mCurVideoWidth
								/ (double) this.mCurVideoHeight;
						this.mRectCanvas.right = (int) (this.mRectMonitor.bottom * ratio);
						this.mRectCanvas
								.offset((this.mRectMonitor.right - this.mRectCanvas.right) / 2,
										0);
					} else {
						LcLog.i("IOTCamera", "Portrait layout");
						double ratio = (double) this.mCurVideoWidth
								/ (double) this.mCurVideoHeight;

						this.mRectCanvas.bottom = (int) (this.mRectMonitor.right / ratio);

						this.mRectCanvas
								.offset(0,
										(this.mRectMonitor.bottom - this.mRectCanvas.bottom) / 2);
					}
				}

				this.vLeft = this.mRectCanvas.left;
				this.vTop = this.mRectCanvas.top;
				this.vRight = this.mRectCanvas.right;
				this.vBottom = this.mRectCanvas.bottom;

				this.mCurrentScale = 1.0F;

				parseMidPoint(this.mMidPoint, this.vLeft, this.vTop,
						this.vRight, this.vBottom);
				parseMidPoint(this.mMidPointForCanvas, this.vLeft, this.vTop,
						this.vRight, this.vBottom);

				LcLog.e(TAG, "[receiveFrameData]mRectCanvas--left:"
						+ MonitorSurfaceView.this.mRectCanvas.left + ",right:"
						+ MonitorSurfaceView.this.mRectCanvas.right + ",top:"
						+ MonitorSurfaceView.this.mRectCanvas.top + ",bottom:"
						+ MonitorSurfaceView.this.mRectCanvas.bottom);

				LcLog.i("IOTCamera", "Change canvas size ("
						+ (this.mRectCanvas.right - this.mRectCanvas.left)
						+ ", "
						+ (this.mRectCanvas.bottom - this.mRectCanvas.top)
						+ ")");
			}
		}
	}

	public void receiveFrameInfo(MonitorClient camera, int sessionChannel,
			long bitRate, int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
	}

	public void receiveChannelInfo(MonitorClient camera, int sessionChannel,
			int resultCode) {
	}

	public void receiveSessionInfo(MonitorClient camera, int resultCode) {
	}

	public void receiveIOCtrlData(MonitorClient camera, int sessionChannel,
			int avIOCtrlMsgType, byte[] data) {
	}

	public boolean onTouch(View view, MotionEvent event) {
		if (!this.mbEnablePTZ) {
			return false;
		}
		this.mGestureDetector.onTouchEvent(event);

		switch (event.getAction() & 0xFF) {
		case 0:
			if ((this.mRectCanvas.left == this.vLeft)
					&& (this.mRectCanvas.top == this.vTop)
					&& (this.mRectCanvas.right == this.vRight)
					&& (this.mRectCanvas.bottom == this.vBottom))
				break;
			this.mPinchedMode = 1;
			this.mStartPoint.set(event.getX(), event.getY());

			break;
		case 5:
			float dist = spacing(event);

			if (dist <= 10.0F)
				break;
			this.mPinchedMode = 2;
			this.mOrigDist = dist;

			System.out.println("Action_Pointer_Down -> origDist("
					+ this.mOrigDist + ")");

			break;
		case 2:
			if (this.mPinchedMode == 1) {
				if (System.currentTimeMillis() - this.mLastZoomTime < 33L) {
					return true;
				}
				PointF currentPoint = new PointF();
				currentPoint.set(event.getX(), event.getY());

				int offsetX = (int) currentPoint.x - (int) this.mStartPoint.x;
				int offsetY = (int) currentPoint.y - (int) this.mStartPoint.y;

				this.mStartPoint = currentPoint;

				Rect rect = new Rect();
				rect.set(this.mRectCanvas);
				rect.offset(offsetX, offsetY);

				int width = rect.right - rect.left;
				int height = rect.bottom - rect.top;

				if (this.mRectMonitor.bottom - this.mRectMonitor.top > this.mRectMonitor.right
						- this.mRectMonitor.left) {
					if (rect.left > this.mRectMonitor.left) {
						rect.left = this.mRectMonitor.left;
						rect.right = (rect.left + width);
					}

					if (rect.top > this.mRectMonitor.top) {
						rect.top = this.mRectCanvas.top;
						rect.bottom = (rect.top + height);
					}

					if (rect.right < this.mRectMonitor.right) {
						rect.right = this.mRectMonitor.right;
						rect.left = (rect.right - width);
					}

					if (rect.bottom < this.mRectMonitor.bottom) {
						rect.bottom = this.mRectCanvas.bottom;
						rect.top = (rect.bottom - height);
					}
				} else {
					if (rect.left > this.mRectMonitor.left) {
						rect.left = this.mRectCanvas.left;
						rect.right = (rect.left + width);
					}

					if (rect.top > this.mRectMonitor.top) {
						rect.top = this.mRectMonitor.top;
						rect.bottom = (rect.top + height);
					}

					if (rect.right < this.mRectMonitor.right) {
						rect.right = this.mRectCanvas.right;
						rect.left = (rect.right - width);
					}

					if (rect.bottom < this.mRectMonitor.bottom) {
						rect.bottom = this.mRectMonitor.bottom;
						rect.top = (rect.bottom - height);
					}
				}

				System.out.println("offset (" + offsetX + ", " + offsetY
						+ "), after offset rect = (" + rect.left + ", "
						+ rect.top + ", " + rect.right + ", " + rect.bottom
						+ ")");

				this.mRectCanvas.set(rect);
			} else {
				if (this.mPinchedMode != 2)
					break;
				if (System.currentTimeMillis() - this.mLastZoomTime < 33L) {
					return true;
				}
				if (event.getPointerCount() == 1) {
					return true;
				}
				float newDist = spacing(event);
				float scale = newDist / this.mOrigDist;
				this.mCurrentScale *= scale;

				this.mOrigDist = newDist;

				if (this.mCurrentScale > this.mCurrentMaxScale) {
					this.mCurrentScale = this.mCurrentMaxScale;
					return true;
				}

				if (this.mCurrentScale < 1.0F) {
					this.mCurrentScale = 1.0F;
				}

				System.out.println("newDist(" + newDist + ") / origDist("
						+ this.mOrigDist + ") = zoom scale("
						+ this.mCurrentScale + ")");

				int maxWidth = (this.vRight - this.vLeft) * 3;
				int maxHeight = (this.vBottom - this.vTop) * 3;

				int scaledWidth = (int) ((this.vRight - this.vLeft) * this.mCurrentScale);
				int scaledHeight = (int) ((this.vBottom - this.vTop) * this.mCurrentScale);
				int origWidth = this.vRight - this.vLeft;
				int origHeight = this.vBottom - this.vTop;

				int l = (int) (this.mRectMonitor.width() / 2 - (this.mRectMonitor
						.width() / 2 - this.mRectCanvas.left) * scale);
				int t = (int) (this.mRectMonitor.height() / 2 - (this.mRectMonitor
						.height() / 2 - this.mRectCanvas.top) * scale);
				int r = l + scaledWidth;
				int b = t + scaledHeight;

				if ((scaledWidth <= origWidth) || (scaledHeight <= origHeight)) {
					l = this.vLeft;
					t = this.vTop;
					r = this.vRight;
					b = this.vBottom;
				} else if ((scaledWidth >= maxWidth)
						|| (scaledHeight >= maxHeight)) {
					l = this.mRectCanvas.left;
					t = this.mRectCanvas.top;
					r = l + maxWidth;
					b = t + maxHeight;
				}

				this.mRectCanvas.set(l, t, r, b);

				System.out.println("zoom -> l: " + l + ", t: " + t + ", r: "
						+ r + ", b: " + b + ",  width: " + scaledWidth
						+ ", height: " + scaledHeight);
				this.mLastZoomTime = System.currentTimeMillis();
			}

			break;
		case 1:
		case 6:
			if (this.mCurrentScale != 1.0F)
				break;
			this.mPinchedMode = 0;

			break;
		case 3:
		case 4:
		default:
			return false;
		}

		return true;
	}

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if ((this.mRectCanvas.left != this.vLeft)
				|| (this.mRectCanvas.top != this.vTop)
				|| (this.mRectCanvas.right != this.vRight)
				|| (this.mRectCanvas.bottom != this.vBottom)) {
			return false;
		}
		System.out.println("velocityX: " + Math.abs(velocityX)
				+ ", velocityY: " + Math.abs(velocityY));

		if ((e1.getX() - e2.getX() > 100.0F) && (Math.abs(velocityX) > 0.0F)) {
			if ((this.mCamera != null) && (this.mAVChannel >= 0))
				this.mCamera.sendIOCtrl(this.mAVChannel, 4097,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent((byte) 6,
								(byte) 8, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
		} else if ((e2.getX() - e1.getX() > 100.0F)
				&& (Math.abs(velocityX) > 0.0F)) {
			if ((this.mCamera != null) && (this.mAVChannel >= 0))
				this.mCamera.sendIOCtrl(this.mAVChannel, 4097,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent((byte) 3,
								(byte) 8, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
		} else if ((e1.getY() - e2.getY() > 100.0F)
				&& (Math.abs(velocityY) > 0.0F)) {
			if ((this.mCamera != null) && (this.mAVChannel >= 0))
				this.mCamera.sendIOCtrl(this.mAVChannel, 4097,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent((byte) 2,
								(byte) 8, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
		} else if ((e2.getY() - e1.getY() > 100.0F)
				&& (Math.abs(velocityY) > 0.0F)) {
			if ((this.mCamera != null) && (this.mAVChannel >= 0)) {
				this.mCamera.sendIOCtrl(this.mAVChannel, 4097,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent((byte) 1,
								(byte) 8, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
			}
		}
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if ((MonitorSurfaceView.this.mCamera != null)
						&& (MonitorSurfaceView.this.mAVChannel >= 0))
					MonitorSurfaceView.this.mCamera.sendIOCtrl(
							MonitorSurfaceView.this.mAVChannel, 4097,
							AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(
									(byte) 0, (byte) 8, (byte) 0, (byte) 0,
									(byte) 0, (byte) 0));
			}
		}, 1500L);

		return false;
	}

	public void onLongPress(MotionEvent e) {
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return true;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	private void parseMidPoint(PointF point, float left, float top,
			float right, float bottom) {
		point.set((left + right) / 2.0F, (top + bottom) / 2.0F);
	}

	private class ThreadRender extends Thread {
		private boolean mIsRunningThread = false;
		private Object mWaitObjectForStopThread = new Object();

		private ThreadRender() {
		}

		public void stopThread() {
			this.mIsRunningThread = false;
			try {
				this.mWaitObjectForStopThread.notify();
			} catch (Exception localException) {
			}
		}

		public void run() {
			this.mIsRunningThread = true;
			Canvas videoCanvas = null;

			if ((!MonitorSurfaceView.this.mPaint.isDither())
					&& (MonitorSurfaceView.this.mEnableDither)) {
				LcLog.i("IOTCamera",
						"==== Enable Dithering ==== !!!This will decrease FPS.");
				MonitorSurfaceView.this.mPaint
						.setDither(MonitorSurfaceView.this.mEnableDither);
			} else if ((MonitorSurfaceView.this.mPaint.isDither())
					&& (!MonitorSurfaceView.this.mEnableDither)) {
				LcLog.i("IOTCamera", "==== Disable Dithering ====");
				MonitorSurfaceView.this.mPaint
						.setDither(MonitorSurfaceView.this.mEnableDither);
			}

			while (this.mIsRunningThread) {
				if ((MonitorSurfaceView.this.mLastFrame != null)
						&& (!MonitorSurfaceView.this.mLastFrame.isRecycled())) {
					try {
						videoCanvas = MonitorSurfaceView.this.mSurHolder
								.lockCanvas();

						if (videoCanvas != null) {
							videoCanvas.drawColor(R.color.black);
							videoCanvas.drawBitmap(
									MonitorSurfaceView.this.mLastFrame, null,
									MonitorSurfaceView.this.mRectCanvas,
									MonitorSurfaceView.this.mPaint);
						}
					} catch (Exception e) {
						LcLog.d("videoCanvas Error", "videoCanvas Error");
					} finally {
						if (videoCanvas != null)
							MonitorSurfaceView.this.mSurHolder
									.unlockCanvasAndPost(videoCanvas);
						videoCanvas = null;
					}
				}

				try {
					synchronized (this.mWaitObjectForStopThread) {
						this.mWaitObjectForStopThread.wait(33L);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			System.out.println("===ThreadRender exit===");
		}
	}
}
