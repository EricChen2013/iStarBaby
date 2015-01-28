package cn.leature.istarbaby.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.monitor.FriendCameraInfo;
import cn.leature.istarbaby.monitor.MonitorDBManager;
import cn.leature.istarbaby.monitor.MonitorInfo;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LzViewHolder;
import cn.leature.istarbaby.utils.SaveBitmapFile;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendShareMonitorActivity extends Activity implements
		OnClickListener, OnPostProcessListener, OnItemClickListener {

	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	// 文章发表成功（Message用）
	protected static final int CHILD_EDIT_DONE = 4;
	// 文章发表出错（Message用）
	protected static final int CHILD_EDIT_ERROR = -2;

	private ListView mListview;
	private List<MonitorInfo> mListItems = null;
	List<String> mDeviceData = new ArrayList<String>();

	private HttpPostUtil mHttpUtil;
	private Bundle mBundle;
	private LzProgressDialog mProgressDialog;

	private AlertDialog mRemoveDeviceDialog;
	List<String> mDeviceShared = new ArrayList<String>();
	List<String> mDeviceSelected = new ArrayList<String>();
	private String mRemoveDeviceId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_share_monitor);

		mBundle = getIntent().getExtras();
		mHttpUtil = HttpPostUtil.getInstance();
		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		mListItems = new ArrayList<MonitorInfo>();

		// 顶部导航
		setCustomTitleBar();

		initUI();

		getDeviceList();
	}

	private void initUI() {
		mListview = (ListView) findViewById(R.id.sharevideo_lisetview);
		mListview.setOnItemClickListener(this);

		List<MonitorInfo> list = MonitorDBManager.loadMonitorInfoList(this,
				LoginInfo.getLoginUserId(this));
		for (int i = 0; i < list.size(); i++) {
			MonitorInfo monitorInfo = list.get(i);

			mListItems.add(monitorInfo);
		}

		mAdapter = new VideoAdapter();
		mListview.setAdapter(mAdapter);
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("设备共享");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_ccl);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener(this);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_cmp);
	}

	private void backPrePage() {
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			backPrePage();
		}

		return super.onKeyDown(keyCode, event);
	}

	public class VideoAdapter extends BaseAdapter {

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

			TextView textName, textUid;
			ImageView imageLastFrame;
			ImageView shareImage;
			MonitorInfo monitorInfo = mListItems.get(position);

			convertView = getLayoutInflater().inflate(
					R.layout.friend_share_list_item, null);

			imageLastFrame = LzViewHolder.get(convertView,
					R.id.shareviedo_list_snapshot);
			if (monitorInfo.Snapshot.length() > 0) {
				imageLastFrame.setImageBitmap(SaveBitmapFile
						.loadBitmapFromFile(monitorInfo.Snapshot));
			}

			textName = LzViewHolder.get(convertView, R.id.shareviedo_list_name);
			textUid = LzViewHolder.get(convertView, R.id.shareviedo_list_uid);
			shareImage = LzViewHolder.get(convertView,
					R.id.shareviedo_list_mark);

			textName.setText(monitorInfo.NickName);
			textUid.setText(monitorInfo.UID);

			if (isMonitorShared(monitorInfo)) {
				shareImage.setImageResource(R.drawable.delete_x);
			} else if (isMonitorSelected(monitorInfo)) {
				shareImage.setImageResource(R.drawable.select_mark);
			} else {
				shareImage.setImageResource(R.drawable.select_mark_off);
			}

			return convertView;
		}
	}

	private boolean isMonitorShared(MonitorInfo monitorInfo) {

		String uid = monitorInfo.UID;

		return mDeviceShared.contains(uid);
	}

	private boolean isMonitorSelected(MonitorInfo monitorInfo) {

		String uid = monitorInfo.UID;

		return mDeviceSelected.contains(uid);
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage) {
		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE) {
			msg.what = CHILD_EDIT_DONE;
		} else {
			msg.what = CHILD_EDIT_ERROR;
		}

		handler.sendMessage(msg);
	}

	private void getDeviceList() {
		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("ContactId", mBundle.getString("ContactId"));
		param.put("QueryType", "1");

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				if ("".equals(result) || result == null) {
					Toast.makeText(FriendShareMonitorActivity.this, "网络连接失败",
							Toast.LENGTH_LONG).show();
					return;
				}

				try {
					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						FriendCameraInfo friendCameraInfo = new FriendCameraInfo(
								jsonObject);
						mDeviceShared.add(friendCameraInfo.getDevice_id());
					}

					mAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlDeviceList, param);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case CHILD_EDIT_DONE:// 保存
				saveDetailDone(msg.obj.toString());
				break;

			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};
	private VideoAdapter mAdapter;

	private void toErrorMessage(String msgString) {
		// 取得出错，显示提示信息
		mProgressDialog.dismiss();

		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	private void saveDetailDone(String Result) {
		mProgressDialog.dismiss();

		if ((Result == null) || "0".equals(Result)) {

			Toast.makeText(this, "共享失败", Toast.LENGTH_LONG).show();
		} else {

			Toast.makeText(this, "共享成功" + "", Toast.LENGTH_LONG).show();
		}

		backPrePage();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		case R.id.titlebar_rightbtn1:
			shareSettingComplete();
			break;
		case R.id.delete_queding:
			// 确认删除
			removeDeviceDone();
			break;
		case R.id.delete_quxiao:
			// 删除取消
			mRemoveDeviceDialog.dismiss();
			break;
		}
	}

	private void shareSettingComplete() {
		if (mDeviceSelected.size() > 0) {
			mProgressDialog.show();

			Map<String, String> param = new HashMap<String, String>();
			param.put("UserId", LoginInfo.getLoginUserId(this));
			param.put("ContactId", mBundle.getString("ContactId"));

			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < mListItems.size(); i++) {
				MonitorInfo monitorInfo = mListItems.get(i);
				if (isMonitorSelected(monitorInfo)) {
					buffer.append(monitorInfo.UID + "," + monitorInfo.NickName
							+ "," + monitorInfo.ViewAccount + ","
							+ monitorInfo.ViewPassword + "|");
				}
			}
			if (buffer.length() > 0) {
				// 去除最后一个符号|
				buffer.deleteCharAt(buffer.length() - 1);
			}
			param.put("DeviceInfo", buffer.toString());

			param.put("Permission", "0");
			param.put("AddType", "1");

			mHttpUtil.setOnPostProcessListener(FriendShareMonitorActivity.this);
			mHttpUtil.sendPostMessage(ConstantDef.cUrlDeviceInfoAdd, param);
		} else {
			Toast.makeText(this, "请选择共享的设备", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {

		MonitorInfo monitorInfo = mListItems.get(position);
		if (isMonitorShared(monitorInfo)) {
			// 已经共享
			mRemoveDeviceId = monitorInfo.UID;
			removeDeviceDialog();
			return;
		}

		if (isMonitorSelected(monitorInfo)) {
			mDeviceSelected.remove(monitorInfo.UID);
		} else {
			mDeviceSelected.add(monitorInfo.UID);
		}

		mAdapter.notifyDataSetChanged();
	}

	private void removeDeviceDialog() {
		// 显示器删除
		View layout = getLayoutInflater().inflate(R.layout.delete_dialog, null);
		TextView txtTitle = (TextView) layout.findViewById(R.id.alert_text);
		txtTitle.setText("确定要删除设备共享吗？");

		Button btnDelete = (Button) layout.findViewById(R.id.delete_queding);
		btnDelete.setOnClickListener(this);

		Button btnCancel = (Button) layout.findViewById(R.id.delete_quxiao);
		btnCancel.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		mRemoveDeviceDialog = builder.create();
		mRemoveDeviceDialog.show();
		mRemoveDeviceDialog.setContentView(layout);
	}

	private void removeDeviceDone() {
		mRemoveDeviceDialog.dismiss();

		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("ContactId", mBundle.getString("ContactId"));
		param.put("DeviceId", mRemoveDeviceId);

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				if ((result == null) || ("0".equals(result))) {
					Toast.makeText(FriendShareMonitorActivity.this, "删除失败",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(FriendShareMonitorActivity.this, "删除成功",
							Toast.LENGTH_LONG).show();
				}

				mDeviceShared.remove(mRemoveDeviceId);
				mAdapter.notifyDataSetChanged();
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlDeviceContactDelete, param);
	}
}
