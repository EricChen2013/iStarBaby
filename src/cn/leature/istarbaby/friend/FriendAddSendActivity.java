package cn.leature.istarbaby.friend;

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
import cn.leature.istarbaby.domain.FriendGroupListInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.domain.SettingUserInfo;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.utils.ConstantDef;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendAddSendActivity extends Activity implements OnClickListener {

	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	private ImageView mFriendIcon;
	private TextView mFriendName, mTxtGroupName;
	private EditText mEditMemoName;
	private String mSelectedGroupId = "";
	private SettingUserInfo mFriendInfo;
	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private Bundle mBundle;
	private LzProgressDialog mProgressDialog;
	private ArrayList<FriendGroupListInfo> mGroupData;
	private ArrayList<String> mGroupDataName;
	private ArrayList<String> mGroupDataId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_add_send);

		mBundle = getIntent().getExtras();

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.noimage)
				.showImageForEmptyUri(R.drawable.noimage)
				.showImageOnFail(R.drawable.noimage).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).build();

		// 头部导航
		setCustomTitleBar();

		initUI();

		// 显示要加好友信息
		showDetail();
	}

	private void setCustomTitleBar() {
		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("添加好友");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_ccl);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener((this));
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_cmp);
	}

	private void initUI() {
		mFriendIcon = (ImageView) findViewById(R.id.friend_detail_icon);
		mFriendName = (TextView) findViewById(R.id.friend_detail_name);
		mEditMemoName = (EditText) findViewById(R.id.edit_memo_name);
		mTxtGroupName = (TextView) findViewById(R.id.manage_grouptext);
		mTxtGroupName.setOnClickListener(this);

		String result = mBundle.getString("Detail");
		String[] resultinfo = result.split("\\|");

		try {

			JSONObject jsonObject = new JSONObject(resultinfo[1]);
			mFriendInfo = new SettingUserInfo(jsonObject);

			mFriendName.setText(mFriendInfo.getName());

			// 图片加载
			imageLoader.displayImage(
					HttpClientUtil.SERVER_PATH + mFriendInfo.getIcon(),
					mFriendIcon, options);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			backPrePage();
		}

		return super.onKeyDown(keyCode, event);
	}

	private void backPrePage() {
		this.setResult(Activity.RESULT_CANCELED);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	private void addFriendDone() {
		Toast.makeText(FriendAddSendActivity.this, "好友添加成功。", Toast.LENGTH_LONG)
				.show();

		this.setResult(Activity.RESULT_OK);
		this.finish();

		// 设定退出动画
		this.overridePendingTransition(R.anim.activity_nothing_in_out,
				R.anim.activity_out_to_right);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		case R.id.titlebar_rightbtn1:
			addFriendSend();
			break;
		case R.id.manage_grouptext:
			Intent intent = new Intent();
			mBundle.putStringArrayList("GroupName", mGroupDataName);
			mBundle.putStringArrayList("FriendGroupId", mGroupDataId);
			mBundle.putString("SelectedGroupId", mSelectedGroupId);
			intent.putExtras(mBundle);
			intent.setClass(this, FriendGroupSelectActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_FRIEND_SELGROUP);

			// 设定启动动画
			this.overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
			break;
		}
	}

	private void showDetail() {
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

						if (i == 0) {
							// 显示默认分组
							mSelectedGroupId = Groupinfo.getGroup_id();
							mTxtGroupName.setText(Groupinfo.getGroup_name());
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlContactGroupEdit, param1);
	}

	private void addFriendSend() {
		mProgressDialog.show();

		// 日志List取得的参数设定
		Map<String, String> param1 = new HashMap<String, String>();
		param1.put("UserId", LoginInfo.getLoginUserId(this));
		param1.put("ContactId", mFriendInfo.getUser_id());
		param1.put("GroupId", mSelectedGroupId);

		String memo = mEditMemoName.getText().toString().trim();
		if (memo.length() > 0) {
			param1.put("ContactMemo", memo);
		}
		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				addFriendDone();
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlContactAdd, param1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getExtras();
			if (requestCode == ConstantDef.RESULT_CODE_FRIEND_SELGROUP) {
				mSelectedGroupId = bundle.getString("SelectedGroupId");
				mTxtGroupName.setText(bundle.getString("SelectedGroupName"));
				if (bundle.getInt("SelectedGroupIndex") > mGroupDataId.size() - 1) {
					mGroupDataId.add(bundle.getString("SelectedGroupId"));
					mGroupDataName.add(bundle.getString("SelectedGroupName"));
				}
			}
		}
	}
}
