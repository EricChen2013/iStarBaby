package cn.leature.istarbaby.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tutk.IOTC.st_LanSearchInfo;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.utils.DensityUtil;
import cn.leature.istarbaby.utils.LcLog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.text.method.ReplacementTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MonitorAddActivity extends Fragment implements OnClickListener,
		OnItemClickListener, IRegisterMonitorListener {

	protected static final int MESSAGE_NEW_CAMERA_SUCCESS = 100;
	protected static final int MESSAGE_NEW_CAMERA_FAILED = 101;

	private final static int SCANNIN_GREQUEST_CODE = 1;
	private final static int CONNECT_NEW_CAMERA_SETTING = 2;
	/**
	 * 显示扫描结果
	 */
	private EditText mUidTextView;
	private EditText mMonitorNameText;
	private EditText mMonitorPwdText;

	// TitleBar的控件
	private TextView mTitleBarTitle;

	private LinearLayout mSearchButton;

	private Dialog mMonitorSearchDialog;
	private Button mMonitorSearchCancel, mMonitorSearchResearch;
	private ListView mMonitorSearchListView;

	private Dialog mScanCodeDialog;
	private Button mScanCodeCancel, mScanCodeCopy, mScanCodeOpen;
	private TextView mScanCodeResult;

	private ArrayList<Map<String, Object>> mData;
	private SimpleAdapter mSimpleAdapter;

	private String device_uid;
	private String device_nickname;
	private String device_pwd;

	// 用户登录信息
	private String mLoginUserId;

	public ImageButton showLeftMenu, titleBarEdit;

	private int layoutfragment;
	private ImageView mBtn_List, mBtn_Event, mBtn_Add, mBtn_photo;
	private Handler mHandler;

	private int mAVChannel = 0;
	private MyMonitor mMyNewMonitor = null;
	private MonitorInfo mNewMonitorInfo;
	private TextView mBtnSettingWifi;
	private boolean mIsProcessCameraSetting = false;

	private LzProgressDialog mProgressDialog;

	private MonitorShareModel mShareMonitor;

	public static MonitorAddActivity newInstance(int frament,
			ImageView mBtn_List, ImageView mBtn_Event, ImageView mBtn_Add,
			ImageView mBtn_photo, TextView mTitleBarTitle,
			ImageButton mTitleBarPost, Handler handler) {
		MonitorAddActivity newFragment = new MonitorAddActivity();
		newFragment.layoutfragment = frament;
		newFragment.mBtn_List = mBtn_List;
		newFragment.mBtn_Event = mBtn_Event;
		newFragment.mBtn_Add = mBtn_Add;
		newFragment.mBtn_photo = mBtn_photo;
		newFragment.mTitleBarTitle = mTitleBarTitle;
		newFragment.titleBarEdit = mTitleBarPost;

		newFragment.mHandler = handler;

		return newFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LcLog.i("MonitorAddActivity", "--->onCreateView.");

		// 设置主菜单
		View inflate = inflater.inflate(R.layout.activity_monitor_add, null);

		mLoginUserId = LoginInfo.getLoginUserId(getActivity());

		mUidTextView = (EditText) inflate.findViewById(R.id.monitor_uid_code);
		mUidTextView.setTransformationMethod(new InputLowerToUpper());

		mMonitorNameText = (EditText) inflate.findViewById(R.id.monitor_name);
		mMonitorPwdText = (EditText) inflate
				.findViewById(R.id.monitor_password);

		mShareMonitor = MonitorShareModel.getInstance();

		mProgressDialog = new LzProgressDialog(getActivity());
		mProgressDialog.setCancelable(true);

		mUidTextView.setText("");
		mMonitorNameText.setText("Camera");
		mMonitorPwdText.setText("admin");

		mSearchButton = (LinearLayout) inflate
				.findViewById(R.id.monitor_search_btn);
		mSearchButton.setOnClickListener(this);

		LinearLayout mButton = (LinearLayout) inflate
				.findViewById(R.id.qrcode_scan_btn);
		mButton.setOnClickListener(this);

		// titlebar设置
		titleBarEdit.setVisibility(View.VISIBLE);
		mTitleBarTitle.setText("新增监护器");
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_cmp);
		titleBarEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				todoAddMonitor();
			}
		});

		// 设置bottombar
		setBottomBarImage();

		// 设置密码、wifi
		mBtnSettingWifi = (TextView) inflate
				.findViewById(R.id.btn_setting_wifi);
		mBtnSettingWifi.setOnClickListener(this);

		return inflate;
	}

	public class InputLowerToUpper extends ReplacementTransformationMethod {
		@Override
		protected char[] getOriginal() {
			char[] lower = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
					'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
					'w', 'x', 'y', 'z' };
			return lower;
		}

		@Override
		protected char[] getReplacement() {
			char[] upper = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
					'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
					'W', 'X', 'Y', 'Z' };
			return upper;
		}

	}

	private void setBottomBarImage() {

		mBtn_List.setImageResource(R.drawable.monitor_tab_list);

		mBtn_Event.setImageResource(R.drawable.monitor_tab_event);

		mBtn_photo.setImageResource(R.drawable.monitor_tab_gallery);

		mBtn_Add.setImageResource(R.drawable.monitor_tab_add_on);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("onActivityResult", "resultCode:" + resultCode + ",requestCode:"
				+ requestCode);

		switch (requestCode) {
		case SCANNIN_GREQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				Bundle bundle = data.getExtras();
				resultForScanQRCode(bundle.getString("result", ""));
			}
			break;
		case CONNECT_NEW_CAMERA_SETTING:
			// 断开连接
			disconnectNewCamera();

			String pwd = data.getStringExtra("camera_changepwd");
			if (pwd != null) {
				mMonitorPwdText.setText(pwd);
				mNewMonitorInfo.DevicePassword = pwd;
				mNewMonitorInfo.ViewPassword = pwd;
			}

			if (resultCode == Activity.RESULT_OK) {
				completeAddMonitorDone();
			}

			break;
		}
	}

	private boolean checkPatternMatched(String result) {

		Pattern pattern = Pattern.compile("^(http://|https://).*");
		Matcher matcher = pattern.matcher(result);

		return matcher.matches();
	}

	private void resultForScanQRCode(String result) {
		if (result == null || result.trim().length() == 0) {
			// 结果为空
			return;
		}

		result = result.trim();
		if ((!checkPatternMatched(result)) && (result.length() == 20)) {
			// 设备UID
			// 显示扫描到的内容
			Message msg = Message.obtain();
			msg.what = MonitorFragmentActivity.MESSAGE_UID_SCAN_RESULT;
			msg.arg1 = SCANNIN_GREQUEST_CODE;
			msg.obj = result;

			mHandler.sendMessage(msg);

			return;
		}

		// 显示扫描内容
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_scancode_result, null);

		mScanCodeResult = (TextView) view.findViewById(R.id.txt_scan_result);
		mScanCodeResult.setText(result);

		mScanCodeCancel = (Button) view.findViewById(R.id.btnccl_scan_result);
		mScanCodeCancel.setOnClickListener(this);

		mScanCodeCopy = (Button) view.findViewById(R.id.btncpy_scan_result);
		mScanCodeCopy.setOnClickListener(this);

		mScanCodeOpen = (Button) view.findViewById(R.id.btnopn_scan_result);
		mScanCodeOpen.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		mScanCodeDialog = builder.create();
		mScanCodeDialog.show();
		mScanCodeDialog.setContentView(view);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.qrcode_scan_btn:
			// 扫描
			scanQRCode();
			break;
		case R.id.monitor_search_btn:
			// 内网搜索
			searchMonitor();
			break;
		case R.id.monitor_add_research:
			// 重新搜索
			researchMonitor();
			break;
		case R.id.monitor_add_cancel:
			// 取消
			mMonitorSearchDialog.dismiss();
			break;
		case R.id.btn_setting_wifi:
			checkAndConnectNewCamera();
			break;
		case R.id.btnccl_scan_result:
			// 取消
			mScanCodeDialog.dismiss();
			break;
		case R.id.btncpy_scan_result:
			// 复制
			mScanCodeDialog.dismiss();

			ClipboardManager mClipboard = (ClipboardManager) getActivity()
					.getSystemService(Context.CLIPBOARD_SERVICE);

			ClipData clip = ClipData.newPlainText("qrcode result",
					mScanCodeResult.getText().toString());

			mClipboard.setPrimaryClip(clip);

			break;
		case R.id.btnopn_scan_result:
			// 打开网页
			mScanCodeDialog.dismiss();

			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");

			String result = mScanCodeResult.getText().toString();
			if (!checkPatternMatched(result)) {
				result = "http://" + result;
			}
			Uri content_url = Uri.parse(result);
			intent.setData(content_url);

			startActivity(intent);

			break;
		}
	}

	@Override
	public void onResume() {
		mIsProcessCameraSetting = false;

		super.onResume();
	}

	@Override
	public void onPause() {
		if (!mIsProcessCameraSetting) {
			disconnectNewCamera();
		}

		super.onPause();
	}

	private void researchMonitor() {
		mData = new ArrayList<Map<String, Object>>();

		st_LanSearchInfo[] lanSearchInfo = MonitorClient.SearchLAN();
		if (lanSearchInfo != null) {
			for (int i = 0; i < lanSearchInfo.length; i++) {
				st_LanSearchInfo searchInfo = lanSearchInfo[i];

				Map<String, Object> item = new HashMap<String, Object>();
				item.put("title", (new String(searchInfo.UID)).trim());
				item.put("text", (new String(searchInfo.IP)).trim());
				mData.add(item);
			}
		}
		// 设置搜索到的画布大小
		int height = 57;
		if (mData.size() <= 1) {
			setListViewHeightBasedOnChildren(mMonitorSearchListView, height);
		} else if (mData.size() == 2) {
			setListViewHeightBasedOnChildren(mMonitorSearchListView, height * 2);
		} else if (mData.size() >= 3) {
			setListViewHeightBasedOnChildren(mMonitorSearchListView, height * 3);
		}

		mSimpleAdapter = new SimpleAdapter(getActivity(), mData,
				R.layout.monitoradd_search_item,

				new String[] { "title", "text" }, new int[] { R.id.textView1,
						R.id.textView2 });
		mMonitorSearchListView.setAdapter(mSimpleAdapter);
		mSimpleAdapter.notifyDataSetChanged();
	}

	public void setListViewHeightBasedOnChildren(ListView listView, int height) {
		int itemHeight = DensityUtil.dip2px(getActivity(), height);
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = itemHeight;
		listView.setLayoutParams(params);
	}

	private void searchMonitor() {

		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.monitor_add_search, null);

		mMonitorSearchCancel = (Button) view
				.findViewById(R.id.monitor_add_cancel);
		mMonitorSearchCancel.setOnClickListener(this);

		mMonitorSearchResearch = (Button) view
				.findViewById(R.id.monitor_add_research);
		mMonitorSearchResearch.setOnClickListener(this);

		mMonitorSearchListView = (ListView) view
				.findViewById(R.id.monitor_search_listview);
		mMonitorSearchListView.setOnItemClickListener(this);

		mData = new ArrayList<Map<String, Object>>();

		mSimpleAdapter = new SimpleAdapter(getActivity(), mData,
				R.layout.monitoradd_search_item,

				new String[] { "title", "text" }, new int[] { R.id.textView1,
						R.id.textView2 });
		mMonitorSearchListView.setAdapter(mSimpleAdapter);

		st_LanSearchInfo[] lanSearchInfo = MonitorClient.SearchLAN();
		if (lanSearchInfo != null) {
			for (int i = 0; i < lanSearchInfo.length; i++) {
				st_LanSearchInfo searchInfo = lanSearchInfo[i];

				Map<String, Object> item = new HashMap<String, Object>();
				item.put("title", (new String(searchInfo.UID)).trim());
				item.put("text", (new String(searchInfo.IP)).trim());
				mData.add(item);
			}
		}

		mSimpleAdapter.notifyDataSetChanged();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		mMonitorSearchDialog = builder.create();
		mMonitorSearchDialog.show();
		mMonitorSearchDialog.setContentView(view);
	}

	private void scanQRCode() {
		Intent intent = new Intent();
		intent.setClass(getActivity(), MipcaActivityCapture.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		mUidTextView.setText("" + mData.get(position).get("title"));
		mMonitorSearchDialog.dismiss();
	}

	private void todoAddMonitor() {

		if (!checkInputs()) {
			// 输入检查错误
			return;
		}

		if (!checkMonitorExist()) {
			// 监护器是否已经存在
			return;
		}

		completeAddMonitorDone();
	}

	private void completeAddMonitorDone() {

		// 保存设备信息
		MonitorDBManager.saveMonitorInfoTable(getActivity(), mNewMonitorInfo);

		mHandler.sendEmptyMessage(MonitorFragmentActivity.MESSAGE_CHANGE_FRAGMENT_LIST);
	}

	private boolean checkInputs() {
		device_uid = mUidTextView.getText().toString().trim()
				.toUpperCase(Locale.US);
		device_nickname = mMonitorNameText.getText().toString().trim();
		device_pwd = mMonitorPwdText.getText().toString().trim();

		// 检查项目是否有输入
		if ("".equals(device_uid)) {
			Toast.makeText(getActivity(), "[UID]未输入", Toast.LENGTH_LONG).show();
			return false;
		}

		if ("".equals(device_nickname)) {
			Toast.makeText(getActivity(), "[名称]未输入", Toast.LENGTH_LONG).show();
			return false;
		}

		if ("".equals(device_pwd)) {
			Toast.makeText(getActivity(), "[密码]未输入", Toast.LENGTH_LONG).show();
			return false;
		}

		if (device_uid.length() != 20) {
			Toast.makeText(getActivity(), "[UID]必须20位字符", Toast.LENGTH_LONG)
					.show();
			return false;
		}

		return true;
	}

	private boolean checkMonitorExist() {
		// 保存摄像机信息
		mNewMonitorInfo = new MonitorInfo(mLoginUserId, device_uid,
				device_nickname, "", device_pwd, "", device_pwd);
		if (MonitorDBManager.isMonitorExist(getActivity(), mNewMonitorInfo)) {
			// 设备以及存在
			Toast.makeText(getActivity(), "该监护器已经存在，请输入另一个[UID]",
					Toast.LENGTH_LONG).show();
			return false;
		}

		return true;
	}

	public void reloadMonitorAdd(String result) {

		mUidTextView.setText(result);
	}

	private void checkAndConnectNewCamera() {

		if (!checkInputs()) {
			// 输入检查错误
			return;
		}

		if (!checkMonitorExist()) {
			// 监护器是否已经存在
			return;
		}

		mProgressDialog.show();

		disconnectNewCamera();

		mMyNewMonitor = new MyMonitor(mNewMonitorInfo.UID,
				mNewMonitorInfo.ViewAccount, mNewMonitorInfo.ViewPassword);

		mMyNewMonitor.registerIOTCListener(this);

		mMyNewMonitor.connect(mMyNewMonitor.getUID());
		mMyNewMonitor.start(mAVChannel, mMyNewMonitor.getName(),
				mMyNewMonitor.getPassword());
	}

	private void disconnectNewCamera() {
		if (mMyNewMonitor == null) {
			return;
		}

		mMyNewMonitor.unregisterIOTCListener(this);

		mMyNewMonitor.stop(mAVChannel);
		mMyNewMonitor.disconnect();

		mShareMonitor.setCurrentMonitor(null);
		mMyNewMonitor = null;
	}

	private void showAddSettingForModify() {
		mProgressDialog.dismiss();
		mIsProcessCameraSetting = true;

		mShareMonitor.setCurrentMonitor(mMyNewMonitor);

		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putSerializable("monitor_settings", mNewMonitorInfo);
		intent.putExtras(bundle);

		intent.setClass(getActivity(), MonitorAddSettingActivity.class);
		startActivityForResult(intent, CONNECT_NEW_CAMERA_SETTING);

		// 设定启动动画
		getActivity().overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);
	}

	@Override
	public void receiveChannelInfo(MonitorClient paramCamera, int paramInt1,
			int paramInt2) {
		if ((MyMonitor) paramCamera == mMyNewMonitor) {
			if (paramInt2 == 5) {
				// 账号或密码错误
				sendMessageForFailed(paramInt2, "账号或密码输入错误。");
			} else if (paramInt2 == 6) {
				// 连接超时
				sendMessageForFailed(paramInt2, "网络连接超时，请稍后再试。");
			} else if (paramInt2 == 2) {
				// 连接成功
				mNewCameraHandler.sendEmptyMessage(MESSAGE_NEW_CAMERA_SUCCESS);
			}
		}
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
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveSessionInfo(MonitorClient paramCamera, int paramInt) {
		if ((MyMonitor) paramCamera == mMyNewMonitor) {
			if (paramInt == 4) {
				sendMessageForFailed(paramInt, "设备[UID]无法识别。");
			}
		}
	}

	private void sendMessageForFailed(int paramInt, String string) {
		Message msg = Message.obtain();
		msg.what = MESSAGE_NEW_CAMERA_FAILED;
		msg.arg1 = paramInt;
		msg.obj = string;

		mNewCameraHandler.sendMessage(msg);
	}

	private Handler mNewCameraHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_NEW_CAMERA_SUCCESS:
				// 显示
				showAddSettingForModify();
				break;
			case MESSAGE_NEW_CAMERA_FAILED:
				// 显示错误信息
				showMessageForFailed((String) msg.obj);
				break;

			}

			super.handleMessage(msg);
		}
	};

	protected void showMessageForFailed(String msg) {
		mProgressDialog.dismiss();

		Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
	}
}
