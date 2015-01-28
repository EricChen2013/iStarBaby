package cn.leature.istarbaby.selecttime;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import cn.leature.istarbaby.R;

public class WheelView extends View {

	private static final int SCROLLING_DURATION = 400;

	private static final int MIN_DELTA_FOR_SCROLLING = 1;

	private static final int[] SHADOWS_COLORS = new int[] { 0xFF111111,
			0x00AAAAAA, 0x00AAAAAA };

	private static final int ADDITIONAL_ITEM_HEIGHT = 15;

	public int TEXT_SIZE;

	private final int ITEM_OFFSET = TEXT_SIZE / 5;

	private static final int ADDITIONAL_ITEMS_SPACE = 10;

	private static final int LABEL_OFFSET = 8;

	private static final int PADDING = 10;

	private static final int DEF_VISIBLE_ITEMS = 5;

	private WheelAdapter adapter = null;
	private int currentItem = 0;

	private int itemsWidth = 0;
	private int labelWidth = 0;

	private int visibleItems = DEF_VISIBLE_ITEMS;

	private int itemHeight = 0;
	private TextPaint itemsPaint;
	private TextPaint valuePaint;

	private StaticLayout itemsLayout;
	private StaticLayout itemsLayout_current; // 画笔颜色,显示今天的日期，蓝色
	private StaticLayout itemsLayout_Later_year; // 画笔颜色，显示未来日期，灰色
	private StaticLayout labelLayout;
	private StaticLayout valueLayout;
	private boolean day_month;
	private boolean year_year = false;
	private int Later_year = 0;
	private int Later_month;
	private int Later_day;
	private String label;
	private Drawable centerDrawable;

	private GradientDrawable topShadow;
	private GradientDrawable bottomShadow;

	private boolean isScrollingPerformed;
	private int scrollingOffset;
	private static int times = 0;
	private GestureDetector gestureDetector;
	private Scroller scroller;
	private int lastScrollY;
	boolean isCyclic = false;

	private Boolean time_date = false;
	private List<OnWheelChangedListener> changingListeners = new LinkedList<OnWheelChangedListener>();
	private List<OnWheelScrollListener> scrollingListeners = new LinkedList<OnWheelScrollListener>();

