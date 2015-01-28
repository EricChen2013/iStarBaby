package cn.leature.istarbaby.selecttime;

import android.view.View;
import cn.leature.istarbaby.R;

public class HourMain {

	private View view;

	private WheelView wv_hours;
	private WheelView wv_mins;
	public int screenheight;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public HourMain(View view) {
		super();
		this.view = view;
		setView(view);
	}

	/**
	 * 
	 * 弹出日期时间选择器
	 */
	public void initDateTimePicker(int h, int m) {

		wv_hours = (WheelView) view.findViewById(R.id.hour);
		wv_mins = (WheelView) view.findViewById(R.id.min);

		wv_hours.setVisibility(View.VISIBLE);
		wv_mins.setVisibility(View.VISIBLE);
		wv_hours.setAdapter(new NumericWheelAdapter(0, 23));
		wv_hours.setCyclic(true);// 可循环滚动
		wv_hours.setLabel("时");// 添加文字
		wv_hours.setCurrentItem(h);

		wv_mins.setAdapter(new NumericWheelAdapter(0, 59));
		wv_mins.setCyclic(true);// 可循环滚动
		wv_mins.setLabel("分");// 添加文字
		wv_mins.setCurrentItem(m);

		// 根据屏幕密度来指定选择器字体的大小
		int textSize = 0;
		textSize = (screenheight / 100) * 4;
		wv_hours.TEXT_SIZE = textSize;
		wv_mins.TEXT_SIZE = textSize;

	}

	public String getTime() {
		StringBuffer sb = new StringBuffer();
		if (wv_hours.getCurrentItem() < 10) {
			sb.append(0);
		}
		sb.append(wv_hours.getCurrentItem()).append(":");
		if (wv_mins.getCurrentItem() < 10) {
			sb.append(0);
		}
		sb.append(wv_mins.getCurrentItem());
		return sb.toString();
	}

	public void set_time_date() {
		wv_hours.set_time_date();
		wv_mins.set_time_date();

	}

}
