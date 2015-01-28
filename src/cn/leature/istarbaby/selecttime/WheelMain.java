package cn.leature.istarbaby.selecttime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.util.Log;
import android.view.View;
import cn.leature.istarbaby.R;

public class WheelMain
{

	private View view;
	private static WheelView wv_year;
	private static WheelView wv_month;
	private static WheelView wv_day;

	private static int current_year; // 点击对话框后 返回的 当前日期
	private static int current_month;
	private static int current_day;

	public int screenheight;
	private static int START_YEAR = 1980, END_YEAR = 2050;
	OnWheelChangedListener wheelListener_year;
	OnWheelChangedListener wheelListener_month;

	public View getView()
	{
		return view;
	}

	public void setView(View view)
	{
		this.view = view;
	}

	public static int getSTART_YEAR()
	{
		return START_YEAR;
	}

	public static void setSTART_YEAR(int sTART_YEAR)
	{
		START_YEAR = sTART_YEAR;
	}

	public static int getEND_YEAR()
	{
		return END_YEAR;
	}

	public static void setEND_YEAR(int eND_YEAR)
	{
		END_YEAR = eND_YEAR;
	}

	public WheelMain(View view)
	{
		super();
		this.view = view;
		setView(view);
	}

	/**
	 * 
	 * 弹出日期时间选择器
	 */
	public void initDateTimePicker(int year, int month, int day)
	{
		// 添加大小月月份并将其转换为list,方便之后的判断

		Log.e("initDateTimePicker", "initDateTimePicker" + year + month + day);
		String[] months_big =
		{ "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little =
		{ "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 年
		wv_year = (WheelView) view.findViewById(R.id.year);
		wv_year.setYear_true();
		wv_year.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据

		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
		Log.e("initDateTimePicker", "setCurrentItem" + START_YEAR);

		// 月
		wv_month = (WheelView) view.findViewById(R.id.month);

		wv_month.setAdapter(new NumericWheelAdapter(1, 12));
		wv_month.setMonth();
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month);
		Log.e("initDateTimePicker", "setCurrentItem" + START_YEAR);

		// 日
		wv_day = (WheelView) view.findViewById(R.id.day);
		wv_day.setDay();
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1)))
		{
			wv_day.setAdapter(new NumericWheelAdapter(1, 31));
		}
		else if (list_little.contains(String.valueOf(month + 1)))
		{
			wv_day.setAdapter(new NumericWheelAdapter(1, 30));
		}
		else
		{
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdapter(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdapter(1, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);

		Calendar calendar = Calendar.getInstance();
		int year_status = calendar.get(Calendar.YEAR);
		int month_status = calendar.get(Calendar.MONTH);
		// 对点击edittext初始化，判断年月是否大于当前的
		if (year > year_status)
		{
			wv_year.set_Later_year(1);
			wv_month.set_Later_year(1);
			wv_day.set_Later_year(1);
		}
		if (year == year_status)
		{
			wv_year.set_Later_year(0);
			wv_month.set_Later_year(0);
			wv_day.set_Later_year(0);
		}
		if (year < year_status)
		{
			wv_year.set_Later_year(-1);
			wv_month.set_Later_year(-1);
			wv_day.set_Later_year(-1);
		}
		if (month > month_status)
		{
			wv_day.set_Later_month(1);
			wv_month.set_Later_month(1);
			wv_year.set_Later_month(1);
		}
		if (month == month_status)
		{
			wv_day.set_Later_month(0);
			wv_month.set_Later_month(0);
			wv_year.set_Later_month(0);
		}
		if (month < month_status)
		{
			wv_day.set_Later_month(-1);
			wv_month.set_Later_month(-1);
			wv_year.set_Later_month(-1);
		}

		// 添加"年"监听
		wheelListener_year = new OnWheelChangedListener()
		{
			public void onChanged(WheelView wheel, int oldValue, int newValue)
			{
				int year_num = newValue + START_YEAR;
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				// 事件监听后，判断年是否大于当前的
				if (year_num > year)
				{
					wv_year.set_Later_year(1);
					wv_month.set_Later_year(1);
					wv_day.set_Later_year(1);
				}
				if (year_num == year)
				{
					wv_year.set_Later_year(0);
					wv_month.set_Later_year(0);
					wv_day.set_Later_year(0);
				}
				if (year_num < year)
				{
					wv_year.set_Later_year(-1);
					wv_month.set_Later_year(-1);
					wv_day.set_Later_year(-1);
				}

				wv_month.setAdapter(new NumericWheelAdapter(1, 12));
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big
						.contains(String.valueOf(wv_month.getCurrentItem() + 1)))
				{
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				}
				else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1)))
				{
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				}
				else
				{
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		// 添加"月"监听
		wheelListener_month = new OnWheelChangedListener()
		{
			public void onChanged(WheelView wheel, int oldValue, int newValue)
			{
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				Calendar calendar = Calendar.getInstance();
				int month = calendar.get(Calendar.MONTH) + 1;
				// 事件监听后，判断年是否大于当前的
				if (month_num > month)
				{
					wv_day.set_Later_month(1);
					wv_month.set_Later_month(1);
					wv_year.set_Later_month(1);
				}
				if (month_num == month)
				{
					wv_day.set_Later_month(0);
					wv_month.set_Later_month(0);
					wv_year.set_Later_month(0);
				}
				if (month_num < month)
				{
					wv_day.set_Later_month(-1);
					wv_month.set_Later_month(-1);
					wv_year.set_Later_month(-1);
				}

				if (list_big.contains(String.valueOf(month_num)))
				{
					wv_day.setAdapter(new NumericWheelAdapter(1, 31));
				}
				else if (list_little.contains(String.valueOf(month_num)))
				{
					wv_day.setAdapter(new NumericWheelAdapter(1, 30));
				}
				else
				{
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdapter(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdapter(1, 28));
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);

		// 根据屏幕密度来指定选择器字体的大小
		int textSize;

		textSize = (screenheight / 100) * 4;
		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

	}

	// 如果月份跟日期小于10 则在前面补0
	public static StringBuffer setcurrentTime(StringBuffer sb)
	{

		sb.append(current_year).append("/");
		if (current_month < 10)
		{
			sb.append("0");
		}

		sb.append(current_month).append("/");
		if (current_day < 10)
		{
			sb.append("0");
		}
		sb.append(current_day);
		return sb;
	}

	public static String getalltime()
	{
		StringBuffer sb = new StringBuffer();

		Calendar c = Calendar.getInstance();

		current_day = c.get(Calendar.DAY_OF_MONTH);
		current_month = c.get(Calendar.MONTH) + 1;
		current_year = c.get(Calendar.YEAR);
		sb.append((wv_year.getCurrentItem() + START_YEAR)).append("/");
		if ((wv_month.getCurrentItem() + 1) < 10)
		{
			sb.append("0");
		}

		sb.append((wv_month.getCurrentItem() + 1)).append("/");
		if ((wv_day.getCurrentItem() + 1) < 10)
		{
			sb.append("0");
		}
		sb.append((wv_day.getCurrentItem() + 1));
		return sb.toString();
	}

	public static String getTime()
	{
		StringBuffer sb = new StringBuffer();

		Calendar c = Calendar.getInstance();

		current_day = c.get(Calendar.DAY_OF_MONTH);
		current_month = c.get(Calendar.MONTH) + 1;
		current_year = c.get(Calendar.YEAR);

		if (current_year < (wv_year.getCurrentItem() + START_YEAR)
				|| (current_year == (wv_year.getCurrentItem() + START_YEAR) && current_month < (wv_month
						.getCurrentItem() + 1))
				|| (current_year == (wv_year.getCurrentItem() + START_YEAR)
						&& current_month == (wv_month.getCurrentItem() + 1) && current_day < (wv_day
						.getCurrentItem() + 1)))
		{
			setcurrentTime(sb);
		}
		else
		{
			sb.append((wv_year.getCurrentItem() + START_YEAR)).append("/");
			if ((wv_month.getCurrentItem() + 1) < 10)
			{
				sb.append("0");
			}
			sb.append((wv_month.getCurrentItem() + 1)).append("/");
			if ((wv_day.getCurrentItem() + 1) < 10)
			{
				sb.append("0");
			}
			sb.append((wv_day.getCurrentItem() + 1));

		}

		return sb.toString();
	}

}
