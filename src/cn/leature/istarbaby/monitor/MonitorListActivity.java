package cn.leature.istarbaby.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.domain.FriendGroupListInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.friend.FriendSearchActivity;
import cn.leature.istarbaby.friend.FriendDetailAddActivity;
import cn.leature.istarbaby.monitor.MonitorListAdapter.OnMonitorDeleteListener;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LcLog;
import cn.leature.istarbaby.utils.LzViewHolder;
import cn.leature.istarbaby.utils.SaveBitmapFile;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MonitorListActivity extends Fragment implements
		OnItemClickListener, OnClickListener, OnMonitorDeleteListener,
		OnItemLongClickListener {
	private final String TAG = "MonitorListActivity";

	public static final int SHOW_CAMERA_LIST = 100;
	// 用户登录信息
	private String mLoginUserId;

	public ImageButton showLeftMenu, titleBarEdit;

	private ListView mMonitorListView;
	private MonitorListAdapter mMonitorAdapter = null;
	private List<MonitorInfo> mListItems = null;

	private Dialog mMonitorDeleteDialog;
	private MonitorInfo mDeleteMonitorInfo = null;

	private boolean isEditMode = false;

	private MonitorShareModel mShareMonitor = null;

	private int layoutfragment;
	private TextView titleBarTitle;
	private ImageView mBtn_List, mBtn_Event, mBtn_Add, mBtn_photo;
	private Handler mHandler;

	public static MonitorListActivity newInstance(int frament,
			ImageView mBtn_List, ImageView mBtn_Event, ImageView mBtn_Add,
			ImageView mBtn_photo, TextView mTitleBarTitle,
			ImageButton mTitleBarPost, Handler handler) {
		MonitorListActivity newFragment = new MonitorListActivity();
		newFragment.layoutfragment = frament;
		newFragment.mBtn_List = mBtn_List;
		newFragment.mBtn_Event = mBtn_Event;
		newFragment.mBtn_Add = mBtn_Add;
		newFragment.mBtn_photo = mBtn_photo;
		newFragment.titleBarTitle = mTitleBarTitle;
		newFragment.titleBarEdit = mTitleBarPost;

		newFragment.mHandler = handler;

		return newFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LcLog.i(TAG, "--->onCreateView.");

		View inflate = inflater.inflate(R.layout.activity_monitor_list, null);

		isEditMode = false;

		mShareMonitor = MonitorShareModel.getInstance();

		mMonitorListView = (ListView) inflate
				.findViewById(R.id.monitor_listview);
		mMonitorListView.setOnItemClickListener(this);
		mMonitorListView.setOnItemLongClickListener(this);
		// 设置顶部导航

		titleBarEdit.setVisibility(View.VISIBLE);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_edt);
		titleBarEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				monitorEdit();
			}
		});

		titleBarTitle.setText("宝宝监护");

		// 检查用户登录状况
		checkUserLogin();

		// 设置bottombar
		setBottomBarImage();

		loadLocalMonitors();

		TextView myCamera = (TextView) inflate.findViewById(R.id.btn_mycamera);
		TextView friendCamera = (TextView) inflate
				.findViewById(R.id.btn_friendcamera);
		myCamera.setOnClickListener(this);
		friendCamera.setOnClickListener(this);
		return inflate;
	}

	private void setBottomBarImage() {

		mBtn_List.setImageResource(R.drawable.monitor_tab_list_on);

		mBtn_Event.setImageResource(R.drawable.monitor_tab_event);

		mBtn_photo.setImageResource(R.drawable.monitor_tab_gallery);

		mBtn_Add.setImageResource(R.drawable.monitor_tab_add);

	}

	private void checkUserLogin() {
		// 取得用户登录信息
		mLoginUserId = LoginInfo.getLoginUserId(getActivity());
	}

	private void loadLocalMonitors() {
		LcLog.i(TAG, "--->loadLocalMonitors.");
		mListItems = new ArrayList<MonitorInfo>();

		List<MonitorInfo> list = mShareMonitor.getMonitorInfoItems();

		for (int i = 0; i < list.size(); i++) {
			MonitorInfo monitorInfo = list.get(i);
			LcLog.i(TAG, "---->mListItems:" + monitorInfo.UID + ",status:"
					+ monitorInfo.Status

			);
			mListItems.add(monitorInfo);
		}

		if (mListItems.size() == 1) {
			// 表示没有登录设备，只剩下 新增摄像机
			titleBarEdit.setVisibility(View.INVISIBLE);
		}

		mMonitorAdapter = new MonitorListAdapter(getActivity(), 0, mListItems);
		mMonitorAdapter.setOnMonitorDeleteListener(this);
		mMonitorListView.setAdapter(mMonitorAdapter);

		// 如果是编辑模式
		if (isEditMode) {
			monitorEdit();
		}
	}

	public void reloadMonitorList() {
		if (mMonitorAdapter != null) {

			loadLocalMonitors();

			mMonitorAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * @Title: onItemClick
	 * @Description: TODO
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
	 *      android.view.View, int, long)
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (isEditMode) {
			// 编辑模式状态下，不可点
			Toast.makeText(getActivity(), "编辑还没完成啊，亲。", Toast.LENGTH_LONG)
					.show();
			return;
		}

		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		if (position < mListItems.size() - 1) {
			MonitorInfo monitorInfo = mListItems.get(position);
			mShareMonitor.saveCurrentMonitor(monitorInfo.UID);

			bundle.putInt("cameraIndex", position);
			bundle.putSerializable("monitor_selected", monitorInfo);

			intent.setClass(getActivity(), MonitorLiveViewActivity.class);
			intent.putExtras(bundle);

			this.startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_MONITOR_LIVE);
		} else {
			MonitorAdd();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LcLog.e(TAG, "--->onActivityResult.");

		if (resultCode == Activity.RESULT_OK) {
			if ((requestCode == ConstantDef.REQUEST_CODE_MONITOR_SETTING)
					|| (requestCode == ConstantDef.REQUEST_CODE_MONITOR_LIVE)) {
				// 要重新连接
				mHandler.sendEmptyMessage(MonitorFragmentActivity.MESSAGE_RECONNECT_CAMERA_LIST);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.delete_queding:
			// 确认删除
			confirmDelete();
			break;
		case R.id.delete_quxiao:
			// 删除取消
			mMonitorDeleteDialog.dismiss();
			break;
		case R.id.btn_mycamera:
			loadLocalMonitors();
			break;
		case R.id.btn_friendcamera:
			loadFriendCamera();
			break;
		}
	}

	private void loadFriendCamera() {
		mListItems.clear();
		Map<String, String> param = new HashMap<String, String>();
		param.put("ContactId", LoginInfo.getLoginUserId(getActivity()));
		param.put("QueryType", "2");
		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				Log.e("result", "result==" + result);
				if ("0".equals(result)) {
					Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_LONG)
							.show();
					return;
				}

				try {
					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);

						FriendCameraInfo friendCameraInfo = new FriendCameraInfo(
								jsonObject);

						MonitorInfo monitorInfo = new MonitorInfo(
								friendCameraInfo.user_id,
								friendCameraInfo.device_id,
								friendCameraInfo.user_name,
								friendCameraInfo.device_name,
								friendCameraInfo.device_password,
								friendCameraInfo.device_name,
								friendCameraInfo.device_password);
						mListItems.add(monitorInfo);

					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//
				FriendCameraAdapter adapter = new FriendCameraAdapter();
				mMonitorListView.setAdapter(adapter);
			}
		});
		httpGetUtil.execute("DeviceList.aspx", param);
	}

	public class FriendCameraAdapter extends BaseAdapter {

		@Override
		public int getCount() {
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

			TextView textName, textUid, textStatus;
			ImageView imageLastFrame;
			ImageView shareimage;
			final MonitorInfo monitorInfo = mListItems.get(position);

			convertView = getActivity().getLayoutInflater().inflate(
					R.layout.monitor_friendlist_item, null);

			imageLastFrame = LzViewHolder.get(convertView,
					R.id.monitor_friendlist_snapshot);
			if (monitorInfo.Snapshot.length() > 0) {
				imageLastFrame.setImageBitmap(SaveBitmapFile
						.loadBitmapFromFile(monitorInfo.Snapshot));
			}

			textName = LzViewHolder.get(convertView,
					R.id.monitor_friendlist_name);
			textStatus = LzViewHolder.get(convertView,
					R.id.monitor_friendlist_status);
			textUid = LzViewHolder
					.get(convertView, R.id.monitor_friendlist_uid);

			textName.setText(monitorInfo.NickName);
			textStatus.setText(monitorInfo.Status);
			textUid.setText(monitorInfo.UID);

			return convertView;
		}

	}

	private void MonitorAdd() {

		mHandler.sendEmptyMessage(MonitorFragmentActivity.MESSAGE_CHANGE_FRAGMENT_ADD);
	}

	private void confirmDelete() {
		mMonitorDeleteDialog.dismiss();

		// 删除设备信息
		if (MonitorDBManager.deleteMonitorInfoTable(getActivity(),
				mDeleteMonitorInfo)) {

			mShareMonitor.deleteMonitor(mDeleteMonitorInfo.UID);

			// 已删除
			mListItems.remove(mDeleteMonitorInfo);
		}

		if (mListItems.size() == 1) {
			// 表示已经全部删除，只剩下 新增摄像机
			isEditMode = false;
			titleBarEdit.setVisibility(View.INVISIBLE);
		}

		// 通知更新列表
		mHandler.sendEmptyMessage(MonitorFragmentActivity.MESSAGE_CHANGE_MONITOR_DELETE);
	}

	private void monitorEdit() {
		if (isEditMode) {
			// 可编辑状态时，变成非编辑状态
			isEditMode = false;

			// 菜单按钮变成 编辑
			titleBarEdit.setBackgroundResource(R.drawable.selector_hd_edt);
		} else {
			// 非可编辑状态，变成编辑状态
			isEditMode = true;
			mDeleteMonitorInfo = null;

			// 菜单按钮变成 完成
			titleBarEdit.setBackgroundResource(R.drawable.selector_hd_cmp);
			//
		}

		// 通知更新列表
		mMonitorAdapter.setEditMode(isEditMode);
		mMonitorAdapter.notifyDataSetChanged();
	}

	@Override
	public void onMonitorDeleteDone(MonitorInfo monitorInfo) {
		mDeleteMonitorInfo = monitorInfo;
		dialog();
	}

	private void dialog() {
		// 显示器删除回调
		View layout = getActivity().getLayoutInflater().inflate(
				R.layout.delete_dialog, null);
		Button btnDelete = (Button) layout.findViewById(R.id.delete_queding);
		btnDelete.setOnClickListener(this);

		Button btnCancel = (Button) layout.findViewById(R.id.delete_quxiao);
		btnCancel.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		mMonitorDeleteDialog = builder.create();
		mMonitorDeleteDialog.show();
		mMonitorDeleteDialog.setContentView(layout);
	}

	@Override
	public void onMonitorSettingDone(MonitorInfo monitorInfo) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();

		mShareMonitor.saveCurrentMonitor(monitorInfo.UID);

		bundle.putSerializable("monitor_settings", monitorInfo);

		intent.putExtras(bundle);

		intent.setClass(getActivity(), MonitorSettingsActivity.class);

		this.startActivityForResult(intent,
				ConstantDef.REQUEST_CODE_MONITOR_SETTING);

		// 设定启动动画
		getActivity().overridePendingTransition(R.anim.activity_in_from_bottom,
				R.anim.activity_nothing_in_out);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (position < mListItems.size() - 1) {
			mDeleteMonitorInfo = mListItems.get(position);
			dialog();
		}

		return false;
	}
}
