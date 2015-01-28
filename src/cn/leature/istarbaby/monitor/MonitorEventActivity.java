package cn.leature.istarbaby.monitor;

import java.util.ArrayList;
import java.util.List;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LcLog;
import cn.leature.istarbaby.utils.LzViewHolder;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MonitorEventActivity extends Fragment implements OnClickListener,
		OnItemClickListener {
	private TextView titleBarTitle;
	private ImageButton mTitleBarPost;
	private ListView mListView;
	private List<MonitorInfo> mListItems = null;
	private EventAdapter mEventAdapter = null;
	// 用户登录信息
	private int fragmentlayout;
	private ImageView mBtn_Add, mBtn_List, mBtn_photo, mBtn_Event;

	private MonitorShareModel mShareMonitor = null;

	public static MonitorEventActivity newInstance(int frament,
			ImageView mBtn_List, ImageView mBtn_Event, ImageView mBtn_Add,
			ImageView mBtn_photo, TextView mTitleBarTitle,
			ImageButton mTitleBarPost) {
		MonitorEventActivity newFragment = new MonitorEventActivity();
		newFragment.fragmentlayout = frament;
		newFragment.mBtn_List = mBtn_List;
		newFragment.mBtn_Event = mBtn_Event;
		newFragment.mBtn_Add = mBtn_Add;
		newFragment.mBtn_photo = mBtn_photo;
		newFragment.titleBarTitle = mTitleBarTitle;
		newFragment.mTitleBarPost = mTitleBarPost;

		return newFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LcLog.i("MonitorEventActivity", "--->onCreateView.");

		View inflate = inflater.inflate(R.layout.activity_monitor_event, null);

		initUi(inflate);

		// 设置bottombar
		setBottomBarImage();

		loadLocalMonitors();

		return inflate;
	}

	private void setBottomBarImage() {

		mBtn_List.setImageResource(R.drawable.monitor_tab_list);

		mBtn_Event.setImageResource(R.drawable.monitor_tab_event_on);

		mBtn_photo.setImageResource(R.drawable.monitor_tab_gallery);

		mBtn_Add.setImageResource(R.drawable.monitor_tab_add);

	}

	private void initUi(View inflate) {

		mTitleBarPost.setVisibility(View.INVISIBLE);
		titleBarTitle.setText("事件");

		mListView = (ListView) inflate
				.findViewById(R.id.monitor_event_listview);
		mListView.setOnItemClickListener(this);
	}

	private void loadLocalMonitors() {

		mListItems = new ArrayList<MonitorInfo>();
		mShareMonitor = MonitorShareModel.getInstance();
		List<MonitorInfo> list = mShareMonitor.getMonitorInfoItems();
		for (int i = 0; i < list.size(); i++) {
			MonitorInfo monitorInfo = list.get(i);
			if (monitorInfo.UID.length() == 0) {
				continue;
			}

			mListItems.add(monitorInfo);
		}

		mEventAdapter = new EventAdapter();
		mListView.setAdapter(mEventAdapter);
	}

	public void refreshMonitorList() {
		if (mEventAdapter != null) {

			loadLocalMonitors();

			mEventAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

	class EventAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mListItems.size();
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

			TextView textName, textStatus;
			ImageView imageMore;

			final MonitorInfo monitorInfo = mListItems.get(position);

			if (monitorInfo.UID.length() > 0) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.monitor_list_item_event, null);

				textName = LzViewHolder
						.get(convertView, R.id.monitor_list_name);
				textStatus = LzViewHolder.get(convertView,
						R.id.monitor_list_status);

				textName.setText(monitorInfo.NickName);
				textStatus.setText(monitorInfo.Status);

				imageMore = LzViewHolder.get(convertView,
						R.id.monitor_list_more);
				if ("已联机".equals(monitorInfo.Status)) {
					imageMore.setVisibility(View.VISIBLE);
				} else {
					imageMore.setVisibility(View.INVISIBLE);
				}
			}
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		MonitorInfo monitorInfo = mListItems.get(position);
		if ("已联机".equals(monitorInfo.Status)) {

			mShareMonitor.saveCurrentMonitor(monitorInfo.UID);

			showEventDetailActivity(monitorInfo);
		}
	}

	public void showEventDetailActivity(MonitorInfo monitorInfo) {

		Intent intent = new Intent();
		intent.setClass(getActivity(), MonitorEventDetailActivity.class);

		Bundle bundle = new Bundle();
		bundle.putSerializable("monitor_selected", monitorInfo);
		intent.putExtras(bundle);

		startActivityForResult(intent, ConstantDef.REQUEST_CODE_MONITOR_EVENT);

		// 设定启动动画
		getActivity().overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);
	}
}
