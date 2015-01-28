package cn.leature.istarbaby.monitor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.SAvEvent;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlListEventResp;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.selecttime.DateTimePicker;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LcLog;
import cn.leature.istarbaby.utils.LzViewHolder;
import cn.leature.istarbaby.view.XListView;
import cn.leature.istarbaby.view.XListView.IXListViewListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MonitorEventDetailActivity extends Activity implements
		OnClickListener, IRegisterMonitorListener, IXListViewListener,
		OnItemClickListener {
	private final String TAG = "MonitorEventItemActivity";
	private XListView mItemListview;
	private ImageButton titleBarBack, mTitleBarDone;
	private TextView titleBarTitle;

	private int mAVChannel;
	private MyMonitor mMonitorClient;
	private MonitorShareModel mShareMonitor = null;
	private Dialog mDialog, mColokDialog;
	List<AVIOCTRLDEFs.SAvEvent> mEventsData;
	private LzProgressDialog mProgressDialog;
	private TextView mText_monitor_start;

	private EditText mColck_start_date;
	private EditText mColck_start_hour;
	private EditText mColck_end_date;
	private EditText mColck_end_hour;
	private TextView mText_monitor_end;
	// 显示更新时间
	SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	private Intent mIntent;
	private Bundle mBundle;
	boolean isListRefreshing = false;
	private long mStartDate;
	private long mEndDate;
	private String mEventTypeString;
	private String mSelectedEvent = "";
	private itemAdapter mItemAdapter;

	private int[] simleimage = new int[] { R.drawable.search_eventview,
			R.drawable.search_eventview, R.drawable.search_eventview,
			R.drawable.search_eventview, R.drawable.search_eventview };

	private String[] simlename = new String[] { "一个小时", "半天", "一天", "一周", "自定义" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_event_detail);

		mIntent = this.getIntent();
		mBundle = mIntent.getExtras();

		initUI();
		setTitleBar();

		mProgressDialog = new LzProgressDialog(this);

		mShareMonitor = MonitorShareModel.getInstance();
		mMonitorClient = mShareMonitor.getCurrentMonitor();

		mAVChannel = mMonitorClient.getChannel();

		getcalendartime(-24);
	}

	private void getcalendartime(int time) {
		// 取得当前 事件列表
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		mEndDate = calendar.getTimeInMillis();

		calendar.add(Calendar.HOUR, time);
		mStartDate = calendar.getTimeInMillis();

		// 请求数据
		requestEventDetail(mStartDate, mEndDate);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// 注册
		mMonitorClient.registerIOTCListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// 注册
		mMonitorClient.unregisterIOTCListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		LcLog.i(TAG, "--> onActivityResult." + mSelectedEvent);
		mItemAdapter.notifyDataSetChanged();
	}

	private void setTitleBar() {
		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);
		titleBarBack.setOnClickListener((this));

		mTitleBarDone = (ImageButton) this
				.findViewById(R.id.titlebar_rightbtn1);
		mTitleBarDone.setBackgroundResource(R.drawable.selector_hd_search);
		mTitleBarDone.setOnClickListener((this));

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("事件列表");
	}

	private void requestEventDetail(long startdate, long enddate) {
		LcLog.i(TAG, "--> requestEventDetail.");
		if (!isListRefreshing) {
			mProgressDialog.show();
		}

		mMonitorClient.sendIOCtrl(mAVChannel,
				AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTEVENT_REQ,
				AVIOCTRLDEFs.SMsgAVIoctrlListEventReq.parseConent(mAVChannel,
						startdate, enddate, (byte) 0, (byte) 0));

		// 搜索区间时间的显示
		mSelectedEvent = "";
		mText_monitor_start.setText(mDateFormat.format(startdate));
		mText_monitor_end.setText(mDateFormat.format(enddate));
	}

	protected void showListEventArray(int what, byte[] obj) {
		mProgressDialog.dismiss();

		SMsgAVIoctrlListEventResp listEventResp = new AVIOCTRLDEFs.SMsgAVIoctrlListEventResp(
				obj);
		LcLog.i(TAG, "[SMsgAVIoctrlListEventResp] channel:"
				+ listEventResp.channel);
		LcLog.i(TAG, "[SMsgAVIoctrlListEventResp] total:" + listEventResp.total);
		LcLog.i(TAG, "[SMsgAVIoctrlListEventResp] count:" + listEventResp.count);
		LcLog.i(TAG, "[SMsgAVIoctrlListEventResp] index:" + listEventResp.index);
		LcLog.i(TAG, "[SMsgAVIoctrlListEventResp] endflag:"
				+ listEventResp.endflag);

		// 关闭刷新栏
		if (isListRefreshing) {
			isListRefreshing = false;
			closeListRefresh();
		}

		List<AVIOCTRLDEFs.SAvEvent> eventList = listEventResp.eventList;

		if (listEventResp.index == 0) {
			// 第一个数据包
			mItemAdapter = new itemAdapter(eventList);
			mItemListview.setAdapter(mItemAdapter);
		} else {
			for (int i = 0; i < eventList.size(); i++) {
				mEventsData.add(eventList.get(i));
			}

			if (mItemAdapter != null) {
				mItemAdapter.notifyDataSetChanged();
			}
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTEVENT_RESP:
				showListEventArray(msg.what, (byte[]) msg.obj);
				break;
			}
			super.handleMessage(msg);
		}
	};

	private void initUI() {
		mItemListview = (XListView) findViewById(R.id.monitor_eventitem_listview);
		mItemListview.setPullLoadEnable(false);
		mItemListview.setXListViewListener(this);
		mItemListview.setOnItemClickListener(this);

		View headlayout = getLayoutInflater().inflate(
				R.layout.eventitem_headview, null);
		mText_monitor_start = (TextView) headlayout
				.findViewById(R.id.text_monitor_start);
		mText_monitor_end = (TextView) headlayout
				.findViewById(R.id.text_monitor_end);

		mItemListview.addHeaderView(headlayout);

	}

	// 关闭刷新控件
	private void closeListRefresh() {

		mItemListview.stopRefresh();
		mItemListview.stopLoadMore();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		mItemListview.setRefreshTime(mDateFormat.format(calendar.getTime()));
	}

	private void dialog() {

		View layout = getLayoutInflater().inflate(
				R.layout.simple_selection_dialog, null);

		ListView mSimle_listview = (ListView) layout
				.findViewById(R.id.simle_listview);
		SimpleApdate apdate = new SimpleApdate();
		mSimle_listview.setAdapter(apdate);
		mSimle_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0) {
					// 请求数据
					getcalendartime(-1);
					mDialog.dismiss();
				}

				if (position == 1) {
					// 请求数据
					getcalendartime(-12);
					mDialog.dismiss();

				}
				if (position == 2) {
					// 请求数据
					getcalendartime(-24);
					mDialog.dismiss();

				}
				if (position == 3) {
					// 请求数据
					getcalendartime(-168);
					mDialog.dismiss();

				}
				if (position == 4) {
					selectordialog();

				}
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		mDialog = builder.create();
		mDialog.show();
		mDialog.setContentView(layout);
	}

	class SimpleApdate extends BaseAdapter {

		@Override
		public int getCount() {
			return simleimage.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = getLayoutInflater().inflate(
					R.layout.simle_listview_item, null);
			ImageView simle_item_img = LzViewHolder.get(convertView,
					R.id.simle_item_img);
			TextView simle_item_name = LzViewHolder.get(convertView,
					R.id.simle_item_name);

			simle_item_img.setImageResource(simleimage[position]);
			simle_item_name.setText(simlename[position]);

			return convertView;
		}
	}

	private void selectordialog() {

		View layout = getLayoutInflater().inflate(
				R.layout.selector_clock_layout, null);
		mColck_start_date = (EditText) layout
				.findViewById(R.id.colckdate_start);
		mColck_start_hour = (EditText) layout
				.findViewById(R.id.colck_start_hour);
		mColck_end_date = (EditText) layout.findViewById(R.id.colckdate_end);
		mColck_end_hour = (EditText) layout.findViewById(R.id.colck_end_hour);

		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat df2 = new SimpleDateFormat("HH:mm");
		mColck_end_date.setText(df.format(date));
		mColck_end_hour.setText(df2.format(date));

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.HOUR, -24);
		long milliseconds = calendar.getTimeInMillis();
		mColck_start_date.setText(df.format(milliseconds));
		mColck_start_hour.setText(df2.format(milliseconds));

		mColck_start_date.setOnClickListener(this);
		mColck_start_hour.setOnClickListener(this);
		mColck_end_date.setOnClickListener(this);
		mColck_end_hour.setOnClickListener(this);

		Button mQueding = (Button) layout
				.findViewById(R.id.colck_selector_queding);

		Button mQuxiao = (Button) layout
				.findViewById(R.id.colck_selector_quxiao);

		mQuxiao.setOnClickListener(this);
		mQueding.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		mColokDialog = builder.create();
		mColokDialog.show();
		mColokDialog.setContentView(layout);
	}

	class itemAdapter extends BaseAdapter {

		public itemAdapter(List<AVIOCTRLDEFs.SAvEvent> Datalist) {
			mEventsData = Datalist;
		}

		@Override
		public int getCount() {
			return mEventsData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			SAvEvent event = mEventsData.get(position);

			convertView = LayoutInflater.from(MonitorEventDetailActivity.this)
					.inflate(R.layout.monitor_event_item, null);

			TextView event_Status = LzViewHolder.get(convertView,
					R.id.event_status);

			TextView event_Time = LzViewHolder
					.get(convertView, R.id.event_time);

			if (0 == event.event) {
				event_Status.setText("全天候录像");
			} else if (1 == event.event) {
				event_Status.setText("动作侦测");
			} else {
				event_Status.setText("其他");
			}

			String selItem = "#" + (position + 2) + "#";
			if (mSelectedEvent.indexOf(selItem) >= 0) {
				event_Status.setTextColor(getResources().getColor(
						R.color.istar_i));
			} else {
				event_Status.setTextColor(getResources().getColor(
						R.color.istar_h));
			}

			event_Time.setText(event.sTimeDay.getLocalTime());

			return convertView;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		case R.id.titlebar_rightbtn1:
			dialog();
			break;
		case R.id.colckdate_start:
			DateTimePicker.settime().getAllDate(mColck_start_date, this);
			mDialog.dismiss();
			break;
		case R.id.colck_start_hour:
			DateTimePicker.settime().gethourtime(mColck_start_hour, this);
			break;
		case R.id.colckdate_end:
			DateTimePicker.settime().getAllDate(mColck_end_date, this);
			break;
		case R.id.colck_end_hour:
			DateTimePicker.settime().gethourtime(mColck_end_hour, this);
			break;
		case R.id.colck_selector_queding:
			String start_date = mColck_start_date.getText().toString().trim();
			String start_hour = mColck_start_hour.getText().toString().trim();
			String end_date = mColck_end_date.getText().toString().trim();
			String end_hour = mColck_end_hour.getText().toString().trim();

			mStartDate = getTimestamp(start_date + " " + start_hour + ":00");
			mEndDate = getTimestamp(end_date + " " + end_hour + ":00");

			// 请求数据
			requestEventDetail(mStartDate, mEndDate);

			mColokDialog.dismiss();
			mDialog.dismiss();
			break;
		case R.id.colck_selector_quxiao:
			mDialog.dismiss();
			mColokDialog.dismiss();
			break;
		}
	}

	public static long getTimestamp(String time) {
		long rand = 0;
		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
			Date d2 = null;
			try {
				d2 = sdf.parse(time);// 将String to Date类型
				rand = d2.getTime();
			} catch (ParseException e) {
				e.printStackTrace(); // To change body of catch statement use
										// File | Settings | File Templates.
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rand;
	}

	private void backPrePage() {
		this.setResult(Activity.RESULT_CANCELED);
		finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public void receiveChannelInfo(MonitorClient paramCamera, int paramInt1,
			int paramInt2) {

		// 连接结果返回
		LcLog.i(TAG, "[receiveChannelInfo] start param1:" + paramInt1
				+ ",param2:" + paramInt2);
	}

	@Override
	public void receiveFrameData(MonitorClient paramCamera, int paramInt,
			Bitmap paramBitmap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveFrameInfo(MonitorClient paramCamera, int paramInt1,
			long paramLong, int paramInt2, int paramInt3, int paramInt4,
			int paramInt5) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveIOCtrlData(MonitorClient paramCamera, int channel,
			int ioCtrlType, byte[] paramArrayOfByte) {
		Message msg = Message.obtain();
		msg.what = ioCtrlType;
		msg.arg1 = channel;
		msg.obj = paramArrayOfByte;

		handler.sendMessage(msg);
	}

	@Override
	public void receiveSessionInfo(MonitorClient paramCamera, int paramInt) {
		// 连接结果返回
		LcLog.i(TAG, "[receiveSessionInfo] start param:" + paramInt);
	}

	@Override
	public void onRefresh()

	{
		if (isListRefreshing) {
			// 禁止重复刷新
			return;
		}
		isListRefreshing = true;

		getcalendartime(-24);
	}

	@Override
	public void onLoadMore() {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.dismiss();
			}

			if (mColokDialog != null && mColokDialog.isShowing()) {
				mColokDialog.dismiss();
			}

			backPrePage();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		LcLog.i(TAG, "[onItemClick] position:" + position);

		if (position <= 1) {
			// 第一行是header
			return;
		}

		String selItem = "#" + position + "#";
		if (mSelectedEvent.indexOf(selItem) < 0) {
			mSelectedEvent += "#" + position + "#,";
		}

		position = position - 2;

		SAvEvent event = mEventsData.get(position);

		Intent intent = new Intent();

		int[] mSTimeDayBuf = new int[7];
		mSTimeDayBuf[0] = event.sTimeDay.year;
		mSTimeDayBuf[1] = event.sTimeDay.month;
		mSTimeDayBuf[2] = event.sTimeDay.day;
		mSTimeDayBuf[3] = event.sTimeDay.wday;
		mSTimeDayBuf[4] = event.sTimeDay.hour;
		mSTimeDayBuf[5] = event.sTimeDay.minute;
		mSTimeDayBuf[6] = event.sTimeDay.second;
		LcLog.i(TAG, "[onItemClick] sTimeDay:" + mSTimeDayBuf[0] + "/"
				+ mSTimeDayBuf[1] + "/" + mSTimeDayBuf[2] + " "
				+ mSTimeDayBuf[3] + " " + mSTimeDayBuf[4] + ":"
				+ mSTimeDayBuf[5] + ":" + +mSTimeDayBuf[6]);

		if (0 == event.event) {
			mEventTypeString = "全天候录像";
		} else if (1 == event.event) {
			mEventTypeString = "动作侦测";
		} else {
			mEventTypeString = "其他";
		}
		mBundle.putString("event_type_selected", mEventTypeString);
		mBundle.putIntArray("event_stime_selected", mSTimeDayBuf);

		intent.setClass(this, MonitorPlaybackActivity.class);
		intent.putExtras(mBundle);

		this.startActivityForResult(intent,
				ConstantDef.REQUEST_CODE_MONITOR_PLAYBACK);
	}

}
