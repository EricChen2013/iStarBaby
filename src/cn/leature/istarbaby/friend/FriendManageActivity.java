package cn.leature.istarbaby.friend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.FriendDetailInfo;
import cn.leature.istarbaby.domain.FriendGroupListInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.utils.ConstantDef;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class FriendManageActivity extends Activity implements OnClickListener {

	// 文章发表成功（Message用）o
	protected static final int CHILD_EDIT_DONE = 4;
	// 文章发表出错（Message用）
	protected static final int CHILD_EDIT_ERROR = -2;
	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	private Bundle mBundle;
	private ArrayList<FriendGroupListInfo> mGroupData;
	private ArrayList<String> mGroupDataName;
	private ArrayList<String> mGroupDataId;

	private TextView mTxtFriendDelete;
	private TextView mTxtFriendMemoName;
	private TextView mGrouptext;

	private LzProgressDialog mProgressDialog;
	private String mOldGroupName;
	private FriendDetailInfo mFriendDetailInfo;
	private Dialog mFriendDeleteDialog;

	// ContactAdd.aspx UserId ContactId GroupId GroupName
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_manage);

		mBundle = getIntent().getExtras();
		mOldGroupName = mBundle.getString("oldGroup");

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		initUI();
		// 头部导航
		setCustomTitleBar();

		loadContactGroupList();
	}

	private void initUI() {
		mTxtFriendDelete = (TextView) findViewById(R.id.delete_friend);
		mTxtFriendMemoName = (TextView) findViewById(R.id.manage_memoname);
		mGrouptext = (TextView) findViewById(R.id.manage_grouptext);

		mTxtFriendDelete.setOnClickListener(this);
		mTxtFriendMemoName.setOnClickListener(this);
		mGrouptext.setOnClickListener(this);

		String result = mBundle.getString("FriendDetail");
		String[] resultinfo = result.split("\\|");
		try {

			JSONObject jsonObject = new JSONObject(resultinfo[1]);
			mFriendDetailInfo = new FriendDetailInfo(jsonObject);

			String memo = mFriendDetailInfo.getContactMemo();
			if (memo.length() > 0) {
				mTxtFriendMemoName.setText(memo);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadContactGroupList() {
		mProgressDialog.show();

		mGroupData = new ArrayList<FriendGroupListInfo>();
		mGroupDataName = new ArrayList<String>();
		mGroupDataId = new ArrayList<String>();
		// 日志List取得的参数设定
		Map<String, String> param1 = new HashMap<String, String>();
		param1.put("UserId", LoginInfo.getLoginUserId(this));
		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				JSONArray jsonArray;
				try {
					jsonArray = new JSONArray(result);
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);

						FriendGroupListInfo Groupinfo = new FriendGroupListInfo(jsonObject);

						mGroupData.add(Groupinfo);
						mGroupDataName.add(Groupinfo.getGroup_name());
						mGroupDataId.add(Groupinfo.getGroup_id());
						if (mOldGroupName.equals(Groupinfo.getGroup_name())) {
							mGrouptext.setText(mOldGroupName);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlContactGroupEdit, param1);
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("更多");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		case R.id.delete_friend:
			deleteDialog();
			break;
		case R.id.delete_queding:
			// 确认删除
			deleteFriend();
			break;
		case R.id.delete_quxiao:
			// 删除取消
			mFriendDeleteDialog.dismiss();
			break;
		case R.id.manage_memoname:
			modifyMemoName();
			break;
		case R.id.manage_grouptext:
			Intent intent = new Intent();
			mBundle.putStringArrayList("GroupName", mGroupDataName);
			mBundle.putStringArrayList("FriendGroupId", mGroupDataId);
			mBundle.putString("oldGroup", mOldGroupName);
			intent.putExtras(mBundle);
			intent.setClass(this, FriendGroupMoveActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_FRIENDMANAGE);

			// 设定启动动画
			this.overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
			break;
		}
	}

	private void modifyMemoName() {
		String memo = mFriendDetailInfo.getContactMemo();
		if (memo.length() == 0) {
			memo = mFriendDetailInfo.getName();
		}

		Intent memoIntent = new Intent();
		mBundle.putString("ContactMemo", memo);
		memoIntent.putExtras(mBundle);

		memoIntent.setClass(this, FriendMemoNameActivity.class);
		startActivityForResult(memoIntent,
				ConstantDef.REQUEST_CODE_FRIENDMEMONAME);

		// 设定启动动画
		this.overridePendingTransition(R.anim.activity_in_from_right,
				R.anim.activity_nothing_in_out);
	}

	private void deleteDialog() {
		// 显示器删除回调
		View layout = this.getLayoutInflater().inflate(R.layout.delete_dialog,
				null);

		TextView txtTitle = (TextView) layout.findViewById(R.id.alert_text);
		txtTitle.setText("确定要删除吗？删除后将接收不到此人的消息。");

		Button btnDelete = (Button) layout.findViewById(R.id.delete_queding);
		btnDelete.setText("删除好友");
		btnDelete.setOnClickListener(this);

		Button btnCancel = (Button) layout.findViewById(R.id.delete_quxiao);
		btnCancel.setOnClickListener(this);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		mFriendDeleteDialog = builder.create();
		mFriendDeleteDialog.show();
		mFriendDeleteDialog.setContentView(layout);
	}

	private void deleteFriend() {
		mFriendDeleteDialog.dismiss();

		mProgressDialog.show();

		// 删除好友
		// ContactDelete.aspx
		// UserId
		// ContactId
		Map<String, String> param = new HashMap<String, String>();

		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("ContactId", mBundle.getString("ContactId"));
		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				if ((result == null) || ("0".equals(result))) {
					Toast.makeText(FriendManageActivity.this, "删除好友失败",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(FriendManageActivity.this, "删除成功",
							Toast.LENGTH_LONG).show();

					deleteFriendDoneBack();
				}
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlContactDelete, param);
	}

	private void deleteFriendDoneBack() {
		this.setResult(Activity.RESULT_OK);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	private void backPrePage() {
		this.setResult(Activity.RESULT_CANCELED);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getExtras();
			if (requestCode == ConstantDef.RESULT_CODE_FRIENDMEMONAME) {
				mTxtFriendMemoName
						.setText(bundle.getString("friend_memo_name"));
			} else if (requestCode == ConstantDef.RESULT_CODE_FRIENDMANAGE) {
				mOldGroupName = bundle.getString("oldGroup");
				// 重新读取分组信息
				loadContactGroupList();
			}
		}
	}
}
