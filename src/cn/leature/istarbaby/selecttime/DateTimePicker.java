package cn.leature.istarbaby.selecttime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.leature.istarbaby.R;
import cn.leature.istarbaby.domain.ScreenInfo;

public class DateTimePicker
{
	private DateTimePicker()
	{
	}

	static DateTimePicker myTime = new DateTimePicker();
	private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	private DateFormat hourFormat = new SimpleDateFormat("kk:mm");
	private Dialog dialog;
	private Button cancel;
	private Button confirm;
	private HourMain hourMain;
	private WheelMain wheelMain;

	public static DateTimePicker settime()
	{

		return myTime;
	}

	// 选择日期
	// @SuppressWarnings("deprecation")
	public void getdate(final EditText text, Context context)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		final View timepickerview = inflater.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo((Activity) context);
		wheelMain = new WheelMain(timepickerview);

		wheelMain.screenheight = screenInfo.getHeight();
		String time = text.getText().toString();
		Calendar calendar = Calendar.getInstance();

		if (JudgeDate.isDate(time, "yyyy/MM/dd"))
		{
			try
			{

				calendar.setTime(dateFormat.parse(time));

			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year, month, day);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setCancelable(false);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(timepickerview);

		cancel = (Button) timepickerview.findViewById(R.id.cancel);// 以后再说
		confirm = (Button) timepickerview.findViewById(R.id.confirm);// 下载更新
		cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});

		confirm.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub

				text.setText(wheelMain.getTime());
				dialog.dismiss();

			}
		});

	}

	// 选择全部日期
	// @SuppressWarnings("deprecation")
	public void getAllDate(final EditText text, Context context)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		final View timepickerview = inflater.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo((Activity) context);
		wheelMain = new WheelMain(timepickerview);

		wheelMain.screenheight = screenInfo.getHeight();
		String time = text.getText().toString();
		Calendar calendar = Calendar.getInstance();

		if (JudgeDate.isDate(time, "yyyy/MM/dd"))
		{
			try
			{

				calendar.setTime(dateFormat.parse(time));

			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year, month, day);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setCancelable(false);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(timepickerview);

		cancel = (Button) timepickerview.findViewById(R.id.cancel);// 以后再说
		confirm = (Button) timepickerview.findViewById(R.id.confirm);// 下载更新
		cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});

		confirm.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub

				text.setText(wheelMain.getalltime());
				dialog.dismiss();

			}
		});

	}

	// 选择时间
	public void gethourtime(final EditText text, Context content)
	{

		LayoutInflater inflater = LayoutInflater.from(content);
		View hourview = inflater.inflate(R.layout.timehourpicker, null);
		ScreenInfo screenInfo = new ScreenInfo((Activity) content);
		hourMain = new HourMain(hourview);
		hourMain.screenheight = screenInfo.getHeight();
		String time = text.getText().toString();
		Calendar calendar = Calendar.getInstance();
		if (JudgeDate.isDate(time, "kk:mm"))
		{
			try
			{
				calendar.setTime(hourFormat.parse(time));
			}
			catch (ParseException e)
			{

				e.printStackTrace();
			}
		}
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		hourMain.initDateTimePicker(hour, min);

		hourMain.set_time_date();
		new AlertDialog.Builder(content).setView(hourview)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						text.setText(hourMain.getTime());

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
					}
				}).show();
	}

	public void getTextDate(final TextView textView, Context context)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		final View timepickerview = inflater.inflate(R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo((Activity) context);
		wheelMain = new WheelMain(timepickerview);
		wheelMain.screenheight = screenInfo.getHeight();
		String time = textView.getText().toString();
		Calendar calendar = Calendar.getInstance();

		if (JudgeDate.isDate(time, "yyyy/MM/dd"))
		{
			try
			{

				calendar.setTime(dateFormat.parse(time));

			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year, month, day);

		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setCancelable(false);
		dialog = builder.create();
		dialog.show();
		dialog.setContentView(timepickerview);

		cancel = (Button) timepickerview.findViewById(R.id.cancel);// 以后再说
		confirm = (Button) timepickerview.findViewById(R.id.confirm);// 下载更新
		cancel.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});

		confirm.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				textView.setText(wheelMain.getTime());
				dialog.dismiss();

			}
		});
	}

}