	public WheelView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initData(context);
	}

	public WheelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initData(context);
	}

	public WheelView(Context context) {
		super(context);
		initData(context);
	}

	private void initData(Context context) {
		gestureDetector = new GestureDetector(context, gestureListener);
		gestureDetector.setIsLongpressEnabled(false);

		scroller = new Scroller(context);
	}

	public WheelAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(WheelAdapter adapter) {
		this.adapter = adapter;
		invalidateLayouts();
		invalidate();
	}

	public void setInterpolator(Interpolator interpolator) {
		scroller.forceFinished(true);
		scroller = new Scroller(getContext(), interpolator);
	}

	public int getVisibleItems() {
		return visibleItems;
	}

	public void setVisibleItems(int count) {
		visibleItems = count;
		invalidate();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String newLabel) {
		if (label == null || !label.equals(newLabel)) {
			label = newLabel;
			labelLayout = null;
			invalidate();
		}
	}

	public void addChangingListener(OnWheelChangedListener listener) {
		changingListeners.add(listener);
	}

	public void removeChangingListener(OnWheelChangedListener listener) {
		changingListeners.remove(listener);
	}

	protected void notifyChangingListeners(int oldValue, int newValue) {
		for (OnWheelChangedListener listener : changingListeners) {
			listener.onChanged(this, oldValue, newValue);
		}
	}

	public void addScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.add(listener);
	}

	public void removeScrollingListener(OnWheelScrollListener listener) {
		scrollingListeners.remove(listener);
	}

	protected void notifyScrollingListenersAboutStart() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingStarted(this);
		}
	}

	protected void notifyScrollingListenersAboutEnd() {
		for (OnWheelScrollListener listener : scrollingListeners) {
			listener.onScrollingFinished(this);
		}
	}

	public int getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(int index, boolean animated) {
		if (adapter == null || adapter.getItemsCount() == 0) {
			return;
		}
		if (index < 0 || index >= adapter.getItemsCount()) {
			if (isCyclic) {
				while (index < 0) {
					index += adapter.getItemsCount();

				}
				index %= adapter.getItemsCount();
			} else {
				return;
			}
		}
		if (index != currentItem) {
			if (animated) {
				scroll(index - currentItem, SCROLLING_DURATION);
			} else {
				invalidateLayouts();

				int old = currentItem;
				currentItem = index;

				notifyChangingListeners(old, currentItem);

				invalidate();
			}
		}
	}

	public void setCurrentItem(int index) {
		setCurrentItem(index, false);
	}

	public boolean isCyclic() {
		return isCyclic;
	}

	public void setCyclic(boolean isCyclic) {
		this.isCyclic = isCyclic;

		invalidate();
		invalidateLayouts();
	}

	private void invalidateLayouts() {
		itemsLayout_current = null;
		itemsLayout_Later_year = null;
		itemsLayout = null;
		valueLayout = null;
		scrollingOffset = 0;
	}

	private void initResourcesIfNecessary() {
		if (itemsPaint == null) {
			itemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
					| Paint.FAKE_BOLD_TEXT_FLAG);

			itemsPaint.setTextSize(TEXT_SIZE);
		}

		if (valuePaint == null) {
			valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG
					| Paint.FAKE_BOLD_TEXT_FLAG | Paint.DITHER_FLAG);

			valuePaint.setTextSize(TEXT_SIZE);
			valuePaint.setShadowLayer(0.1f, 0, 0.1f, 0xFFC0C0C0);
		}

		if (centerDrawable == null) {
			centerDrawable = getContext().getResources().getDrawable(
					R.drawable.wheel_val);
		}

		if (topShadow == null) {
			topShadow = new GradientDrawable(Orientation.TOP_BOTTOM,
					SHADOWS_COLORS);
		}

		if (bottomShadow == null) {
			bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP,
					SHADOWS_COLORS);
		}

		setBackgroundResource(R.drawable.wheel_bg);
	}

	private int getDesiredHeight(Layout layout) {
		if (layout == null) {
			return 0;
		}

		int desired = getItemHeight() * visibleItems - ITEM_OFFSET * 2
				- ADDITIONAL_ITEM_HEIGHT;

		desired = Math.max(desired, getSuggestedMinimumHeight());

		return desired;
	}

	private String getTextItem(int index) {
		if (adapter == null || adapter.getItemsCount() == 0) {
			return null;
		}
		int count = adapter.getItemsCount();
		if ((index < 0 || index >= count) && !isCyclic) {
			return null;
		} else {
			while (index < 0) {
				index = count + index;
			}
		}

		index %= count;
		return adapter.getItem(index);
	}

	private String buildText(boolean useCurrentValue) // 创建对话框的文字，显示黑色文字部分
	{

		StringBuilder itemsText = new StringBuilder();
		int addItems = visibleItems / 2 + 1;
		for (int i = currentItem - addItems; i <= currentItem + addItems; i++) {

			// 添加大小月月份并将其转换为list,方便之后的判断
			if (useCurrentValue || i != currentItem) {
				String text = getTextItem(i);

				if (text != null) {
					itemsText.append(text);

				}

			}
			if (i < currentItem + addItems) {
				itemsText.append("\n");
			}
		}

		return itemsText.toString();
	}

	private String buildText_Later_year(boolean useCurrentValue) // 画出未来的日期
	{
		StringBuilder itemsText = new StringBuilder();
		int addItems = visibleItems / 2 + 1;

		for (int i = currentItem - addItems; i <= currentItem + addItems; i++) {

			adapter.getItem(currentItem);
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			int day = calendar.get(Calendar.DATE);
			// 添加大小月月份并将其转换为list,方便之后的判断
			if (useCurrentValue || i != currentItem) {
				String text = getTextItem(i);
				int result = Integer.parseInt(text);
				if (Later_year == 1) // 当年份大于今年时
				{
					if (year < result
							|| (month != result && day_month == false && year_year == false)
							|| (day != result && day_month == true)) // 将年月日分开
					{
						if (text != null) {
							itemsText.append(text);

						}
					}
				}
				if (Later_year == 0) // 当年份等于今年时
				{
					// 月份大于当前月份 Later_month == 1
					if ((Later_month == 1)
							&& (year < result
									|| ((month < result) && day_month == false && year_year == false) || (day_month == true && day != result))) {
						itemsText.append(text);
					}
					// 月份等于当前月份 Later_month == 0 day_month 用于 区分开 text中的月和日
					if ((Later_month == 0)
							&& (year < result
									|| ((month < result) && day_month == false && year_year == false) || (day < result && day_month == true))) {

						itemsText.append(text);

					}
					if ((Later_month == -1)
							&& (year < result || ((month < result)
									&& day_month == false && year_year == false))) {
						itemsText.append(text);
					}

				}

				if (Later_year == -1 && year < result) {
					itemsText.append(text);
				}

			}
			if (i < currentItem + addItems) {
				itemsText.append("\n");
			}
		}

		return itemsText.toString();
	}

	private String buildText_current(boolean useCurrentValue) // 画出今天的日期
	{
		StringBuilder itemsText = new StringBuilder();
		int addItems = visibleItems / 2 + 1;
		times++;
		Log.i("times", times + "");
		for (int i = currentItem - addItems; i <= currentItem + addItems; i++) {

			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			int day = calendar.get(Calendar.DATE);
			// 添加大小月月份并将其转换为list,方便之后的判断
			if (useCurrentValue || i != currentItem) {
				String text = getTextItem(i);
				if (text.equals(year + "")
						|| (text.equals(month + "") && day_month == false)
						|| (text.equals(day + "") && day_month == true)) // 判断今天的日期
				{
					if (text != null) {
						itemsText.append(text);

					}
				}

			}
			if (i < currentItem + addItems) {
				itemsText.append("\n");
			}
		}

		return itemsText.toString();
	}

	private int getMaxTextLength() {
		WheelAdapter adapter = getAdapter();
		if (adapter == null) {
			return 0;
		}

		int adapterLength = adapter.getMaximumLength();
		if (adapterLength > 0) {
			return adapterLength;
		}

		String maxText = null;
		int addItems = visibleItems / 2;
		for (int i = Math.max(currentItem - addItems, 0); i < Math.min(
				currentItem + visibleItems, adapter.getItemsCount()); i++) {
			String text = adapter.getItem(i);
			if (text != null
					&& (maxText == null || maxText.length() < text.length())) {
				maxText = text;
			}
		}

		return maxText != null ? maxText.length() : 0;
	}

	private int getItemHeight() {
		if (itemHeight != 0) {
			return itemHeight;
		} else if (itemsLayout != null && itemsLayout.getLineCount() > 2) {
			itemHeight = itemsLayout.getLineTop(2) - itemsLayout.getLineTop(1);
			return itemHeight;
		}

		return getHeight() / visibleItems;
	}

	private int calculateLayoutWidth(int widthSize, int mode) {
		initResourcesIfNecessary();

		int width = widthSize;

		int maxLength = getMaxTextLength();
		if (maxLength > 0) {
			float textWidth = FloatMath.ceil(Layout.getDesiredWidth("0",
					itemsPaint));
			itemsWidth = (int) (maxLength * textWidth);
		} else {
			itemsWidth = 0;
		}
		itemsWidth += ADDITIONAL_ITEMS_SPACE;

		labelWidth = 0;
		if (label != null && label.length() > 0) {
			labelWidth = (int) FloatMath.ceil(Layout.getDesiredWidth(label,
					valuePaint));
		}

		boolean recalculate = false;
		if (mode == MeasureSpec.EXACTLY) {
			width = widthSize;
			recalculate = true;
		} else {
			width = itemsWidth + labelWidth + 2 * PADDING;
			if (labelWidth > 0) {
				width += LABEL_OFFSET;
			}

			width = Math.max(width, getSuggestedMinimumWidth());

			if (mode == MeasureSpec.AT_MOST && widthSize < width) {
				width = widthSize;
				recalculate = true;
			}
		}

		if (recalculate) {

			int pureWidth = width - LABEL_OFFSET - 2 * PADDING;
			if (pureWidth <= 0) {
				itemsWidth = labelWidth = 0;
			}
			if (labelWidth > 0) {
				double newWidthItems = (double) itemsWidth * pureWidth
						/ (itemsWidth + labelWidth);
				itemsWidth = (int) newWidthItems;
				labelWidth = pureWidth - itemsWidth;
			} else {
				itemsWidth = pureWidth + LABEL_OFFSET;
			}
		}

		if (itemsWidth > 0) {
			createLayouts(itemsWidth, labelWidth);
		}

		return width;
	}

	private void createLayouts(int widthItems, int widthLabel) {
		if (itemsLayout_current == null
				|| itemsLayout_current.getWidth() > widthItems) {
			itemsLayout_current = new StaticLayout(
					buildText_current(isScrollingPerformed), itemsPaint,
					widthItems,
					widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE
							: Layout.Alignment.ALIGN_CENTER, 1,
					ADDITIONAL_ITEM_HEIGHT, false);
		} else {
			itemsLayout_current.increaseWidthTo(widthItems);
		}

		itemsLayout_Later_year = new StaticLayout(
				buildText_Later_year(isScrollingPerformed), itemsPaint,
				widthItems, widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE
						: Layout.Alignment.ALIGN_CENTER, 1,
				ADDITIONAL_ITEM_HEIGHT, false);

		if (itemsLayout == null || itemsLayout.getWidth() > widthItems) {
			itemsLayout = new StaticLayout(buildText(isScrollingPerformed),
					itemsPaint, widthItems,
					widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE
							: Layout.Alignment.ALIGN_CENTER, 1,
					ADDITIONAL_ITEM_HEIGHT, false);
		} else {
			itemsLayout.increaseWidthTo(widthItems);
		}

		if (!isScrollingPerformed
				&& (valueLayout == null || valueLayout.getWidth() > widthItems)) {
			String text = getAdapter() != null ? getAdapter().getItem(
					currentItem) : null;
			valueLayout = new StaticLayout(text != null ? text : "",
					valuePaint, widthItems,
					widthLabel > 0 ? Layout.Alignment.ALIGN_OPPOSITE
							: Layout.Alignment.ALIGN_CENTER, 1,
					ADDITIONAL_ITEM_HEIGHT, false);
		} else if (isScrollingPerformed) {
			valueLayout = null;
		} else {
			valueLayout.increaseWidthTo(widthItems);
		}

		if (widthLabel > 0) {
			if (labelLayout == null || labelLayout.getWidth() > widthLabel) {
				labelLayout = new StaticLayout(label, valuePaint, widthLabel,
						Layout.Alignment.ALIGN_NORMAL, 1,
						ADDITIONAL_ITEM_HEIGHT, false);
			} else {
				labelLayout.increaseWidthTo(widthLabel);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width = calculateLayoutWidth(widthSize, widthMode);

		int height;
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = getDesiredHeight(itemsLayout);

			if (heightMode == MeasureSpec.AT_MOST) {
				height = Math.min(height, heightSize);
			}
		}

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (itemsLayout == null) {
			if (itemsWidth == 0) {
				calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
			} else {
				createLayouts(itemsWidth, labelWidth);
			}
		}

		if (itemsWidth > 0) {
			canvas.save();

			canvas.translate(PADDING, -ITEM_OFFSET);
			drawItems(canvas);
			drawValue(canvas);
			if (!time_date) {
				drawItems_current(canvas); // 画今天日期
				drawItems_Later_year(canvas); // 话未来日期
			}
			canvas.restore();
			int addItems = visibleItems / 2 + 1;

		}

		drawCenterRect(canvas);
		drawShadows(canvas);

	}

	private void drawShadows(Canvas canvas) {
		topShadow.setBounds(0, 0, getWidth(), getHeight() / visibleItems);
		topShadow.draw(canvas);

		bottomShadow.setBounds(0, getHeight() - getHeight() / visibleItems,
				getWidth(), getHeight());
		bottomShadow.draw(canvas);

	}

	private void drawValue(Canvas canvas) {
		valuePaint.setColor(Color.RED);
		valuePaint.drawableState = getDrawableState();

		Rect bounds = new Rect();
		itemsLayout.getLineBounds(visibleItems / 2, bounds);

		if (labelLayout != null) {
			canvas.save();
			canvas.translate(itemsLayout.getWidth() + LABEL_OFFSET, bounds.top);
			labelLayout.draw(canvas);
			canvas.restore();
		}

		if (valueLayout != null) {
			canvas.save();
			canvas.translate(0, bounds.top + scrollingOffset);
			valueLayout.draw(canvas);
			canvas.restore();
		}
	}

	private void drawItems_current(Canvas canvas) // 画今天日期
	{
		canvas.save();

		int top = itemsLayout_current.getLineTop(1);
		canvas.translate(0, -top + scrollingOffset);

		itemsPaint.setColor(Color.BLUE);
		itemsPaint.drawableState = getDrawableState();
		itemsLayout_current.draw(canvas);

		canvas.restore();
	}

	private void drawItems_Later_year(Canvas canvas) // 画未来日期
	{
		canvas.save();

		int top = itemsLayout_Later_year.getLineTop(1);
		canvas.translate(0, -top + scrollingOffset);

		itemsPaint.setColor(Color.GRAY);
		itemsPaint.drawableState = getDrawableState();
		itemsLayout_Later_year.draw(canvas);

		canvas.restore();
	}

	private void drawItems(Canvas canvas) {
		canvas.save();

		int top = itemsLayout.getLineTop(1);
		canvas.translate(0, -top + scrollingOffset);

		itemsPaint.setColor(Color.BLACK);
		itemsPaint.drawableState = getDrawableState();
		itemsLayout.draw(canvas);

		canvas.restore();
	}

	private void drawCenterRect(Canvas canvas) {
		int center = getHeight() / 2;
		int offset = getItemHeight() / 2;
		centerDrawable.setBounds(0, center - offset, getWidth(), center
				+ offset);
		centerDrawable.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		WheelAdapter adapter = getAdapter();
		if (adapter == null) {
			return true;
		}

		if (!gestureDetector.onTouchEvent(event)
				&& event.getAction() == MotionEvent.ACTION_UP) {
			justify();
		}
		return true;
	}

	private void doScroll(int delta) {
		scrollingOffset += delta;

		int count = scrollingOffset / getItemHeight();
		int pos = currentItem - count;
		if (isCyclic && adapter.getItemsCount() > 0) {

			while (pos < 0) {
				pos += adapter.getItemsCount();
			}
			pos %= adapter.getItemsCount();
		} else if (isScrollingPerformed) {

			if (pos < 0) {
				count = currentItem;
				pos = 0;
			} else if (pos >= adapter.getItemsCount()) {
				count = currentItem - adapter.getItemsCount() + 1;
				pos = adapter.getItemsCount() - 1;
			}
		} else {

			pos = Math.max(pos, 0);
			pos = Math.min(pos, adapter.getItemsCount() - 1);
		}

		int offset = scrollingOffset;
		if (pos != currentItem) {
			setCurrentItem(pos, false);
		} else {
			invalidate();
		}

		scrollingOffset = offset - count * getItemHeight();
		if (scrollingOffset > getHeight()) {
			scrollingOffset = scrollingOffset % getHeight() + getHeight();
		}
	}

	private SimpleOnGestureListener gestureListener = new SimpleOnGestureListener() {
		public boolean onDown(MotionEvent e) {
			if (isScrollingPerformed) {
				scroller.forceFinished(true);
				clearMessages();
				return true;
			}
			return false;
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			startScrolling();
			doScroll((int) -distanceY);
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			lastScrollY = currentItem * getItemHeight() + scrollingOffset;
			int maxY = isCyclic ? 0x7FFFFFFF : adapter.getItemsCount()
					* getItemHeight();
			int minY = isCyclic ? -maxY : 0;
			scroller.fling(0, lastScrollY, 0, (int) -velocityY / 2, 0, 0, minY,
					maxY);
			setNextMessage(MESSAGE_SCROLL);
			return true;
		}
	};

	private final int MESSAGE_SCROLL = 0;
	private final int MESSAGE_JUSTIFY = 1;

	private void setNextMessage(int message) {
		clearMessages();
		animationHandler.sendEmptyMessage(message);
	}

	private void clearMessages() {
		animationHandler.removeMessages(MESSAGE_SCROLL);
		animationHandler.removeMessages(MESSAGE_JUSTIFY);
	}

	private Handler animationHandler = new Handler() {
		public void handleMessage(Message msg) {
			scroller.computeScrollOffset();
			int currY = scroller.getCurrY();
			int delta = lastScrollY - currY;
			lastScrollY = currY;
			if (delta != 0) {
				doScroll(delta);
			}

			if (Math.abs(currY - scroller.getFinalY()) < MIN_DELTA_FOR_SCROLLING) {
				currY = scroller.getFinalY();
				scroller.forceFinished(true);
			}
			if (!scroller.isFinished()) {
				animationHandler.sendEmptyMessage(msg.what);
			} else if (msg.what == MESSAGE_SCROLL) {
				justify();
			} else {
				finishScrolling();
			}
		}
	};

	private void justify() {
		if (adapter == null) {
			return;
		}

		lastScrollY = 0;
		int offset = scrollingOffset;
		int itemHeight = getItemHeight();
		boolean needToIncrease = offset > 0 ? currentItem < adapter
				.getItemsCount() : currentItem > 0;
		if ((isCyclic || needToIncrease)
				&& Math.abs((float) offset) > (float) itemHeight / 2) {
			if (offset < 0)
				offset += itemHeight + MIN_DELTA_FOR_SCROLLING;
			else
				offset -= itemHeight + MIN_DELTA_FOR_SCROLLING;
		}
		if (Math.abs(offset) > MIN_DELTA_FOR_SCROLLING) {
			scroller.startScroll(0, 0, 0, offset, SCROLLING_DURATION);
			setNextMessage(MESSAGE_JUSTIFY);
		} else {
			finishScrolling();
		}
	}

	private void startScrolling() {
		if (!isScrollingPerformed) {
			isScrollingPerformed = true;
			notifyScrollingListenersAboutStart();
		}
	}

	void finishScrolling() {
		if (isScrollingPerformed) {
			notifyScrollingListenersAboutEnd();
			isScrollingPerformed = false;
		}
		invalidateLayouts();
		invalidate();
	}

	public void scroll(int itemsToScroll, int time) {
		scroller.forceFinished(true);

		lastScrollY = scrollingOffset;
		int offset = itemsToScroll * getItemHeight();

		scroller.startScroll(0, lastScrollY, 0, offset - lastScrollY, time);
		setNextMessage(MESSAGE_SCROLL);

		startScrolling();
	}

	public void setYear_true() // 设置年记号 表明 初始化的是年的 wheelview
	{
		year_year = true;
	}

	public void setDay() // 设置月记号
	{
		day_month = true;
	}

	public void setMonth() {
		day_month = false;
	}

	public void set_Later_month(int month) // 设置月是否大于当前月份
	{
		Later_month = month;
	}

	public void set_Later_year(int year) {
		Later_year = year;
	}

	public void set_Later_day(int day) // 设置日是否大于当前日
	{
		Later_day = day;
	}

	public void set_time_date() {
		time_date = true;
	}

}
