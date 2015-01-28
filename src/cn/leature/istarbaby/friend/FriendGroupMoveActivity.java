package cn.leature.istarbaby.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.FriendGroupListInfo;
import cn.leature.istarbaby.domain.LoginInfo;
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
import android.app.AlertDialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FriendGroupMoveActivity extends Activity implements
		OnItemClickListener, OnClickListener, OnPostProcessListener {

	protected static final int GROUP_CHANGE_DONE = 9;
	protected static final int GROUP_CHANGE_ERROR = -1;

	private ArrayList<String> groupNameList;
	private ArrayList<String> groupIdList;
	private MoveManageAdapter adapter;
	private int mSelectedGroup = 0;
	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	private Intent mIntent;
	private Bundle mBundle;
	private LzProgressDialog mProgressDialog;
	private HttpPostUtil mHttpUtil;
	private TextView mTxtGroupNewName;
	private AlertDialog mFriendGroupNameDialog;
	private EditText mFriendDialogName;
	private String mNameOfNewGroup = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_grpmv);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);
		mHttpUtil = HttpPostUtil.getInstance();

		mIntent = getIntent();
		mBundle = mIntent.getExtras();

		initUI();
		// 头部导航
		setCustomTitleBar();
	}

	private void initUI() {
		mTxtGroupNewName = (TextView) findViewById(R.id.group_newname);
		mTxtGroupNewName.setOnClickListener(this);

		ListView listview = (ListView) findViewById(R.id.movemanage_listview);
		groupNameList = mBundle.getStringArrayList("GroupName");
		groupIdList = mBundle.getStringArrayList("FriendGroupId");
		String oldGroup = mBundle.getString("oldGroup");
		adapter = new MoveManageAdapter();
		for (int i = 0; i < groupNameList.size(); i++) {
			if (oldGroup.equals(groupNameList.get(i))) {
				mSelectedGroup = i;
			}

		}
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(this);
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("移动分组");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);
	}

	private class MoveManageAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return groupNameList.size();
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
			String groupName = groupNameList.get(position);
			convertView = getLayoutInflater().inflate(
					R.layout.wifi_listview_item, null);

			LinearLayout layoutitem = LzViewHolder.get(convertView,
					R.id.linearlayout_item);
			layoutitem.setTag(position + "");
			TextView mTextName = LzViewHolder.get(convertView,
					R.id.wifi_textname);
			final TextView mTextSignal = LzViewHolder.get(convertView,
					R.id.wifi_signal);

			final ImageView mWifi_resize = LzViewHolder.get(convertView,
					R.id.wifi_resize);
			mTextName.setText(groupName);
			mTextSignal.setVisibility(View.INVISIBLE);

			if (position == mSelectedGroup) {
				mWifi_resize.setVisibility(View.VISIBLE);

			} else {
				mWifi_resize.setVisibility(View.INVISIBLE);
			}

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (position == mSelectedGroup) {
			return;
		}

		mSelectedGroup = position;
		adapter.notifyDataSetChanged();

		changeGroup();
	}

	private void backPrePage() {
		setResult(Activity.RESULT_CANCELED, mIntent);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			backPrePage();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		case R.id.group_newname:
			groupNewName();
			break;
		case R.id.friend_dialog_closebtn:
			mFriendGroupNameDialog.dismiss();
			break;
		case R.id.friend_dialog_okbtn:
			doAddNewGroup();
			break;
		}
	}

	private void groupNewName() {

		LayoutInflater inflater = getLayoutInflater();
		View inflate = inflater.inflate(R.layout.friend_group_name_dialog,
				(ViewGroup) findViewById(R.id.friend_dialog));

		Button btn_ok = (Button) inflate.findViewById(R.id.friend_dialog_okbtn);

		Button btn_close = (Button) inflate
				.findViewById(R.id.friend_dialog_closebtn);
		mFriendDialogName = (EditText) inflate
				.findViewById(R.id.friend_dialog_name);

		btn_ok.setOnClickListener(this);
		btn_close.setOnClickListener(this);

		// dialog
		mFriendGroupNameDialog = new AlertDialog.Builder(this).setView(inflate)
				.show();
	}

	public void doAddNewGroup() {
		mNameOfNewGroup = mFriendDialogName.getText().toString().trim();
		// 检查项目是否有输入
		if ("".equals(mNameOfNewGroup)) {
			Toast.makeText(this, "[分组名]未输入", Toast.LENGTH_LONG).show();
			return;
		}

		if (isGroupNameExist()) {
			Toast.makeText(this, "[分组名]新分组名已存在", Toast.LENGTH_LONG).show();
			return;
		}

		mFriendGroupNameDialog.dismiss();

		// 取得组列表
		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("GroupName", mNameOfNewGroup);
		param.put("EditType", "1");

		postNewGroup(param, ConstantDef.cUrlContactGroupEdit);
	}

	private boolean isGroupNameExist() {
		for (int i = 0; i < groupNameList.size(); i++) {
			if (mNameOfNewGroup.equals(groupNameList.get(i))) {
				return true;
			}
		}

		return false;
	}

	private void postNewGroup(Map<String, String> param, String url) {
		mProgressDialog.show();

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				if ((result == null) || ("0".equals(result))) {
					Toast.makeText(FriendGroupMoveActivity.this, "新分组创建失败",
							Toast.LENGTH_LONG).show();
					return;
				}

				JSONArray jsonArray;
				try {
					jsonArray = new JSONArray(result);
					for (int i = jsonArray.length() - 1; i >= 0; i--) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);

						FriendGroupListInfo groupListInfo = new FriendGroupListInfo(
								jsonObject);
						if (mNameOfNewGroup.equals(groupListInfo
								.getGroup_name())) {
							groupNameList.add(groupListInfo.getGroup_name());
							groupIdList.add(groupListInfo.getGroup_id());
							mSelectedGroup = groupIdList.size() - 1;

							changeGroup();
							break;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});
		httpGetUtil.execute(url, param);
	}

	private void changeGroup() {
		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();

		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("ContactId", mBundle.getString("ContactId"));
		param.put("GroupId", groupIdList.get(mSelectedGroup));

		mHttpUtil.setOnPostProcessListener(this);
		mHttpUtil.sendPostMessage(ConstantDef.cUrlContactEdit, param);
	}

	private void saveDetailDone(String Result) {
		mProgressDialog.dismiss();

		if ((Result == null) || "0".equals(Result)) {
			Toast.makeText(this, "保存失败", Toast.LENGTH_LONG).show();
			return;
		}

		mBundle.putString("oldGroup", groupNameList.get(mSelectedGroup));
		mIntent.putExtras(mBundle);
		setResult(Activity.RESULT_OK, mIntent);
		this.finish();
	}

	@Override
	public void onPostDone(int responseCode, String responseMessage) {
		Message msg = Message.obtain();
		msg.arg1 = responseCode;
		msg.obj = responseMessage;

		if (responseCode == HttpPostUtil.POST_SUCCESS_CODE) {
			msg.what = GROUP_CHANGE_DONE;
		} else {
			msg.what = GROUP_CHANGE_ERROR;
		}

		handler.sendMessage(msg);
	}

	private void toErrorMessage(String msgString) {
		// 取得出错，显示提示信息
		mProgressDialog.dismiss();

		Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case GROUP_CHANGE_DONE:// 保存
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
}
