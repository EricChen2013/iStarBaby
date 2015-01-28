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
import cn.leature.istarbaby.utils.CheckUserIdUtil;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LzViewHolder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorShareListActivity extends Activity implements
		OnClickListener, OnPostProcessListener {

	private ListView mFxlistview;
	private ArrayList<FriendCameraInfo> mListData = new ArrayList<FriendCameraInfo>();
	private AlertDialog mDeleteFXDialog;
	private HttpPostUtil httpUtil;
	private static final int ALBUM_LIST_DONE = 1;
	private static final int ALBUM_LIST_ERROR = -1;
	private FxListAdapter mAdapter;
	private int mSelectedFriendPosition = -1;
	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;
	private Bundle mBundle;
	private MonitorInfo mMonitorInfo;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private LzProgressDialog mProgressDialog;

	private TextView mTxtSearchUserid;
	private AlertDialog mSearchUseridDialog;
	private EditText mEditSearchUserid;
	private String mUseridForSearch = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitor_share_list);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);
		httpUtil = HttpPostUtil.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.noimage_child)
				.showImageForEmptyUri(R.drawable.noimage_child)
				.showImageOnFail(R.drawable.noimage_child).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

		mBundle = getIntent().getExtras();
		mMonitorInfo = (MonitorInfo) mBundle
				.getSerializable("monitor_settings");

		setCustomTitleBar();
		initUI();

		loadDeviceList();
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("设备共享");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener(this);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_add);
	}

	private void initUI() {
		mFxlistview = (ListView) findViewById(R.id.monitorcamera_fxlistview);

		mTxtSearchUserid = (TextView) findViewById(R.id.search_userid);
		mTxtSearchUserid.setOnClickListener(this);
	}

	private void loadDeviceList() {
		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("DeviceId", mMonitorInfo.UID);
		param.put("QueryType", "3");

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				if ("0".equals(result)) {
					Toast.makeText(MonitorShareListActivity.this, "加载失败",
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

				mAdapter = new FxListAdapter();
				mFxlistview.setAdapter(mAdapter);
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlDeviceList, param);
	}

	class FxListAdapter extends BaseAdapter {

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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = getLayoutInflater().inflate(
					R.layout.monitor_sharelist_item, null);

			TextView item_Username, item_Userid;
			final FriendCameraInfo friendCameraInfo = mListData.get(position);

			ImageView item_Usericon = LzViewHolder.get(convertView,
					R.id.monitor_sharelist_usericon);
			item_Username = LzViewHolder.get(convertView,
					R.id.monitor_sharelist_username);
			item_Userid = LzViewHolder.get(convertView,
					R.id.monitor_sharelist_userid);

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

			ImageView imgDelete = LzViewHolder.get(convertView,
					R.id.monitor_share_action);
			imgDelete.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					mSelectedFriendPosition = position;
					dialog(friendCameraInfo.getContact_id(),
							friendCameraInfo.getDevice_id());
				}
			});

			return convertView;
		}
	}

	private void dialog(final String Contact_id, final String Device_id) {
		// 显示器删除回调
		View layout = getLayoutInflater().inflate(R.layout.delete_dialog, null);
		Button btnDelete = (Button) layout.findViewById(R.id.delete_queding);
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDeleteFXDialog.dismiss();
				mProgressDialog.show();

				Map<String, String> param = new HashMap<String, String>();
				// 本人id
				param.put("UserId",
						LoginInfo.getLoginUserId(MonitorShareListActivity.this));
				// 好友id
				param.put("ContactId", Contact_id);
				param.put("DeviceId", Device_id);

				httpUtil.setOnPostProcessListener(MonitorShareListActivity.this);
				httpUtil.sendPostMessage(ConstantDef.cUrlDeviceContactDelete,
						param);
			}
		});

		Button btnCancel = (Button) layout.findViewById(R.id.delete_quxiao);
		btnCancel.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		mDeleteFXDialog = builder.create();
		mDeleteFXDialog.show();
		mDeleteFXDialog.setContentView(layout);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_title:
			break;
		case R.id.titlebar_leftbtn:
			backpage();
			break;
		case R.id.delete_quxiao:
			mDeleteFXDialog.dismiss();
			break;
		case R.id.titlebar_rightbtn1:
			// 添加分享好友
			Intent intent = new Intent();
			intent.putExtras(mBundle);
			intent.setClass(this, MonitorAddFriendActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_MonitorAddFriendFx);
			break;
		case R.id.search_userid:
			searchUseridDialog();
			break;
		case R.id.friend_dialog_closebtn:
			mSearchUseridDialog.dismiss();
			break;
		case R.id.friend_dialog_okbtn:
			searchDetail();
			break;
		}
	}

	private void searchUseridDialog() {

		LayoutInflater inflater = getLayoutInflater();
		View inflate = inflater.inflate(R.layout.monitor_share_adduser,
				(ViewGroup) findViewById(R.id.friend_dialog));

		Button btn_ok = (Button) inflate.findViewById(R.id.friend_dialog_okbtn);
		Button btn_close = (Button) inflate
				.findViewById(R.id.friend_dialog_closebtn);

		mEditSearchUserid = (EditText) inflate
				.findViewById(R.id.friend_dialog_name);

		btn_ok.setOnClickListener(this);
		btn_close.setOnClickListener(this);

		// dialog
		mSearchUseridDialog = new AlertDialog.Builder(this).setView(inflate)
				.show();
	}

	public void searchDetail() {
		// 日志List取得的参数设定
		mUseridForSearch = mEditSearchUserid.getText().toString().trim();

		if (mUseridForSearch.equals("")) {
			Toast.makeText(this, "[账号/手机号码]未输入", Toast.LENGTH_LONG).show();
			return;
		}
		if (mUseridForSearch.length() != 11) {
			Toast.makeText(this, "[账号/手机号码]输入有误", Toast.LENGTH_LONG).show();
			return;
		}
		if (!CheckUserIdUtil.isValidUserId(mUseridForSearch)) {
			Toast.makeText(MonitorShareListActivity.this, "[账号/手机号码]输入有误",
					Toast.LENGTH_LONG).show();
			return;
		}

		mSearchUseridDialog.dismiss();
		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("AddType", "3");

		String DeviceInfo = mMonitorInfo.UID + "," + mMonitorInfo.NickName
				+ "," + mMonitorInfo.DeviceName + ","
				+ mMonitorInfo.DevicePassword;
		param.put("DeviceInfo", DeviceInfo);
		param.put("ContactInfo", mUseridForSearch);
		param.put("Permission", "0");

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				if ((result == null) || ("0".equals(result))
						|| ("9".equals(result))) {
					Toast.makeText(MonitorShareListActivity.this,
							"没有找到符合搜索条件的用户", Toast.LENGTH_LONG).show();
					return;
				}

				if ("8".equals(result)) {
					Toast.makeText(MonitorShareListActivity.this,
							"已共享的设备不需要添加", Toast.LENGTH_LONG).show();
					return;
				}

				Toast.makeText(MonitorShareListActivity.this, "添加共享成功",
						Toast.LENGTH_LONG).show();

				// 重新刷新数据
				mListData.clear();
				loadDeviceList();
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlDeviceInfoAdd, param);
	}

	private void backpage() {
		setResult(Activity.RESULT_CANCELED);

		this.finish();

		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			backpage();
		}

		return true;
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage) {
		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE) {
			msg.what = ALBUM_LIST_DONE;
		} else {
			msg.what = ALBUM_LIST_ERROR;
		}

		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ALBUM_LIST_DONE:
				toListDailyDone(msg.obj.toString());
				break;

			default:
				// 错误处理
				toErrorMessage(msg.obj.toString());
				break;
			}

			super.handleMessage(msg);
		}

	};

	private void toListDailyDone(String result) {
		mProgressDialog.dismiss();

		if (result == null || "0".equals(result)) {
			return;
		}

		mListData.remove(mSelectedFriendPosition);
		mAdapter.notifyDataSetChanged();
		Toast.makeText(this, "删除成功", Toast.LENGTH_LONG).show();
	}

	private void toErrorMessage(String msgString) {
		mProgressDialog.dismiss();

		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case ConstantDef.REQUEST_CODE_MonitorAddFriendFx:
				mListData.clear();

				loadDeviceList();
				break;
			}
		}
	};
}
