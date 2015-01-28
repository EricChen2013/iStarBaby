package cn.leature.istarbaby.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpPostUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.network.HttpPostUtil.OnPostProcessListener;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LzViewHolder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.view.KeyEvent;
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
import android.widget.Toast;

public class MonitorAddFriendActivity extends Activity implements
		OnClickListener, OnPostProcessListener, OnItemClickListener {

	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	// 文章发表成功（Message用）o
	protected static final int CHILD_EDIT_DONE = 4;
	// 文章发表出错（Message用）
	protected static final int CHILD_EDIT_ERROR = -2;

	private FriendAdapter mAdapter;
	ArrayList<FriendCameraInfo> mListData = new ArrayList<FriendCameraInfo>();
	ArrayList<String> mContactData = new ArrayList<String>();
	private ListView mListView;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private HttpPostUtil mHttpUtil;
	private Bundle mBundle;
	private MonitorInfo mMonitorInfo;
	private LzProgressDialog mProgressDialog;
	private String ContactInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_add_friend);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.noimage_child)
				.showImageForEmptyUri(R.drawable.noimage_child)
				.showImageOnFail(R.drawable.noimage_child).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();
		mHttpUtil = HttpPostUtil.getInstance();
		mBundle = getIntent().getExtras();
		mMonitorInfo = (MonitorInfo) mBundle
				.getSerializable("monitor_settings");

		initUI();
		// 顶部导航
		setCustomTitleBar();
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("联系人");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_ccl);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener(this);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_cmp);
	}

	private void initUI() {
		mProgressDialog.show();

		mListView = (ListView) findViewById(R.id.monitor_friendfx_listview);
		mListView.setOnItemClickListener(this);

		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("DeviceId", mMonitorInfo.UID);
		param.put("QueryType", "4");

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				if ((result == null) || "0".equals(result)) {
					Toast.makeText(MonitorAddFriendActivity.this, "加载失败",
							Toast.LENGTH_LONG).show();
					return;
				}

				try {
					JSONArray jsonArray = new JSONArray(result);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);

						FriendCameraInfo friendCameraInfo = new FriendCameraInfo(
								jsonObject);

						mListData.add(friendCameraInfo);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//
				mAdapter = new FriendAdapter();
				mListView.setAdapter(mAdapter);
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlDeviceList, param);
	}

	class FriendAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mListData.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = getLayoutInflater().inflate(
					R.layout.monitor_sharelist_item, null);

			FriendCameraInfo friendCameraInfo = mListData.get(position);

			ImageView item_Usericon = LzViewHolder.get(convertView,
					R.id.monitor_sharelist_usericon);
			TextView item_Username = LzViewHolder.get(convertView,
					R.id.monitor_sharelist_username);
			TextView item_Userid = LzViewHolder.get(convertView,
					R.id.monitor_sharelist_userid);
			ImageView item_delete = LzViewHolder.get(convertView,
					R.id.monitor_share_action);

			String memo = friendCameraInfo.getContact_memo();
			if (memo.length() == 0) {
				memo = friendCameraInfo.getContact_name();
			}
			item_Username.setText(memo);

			item_Userid.setText(friendCameraInfo.getContact_id());

			if (!"".equals(friendCameraInfo.getContact_icon())) {

				imageLoader.displayImage(HttpClientUtil.SERVER_PATH
						+ friendCameraInfo.getContact_icon(), item_Usericon,
						options);
			}

			if (isFriendSelected(friendCameraInfo.getContact_id())) {
				item_delete.setImageResource(R.drawable.select_mark);
			} else {
				item_delete.setImageResource(R.drawable.select_mark_off);
			}

			return convertView;
		}
	}

	public boolean isFriendSelected(String friendid) {

		return mContactData.contains(friendid);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backpage();
			break;
		case R.id.titlebar_rightbtn1:
			addFriendShareComplete();
			break;
		}
	}

	private void addFriendShareComplete() {
		if ((mContactData == null) || (mContactData.size() == 0)) {
			backpage();
			return;
		}

		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("AddType", "2");

		String DeviceInfo = mMonitorInfo.UID + "," + mMonitorInfo.NickName
				+ "," + mMonitorInfo.DeviceName + ","
				+ mMonitorInfo.DevicePassword;
		param.put("DeviceInfo", DeviceInfo);

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < mContactData.size(); i++) {
			buffer.append(mContactData.get(i)).append(",");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		ContactInfo = buffer.toString().trim();

		param.put("ContactInfo", ContactInfo);
		param.put("Permission", "0");

		mHttpUtil.setOnPostProcessListener(MonitorAddFriendActivity.this);
		mHttpUtil.sendPostMessage(ConstantDef.cUrlDeviceInfoAdd, param);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backpage();
		}

		return true;
	}

	private void backpage() {
		if (mContactData.size() == 0) {
			setResult(Activity.RESULT_CANCELED);
		} else {
			setResult(Activity.RESULT_OK);
		}

		this.finish();
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

	private void saveDetailDone(String Result) {
		mProgressDialog.dismiss();

		if ((Result == null) || "0".equals(Result)) {
			Toast.makeText(this, "添加失败", Toast.LENGTH_LONG).show();
			return;
		}

		Toast.makeText(this, "添加成功", Toast.LENGTH_LONG).show();

		backpage();
	}

	private void toErrorMessage(String msgString) {
		// 取得出错，显示提示信息
		mProgressDialog.dismiss();

		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
		mContactData.clear();
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

		FriendCameraInfo friendCameraInfo = mListData.get(position);
		if (isFriendSelected(friendCameraInfo.getContact_id())) {
			mContactData.remove(friendCameraInfo.getContact_id());
		} else {
			mContactData.add(friendCameraInfo.getContact_id());
		}

		mAdapter.notifyDataSetChanged();
	}
}
