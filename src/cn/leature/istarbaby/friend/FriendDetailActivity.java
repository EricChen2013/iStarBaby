package cn.leature.istarbaby.friend;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.common.LzProgressDialog;
import cn.leature.istarbaby.domain.FriendDetailInfo;
import cn.leature.istarbaby.domain.LoginInfo;
import cn.leature.istarbaby.network.HttpClientUtil;
import cn.leature.istarbaby.network.HttpGetUtil;
import cn.leature.istarbaby.network.HttpGetUtil.RequestGetDoneCallback;
import cn.leature.istarbaby.utils.ConstantDef;
import cn.leature.istarbaby.utils.DateUtilsDef;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FriendDetailActivity extends Activity implements OnClickListener {

	private TextView titleBarTitle;
	private ImageButton titleBarBack;
	private ImageButton titleBarEdit;

	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private Bundle mBundle;
	private LzProgressDialog mProgressDialog;

	private ImageView mUserIcon;
	private TextView mName, mFriendInfoMore, mFriendId, mNickName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_detail);

		mProgressDialog = new LzProgressDialog(this);
		mProgressDialog.setCancelable(true);
		mBundle = getIntent().getExtras();

		initUI();
		// 头部导航
		setCustomTitleBar();

		loadDetail();
	}

	private void setCustomTitleBar() {

		titleBarTitle = (TextView) this.findViewById(R.id.titlebar_title);
		titleBarTitle.setText("个人资料");

		titleBarBack = (ImageButton) this.findViewById(R.id.titlebar_leftbtn);
		titleBarBack.setOnClickListener((this));
		titleBarBack.setBackgroundResource(R.drawable.selector_hd_bk);

		titleBarEdit = (ImageButton) this.findViewById(R.id.titlebar_rightbtn1);
		titleBarEdit.setOnClickListener(this);
		titleBarEdit.setBackgroundResource(R.drawable.selector_hd_more);
	}

	public void loadDetail() {
		mProgressDialog.show();

		Map<String, String> param = new HashMap<String, String>();
		param.put("UserId", LoginInfo.getLoginUserId(this));
		param.put("ContactId", mBundle.getString("ContactId"));

		HttpGetUtil httpGetUtil = new HttpGetUtil(new RequestGetDoneCallback() {

			@Override
			public void requestWithGetDone(String result) {
				mProgressDialog.dismiss();

				Log.e("result", "result==" + result);
				if ("9".equals(result)) {
					Toast.makeText(FriendDetailActivity.this, "网络异常，请检查你的网络！",
							Toast.LENGTH_LONG).show();
					return;
				}

				mBundle.putString("FriendDetail", result);
				String[] resultInfo = result.split("\\|");
				try {
					LinearLayout layout = (LinearLayout) findViewById(R.id.layout_user_nickname);
					View view = (View) findViewById(R.id.view_user_nickname);

					JSONObject jsonObject = new JSONObject(resultInfo[1]);
					FriendDetailInfo info = new FriendDetailInfo(jsonObject);

					String memo = info.getContactMemo();
					if (memo.length() == 0) {
						memo = info.getName();

						layout.setVisibility(View.GONE);
						view.setVisibility(View.GONE);
					} else {
						layout.setVisibility(View.VISIBLE);
						view.setVisibility(View.VISIBLE);
					}

					mName.setText(memo);
					mFriendId.setText(info.getUserId());
					mNickName.setText(info.getName());

					String friendInfoMore = "";
					if ("1".equals(info.getGender())) {
						friendInfoMore += "男性";
					} else if ("2".equals(info.getGender())) {
						friendInfoMore += "女性";
					}

					int age = DateUtilsDef.calculateUserAgeWithBirthday(info
							.getBirthday());
					if (age > 0) {
						friendInfoMore += "  " + age + "岁";
					}

					mFriendInfoMore.setText(friendInfoMore);

					// 图片加载
					imageLoader.displayImage(
							HttpClientUtil.SERVER_PATH + info.getIcon(),
							mUserIcon, options);

					mBundle.putString("oldGroup", info.getGroupName());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		httpGetUtil.execute(ConstantDef.cUrlContactSearch, param);
	}

	private void initUI() {

		TextView txtSendMsg = (TextView) findViewById(R.id.txt_send_msg);
		mUserIcon = (ImageView) findViewById(R.id.friend_detail_icon);
		mUserIcon.setOnClickListener(this);

		mName = (TextView) findViewById(R.id.friend_detail_name);
		mFriendInfoMore = (TextView) findViewById(R.id.friend_userinfo_more);
		mFriendId = (TextView) findViewById(R.id.friend_userid);
		mNickName = (TextView) findViewById(R.id.friend_user_nickname);

		TextView txtShareCamera = (TextView) findViewById(R.id.txt_share_camera);
		txtShareCamera.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.txt_share_camera:
			intent.putExtras(mBundle);
			intent.setClass(this, FriendShareMonitorActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_FRIEND_SHARE);
			break;
		case R.id.titlebar_leftbtn:
			backPrePage();
			break;
		case R.id.titlebar_rightbtn1:
			intent.putExtras(mBundle);
			intent.setClass(this, FriendManageActivity.class);
			startActivityForResult(intent,
					ConstantDef.REQUEST_CODE_FRIENDMANAGE);

			// 设定启动动画
			this.overridePendingTransition(R.anim.activity_in_from_right,
					R.anim.activity_nothing_in_out);
			break;
		}
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
		if (resultCode == Activity.RESULT_CANCELED) {

			loadDetail();
		} else if (resultCode == Activity.RESULT_OK) {
			if (requestCode == ConstantDef.REQUEST_CODE_FRIENDMANAGE) {
				// 删除好友，返回前一页面
				backPrePage();
			}
		}
	}
}
