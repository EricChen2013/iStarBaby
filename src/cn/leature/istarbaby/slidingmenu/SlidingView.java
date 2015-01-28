/**  
 * Copyright © 2014 Leature Technology Co., Ltd.. All rights reserved.
 *
 * @Title: SlidingView.java
 * @Prject: iStarBaby
 * @Package: cn.leature.istarbaby.slidingmenu
 * @Description: TODO
 * @author: Administrator  
 * @date: 2014-6-11 下午12:10:42
 * @version: V1.0  
 */
package cn.leature.istarbaby.slidingmenu;

/**
 * @ClassName: SlidingView
 * @Description: TODO
 * @author: Administrator
 * @date: 2014-6-11 下午12:10:42
 */

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class SlidingView extends ViewGroup {

	private FrameLayout mContainer;
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;

	private float mLastMotionX;
	private float mLastMotionY;
	private static final int SNAP_VELOCITY = 1000;
	private View mLeftView;

	private boolean mIsBeingClose = false;
	public boolean mC = false;
	public boolean isopen;

	public SlidingView(Context context) {
		super(context);
		init();
	}

	public SlidingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlidingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mContainer.measure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int width = r - l;
		final int height = b - t;
		mContainer.layout(0, 0, width, height);
	}

	private void init() {
		mContainer = new FrameLayout(getContext());
		mContainer.setBackgroundColor(0xff000000);
		mScroller = new Scroller(getContext());
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		super.addView(mContainer);
	}

	public void setView(View v) {
		if (mContainer.getChildCount() > 0) {
			mContainer.removeAllViews();
		}
		mContainer.addView(v);
	}

	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		invalidate();
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				if (oldX != x || oldY != y) {
					scrollTo(x, y);
				}
				// Keep on drawing until the animation has finished.
				invalidate();
			} else {
				clearChildrenCache();
			}
		} else {
			clearChildrenCache();
		}
	}

	private boolean mIsBeingDragged;

	/**
	 * 实现了ontouch的分发拦截
	 */

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (isopen) {
			requestDisallowInterceptTouchEvent(true);
		} else {
			requestDisallowInterceptTouchEvent(false);
		}

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mIsBeingDragged = false;

			if (getScrollX() == -getLeftMenuWidth()
					&& mLastMotionX > getLeftMenuWidth()) {
				// 左侧页面关闭
				mIsBeingClose = true;
				int dx = -getScrollX();
				smoothScrollTo(dx);
				clearChildrenCache();

				return true;

			}
			break;

		case MotionEvent.ACTION_MOVE:
			final float dx = x - mLastMotionX;
			final float xDiff = Math.abs(dx);
			final float yDiff = Math.abs(y - mLastMotionY);
			if (xDiff > mTouchSlop && xDiff > yDiff && dx > 0) {
				// 只能往右边拖动
				mIsBeingDragged = true;
				mLastMotionX = x;
			}

			break;
		default:
			break;
		}
		return mIsBeingDragged;
	}

	public void setmCtrue() {
		isopen = true;
	}

	public void setmCfalse() {
		isopen = false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:

			mLastMotionX = x;
			mLastMotionY = y;

			if (getScrollX() == -getLeftMenuWidth()
					&& mLastMotionX < getLeftMenuWidth()) {
				return false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mIsBeingDragged) {
				enableChildrenCache();
				final float deltaX = mLastMotionX - x;
				mLastMotionX = x;
				float oldScrollX = getScrollX();
				float scrollX = oldScrollX + deltaX;

				if (deltaX < 0) { // 往右拉的最大位置
					final float leftBound = 0;
					final float rightBound = -getLeftMenuWidth();
					if (scrollX > leftBound) {
						scrollX = leftBound;
					} else if (scrollX < rightBound) {
						scrollX = rightBound;
					}

				} else if (deltaX > 0) { // right view
					final float leftBound = 0;
					final float rightBound = -getLeftMenuWidth();
					if (scrollX > leftBound) {
						scrollX = leftBound;
					} else if (scrollX < rightBound) {
						scrollX = rightBound;
					}

				}

				scrollTo((int) scrollX, this.getScrollY());

			}
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		case MotionEvent.ACTION_UP:
			if (mIsBeingDragged) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000);
				int velocityX = (int) velocityTracker.getXVelocity();
				velocityX = 0;
				int oldScrollX = getScrollX();
				int dx = 0;
				if (oldScrollX < 0) {
					// 左边
					if (oldScrollX < -getLeftMenuWidth() / 2
							|| velocityX > SNAP_VELOCITY) {
						// 左侧页面划出
						dx = -getLeftMenuWidth() - oldScrollX;

					} else if (oldScrollX >= -getLeftMenuWidth() / 2
							|| velocityX < -SNAP_VELOCITY) {
						// 左侧页面关闭
						dx = -oldScrollX;
					}
				}
				smoothScrollTo(dx);
				clearChildrenCache();

			}
			break;
		}

		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
		return true;
	}

	private int getLeftMenuWidth() {
		if (mLeftView == null) {
			return 0;
		}
		return mLeftView.getWidth();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	public View getMenuView() {
		return mLeftView;
	}

	public void setLeftView(View mLeftView) {
		this.mLeftView = mLeftView;
	}

	/**
	 * 打开（关闭）左侧页面
	 */
	public void showLeftView() {
		if (mIsBeingClose) {
			mIsBeingClose = false;
			return;
		}

		int menuWidth = mLeftView.getWidth();
		int oldScrollX = getScrollX();
		if (oldScrollX == 0) {
			smoothScrollTo(-menuWidth);
		} else if (oldScrollX == -menuWidth) {
			smoothScrollTo(menuWidth);
		}
	}

	void smoothScrollTo(int dx) {
		int duration = 500;
		int oldScrollX = getScrollX();

		mScroller.startScroll(oldScrollX, getScrollY(), dx, getScrollY(),
				duration);
		invalidate();
		mIsBeingClose = false;
	}

	void enableChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(true);
		}
	}

	void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(false);
		}
	}

}
