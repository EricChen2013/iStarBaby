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
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.LzViewHolder;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendGroupSelectActivity extends Activity implements
		OnItemClickListener, OnClickListener {

	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	private LzProgressDialog mProgressDialog;
	private Intent mIntent;
	private Bundle mBundle;

	private TextView mTxtGroupNewName;
	private AlertDialog mFriendGroupNameDialog;
	private EditText mFriendDialogName;
	private String mNameOfNewGroup = "";

	private ArrayList<String> groupNameList;
	private ArrayList<String> groupIdList;
	private SelGroupAdapter mAdapter;
	private int mSelectedGroup = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_grpmv);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

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
		String oldGroup = mBundle.getString("SelectedGroupId");

		for (int i = 0; i < groupIdList.size(); i++) {
			if (oldGroup.equals(groupIdList.get(i))) {
				mSelectedGroup = i;
			}
		}

		mAdapter = new SelGroupAdapter();
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(this);
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("选择分组");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);
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

	private void selectGroupDone() {
		mBundle.putString("SelectedGroupId", groupIdList.get(mSelectedGroup));
		mBundle.putString("SelectedGroupName",
				groupNameList.get(mSelectedGroup));
		mBundle.putInt("SelectedGroupIndex", mSelectedGroup);
		mIntent.putExtras(mBundle);
		setResult(Activity.RESULT_OK, mIntent);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {

		mSelectedGroup = position;

		selectGroupDone();
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

	private class SelGroupAdapter extends BaseAdapter {

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
					Toast.makeText(FriendGroupSelectActivity.this, "新分组创建失败",
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

							selectGroupDone();
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

}
